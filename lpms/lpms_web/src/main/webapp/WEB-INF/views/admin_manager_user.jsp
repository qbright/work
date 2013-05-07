<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="row well">
<c:set value="${page.content}" var="content"></c:set>
<c:choose>
	<c:when test="${fn:length(content) ==0}">
<span class="span3 offset4 label label-info">
	<p>暂无数据！</p>
</span>
	</c:when>
	<c:otherwise>
			<div class="span11">
				<div class="navbar">
					<div class="navbar-inner">
						<a class="brand" href="#">用户列表</a> <input type="hidden"
							id="page_url" value="admin/manager_user"> <input
							type="hidden" id="page_container" value="main_container">
						<tags:sort />
						<button class="btn btn-danger spa pull-right"
							onclick="jump(2,'main_nav','admin/add_user')">
							<i class="icon-plus icon-white"></i>
						</button>
					</div>
				</div>
			</div>
			<div class="span11" id="table_container">
				<table class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>姓名</th>
							<th>电子邮箱</th>
							<th>管理机器数量</th>
							<th>角色</th>
							<th style="width: 60px;"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${page.content}" var="user">
							<tr>
								<td>${user.name}</td>
								<td>${user.email}</td>
								<td>${user.manager_num}</td>
								<td>
								<c:choose>
									<c:when test="${user.root == 0 }">
										<span class="label label-info">普通用户</span>
									</c:when>
									<c:otherwise>
										<span class="label label-success">管理员</span>
									</c:otherwise>
								</c:choose>
								</td>
								<td>
									<div class="btn-group">
										<button class="btn btn-primary btn-small"
											onclick="updateUser(${user.id})">
											<i class="icon-wrench icon-white"></i>
										</button>
										<button class="btn  btn-danger btn-small"
											onclick="deleteUser(${user.id})">
											<i class="icon-remove icon-white"></i>
										</button>
									</div>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<tags:pagination />
	</c:otherwise>
</c:choose>
</div>