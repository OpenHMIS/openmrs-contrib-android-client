package org.openmrs.mobile.data;

/**
 * This class contains the paging information used by the entity services to paginate results. Both page and pageSize are
 * 1-based, defining either as 0 will cause paging to be ignored.
 */
public class PagingInfo {
    private int page;
    private int pageSize;

    public PagingInfo() {
    }

    /**
     * Creates a new {@link PagingInfo} instance.
     *
     * @param page     The 1-based number of the page being requested.
     * @param pageSize The number of records to include on each page.
     */
    public PagingInfo(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getLimit() {
        return pageSize;
    }

    public int getStartIndex() {
        return ((page - 1) * pageSize) + 1;
    }
}
