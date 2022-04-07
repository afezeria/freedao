package com.github.afezeria.freedao.spring.runtime;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@ConfigurationProperties(prefix = "freedao.datasource")
@Data
@Component
public class FreedaoProperties {
    /**
     * 默认数据源名称
     */
    private String primary = "master";
    private LinkedHashMap<String, HikariDataSource> datasource;

}
