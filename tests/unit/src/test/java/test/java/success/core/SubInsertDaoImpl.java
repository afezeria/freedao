package test.java.success.core;

import com.github.afezeria.freedao.runtime.classic.DbGenerator;
import com.github.afezeria.freedao.runtime.classic.LogHelper;
import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
import com.github.afezeria.freedao.runtime.classic.SqlSignature;
import com.github.afezeria.freedao.runtime.classic.context.DaoContext;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Person;
import test.PersonType;

public final class SubInsertDaoImpl implements SubInsertDao {
  private static final Logger __logger = LoggerFactory.getLogger(SubInsertDaoImpl.class);

  private final SqlSignature insert_0_sign = new SqlSignature(com.github.afezeria.freedao.StatementType.INSERT,test.java.success.core.SubInsertDaoImpl.class,"insert",int.class, test.Person.class, java.util.Map.class);

  private final Function<Object[], Object[]> insert_0_sql = _params -> {
    Person p_arg0 = (Person) _params[0];
    Map<String, Object> p__context_parameter = (Map<String, Object>) _params[1];

    List<Object> l_sqlArgs_0 = new ArrayList<>();
    StringBuilder l_builder_0 = new StringBuilder();
    //insert 
    String l_tmpVar_0 = p_arg0.getName();
    l_sqlArgs_0.add(l_tmpVar_0);
    Boolean l_tmpVar_1 = p_arg0.getActive();
    l_sqlArgs_0.add(l_tmpVar_1);
    LocalDateTime l_tmpVar_2 = p_arg0.getWhenCreated();
    l_sqlArgs_0.add(l_tmpVar_2);
    Integer l_tmpVar_3 = p_arg0.getAge();
    l_sqlArgs_0.add(l_tmpVar_3);
    String l_tmpVar_4 = p_arg0.getNickName();
    l_sqlArgs_0.add(l_tmpVar_4);
    PersonType l_tmpVar_5 = p_arg0.getType();
    l_sqlArgs_0.add(test.Enum2StringParameterTypeHandler.handleParameter(l_tmpVar_5));
    l_builder_0.append("\n"
            + "insert into \"person\" (\"name\", \"active\", \"when_created\", \"age\", \"nick_name\", \"type\")\n"
            + "values (?, ?, ?, ?, ?, ?)\n");

    String l_sql_0 = l_builder_0.toString();
    return new Object[]{l_sql_0, l_sqlArgs_0};
  };

  private final SqlExecutor<Integer> insert_0_executor = (__connection, __method_args__, __sql, __args) -> {
    if (__logger.isDebugEnabled()) {
      LogHelper.logSql(__logger, __sql);
      LogHelper.logArgs(__logger, __args);
    }
    try (PreparedStatement __stmt = __connection.prepareStatement(__sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int __idx = 0; __idx < __args.size(); __idx++) {
        __stmt.setObject(__idx + 1, __args.get(__idx));
      }
      __stmt.execute();
      ResultSet __rs = __stmt.getGeneratedKeys();
      Person __item = (Person) __method_args__[0];
      while (__rs.next()) {
        __item.setId((Long) DbGenerator.gen(__rs, "id", Long.class));
        break;
      }
      return __stmt.getUpdateCount();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  };

  private DaoContext __context__;

  @Override
  @SuppressWarnings("unchecked")
  public int insert(Person arg0) {
    Object[] __method_args__ = {arg0};
    Object[] __sqlAndArgs__ = __context__.buildSql(insert_0_sign, __method_args__, insert_0_sql);
    return __context__.execute(insert_0_sign, __method_args__, (String) __sqlAndArgs__[0], (List<Object>) __sqlAndArgs__[1], insert_0_executor);
  }
}
