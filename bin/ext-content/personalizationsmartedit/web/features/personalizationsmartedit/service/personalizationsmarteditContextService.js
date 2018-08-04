angular.module('personalizationsmarteditContextServiceModule', [
        'yjqueryModule',
        'personalizationsmarteditCommons',
        'contextualMenuServiceModule',
        'personalizationsmarteditContextUtilsModule'
    ])
    .factory('personalizationsmarteditContextService', function(yjQuery, personalizationsmarteditContextServiceReverseProxy, contextualMenuService, personalizationsmarteditContextUtils) {

        /*
         * Usage
         * When using ContextService objects do not use properties directly,
         * always use getter(to retrieve object) and setter(to set object),
         * so all synchronization functions which are in getters and setters are called.
         * Example:
         * var examplePersonalization = ContextService.getPersonalization();
         * examplePersonalization.enabled = true;
         * examplePersonalization.myComponents = ["component1", "component2"];
         * ContextService.setPersonalization(examplePersonalization);
         */

        var ContextService = personalizationsmarteditContextUtils.getContextObject();
        var ContextServiceReverseProxy = new personalizationsmarteditContextServiceReverseProxy('PersonalizationCtxReverseGateway');

        ContextService.getPersonalization = function() {
            return ContextService.personalization;
        };
        ContextService.setPersonalization = function(personalization) {
            ContextService.personalization = personalization;
            contextualMenuService.refreshMenuItems();
        };

        ContextService.getCustomize = function() {
            return ContextService.customize;
        };
        ContextService.setCustomize = function(customize) {
            ContextService.customize = customize;
            contextualMenuService.refreshMenuItems();
        };

        ContextService.getCombinedView = function() {
            return ContextService.combinedView;
        };
        ContextService.setCombinedView = function(combinedView) {
            ContextService.combinedView = combinedView;
            contextualMenuService.refreshMenuItems();
        };

        ContextService.getSeData = function() {
            return ContextService.seData;
        };
        ContextService.setSeData = function(seData) {
            ContextService.seData = seData;
        };

        ContextService.applySynchronization = function() {
            ContextServiceReverseProxy.applySynchronization();
        };

        ContextService.setPageId = function(newPageId) {
            ContextService.seData.pageId = newPageId;
            ContextServiceReverseProxy.setPageId(newPageId);
        };

        return ContextService;
    })
    .factory('personalizationsmarteditContextServiceProxy', function(gatewayProxy, personalizationsmarteditContextService) {
        var proxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        proxy.prototype.setPersonalization = function(newPersonalization) {
            personalizationsmarteditContextService.setPersonalization(newPersonalization);
        };
        proxy.prototype.setCustomize = function(newCustomize) {
            personalizationsmarteditContextService.setCustomize(newCustomize);
        };
        proxy.prototype.setCombinedView = function(newCombinedView) {
            personalizationsmarteditContextService.setCombinedView(newCombinedView);
        };
        proxy.prototype.setSeData = function(newSeData) {
            personalizationsmarteditContextService.setSeData(newSeData);
        };

        return new proxy('PersonalizationCtxGateway');
    })
    .factory('personalizationsmarteditContextServiceReverseProxy', function(gatewayProxy) {
        var reverseProxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };
        reverseProxy.prototype.applySynchronization = function() {};
        reverseProxy.prototype.setPageId = function() {};

        return reverseProxy;
    });
