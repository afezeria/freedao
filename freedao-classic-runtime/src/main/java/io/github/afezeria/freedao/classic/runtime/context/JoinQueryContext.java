package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.ResultTypeHandler;
import io.github.afezeria.freedao.StatementType;
import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.ReferenceValue;
import io.github.afezeria.freedao.annotation.Table;
import io.github.afezeria.freedao.classic.runtime.ResultHandler;
import io.github.afezeria.freedao.classic.runtime.SqlExecutor;
import io.github.afezeria.freedao.classic.runtime.SqlSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author afezeria
 */
public class JoinQueryContext extends DaoContext {

    private static final Logger logger = LoggerFactory.getLogger(JoinQueryContext.class);
    public static ThreadLocal<List<String>> localJoinIds = new ThreadLocal<>();

    @Override
//    @SuppressWarnings("unchecked")
    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        List<String> joinIds = localJoinIds.get();
        if (joinIds == null) {
            return getDelegate().execute(signature, methodArgs, sql, sqlArgs, executor, resultHandler);
        }
        if (signature.getType() != StatementType.SELECT) {
            throw new IllegalArgumentException(signature.getType() + " statement does not support join query");
        }
        if (signature.getElementType().getAnnotation(Table.class) == null) {
            throw new IllegalArgumentException("the return type is not an entity type or collection of entity, not support join query");
        }

        SqlAndHandler<E> pair = getPair(signature.getElementType(), joinIds, sql, resultHandler);

        return getDelegate().withConnection(connection -> {
            T result = getDelegate().execute(signature, methodArgs, pair.sql, sqlArgs, executor, pair.handler);
            return result;
        });
    }

    private static final Map<Class<?>, Map<String, JoinData>> cache = new ConcurrentHashMap<>();


    public <E> SqlAndHandler<E> getPair(Class<?> entityClass, List<String> joinIds, String originalSql, ResultHandler<E> originalHandler) {
        Map<String, JoinData> joinDataMap = cache.computeIfAbsent(
                entityClass,
                this::createJoinDataMap);
        SqlAndHandler<E> pair = new SqlAndHandler<>();
        if (joinDataMap == null) {
            pair.sql = originalSql;
            pair.handler = originalHandler;
            return pair;
        }
        StringBuilder selectItems = new StringBuilder("_main.*");
        StringBuilder joins = new StringBuilder();
        Collection<String> ids = joinIds.isEmpty() ? joinDataMap.keySet() : joinIds;
        for (String id : ids) {
            JoinData joinData = joinDataMap.get(id);
            if (joinData == null) {
                throw new IllegalArgumentException("class " + entityClass.getCanonicalName() + "has no joinId:" + id);
            }
            for (MappingData mapping : joinData.mappingDataList) {
                selectItems.append(",")
                        .append(joinData.joinId)
                        .append(".")
                        .append(mapping.columnName)
                        .append(" as ")
                        .append(mapping.resultSetColumnName);
            }
            joins.append(" left join ")
                    .append(joinData.table)
                    .append(" as ")
                    .append(joinData.joinId)
                    .append(joinData.onClause);
        }
        pair.sql = "select " + selectItems + " from (" + originalSql + ") as _main " + joins;
        pair.handler = (rs, item) -> {
            E result = originalHandler.handle(rs, item);
            for (String id : ids) {
                JoinData joinData = joinDataMap.get(id);
                for (MappingData mapping : joinData.mappingDataList) {
                    Object object;
                    if (mapping.resultTypeHandlerMethod == null) {
                        object = rs.getObject(mapping.resultSetColumnName, mapping.field.getType());
                    } else {
                        object = rs.getObject(mapping.resultSetColumnName);
                        object = mapping.resultTypeHandlerMethod.invoke(null, object, mapping.field.getType());
                    }
                    mapping.field.set(result, object);
                }
            }
            return result;
        };
        return pair;
    }

    private Map<String, JoinData> createJoinDataMap(Class<?> entityClass) {

        Join[] joins = entityClass.getAnnotationsByType(Join.class);
        if (joins.length == 0) {
            logger.debug("{} is missing Join annotation, should not use the join query", entityClass.getCanonicalName());
            return null;
        }
        Map<String, JoinData> joinMap = new HashMap<>();
        for (Join join : joins) {
            JoinData joinData = new JoinData();
            joinData.joinId = join.id();
            Class<?> joinEntityClass = join.entityClass();
            String[] referencesKey;
            String schema;
            String table;
            if (joinEntityClass != null && joinEntityClass != Object.class) {
                Table joinTable = joinEntityClass.getAnnotation(Table.class);
                referencesKey = joinTable.primaryKeys();
                schema = joinTable.schema();
                if (joinTable.name().isEmpty()) {
                    if (joinTable.value().isEmpty()) {
                        table = toSnakeCase(joinEntityClass.getSimpleName());
                    } else {
                        table = joinTable.value();
                    }
                } else {
                    table = joinTable.name();
                }

            } else {
                referencesKey = join.referenceKey();
                schema = join.schema();
                table = join.table();
            }
            joinData.table = !schema.isEmpty() ? schema + "." + table : table;
            StringBuilder onClause = new StringBuilder(" on ");
            for (int i = 0; i < join.foreignKey().length; i++) {
                if (i > 0) {
                    onClause.append(" and ");
                }
                onClause.append(join.id())
                        .append(".")
                        .append(referencesKey[i])
                        .append(" = _main.")
                        .append(join.foreignKey()[i]);
            }
            joinData.onClause = onClause.toString();
            joinMap.put(join.id(), joinData);
        }

        //类上有Join注解时必定存在字段有ReferenceValue注解，编译时会检查
        for (Field f : entityClass.getDeclaredFields()) {
            ReferenceValue referenceValueAnn = f.getAnnotation(ReferenceValue.class);
            Column columnAnn = f.getAnnotation(Column.class);
            if (referenceValueAnn != null) {
                JoinData joinData = joinMap.get(referenceValueAnn.joinId());
                f.setAccessible(true);
                MappingData data = new MappingData();
                data.field = f;
                if (!Objects.equals(columnAnn.resultTypeHandle(), ResultTypeHandler.class)) {
                    Method method;
                    try {
                        method = columnAnn.resultTypeHandle().getDeclaredMethod("handleResult", Object.class, Class.class);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    method.setAccessible(true);
                    data.resultTypeHandlerMethod = method;
                }
                data.columnName = referenceValueAnn.columnName();
                data.resultSetColumnName = joinData.joinId + "_" + referenceValueAnn.columnName();
                joinData.mappingDataList.add(data);
            }
        }

        return joinMap;
    }

    private static final Pattern compile = Pattern.compile("[a-z]+|[0-9]+|[A-Z][a-z]+|[A-Z]++(?![a-z])|[A-Z]");

    public static String toSnakeCase(String s) {
        Matcher matcher = compile.matcher(s);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group().substring(0, 1).toLowerCase())
                    .append(matcher.group().substring(1))
                    .append("_");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    private static class JoinData {
        String joinId;
        String table;
        String onClause;
        List<MappingData> mappingDataList = new ArrayList<>();
    }

    private static class MappingData {

        Field field;
        Method resultTypeHandlerMethod;
        String columnName;
        String resultSetColumnName;
    }

    static class SqlAndHandler<E> {
        String sql;
        ResultHandler<E> handler;
    }

}
