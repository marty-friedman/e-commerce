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
angular.module('itemTitleStrategyInterfaceModule', [])
    /**
     * @ngdoc service
     * @name itemStrategyInterfaceModule.service:ItemTitleStrategyInterface
     * @description
     * Interface describing the contract to retrieve an item in navigation editor.
     * It is used in the navigation editor to display the entry's item title, and it does that by finding 
     * the service handler by name ie. "'item' + itemTypeSelected + 'HandlerService'".
     */
    .factory('ItemTitleStrategyInterface', function() {

        var ItemTitleStrategyInterface = function() {};

        /**
         * @ngdoc method
         * @name itemStrategyInterfaceModule.service:ItemTitleStrategyInterface#getItemById
         * @methodOf itemStrategyInterfaceModule.service:ItemTitleStrategyInterface
         *
         * @description
         * Get the Item title for a given itemId by making a call to the cmswebservices item type API. 
         *
         * @param {String} itemId the item unique identifier
         * @param {Object} parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        ItemTitleStrategyInterface.prototype.getItemTitleById = function() {
            throw "getItemById is not implemented";
        };

        return ItemTitleStrategyInterface;
    });
