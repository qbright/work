var ProcInfo = {
	init:function(){
		this.loadMore();
	},
	
	loadMore:function(){
		var procInfoLength = parseInt($("#procInfo_length").val());
		var load = parseInt($("#load").val());
		var size = parseInt($("#size").val());
		var start = load * size;
		var end = start + size;
		$("#proc_table tr:gt(" + start +"):lt(" + size + ")" ).show();
		$("#load").val(load + 1);
		if(end >= procInfoLength){
			$("#load_more").addClass("disabled");
			$("#load_more").attr("disabled","disabled");
		}
		
	}
};