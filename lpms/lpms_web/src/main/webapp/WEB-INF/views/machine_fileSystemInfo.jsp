<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${fileSystemInfo_list}" var="fileSystem">
		<div class="well">
    	<div class="fileSystem_container">
    		<div class="container1" >${fileSystem.dirName}</div>
    		<div class="container2" >
    			<dl class="dl-horizontal">
    				<dt>File System</dt>
    				<dd>${fileSystem.sysTypeName }</dd>
    			</dl>
    			<dl class="dl-horizontal">
    				<dt>FileSystem Type</dt>
    				<dd>${fileSystem.typeName }</dd>
    			</dl>
    		
    		</div>
    		<div class="container3" >
    			<dl class="dl-horizontal">
    				<dt>Disk Reads</dt>
    				<dd>${fileSystem.diskReads }</dd>
    			</dl>
    			<dl class="dl-horizontal">
    				<dt>Disk Write</dt>
    				<dd>${fileSystem.diskWrites }</dd>
    			</dl>
    		</div>
    		<div class="container4" >
			  		 <div class="progress  progress-striped active">
						  <div class="bar bar-danger"  id="local_disk_used_bar" style="width: ${fileSystem.usedPercent};">${fileSystem.usedPercent} Used</div>
						  <div class="bar bar-info" id="local_disk_free_bar" style="width: ${fileSystem.freePercent};">${fileSystem.freePercent} Free</div>
					</div>
			  		  	<div class="alert alert-info"><strong>Total: </strong>${fileSystem.total } &nbsp;&nbsp;<strong>Used: </strong>${fileSystem.used}</div>
    		</div>
    		
    		
    	</div>
	</div> 


</c:forEach>
   