package test.java.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = PrefixTestEntity.class)
public interface QueryByNameDao {

    List<PrefixTestEntity> queryByName(@NotNull String name);
}
