$(function() {
	$("#login").click(function() {
		$(this).button("loading");
		$("#root_type").val($("#type_radio > .active").attr("id"));
		$("#login_form").submit();
	});
	
}); 