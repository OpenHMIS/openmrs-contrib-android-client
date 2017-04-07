package org.openmrs.mobile.data;

/**
 * This class contains the paging information used by the entity services to paginate results. Both page and pageSize are
 * 1-based, defining either as 0 will cause paging to be ignored.
 */
public class PagingInfo {
    private int page;
    private int pageSize;
    private Long totalRecordCount;
    private boolean loadRecordCount;

    public PagingInfo() {}

    /**
     * Creates a new {@link PagingInfo} instance.
     * @param page The 1-based number of the page being requested.
     * @param pageSize The number of records to include on each page.
     */
    public PagingInfo(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;

        this.loadRecordCount = true;
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

    public Long getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(Long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;

        // If the total records is set to anything other than null, than don't reload the count
        this.loadRecordCount = totalRecordCount == null;
    }

    public boolean shouldLoadRecordCount() {
        return loadRecordCount;
    }

    public void setLoadRecordCount(boolean loadRecordCount) {
        this.loadRecordCount = loadRecordCount;
    }

    public Boolean hasMoreResults() {
        return (page * pageSize) < totalRecordCount;
    }
}
