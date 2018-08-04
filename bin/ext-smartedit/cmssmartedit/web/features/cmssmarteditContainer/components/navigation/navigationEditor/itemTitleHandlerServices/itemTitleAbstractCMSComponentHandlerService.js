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
angular.module("itemTitleAbstractCMSComponentHandlerServiceModule", ['resourceModule', 'functionsModule', 'resourceLocationsModule', 'componentServiceModule', 'itemTitleStrategyInterfaceModule'])
    .factory("itemTitleAbstractCMSComponentHandlerService", function($q, extend, ITEMS_RESOURCE_URI, URIBuilder, restServiceFactory, ItemTitleStrategyInterface) {

        var itemTitleAbstractCMSComponentHandlerService = function() {};
        itemTitleAbstractCMSComponentHandlerService = extend(ItemTitleStrategyInterface, itemTitleAbstractCMSComponentHandlerService);

        itemTitleAbstractCMSComponentHandlerService.prototype.getItemTitleById = function(itemId, uriParameters) {

            var itemsUri = new URIBuilder(ITEMS_RESOURCE_URI).replaceParams(uriParameters).build();
            var restServiceItemsResource = restServiceFactory.get(itemsUri);
            return restServiceItemsResource.getById(itemId).then(function(response) {
                var itemInfo = {};
                itemInfo.itemType = response.typeCode;
                if (response.typeCode === "CMSLinkComponent") {
                    itemInfo.title = response.linkName;
                    return itemInfo;
                } else {
                    itemInfo.title = response.name;
                    return itemInfo;
                }
            });
        };

        return new itemTitleAbstractCMSComponentHandlerService();
    });
