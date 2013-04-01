<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="updateMachine" class="modal hide fade" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">×</button>
		<h3 id="myModalLabel">修改信息</h3>
	</div>
	<div class="modal-body">
		<form action="" class="span6 pull-left" id="machineChange_form">
			<fieldset>
				<input type="hidden" name="id" value="${serverMachine.id }">
				<label>服务器名称</label>
				<input id="machineName" type="text" name="machineName" class="input-xlarge"
					placeholder="请输入姓名" onchange="checkMachineUnique();" data-oldvalue="${serverMachine.machineName }" value="${serverMachine.machineName }" required> <span
					class="text-error disable" id="input-text_1">名称已被占用</span> 
				<label>服务器ip地址</label>
			 <div class="input-append input-prepend">
				  <input class="span3" id="appendedInput" name="connection_ip" value="${serverMachine.connection_ip }" type="text"placeholder="请输入ip地址">
				  <span class="add-on">：</span>
				  <input class="span1" type="text" onchange="checkPorts();" name="connection_port" id="port" value="${serverMachine.connection_port }" placeholder="端口">
			</div>
			<label>服务器系统类型</label>
			  <input type="text" name="system" class="input-xlarge" value="${serverMachine.system }" placeholder="System" /> 
				
			</fieldset>
		</form>
	</div>
	<div class="modal-footer">
		<button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
		<button class="btn btn-primary" onclick="saveMachineChange()">保存修改</button>
	</div>
</div>