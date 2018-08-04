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
 * @name restrictionsModule
 * @requires restrictionsServiceModule
 * @description
 * This module defines the {@link restrictionsModule.restrictionsFacade restrictionsFacade} factory managing all restrictions.
 */
angular.module('restrictionsModule', [
        'restrictionsServiceModule'
    ])

    /**
     * @ngdoc service
     * @name restrictionsModule.restrictionsFacade
     * @requires restrictionsService
     * @description
     * A facade that exposes only the business logic necessary for features that need to work with restrictions.
     */
    .factory('restrictionsFacade', function(
        restrictionsService
    ) {

        return {

            // restrictionsService
            getAllRestrictions: restrictionsService.getAllRestrictions,
            getPagedRestrictionsForType: restrictionsService.getPagedRestrictionsForType

        };

    });
