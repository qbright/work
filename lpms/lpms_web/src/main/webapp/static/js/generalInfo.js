var GeneralInfo = {
		init:function(){
			var swapMemoryTotal = $("#swap_memory_total").val();
			var swapMemoryUsed = $("#swap_memory_used").val();
			var localDiskTotal = $("#local_disk_total").val();
			var localDiskUsed = $("#local_disk_used").val();
			var realMemoryTotal = $("#real_memory_total").val();
			var realMemoryUsed = $("#real_memory_used").val();
			
			var _swapMemoryTotal = parseFloat(swapMemoryTotal);
			var _swapMemoryUsed = parseFloat(swapMemoryUsed);
			var _localDiskTotal = parseFloat(localDiskTotal);
			var _localDiskUsed = parseFloat(localDiskUsed);
			var _realMemoryTotal = parseFloat(realMemoryTotal);
			var _realMemoryUsed = parseFloat(realMemoryUsed);
			
			var localDiskUsedPre = parseInt((_localDiskUsed/_localDiskTotal) * 100);
			var localDiskFreePre = 100 - localDiskUsedPre;
			
			var realMemoryUsedPre = parseInt((_realMemoryUsed/_realMemoryTotal) * 100);
			var realMemoryFreePre = 100 - realMemoryUsedPre;
			
			var swapMemoryUsedPre = parseInt((_swapMemoryUsed/_swapMemoryTotal) * 100);
			var swapMemoryFreePre = 100 - swapMemoryUsedPre;
			
			
			$("#local_disk_used_bar").css("width",localDiskUsedPre + "%").html(localDiskUsedPre + "% Used");
			$("#local_disk_free_bar").css("width",localDiskFreePre + "%").html(localDiskFreePre + "% Free");
			
			$("#real_memory_used_bar").css("width",realMemoryUsedPre + "%").html(realMemoryUsedPre + "% Used");
			$("#real_memory_free_bar").css("width",realMemoryFreePre + "%").html(realMemoryFreePre + "% Free");
			
			$("#swap_memory_used_bar").css("width",swapMemoryUsedPre + "%").html(swapMemoryUsedPre + "% Used");
			$("#swap_memory_free_bar").css("width",swapMemoryFreePre + "%").html(swapMemoryFreePre + "% Free");
			
 		}

};