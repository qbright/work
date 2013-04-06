<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<input type="hidden" id="machineId" value="${param.machineId}">
<div class="row well">
	<div class="span2 well" style="background: white;height:500px">
		<ul class="nav nav-pills nav-stacked">
			<li class="nav-header">基本功能</li>
			<li class="active"><a href="#">基本信息</a></li>
			<li><a href="#">Library</a></li>
		</ul>

	</div>
	<div class="span8 well" style="background: white; overflow: auto;min-height:500px;"></div>

</div>
<script type="text/javascript" src=""></script>