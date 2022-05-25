package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.Table;
import lombok.Data;
import test.Entity;

/**
 * @author afezeria
 */
@Table(name = "ta", primaryKeys = {"id"})
@Join(id = "_p", foreignKey = {"name"}, referenceKey = {"a", "b"}, table = "abc")
@Data
public class EntityForeignKeySizeNotEqualReferenceKeySize implements Entity {
    private String name;
}