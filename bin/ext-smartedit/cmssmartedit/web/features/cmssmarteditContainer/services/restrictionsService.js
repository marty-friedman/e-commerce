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
 * @name restrictionsServiceModule
 * @requires restrictionsRestServiceModule
 * @requires structuresRestServiceModule
 * @requires typeStructureRestServiceModule
 * @description
 * This module provides the {@link restrictionsServiceModule.service:restrictionsService restrictionsService} service used to consolidate business logic for SAP Hybris platform CMS restrictions.
 */
angular.module('restrictionsServiceModule', [
        'restrictionsRestServiceModule',
        'structuresRestServiceModule',
        'typeStructureRestServiceModule',
        'yLoDashModule'
    ])

    /**
     * @ngdoc service
     * @name restrictionsServiceModule.service:restrictionsService
     * @requires restrictionsRestService
     * @requires structureModeManagerFactory
     * @requires structuresRestService
     * @description
     * Service that concerns business logic tasks related to CMS restrictions in the SAP Hybris platform.
     */
    .service('restrictionsService', function(
        lodash,
        restrictionsRestService,
        structureModeManagerFactory,
        structuresRestService,
        typeStructureRestService
    ) {

        var modeManager = structureModeManagerFactory.createModeManager(["add", "edit", "create"]);

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getAllRestrictions
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * 
         * @deprecated since 6.4
         * 
         * @returns {Array} All restrictions in the system (any type of restriction).
         */
        this.getAllRestrictions = function() {
            return restrictionsRestService.get();
        };

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getStructureApiUri
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * @param {String} mode The structure mode.
         * @param {String} mode Optional typecode, if omited will leave a placeholder in URI that will be replaced with the item.typeCode.
         * @returns {String} A URI for the structure of restrictions, given a structure mode
         */
        this.getStructureApiUri = function getStructureApiUri(mode, typeCode) {
            modeManager.validateMode(mode);
            return structuresRestService.getUriForContext(mode, typeCode);
        };

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getContentApiUri
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * @param {Object} uriContext The {@link resourceLocationsModule.object:UriContext uriContext}, as defined on the resourceLocationModule.
         * @returns {String} A URI to CRUD restrictions, to a given site/catalog/version
         */
        this.getContentApiUri = function getContentApiUri(uriContext) {
            return restrictionsRestService.getContentApiUri(uriContext);
        };

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getById
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * 
         * @deprecated since 6.4
         * 
         * @param {String} restrictionId Identifier for a given restriction.
         * @return {Object} The restriction matching the given ID.
         */
        this.getById = function getById(restrictionId) {
            return restrictionsRestService.getById(restrictionId).then(function(restriction) {
                return restriction;
            });
        };

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getPagedRestrictionsForType
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * @param {String} restrictionTypeCode Code for the restriction type.
         * @param {String} mask A string value sent to the server upon fetching a page to further restrict the search, it is sent as query string "mask".
         * @param {String} pageSize The maximum size of each page requested from the backend.
         * @param {String} currentPage Current page number.
         * @return {Object} The restriction matching the identifier passed as parameter.
         */
        this.getPagedRestrictionsForType = function(restrictionTypeCode, mask, pageSize, currentPage) {
            return restrictionsRestService.get({
                pageSize: pageSize,
                currentPage: currentPage,
                mask: mask,
                sort: 'name:ASC',
                params: "typeCode:" + restrictionTypeCode
            });
        };

        /**
         * @ngdoc method
         * @name restrictionsServiceModule.service:restrictionsService#getPagedRestrictionsForType
         * @methodOf restrictionsServiceModule.service:restrictionsService
         * @returns {Array} An array of restriction TypeCodes that are supported by SmartEdit.
         */
        this.getSupportedRestrictionTypeCodes = function getSupportedRestrictionTypeCodes() {
            return typeStructureRestService.getStructuresByCategory('RESTRICTION').then(function(structures) {
                return lodash.map(structures, function(structure) {
                    return structure.code;
                });
            });
        };

    });
