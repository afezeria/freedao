package io.github.afezeria.freedao.classic.runtime;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 分页结果
 *
 * @author afezeria
 */
@Slf4j
@Getter
public class Page<E> {
    /**
     * 总数
     */
    private Long total;

    /**
     * 每页条数
     */
    private int pageSize;
    /**
     * 当前页码，从1开始
     */
    private int pageIndex;

    /**
     * 不查询总条数
     */
    private boolean skipCount = false;

    /**
     * 查询结果
     */
    private List<E> records;

    /**
     * 排序
     */
    private String orderBy;

    /**
     * 不分页只排序
     */
    private boolean orderByOnly = true;


    private Page() {
    }

    public static Page<?> of(String orderBy) {
        Page<?> page = new Page<>();
        page.setOrderBy(orderBy);
        return page;
    }

    public static Page<?> of(int pageIndex, int pageSize) {
        Page<?> page = new Page<>();
        page.setIndexAndSize(pageIndex, pageSize);
        return page;
    }

    public Page<E> setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Page<E> setIndexAndSize(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            throw new IllegalArgumentException("pageIndex must greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must greater than 0");
        }
        if (pageSize > FreedaoGlobalConfiguration.maxPageSizeLimit) {
            pageSize = FreedaoGlobalConfiguration.maxPageSizeLimit;
            log.debug("pageSize({}) exceeds the maxPageSizeLimit({})", pageSize, FreedaoGlobalConfiguration.maxPageSizeLimit);
        }
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.orderByOnly = false;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Page<?> setSkipCount(boolean skipCount) {
        this.skipCount = skipCount;
        return this;
    }

    public Page<E> setRecords(List<E> records) {
        this.records = records;
        return this;
    }

    public Page<E> setTotal(Long total) {
        this.total = total;
        return this;
    }

    public int getOffset() {
        return (pageIndex - 1) * pageSize;
    }

    public int getLimit() {
        return pageSize;
    }

    public long getPages() {
        if (pageSize == 0) {
            return 0L;
        }
        long pages = total / pageSize;
        if (total % pageSize != 0) {
            pages++;
        }
        return pages;
    }

}
