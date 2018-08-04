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
angular.module('pageSynchronizationPanelModule', ['functionsModule', 'synchronizationPanelModule', 'pageSynchronizationServiceModule', 'pageSynchronizationHeaderModule', 'smarteditServicesModule'])
    .constant("SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL", "se.cms.synchronization.page.select.all.slots")
    .controller('PageSynchronizationPanelController', function($attrs, SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL, isBlank, pageSynchronizationService, pageInfoService) {

        this.getSyncStatus = function() {
            return pageSynchronizationService.getSyncStatus(this.itemId, this.uriContext).then(function(syncStatus) {
                syncStatus.selectAll = SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL;
                return syncStatus;
            }.bind(this));
        }.bind(this);

        this.headerTemplateUrl = "pageSynchronizationHeaderWrapperTemplate.html";

        this.onSyncStatusReady = function($syncStatus) {
            if ($syncStatus.unavailableDependencies.length > 0) {
                this._disablePageSync();
            } else if (!$syncStatus.lastSyncStatus) {
                this._enablePageSync();
            } else {
                this._enableSlotsSync();
            }
        }.bind(this);

        this.performSync = function(array) {
            return pageSynchronizationService.performSync(array, this.uriContext);
        }.bind(this);

        this.$postLink = function() {
            this.showSyncButton = isBlank($attrs.syncItems);
            if (isBlank(this.itemId)) {
                pageInfoService.getPageUID().then(function(pageUID) {
                    this.itemId = pageUID;
                }.bind(this));
            }
        };

        this.getApi = function($api) {
            this.synchronizationPanelApi = $api;
        };

        // disbale page sync
        this._disablePageSync = function() {
            this.synchronizationPanelApi.displayItemList(false);
        };

        // enable page sync only
        this._enablePageSync = function() {
            this.synchronizationPanelApi.selectAll();
            this.synchronizationPanelApi.displayItemList(false);
        };

        // enable slot/page sync
        this._enableSlotsSync = function() {
            this.synchronizationPanelApi.displayItemList(true);
        };
    })
    .component('pageSynchronizationPanel', {
        templateUrl: 'pageSynchronizationPanelTemplate.html',
        controller: 'PageSynchronizationPanelController',
        controllerAs: 'pageSync',
        bindings: {
            syncItems: '=?',
            itemId: '=?',
            uriContext: '=?',
            onSelectedItemsUpdate: '<?'
        }
    });
