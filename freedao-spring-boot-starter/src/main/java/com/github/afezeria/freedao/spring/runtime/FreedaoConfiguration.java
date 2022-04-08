package com.github.afezeria.freedao.spring.runtime;

import com.github.afezeria.freedao.classic.runtime.context.DaoContext;
import com.github.afezeria.freedao.classic.runtime.context.ExecutorContext;
import com.github.afezeria.freedao.classic.runtime.context.PaginationQueryContext;
import com.github.afezeria.freedao.classic.runtime.context.ParameterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ConditionalOnMissingBean(type = "com.github.afezeria.freedao.classic.runtime.context.DaoContext")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FreedaoProperties.class)
public class FreedaoConfiguration {

    @Autowired
    private FreedaoProperties freedaoProperties;

    @Autowired(required = false)
    private DataSourceSelectStrategy strategy;

    @Bean
    public DaoContext daoContext(DataSource dataSource) {
        return new ParameterContext(new PaginationQueryContext(new ExecutorContext(new SpringTransactionContext(dataSource))), null);
    }

    @Bean
    @ConditionalOnMissingBean(type = "javax.sql.DataSource")
    public DataSource abstractRoutingDataSource() {
        if (strategy == null) {
            strategy = DataSourceSelectStrategy.DEFAULT;
        }
        if (!freedaoProperties.getDatasource().containsKey(freedaoProperties.getPrimary())) {
            throw new IllegalArgumentException("missing '" + freedaoProperties.getPrimary() + "' dataSource");
        }

        Map<Object, Object> dataSources = new HashMap<>(freedaoProperties.getDatasource());
        DynamicDataSource ds = new DynamicDataSource(strategy, freedaoProperties);
        ds.setTargetDataSources(dataSources);
        ds.setDefaultTargetDataSource(dataSources.get(freedaoProperties.getPrimary()));
        return ds;
    }

    @Bean
    public FreedaoDatasourceInterceptor freedaoDatasourceInterceptor() {
        return new FreedaoDatasourceInterceptor();
    }
}