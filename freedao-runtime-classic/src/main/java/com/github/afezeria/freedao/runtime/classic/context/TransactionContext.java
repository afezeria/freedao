package com.github.afezeria.freedao.runtime.classic.context;

import com.github.afezeria.freedao.runtime.classic.DaoContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 */
public class TransactionContext extends DaoContext {

    Map<String, DataSource> map;

    DataSource defaultDataSource;

    public TransactionContext(LinkedHashMap<String, DataSource> map) {
        if (map.isEmpty()) {
            throw new IllegalArgumentException("map cannot be empty");
        }
        for (Map.Entry<String, DataSource> entry : map.entrySet()) {
            defaultDataSource = entry.getValue();
            break;
        }
        this.map = map;
    }

    @Override
    public <T> T withConnection(Function<Connection, T> function) {
        try {
            if (currentConnection != null) {
                return function.apply(currentConnection);
            } else if (currentDataSource != null) {
                if (transactionFlag) {
                    currentConnection = currentDataSource.getConnection();
                    return function.apply(currentConnection);
                } else {
                    try (Connection connection = currentDataSource.getConnection()) {
                        return function.apply(connection);
                    }
                }
            } else {
                try (Connection connection = defaultDataSource.getConnection()) {
                    return function.apply(connection);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DataSource currentDataSource = null;

    private Boolean transactionFlag = false;

    private Connection currentConnection = null;

    @Override
    public Connection getConnection() {
        try {
            if (currentConnection != null) {
                return currentConnection;
            } else if (currentDataSource != null) {
                currentConnection = currentDataSource.getConnection();
                return currentConnection;
            } else {
                return defaultDataSource.getConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
