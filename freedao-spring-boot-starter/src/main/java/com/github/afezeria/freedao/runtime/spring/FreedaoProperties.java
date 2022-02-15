package com.github.afezeria.freedao.runtime.spring;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 *
 */
@ConfigurationProperties(prefix = "freedao")
@Data
@Component
public class FreedaoProperties {
    private Boolean active;
    private LinkedHashMap<String, HikariConfig> dataSources;
}
