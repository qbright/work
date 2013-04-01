/**
 * @author qbright
 *
 * @date 2013-1-23
 */
package me.qbright.lpms.web.common;

public class PageRequest {
	private int pageNum = 1;
	private int pageSize = 10;

	private int startCount;
	private Sort sort = new Sort();

	public static PageRequest build(PageRequest pageRequest) {
		if (pageRequest == null) {
			PageRequest pr = new PageRequest();
			pr.setSort(setDefaultSort());
			return pr;
		} else {
			if (pageRequest.getSort().getOrder() == null
					|| pageRequest.getSort().getOrderBy() == null) {
				pageRequest.setSort(setDefaultSort());
			}
			return pageRequest;
		}
	}

	/**
	 * 
	 */
	public PageRequest() {
		// TODO Auto-generated constructor stub
	}

	public PageRequest(int pageSize) {
		this.pageSize = pageSize;
	}

	private static Sort setDefaultSort() {
		return new Sort("id", Sort.ORDER_DESC);
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		if (pageNum < 1) {
			this.pageNum = 1;
		} else {
			this.pageNum = pageNum;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

	public int getStartCount() {
		this.startCount = (pageNum - 1) * pageSize;
		return startCount;
	}

}
