<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="well">
	<fieldset>
		<legend>个人资料</legend>
		<legend class="offset1 span6">姓名:${user.name }</legend>
		<legend class="offset1 span6">E-Mail:${user.email }</legend>
		<legend class="offset1 span6">管理机器数量：${user.manager_num }</legend>
	</fieldset>
	<form  class="form-signin  offset3 ">
		<fieldset>
			<legend>修改密码</legend>
			<label>旧密码</label> <input type="password" name="oldPassword" id="oldPassword"
				class="input-large" value="" onchange="checkOldPassword()"/> 
				 <span class="text-error disable" id="input-text_1">密码错误！</span>
			<label>新密码</label>
			<input id="password" type="password" name="password" class="input-large" value="" onchange="checkSame()"/>
			 <span class="text-error disable" id="input-text_2">密码输入不一致！！</span>
			<label>再次输入新密码</label> <input type="password" id="rePassword" onchange="checkSame()"
				class="input-large" value=""  />
			<label>
			<button type="button" class="btn btn-primary" onclick="changePassword()">保存修改</button>
			</label>
			
		</fieldset>
	</form>
</div>