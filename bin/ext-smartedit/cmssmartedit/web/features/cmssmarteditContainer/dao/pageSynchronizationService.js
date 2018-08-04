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
angular.module('pageSynchronizationServiceModule', ['synchronizationPollingServiceModule'])
    .service('pageSynchronizationService', function(syncPollingService) {

        this.getSyncStatus = function(itemId, uriContext) {
            return syncPollingService.getSyncStatus(itemId, uriContext).then(function(syncStatus) {
                return syncStatus;
            });
        };

        this.performSync = function(array, uriContext) {
            return syncPollingService.performSync(array, uriContext);
        };
    });
