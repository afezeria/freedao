package com.github.afezeria.freedao.classic.runtime;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 分页结果
 *
 * @author afezeria
 */
@Data
@Slf4j
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

    public Page() {
    }

    public void setPageSize(int pageSize) {
        if (pageSize > FreedaoGlobalConfiguration.maxPageSizeLimit) {
            this.pageSize = FreedaoGlobalConfiguration.maxPageSizeLimit;
            log.debug("pageSize({}) exceeds the maxPageSizeLimit({})", pageSize, FreedaoGlobalConfiguration.maxPageSizeLimit);
            return;
        }
        this.pageSize = pageSize;
    }

    public long getPages() {
        if (pageSize == 0) {
            return 0L;
        }
        long pages = count / pageSize;
        if (count % pageSize != 0) {
            pages++;
        }
        return pages;
    }

}
