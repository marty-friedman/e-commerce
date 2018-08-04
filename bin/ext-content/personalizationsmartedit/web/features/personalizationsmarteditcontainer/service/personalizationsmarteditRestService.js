angular.module('personalizationsmarteditRestServiceModule', [
        'smarteditServicesModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule',
        'yjqueryModule'
    ])
    .factory('personalizationsmarteditRestService', function(restServiceFactory, personalizationsmarteditUtils, personalizationsmarteditContextService, $http, $q, yjQuery) {

        var CUSTOMIZATIONS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizations";
        var CUSTOMIZATION = CUSTOMIZATIONS + "/:customizationCode";

        var CUSTOMIZATION_PACKAGES = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/customizationpackages";
        var CUSTOMIZATION_PACKAGE = CUSTOMIZATION_PACKAGES + "/:customizationCode";

        var ACTIONS_DETAILS = "/personalizationwebservices/v1/catalogs/:catalogId/catalogVersions/:catalogVersion/actions";

        var VARIATIONS = CUSTOMIZATION + "/variations";
        var VARIATION = VARIATIONS + "/:variationCode";

        var ACTIONS = VARIATION + "/actions";
        var ACTION = ACTIONS + "/:actionId";

        var CXCMSC_ACTIONS_FROM_VARIATIONS = "/personalizationwebservices/v1/query/cxcmscomponentsfromvariations";

        var PREVIEWTICKET = "/previewwebservices/v1/preview/:ticketId";
        var SEGMENTS = "/personalizationwebservices/v1/segments";

        var CATALOGS = "/cmswebservices/v1/sites/:siteId/cmsitems";
        var CATALOG = CATALOGS + "/:itemUuid";

        var ADD_CONTAINER = "/personalizationwebservices/v1/query/cxReplaceComponentWithContainer";

        var COMPONENT_TYPES = '/cmswebservices/v1/types?category=COMPONENT';

        var UPDATE_CUSTOMIZATION_RANK = "/personalizationwebservices/v1/query/cxUpdateCustomizationRank";

        var VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS = "variations(active,actions,enabled,code,name,rank,status,catalog,catalogVersion)";

        var FULL_FIELDS = "FULL";

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

        var extendRequestParamObjWithCustomizatonCode = function(requestParam, customizatiodCode) {
            var customizationCodeParam = {
                customizationCode: customizatiodCode
            };
            requestParam = angular.extend(requestParam, customizationCodeParam);
            return requestParam;
        };

        var extendRequestParamObjWithVariationCode = function(requestParam, variationCode) {
            var param = {
                variationCode: variationCode
            };
            requestParam = angular.extend(requestParam, param);
            return requestParam;
        };

        var getParamsAction = function(oldComponentId, newComponentId, slotId, containerId, customizationId, variationId) {
            var entries = [];
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "oldComponentId", oldComponentId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "newComponentId", newComponentId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "slotId", slotId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "containerId", containerId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variationId", variationId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customizationId", customizationId);
            return {
                "params": {
                    "entry": entries
                }
            };
        };

        var getPathVariablesObjForModifyingActionURI = function(customizationId, variationId, actionId, filter) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
            filter = filter || {};
            return {
                customizationCode: customizationId,
                variationCode: variationId,
                actionId: actionId,
                catalogId: filter.catalog || experienceData.catalogDescriptor.catalogId,
                catalogVersion: filter.catalogVersion || experienceData.catalogDescriptor.catalogVersion
            };
        };

        var prepareURI = function(uri, pathVariables) {
            return uri.replace(/((?:\:)(\w*)(?:\/))/g, function(match, p1, p2) {
                return pathVariables[p2] + "/";
            });
        };

        var getParamsForCustomizations = function(filter) {
            return {
                code: angular.isDefined(filter.code) ? filter.code : undefined,
                pageId: angular.isDefined(filter.pageId) ? filter.pageId : undefined,
                pageCatalogId: angular.isDefined(filter.pageCatalogId) ? filter.pageCatalogId : undefined,
                name: angular.isDefined(filter.name) ? filter.name : undefined,
                negatePageId: angular.isDefined(filter.negatePageId) ? filter.negatePageId : undefined,
                catalogs: angular.isDefined(filter.catalogs) ? filter.catalogs : undefined,
                statuses: angular.isDefined(filter.statuses) ? filter.statuses.join(',') : undefined
            };
        };

        var getActionsDetails = function(filter) {
            var restService = restServiceFactory.get(ACTIONS_DETAILS);
            filter = extendRequestParamObjWithCatalogAwarePathVariables(filter);
            return restService.get(filter);
        };

        var restService = {};

        restService.getCustomizations = function(filter) {
            filter = filter || {};

            var restService = restServiceFactory.get(CUSTOMIZATIONS);
            var requestParams = {};

            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);

            requestParams.pageSize = filter.currentSize || 10;
            requestParams.currentPage = filter.currentPage || 0;

            yjQuery.extend(requestParams, getParamsForCustomizations(filter));

            return restService.get(requestParams);
        };

        restService.getComponenentsIdsForVariation = function(customizationId, variationId, catalog, catalogVersion) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;

            var restService = restServiceFactory.get(CXCMSC_ACTIONS_FROM_VARIATIONS);
            var entries = [];
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", customizationId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "variations", variationId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", catalog || experienceData.catalogDescriptor.catalogId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", catalogVersion || experienceData.catalogDescriptor.catalogVersion);
            var requestParams = {
                "params": {
                    "entry": entries
                }
            };
            return restService.save(requestParams);
        };

        restService.getCxCmsActionsOnPageForCustomization = function(customization, currentPage) {
            var filter = {
                type: "CXCMSACTION",
                catalogs: "ALL",
                pageId: personalizationsmarteditContextService.getSeData().pageId,
                pageCatalogId: personalizationsmarteditContextService.getSeData().seExperienceData.pageContext.catalogId,
                customizationCode: customization.code || "",
                currentPage: currentPage || 0
            };

            return getActionsDetails(filter);
        };

        restService.getPreviewTicket = function(previewTicketId) {
            var previewTicketData = personalizationsmarteditContextService.getSeData().sePreviewData;
            var restService = restServiceFactory.get(PREVIEWTICKET, "ticketId");
            var previewTicket = {
                ticketId: previewTicketId || previewTicketData.previewTicketId
            };
            return restService.get(previewTicket);
        };

        restService.updatePreviewTicket = function(previewTicket) {
            var restService = restServiceFactory.get(PREVIEWTICKET, "ticketId");
            return restService.update(previewTicket);
        };

        restService.createPreviewTicket = function(previewTicket) {
            var previewRESTService = restServiceFactory.get(PREVIEWTICKET);
            return previewRESTService.save(previewTicket);
        };

        restService.getSegments = function(filter) {
            var restService = restServiceFactory.get(SEGMENTS);
            return restService.get(filter);
        };

        restService.getCustomization = function(filter) {
            var restService = restServiceFactory.get(CUSTOMIZATION, "customizationCode");

            var requestParams = extendRequestParamObjWithCustomizatonCode({}, filter.code);
            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter);

            return restService.get(requestParams);
        };

        restService.createCustomization = function(customization) {
            var restService = restServiceFactory.get(CUSTOMIZATION_PACKAGES);

            return restService.save(extendRequestParamObjWithCatalogAwarePathVariables(customization));
        };

        restService.updateCustomization = function(customization) {
            var restService = restServiceFactory.get(CUSTOMIZATION, "customizationCode");
            customization.customizationCode = customization.code;
            return restService.update(extendRequestParamObjWithCatalogAwarePathVariables(customization));
        };

        restService.updateCustomizationPackage = function(customization) {
            var restService = restServiceFactory.get(CUSTOMIZATION_PACKAGE, "customizationCode");
            customization.customizationCode = customization.code;
            return restService.update(extendRequestParamObjWithCatalogAwarePathVariables(customization));
        };

        restService.deleteCustomization = function(customizationCode) {
            var restService = restServiceFactory.get(CUSTOMIZATION, "customizationCode");

            var requestParams = {
                customizationCode: customizationCode
            };

            return restService.remove(extendRequestParamObjWithCatalogAwarePathVariables(requestParams));
        };

        restService.getVariation = function(customizationCode, variationCode) {
            var restService = restServiceFactory.get(VARIATION, "variationCode");

            var requestParams = extendRequestParamObjWithVariationCode({}, variationCode);
            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
            requestParams = extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

            return restService.get(requestParams);
        };

        restService.editVariation = function(customizationCode, variation) {
            var restService = restServiceFactory.get(VARIATION, "variationCode");

            variation = extendRequestParamObjWithCatalogAwarePathVariables(variation);
            variation = extendRequestParamObjWithCustomizatonCode(variation, customizationCode);
            variation.variationCode = variation.code;
            return restService.update(variation);
        };

        restService.deleteVariation = function(customizationCode, variationCode) {
            var restService = restServiceFactory.get(VARIATION, "variationCode");

            var requestParams = extendRequestParamObjWithVariationCode({}, variationCode);
            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams);
            requestParams = extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

            return restService.remove(requestParams);
        };

        restService.createVariationForCustomization = function(customizationCode, variation) {
            var restService = restServiceFactory.get(VARIATIONS);

            variation = extendRequestParamObjWithCatalogAwarePathVariables(variation);
            variation = extendRequestParamObjWithCustomizatonCode(variation, customizationCode);

            return restService.save(variation);
        };

        restService.getVariationsForCustomization = function(customizationCode, filter) {
            var restService = restServiceFactory.get(VARIATIONS);

            var requestParams = {};
            var varForCustFilter = filter || {};

            requestParams = extendRequestParamObjWithCatalogAwarePathVariables(requestParams, varForCustFilter);
            requestParams = extendRequestParamObjWithCustomizatonCode(requestParams, customizationCode);

            requestParams.fields = VARIATION_FOR_CUSTOMIZATION_DEFAULT_FIELDS;

            var includeFullFields = typeof varForCustFilter.includeFullFields === "undefined" ? false : varForCustFilter.includeFullFields;

            if (includeFullFields) {
                requestParams.fields = FULL_FIELDS;
            }

            return restService.get(requestParams);
        };

        restService.replaceComponentWithContainer = function(componentId, slotId, filter) {
            var restService = restServiceFactory.get(ADD_CONTAINER);
            var catalogParams = extendRequestParamObjWithCatalogAwarePathVariables({}, filter);
            var requestParams = getParamsAction(componentId, null, slotId, null, null, null);
            personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "catalog", catalogParams.catalogId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "catalogVersion", catalogParams.catalogVersion);
            personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "slotCatalog", filter.slotCatalog);
            personalizationsmarteditUtils.pushToArrayIfValueExists(requestParams.params.entry, "oldComponentCatalog", filter.oldComponentCatalog);

            return restService.save(requestParams);
        };

        restService.getActions = function(customizationId, variationId, filter) {
            var restService = restServiceFactory.get(ACTIONS);
            var pathVariables = getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);

            var requestParams = {
                "fields": FULL_FIELDS
            };
            requestParams = angular.extend(requestParams, pathVariables);

            return restService.get(requestParams);
        };

        restService.createActions = function(customizationId, variationId, data, filter) {

            var pathVariables = getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
            var url = prepareURI(ACTIONS, pathVariables);

            return $http({
                url: url,
                method: 'PATCH',
                data: data,
                headers: {
                    "Content-Type": "application/json;charset=utf-8"
                }
            });
        };

        restService.addActionToContainer = function(componentId, catalogId, containerId, customizationId, variationId, filter) {
            var restService = restServiceFactory.get(ACTIONS);
            var pathVariables = getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
            var requestParams = {
                "type": "cxCmsActionData",
                "containerId": containerId,
                "componentId": componentId,
                "componentCatalog": catalogId
            };
            requestParams = angular.extend(requestParams, pathVariables);
            return restService.save(requestParams);
        };

        restService.editAction = function(customizationId, variationId, actionId, newComponentId, newComponentCatalog, filter) {
            var restService = restServiceFactory.get(ACTION, "actionId");

            var requestParams = getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);

            return restService.get(requestParams).then(function successCallback(actionInfo) {
                actionInfo = angular.extend(actionInfo, requestParams);
                actionInfo.componentId = newComponentId;
                actionInfo.componentCatalog = newComponentCatalog;
                return restService.update(actionInfo);
            });
        };

        restService.deleteAction = function(customizationId, variationId, actionId, filter) {
            var restService = restServiceFactory.get(ACTION, "actionId");

            var requestParams = getPathVariablesObjForModifyingActionURI(customizationId, variationId, actionId, filter);

            return restService.remove(requestParams);
        };

        restService.deleteActions = function(customizationId, variationId, actionIds, filter) {
            var pathVariables = getPathVariablesObjForModifyingActionURI(customizationId, variationId, undefined, filter);
            var url = prepareURI(ACTIONS, pathVariables);

            return $http({
                url: url,
                method: 'DELETE',
                data: actionIds,
                headers: {
                    "Content-Type": "application/json;charset=utf-8"
                }
            });
        };

        restService.getComponents = function(filter) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
            var restService = restServiceFactory.get(CATALOGS);
            var requestParams = {
                siteId: experienceData.siteDescriptor.uid
            };
            requestParams = angular.extend(requestParams, filter);

            return restService.get(extendRequestParamObjWithCatalogAwarePathVariables(requestParams, filter));
        };

        restService.getComponent = function(itemUuid) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
            var restService = restServiceFactory.get(CATALOG, "itemUuid");
            var requestParams = {
                itemUuid: itemUuid,
                siteId: experienceData.siteDescriptor.uid
            };

            return restService.get(requestParams);
        };

        restService.getNewComponentTypes = function() {
            var restService = restServiceFactory.get(COMPONENT_TYPES);
            return restService.get();
        };

        restService.updateCustomizationRank = function(customizationId, icreaseValue) {
            var experienceData = personalizationsmarteditContextService.getSeData().seExperienceData;
            var restService = restServiceFactory.get(UPDATE_CUSTOMIZATION_RANK);
            var entries = [];
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "customization", customizationId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "increaseValue", icreaseValue);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog", experienceData.catalogDescriptor.catalogId);
            personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalogVersion", experienceData.catalogDescriptor.catalogVersion);
            var requestParams = {
                "params": {
                    "entry": entries
                }
            };
            return restService.save(requestParams);
        };

        return restService;
    });
