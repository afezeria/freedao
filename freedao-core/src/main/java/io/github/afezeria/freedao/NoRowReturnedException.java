package io.github.afezeria.freedao;

/**
 * 方法类型为查询放方法，返回值为原始类型，且sql未返回一行数据时抛出该异常
 *
 * @author afezeria
 */
public class NoRowReturnedException extends RuntimeException {
}
