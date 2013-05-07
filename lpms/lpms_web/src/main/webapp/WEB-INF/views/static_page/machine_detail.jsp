<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="machineId" value="${param.id}">
<div class="row well">
	<div class="span10 well" style="background:white;width:820px;font-size: 14px;">
		<strong>${param.machineName } (${param.machineIp })</strong>		
	</div>
	<div class="span2 well" style="background: white;height:500px;">
		<ul class="nav nav-pills nav-stacked" id="machine_detail_menu">
			<li class="nav-header"><i class="icon-home"></i> 信息列表</li>
			<li class="active"><a href="#" onclick="getMachineDetail('generalInfo');"><i class="icon-user icon-white"></i> 基本信息</a></li>
			<li><a href="#" onclick="getMachineDetail('javaEnvironment');"><i class="icon-leaf"></i> Java 环境信息</a></li>
			<li><a href="#" onclick="getMachineDetail('procInfo');"><i class="icon-tasks"></i> 进程信息</a></li>
			<li><a href="#" onclick="getMachineDetail('netInfo');"><i class="icon-globe"></i> 网络信息</a></li>
			<li><a href="#" onclick="getMachineDetail('fileSystemInfo');"><i class="icon-hdd"></i> 硬盘信息</a></li>
		</ul>
	</div>
	<div class="span8 well" style="background: white; overflow: auto;min-height:500px;" id="machine_detail_container">
			
	</div>
</div>
<script type="text/javascript">
	$(function(){
		getMachineDetail("generalInfo");
	});

</script>