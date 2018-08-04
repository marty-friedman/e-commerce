$(function()
{
	hac.global.messageFromTag($("#bpmnProcessResult"));

	$("#tabs").tabs();

	$("#clearScriptContent").click(function() {
		$("#source").val('');
		$("#process").val('');
		$("#saveButton").hide();
		return false;
	});
}
);