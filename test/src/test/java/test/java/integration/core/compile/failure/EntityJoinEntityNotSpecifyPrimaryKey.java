package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.Table;
import lombok.Data;
import test.Entity;

/**
 * @author afezeria
 */
@Table(name = "ta", primaryKeys = {"id"})
@Join(id = "_p", foreignKey = {"name"}, entityClass = NoPrimaryKeyEntity.class)
@Data
public class EntityJoinEntityNotSpecifyPrimaryKey implements Entity {
    private String name;
}