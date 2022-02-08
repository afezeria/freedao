package com.github.afezeria.freedao.runtime.spring;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 */
@ConfigurationProperties(prefix = "freedao")
public class MultipleDataSourceProperties {
    private List<DataSourceProperties> datasource;
}
