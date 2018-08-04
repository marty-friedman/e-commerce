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
 * @name authorizationModule.authorizationService
 * @description
 * The authorization module provides a service that checks if the current user was granted certain
 * permissions by contacting the Global Permissions REST API.
 * 
 * This module makes use of the {@link smarteditServicesModule smarteditServicesModule} to poll the
 * Global Permissions REST API in the backend.
 */
angular.module('authorizationModule', ['smarteditServicesModule', 'loadConfigModule', 'sessionServiceModule', 'resourceLocationsModule'])
    /**
     * @ngdoc service
     * @name authorizationModule.AuthorizationService
     *
     * @description
     * This service makes calls to the Global Permissions REST API to check if the current user was
     * granted certain permissions.
     */
    .service('authorizationService', function($log, restServiceFactory, sessionService, USER_GLOBAL_PERMISSIONS_RESOURCE_URI) {
        var permissionsResource = restServiceFactory.get(USER_GLOBAL_PERMISSIONS_RESOURCE_URI);

        /*
         * This method will look for the result for the given permission name. If found, it is
         * verified that it has been granted. Otherwise, the method will return false.
         */
        var getPermissionResult = function(permissionResults, permissionName) {
            var permission = permissionResults.find(function(permission) {
                return permission.key.toLowerCase() === permissionName.toLowerCase();
            });

            return !!permission && permission.value === 'true';
        };

        /*
         * This method merges permission results. It iterates through the list of permission names that
         * were checked and evaluates if the permission is granted. It immediately returns false when
         * it encounters a permission that is denied.
         */
        var mergePermissionResults = function(permissionResults, permissionNames) {
            var hasPermission = !!permissionNames && permissionNames.length > 0;
            var index = 0;

            while (hasPermission && index < permissionNames.length) {
                hasPermission = hasPermission && getPermissionResult(permissionResults, permissionNames[index++]);
            }

            return hasPermission;
        };

        /*
         * This method makes a call to the Global Permissions API with the given permission names
         * and returns the list of results.
         */
        var getPermissions = function(permissionNames) {
            return sessionService.getCurrentUsername().then(function(user) {
                if (!user) {
                    return [];
                }

                return permissionsResource.get({
                    user: user,
                    permissionNames: permissionNames.join(',')
                }).then(function(response) {
                    return response.permissions;
                });
            });
        };

        /**
         * @ngdoc method
         * @name authorizationModule.AuthorizationService#canPerformOperation
         * @methodOf authorizationModule.AuthorizationService
         *
         * @description
         * This method checks if the current user has been granted the permissions required to
         * perform a certain operation.
         *
         * @param {String} permissionNames A string of comma separated values that contains the global permissions to check.
         * 
         * @return {Boolean} true if the user is granted all of the given permissions, false otherwise
         * 
         * @throws Will throw an error if the permissionNames string is empty
         * 
         * @deprecated since version 6.4. Use {@link authorizationModule.AuthorizationService#hasGlobalPermissions hasGlobalPermissions()} instead.
         */
        this.canPerformOperation = function(permissionNames) {
            var permissionNamesArray = !!permissionNames ? permissionNames.split(',') : [];

            return this.hasGlobalPermissions(permissionNamesArray);
        };

        /**
         * @ngdoc method
         * @name authorizationModule.AuthorizationService#hasGlobalPermissions
         * @methodOf authorizationModule.AuthorizationService
         *
         * @description
         * This method checks if the current user is granted the given global permissions.
         *
         * @param {String[]} permissionNames The list of global permissions to check.
         * 
         * @return {Boolean} true if the user is granted all of the given permissions, false otherwise
         * 
         * @throws Will throw an error if the permissionNames parameter is not an array
         * @throws Will throw an error if the permissionNames array is empty
         */
        this.hasGlobalPermissions = function(permissionNames) {
            if (!(permissionNames instanceof Array)) {
                throw 'permissionNames must be an array';
            }

            if (permissionNames.length < 1) {
                throw 'permissionNames cannot be empty';
            }

            var onSuccess = function(permissions) {
                return mergePermissionResults(permissions, permissionNames);
            };

            var onError = function() {
                $log.error('AuthorizationService - Failed to determine authorization for the following permissions: ' + permissionNames.toString());
                return false;
            };

            return getPermissions(permissionNames).then(onSuccess, onError);
        };
    });
