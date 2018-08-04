angular.module('personalizationsmarteditContextServiceModule', [
        'sharedDataServiceModule',
        'loadConfigModule',
        'personalizationsmarteditContextUtilsModule'
    ])
    .factory('personalizationsmarteditContextService', function($q, personalizationsmarteditContextServiceProxy, sharedDataService, loadConfigManagerService, personalizationsmarteditContextUtils) {

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
        var ContextServiceProxy = new personalizationsmarteditContextServiceProxy('PersonalizationCtxGateway');

        ContextService.getPersonalization = function() {
            return ContextService.personalization;
        };
        ContextService.setPersonalization = function(personalization) {
            ContextService.personalization = personalization;
            ContextServiceProxy.setPersonalization(personalization);
        };

        ContextService.getCustomize = function() {
            return ContextService.customize;
        };
        ContextService.setCustomize = function(customize) {
            ContextService.customize = customize;
            ContextServiceProxy.setCustomize(customize);
        };

        ContextService.getCombinedView = function() {
            return ContextService.combinedView;
        };
        ContextService.setCombinedView = function(combinedView) {
            ContextService.combinedView = combinedView;
            ContextServiceProxy.setCombinedView(combinedView);
        };

        ContextService.getSeData = function() {
            return ContextService.seData;
        };
        ContextService.setSeData = function(seData) {
            ContextService.seData = seData;
            ContextServiceProxy.setSeData(seData);
        };

        ContextService.refreshExperienceData = function() {
            return sharedDataService.get('experience').then(function(data) {
                var seData = ContextService.getSeData();
                seData.seExperienceData = data;
                ContextService.setSeData(seData);
                return $q.when();
            });
        };

        ContextService.refreshPreviewData = function() {
            return sharedDataService.get('preview').then(function(data) {
                var seData = ContextService.getSeData();
                seData.sePreviewData = data;
                ContextService.setSeData(seData);
            });
        };

        ContextService.refreshConfigurationData = function() {
            loadConfigManagerService.loadAsObject().then(function(configurations) {
                var seData = ContextService.getSeData();
                seData.seConfigurationData = configurations;
                ContextService.setSeData(seData);
            });
        };

        ContextService.applySynchronization = function() {
            ContextServiceProxy.setPersonalization(ContextService.personalization);
            ContextServiceProxy.setCustomize(ContextService.customize);
            ContextServiceProxy.setCombinedView(ContextService.combinedView);
            ContextServiceProxy.setSeData(ContextService.seData);

            ContextService.refreshExperienceData();
            ContextService.refreshPreviewData();
            ContextService.refreshConfigurationData();
        };

        ContextService.getContexServiceProxy = function() {
            return ContextServiceProxy;
        };

        ContextService.customizeFiltersState = {};
        ContextService.getCustomizeFiltersState = function() {
            return ContextService.customizeFiltersState;
        };
        ContextService.setCustomizeFiltersState = function(filters) {
            ContextService.customizeFiltersState = filters;
        };

        return ContextService;
    })
    .factory('personalizationsmarteditContextServiceProxy', function(gatewayProxy) {
        var proxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        proxy.prototype.setPersonalization = function() {};
        proxy.prototype.setCustomize = function() {};
        proxy.prototype.setCombinedView = function() {};
        proxy.prototype.setSeData = function() {};

        return proxy;
    })
    .factory('personalizationsmarteditContextServiceReverseProxy', function(gatewayProxy, personalizationsmarteditContextService) {
        var reverseProxy = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        reverseProxy.prototype.applySynchronization = function() {
            personalizationsmarteditContextService.applySynchronization();
        };

        reverseProxy.prototype.setPageId = function(newPageId) {
            var seData = personalizationsmarteditContextService.getSeData();
            seData.pageId = newPageId;
            personalizationsmarteditContextService.setSeData(seData);
        };

        return new reverseProxy('PersonalizationCtxReverseGateway');
    });
