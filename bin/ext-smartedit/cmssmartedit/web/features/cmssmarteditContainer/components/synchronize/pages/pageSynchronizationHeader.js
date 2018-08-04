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
angular.module('pageSynchronizationHeaderModule', ['translationServiceModule', 'smarteditServicesModule', 'catalogServiceModule', 'l10nModule', 'functionsModule'])
    .controller('PageSynchronizationHeaderController', function($translate, sharedDataService, catalogService, l10nFilter, isBlank) {
        this.$onChanges = function() {
            if (!this.syncStatus) {
                return;
            }

            this.ready = false;

            this.pageHasUnavailableDependencies = this.syncStatus.unavailableDependencies.length > 0;
            this.pageHasSyncStatus = !isBlank(this.syncStatus.lastSyncStatus);

            if (this.pageHasUnavailableDependencies || !this.pageHasSyncStatus) {

                sharedDataService.get('experience').then(function(data) {
                    var catalog = this.getCurrentCatalogIdFromExperience(data);
                    var catalogId = catalog.catalogId;
                    var catalogName = l10nFilter(catalog.catalogName);
                    return catalogService.getActiveContentCatalogVersionByCatalogId(catalogId).then(function(catalogVersion) {

                        this.headerText = this.pageHasUnavailableDependencies ? $translate.instant('se.cms.synchronization.page.unavailable.items.description', {
                            itemNames: this._fetchUnavailableDependencies(),
                            catalogName: catalogName,
                            catalogVersion: catalogVersion
                        }) : $translate.instant('se.cms.synchronization.page.new.description', {
                            catalogName: catalogName,
                            catalogVersion: catalogVersion
                        });
                        this.ready = true;

                    }.bind(this));
                }.bind(this));

            } else {
                $translate('se.cms.synchronization.page.header.help').then(function(translation) {
                    this.helpTemplate = "<span>" + translation + "</span>";
                }.bind(this));
                this.ready = true;
            }

        };

        this._fetchUnavailableDependencies = function() {
            return this.syncStatus.unavailableDependencies.map(function(item) {
                return item.name;
            }).join(',');
        };

        this.userIsInsidePage = function(experience) {
            return !!experience.pageContext;
        };

        this.getCurrentCatalogIdFromExperience = function(experience) {
            return {
                catalogId: this.userIsInsidePage(experience) ? experience.pageContext.catalogId : experience.catalogDescriptor.catalogId,
                catalogName: this.userIsInsidePage(experience) ? experience.pageContext.catalogName : experience.catalogDescriptor.name
            };
        };
    })
    .component('pageSynchronizationHeader', {
        templateUrl: 'pageSynchronizationHeaderTemplate.html',
        bindings: {
            syncStatus: '<'
        },
        controller: 'PageSynchronizationHeaderController',
        controllerAs: 'pageSync'
    });
