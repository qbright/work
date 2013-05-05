<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="machineId" value="${param.id}">
<div class="row well">
	<div class="span2 well" style="background: white;height:500px">
		<ul class="nav nav-pills nav-stacked" id="machine_detail_menu">
			<li class="nav-header"><i class="icon-home"></i> 信息列表</li>
			<li class="active"><a href="#"><i class="icon-user icon-white"></i> 基本信息</a></li>
			<li><a href="#"><i class="icon-user"></i> Library</a></li>
		</ul>

	</div>
	<div class="span8 well" style="background: white; overflow: auto;min-height:500px;" id="machine_detail_container"></div>

</div>
<script type="text/javascript">
	$(function(){
		getMachineDetail("generalInfo");
	});

</script>