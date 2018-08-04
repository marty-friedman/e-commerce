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
angular.module('experienceSelectorModule', ['eventServiceModule', 'recompileDomModule', 'genericEditorModule', 'smarteditServicesModule', 'iframeClickDetectionServiceModule', 'iFrameManagerModule', 'genericEditorModule', 'resourceLocationsModule', 'previewDataDropdownPopulatorModule', 'experienceServiceModule', 'yLoDashModule', 'functionsModule', 'catalogServiceModule', 'languageServiceModule'])
    .constant("ERROR_CATALOG_SERVICE_FAILED", "catalogService received rejected promise: ")
    .constant("WARN_NO_PRODUCT_CATALOGS_FOUND", "No product catalogs we found")
    .controller('ExperienceSelectorController', function($q, $scope, $filter, $timeout, $document, getAbsoluteURL, systemEventService, siteService, sharedDataService, iframeClickDetectionService, iFrameManager, GenericEditor, experienceService, EVENTS, TYPES_RESOURCE_URI, PREVIEW_RESOURCE_URI, lodash, formatDateAsUtc, catalogService, languageService) {
        var siteCatalogs = {};

        this.$onInit = function() {

            this.resetExperienceSelector = function() {
                $q.all([sharedDataService.get('experience'), sharedDataService.get('configuration')]).then(function(array) {
                    var experience = array[0];
                    var configuration = array[1];

                    var selectedExperience = lodash.cloneDeep(experience);
                    delete selectedExperience.siteDescriptor;
                    delete selectedExperience.languageDescriptor;
                    selectedExperience.previewCatalog = experience.siteDescriptor.uid + '_' + experience.catalogDescriptor.catalogId + '_' + experience.catalogDescriptor.catalogVersion;
                    selectedExperience.language = experience.languageDescriptor.isocode;
                    selectedExperience.productCatalogVersions = selectedExperience.productCatalogVersions.map(function(productCatalogVersion) {
                        return productCatalogVersion.uuid;
                    });

                    this.smarteditComponentType = 'PreviewData';
                    this.smarteditComponentId = null;
                    this.structureApi = TYPES_RESOURCE_URI + '/:smarteditComponentType?mode=DEFAULT';
                    this.contentApi = configuration && configuration.previewTicketURI || PREVIEW_RESOURCE_URI;

                    this.content = selectedExperience;
                    if (!this.isReady) {
                        this.isReady = true;
                    } else {
                        this.recompile();
                    }
                }.bind(this));
            }.bind(this);

        };

        this.returnProductCatalogsByUuids = function(versionUuids) {
            var versions = [];
            siteCatalogs.productCatalogs.forEach(function(catalog) {
                if (catalog.versions) {
                    var versionMatch = catalog.versions.find(function(version) {
                        return versionUuids.indexOf(version.uuid) > -1;
                    });
                    versions.push({
                        catalog: catalog.catalogId,
                        catalogVersion: versionMatch.version
                    });
                }
            });
            return versions;
        };

        this.preparePayload = function(originalPayload) {
            siteCatalogs.siteId = originalPayload.previewCatalog.split('_')[0];
            siteCatalogs.catalogId = originalPayload.previewCatalog.split('_')[1];
            siteCatalogs.catalogVersion = originalPayload.previewCatalog.split('_')[2];

            return catalogService.getProductCatalogsForSite(siteCatalogs.siteId).then(function(productCatalogs) {
                siteCatalogs.productCatalogs = productCatalogs;
                siteCatalogs.productCatalogVersions = originalPayload.productCatalogVersions;
                return $q.all([sharedDataService.get('experience'), sharedDataService.get('configuration')]).then(function(array) {

                    var experience = array[0];
                    var configuration = array[1];
                    return siteService.getSiteById(siteCatalogs.siteId).then(function(siteDescriptor) {

                        var transformedPayload = lodash.cloneDeep(originalPayload);
                        delete transformedPayload.previewCatalog;
                        delete transformedPayload.time;

                        transformedPayload.language = languageService.convertBCP47TagToJavaTag(transformedPayload.language);
                        transformedPayload.catalog = siteCatalogs.catalogId;
                        transformedPayload.catalogVersion = siteCatalogs.catalogVersion;
                        transformedPayload.resourcePath = getAbsoluteURL(configuration.domain, siteDescriptor.previewUrl);
                        transformedPayload.pageId = experience.pageId;
                        transformedPayload.time = originalPayload.time;
                        transformedPayload.catalogVersions = this.returnProductCatalogsByUuids(originalPayload.productCatalogVersions);

                        return transformedPayload;

                    }.bind(this));

                }.bind(this));

            }.bind(this));
        }.bind(this);

        this.updateCallback = function(payload, response) {
            delete this.smarteditComponentId; //to force a permanent POST
            this.dropdownStatus.isopen = false;

            // Then perform the actual update.
            var experienceParams = lodash.cloneDeep(response);
            delete experienceParams.catalog;
            delete experienceParams.time;

            experienceParams.language = languageService.convertJavaTagToBCP47Tag(experienceParams.language);
            experienceParams.siteId = siteCatalogs.siteId;
            experienceParams.catalogId = siteCatalogs.catalogId;
            experienceParams.catalogVersion = siteCatalogs.catalogVersion;
            experienceParams.time = formatDateAsUtc(payload.time);
            experienceParams.pageId = response.pageId;
            experienceParams.productCatalogVersions = siteCatalogs.productCatalogVersions;
            experienceService.buildDefaultExperience(experienceParams).then(function(experience) {
                sharedDataService.set('experience', experience).then(function() {
                    systemEventService.sendAsynchEvent(EVENTS.EXPERIENCE_UPDATE);
                    iFrameManager.loadPreview(experience.siteDescriptor.previewUrl, response.ticketId);
                    var preview = {
                        previewTicketId: response.ticketId,
                        resourcePath: experience.siteDescriptor.previewUrl
                    };
                    sharedDataService.set('preview', preview);
                });
            });
        }.bind(this);

        this.modalHeaderTitle = 'se.experience.selector.header';

        this.$postLink = function() {

            this.unRegCloseExperienceFn = iframeClickDetectionService.registerCallback('closeExperienceSelector', function() {
                if (this.dropdownStatus && this.dropdownStatus.isopen) {
                    this.dropdownStatus.isopen = false;
                }
            }.bind(this));

            this.unRegFn = systemEventService.registerEventHandler('OVERLAY_DISABLED', function() {
                if (this.dropdownStatus && this.dropdownStatus.isopen) {
                    this.dropdownStatus.isopen = false;
                }
            }.bind(this));
        };

        this.$onDestroy = function() {
            if (this.unRegFn) {
                this.unRegFn();
            }
            if (this.unRegCloseExperienceFn) {
                this.unRegCloseExperienceFn();
            }
        };

        this.getApi = function($api) {
            $api.setPreparePayload(this.preparePayload);
            $api.setUpdateCallback(this.updateCallback);
            $api.setAlwaysShowSubmit(true);
            $api.setAlwaysShowReset(true);
            $api.setSubmitButtonText('se.componentform.actions.apply');
            $api.setCancelButtonText('se.componentform.actions.cancel');
            $api.setOnReset(function() {
                this.dropdownStatus.isopen = false;
            }.bind(this));
        }.bind(this);
    })
    .component('experienceSelector', {
        templateUrl: 'experienceSelectorTemplate.html',
        transclude: true,
        controller: 'ExperienceSelectorController',
        controllerAs: 'es',
        bindings: {
            experience: '=',
            dropdownStatus: '=',
            resetExperienceSelector: '='
        }
    });
