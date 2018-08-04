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
angular.module('entrySearchStrategyInterfaceModule', [])
    /**
     * @ngdoc service
     * @name entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface
     * @description
     * Interface describing the contract for entry search in navigation node editor and is part of strategy for the search of EditableDropdown widget which is configured by name.
     * For example when we select the value from the ITEMTYPE dropdown
     * then it tries to find the service handler by name ie. "'search' + itemTypeSelected from Dropdown + 'HandlerService'".
     * This service manage the search for components in the dropdown during the creation of navigation entry.
     */
    .factory('EntrySearchStrategyInterface', function() {

        var EntrySearchStrategyInterface = function() {};

        /**
         * @ngdoc method
         * @name entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface#getSearchDropdownProperties
         * @methodOf entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface
         *
         * @description
         * This method provides search result render properties. It returns a object which has a templateURL required to render search choices and placeholder key for the dropdown.
         *
         */
        EntrySearchStrategyInterface.prototype.getSearchDropdownProperties = function() {
            throw "getSearchDropdownProperties is not implemented";
        };

        /**
         * @ngdoc method
         * @name entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface#getItem
         * @methodOf entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface
         *
         * @description
         * Fetch an item identified by the given id by making a REST call to the appropriate item end point.
         *
         * @param {String} id the item identifier
         * @param {Object} parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        EntrySearchStrategyInterface.prototype.getItem = function() {
            throw "getItem is not implemented";
        };

        /**
         * @ngdoc method
         * @name entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface#getSearchResults
         * @methodOf entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface
         *
         * @description
         * Fetch the search results by making a REST call to the appropriate item end point.
         *
         * @param {String} mask for filtering the search
         * @param {Object} parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        EntrySearchStrategyInterface.prototype.getSearchResults = function() {};

        /**
         * @ngdoc method
         * @name entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface#getPage
         * @methodOf entrySearchStrategyInterfaceModule.service:EntrySearchStrategyInterface
         *
         * @description
         * Fetch paged search results by making a REST call to the appropriate item end point.
         * Must return a Page of type Page as per SmartEdit documentation
         * @param {String} mask for filtering the search
         * @param {String} pageSize number of items in the page
         * @param {String} currentPage current page number
         * @param {Object} parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations
         */
        EntrySearchStrategyInterface.prototype.getPage = function() {};

        return EntrySearchStrategyInterface;
    });
