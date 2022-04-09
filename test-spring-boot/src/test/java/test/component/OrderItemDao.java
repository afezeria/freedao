package test.component;

import com.github.afezeria.freedao.annotation.Dao;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = OrderItem.class)
public interface OrderItemDao {

    int insert(OrderItem item);

    Integer countByOrderId(Integer orderId);

    List<OrderItem> selectByOrderId(Integer orderId);
}
