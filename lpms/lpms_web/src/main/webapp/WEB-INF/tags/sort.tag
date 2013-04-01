<%@tag pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<c:choose>
		<c:when test="${page.sort.order == 'asc' }">
		<button onclick="changeOrder('desc')" class="btn spa pull-right sort-button">
			<i class="icon-arrow-up pull-right"></i>
		</button>
		</c:when>
		<c:otherwise>
		<button onclick="changeOrder('asc')" class="btn spa pull-right sort-button">
			<i class="icon-arrow-down pull-right"></i>
		</button>
		</c:otherwise>
	</c:choose>
<div class="btn-group pull-right">
	<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
		排序:${page.sortMap[page.sort.orderBy]} <span class="caret"></span>
	</a>
	<ul class="dropdown-menu">
		<c:forEach items="${page.sortMap}" var="sort">
			<c:if test="${sort.key != page.sort.order }">
				<li onclick="sortRequest('${sort.key}')"><a href="#">${sort.value}</a></li>
			</c:if>
		</c:forEach>

	</ul>

</div>
