function jump(index, parentId, url) {
	getContent(url);
	resetMenu(index, parentId);
	return false;
}

function getContent(url) {
	$.ajax({
		url : url,
		dataType : "html",
		cache : false,
		type : "GET",
		success : function(response) {
			$("#main_container").html(response);
		}
	});
}

function resetMenu(index, parentId) {
	var i = 1;
	$("#" + parentId + " > li").each(function() {
		
		if (index == i) {
			$(this).addClass("active");
			$(this).find("i").addClass("icon-white");
		} else {
			$(this).removeClass("active");
			$(this).find("i").removeClass("icon-white");
		}
		i++;

	});
}

function checkUnique() {

	var $name = $("#username");
	var $oldname = $("#username");
	if ($name.val().trim() == "")
		return false;
	if ($oldname.data("oldvalue") != undefined) {
		if ($oldname.data("oldvalue") == $name.val()) {
			return false;
		}
	}
	$.ajax({
		url : "admin/checkUnique",
		data : "name=" + $name.val(),
		type : "POST",
		dataType : "html",
		success : function(response) {
			if (response == "false") {
				$("#input-text_1").removeClass("disable");
				$name.val("");
				$("#input-text_1").addClass("disable");
			}
		}
	});
}

function checkSame() {
	var $password = $("#password");
	var $rePassword = $("#rePassword");
	if ($rePassword.val().trim() == "")
		return;
	if ($password.val() != $rePassword.val()) {
		$("#input-text_2").removeClass("disable");
		$rePassword.val("");
	} else {
		$("#input-text_2").addClass("disable");
	}
}

function checkForm() {
	$("#root_type").val($("#type_radio > .active").attr("id"));
	$("fieldset > input").each(function() {
		if ($(this).val() == "") {
			alert("输入有误，请检查！");
			return false;
		}
	});
	submitForm();
}

function submitForm() {
	$.ajax({
		url : "admin/add",
		data : $("#add_form").serialize(),
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			jump(1, 'main_nav', 'admin/manager_user');
		}
	});
}

function updateUser(userId) {
	$.ajax({
		url : "admin/prepareUpdate",
		data : "id=" + userId,
		cache : false,
		type : "GET",
		dataType : "html",
		success : function(response) {
			$("body").append(response);
			$("#updateUser").modal("show");
			$("#updateUser").on("hidden", function() {
				$("#updateUser").remove();
			});
		}
	});
}

function saveChange() {
	$("#root_type").val($("#type_radio > .active").attr("id"));
	$.ajax({
		url : "admin/saveChange",
		data : $("#change_form").serialize(),
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			$("#updateUser").modal("hide");
			jump(1, 'main_nav', 'admin/manager_user');
		}
	});
}

function deleteUser(userId) {
	if (confirm("确认删除？？")) {
		$.ajax({
			url : "admin/deleteUser",
			data : "id=" + userId,
			cache : false,
			type : "GET",
			dataType : "html",
			success : function(response) {
				jump(1, 'main_nav', 'admin/manager_user');
			}
		});

	}
}

function checkPorts() {
	var port = $("#port").val();
	var patrn = /^[0-9]{1,6}$/;
	if (!patrn.exec(port)) {
		alert("请输入数字");
		$("#port").val("");
		return false;
	}
	if (port < 0 || port > 65535) {
		alert("请输入正确的端口数值 （0 < p < 65535）");
		$("#port").val("");
		return false;
	}
}

function checkIp(){
	var ip = $("#machine_ip");
	//var patrn = /((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))/;
	var patrn = /\b(([01]?\d?\d|2[0-4]\d|25[0-5])\.){3}([01]?\d?\d|2[0-4]\d|25[0-5])\b/;
	if(!patrn.exec(ip.val())){
		alert("请输入正确格式的ip地址");
		ip.val("");
		return false;
	}
}

function checkForm2() {
	$("fieldset > input").each(function() {
		if ($(this).val() == "") {
			alert("输入有误，请检查！");
			return false;
		}
	});
	submitForm2();
}

function submitForm2() {
	$.ajax({
		url : "user/add",
		data : $("#add_form").serialize(),
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			jump(1, 'main_nav', 'user/manager_machine');
		}
	});
}

function checkMachineUnique() {
	var $name = $("#machineName");
	var $oldname = $("#machineName");
	if ($name.val().trim() == "")
		return false;
	if ($oldname.data("oldvalue") != undefined) {
		if ($oldname.data("oldvalue") == $name.val()) {
			return false;
		}
	}
	$.ajax({
		url : "user/checkUnique",
		data : "machineName=" + $name.val(),
		type : "POST",
		dataType : "html",
		success : function(response) {
			if (response == "false") {
				$("#input-text_1").removeClass("disable");
				$name.val("");
				return false;
			} else {
				$("#input-text_1").addClass("disable");
			}
		}
	});
}

function deleteMachine(machineId) {
	if (confirm("确认删除？？")) {
		$.ajax({
			url : "user/deleteMachine",
			data : "id=" + machineId,
			cache : false,
			type : "GET",
			dataType : "html",
			success : function(response) {
				jump(1, 'main_nav', 'user/manager_machine');
			}
		});

	}
}

function updateMachine(machineId) {
	$.ajax({
		url : "user/prepareUpdate",
		data : "id=" + machineId,
		cache : false,
		type : "GET",
		dataType : "html",
		success : function(response) {
			$("body").append(response);
			$("#updateMachine").modal("show");
			$("#updateMachine").on("hidden", function() {
				$("#updateMachine").remove();
			});
		}
	});
}

function saveMachineChange() {
	$.ajax({
		url : "user/saveChange",
		data : $("#machineChange_form").serialize(),
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			$("#updateMachine").modal("hide");
			jump(1, 'main_nav', 'user/manager_machine');
		}
	});
}

function checkOldPassword() {
	$.ajax({
		url : "admin/checkPassword",
		data : "oldPassword=" + $("#oldPassword").val(),
		cache : false,
		type : "POST",
		dataType : "html",
		async : true,
		success : function(response) {
			if (response == "false") {
				$("#input-text_1").removeClass("disable");
				$("#oldPassword").val("");
			} else {
				$("#input-text_1").addClass("disable");
			}
		}
	});
}

function changePassword() {
	$.ajax({
		url : "admin/changePassword",
		data : "password=" + $("#password").val(),
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			jump(3, 'main_nav', 'admin/getSelf');
		}
	});
}

function getDetail(machineId,machineName,machineIp) {
	
	$.ajax({
		url : "user/checkAlive",
		data : "id=" + machineId,
		cache : false,
		type : "POST",
		dataType : "html",
		success : function(response) {
			if(response == "true"){
				jump(1, 'main_nav', 'user/machine_detail?id=' + machineId + "&machineName=" + machineName + "&machineIp=" + machineIp);
			}else{
				alert("服务器无法连接,请检查服务器");
			}
		}
	});
}


function getMachineDetail(type){
	var index = 2;
	var url = "";
	switch (type) {
	case "generalInfo":
		url = "machine/generalInfo";
		break;
	case "javaEnvironment":
		url = "machine/javaEnvironment";
		index = 3;
		break;
	case "procInfo"	:
		url = "machine/procInfo";
		index = 4;
		break;
	case "netInfo" :
		url = "machine/netInfo";
		index = 5;
		break;
	case "fileSystemInfo":
		url = "machine/fileSystemInfo";
		index = 6;
		break;
	}
	jumpDetail(index, "machine_detail_menu", url);
	return false;
}

function getDetailContent(url,index, parentId){
	var machineId = $("#machineId").val();
	$.ajax({
		url : url,
		data : "id=" + machineId,
		cache : false,
		type : "GET",
		dataType : "html",
		beforeSend:function(){
			$("#machine_detail_container").html("<img  src='static/images/loading.gif' style='margin-left:200px'/>");
		},
		success : function(response) {
			$("#machine_detail_container").html(response);
			resetMenu(index, parentId);
		},
		error:function(){
			alert("获取信息出错");
		}
	});
}

function jumpDetail(index, parentId, url){
	getDetailContent(url,index, parentId);
	
}