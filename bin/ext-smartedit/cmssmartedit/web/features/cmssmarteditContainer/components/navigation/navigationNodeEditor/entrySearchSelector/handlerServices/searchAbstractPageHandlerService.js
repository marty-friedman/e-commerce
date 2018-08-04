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
angular.module("searchAbstractPageHandlerServiceModule", ['cmsitemsRestServiceModule', 'resourceLocationsModule', 'functionsModule', 'entrySearchStrategyInterfaceModule'])
    .factory("searchAbstractPageHandlerService", function($q, URIBuilder, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, restServiceFactory, cmsitemsUri, extend, EntrySearchStrategyInterface) {

        var searchAbstractPageHandlerService = function() {
            this.SEARCH_TEMPLATE = "itemSearchHandlerTemplate.html";
            this.PLACEHOLDER_KEY = 'se.cms.navigationmanagement.navnode.node.entry.dropdown.page.search';
            this.uriParameters = {};
        };
        searchAbstractPageHandlerService = extend(EntrySearchStrategyInterface, searchAbstractPageHandlerService);

        searchAbstractPageHandlerService.prototype._getItem = function(uid) {
            var restServiceItemsResource = restServiceFactory.get(new URIBuilder(cmsitemsUri).replaceParams(this.uriParameters).build());
            return restServiceItemsResource.get({
                mask: uid,
                pageSize: 10,
                currentPage: 0,
                catalogId: this.uriParameters[CONTEXT_CATALOG],
                catalogVersion: this.uriParameters[CONTEXT_CATALOG_VERSION],
                typeCode: 'AbstractPage',
                itemSearchParams: 'pageStatus:active'
            }).then(function(pages) {
                var page = pages.response[0];
                return {
                    name: page.name,
                    id: page.uid,
                    typeCode: page.typeCode
                };
            });
        };

        searchAbstractPageHandlerService.prototype._getPage = function(mask, pageSize, currentPage) {
            var restServiceItemsResource = restServiceFactory.get(new URIBuilder(cmsitemsUri).replaceParams(this.uriParameters).build());
            return restServiceItemsResource.get({
                mask: mask,
                pageSize: pageSize,
                currentPage: currentPage,
                catalogId: this.uriParameters[CONTEXT_CATALOG],
                catalogVersion: this.uriParameters[CONTEXT_CATALOG_VERSION],
                typeCode: 'AbstractPage',
                itemSearchParams: 'pageStatus:active',
                sort: 'name:asc'
            }).then(function(pages) {
                pages.results = pages.response.map(function(page) {
                    return {
                        name: page.name,
                        id: page.uid,
                        typeCode: page.typeCode
                    };
                });
                delete pages.response;
                return pages;
            });
        };

        searchAbstractPageHandlerService.prototype.getSearchDropdownProperties = function() {
            var properties = {
                templateUrl: this.SEARCH_TEMPLATE,
                placeHolderI18nKey: this.PLACEHOLDER_KEY,
                isPaged: true
            };
            return properties;
        };

        searchAbstractPageHandlerService.prototype.getPage = function(mask, pageSize, currentPage, parameters) {
            this.uriParameters = parameters;
            return this._getPage(mask, pageSize, currentPage);
        };

        searchAbstractPageHandlerService.prototype.getItem = function(uid, parameters) {
            this.uriParameters = parameters;
            return this._getItem(uid);
        };

        return new searchAbstractPageHandlerService();
    });
