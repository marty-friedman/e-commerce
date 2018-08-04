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
angular.module("itemTitleAbstractPageHandlerServiceModule", ['functionsModule', 'resourceModule', 'resourceLocationsModule', 'componentServiceModule', 'itemTitleStrategyInterfaceModule'])
    .factory("itemTitleAbstractPageHandlerService", function($q, extend, restServiceFactory, ItemTitleStrategyInterface, URIBuilder, PAGES_LIST_RESOURCE_URI) {

        var itemTitleAbstractPageHandlerService = function() {};
        itemTitleAbstractPageHandlerService = extend(ItemTitleStrategyInterface, itemTitleAbstractPageHandlerService);

        itemTitleAbstractPageHandlerService.prototype.getItemTitleById = function(itemId, uriContext) {

            var pagesUri = new URIBuilder(PAGES_LIST_RESOURCE_URI).replaceParams(uriContext).build();
            var restServiceItemsResource = restServiceFactory.get(pagesUri);
            return restServiceItemsResource.getById(itemId).then(function(response) {
                var itemInfo = {};
                itemInfo.itemType = response.typeCode;
                itemInfo.title = response.name;
                return itemInfo;
            });
        };

        return new itemTitleAbstractPageHandlerService();
    });
