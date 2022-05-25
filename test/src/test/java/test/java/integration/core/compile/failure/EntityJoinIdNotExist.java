package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.ReferenceValue;
import io.github.afezeria.freedao.annotation.Table;
import lombok.Data;
import test.Entity;

/**
 * @author afezeria
 */
@Table(name = "ta", primaryKeys = {"id"})
@Join(id = "_p", foreignKey = {"name"}, table = "abc", referenceKey = {"abc"})
@Data
public class EntityJoinIdNotExist implements Entity {
    @Column(exist = false)
    @ReferenceValue(joinId = "_aa", columnName = "name")
    private String name;
}