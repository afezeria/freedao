package com.github.afezeria.freedao.classic.runtime;

import com.github.afezeria.freedao.StatementType;
import lombok.Getter;

import java.util.function.Function;

/**
 * sql签名
 */
@Getter
public class SqlSignature<T, E> {
    /**
     * sql类型
     */
    private final StatementType type;
    private final boolean isCustomSql;
    private final Class<?> daoClass;
    private final String method;
    private final Class<?> returnType;
    private final Class<?> containerType;
    private final Class<?> elementType;
    private final Class<?>[] parameterTypeList;
    private final Function<Object[], Object[]> sqlBuilderClosure;
    private final SqlExecutor<T, E> sqlExecutor;
    private final ResultHandler<E> resultHandler;


    public SqlSignature(StatementType type,
                        boolean isCustomSql,
                        Class<?> daoClass,
                        String method,
                        Class<?> returnType,
                        Class<?> containerType,
                        Class<?> elementType,
                        Class<?>[] parameterTypeList,
                        Function<Object[], Object[]> sqlBuilderClosure,
                        SqlExecutor<T, E> sqlExecutor,
                        ResultHandler<E> resultHandler) {
        this.type = type;
        this.isCustomSql = isCustomSql;
        this.daoClass = daoClass;
        this.method = method;
        this.returnType = returnType;
        this.containerType = containerType;
        this.elementType = elementType;
        this.sqlBuilderClosure = sqlBuilderClosure;
        this.parameterTypeList = parameterTypeList;
        this.sqlExecutor = sqlExecutor;
        this.resultHandler = resultHandler;
    }
}
