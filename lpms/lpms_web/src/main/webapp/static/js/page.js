function pageJump(pageNum){
	/*var pageNum = Number.valueOf($("#page_pageNum").val()) + 1;*/
	if($("#page_pageNum").val() == pageNum){
		return false;
	}
	$("#page_pageNum").val(pageNum);
	pageRequest();
}


function pageNextOrPrev(type){
	var pageNum;
	if(type == "next"){
		pageNum = Number($("#page_pageNum").val()) + 1;
	}else {
		pageNum = Number($("#page_pageNum").val()) - 1;
	}
	
	pageJump(pageNum);
}

function pageRequest(){
	$.ajax({
		url:$("#page_url").val(),
		type:"POST",
		data:$("#pageRequest_form").serialize(),
		cache:false,
		dataType:"html",
		success:function(response){
			$("#" + $("#page_container").val()).html(response);
			
		}
	});
}

function sortRequest(orderBy){
	$("#page_pageNum").val(1);
	$("#page_sort_order").val("desc");
	$("#page_sort_orderBy").val(orderBy);
	pageRequest();
}

function changeOrder(order){
	$("#page_pageNum").val(1);
	$("#page_sort_order").val(order);
	pageRequest();
}
