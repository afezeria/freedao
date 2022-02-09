//package test.java.crud.count;
//
//import com.github.afezeria.freedao.Long2IntegerResultHandler;
//import com.github.afezeria.freedao.runtime.classic.DaoContext;
//import com.github.afezeria.freedao.runtime.classic.LogHelper;
//import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
//import com.github.afezeria.freedao.runtime.classic.SqlSignature;
//import java.lang.Exception;
//import java.lang.Integer;
//import java.lang.Object;
//import java.lang.Override;
//import java.lang.RuntimeException;
//import java.lang.String;
//import java.lang.StringBuilder;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public final class PersonCountDaoImpl implements PersonCountDao {
//    private static final Logger __logger = LoggerFactory.getLogger(PersonCountDaoImpl.class);
//
//    private final SqlSignature count_0_sign = new SqlSignature(com.github.afezeria.freedao.StatementType.SELECT,test.java.crud.count.PersonCountDaoImpl.class,"count",Integer.class);
//
//    private final Function<Object[], Object[]> count_0_sql = _params -> {
//
//        List<Object> l_sqlArgs_0 = new ArrayList<>();
//        StringBuilder l_builder_0 = new StringBuilder();
//        //select
//        l_builder_0.append("\n"
//                + "select count(*) as _cot from \"person\"\n");
//
//        String l_sql_0 = l_builder_0.toString();
//        return new Object[]{l_sql_0, l_sqlArgs_0};
//    };
//
//    private final SqlExecutor<Integer> count_0_executor = (__connection, __method_args__, __sql, __args) -> {
//        if (__logger.isDebugEnabled()) {
//            LogHelper.logSql(__logger, __sql);
//            LogHelper.logArgs(__logger, __args);
//        }
//        try (PreparedStatement __stmt = __connection.prepareStatement(__sql)) {
//            for (int __idx = 0; __idx < __args.size(); __idx++) {
//                __stmt.setObject(__idx + 1, __args.get(__idx));
//            }
//            __stmt.execute();
//            ResultSet __rs = __stmt.getResultSet();
//            Integer __item = null;
//            while (__rs.next()) {
//                __item = Long2IntegerResultHandler.handle(__rs.getObject(1));
//                break;
//            }
//            return __item;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    };
//
//    @Autowired
//    private DaoContext __context__;
//
//    @Override
//    public Integer count() {
//        Object[] __method_args__ = {};
//        Object[] __sqlAndArgs__ = __context__.buildSql(count_0_sign, __method_args__, count_0_sql);
//        return __context__.execute(count_0_sign, __method_args__, (String) __sqlAndArgs__[0], (List<Object>) __sqlAndArgs__[1], count_0_executor);
//    }
//}
