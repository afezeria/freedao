package com.github.afezeria.freedao.spring.runtime;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author afezeria
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final DataSourceSelectStrategy strategy;

    private final Map<String, String[]> prefixMap;

    public DynamicDataSource(DataSourceSelectStrategy strategy, FreedaoProperties freedaoProperties) {
        this.strategy = strategy;
        prefixMap = getAllCommonPrefixAndStringArray(freedaoProperties.getDatasource().keySet().toArray(new String[0]));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return strategy.apply(prefixMap);
    }

    public static Map<String, String[]> getAllCommonPrefixAndStringArray(String[] strings) {
        Map<String, LinkedHashSet<String>> map = new HashMap<>();
        for (int i = 0; i < strings.length; i++) {
            String s1 = strings[i];
            for (int j = i; j < strings.length; j++) {
                String s2 = strings[j];
                String prefix = getPrefix(s1, s2);
                if (!prefix.isEmpty()) {
                    LinkedHashSet<String> set = map.computeIfAbsent(prefix, it -> new LinkedHashSet<>());
                    set.add(s1);
                    set.add(s2);
                }
            }
        }
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toArray(new String[0])));
    }

    public static String getPrefix(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        int i = 0;
        while (i < minLength) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
            i++;
        }
        return s1.substring(0, i);
    }
}
