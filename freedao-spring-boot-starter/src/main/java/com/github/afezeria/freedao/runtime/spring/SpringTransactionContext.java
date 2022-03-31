package com.github.afezeria.freedao.runtime.spring;

import com.github.afezeria.freedao.runtime.classic.context.DaoContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * @author afezeria
 */
public class SpringTransactionContext extends DaoContext {

    private final DataSource dataSource;
    private final SQLErrorCodeSQLExceptionTranslator translator;

    public SpringTransactionContext(DataSource dataSource) {
        super(null);
        this.dataSource = dataSource;
        this.translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
    }

    @Override
    public <T> T withTx(Function<Connection, T> supplier) {
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            return supplier.apply(connection);
        } catch (RuntimeException e) {
            Throwable throwable = e.getCause();
            DataAccessException ex = null;
            if (throwable instanceof SQLException) {
                ex = translator.translate("freedao", null, (SQLException) throwable);
            }
            if (ex != null) {
                throw ex;
            }
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
