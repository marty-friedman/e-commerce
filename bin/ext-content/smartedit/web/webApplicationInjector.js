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
var smartEditBootstrapGatewayId = 'smartEditBootstrap';
parent.postMessage({
    pk: Math.random(),
    gatewayId: smartEditBootstrapGatewayId,
    eventId: 'loading',
    data: {
        location: document.location.href
    }
}, '*');

(function heartBeat() {
    var HEART_BEAT_PERIOD = 500; // 0.5 seconds
    var HEART_BEAT_GATEWAY_ID = "heartBeatGateway";
    var HEART_BEAT_MSG_ID = 'heartBeat';

    parent.postMessage({
        pk: Math.random(),
        gatewayId: HEART_BEAT_GATEWAY_ID,
        eventId: HEART_BEAT_MSG_ID,
        data: {
            location: document.location.href
        }
    }, '*');
    setTimeout(heartBeat, HEART_BEAT_PERIOD);
})();


window.addEventListener("load", function(event) {

    parent.postMessage({
        pk: Math.random(),
        gatewayId: smartEditBootstrapGatewayId,
        eventId: 'bootstrapSmartEdit',
        data: {
            location: document.location.href
        }
    }, '*');
});

var injectJS = function(head, srcs, index) {
    if (index < srcs.length) {
        $script(srcs[index], function() {
            injectJS(head, srcs, index + 1);
        });
    }
};

var injectCSS = function(head, cssPaths, index) {

    var link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = cssPaths[index];
    head.appendChild(link);

    if (index + 1 < cssPaths.length) {
        injectCSS(head, cssPaths, index + 1);
    }
};

// Listen to message from child window
window.addEventListener("message", function(e) {

    //	var originControl = '127.0.0.1:7000';
    //
    //	if(e.origin.indexOf(originControl)==-1){
    //		throw e.origin+" is not allowed to override this storefront";
    //	}
    var event = e.data;
    if (event.gatewayId === smartEditBootstrapGatewayId && event.eventId === 'bundle') {

        var data = event.data;

        window.smartedit = window.smartedit || {};
        if (data.resources && data.resources.properties) {
            for (var i in data.resources.properties) {
                window.smartedit[i] = data.resources.properties[i];
            }
        }

        var head = document.getElementsByTagName("head")[0];

        //JS Files
        if (data.resources && data.resources.js && data.resources.js.length > 0) {
            injectJS(head, data.resources.js, 0);
        }

        //CSS Files
        if (data.resources && data.resources.css && data.resources.css.length > 0) {
            injectCSS(head, data.resources.css, 0);
        }
    }

}, false);
