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
angular.module('synchronizationPollingServiceModule', ['gatewayProxyModule', 'eventServiceModule', 'synchronizationConstantsModule'])
    .run(function(syncPollingService) {
        syncPollingService.registerSyncPollingEvents();
    })
    .factory('syncPollingService', function(gatewayProxy, systemEventService, SYNCHRONIZATION_POLLING) {

        var SyncPollingService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        SyncPollingService.prototype.getSyncStatus = function() {};

        SyncPollingService.prototype._fetchSyncStatus = function() {};

        SyncPollingService.prototype.changePollingSpeed = function() {};

        SyncPollingService.prototype.registerSyncPollingEvents = function() {
            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SPEED_UP, this.changePollingSpeed.bind(this));
            systemEventService.registerEventHandler(SYNCHRONIZATION_POLLING.SLOW_DOWN, this.changePollingSpeed.bind(this));
        };

        SyncPollingService.prototype.performSync = function() {};


        return new SyncPollingService('syncPollingService');

    });
