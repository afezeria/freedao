//package expect;
//
//import com.github.afezeria.freedao.runtime.spring.DaoContext;
//
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//
///**
// * @date 2021/12/6
// */
//public class JavaPersonDaoImpl implements JavaPersonDao {
//    private DaoContext context;
//
//    public List<?> abc() {
//        return null;
//    }
//
//    @Override
//    public List<Person> selectByName(String name) {
//        Function<ResultSet, List<Person>> function = resultSet -> {
//            try {
//                List<Person> list = new ArrayList<>();
//                while (resultSet.next()) {
//                    Person person = new Person();
//                    person.setId(resultSet.getObject("id", Long.class));
//                    person.setAge(resultSet.getObject("age", Integer.class));
//                    person.setName(resultSet.getObject("name", String.class));
//                    list.add(person);
//                }
//                return list;
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        };
////        return context.interceptMethodInvoke(new Object[]{name}, params -> {
////            return context.interceptSqlBuild(() -> {
////                String _name = (String) params[0];
////                StringBuilder __builder__ = new StringBuilder();
////                __builder__.append("select id,age,name from person where name = ?");
////                String __sql__ = __builder__.toString();
////
////                return new Object[]{new Object[]{_name}, __sql__};
////            });
////        });
//        return null;
//    }
//
////    @Override
////    public List<Person> selectByIds(List<Long> ids) {
////        Function<ResultSet, Person> handler = (rs) -> {
////            Person person = new Person();
////            try {
////                person.setId(rs.getObject("id", Long.class));
////                person.setAge(rs.getObject("age", Integer.class));
////                person.setName(rs.getObject("name", String.class));
////            } catch (Exception e) {
////                throw new RuntimeException(e);
////            }
////            return person;
////        };
////        return context.interceptMethodInvoke(new Object[]{ids}, params -> {
////            context.interceptSqlBuild(() -> {
////                List<Long> _ids = (List<Long>) params[0];
////
////                List<Object> sqlArgs = new ArrayList<>();
////                StringBuilder __builder__ = new StringBuilder();
////                __builder__.append("select *");
////                __builder__.append("from person");
////                __builder__.append("where id in");
////
////                StringBuilder __builder__1 = new StringBuilder();
////                __builder__1.append("(");
////                for (Long _item : _ids) {
////                    __builder__1.append(_item);
////                    __builder__1.append(",");
////                    sqlArgs.add(_item);
////                }
////                if (__builder__1.length() > 1) {
////                    __builder__1.deleteCharAt(__builder__1.lastIndexOf(","));
////                }
////
////                __builder__1.append(")");
////                __builder__.append(__builder__1);
////                String __sql__ = __builder__.toString();
////                return new Object[]{sqlArgs, __sql__};
////            });
////            return null;
////        });
////    }
////
////    @Override
////    public List<User> selectByNameLikeAndIdIn(String name, List<Long> ids) {
////
////        return context.interceptMethodInvoke(new Object[]{name, ids}, params -> {
////            context.interceptSqlBuild(() -> {
////                String _name = (String) params[0];
////                List<Long> _ids = (List<Long>) params[1];
////
////                List<Object> sqlArgs = new ArrayList<>();
////                List<Object> __sqlParameterList__ = new ArrayList<>();
////                StringBuilder __builder__ = new StringBuilder();
////                __builder__.append("select *");
////                __builder__.append("from person");
////                {
////                    StringBuilder __builder__1 = new StringBuilder();
////                    __builder__1.append("and name like 'a%'");
////                    __builder__1.append("and id in");
////                    {
////
////                        StringBuilder __builder__2 = new StringBuilder();
////                        __builder__2.append("(");
////                        for (Long _item : _ids) {
////                            __builder__2.append(_item);
////                            sqlArgs.add(_item);
////                            __builder__2.append(",");
////                        }
////                        if (__builder__2.length() > 1) {
////                            __builder__2.deleteCharAt(__builder__2.lastIndexOf(","));
////                        }
////
////                        __builder__2.append(")");
////                        __builder__1.append(__builder__2);
////                    }
////                    __builder__1 = FreedaoStringBuilderUtil.trimSuffix(__builder__1, "and", "or", " ");
////                    __builder__1 = FreedaoStringBuilderUtil.trimPrefix(__builder__1, "and", "or", " ");
////                    __builder__.append("where ");
////                    __builder__.append(__builder__1);
////                }
////                String __sql__ = __builder__.toString();
////                return new Object[]{sqlArgs, __sql__};
////            });
////            return null;
////        });
////
////    }
////
////    @Override
////    public List<User> select(List<Long> list) {
////        return context.interceptMethodInvoke(new Object[]{list}, params -> {
////            context.interceptSqlBuild(() -> {
////                List<Long> _list = (List<Long>) params[1];
////
////                List<Object> __sqlParameterList__ = new ArrayList<>();
////                StringBuilder __builder__ = new StringBuilder();
////                __builder__.append("select * from person");
////                boolean _flag = _list.size() > 0;
////                if (_flag) {
////                    StringBuilder __builder__1 = new StringBuilder();
////                    __builder__1.append("id in");
////                    {
////                        StringBuilder __builder__2 = new StringBuilder();
////                        __builder__2.append("(");
////                        for (Long _item : _list) {
////                            __builder__2.append(_item);
////                            __sqlParameterList__.add(_item);
////                            __builder__2.append(",");
////                        }
////                        if (__builder__2.length() > 1) {
////                            __builder__2.deleteCharAt(__builder__2.lastIndexOf(","));
////                        }
////                        __builder__2.append(")");
////                        __builder__1.append(__builder__2);
////                    }
////                    __builder__.append("where ");
////                    __builder__.append(__builder__1);
////                }
////                String __sql__ = __builder__.toString();
////                return new Object[]{__sqlParameterList__, __sql__};
////            });
////            return null;
////        });
////
////    }
//}
