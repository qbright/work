<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="row well">
	<form action="" class="span7 pull-left" id="add_form">
		<fieldset>
			<legend>添加服务器</legend>	
			<div class=""></div>
			<label>服务器名称</label>
			  <input id="machineName" type="text" name="machineName" class="input-xlarge" placeholder="请输入名称" onchange="checkMachineUnique();" required>
			  <span class="text-error disable" id="input-text_1">名称已被占用</span>
            <label>密码</label>
            <input type="password" id="password" name="password" class="input-xlarge" placeholder="password" onchange="checkSame()" required>
            <span class="text-error disable" id="input-text_2">两次输入的密码不一致</span>
            <label>确认密码</label>
            <input type="password" id="rePassword" class="input-xlarge" placeholder="password" onchange="checkSame()"required>
            <label>服务器ip地址</label>
			 <div class="input-append input-prepend">
				  <input class="span3" id="machine_ip" name="connection_ip" onchange="checkIp();" type="text"placeholder="请输入ip地址">
				  <span class="add-on">：</span>
				  <input class="span1" type="text" onchange="checkPorts();" name="connection_port" id="port" placeholder="端口">
			</div>
			<label>服务器系统类型</label>
			  <input type="text" name="system" class="input-xlarge" placeholder="System" /> 
		</fieldset>
	</form>
	<div class="span3">
		<div class="help-block" style="height:330px">
			<div class="help-block" style="height:100px;"></div>
          </div>
		<div class="span3">
			<button class="btn btn-large btn-block btn-primary" type="button" onclick="checkForm2()">添加服务器</button>
		</div>
	</div>
	
</div>