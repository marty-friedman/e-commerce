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
angular.module('pageSyncMenuToolbarItemModule', ['crossFrameEventServiceModule', 'catalogServiceModule', 'pageSynchronizationPanelModule', 'smarteditServicesModule'])
    .controller('PageSyncMenuToolbarItemController', function(crossFrameEventService, catalogService, assetsService, systemEventService, iframeClickDetectionService, pageInfoService, pageSynchronizationService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING, $scope) {

        this.fetchSyncStatus = function() {
            pageInfoService.getPageUUID().then(function(pageUUID) {
                pageSynchronizationService.getSyncStatus(pageUUID).then(function(syncStatus) {
                    this.isNotInSync = syncStatus.status !== SYNCHRONIZATION_STATUSES.IN_SYNC;
                }.bind(this));
            }.bind(this));
        }.bind(this);


        this.$onInit = function() {

            this.isContentCatalogVersionNonActive = false;

            catalogService.isContentCatalogVersionNonActive().then(function(isNonActive) {
                if (isNonActive) {

                    this.icons = {
                        open: assetsService.getAssetsRoot() + "/images/icon_info_white.png",
                        closed: assetsService.getAssetsRoot() + "/images/icon_info_blue.png"
                    };

                    this.menuIcon = this.icons.closed;
                    this.isNotInSync = false;
                    this.isContentCatalogVersionNonActive = true;

                    var unRegisterSyncPolling = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus);

                    this.fetchSyncStatus();

                    $scope.$on('$destroy', function() {
                        unRegisterSyncPolling();
                    });

                }
            }.bind(this));
        };

    })
    .component('pageSyncMenuToolbarItem', {
        templateUrl: 'pageSyncMenuToolbarItemTemplate.html',
        controller: 'PageSyncMenuToolbarItemController',
        controllerAs: '$ctrl',
        bindings: {
            toolbarItem: '<item'
        }
    });
