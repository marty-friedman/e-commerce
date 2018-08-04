$(function()
{
	hac.global.messageFromTag($("#bpmnProcessResult"));
	$("#tabs").tabs();
	
	$( "#download" ).click(function() {
  		sendAjaxRequest($( "#processCode" ).val());
	});
	
	$("#processCode").keypress(function(e) {
		if(e.which == 13) {
			$( "#download" ).click();
			return false;
		}
	});
	
	function sendAjaxRequest(processCode){
       var token = $("input[name='_csrf']").val();
       var header = "X-CSRF-TOKEN";
       $(document).ajaxSend(function(e, xhr, options) {
           xhr.setRequestHeader(header, token);
       });
       
       $.ajax({
           type : 'POST',
           contentType: "text/xml; charset=\"utf-8\"",
       	url : "/bpmn/export", 
           data : processCode,
		dataType : 'text',
           success: function (response) {
           	hac.global.notify($("#result").data("success"));
           	var blob = new Blob([response], {type: "text/plain;charset=utf-8"});
  				saveAs(blob, processCode+".bpmn");
           },
		error: function(jqXHR, error, errorThrown) {
			hac.global.error($("#result").data("error"));
		}
       });
   };
}
);