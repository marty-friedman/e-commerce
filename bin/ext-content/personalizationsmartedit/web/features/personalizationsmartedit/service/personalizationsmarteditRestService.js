angular.module('personalizationsmarteditRestServiceModule', [
        'smarteditServicesModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule'
    ])
    .factory('personalizationsmarteditRestService', function(restServiceFactory, personalizationsmarteditUtils, personalizationsmarteditContextService) {

        var ACTIONS_DETAILS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions";

        var extendRequestParamObjWithCatalogAwarePathVariables = function(requestParam, catalogAware) {
            catalogAware = catalogAware || {};
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
            var catalogAwareParams = {
                catalogId: catalogAware.catalog || experienceData.catalogDescriptor.catalogId,
                catalogVersion: catalogAware.catalogVersion || experienceData.catalogDescriptor.catalogVersion
            };
            requestParam = angular.extend(requestParam, catalogAwareParams);
            return requestParam;
        };

        var restService = {};

        restService.getCxCmsAllActionsForContainer = function(containerId, filter) {
            filter = filter || {};
            var restService = restServiceFactory.get(ACTIONS_DETAILS);
            var requestParams = {
                type: "CXCMSACTION",
                customizationStatus: "ENABLED",
                variationStatus: "ENABLED",
                catalogs: "ALL",
                needsTotal: true,
                containerId: containerId
            };
            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams);

            requestParams.pageSize = filter.currentSize || 25;
            requestParams.currentPage = filter.currentPage || 0;

            return restService.get(requestParams);
        };

        return restService;
    });
