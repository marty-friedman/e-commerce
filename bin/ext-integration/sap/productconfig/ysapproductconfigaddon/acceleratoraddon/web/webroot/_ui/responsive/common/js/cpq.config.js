/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */





CPQ.config = {
    lastTarget: undefined,
    addToCartClickTimestamp: 0,
    ddlbValuePriceRegEx: /\[[^\[\]]*\]$/,
    bindAll: function () {
        if (!String.prototype.startsWith) {
            String.prototype.startsWith = function (searchString, position) {
                position = position || 0;
                return this.indexOf(searchString, position) === position;
            };
        }
        CPQ.config.USER_CAN_TOUCH = (('ontouchstart' in window) || (navigator.msMaxTouchPoints > 0));
        window.addEventListener('mouseover', function onFirstHover() {
            CPQ.config.USER_CAN_HOVER = true;
            window.removeEventListener('mouseover', onFirstHover, false);
        }, false);
        CPQ.config.getSimilarVariants();
        CPQ.config.getPricing();
        CPQ.config.getAnalytics();
        CPQ.config.generateAriaDescribedBy();
        CPQ.config.registerStaticOnClickHandlers();
        CPQ.config.doAfterPost();
        $(window).resize(function () {
            CPQ.config.makeLabelsUnderImagesSameHeight('.cpq-csticValue', '.cpq-csticValueImageLabel', true);
        });
    },

    registerStaticOnClickHandlers: function () {
        $(".product-details .name").on(
            "click keypress",
            function (e) {
                CPQ.uihandler.clickHideShowImageGallery(
                    CPQ.config.doUpdatePost, e);
            });
        $("#cpqMenuArea").on("click", function (e) {
            CPQ.config.menuOpen(e);
        });
    },

    registerOnClickHandlers: function () {
        $(".cpq-group-title-close, .cpq-group-title-open").on("click keydown",
            function (e) {
                CPQ.uihandler.clickGroupHeader(CPQ.config.doUpdatePost, e);
            });

        // mousedown/mouseup replaces the "click" event registration
        // If the user changes a value in a text field and clicks direct
        // addToCart, the page will be updated by the onChange event
        // and the "old" button receives the "mousedown" and the "new"
        // button receives the "mouseup" event - no "click" event
        // (click is mousedown+mouseup in one "field").
        // Now with the mousedown the time is taken and with the mousup it is checked
        // if less than 2 seconds are passed by, since mousedown.
        $(".cpq-btn-addToCart").on("mousedown", function (e) {
            CPQ.config.addToCartClickTimestamp = e.timeStamp;
        });

        $(".cpq-btn-addToCart").on("mouseup", function (e) {
            if ((e.timeStamp - CPQ.config.addToCartClickTimestamp) < 2000) {
                CPQ.config.clickAddToCartButton();
            }
        });

        $(".cpq-btn-addToCart").on("keydown", function (e) {
            if (e.which === 13) {
                CPQ.config.clickAddToCartButton();
            }
        });

        $(".cpq-csticlabel-longtext-icon").on('click', function (e) {
            CPQ.config.longTextIconClicked(e);
        });

        CPQ.config.registerConflictOnClickHandlers();
        CPQ.config.registerMenuOnClickHandlers();
        CPQ.config.registerPreviousNextOnClickHandlers();
        CPQ.config.registerCsticValueImageOnClickHandler();
        CPQ.config.registerAnalyticsTooltip();

    },

    registerConflictOnClickHandlers: function () {
        $(".cpq-conflict-link-to-config").on(
            "click",
            function (e) {
                CPQ.config.handleConflictNavigation(
                    CPQ.idhandler.getCsticIdFromCsticFieldId,
                    "NAV_TO_CSTIC_IN_GROUP", e);
            });

        $(".cpq-conflict-link").on(
            "click",
            function (e) {
                CPQ.config.handleConflictNavigation(
                    CPQ.idhandler.getCsticIdFromViolatedCsticFieldId,
                    "NAV_TO_CSTIC_IN_CONFLICT", e);
            });

        $(".cpq-conflict-retractValue-button").on("click", function (e) {
            CPQ.config.handleRetractConflict(e);
        });
    },

    registerMenuOnClickHandlers: function () {
        $(".cpq-menu-node, .cpq-menu-conflict-header").on("click", function (e) {
            CPQ.config.menuGroupToggle(e);
        });
        $(".cpq-menu-leaf, .cpq-menu-conflict-node").on("click", function (e) {
            CPQ.config.menuNavigation(e);
        });
        $(".cpq-menu-icon-remove").on("click", function (e) {
            CPQ.config.menuClose(e);
        });
    },

    registerPreviousNextOnClickHandlers: function () {
        $(".cpq-previous-button").on("click", function (e) {
            CPQ.config.previousNextButtonClicked("PREV_BTN", e);
        });
        $(".cpq-next-button").on("click", function (e) {
            CPQ.config.previousNextButtonClicked("NEXT_BTN", e);
        });
    },

    registerCsticValueImageOnClickHandler: function () {
        var multiSelectValueImages = $(".cpq-cstic-value-container-multi");
        multiSelectValueImages.on("click", function (e) {
            CPQ.config.csticValueImageMultiClicked(e);
        });
        multiSelectValueImages.on("keypress", function (e) {
            if (e.which === 13 || e.which === 32) { // enter or space
                CPQ.config.csticValueImageMultiClicked(e);
            }
        });

        var singleSelectValueImages = $(".cpq-cstic-value-container-single");
        singleSelectValueImages.on("click", function (e) {
            CPQ.config.csticValueImageSingleClicked(e);
        });
        singleSelectValueImages.on("keypress", function (e) {
            if (e.which === 13 || e.which === 32) { // enter or space
                CPQ.config.csticValueImageSingleClicked(e);
            }
        });

        var valueImageContainer = $(".cpq-cstic-value-image-container");
        valueImageContainer.on("mouseenter", function (e) {
            CPQ.config.hoverOrFocusOnValueImage($(this));
        });
        valueImageContainer.on("focusin", function (e) {
            CPQ.config.hoverOrFocusOnValueImage($(this));
        });
        valueImageContainer.on("mouseleave", function (e) {
            CPQ.config.hoverLostOrBlurOnVlaueImage($(this));
        });
        valueImageContainer.on("focusout", function (e) {
            CPQ.config.hoverLostOrBlurOnVlaueImage($(this));
        });
    },

    registerVariantListOnClickHandlers: function () {
        $(".cpq-vc-viewDetails-btn").on("click", function (e) {
            CPQ.config.showVariantOverview(e);
        });
    },

    checkValueImageClicked: function (elem) {
        if (elem.hasClass("cpq-cstic-value-image")
            || elem.find(".cpq-cstic-value-image").length > 0) {
            CPQ.config.valueChangeViaImage = true;
        }
    },

    hoverOrFocusOnValueImage: function (elem) {
        if (CPQ.config.valueChangeViaImage) {
            CPQ.config.valueChangeViaImage = false;
        } else {
            elem.addClass("cpq-cstic-value-image-container-hover");
        }
    },

    hoverLostOrBlurOnVlaueImage: function (elem) {
        elem.removeClass("cpq-cstic-value-image-container-hover");
        CPQ.config.valueChangeViaImage = false;
    },

    csticValueImageMultiClicked: function (e) {
        CPQ.config.checkValueImageClicked($(e.target));
        var containerId = $(e.currentTarget).attr('id');
        // remove suffix .container
        var csticValueIdInput = containerId.substring(0,
                containerId.length - 10)
            + ".checkBoxWithImage";
        var input = $(CPQ.core.encodeId(csticValueIdInput));
        if (input.val() === "true") {
            input.val("false");
        } else {
            input.val("true");
        }
        var cpqAction = "VALUE_CHANGED";
        var path = input.attr('name');
        var data = CPQ.config.getSerializedConfigForm(cpqAction, path, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    csticValueImageSingleClicked: function (e) {
        CPQ.config.checkValueImageClicked($(e.target));
        var containerId = $(e.currentTarget).attr('id');
        // remove suffix .container
        var csticValueNameId = containerId
                .substring(0, containerId.length - 10)
            + ".valueName";
        var csticValueNameDiv = $(CPQ.core.encodeId(csticValueNameId));
        var csticValueName = csticValueNameDiv.text();
        var imageGroupId = csticValueNameDiv.parent().attr('id');
        // suffix .radioGroupWithImage
        var inputId = imageGroupId.substring(0, imageGroupId.length - 20)
            + ".radioButtonWithImage";
        var input = $(CPQ.core.encodeId(inputId));
        input.val(csticValueName);
        var cpqAction = "VALUE_CHANGED";
        var path = input.attr('name');
        var data = CPQ.config.getSerializedConfigForm(cpqAction, path, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    longTextIconClicked: function (e) {
        var labelId = $(e.currentTarget).parent().attr('id');
        var csticId = CPQ.idhandler.getCsticIdFromLableId(labelId);
        var targetId = CPQ.core.encodeId(csticId + ".showFullLongText");
        var cpqAction;
        if ($(targetId).val() !== "true") {
            $(targetId).val("true");
            cpqAction = 'SHOW_FULL_LONG_TEXT';
            $(e.currentTarget).next().show();
        } else {
            $(targetId).val("false");
            cpqAction = 'HIDE_FULL_LONG_TEXT';
            $(e.currentTarget).next().hide();
        }
        csticId = CPQ.idhandler.getCsticIdFromConflictCstic(csticId);

        var data = CPQ.config
            .getSerializedConfigForm(cpqAction, csticId, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    previousNextButtonClicked: function (cpqAction, e) {
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm(cpqAction, "", false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data, "##first##"]);
    },

    menuClose: function (e) {
        CPQ.config.menuRemove();
        CPQ.focushandler.focusOnFirstInput();
        e.preventDefault();
        e.stopPropagation();
    },

    menuGroupToggle: function (e) {
        var menuNodeId = $(e.currentTarget).attr("id");
        var nodeId = CPQ.idhandler.getGroupIdFromMenuNodeId(menuNodeId);
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm("MENU_NAVIGATION", "",
            false, "", nodeId);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    menuNavigation: function (e) {
        var menuNodeId = $(e.currentTarget).attr("id");
        var nodeId = CPQ.idhandler.getGroupIdFromMenuNodeId(menuNodeId);
        $("#groupIdToDisplay").val(nodeId);
        var data = CPQ.config.getSerializedConfigForm("MENU_NAVIGATION", "",
            true, nodeId);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data, "##first##"]);
        CPQ.config.menuRemove();
    },

    menuRemove: function () {
        var sideBar = $("#configSidebarSlot").parent();
        sideBar.addClass("hidden-xs hidden-sm");
        var menu = $("#cpqMenuArea");
        menu.removeClass("hidden-xs hidden-sm");
        var config = $("#configContentSlot").parent();
        config.removeClass("hidden-xs hidden-sm");
        config.addClass("col-xs-12 col-sm-12");
        // show footer header
        $(".main-footer").removeClass("hidden-xs hidden-sm");
        $(".main-header").removeClass("hidden-xs hidden-sm");
        $("#product-details-header").removeClass("hidden-xs hidden-sm");
    },

    menuOpen: function (e) {
        var sideBar = $("#configSidebarSlot").parent();
        sideBar.removeClass("hidden-xs hidden-sm");
        var menu = $("#cpqMenuArea");
        menu.addClass("hidden-xs hidden-sm");
        var config = $("#configContentSlot").parent();
        config.addClass("hidden-xs hidden-sm");
        config.removeClass("col-xs-12 col-sm-12");
        // show footer header
        $(".main-footer").removeClass("hidden-xs hidden-sm");
        $(".main-header").removeClass("hidden-xs hidden-sm");
        $(".product-details-header").removeClass("hidden-xs hidden-sm");
        // hide footer header
        $(".main-footer").addClass("hidden-xs hidden-sm");
        $(".main-header").addClass("hidden-xs hidden-sm");
        $("#product-details-header").addClass("hidden-xs hidden-sm");
        CPQ.focushandler.focusRestore($(".cpq-menu-leaf-selected").attr("id"),
            true, $(window).height() / 4);
        e.preventDefault();
        e.stopPropagation();
    },

    handleConflictNavigation: function (getCsticIdFx, cpqAction, e) {
        var csticFieldId = $(e.currentTarget).parent().attr("id");
        var csticId = getCsticIdFx.apply(this, [csticFieldId]);
        $("#autoExpand").val(false);
        var data = CPQ.config.getSerializedConfigForm(cpqAction, csticId, true,
            "");
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    handleRetractConflict: function (e) {
        var csticFieldId = $(e.currentTarget).parent().attr("id");
        var csticId = CPQ.idhandler.getCsticIdFromCsticFieldId(csticFieldId);
        var targetId = CPQ.core.encodeId("conflict." + csticId
            + ".retractValue");
        var path = $(e.currentTarget).parents(".cpq-cstic").children(
            "input:hidden").attr("name");
        $(targetId).val(true);
        var data = CPQ.config.getSerializedConfigForm("RETRACT_VALUE", path,
            false, "");
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    ensureMessagesAreVisible: function () {
        if ($(".alert-info").length && !conflicts) {
            $(".alert-info").each(function () {
                if ($(this).parent().attr("class") === "global-alerts") {
                    $(document).scrollTop($(".alert-info").offset().top - 10);
                    return false;
                }
            });
        }
    },

    makeLabelsUnderImagesSameHeight: function (containerClass, labelClass, newLineCheck) {
        $(containerClass).each(function () {
            var maxHeight = 0;
            var lastYOffSet = 0;
            var actualLine = [];
            var imagesOfCstic = $(this).find(labelClass);
            imagesOfCstic.height('auto');
            imagesOfCstic.each(function () {
                var actualHeight = $(this).height();
                var actualOffSet = $(this).offset().top;
                if (lastYOffSet === 0) {
                    // first elem
                    lastYOffSet = actualOffSet;
                    maxHeight = actualHeight;
                } else if (newLineCheck && lastYOffSet !== actualOffSet) {
                    // new line
                    $(actualLine).height(maxHeight);
                    maxHeight = actualHeight;
                    lastYOffSet = actualOffSet;
                    actualLine = [];
                } else if (actualHeight > maxHeight) {
                    // new max height in actual line
                    maxHeight = actualHeight;
                }
                actualLine.push(this);
            });
            if (actualLine.length > 0) {
                $(actualLine).height(maxHeight);
                actualLine = [];
            }
        });
    },

    doAfterPost: function () {
        CPQ.config.registerOnClickHandlers();
        CPQ.config.registerAjax();
        if ($("#focusId").attr("value").length > 0) {
            CPQ.focushandler
                .focusOnInputByCsticKey($("#focusId").attr("value"));
            $("#focusId").val("");
        }
        CPQ.config.ensureMessagesAreVisible();
        CPQ.config.makeLabelsUnderImagesSameHeight('.cpq-csticValue', '.cpq-csticValueImageLabel', true);
        CPQ.config.ifExpModeActive();
    },

    getSimilarVariants: function () {
        if ($('#configVariantSearchResults').length > 0) {
            CPQ.core.ajaxRunCounterAsyncServices++;
            if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                CPQ.core.ajaxStartTimeAsyncServices = new Date().getTime();
            }
            CPQ.config.variantSearchAbortHanlder = function () {
                // firefox just shows "undefined" if we do not cancelled ajax
                // requests proper
                // chrome seems to always work fine
                // IE shows a blank page if we cancel ajax
                if (navigator.userAgent.indexOf("Firefox") > -1) {
                    $.ajax().abort();
                }
            };
            $(window)
                .bind('beforeunload', CPQ.config.variantSearchAbortHanlder);
            var data = "configId=" + $('#configId').val() + "&productCode="
                + $(CPQ.core.encodeId('kbKey.productCode')).val();
            if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                CPQ.core.ajaxServerStartTimeAsyncServices = new Date().getTime();
            }
            $.post(CPQ.core.getVaraiantSearchUrl(), data, function (response) {
                if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                    CPQ.core.ajaxServerStopTimeAsyncServices = new Date().getTime();
                }
                CPQ.uihandler.updateSlotContent(response,
                    "configVariantSearchResults");
                $(window).unbind('beforeunload',
                    CPQ.config.variantSearchAbortHanlder);
                CPQ.config.variantSearchAbortHanlder = undefined;

                $("#cpqVariantCarousel").owlCarousel({
                    navigation: true,
                    navigationText: ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
                    pagination: false,
                    responsiveBaseWidth: '.cpq-vc-container',
                    afterAction: function () {
                        CPQ.config.makeLabelsUnderImagesSameHeight('.cpq-vc-container', '.cpq-vc-name', false);
                        CPQ.config.updateVariantImageHeight();
                    }
                });
                // register owl eventt handler here
                CPQ.config.registerVariantListOnClickHandlers();
                CPQ.core.ajaxRunCounterAsyncServices--;
                if (CPQ.core.ajaxRunCounterAsyncServices === 0) {
                    CPQ.core.ajaxStopTimeAsyncServices = new Date().getTime();
                }
            });
        }
    },

    getAnalytics: function () {
        if ($("#analyticsEnabled").text() === "true") {
            var ret = CPQ.config.prepareAsyncServiceCall();
            $.post(CPQ.core.getAnalyticsUrl(), ret.data, function (response) {
                if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                    CPQ.core.ajaxServerStopTimeAsyncServices = new Date().getTime();
                }
                if (ret.configState === CPQ.core.configState) {
                    CPQ.config.insertAnalyticValues(response);
                }
                CPQ.core.ajaxRunCounterAsyncServices--;
                if (CPQ.core.ajaxRunCounterAsyncServices === 0) {
                    CPQ.core.ajaxStopTimeAsyncServices = new Date().getTime();
                }
            });
        }
    },

    removeDefaultToolTip: function(){
        $(".cpq-label-default").each(function(){
            if(!$(this).attr("title-data")){
                $(this).attr("title-data", $(this).attr("title"));
                $(this).removeAttr("title");
            }
        });
    },

    restoreDefaultToolTip: function(){
        $(".cpq-label-default").each(function(){
            if(!$(this).attr("title")){
                $(this).attr("title", $(this).attr("title-data"));
                $(this).removeAttr("title-data");
            }
        });
    },

    registerAnalyticsTooltip: function() {
        $(".cpq-label-default").click(function (e) {
            var mobileTooltip = $(this).find(".mobileTooltip");
            if (!mobileTooltip.length) {
                if(!CPQ.config.USER_CAN_HOVER || CPQ.config.USER_CAN_TOUCH){
                    $(".mobileTooltip").remove();
                    CPQ.config.removeDefaultToolTip();
                    var style ="";
                    var positionToElem = $(this).closest(".cpq-csticValue").find(".cpq-cstic-value-with-image"); // frist image container
                    if(positionToElem[0]){
                        var actualElem = $(this).closest(".cpq-cstic-value-with-image"); // image container clicked
                        var left = -15 + actualElem.offset().left - positionToElem.offset().left;
                        var top = 175 + actualElem.offset().top - positionToElem.offset().top;
                        style = ' style="left:'+left+'px; top:'+top+'px"';
                    }
                    $(this).append('<span class="mobileTooltip"'+style+'>' + $(this).attr("title-data") + '</span>');
                    e.preventDefault();
                    e.stopPropagation();
                }
            } else {
                mobileTooltip.remove();
                e.preventDefault();
                e.stopPropagation();
            }
        });
        $(document).click(function(e) {
            $(".mobileTooltip").remove();
        });
        $(".cpq-label-default").mouseover(function(e) {
           CPQ.config.restoreDefaultToolTip();
           $(".mobileTooltip").remove();
        });
    },

    insertAnalyticValues: function (reposnse) {
        var obj = $.parseJSON(reposnse);
        for (var ii = 0, lenII = obj.analyticCstics.length; ii < lenII; ++ii) {
            var analyticCstic = obj.analyticCstics[ii];
            var uiKey = analyticCstic.csticUiKey;
            if ($(CPQ.core.encodeId(uiKey + '.key')).length === 0) {
                continue;
            }

            for (var jj = 0, lenJJ = analyticCstic.analyticValues.length; jj < lenJJ; ++jj) {
                CPQ.config.replaceAnalyticValues(obj, uiKey, analyticCstic.analyticValues[jj]);
            }
        }
        CPQ.config.makeLabelsUnderImagesSameHeight('.cpq-csticValue', '.cpq-csticValueImageLabel', true);
    },

    replaceAnalyticValues: function(obj, uiKey, analyticValue) {
        var uiValueKey = uiKey + '.' + analyticValue.csticValueName;
        var analyticsDiv = $(CPQ.core.encodeId(uiValueKey + '.analytics'));
        if (analyticsDiv.length <= 0) {
            return;
        }
        var popularityTooltipSpan = $(CPQ.core.encodeId(uiValueKey + '.popularityTooltip'));
        var popularityInPercentSpan = $(CPQ.core.encodeId(uiValueKey + '.popularityInPercent'));
        var popularityAriaText = $(CPQ.core.encodeId(uiValueKey + '.popularityAriaText'));

        popularityTooltipSpan.attr("title", (popularityTooltipSpan.attr("title").replace(obj.popularityInPercent.placeHolder, analyticValue.popularityInPercent)));
        popularityInPercentSpan.html(popularityInPercentSpan.html().replace(obj.popularityInPercent.placeHolder, analyticValue.popularityInPercent));
        popularityAriaText.html(popularityAriaText.html().replace(obj.popularityInPercent.placeHolder, analyticValue.popularityInPercent));
        analyticsDiv.removeClass('cpq-csticValueAnalyticsTemplate').addClass('cpq-csticValueAnalytics');
        if(popularityAriaText.html().startsWith(popularityInPercentSpan.html())){
            popularityAriaText.html(popularityAriaText.html().replace(popularityInPercentSpan.html(), ""));
        }
    },

    getPricing: function () {
        if ($("#asyncPricingMode").text() === "true") {
            var ret = CPQ.config.prepareAsyncServiceCall();
            $.post(CPQ.core.getPricingUrl(), ret.data, function (response) {
                if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
                    CPQ.core.ajaxServerStopTimeAsyncServices = new Date().getTime();
                }
                if (ret.configState === CPQ.core.configState) {
                    CPQ.config.updateValuePrices(response);
                    CPQ.config.updatePriceSummary(response);
                }
                CPQ.core.ajaxRunCounterAsyncServices--;
                if (CPQ.core.ajaxRunCounterAsyncServices === 0) {
                    CPQ.core.ajaxStopTimeAsyncServices = new Date().getTime();
                }
            });
        }
    },

    prepareAsyncServiceCall: function (){
        CPQ.core.ajaxRunCounterAsyncServices++;
        if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
            CPQ.core.ajaxStartTimeAsyncServices = new Date().getTime();
        }
        var ret = {};
        ret.data = "productCode="
            + $(CPQ.core.encodeId('kbKey.productCode')).val();
        ret.configState = CPQ.core.configState;
        if (CPQ.core.ajaxRunCounterAsyncServices === 1) {
            CPQ.core.ajaxServerStartTimeAsyncServices = new Date().getTime();
        }
        return ret;
    },

    updatePriceSummary: function (data) {
        clearTimeout(CPQ.core.priceSummaryDropHandle);
        var obj = $.parseJSON(data);
        if (obj.pricingError) {
            $(".cpq-pricing-summary-error").slideDown(300, function () {
                $(".cpq-pricing-summary-error").attr('role', 'alert');
            });
        }
        else if ($(".cpq-pricing-summary-error").is(":visible")) {
            $(".cpq-pricing-summary-error").removeAttr('role');
            $(".cpq-pricing-summary-error").slideUp(300);
        }
        $("#basePriceValue").html(obj.basePriceValue);
        $("#basePriceValue").attr("title", obj.basePriceValue);
        $("#selectedOptionsValue").html(obj.selectedOptionsValue);
        $("#selectedOptionsValue").attr("title", obj.selectedOptionsValue);
        $("#currentTotalValue").html(obj.currentTotalValue);
        $("#currentTotalValue").attr("title", obj.currentTotalValue);
    },

    updateValuePrices: function (data) {
        clearTimeout(CPQ.core.valuePriceDropHandle);
        clearTimeout(CPQ.core.valuePriceDropdownDropHandle);
        var obj = $.parseJSON(data);
        for (var i = 0; i < obj.valuePricesArray.length; i++) {
            var cstic = obj.valuePricesArray[i];
            var csticKey = cstic.csticKey;
            var selectedValue = $(CPQ.core.encodeId(csticKey + ".ddlb")).val();
            for (var j = 0; j < cstic.csticValuesArray.length; j++) {
                var csticValue = cstic.csticValuesArray[j];
                var csticValueKey = csticValue.csticValueKey;
                var idHeader = csticKey + "." + csticValueKey;
                var csticValueOption = idHeader + ".option";
                var csticValueLabel = idHeader + ".label";
                var csticCheckboxId = csticKey + ".checkBox";
                var elementId = idHeader + ".valuePrice";
                var elementCheckBoxId = csticKey + ".valuePrice";

                CPQ.config.setValuePrice(csticValueLabel, $(CPQ.core.encodeId(csticValueLabel)), elementId, csticValue.valuePrice);
                CPQ.config.setValuePrice(csticCheckboxId, $(CPQ.core.encodeId(csticCheckboxId)), elementCheckBoxId, csticValue.valuePrice);
                CPQ.config.setValuePriceForDropDown(csticValueOption, $(CPQ.core.encodeId(csticValueOption)), csticValue.valuePrice, selectedValue);
            }
        }
    },

    setValuePriceForDropDown: function (csticValueOption, csticValue, valuePrice, selectedValue) {
        if (csticValue.length > 0) {
            var textOfCsticValue = csticValue.text();
            textOfCsticValue = textOfCsticValue.replace(CPQ.config.ddlbValuePriceRegEx, "");
            var sign = "";
            var isSelectedText = valuePrice.match(/\d+/g) == null;
            if (!valuePrice.startsWith("-") && !isSelectedText) {
                sign = "+";
            }
            if (!isSelectedText || (selectedValue === csticValue.val()) && valuePrice.length > 0) {
                textOfCsticValue += "  [" + sign + valuePrice + "]";
            }
            $(CPQ.core.encodeId(csticValueOption)).text(textOfCsticValue);
        }
    },

    setValuePrice: function (csticValueLabel, csticValueByLabel, elementId, valuePrice) {
        if (csticValueByLabel.length > 0) {
            var isSelectedOption = csticValueByLabel.parent().find('input:checked, div.cpq-cstic-image-value-selected').length > 0;
            var isSelectedText = valuePrice.match(/\d+/g) == null;
            if (isSelectedText && !isSelectedOption) {
                valuePrice = "";
            } else if (!valuePrice.startsWith("-") && !isSelectedText) {
                valuePrice = "+" + valuePrice;
            }
            var html = "<div id='" + elementId + "' class='cpq-csticValueDeltaPrice cpq-csticValueLabel' title='" + valuePrice + "'>" + valuePrice + "</div>";
            // if value price is already present it needs to be removed
            $(CPQ.core.encodeId(elementId)).remove();
            $(CPQ.core.encodeId(csticValueLabel)).after(html);
        }
    },

    updateVariantImageHeight: function (counter) {
        // reset height, in case all items get smaller due to screen width change
        $(".cpq-vc-imgContainer").css("line-height", "0px");
        $(".cpq-vc-imgContainer").css("height", "auto");
        var maxImageHeight = Math.max.apply(null, $(".cpq-vc-imgContainer").map(function () {
                return $(this).height();
            }
        ).get());
        // if images are not loaded yet, height of every container is '1'.
        // This is because we set the line-heigt of each image container to '0' and its height to 'auto'.
        // Do not set this height, instead give more time to wait
        if (maxImageHeight > 1) {
            $(".cpq-vc-imgContainer").css("height", maxImageHeight + "px");
            // set line height as well, to force vertical middle alignment for image
            $(".cpq-vc-imgContainer").css("line-height", maxImageHeight + "px");
        } else {
            if (undefined === counter) {
                setTimeout(function () {
                    CPQ.config.updateVariantImageHeight(1);
                }, 50);
            } else {
                counter++;
                // give up after 10 tries, maybe none of the variants has an image assigned?
                if (counter < 10) {
                    setTimeout(function () {
                        CPQ.config.updateVariantImageHeight(counter);
                    }, 50);
                }
            }
        }
    },


    showVariantOverview: function (e) {
        var sUrl = $(e.currentTarget).data("goToOverview");
        window.location = sUrl;
    },

    doUpdatePost: function (e, data, focusId) {
        CPQ.core.configState++;
        CPQ.core.ajaxServerStartTime = new Date().getTime();
        $.post(CPQ.core.getPageUrl(), data, function (response) {
            if (CPQ.core.ajaxRunCounter === 1) {
                CPQ.core.ajaxServerStopTime = new Date().getTime();
                if (CPQ.core.doRedirect(response)) {
                    return;
                }
                var focusElementId;
                var scrollTo;
                var oldOffsetTop = 0;
                if (focusId) {
                    focusElementId = focusId;
                    scrollTo = true;
                } else {
                    focusElementId = CPQ.focushandler.focusSave();
                    scrollTo = false;
                    oldOffsetTop = CPQ.config.getOffset(focusElementId);
                }
                CPQ.config.updateContent(response);
                CPQ.config.getSimilarVariants();
                CPQ.config.getPricing();
                CPQ.config.getAnalytics();
                CPQ.config.generateAriaDescribedBy();
                if (focusElementId === "##first##") {
                    CPQ.focushandler.focusOnFirstInput();
                } else {
                    CPQ.config.doScrolling(focusElementId, scrollTo, oldOffsetTop);
                }
                CPQ.config.doAfterPost();
                CPQ.core.lastAjaxDone();
            }
            CPQ.core.ajaxRunning = false;
            CPQ.core.ajaxRunCounter--;
        });
        e.preventDefault();
        e.stopPropagation();
    },

    getOffset: function (focusElementId) {
        var oldOffsetTop = 0;

        if (focusElementId.length > 0) {
            var focusElement = $(CPQ.core.encodeId(focusElementId));
            if ($(focusElement).offset()) {
                oldOffsetTop = $(focusElement).offset().top - $(document).scrollTop();
            }
        }
        return oldOffsetTop;
    },

    doScrolling: function (focusElementId, scrollTo, oldOffsetTop) {
        var newOffsetTop = oldOffsetTop;
        if (focusElementId.length > 0 && !scrollTo) {
            var elem = $(CPQ.core.encodeId(focusElementId));
            if (elem.length > 0) {
                newOffsetTop = elem.offset().top - $(document).scrollTop();
            }
        }
        CPQ.focushandler.focusRestore(focusElementId, scrollTo,
            newOffsetTop - oldOffsetTop);
    },

    getSerializedConfigForm: function (cpqAction, focusId, forceExpand,
                                       groupIdToToggle, groupIdToToggleInSpecTree) {
        $("#cpqAction").val(cpqAction);
        $("#focusId").val(focusId);
        $("#forceExpand").val(forceExpand);
        $("#groupIdToToggle").val(groupIdToToggle);
        $("#groupIdToToggleInSpecTree").val(groupIdToToggleInSpecTree);
        var data = $("#configform").serialize();
        $("#cpqAction").val("");
        $("#focusId").val("");
        $("#forceExpand").val(false);
        $("#groupIdToToggle").val("");
        $("#groupIdToToggleInSpecTree").val("");

        return data;
    },

    updateContent: function (response) {
        var analyticState;
        if ($("#analyticsEnabled").text() === "true") {
            analyticState = CPQ.uihandler.storeState('.cpq-csticValueAnalytics');
        }
        var valuePriceState;
        var valuePriceDDLB;
        if ($("#asyncPricingMode").text() === "true") {
            valuePriceState = CPQ.uihandler.storeState('.cpq-csticValueDeltaPrice', function (obj, item) {
                item.labelId = obj.prev().attr('id');
                item.html = obj.wrap('<p/>').parent().html();
            });
            valuePriceDDLB = CPQ.uihandler.storeState('select[id*=ddlb]');
        }
        CPQ.uihandler.updateSlotContent(response, "configContentSlot");
        if (analyticState) {
            CPQ.uihandler.restoreState(analyticState, function (item) {
                var div = $(CPQ.core.encodeId(item.id));
                if (div.length > 0) {
                    div.html(item.html);
                    div.removeClass('cpq-csticValueAnalyticsTemplate').addClass('cpq-csticValueAnalytics');
                }
            });
        }
        if (valuePriceState) {
            CPQ.uihandler.restoreState(valuePriceState, function (item) {
                var label = $(CPQ.core.encodeId(item.labelId));
                label.after(item.html);
            });
            CPQ.core.valuePriceDropHandle = setTimeout(function () {
                $('.cpq-csticValueDeltaPrice').remove();
            }, 250);
        }
        if (valuePriceDDLB) {
            CPQ.uihandler.restoreState(valuePriceDDLB, function (item) {
                var select = $(CPQ.core.encodeId(item.id));
                var values = select.children();
                var oldValues = $.parseHTML("<div>" + item.html + "</div>");
                $.each(values, function (id, value) {
                    var oldValue = $(oldValues).find("option[id='" + value.id + "']");
                    if (oldValue && oldValue.text().match(CPQ.config.ddlbValuePriceRegEx)) {
                        $(value).text(oldValue.text());
                    }
                });
            });
            CPQ.core.valuePriceDropdownDropHandle = setTimeout(function () {
                var dropDowns = $('select[id*=ddlb]');
                $.each(dropDowns, function (id, ddlb) {
                    $.each(dropDowns.children(), function (counter, optionValue) {
                        var textOfCsticValue = optionValue.text;
                        textOfCsticValue = textOfCsticValue.replace(CPQ.config.ddlbValuePriceRegEx, "");
                        optionValue.text = textOfCsticValue;
                    });
                });
            }, 250);
        }

        CPQ.uihandler.updateSlotContent(response, "configSidebarSlot");

        var varaiantSearchState = CPQ.uihandler.storeState('#configVariantSearchResults');
        var priceSummaryState;
        if ($("#asyncPricingMode").text() === "true") {
            priceSummaryState = CPQ.uihandler.storeState('#basePriceValue, #currentTotalValue, #selectedOptionsValue');
        }
        CPQ.uihandler.updateSlotContent(response, "configBottombarSlot");
        if (priceSummaryState) {
            CPQ.uihandler.restoreState(priceSummaryState);
            CPQ.core.priceSummaryDropHandle = setTimeout(function () {
                $('#basePriceValue, #currentTotalValue, #selectedOptionsValue').text('-');
                $('#basePriceValue, #currentTotalValue, #selectedOptionsValue').prop('title', '-');
            }, 250);
        }
        CPQ.uihandler.restoreState(varaiantSearchState);
        CPQ.uihandler.updateSlotContent(response, "cpq-message-area");
        CPQ.config.checkValueHasChanged();
    },

    registerAjax: function () {
        $("#configform").submit(function (e) {
            e.preventDefault();
        });
        // FF and Chrome does fire onChange when enter is pressed in input field
        // and
        // additionally the onKeyPress event
        $("#configform :input").on("change", function (e) {
            CPQ.config.fireValueChangedPost(e);
        });
        // IE does not fire onChange when enter is pressed in input field, only
        // on
        // focus loss
        $("#configform :input").keypress(function (e) {
            if (e.which === 13) {
                CPQ.config.fireValueChangedPost(e);
            }
        });
        $(document).ajaxError(function (event, xhr) {
            document.write(xhr.responseText);
        });

    },

    ifExpModeActive: function () {
        var result = new RegExp("expmode=true", "i").exec(window.location.search);
        if (result === null) {
            return;
        }

        $('label.cpq-csticlabel, label.cpq-csticValueLabel, option.cpq-csticValue, img.cpq-cstic-value-image').each(function () {
            var idSubstring = null;
            var elementId = $(this).attr("id");
            try {
                idSubstring = elementId.substring(0, elementId.lastIndexOf('.'));
            } catch (e) {
                return;
            }

            var tooltip = CPQ.config.getTooltipText(idSubstring);
            $(this).prop("title", tooltip);
        });
    },

    getTooltipText: function (idSubstring) {
        return idSubstring.substring(idSubstring.length, idSubstring.lastIndexOf('.') + 1);
    },

    checkValueHasChanged: function () {
        if (CPQ.config.lastTarget) {
            var element = document.getElementById(CPQ.config.lastTarget.id);
            if (element === null || element.value !== CPQ.config.lastTarget.value || element.checked !== CPQ.config.lastTarget.checked) {
                CPQ.config.lastTarget = null;
            }
        }
    },

    hasTargetChanged: function (e) {
        if (!CPQ.config.lastTarget || e.target.id !== CPQ.config.lastTarget.id
            || e.target.value !== CPQ.config.lastTarget.value
            || e.target.checked !== CPQ.config.lastTarget.checked) {
            CPQ.config.lastTarget = e.target;
            return true;
        }
        return false;
    },

    fireValueChangedPost: function (e) {
        if (CPQ.config.hasTargetChanged(e)) {
            var path = $(e.currentTarget).parents(".cpq-cstic").children(
                "input:hidden").attr("name");
            var data = CPQ.config.getSerializedConfigForm('VALUE_CHANGED',
                path, false, "");
            setTimeout(function () {
                CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
            }, 50);
        } else {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    },

    clickAddToCartButton: function () {
        // Postpone submit, to take care that a potential
        // update is triggered in parallel, by a focus lost
        setTimeout(function addToCartClicked() {
            var form = $("#configform")[0];
            form.setAttribute("action", CPQ.core.getAddToCartUrl());
            CPQ.core.firePost(function () {
                form.submit();
            });
        }, 100);
    },

    generateAriaDescribedBy: function () {
        // for screenreader support link any form related message with the corresponsing form tag
        $(".cpq-error, .cpq-conflict, .cpq-warning, .cpq-messages").each(function () {
            var messageFieldId = $(this).attr('id');
            var csticId = CPQ.idhandler.getCsticIdFromFieldGeneric(messageFieldId);
            var selector = CPQ.core.encodeId(csticId + ".inputNum")
                + ", " + CPQ.core.encodeId(csticId + ".input")
               //+ ", " + CPQ.core.encodeId(csticId + ".radioGroup")
                + ", " + CPQ.core.encodeId(csticId + ".checkBoxList")
                + ", " + CPQ.core.encodeId(csticId + ".checkBox")
                + ", " + CPQ.core.encodeId(csticId + ".ddlb");
            var formElem = $(selector);
            if(!formElem[0]){
                var radioGroup = $(CPQ.core.encodeId(csticId + ".radioGroup"));
                formElem = radioGroup.find(":checked");
                if(!formElem[0]){
                    formElem = radioGroup.find(".cpq-csticValueSelect-first");
                }
            }
            var prevContent = formElem.attr('aria-describedby');
            var newContent = messageFieldId;
            if (prevContent) {
                newContent = prevContent + " " + newContent;
            }
            formElem.attr('aria-describedby', newContent);
        });
    }
};

$(document).ready(function () {
    if ($("#dynamicConfigContent").length > 0) {
        CPQ.core.pageType = "config";
        CPQ.core.formNameId = "#configform";
        CPQ.config.bindAll();
    }
});
