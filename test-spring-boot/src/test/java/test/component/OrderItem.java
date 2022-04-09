package test.component;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;

@Table(name = "t_order_item", primaryKeys = {"id"})
@Data
public class OrderItem {
    @Column(insert = false)
    @AutoFill
    private Integer id;
    private Integer orderId;
    private String name;
}