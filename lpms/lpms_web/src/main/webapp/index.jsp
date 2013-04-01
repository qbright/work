<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>LPMS</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="static/css/reset.css">
<link rel="stylesheet" href="static/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="static/css/lpms_theme.css">
</head>
<body>
	<header class="jumbotron masthead" id="overview">
		<div class="container">
			<div class="span12">
				<h1>LPMS</h1>
			</div>
			<div class="span10 offset2">
				<h3>Linux Performance Monitoring System</h3>
			</div>
			<div class="span8 offset4">

				<form class="form-signin" id="login_form" action="login"
					method="post">
					<h2 class="form-signin-heading">Login in</h2>
					<input type="hidden" name="root" id="root_type"> <input
						type="text" class="input-block-level" name="name"
						placeholder="User Name"> <input type="password"
						class="input-block-level" name="password" placeholder="Password">

					<div class="row">
						<div class="span3 btn-group" id="type_radio"
							data-toggle="buttons-radio">
							<button type="button" id="0" class="btn btn-primary active">User</button>
							<button type="button" id="1" class="btn btn-primary">Admin</button>
						</div>
						<c:if test="${error == 1}">
							<div class="span3 alert" style="margin-bottom: 0">
								<button type="button" class="close" data-dismiss="alert">&times</button>
								<strong>登录失败!</strong>
							</div>
						</c:if>
						
					</div>
					<div class="span12" style="padding: 10px 0 0 0">
						<button id="login" type="button" class="btn btn-large btn-success"
							data-loading-text="Logining....">Login In</button>
					</div>
				</form>
			</div>
		</div>
	</header>

	<footer class="footer">
		<div class="container">
			<p>Designed and built with qbright 郑启光 广东工业大学09级网络一班</p>
		</div>
	</footer>
	<div>
		<script type="text/javascript" src="static/js/jquery-1.9.0.min.js"></script>
		<script type="text/javascript"
			src="static/bootstrap/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="static/js/lpms_login.js"></script>
	</div>
</body>
</html>
