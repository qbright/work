/**
 * @author qbright
 *
 * @date 2013-1-23
 */
package me.qbright.lpms.web.common;

public class Sort {
	public static String ORDER_DESC = "desc";
	public static String ORDER_ASC = "asc";
	private String order;
	private String orderBy;

	/**
	 * 
	 */
	public Sort() {
		// TODO Auto-generated constructor stub
	}

	public Sort(String orderBy, String order) {
		this.orderBy = orderBy;
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}


}
