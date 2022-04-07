package com.github.afezeria.freedao.runtime.spring;

import com.github.afezeria.freedao.classic.runtime.context.DaoContext;
import com.github.afezeria.freedao.classic.runtime.context.ExecutorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 *
 */
@ConditionalOnMissingBean(type = "com.github.afezeria.freedao.classic.runtime.context.DaoContext")
@Configuration(proxyBeanMethods = false)
public class FreedaoConfiguration {
    @Autowired
    private DataSource dataSource;

    @Bean
    public DaoContext daoContext() {
        return new ExecutorContext(new SpringTransactionContext(dataSource));
    }
}