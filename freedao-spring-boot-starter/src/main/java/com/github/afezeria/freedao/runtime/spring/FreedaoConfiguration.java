package com.github.afezeria.freedao.runtime.spring;

import com.github.afezeria.freedao.runtime.classic.DaoContext;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
@EnableConfigurationProperties(FreedaoProperties.class)
@ConditionalOnMissingBean(type = "com.github.afezeria.freedao.runtime.classic.DaoContext")
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "freedao",
        name = "active",
        havingValue = "true"
)
public class FreedaoConfiguration {

    @Autowired
    private FreedaoProperties freedaoProperties;

    @Bean
    public DaoContext daoContext() {
        LinkedHashMap<String, DataSource> map = new LinkedHashMap<>();
        for (Map.Entry<String, HikariConfig> entry : freedaoProperties.getDataSources().entrySet()) {
            map.put(entry.getKey(), new HikariDataSource(entry.getValue()));
        }
        return DaoContext.builder()
                .withDefault(map)
                .build();
    }
}