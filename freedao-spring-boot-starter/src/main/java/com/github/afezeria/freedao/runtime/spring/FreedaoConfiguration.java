package com.github.afezeria.freedao.runtime.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 */
@Configuration(proxyBeanMethods = false)
//@Configuration
//@EnableConfigurationProperties(DemoProperties.class)
@ConditionalOnProperty(
        prefix = "demo",
        name = "isopen",
        havingValue = "true"
)
public class FreedaoConfiguration {
//    public DataSourceProperties abc(){
//
//    }

    @Bean
    @ConfigurationProperties("app.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

}