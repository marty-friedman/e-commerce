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
angular.module("itemTitleMediaHandlerServiceModule", ['resourceModule', 'functionsModule', 'resourceLocationsModule', 'functionsModule', 'itemTitleStrategyInterfaceModule'])
    .factory("itemTitleMediaHandlerService", function(extend, URIBuilder, MEDIA_RESOURCE_URI, ItemTitleStrategyInterface, restServiceFactory) {

        var itemTitleMediaHandlerService = function() {
            this.uriParameters = {};
        };
        itemTitleMediaHandlerService = extend(ItemTitleStrategyInterface, itemTitleMediaHandlerService);


        itemTitleMediaHandlerService.prototype.getItemTitleById = function(itemId, uriParameters) {
            var mediaUri = new URIBuilder(MEDIA_RESOURCE_URI + '/' + itemId).replaceParams(uriParameters).build();

            var restServiceMediaSearchResource = restServiceFactory.get(mediaUri);
            return restServiceMediaSearchResource.get().then(function(response) {
                var itemInfo = {};
                itemInfo.itemType = "Media";
                itemInfo.title = response.code;
                return itemInfo;
            });
        };

        return new itemTitleMediaHandlerService();
    });
