package io.github.afezeria.freedao.classic.runtime;

import org.slf4j.Logger;

import java.util.List;
import java.util.StringTokenizer;

/**
 */
public class LogHelper {

    public static void logSql(Logger logger, String sql) {
        StringTokenizer tokenizer = new StringTokenizer(sql);
        StringBuilder builder = new StringBuilder();
        boolean hasMoreTokens = tokenizer.hasMoreTokens();
        while (hasMoreTokens) {
            builder.append(tokenizer.nextToken());
            hasMoreTokens = tokenizer.hasMoreTokens();
            if (hasMoreTokens) {
                builder.append(' ');
            }
        }
        logger.debug("==>  Preparing: {}", builder);
    }

    public static void logArgs(Logger logger, List<Object> args) {
        StringBuilder builder = new StringBuilder();
        for (Object o : args) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            if (o == null) {
                builder.append("null");
            } else {
                builder.append(o);
                builder.append("(").append(o.getClass().getSimpleName()).append(")");
            }
        }
        logger.debug("==> Parameters: {}", builder);
    }
//    public static void logResult(Statement statement){
//
//        if (t == null) {
//            logger.debug("<==      Total: {}", 0);
//        } else if (t instanceof Collection) {
//            logger.debug("<==      Total: {}", ((Collection<?>) t).size());
//        } else {
//            logger.debug("<==      Total: {}", 1);
//        }
//
//
//    }
}
