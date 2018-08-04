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
 * @name synchronizationServiceModule
 * @description
 *
 * The synchronization module contains the service necessary to perform catalog synchronization.
 *
 * The {@link synchronizationServiceModule.service:synchronizationService synchronizationService} 
 * calls backend API in order to get synchronization status or trigger a catalog synchronization between two catalog versions.
 *
 */
angular.module('synchronizationServiceModule', ['smarteditServicesModule', 'resourceLocationsModule', 'alertServiceModule', 'authenticationModule', 'timerModule'])
    /**
     * @ngdoc object
     * @name synchronizationServiceModule.object:CATALOG_SYNC_INTERVAL_IN_MILLISECONDS
     * @description
     * This object defines an injectable Angular constant that determines the frequency to update catalog synchronization information. 
     * Value given in milliseconds. 
     */
    .constant('CATALOG_SYNC_INTERVAL_IN_MILLISECONDS', 5000)
    /**
     * @ngdoc service
     * @name synchronizationServiceModule.service:synchronizationService
     * @description
     *
     * The synchronization service manages RESTful calls to the synchronization service's backend API.
     * 
     */
    .service('synchronizationService', function(restServiceFactory, timerService, $q, $translate, alertService, authenticationService, operationContextService, OPERATION_CONTEXT, CATALOG_SYNC_INTERVAL_IN_MILLISECONDS) {

        // Constants
        var BASE_URL = "/cmswebservices";
        var SYNC_JOB_INFO_BY_TARGET_URI = '/cmswebservices/v1/catalogs/:catalog/synchronizations/targetversions/:target';
        var SYNC_JOB_INFO_BY_SOURCE_AND_TARGET_URI = '/cmswebservices/v1/catalogs/:catalog/versions/:source/synchronizations/versions/:target';

        // Variables
        var intervalHandle = {};
        var syncJobInfoByTargetRestService = restServiceFactory.get(SYNC_JOB_INFO_BY_TARGET_URI);
        var syncJobInfoBySourceAndTargetRestService = restServiceFactory.get(SYNC_JOB_INFO_BY_SOURCE_AND_TARGET_URI, 'catalog');

        operationContextService.register(SYNC_JOB_INFO_BY_TARGET_URI, OPERATION_CONTEXT.CMS);
        operationContextService.register(SYNC_JOB_INFO_BY_SOURCE_AND_TARGET_URI, OPERATION_CONTEXT.CMS);

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#updateCatalogSync
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method is used to synchronize a catalog between two catalog versions.
         *
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String} catalog.sourceCatalogVersion The name of the source catalog version. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.updateCatalogSync = function(catalog) {
            return syncJobInfoBySourceAndTargetRestService.update({
                'catalog': catalog.catalogId,
                'source': catalog.sourceCatalogVersion,
                'target': catalog.targetCatalogVersion
            }).then(function(response) {
                return response;
            }.bind(this), function(reason) {
                var translationErrorMsg = $translate.instant('sync.running.error.msg', {
                    catalogName: catalog.name
                });
                if (reason.statusText === 'Conflict') {
                    alertService.showDanger({
                        message: translationErrorMsg
                    });
                }
                return false;
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#getCatalogSyncStatus
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method is used to get the status of the last synchronization job between two catalog versions. 
         * 
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String=} catalog.sourceCatalogVersion The name of the source catalog version. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.getCatalogSyncStatus = function(catalog) {
            if (catalog.sourceCatalogVersion) {
                return this.getSyncJobInfoBySourceAndTarget(catalog);
            } else {
                return this.getLastSyncJobInfoByTarget(catalog);
            }
        };

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#getCatalogSyncStatus
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method is used to get the status of the last synchronization job between two catalog versions. 
         * 
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String=} catalog.sourceCatalogVersion The name of the source catalog version. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.getSyncJobInfoBySourceAndTarget = function(catalog) {
            return syncJobInfoBySourceAndTargetRestService.get({
                'catalog': catalog.catalogId,
                'source': catalog.sourceCatalogVersion,
                'target': catalog.targetCatalogVersion
            });
        };

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#getCatalogSyncStatus
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method is used to get the status of the last synchronization job. 
         * 
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.getLastSyncJobInfoByTarget = function(catalog) {
            return syncJobInfoByTargetRestService.get({
                'catalog': catalog.catalogId,
                'target': catalog.targetCatalogVersion
            });
        };

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#stopAutoGetSyncData
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method starts the auto synchronization status update in a catalog between two given catalog versions.
         *
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String=} catalog.sourceCatalogVersion The name of the source catalog version. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.startAutoGetSyncData = function(catalog, callback) {
            var catalogId = catalog.catalogId;
            var sourceCatalogVersion = catalog.sourceCatalogVersion;
            var targetCatalogVersion = catalog.targetCatalogVersion;

            var jobKey = this._getJobKey(catalogId, sourceCatalogVersion, targetCatalogVersion);

            var syncJobTimer = timerService.createTimer(this._autoSyncCallback.bind(this, catalog, callback, jobKey), CATALOG_SYNC_INTERVAL_IN_MILLISECONDS);
            syncJobTimer.start();

            intervalHandle[jobKey] = syncJobTimer;
        };

        this._autoSyncCallback = function(catalog, callback, jobKey) {
            authenticationService.isAuthenticated(BASE_URL).then(function(response) {
                if (!response) {
                    this.stopAutoGetSyncData(catalog);
                }
                this.getCatalogSyncStatus(catalog)
                    .then(callback)
                    .then(function() {
                        if (!intervalHandle[jobKey]) {
                            this.startAutoGetSyncData(catalog, callback);
                        }
                    }.bind(this));
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name synchronizationServiceModule.service:synchronizationService#stopAutoGetSyncData
         * @methodOf synchronizationServiceModule.service:synchronizationService
         *
         * @description
         * This method stops the auto synchronization status update in a catalog between two given catalog versions.
         *
         * @param {Object} catalog An object that contains the information about the catalog to be synchronized.
         * @param {String} catalog.catalogId The ID of the catalog to synchronize. 
         * @param {String=} catalog.sourceCatalogVersion The name of the source catalog version. 
         * @param {String} catalog.targetCatalogVersion The name of the target catalog version.
         */
        this.stopAutoGetSyncData = function(catalog) {
            var jobKey = this._getJobKey(catalog.catalogId, catalog.sourceCatalogVersion, catalog.targetCatalogVersion);
            if (intervalHandle[jobKey]) {
                intervalHandle[jobKey].stop();
                intervalHandle[jobKey] = undefined;
            }
        };

        this._getJobKey = function(catalogId, sourceCatalogVersion, targetCatalogVersion) {
            return catalogId + "_" + sourceCatalogVersion + "_" + targetCatalogVersion;
        };
    });
