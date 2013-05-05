<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
   <dl class="dl-horizontal">
 		 <dt>System Hostname</dt>
  		 <dd>${system_hostname} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>Operating System</dt>
  		 <dd>${operating_system} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>System Uptime</dt>
  		 <dd>${system_uptime} </dd>
	</dl>
   <dl class="dl-horizontal">
 	  	 <dt>Running Processes</dt>
  		 <dd>${running_processes} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>Server Time</dt>
  		 <dd>${time_on_system} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>Cpu Usage</dt>
  		 <dd>${cpu_usage} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>Processor Info</dt>
  		 <dd>${processor_info} </dd>
	</dl>
	
	<dl class="dl-horizontal">
 		 <dt>localDisk</dt>
  		 <dd>
  		 <div class="progress  progress-striped active">
			  <div class="bar bar-danger"  id="local_disk_used_bar" style="width: 0;"></div>
			  <div class="bar bar-info" id="local_disk_free_bar" style="width: 0;"></div>
		</div>
  		  </dd>
  		  <dd>
  		  	<div class="alert alert-info"><strong>Total: </strong>${local_disk.total } &nbsp;&nbsp;<strong>Used: </strong>${local_disk.used }</div>
  		  </dd>
	</dl>
	
	<dl class="dl-horizontal">
 		 <dt>Real Memory</dt>
  		 <dd>
  		 <div class="progress  progress-striped active">
			  <div class="bar bar-danger"  id="real_memory_used_bar" style="width: 0;"></div>
			  <div class="bar bar-info" id="real_memory_free_bar" style="width: 0;"></div>
		</div>
  		  </dd>
  		  <dd>
  		  	<div class="alert alert-info"><strong>Total: </strong>${real_memory.total } &nbsp;&nbsp;<strong>Used: </strong>${real_memory.used }</div>
  		  </dd>
	</dl>
	
	<dl class="dl-horizontal">
 		 <dt>Swap Memory</dt>
  		 <dd>
  		 <div class="progress  progress-striped active">
			  <div class="bar bar-danger"  id="swap_memory_used_bar" style="width: 0;"></div>
			  <div class="bar bar-info" id="swap_memory_free_bar" style="width: 0;"></div>
		</div>
  		  </dd>
  		  <dd>
  		  	<div class="alert alert-info"><strong>Total: </strong>${swap_memory.total } &nbsp;&nbsp;<strong>Used: </strong>${swap_memory.used }</div>
  		  </dd>
	</dl>
	
	
	
  <input type="hidden" id="swap_memory_total" value="${swap_memory.total }">
  <input type="hidden" id="swap_memory_used" value="${swap_memory.used }">
  <input type="hidden" id="local_disk_total" value="${local_disk.total }">
  <input type="hidden" id="local_disk_used" value="${local_disk.used }">
  <input type="hidden" id="real_memory_total" value="${real_memory.total }">
  <input type="hidden" id="real_memory_used" value="${real_memory.used}">
     
  <script type="text/javascript">
  	$(function(){
  		GeneralInfo.init();
  	});
  
  </script> 
      