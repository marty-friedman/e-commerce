ACC.subcriptionDetails = {

    bindAll: function ()
    {
        this.subscriptionPopup();
        this.subscriptionTabs();
        this.subscriptionActions();
    },

    subscriptionPopup: function () {
        if($(".js-cancel-subscription").length>0){

            $(document).on("click",".js-cancel-subscription",function(e){
                e.preventDefault();
                	var title = $(this).data("help");
                    ACC.colorbox.open(title, {
                    	onOpen: function(){
                            $("#colorbox").addClass("subscription-popup");
                        },
                        html: $("#cancel-subscription-confirm").html(),
                        width: "400px"
                    });
            })

            $(document).on("click","#cancel-subscription-confirm .r_action_btn",function(e){
                e.preventDefault();
                $.colorbox.close();
            })
        }
    },

    subscriptionTabs: function () {
        $(".account-upgrade-subscription .tabs").accessibleTabs({
            tabhead:'h2',
            fx:"show",
            autoAnchor:true,
            fxspeed:null
        });
    },

    subscriptionActions: function () {

        $(document).on("click", ".view-potential-upgrade-billing-details", function(){
            $self=$(this);
            var popupTitle = $(this).data("popupTitle");
            
            ACC.colorbox.open(popupTitle,{
                href:$self.data("url"),
                close:'<span class="glyphicon glyphicon-remove"></span>',
                maxWidth:"100%",
        		opacity:0.7,
        		width:"auto",
                onComplete: function(){
                    $.colorbox.resize();

                    if($("#addUpgradeButton").hasClass("not-upgradable")){
                        $("#upgrade-billing-changes .confirm").attr("disabled","disabled").addClass("not-upgradable")
                    }
                }
            })
        })

        $(document).on("click", "#upgrade-billing-changes .r_action_btn", function(e){
            e.preventDefault();
            $.colorbox.close();
        })
    }
};

$(document).ready(function () {
    ACC.subcriptionDetails.bindAll();
});
$(window).resize(function(){
	var colorBoxWidth = $('#colorbox').width();
	if(colorBoxWidth > "550") {
		$.colorbox.resize({
			width: "600"
		});
	}
	else {
		$.colorbox.resize();
		
	}
});