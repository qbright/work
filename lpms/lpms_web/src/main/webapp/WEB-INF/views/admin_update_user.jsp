<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="updateUser" class="modal hide fade" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h3 id="myModalLabel">修改信息</h3>
	</div>
	<div class="modal-body">
		<form action="" class="span6 pull-left" id="change_form">
			<fieldset>
				<input type="hidden" name="id" value="${user.id }">
				<input type="hidden" name="root" id="root_type"><label>姓名</label>
				<input id="username" type="text" name="name" class="input-xlarge"
					placeholder="请输入姓名" onchange="checkUnique();" data-oldvalue="${user.name }" value="${user.name }" required> <span
					class="text-error disable" id="input-text_1">用户名已被注册</span> <label>电子邮箱</label>
				<input type="email" name="email" class="input-xlarge" value="${user.email }"
					placeholder="请输入电子邮箱" required> <label>角色</label>
				<div class="span3 btn-group" id="type_radio"
					data-toggle="buttons-radio">
					<button type="button" id="0" 
						<c:choose>
							<c:when test="${user.root == 0 }">
								class="btn btn-warning active"
							</c:when>
							<c:otherwise>
								class="btn btn-warning"
							</c:otherwise>
						</c:choose>	
					
					>User</button>
					<button type="button" id="1" 
								<c:choose>
							<c:when test="${user.root == 1 }">
								class="btn btn-warning active"
							</c:when>
							<c:otherwise>
								class="btn btn-warning"
							</c:otherwise>
						</c:choose>	
					>Admin</button>
				</div>
			</fieldset>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
		<button class="btn btn-primary" onclick="saveChange();">保存修改</button>
	</div>
</div>