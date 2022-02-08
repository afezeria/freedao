//package test.java.crud.update;
//
//import com.github.afezeria.freedao.runtime.classic.DaoContext;
//import com.github.afezeria.freedao.runtime.classic.LogHelper;
//import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
//import com.github.afezeria.freedao.runtime.classic.SqlSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.sql.PreparedStatement;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Function;
//
//@Component
//public final class PersonUpdateDaoImpl implements PersonUpdateDao {
//    private static final Logger __logger = LoggerFactory.getLogger(PersonUpdateDaoImpl.class);
//
//    private final SqlSignature update_0_sign = new SqlSignature(com.github.afezeria.freedao.StatementType.UPDATE, test.java.crud.update.PersonUpdateDaoImpl.class, "update", int.class, test.java.crud.update.PersonUpdate.class);
//
//    private final Function<Object[], Object[]> update_0_sql = _params -> {
//        PersonUpdate p_entity = (PersonUpdate) _params[0];
//
//        List<Object> l_sqlArgs_0 = new ArrayList<>();
//        StringBuilder l_builder_0 = new StringBuilder();
//        //update
//        l_builder_0.append("\n"
//                + "update \"person\"\n"
//                + "set \"id\" = ?, \"name\" = ?, \"create_date\" = ?\n"
//                + "where \"id\" = ?\n");
//        Long l_tmpVar_0 = p_entity.getId();
//        l_sqlArgs_0.add(l_tmpVar_0);
//        String l_tmpVar_1 = p_entity.getName();
//        l_sqlArgs_0.add(l_tmpVar_1);
//        LocalDateTime l_tmpVar_2 = p_entity.getCreateDate();
//        l_sqlArgs_0.add(l_tmpVar_2);
//        Long l_tmpVar_3 = p_entity.getId();
//        l_sqlArgs_0.add(l_tmpVar_3);
//
//        String l_sql_0 = l_builder_0.toString();
//        return new Object[]{l_sql_0, l_sqlArgs_0};
//    };
//
//    private final SqlExecutor<Integer> update_0_executor = (__connection, __method_args__, __sql, __args) -> {
//        if (__logger.isDebugEnabled()) {
//            LogHelper.logSql(__logger, __sql);
//            LogHelper.logArgs(__logger, __args);
//        }
//        try (PreparedStatement __stmt = __connection.prepareStatement(__sql)) {
//            for (int __idx = 0; __idx < __args.size(); __idx++) {
//                __stmt.setObject(__idx + 1, __args.get(__idx));
//            }
//            __stmt.execute();
//            return __stmt.getLargeUpdateCount();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    };
//
//    @Autowired
//    private DaoContext __context__;
//
//    @Override
//    public int update(PersonUpdate entity) {
//        Objects.requireNonNull(entity);
//        Object[] __method_args__ = {entity};
//        Object[] __sqlAndArgs__ = __context__.buildSql(update_0_sign, __method_args__, update_0_sql);
//        return __context__.execute(update_0_sign, __method_args__, (String) __sqlAndArgs__[0], (List<Object>) __sqlAndArgs__[1], update_0_executor);
//    }
//}
