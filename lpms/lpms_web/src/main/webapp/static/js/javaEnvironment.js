var JavaEnvironment = {
	init:function(){
		  var usedPercent = $("#jvm_memory_percent").val();
		  var freePercent = 100 -  parseFloat(usedPercent,2) + "%";
		  
		  
		  $("#jvm_memory_used_bar").css("width",usedPercent).html(usedPercent + " Used");
		  $("#jvm_memory_free_bar").css("width",freePercent).html(freePercent + " Free");
		  
		  	
		  
	}	
};