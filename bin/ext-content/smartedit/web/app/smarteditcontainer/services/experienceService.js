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
angular.module('experienceServiceModule', ['crossFrameEventServiceModule', 'catalogServiceModule', 'languageServiceModule', 'smarteditServicesModule', 'yLoDashModule', 'gatewayProxyModule'])

    /**
     * @ngdoc service
     * @name experienceServiceModule.service:experienceService
     *
     * @description
     * The experience Service deals with building experience objects given a context.
     */
    .service('experienceService', function($q, $location, crossFrameEventService, EVENTS, siteService, catalogService, languageService, sharedDataService, lodash, gatewayProxy, STOREFRONT_PATH_WITH_PAGE_ID) {


        var ExperienceService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this, ['updateExperiencePageContext', 'getCurrentExperience']);
        };

        /**
         * @ngdoc method
         * @name experienceServiceModule.service:experienceService#buildDefaultExperience
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
         *
         * @returns {object} an experience
         */
        ExperienceService.prototype.buildDefaultExperience = function(params) {

            var siteId = params.siteId;
            var catalogId = params.catalogId;
            var catalogVersion = params.catalogVersion;
            var productCatalogVersions = params.productCatalogVersions;


            return siteService.getSiteById(siteId).then(function(siteDescriptor) {
                return catalogService.getContentCatalogsForSite(siteId).then(function(catalogs) {
                    return catalogService.getProductCatalogsForSite(siteId).then(function(productCatalogs) {
                        var currentCatalog = catalogs.find(function(catalog) {
                            return catalog.catalogId === catalogId;
                        });

                        var currentCatalogVersion = (currentCatalog) ? currentCatalog.versions.find(function(result) {
                            return result.version === catalogVersion;
                        }) : null;

                        if (!currentCatalogVersion) {
                            return $q.reject("no catalogVersionDescriptor found for _catalogId_ catalogId and _catalogVersion_ catalogVersion".replace("_catalogId_", catalogId).replace("_catalogVersion_", catalogVersion));
                        }

                        var currentProductCatalogVersions = [];
                        productCatalogs.forEach(function(productCatalog) {

                            // for each product catalog either choose the version already present in the params or choose the active version.
                            var currentProductCatalogVersion = productCatalog.versions.find(function(version) {
                                return productCatalogVersions ? productCatalogVersions.indexOf(version.uuid) > -1 : version.active === true;
                            });
                            currentProductCatalogVersions.push({
                                catalog: productCatalog.catalogId,
                                catalogName: productCatalog.name,
                                catalogVersion: currentProductCatalogVersion.version,
                                active: currentProductCatalogVersion.active,
                                uuid: currentProductCatalogVersion.uuid
                            });
                        });

                        return languageService.getLanguagesForSite(siteId).then(function(languages) {
                            // Set the selected experience in the shared data service

                            var language = params.language ? languages.find(function(language) {
                                return language.isocode === params.language;
                            }) : languages[0];

                            var defaultExperience = lodash.cloneDeep(params);
                            delete defaultExperience.siteId;
                            delete defaultExperience.catalogId;
                            delete defaultExperience.catalogVersion;

                            defaultExperience.siteDescriptor = siteDescriptor;
                            defaultExperience.catalogDescriptor = {
                                catalogId: catalogId,
                                catalogVersion: currentCatalogVersion.version,
                                catalogVersionUuid: currentCatalogVersion.uuid,
                                name: currentCatalog.name,
                                siteId: siteId,
                                active: currentCatalogVersion.active
                            };
                            defaultExperience.languageDescriptor = language;
                            defaultExperience.time = defaultExperience.time || null;

                            defaultExperience.productCatalogVersions = currentProductCatalogVersions;

                            return defaultExperience;
                        });

                    });
                });
            });
        };

        /**
         * @ngdoc method
         * @name experienceServiceModule.service:experienceService#updateExperiencePageId
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Used to update the page ID stored in the current experience and reloads the page to make the changes visible.
         *
         * @param {String} newPageID the ID of the page that must be stored in the current experience.
         *
         */
        ExperienceService.prototype.updateExperiencePageId = function(newPageID) {
            sharedDataService.get('experience').then(function(currentExperience) {
                if (!currentExperience) {
                    // Experience haven't been set. Thus, the experience hasn't been loaded. No need to update the
                    // experience then.
                    return;
                }

                currentExperience.pageId = newPageID;

                var experiencePath = this.getExperiencePath(currentExperience);
                $location.path(experiencePath).replace();

            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name experienceServiceModule.service:experienceService#loadExperience
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Used to update the experience with the parameters provided and reloads the page to make the changes visible. 
         *
         * @param {Object} params The object containing the paratements for the experience to be loaded.
         * @param {String} params.siteId the ID of the site that must be stored in the current experience.
         * @param {String} params.catalogId the ID of the catalog that must be stored in the current experience.
         * @param {String} params.catalogVersion the version of the catalog that must be stored in the current experience.
         * @param {String} params.pageUid the ID of the page that must be stored in the current experience.
         *
         */
        ExperienceService.prototype.loadExperience = function(params) {
            var newExperience = {
                siteDescriptor: {
                    uid: params.siteId
                },
                catalogDescriptor: {
                    catalogId: params.catalogId,
                    catalogVersion: params.catalogVersion
                },
                pageId: params.pageUid
            };

            var experiencePath = this.getExperiencePath(newExperience);
            $location.path(experiencePath).replace();
        };

        ExperienceService.prototype.updateExperiencePageContext = function(pageCatalogVersionUuid, pageId) {
            return sharedDataService.get('experience').then(function(currentExperience) {
                return catalogService.getContentCatalogsForSite(currentExperience.catalogDescriptor.siteId).then(function(catalogs) {
                    if (!currentExperience) {
                        // Experience haven't been set. Thus, the experience hasn't been loaded. No need to update the
                        // experience then.
                        return;
                    }

                    var pageCatalogVersion = lodash.flatten(catalogs.map(function(catalog) {
                        return lodash.cloneDeep(catalog.versions).map(function(version) {
                            version.catalogName = catalog.name;
                            version.catalogId = catalog.catalogId;
                            return version;
                        });

                    })).filter(function(version) {
                        return version.uuid === pageCatalogVersionUuid;
                    })[0];

                    return catalogService.getDefaultSiteForContentCatalog(pageCatalogVersion.catalogId).then(function(siteDescriptor) {

                        currentExperience.pageId = pageId;
                        currentExperience.pageContext = {
                            catalogId: pageCatalogVersion.catalogId,
                            catalogName: pageCatalogVersion.catalogName,
                            catalogVersion: pageCatalogVersion.version,
                            catalogVersionUuid: pageCatalogVersion.uuid,
                            siteId: siteDescriptor.uid,
                            active: pageCatalogVersion.active
                        };

                        return currentExperience;

                    });
                });
            }).then(function(experience) {
                crossFrameEventService.publish(EVENTS.PAGE_CHANGE, experience);
            });
        };

        ExperienceService.prototype.getExperiencePath = function(experience) {
            return STOREFRONT_PATH_WITH_PAGE_ID
                .replace(":siteId", experience.siteDescriptor.uid)
                .replace(":catalogId", experience.catalogDescriptor.catalogId)
                .replace(":catalogVersion", experience.catalogDescriptor.catalogVersion)
                .replace(":pageId", experience.pageId);
        };

        /**
         * @ngdoc method
         * @name experienceServiceModule.service:experienceService#getCurrentExperience
         * @methodOf experienceServiceModule.service:experienceService
         *
         * @description
         * Retrieves the active experience. 
         *
         * @returns {object} an experience
         */
        ExperienceService.prototype.getCurrentExperience = function() {
            return sharedDataService.get('experience');
        };

        return new ExperienceService('experienceService');
    });
