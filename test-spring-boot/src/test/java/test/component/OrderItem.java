package test.component;

import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Table;
import io.github.afezeria.freedao.classic.runtime.AutoFill;
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