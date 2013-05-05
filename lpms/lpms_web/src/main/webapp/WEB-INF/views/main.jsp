<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>LPMS</title>
<link rel="stylesheet" href="static/css/reset.css">
<link rel="stylesheet" href="static/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="static/css/lpms_theme_main.css">
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top">

		<div class="navbar-inner">
			<div class="container">
				<a class="brand" href="#">LPMS</a>
				<ul class="nav" id="main_nav">
					<c:choose>
						<c:when test="${user.root == 1 }">
							<!-- root menu -->
							<li class="active"><a href="#"
								onclick="jump(1,'main_nav','admin/manager_user');">用户管理</a></li>
							<li><a href="#"
								onclick="jump(2,'main_nav','admin/add_user')">添加用户</a></li>

							<li><a href="#"
								onclick="jump(3,'main_nav','admin/getSelf');">个人资料</a></li>
						</c:when>
						<c:otherwise>
							<!-- user menu -->
							<li class="active"><a href="#"
								onclick="jump(1,'main_nav','user/manager_machine');">服务器列表</a></li>
							<li><a href="#"
								onclick="jump(2,'main_nav','user/add_machine')">添加服务器</a></li>

							<li><a href="#" onclick="jump(3,'main_nav','admin/getSelf');">个人资料</a></li>
						</c:otherwise>
					</c:choose>
				</ul>

				<div class="span2 offset5">
					<a class="brand">欢迎 ${user.name }</a>
				</div>
			</div>
		</div>
	</div>
	<div class="container " id="main_container"></div>
	<div>
		<script type="text/javascript" src="static/js/jquery-1.9.0.min.js"></script>
		<script type="text/javascript"
			src="static/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="static/js/lpms.js"></script>
		<script type="text/javascript" src="static/js/page.js"></script>
		<c:if test="${user.root == 0 }">
		<script type="text/javascript" src="static/js/generalInfo.js"></script>
		
		</c:if>
		<script type="text/javascript">
			<c:choose>
			<c:when test="${user.root == 1 }">
			jump(1, 'main_nav', 'admin/manager_user');
			</c:when>
			<c:otherwise>
			jump(1, 'main_nav', 'user/manager_machine');
			</c:otherwise>
			</c:choose>
		</script>
	</div>
</body>
</html>