ACC.skiplinks = {

	bindAll: function()
	{
		this.bindLinks();
	},

	bindLinks: function()
	{
		$("a[href^='#']").not("a[href='#']").click(function()
		{
			var target = $(this).attr("href");
			$(target).attr("tabIndex", -1).focus();
		});
	}
};

$(document).ready(function ()
{
	var safari = /webkit/.test( navigator.userAgent );
	if (safari)
	{
		ACC.skiplinks.bindAll();
	}
});
