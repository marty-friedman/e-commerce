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
angular.module('uriDropdownPopulatorModule', ['dropdownPopulatorInterfaceModule', 'smarteditServicesModule', 'yLoDashModule', 'functionsModule'])
    .factory('UriDropdownPopulatorFactory', function($q, lodash, DropdownPopulatorInterface, extend, restServiceFactory, getDataFromResponse, getKeyHoldingDataFromResponse) {

        var UriDropdownPopulatorFactory = function() {};

        UriDropdownPopulatorFactory = extend(DropdownPopulatorInterface, UriDropdownPopulatorFactory);

        UriDropdownPopulatorFactory.prototype._buildQueryParams = function(dependsOn, model) {
            var queryParams = dependsOn.split(",").reduce(function(obj, current) {
                obj[current] = model[current];
                return obj;
            }, {});

            return queryParams;
        };

        /**
         * @ngdoc method
         * @name uriDropdownPopulatorModule.service:uriDropdownPopulator#fetchAll
         * @methodOf uriDropdownPopulatorModule.service:uriDropdownPopulator
         *
         * @description
         * Implementation of the {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchAll DropdownPopulatorInterface.fetchAll} method
         */
        UriDropdownPopulatorFactory.prototype.fetchAll = function(payload) {

            var params;

            if (payload.field.dependsOn) {
                params = this._buildQueryParams(payload.field.dependsOn, payload.model);
            }

            return restServiceFactory.get(payload.field.uri).get(params).then(function(response) {
                var dataFromResponse = getDataFromResponse(response);
                var options = this.populateAttributes(dataFromResponse, payload.field.idAttribute, payload.field.labelAttributes);

                if (payload.search) {
                    options = this.search(options, payload.search);
                }

                return $q.when(options);
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name uriDropdownPopulatorModule.service:uriDropdownPopulator#fetchPage
         * @methodOf uriDropdownPopulatorModule.service:uriDropdownPopulator
         *
         * @description
         * Implementation of the {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchPage DropdownPopulatorInterface.fetchPage} method
         */
        UriDropdownPopulatorFactory.prototype.fetchPage = function(payload) {

            var params = {};

            if (payload.field.dependsOn) {
                params = this._buildQueryParams(payload.field.dependsOn, payload.model);
            }

            params.pageSize = payload.pageSize;
            params.currentPage = payload.currentPage;
            params.mask = payload.search;

            if (payload.field.params) {
                lodash.extend(params, payload.field.params);
            }

            return restServiceFactory.get(payload.field.uri).get(params).then(function(response) {
                var key = getKeyHoldingDataFromResponse(response);
                response[key] = this.populateAttributes(response[key], payload.field.idAttribute, payload.field.labelAttributes);

                return $q.when(response);
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name uriDropdownPopulatorModule.service:uriDropdownPopulator#getItem
         * @methodOf uriDropdownPopulatorModule.service:uriDropdownPopulator
         *
         * @description
         * Implementation of the {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#getItem DropdownPopulatorInterface.getItem} method
         *
         * @param {Object} payload The payload object containing the uri and other options
         * @param {String} payload.id The id of the item to fetch
         * @param {String} payload.field.uri The uri used to make a rest call to fetch data
         * @param {String} [payload.field.dependsOn=null] A comma separated list of attributes to include from the model when building the request params
         * @param {String} [payload.field.idAttribute=id] The name of the attribute to use when setting the id attribute
         * @param {String} [payload.field.labelAttributes=label] A list of attributes to use when setting the label attribute
         * @param {String} [payload.model=null] The model used when building query params on attributes defined in payload.field.dependsOn
         * 
         * @returns {Promise} A promise that resolves to the option that was fetched
         */
        UriDropdownPopulatorFactory.prototype.getItem = function(payload) {
            return restServiceFactory.get(payload.field.uri).getById(payload.id).then(function(item) {
                item = this.populateAttributes([item], payload.field.idAttribute, payload.field.labelAttributes)[0];

                return $q.when(item);
            }.bind(this));
        };

        return UriDropdownPopulatorFactory;
    })
    /**
     * @ngdoc service
     * @name uriDropdownPopulatorModule.service:uriDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
     * containing uri attribute.
     */
    .factory('uriDropdownPopulator', function(UriDropdownPopulatorFactory) {
        return new UriDropdownPopulatorFactory();
    });
