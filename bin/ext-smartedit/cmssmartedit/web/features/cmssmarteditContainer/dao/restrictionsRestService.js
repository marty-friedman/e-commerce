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
/**
 * @ngdoc overview
 * @name restrictionsRestServiceModule
 * @requires functionsModule
 * @requires smarteditServicesModule
 * @description
 * This module defines the {@link restrictionsRestServiceModule.service:restrictionsRestService restrictionsRestService} REST service for restrictions API.
 */

angular.module('restrictionsRestServiceModule', [
        'functionsModule',
        'smarteditServicesModule'
    ])

    /**
     * @ngdoc service
     * @name restrictionsRestServiceModule.service:restrictionsRestService
     * @requires languageService
     * @requires RESTRICTIONS_RESOURCE_URI
     * @requires restServiceFactory
     * @requires URIBuilder
     * @description
     * Service that handles REST requests for the restrictions CMS API endpoint.
     */
    .service('restrictionsRestService', function(
        languageService,
        RESTRICTIONS_RESOURCE_URI,
        restServiceFactory,
        URIBuilder
    ) {

        var restrictionsRestService = restServiceFactory.get(RESTRICTIONS_RESOURCE_URI);

        /**
         * @ngdoc method
         * @name restrictionsRestServiceModule.service:restrictionsRestService#get
         * @methodOf restrictionsRestServiceModule.service:restrictionsRestService
         * @param {Object} params Object containing parameters passed to the method.
         * @return {Array} An array of all restrictions in the system.
         */
        this.get = function(params) {
            return restrictionsRestService.get(params);
        };

        /**
         * @ngdoc method
         * @name restrictionsRestServiceModule.service:restrictionsRestService#getById
         * @methodOf restrictionsRestServiceModule.service:restrictionsRestService
         * @param {String} restrictionId Identifier for a given restriction.
         * @return {Object} The restriction matching the identifier passed as parameter.
         */
        this.getById = function getById(restrictionId) {
            return restrictionsRestService.getById(restrictionId).then(function(restriction) {
                return restriction;
            });
        };

        /**
         * @ngdoc method
         * @name restrictionsRestServiceModule.service:restrictionsRestService#getContentApiUri
         * @methodOf restrictionsRestServiceModule.service:restrictionsRestService
         * @param {Object} uriContext The {@link resourceLocationsModule.object:UriContext uriContext}, as defined on the resourceLocationModule.
         * @return {String} A URI for CRUD of restrictions on a specific site/catalog/version
         */
        this.getContentApiUri = function(uriContext) {
            return new URIBuilder(RESTRICTIONS_RESOURCE_URI).replaceParams(uriContext).build();
        };

    });
