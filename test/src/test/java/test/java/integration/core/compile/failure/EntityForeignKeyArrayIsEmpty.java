package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.Table;
import lombok.Data;
import test.Entity;
import test.Person;

/**
 * @author afezeria
 */
@Table(name = "ta", primaryKeys = {"id"})
@Join(id = "_p", foreignKey = {}, entityClass = Person.class)
@Data
public class EntityForeignKeyArrayIsEmpty implements Entity {
    private String name;
}