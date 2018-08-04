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
angular.module('sessionServiceInterfaceModule', [])

    /**
     * @ngdoc object
     * @name sessionServiceModule.object:WHOAMI_RESOURCE_URI
     *
     * @description
     * Resource URI of the WhoAmI REST service used to retrieve information on the
     * current logged-in user.

     */
    .constant('WHO_AM_I_RESOURCE_URI', '/authorizationserver/oauth/whoami')

    /**
     * @ngdoc service
     * @name sessionServiceModule.service:sessionService
     * @description
     * # The sessionServiceInterface
     *
     * The Session service provides information related to the current session
     * and the authenticated user.
     */
    .factory('SessionServiceInterface', function() {

        var SessionServiceInterface = function() {};

        /**
         * @ngdoc method
         * @name sessionServiceModule.service:sessionService#getCurrentUsername
         * @methodOf sessionServiceModule.service:sessionService
         *
         * @description
         * Returns the username, previously mentioned as "principalUID",
         * associated to the authenticated user.
         *
         * @returns {Promise<String>} A promise resolving to the username,
         * previously mentioned as "principalUID", associated to the
         * authenticated user.
         */
        SessionServiceInterface.prototype.getCurrentUsername = function() {};

        /**
         * @ngdoc method
         * @name sessionServiceModule.service:sessionService#getCurrentUserDisplayName
         * @methodOf sessionServiceModule.service:sessionService
         *
         * @description
         * Returns the displayed name associated to the authenticated user.
         *
         * @returns {Promise<String>} A promise resolving to the displayed name
         * associated to the authenticated user.
         */
        SessionServiceInterface.prototype.getCurrentUserDisplayName = function() {};

        /**
         * @ngdoc method
         * @name sessionServiceModule.service:sessionService#hasUserChanged
         * @methodOf sessionServiceModule.service:sessionService
         *
         * @description
         * Returns boolean indicating whether the current user is different from
         * the last authenticated one.
         *
         * @returns {Boolean} Boolean indicating whether the current user is
         * different from the last authenticated one.
         */
        SessionServiceInterface.prototype.hasUserChanged = function() {};

        /**
         * @ngdoc method
         * @name sessionServiceModule.service:sessionService#resetCurrentUserData
         * @methodOf sessionServiceModule.service:sessionService
         *
         * @description
         * Reset all data associated to the authenticated user.
         * to the authenticated user.
         */
        SessionServiceInterface.prototype.resetCurrentUserData = function() {};

        /**
         * @ngdoc method
         * @name sessionServiceModule.service:sessionService#setCurrentUsername
         * @methodOf sessionServiceModule.service:sessionService
         *
         * @description
         * Set the username, previously mentioned as "principalUID", associated
         * to the authenticated user.
         *
         * @param {String} currentUsername Username, previously mentioned as
         * "principalUID", associated to the authenticated user.
         */
        SessionServiceInterface.prototype.setCurrentUsername = function() {};

        return SessionServiceInterface;

    });
