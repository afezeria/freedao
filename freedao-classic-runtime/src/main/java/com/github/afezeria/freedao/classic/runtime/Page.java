package com.github.afezeria.freedao.classic.runtime;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 *
 * @author afezeria
 */
@Data
public class Page<E> {
    /**
     * 总数
     */
    private long count;

    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 当前页码，从1开始
     */
    private int pageIndex;

    /**
     * 查询结果
     */
    private List<E> records;

    /**
     * 是否优化count sql
     */
    private boolean optimizeCountSql;

    protected Page() {
    }
}
