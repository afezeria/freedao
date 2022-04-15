package test.component;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.classic.runtime.DS;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Order.class)
public interface OrderDao {
    List<Order> queryByIdGreaterThan(Integer id);

    List<Order> list(Order order);

    int insert(Order order);

    Integer count(Order order);

    @DS(Db.MASTER_1)
    Order selectOneById(Integer id);
}
