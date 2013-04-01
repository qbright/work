<%@tag pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="btn-group pull-right">
	<button	<c:choose>
				<c:when test="${page.hasPreviousPage == false}">
					class="btn disabled"	disabled="disabled"
				</c:when>
				<c:otherwise>
					class="btn"
				</c:otherwise>
			</c:choose>	
		onclick="pageNextOrPrev('prev')">前一页</button>
	<div class="btn-group dropup">
	<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
			第 ${page.pageNum } 页
		<span class="caret"></span>	
	</a>
	<ul class="dropdown-menu page-dropdown">
	
		<c:forEach begin="1" end="${page.totalPages}" varStatus="status">
			<li onclick="pageJump(${status.index})"
				<c:if test="${page.pageNum == status.index }">
					class="disabled" disabled="disabled"
				</c:if>
			><a href="#">第 ${status.index} 页</a></li>
		</c:forEach>	
	</ul>
</div>
	<button 
		<c:choose>
				<c:when test="${page.hasNextPage == false}">
					class="btn disabled"	disabled="disabled"
				</c:when>
				<c:otherwise>
					class="btn" 
					
				</c:otherwise>
			</c:choose>	
	 onclick="pageNextOrPrev('next')">后一页</button>
</div>


<form id="pageRequest_form" method="post">
	<input name="pageNum" value="${page.pageNum}" id="page_pageNum" type="hidden">
	<input name="pageSize" value="${page.pageSize}" id="page_pageSize" type="hidden">
	<input name="sort.order" value="${page.sort.order}" id="page_sort_order" type="hidden">
	<input name="sort.orderBy" value="${page.sort.orderBy}" id="page_sort_orderBy" type="hidden"> 
</form>