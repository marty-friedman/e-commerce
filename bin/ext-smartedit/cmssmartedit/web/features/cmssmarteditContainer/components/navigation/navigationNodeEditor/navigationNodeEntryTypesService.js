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
angular.module('navigationNodeEntryTypesServiceModule', ['resourceModule'])

    /**
     * @ngdoc service
     * @name navigationNodeEntryTypesServiceModule.service:navigationNodeEntryTypesService
     * @description
     * This service manages navigation node entry types data by making REST call to the cmswebservices navigationentrytypes API.
     */
    .service('navigationNodeEntryTypesService', function(navigationEntryTypesRestService) {

        /**
         * @ngdoc method
         * @name navigationNodeEntryTypesServiceModule.service:navigationNodeEntryTypesService#getNavigationNodeEntryTypes
         * @methodOf navigationNodeEntryTypesServiceModule.service:navigationNodeEntryTypesService
         *
         * @description
         * Returns the navigation node entry types supported by cmswebservices navigationentrytypes API.
         *
         * @return {Array} all navigation entry types supported.
         */
        this.getNavigationNodeEntryTypes = function() {
            return navigationEntryTypesRestService.get().then(function(response) {
                return response.navigationEntryTypes;
            });
        };
    });
