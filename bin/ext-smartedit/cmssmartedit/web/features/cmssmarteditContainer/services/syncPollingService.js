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
angular.module('synchronizationPollingServiceModule', [
        'renderServiceInterfaceModule',
        'smarteditServicesModule',
        'eventServiceModule',
        'timerModule',
        'gatewayProxyModule',
        'crossFrameEventServiceModule',
        'resourceModule',
        'synchronizationConstantsModule',
        'catalogServiceModule',
        'yLoDashModule',
        'functionsModule'
    ])
    .run(function(syncPollingService) {
        syncPollingService.initSyncPolling();
    })
    .constant('SYNC_POLLING_THROTTLE', 500)
    .factory('syncPollingService', function($log, $q, SYNCHRONIZATION_POLLING, EVENTS, OVERLAY_RERENDERED_EVENT, gatewayProxy, pageInfoService, timerService, crossFrameEventService, systemEventService, synchronizationResource, catalogService, lodash, isBlank, SYNC_POLLING_THROTTLE) {

        var SyncPollingService = function(gatewayId) {
            this.gatewayId = gatewayId;

            gatewayProxy.initForService(this);
        };

        var getPageUUID = function(_pageUUID) {
            return !isBlank(_pageUUID) ? $q.when(_pageUUID) : pageInfoService.getPageUUID();
        };

        SyncPollingService.prototype.getSyncStatus = function(pageUUID, uriContext) {
            if (this.syncStatus[pageUUID] && pageUUID === this.syncStatus[pageUUID].itemId) {
                return $q.when(this.syncStatus[pageUUID]);
            } else {
                return getPageUUID(pageUUID).then(function(pageUUID) {
                    if (pageUUID) {
                        this.syncPollingTimer.restart(this.refreshInterval);
                        return this._fetchSyncStatus(pageUUID, uriContext).then(function(syncStatus) {
                            this.syncStatus[syncStatus.itemId] = syncStatus;
                            return syncStatus;
                        }.bind(this), function() {
                            $log.error('syncPollingService::getSyncStatus - failed call to _fetchSyncStatus');
                            return $q.reject();
                        });
                    }
                }.bind(this), function() {
                    $log.error('syncPollingService::getSyncStatus - failed call to getPageUUID');
                    this.syncPollingTimer.stop();
                    return this._fetchSyncStatus(pageUUID, uriContext);
                }.bind(this));
            }
        };

        SyncPollingService.prototype._fetchSyncStatus = function(_pageUUID, uriContext) {
            return getPageUUID(_pageUUID).then(function(pageUUID) {
                if (pageUUID) {
                    return catalogService.isContentCatalogVersionNonActive().then(function(isContentCatalogVersionNonActive) {
                        if (isContentCatalogVersionNonActive) {

                            return catalogService.getContentCatalogActiveVersion(uriContext).then(function(activeVersion) {
                                return synchronizationResource.getPageSynchronizationGetRestService(uriContext).get({
                                    pageUid: pageUUID,
                                    target: activeVersion
                                }).then(function(syncStatus) {
                                    if (JSON.stringify(syncStatus) !== JSON.stringify(this.syncStatus[syncStatus.itemId])) {
                                        crossFrameEventService.publish(SYNCHRONIZATION_POLLING.FAST_FETCH, syncStatus);
                                    }
                                    this.syncStatus[syncStatus.itemId] = syncStatus;

                                    return syncStatus;
                                }.bind(this));
                            }.bind(this));

                        } else {
                            return $q.reject();
                        }
                    }.bind(this));
                } else {
                    return $q.when({});
                }
            }.bind(this), function() {
                this.syncPollingTimer.stop();
                return $q.reject();
            }.bind(this));
        };

        SyncPollingService.prototype.changePollingSpeed = function(eventId, itemId) {
            if (eventId === SYNCHRONIZATION_POLLING.SPEED_UP) {
                this.syncStatus = {};
                if (itemId && this.triggers.indexOf(itemId) === -1) {
                    this.triggers.push(itemId);
                }

                this.refreshInterval = SYNCHRONIZATION_POLLING.FAST_POLLING_TIME;
            } else {
                if (itemId) {
                    this.triggers.splice(this.triggers.indexOf(itemId), 1);
                }
                if (this.triggers.length === 0) {
                    this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
                }
            }

            this.syncPollingTimer.restart(this.refreshInterval);

        };

        SyncPollingService.prototype.initSyncPolling = function() {
            this.refreshInterval = SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME;
            this.triggers = [];
            this.syncStatus = {};

            var changePolling = this.changePollingSpeed.bind(this);

            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SPEED_UP, changePolling);
            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SLOW_DOWN, changePolling);

            crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FETCH_SYNC_STATUS_ONCE, function(eventId, pageUUID) {
                this._fetchSyncStatus.bind(this)(pageUUID);
            }.bind(this));

            crossFrameEventService.subscribe(OVERLAY_RERENDERED_EVENT, lodash.throttle(function() {
                if (this.syncPollingTimer.isActive()) {
                    this._fetchSyncStatus.bind(this)();
                }
            }.bind(this), SYNC_POLLING_THROTTLE));

            crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, this.stopSync.bind(this));

            this.syncPollingTimer = timerService.createTimer(this._fetchSyncStatus.bind(this), this.refreshInterval);
        };


        SyncPollingService.prototype.performSync = function(array, uriContext) {
            return catalogService.isContentCatalogVersionNonActive(uriContext).then(function(isNonActive) {
                if (isNonActive) {
                    return catalogService.getContentCatalogActiveVersion(uriContext).then(function(activeVersion) {
                        return synchronizationResource.getPageSynchronizationPostRestService(uriContext).save({
                            target: activeVersion,
                            items: array
                        });
                    });
                } else {
                    return $q.reject();
                }
            });
        };

        SyncPollingService.prototype.stopSync = function() {
            if (this.syncPollingTimer.isActive()) {
                this.syncPollingTimer.stop();
            }
        };

        SyncPollingService.prototype.startSync = function(pollingType) {
            if (!this.syncPollingTimer.isActive()) {
                this.changePollingSpeed(pollingType || SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME);
            }
        };




        return new SyncPollingService('syncPollingService');

    });
