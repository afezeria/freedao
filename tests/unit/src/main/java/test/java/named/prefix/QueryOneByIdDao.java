package test.java.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
@Dao(crudEntity = PrefixTestEntity.class)
public interface QueryOneByIdDao {

    PrefixTestEntity queryOneById(@NotNull Long id);
}
