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
angular.module("searchAbstractCMSComponentHandlerServiceModule", ['functionsModule', 'resourceModule', 'resourceLocationsModule', 'componentServiceModule', 'entrySearchStrategyInterfaceModule'])
    .factory("searchAbstractCMSComponentHandlerService", function($q, extend, ITEMS_RESOURCE_URI, restServiceFactory, EntrySearchStrategyInterface, URIBuilder) {

        var searchAbstractCMSComponentHandlerService = function() {
            this.SEARCH_TEMPLATE = "itemSearchHandlerTemplate.html";
            this.PLACEHOLDER_KEY = 'se.cms.navigationmanagement.navnode.node.entry.dropdown.component.search';
            this.uriParameters = {};
        };
        searchAbstractCMSComponentHandlerService = extend(EntrySearchStrategyInterface, searchAbstractCMSComponentHandlerService);

        searchAbstractCMSComponentHandlerService.prototype._getItem = function(id) {
            var restServiceItemsResource = restServiceFactory.get(new URIBuilder(ITEMS_RESOURCE_URI).replaceParams(this.uriParameters).build());
            return restServiceItemsResource.getById(id).then(function(response) {
                return {
                    name: response.name,
                    id: response.uid,
                    typeCode: response.typeCode
                };
            });
        };

        searchAbstractCMSComponentHandlerService.prototype._getComponents = function() {
            return this._getPage().then(function(response) {
                return response.results;
            });
        };

        searchAbstractCMSComponentHandlerService.prototype._getPage = function(mask, pageSize, currentPage) {
            var restServiceItemsResource = restServiceFactory.get(new URIBuilder(ITEMS_RESOURCE_URI).replaceParams(this.uriParameters).build());
            return restServiceItemsResource.get({
                mask: mask,
                pageSize: pageSize,
                currentPage: currentPage
            }).then(function(response) {
                response.results = response.componentItems.map(function(component) {
                    return {
                        name: component.name,
                        id: component.uid,
                        typeCode: component.typeCode
                    };
                });
                delete response.componentItems;
                return response;
            });
        };

        searchAbstractCMSComponentHandlerService.prototype.getSearchDropdownProperties = function() {
            var properties = {
                templateUrl: this.SEARCH_TEMPLATE,
                placeHolderI18nKey: this.PLACEHOLDER_KEY,
                isPaged: true
            };
            return properties;
        };

        searchAbstractCMSComponentHandlerService.prototype.getSearchResults = function(mask, parameters) {
            this.uriParameters = parameters;
            return this._getComponents();
        };

        searchAbstractCMSComponentHandlerService.prototype.getPage = function(mask, pageSize, currentPage, parameters) {
            this.uriParameters = parameters;
            return this._getPage(mask, pageSize, currentPage);
        };

        searchAbstractCMSComponentHandlerService.prototype.getItem = function(id, parameters) {
            this.uriParameters = parameters;
            return this._getItem(id);
        };


        return new searchAbstractCMSComponentHandlerService();
    });
