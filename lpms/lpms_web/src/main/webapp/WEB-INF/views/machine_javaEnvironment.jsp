<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
   <dl class="dl-horizontal">
 		 <dt>JDK Version</dt>
  		 <dd>${jdk_version} </dd>
	</dl>
	 <dl class="dl-horizontal">
 		 <dt>Java Vendor</dt>
  		 <dd>${java_vendor} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>JVM Name</dt>
  		 <dd>${java_vm_name} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>JVM Version</dt>
  		 <dd>${java_vm_version} </dd>
	</dl>
   <dl class="dl-horizontal">
 	  	 <dt>Java Home</dt>
  		 <dd>${java_home} </dd>
	</dl>
	 <dl class="dl-horizontal">
 	  	 <dt>Availbale Processor</dt>
  		 <dd>${available_processor} </dd>
	</dl>
	<dl class="dl-horizontal">
 		 <dt>JVM Memory Usage</dt>
  		 <dd>
  		 <div class="progress  progress-striped active">
			  <div class="bar bar-danger"  id="jvm_memory_used_bar" style="width: 0;"></div>
			  <div class="bar bar-info" id="jvm_memory_free_bar" style="width: 0;"></div>
		</div>
  		  </dd>
  		  <dd>
  		  	<div class="alert alert-info"><strong>Total: </strong>${total_memory} &nbsp;&nbsp;<strong>Free: </strong>${free_memory}</div>
  		  </dd>
	</dl>
  <input type="hidden" id="jvm_memory_percent" value="${memory_used_percent}">
	
	 <script type="text/javascript">
  	$(function(){
  		JavaEnvironment.init();
  	});
  
  </script> 
	