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
						<a class="brand" href="#">Title</a> <input type="hidden"
							id="page_url" value="user/manager_machine"> <input
							type="hidden" id="page_container" value="main_container">
						<tags:sort />
						<button class="btn btn-danger spa pull-right"
							onclick="jump(2,'main_nav','user/add_machine')">
							<i class="icon-plus icon-white"></i>
						</button>
					</div>
				</div>
			</div>
			<div class="span11" id="table_container">
				<table class="table table-striped table-bordered table-condensed table-hover">
					<thead>
						<tr>
							<th>服务器名称</th>
							<th>ip地址</th>
							<th>操作系统版本</th>
							<th>最后登录时间</th>
							<th style="width: 60px;"></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${page.content}" var="machine">
							<tr >
								<td  onclick="getDetail(${machine.id})" style="cursor: pointer;">${machine.machineName}</td>
								<td>${machine.connection_ip}</td>
								<td>${machine.system}</td>
								<td>${machine.last_login}</td>
								<td>
									<div class="btn-group">
										<button class="btn btn-primary btn-small"
											onclick="updateMachine(${machine.id})">
											
											<i class="icon-wrench icon-white"></i>
										</button>
										<button class="btn  btn-danger btn-small"
											onclick="deleteMachine(${machine.id})">
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