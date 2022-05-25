package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Table;
import lombok.Data;

/**
 * @author afezeria
 */
@Table
@Data
public class NoPrimaryKeyEntity {
    private String name;
}
