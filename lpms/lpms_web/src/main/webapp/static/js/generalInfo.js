var GeneralInfo = {
		init:function(){
			var swapMemoryUsedPer = $("#swap_memory_usedPercent").val();
			var localDiskUsedPer = $("#local_disk_usedPercent").val();
			var realMemoryUsedPer = $("#real_memory_usedPercent").val();
			
			var swapMemoryFreePer = (100 - parseFloat(swapMemoryUsedPer,2)) + "%";
			var localDiskFreePer = (100 - parseFloat(localDiskUsedPer,2)) + "%";
			var realMemoryFreePer = (100 - parseFloat(realMemoryUsedPer,2)) + "%";
			
			$("#local_disk_used_bar").css("width",localDiskUsedPer).html(localDiskUsedPer +" Used");
			$("#local_disk_free_bar").css("width",localDiskFreePer + "%").html(localDiskFreePer + " Free");
			
			$("#real_memory_used_bar").css("width",realMemoryUsedPer).html(realMemoryUsedPer + " Used");
			$("#real_memory_free_bar").css("width",realMemoryFreePer).html(realMemoryFreePer + " Free");
			
			$("#swap_memory_used_bar").css("width",swapMemoryUsedPer).html(swapMemoryUsedPer + " Used");
			$("#swap_memory_free_bar").css("width",swapMemoryFreePer).html(swapMemoryFreePer + " Free");
			
 		}

};