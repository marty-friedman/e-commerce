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
angular.module('catalogVersionPermissionRestServiceModule', ['smarteditServicesModule', 'sessionServiceModule', 'functionsModule'])

    /**
     * @ngdoc object
     * @name catalogVersionPermissionRestServiceModule.object:CATALOG_VERSION_PERMISSIONS_RESOURCE_URI
     *
     * @description
     * Path to fetch permissions of a given catalog version.
     */
    .constant('CATALOG_VERSION_PERMISSIONS_RESOURCE_URI', "/permissionswebservices/v1/permissions/principals/:principal/catalogs")

    /**
     * @ngdoc service
     * @name catalogVersionPermissionRestServiceModule.service:catalogVersionPermissionRestService
     *
     * @description
     * The catalog version permission service is used to check if the current user has been granted certain permissions
     * on a given catalog ID and catalog Version.
     */
    .service('catalogVersionPermissionRestService', function(restServiceFactory, sessionService, CATALOG_VERSION_PERMISSIONS_RESOURCE_URI, URIBuilder) {
        var validateParams = function(catalogId, catalogVersion) {
            if (!catalogId) {
                throw new Error("catalog.version.permission.service.catalogid.is.required");
            }

            if (!catalogVersion) {
                throw new Error("catalog.version.permission.service.catalogversion.is.required");
            }
        };

        var loadPrincipal = function() {
            return sessionService.getCurrentUsername().then(function(username) {
                return username;
            });
        };

        /**
         * @ngdoc method
         * @name catalogVersionPermissionRestServiceModule.service:catalogVersionPermissionRestService#getCatalogVersionPermissions
         * @methodOf catalogVersionPermissionRestServiceModule.service:catalogVersionPermissionRestService
         *
         * @description
         * This method returns permissions from the Catalog Version Permissions Service API.
         *
         * Sample Request:
         * GET /permissionswebservices/v1/permissions/principals/{principal}/catalogs?catalogId=apparel-deContentCatalog&catalogVersion=Online
         *
         * Sample Response from API:
         * {
         * "permissionsList": [
         *     {
         *       "catalogId": "apparel-deContentCatalog",
         *       "catalogVersion": "Online",
         *       "permissions": [
         *         {
         *           "key": "read",
         *           "value": "true"
         *         },
         *         {
         *           "key": "write",
         *           "value": "false"
         *         }
         *       ]
         *     }
         *    ]
         * }
         *
         * Sample Response returned by the service:
         * {
         *   "catalogId": "apparel-deContentCatalog",
         *   "catalogVersion": "Online",
         *   "permissions": [
         *      {
         *        "key": "read",
         *        "value": "true"
         *      },
         *      {
         *        "key": "write",
         *        "value": "false"
         *      }
         *     ]
         *  }
         *
         * @param {String} catalogId The Catalog ID
         * @param {String} catalogVersion The Catalog Version name
         *
         * @returns {IPromise} A Promise which returns an object exposing a permissions array containing the catalog version permissions
         */
        this.getCatalogVersionPermissions = function(catalogId, catalogVersion) {
            validateParams(catalogId, catalogVersion);

            return loadPrincipal().then(function(user) {
                var postURI = new URIBuilder(CATALOG_VERSION_PERMISSIONS_RESOURCE_URI).replaceParams({
                    principal: user
                }).build();
                var restService = restServiceFactory.get(postURI);

                return restService.get({
                    catalogId: catalogId,
                    catalogVersion: catalogVersion
                }).then(function(permissionsResponse) {
                    return permissionsResponse.permissionsList[0] || {};
                });
            });
        };
    });
