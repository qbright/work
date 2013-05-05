/**
 * @author qbright
 *
 * @date 2013-1-23
 */
package me.qbright.lpms.web.common;

import java.util.List;
import java.util.Map;

public class Page<T> {

	private long total;
	private int pageNum;
	private int pageSize = 10;
	private List<T> content;
	private Sort sort;
	private boolean isEmpty;
	private boolean isFirstPage;
	private boolean isLastPage;
	private boolean hasNextPage;
	private boolean hasPreviousPage;
	private int totalPages;

	private Map<String, String> sortMap;

	public Page(PageRequest request, long total, List<T> content,
			Map<String, String> sortMap) {
		this.total = total;
		this.pageSize = request.getPageSize();
		this.pageNum = request.getPageNum();
		this.content = content;
		this.sort = request.getSort();
		this.sortMap = sortMap;
		isEmpty();
		isFirstPage();
		isLastPage();
		hasNextPage();
		hasPreviousPage();
	}

	public Sort getSort() {
		return sort;
	}

	private void isEmpty() {
		if (content == null || content.size() == 0) {
			this.isEmpty = true;
		} else {
			this.isEmpty = false;
		}
	}

	public int getTotalPages() {
		if ((int) (total) % pageSize == 0 && total != 0) {
			this.totalPages = (int) (total) / pageSize;
		} else if ((int) (total) % pageSize != 0) {
			this.totalPages = ((int) (total) / pageSize) + 1;
		} else {
			this.totalPages = 1;
		}
		return totalPages;
	}

	private void hasNextPage() {
		getTotalPages();
		if (pageNum < totalPages) {
			this.hasNextPage = true;
		} else {
			this.hasNextPage = false;
		}

	}

	private void hasPreviousPage() {
		if (pageNum > 1) {
			this.hasPreviousPage = true;
		} else {
			this.hasPreviousPage = false;
		}

	}

	private void isFirstPage() {
		if (pageNum == 1) {
			this.isFirstPage = true;
		} else {
			this.isFirstPage = false;
		}

	}

	private void isLastPage() {
		getTotalPages();
		if (pageNum == totalPages) {
			this.isLastPage = true;
		} else {
			this.isLastPage = false;
		}

	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public boolean isHasPreviousPage() {
		return hasPreviousPage;
	}

	public void setHasPreviousPage(boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void setFirstPage(boolean isFirstPage) {
		this.isFirstPage = isFirstPage;
	}

	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public boolean getIsEmpty() {
		return this.isEmpty;
	}

	public boolean getIsFirstPage() {
		return this.isFirstPage;
	}

	public boolean getIsLastPage() {
		return this.isLastPage;
	}

	public Map<String, String> getSortMap() {
		return sortMap;
	}

	public void setSortMap(Map<String, String> sortMap) {
		this.sortMap = sortMap;
	}

}
