<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="row well">
	<form action="" class="span7 pull-left" id="add_form">
		<fieldset>
			<input type="hidden" name="root" id="root_type">
			<legend>添加用户</legend>	
			<div class=""></div>
			<label>姓名</label>
			  <input id="username" type="text" name="name" class="input-xlarge" placeholder="请输入姓名" onchange="checkUnique();" required>
			  <span class="text-error disable" id="input-text_1">用户名已被注册</span>
			<label>密码</label>
			  <input type="password" id="password" name="password" class="input-xlarge" placeholder="password" onchange="checkSame()" required> 
			  <span class="text-error disable" id="input-text_2">两次输入的密码不一致</span>
			<label>确认密码</label>
			  <input type="password" id="rePassword" class="input-xlarge" placeholder="password" onchange="checkSame()"required> 
			  <label>电子邮箱</label>
			  <input type="email" name="email" class="input-xlarge" placeholder="请输入电子邮箱"required>
			   <label>角色</label>
			  <div class="span3 btn-group" id="type_radio"
					data-toggle="buttons-radio">
					<button type="button" id="0" class="btn btn-warning active">User</button>
					<button type="button" id="1" class="btn btn-warning">Admin</button>
			 </div>
		</fieldset>
	</form>
	<div class="span3">
		<div class="help-block" style="height:330px">
			<div class="help-block" style="height:100px;"></div>
		
		
          </div>
		<div class="span3">
			<button class="btn btn-large btn-block btn-primary" type="button" onclick="checkForm()">添加用户</button>
			
		</div>
	</div>
	
</div>