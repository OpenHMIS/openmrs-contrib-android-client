package org.openmrs.mobile.data;

import android.support.annotation.Nullable;

/**
 * This class contains the paging information used by the entity services to paginate results. Both page and pageSize are
 * 1-based, defining either as 0 will cause paging to be ignored.
 */
public class PagingInfo {
	/**
	 * Sets the page size to 10,000 so that all results will be included and the server-defined default limit won't be used.
	 */
	public static final PagingInfo ALL = new PagingInfo(1,10000);

	private int page;
	private int pageSize;
	private Integer totalRecordCount;
	private boolean loadRecordCount;

	public PagingInfo() {
	}

	/**
	 * Creates a new {@link PagingInfo} instance.
	 * @param page     The 1-based number of the page being requested.
	 * @param pageSize The number of records to include on each page.
	 */
	public PagingInfo(int page, int pageSize) {
		this.page = page;
		this.pageSize = pageSize;
	}

	public static Integer getPage(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getPage();
	}

	public static Integer getPageSize(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getPageSize();
	}

	public static Integer getLimit(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getLimit();
	}

	public static Integer getStartIndex(@Nullable PagingInfo pagingInfo) {
		return pagingInfo == null ? null : pagingInfo.getStartIndex();
	}

	public static boolean isValid(PagingInfo pagingInfo) {
		return !(pagingInfo == null || pagingInfo.getPage() == 0);
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
		if (page == 0)
			return 0;
		return ((page - 1) * pageSize) + 1;
	}

	public Integer getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(Integer totalRecordCount) {
		this.totalRecordCount = totalRecordCount;

		// If the total records is set to anything other than null, than don't reload the count
		this.loadRecordCount = totalRecordCount == null;
	}

	public Integer getTotalPages() {
		return (pageSize + getTotalRecordCount() - 1) / pageSize;
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
