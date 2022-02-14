package test.java.xml.complex;

import com.github.afezeria.freedao.annotation.XmlTemplate;
import com.github.afezeria.freedao.runtime.classic.DaoContext;
import com.github.afezeria.freedao.runtime.classic.LogHelper;
import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
import com.github.afezeria.freedao.runtime.classic.SqlSignature;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.StringBuilder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Person;

public final class TestExpr1DaoImpl implements TestExpr1Dao {
  private static final Logger __logger = LoggerFactory.getLogger(TestExpr1DaoImpl.class);

  private final SqlSignature query_0_sign = new SqlSignature(com.github.afezeria.freedao.StatementType.SELECT,test.java.xml.complex.TestExpr1DaoImpl.class,"query",List.class, test.java.xml.complex.TestExpr1Dao.A.class, java.lang.Boolean.class, java.lang.Long.class, java.lang.Boolean.class, java.lang.Double.class, java.lang.Object.class, java.lang.Boolean.class, java.util.List.class);

  private final Function<Object[], Object[]> query_0_sql = _params -> {
    TestExpr1Dao.A p_a = (TestExpr1Dao.A) _params[0];
    Boolean p_b = (Boolean) _params[1];
    Long p_c = (Long) _params[2];
    Boolean p_d = (Boolean) _params[3];
    Double p_e = (Double) _params[4];
    Object p_f = (Object) _params[5];
    Boolean p_g = (Boolean) _params[6];
    List<Map<String, TestExpr1Dao.D>> p_h = (List<Map<String, TestExpr1Dao.D>>) _params[7];

    List<Object> l_sqlArgs_0 = new ArrayList<>();
    StringBuilder l_builder_0 = new StringBuilder();
    //select
    l_builder_0.append("\n"
            + "select * from person ");
    Boolean l_tmpVar_0 = false;
    l_tmpVar_0 = (((((((Objects.equals(p_a.getG(), p_b))) && ((p_c).compareTo(1L) > 0)) && (((p_b && p_d) && ((p_e).compareTo(-1.2) <= 0)))) && ((p_f == null))) && (p_g || true)) && (((p_h.get(0).get("abc").getC().get(2)).compareTo(1) > 0)));
    if (l_tmpVar_0) {
      //if test="(a.g == b ) and c > 1L and (b and d and e <= -1.2) and (f == null) and g or true and (h.0.\"abc\".c.2 > 1)"
      l_builder_0.append("where 1=1");
    }
    l_builder_0.append("\n");

    String l_sql_0 = l_builder_0.toString();
    return new Object[]{l_sql_0, l_sqlArgs_0};
  };

  private final SqlExecutor<List<Person>> query_0_executor = (__connection, __method_args__, __sql, __args) -> {
    if (__logger.isDebugEnabled()) {
      LogHelper.logSql(__logger, __sql);
      LogHelper.logArgs(__logger, __args);
    }
    try (PreparedStatement __stmt = __connection.prepareStatement(__sql)) {
      for (int __idx = 0; __idx < __args.size(); __idx++) {
        __stmt.setObject(__idx + 1, __args.get(__idx));
      }
      __stmt.execute();
      ResultSet __rs = __stmt.getResultSet();
      List<Person> __list = new ArrayList<>();
      Person __item = null;
      while (__rs.next()) {
        __item = new Person(
        );
        __item.setId(__rs.getObject("id",Long.class));
        __item.setName(__rs.getObject("name",String.class));
        __item.setWhenCreated(__rs.getObject("when_created",LocalDateTime.class));
        __item.setActive(__rs.getObject("active",Boolean.class));
        __list.add(__item);
      }
      return __list;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  };

  private DaoContext __context__;

  @Override
  @XmlTemplate("<select>\n"
          + "select * from person <if test='(a.g == b ) and c > 1L and (b and d and e &lt;= -1.2) and (f == null) and g or true and (h.0.\"abc\".c.2 > 1)'>where 1=1</if>\n"
          + "</select>\n")
  public List<Person> query(TestExpr1Dao.A a, Boolean b, Long c, Boolean d, Double e, Object f,
                            Boolean g, List<Map<String, TestExpr1Dao.D>> h) {
    Object[] __method_args__ = {a, b, c, d, e, f, g, h};
    Object[] __sqlAndArgs__ = __context__.buildSql(query_0_sign, __method_args__, query_0_sql);
    return __context__.execute(query_0_sign, __method_args__, (String) __sqlAndArgs__[0], (List<Object>) __sqlAndArgs__[1], query_0_executor);
  }
}
