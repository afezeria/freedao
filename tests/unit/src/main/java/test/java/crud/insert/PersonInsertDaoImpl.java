//package test.java.crud.insert;
//
//import com.github.afezeria.freedao.runtime.classic.DaoContext;
//import com.github.afezeria.freedao.runtime.classic.LogHelper;
//import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
//import com.github.afezeria.freedao.runtime.classic.SqlSignature;
//import java.lang.Exception;
//import java.lang.Integer;
//import java.lang.Long;
//import java.lang.Object;
//import java.lang.Override;
//import java.lang.RuntimeException;
//import java.lang.String;
//import java.lang.StringBuilder;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Function;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public final class PersonInsertDaoImpl implements PersonInsertDao {
//    private static final Logger __logger = LoggerFactory.getLogger(PersonInsertDaoImpl.class);
//
//    private final SqlSignature insert_0_sign = new SqlSignature(com.github.afezeria.freedao.StatementType.INSERT,test.java.crud.insert.PersonInsertDaoImpl.class,"insert",int.class, test.java.crud.insert.PersonInsert.class);
//
//    private final Function<Object[], Object[]> insert_0_sql = _params -> {
//        PersonInsert p_entity = (PersonInsert) _params[0];
//
//        List<Object> l_sqlArgs_0 = new ArrayList<>();
//        StringBuilder l_builder_0 = new StringBuilder();
//        //insert
//        l_builder_0.append("\n"
//                + "insert into \"person\" (\"name\")\n"
//                + "values (?)\n");
//        String l_tmpVar_0 = p_entity.getName();
//        l_sqlArgs_0.add(l_tmpVar_0);
//
//        String l_sql_0 = l_builder_0.toString();
//        return new Object[]{l_sql_0, l_sqlArgs_0};
//    };
//
//    private final SqlExecutor<Integer> insert_0_executor = (__connection, __method_args__, __sql, __args) -> {
//        if (__logger.isDebugEnabled()) {
//            LogHelper.logSql(__logger, __sql);
//            LogHelper.logArgs(__logger, __args);
//        }
//        try (PreparedStatement __stmt = __connection.prepareStatement(__sql, Statement.RETURN_GENERATED_KEYS)) {
//            for (int __idx = 0; __idx < __args.size(); __idx++) {
//                __stmt.setObject(__idx + 1, __args.get(__idx));
//            }
//            __stmt.execute();
//            ResultSet __rs = __stmt.getGeneratedKeys();
//            PersonInsert __item = (PersonInsert) __method_args__[0];
//            while (__rs.next()) {
//                __item.setId(__rs.getObject("id",Long.class));
//                break;
//            }
//            return __stmt.getUpdateCount();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    };
//
//    @Autowired
//    private DaoContext __context__;
//
//    @Override
//    public int insert(PersonInsert entity) {
//        Objects.requireNonNull(entity);
//        Object[] __method_args__ = {entity};
//        Object[] __sqlAndArgs__ = __context__.buildSql(insert_0_sign, __method_args__, insert_0_sql);
//        return __context__.execute(insert_0_sign, __method_args__, (String) __sqlAndArgs__[0], (List<Object>) __sqlAndArgs__[1], insert_0_executor);
//    }
//}
