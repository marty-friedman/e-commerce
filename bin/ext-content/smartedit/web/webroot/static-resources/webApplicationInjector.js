/*!
  * $script.js JS loader & dependency manager
  * https://github.com/ded/script.js
  * (c) Dustin Diaz 2014 | License MIT
  */
!function(t,e){"undefined"!=typeof module&&module.exports?module.exports=e():"function"==typeof define&&define.amd?define(e):this.$script=e()}(0,function(){function t(t,e){for(var n=0,o=t.length;n<o;++n)if(!e(t[n]))return c;return 1}function e(e,n){t(e,function(t){return!n(t)})}function n(a,s,i){function c(t){return t.call?t():l[t]}function u(){if(!--w){l[v]=1,g&&g();for(var n in m)t(n.split("|"),c)&&!e(m[n],c)&&(m[n]=[])}}a=a[d]?a:[a];var f=s&&s.call,g=f?s:i,v=f?a.join(""):s,w=a.length;return setTimeout(function(){e(a,function t(e,n){return null===e?u():(!n&&!/^https?:\/\//.test(e)&&r&&(e=-1===e.indexOf(".js")?r+e+".js":r+e),h[e]?(v&&(p[v]=1),2==h[e]?u():setTimeout(function(){t(e,!0)},0)):(h[e]=1,v&&(p[v]=1),void o(e,u)))})},0),n}function o(t,e){var n,o=s.createElement("script");o.onload=o.onerror=o[f]=function(){o[u]&&!/^c|loade/.test(o[u])||n||(o.onload=o[f]=null,n=1,h[t]=2,e())},o.async=1,o.src=a?t+(-1===t.indexOf("?")?"?":"&")+a:t,i.insertBefore(o,i.lastChild)}var r,a,s=document,i=s.getElementsByTagName("head")[0],c=!1,d="push",u="readyState",f="onreadystatechange",l={},p={},m={},h={};return n.get=o,n.order=function(t,e,o){!function r(a){a=t.shift(),t.length?n(a,r):n(a,e,o)}()},n.path=function(t){r=t},n.urlArgs=function(t){a=t},n.ready=function(o,r,a){var s=[];return!e(o=o[d]?o:[o],function(t){l[t]||s[d](t)})&&t(o,function(t){return l[t]})?r():function(t){m[t]=m[t]||[],m[t][d](r),a&&a(s)}(o.join("|")),n},n.done=function(t){n([null],t)},n});/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
var smartEditBootstrapGatewayId="smartEditBootstrap";parent.postMessage({pk:Math.random(),gatewayId:smartEditBootstrapGatewayId,eventId:"loading",data:{location:document.location.href}},"*"),function t(){parent.postMessage({pk:Math.random(),gatewayId:"heartBeatGateway",eventId:"heartBeat",data:{location:document.location.href}},"*"),setTimeout(t,500)}(),window.addEventListener("load",function(t){parent.postMessage({pk:Math.random(),gatewayId:smartEditBootstrapGatewayId,eventId:"bootstrapSmartEdit",data:{location:document.location.href}},"*")});var injectJS=function(t,e,n){n<e.length&&$script(e[n],function(){injectJS(t,e,n+1)})},injectCSS=function(t,e,n){var o=document.createElement("link");o.rel="stylesheet",o.href=e[n],t.appendChild(o),n+1<e.length&&injectCSS(t,e,n+1)};
// Listen to message from child window
window.addEventListener("message",function(t){
//	var originControl = '127.0.0.1:7000';
//
//	if(e.origin.indexOf(originControl)==-1){
//		throw e.origin+" is not allowed to override this storefront";
//	}
var e=t.data;if(e.gatewayId===smartEditBootstrapGatewayId&&"bundle"===e.eventId){var n=e.data;if(window.smartedit=window.smartedit||{},n.resources&&n.resources.properties)for(var o in n.resources.properties)window.smartedit[o]=n.resources.properties[o];var r=document.getElementsByTagName("head")[0];
//JS Files
n.resources&&n.resources.js&&n.resources.js.length>0&&injectJS(r,n.resources.js,0),
//CSS Files
n.resources&&n.resources.css&&n.resources.css.length>0&&injectCSS(r,n.resources.css,0)}},!1);