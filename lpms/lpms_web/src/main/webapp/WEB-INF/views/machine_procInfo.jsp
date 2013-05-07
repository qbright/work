<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 

<table class="table table-bordered table-hover table-condensed">
	<thead>
		<tr>
			<th>Pid</th>
			<th>ProcExe</th>
			<th>CpuPercent</th>
			<th>CpuTime</th>
			<th>User</th>
			<th>MemVirt</th>
			<th>MemResident</th>
			<th>MemShare</th>
			<th>ProcState</th>
			<th>ProcNice</th>
			<th>ProcPriority</th>
			
			
		</tr>
	</thead>
	<tbody id="proc_table">
		<c:forEach items="${procInfo_list }" varStatus="status" var="procInfo">
			<tr style="display: none">
			
				<td>${procInfo.pid }</td>
				<td>${procInfo.procExe }</td>
				<td>${procInfo.cpuPercent }</td>
				<td>${procInfo.cpuTime }</td>
				<td>${procInfo.user }</td>
				<td>${procInfo.memVirt }</td>
				<td>${procInfo.memResident }</td>
				<td>${procInfo.memShare }</td>
				<td>${procInfo.procState }</td>
				<td>${procInfo.procNice }</td>
				<td>${procInfo.procPriority }</td>
			</tr>

		</c:forEach>
	</tbody>
</table>

<button class="btn btn-large btn-block btn-primary" onclick="ProcInfo.loadMore();" id="load_more" type="button">加载更多</button>
<input type="hidden" id="load" value="0">
<input type="hidden" id="size" value="15">
<input type="hidden" id="procInfo_length" value="${fn:length(procInfo_list)} ">
<script type="text/javascript">
	$(function(){
		ProcInfo.init();
	});
</script>


