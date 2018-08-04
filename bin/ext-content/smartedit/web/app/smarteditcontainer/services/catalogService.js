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
angular.module('catalogServiceModule', ['gatewayProxyModule', 'smarteditServicesModule', 'resourceLocationsModule', 'functionsModule', 'yLoDashModule', 'eventServiceModule'])
    /**
     * @deprecated since 6.4
     */
    .constant('CATALOG_VERSION_DETAILS_RESOURCE_URI', '/cmswebservices/v1/sites/:siteUID/catalogversiondetails')
    .constant('CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API', '/cmssmarteditwebservices/v1/sites/:siteUID/contentcatalogs')
    .constant('PRODUCT_CATALOG_VERSION_DETAILS_RESOURCE_API', '/cmssmarteditwebservices/v1/sites/:siteUID/productcatalogs')
    .service('catalogService', function(
        $q,
        $log,
        lodash,
        gatewayProxy,
        sharedDataService,
        restServiceFactory,
        operationContextService,
        siteService,
        urlService,
        systemEventService,
        EVENTS,
        CATALOG_VERSION_DETAILS_RESOURCE_URI,
        CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API,
        PRODUCT_CATALOG_VERSION_DETAILS_RESOURCE_API,
        CONTEXT_SITE_ID,
        CONTEXT_CATALOG,
        CONTEXT_CATALOG_VERSION,
        OPERATION_CONTEXT) {

        // ------------------------------------------------------------------------------------------------------------------------
        //  Deprecated
        // ------------------------------------------------------------------------------------------------------------------------
        /**
         * @deprecated since 6.4
         */
        var cache = {};

        /**
         * @deprecated since 6.4
         */
        var catalogRestService = restServiceFactory.get(CATALOG_VERSION_DETAILS_RESOURCE_URI);

        /**
         * @deprecated since 6.4
         */
        this.getCatalogsForSite = function(siteUID) {
            return cache[siteUID] ? $q.when(cache[siteUID]) : catalogRestService.get({
                siteUID: siteUID
            }).then(function(catalogsDTO) {
                cache[siteUID] = catalogsDTO.catalogVersionDetails.reduce(function(acc, catalogVersionDescriptor) {
                    if (catalogVersionDescriptor.name && catalogVersionDescriptor.catalogId && catalogVersionDescriptor.version) {
                        acc.push({
                            name: catalogVersionDescriptor.name,
                            catalogId: catalogVersionDescriptor.catalogId,
                            catalogVersion: catalogVersionDescriptor.version,
                            active: catalogVersionDescriptor.active,
                            thumbnailUrl: catalogVersionDescriptor.thumbnailUrl
                        });
                    }
                    return acc;
                }, []);
                return cache[siteUID];
            });
        };

        /**
         * @deprecated since 6.4
         */
        this.getAllCatalogsGroupedById = function() {
            return this.getAllContentCatalogsGroupedById();
        };

        // ------------------------------------------------------------------------------------------------------------------------
        //  Active
        // ------------------------------------------------------------------------------------------------------------------------
        var contentCatalogsCache = {};
        var productCatalogsCache = {};

        // =====================================================================================================================
        //  Content Catalogs 
        // =====================================================================================================================        
        var contentCatalogRestService = restServiceFactory.get(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API);

        operationContextService
            .register(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API, OPERATION_CONTEXT.CMS)
            .register(CONTENT_CATALOG_VERSION_DETAILS_RESOURCE_API, OPERATION_CONTEXT.INTERACTIVE);

        var productCatalogRestService = restServiceFactory.get(PRODUCT_CATALOG_VERSION_DETAILS_RESOURCE_API);


        // ------------------------------------------------------------------------------------------------------------------------
        //  Cache Stuffs
        // ------------------------------------------------------------------------------------------------------------------------
        function clearAllCaches() {
            cache = {};
            contentCatalogsCache = {};
            productCatalogsCache = {};
        }

        /**
         * Since the contentCatalogsCache holds all the display condition information, we need to make sure to
         * invalidate this whenever a page is created or deleted.
         */
        function clearContentCatalogCache() {
            contentCatalogsCache = {};
        }

        // ------------------------------------------------------------------------------------------------------------------------

        /**
         * Handles caching of contentCatalogs
         */
        this.getContentCatalogsForSite = function(siteUID) {
            return (contentCatalogsCache[siteUID]) ? $q.when(contentCatalogsCache[siteUID]) : contentCatalogRestService.get({
                siteUID: siteUID
            }).then(function(catalogsDTO) {
                contentCatalogsCache[siteUID] = catalogsDTO.catalogs;
                return contentCatalogsCache[siteUID];
            });
        };

        this.getAllContentCatalogsGroupedById = function() {
            return siteService.getSites().then(function(sites) {
                var promisesToResolve = sites.map(function(site) {
                    return this.getContentCatalogsForSite(site.uid).then(function(catalogs) {
                        catalogs.forEach(function(catalog) {
                            catalog.versions = catalog.versions.map(function(catalogVersion) {
                                catalogVersion.siteDescriptor = site;
                                return catalogVersion;
                            });
                        });

                        return catalogs;
                    });
                }.bind(this));

                return $q.all(promisesToResolve);
            }.bind(this));
        };

        this.getCatalogByVersion = function(siteUID, catalogVersionName) {
            return this.getContentCatalogsForSite(siteUID).then(function(catalogs) {
                return catalogs.filter(function(catalog) {
                    return catalog.versions.some(function(currentCatalogVersion) {
                        return currentCatalogVersion.version === catalogVersionName;
                    });
                });
            });
        };

        this.isContentCatalogVersionNonActive = function(_uriContext) {
            return this._getContext(_uriContext).then(function(uriContext) {
                return this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    var currentCatalog = catalogs.find(function(catalog) {
                        return catalog.catalogId === uriContext[CONTEXT_CATALOG];
                    });
                    var currentCatalogVersion = (currentCatalog) ? currentCatalog.versions.find(function(catalogVersion) {
                        return catalogVersion.version === uriContext[CONTEXT_CATALOG_VERSION];
                    }) : null;

                    if (!currentCatalogVersion) {
                        throw Error('Invalid URI ', uriContext, ". Cannot find catalog version.");
                    }

                    return !currentCatalogVersion.active;
                });
            }.bind(this));
        };

        this.getContentCatalogActiveVersion = function(_uriContext) {
            return this._getContext(_uriContext).then(function(uriContext) {
                return this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    var currentCatalog = catalogs.find(function(catalog) {
                        return catalog.catalogId === uriContext[CONTEXT_CATALOG];
                    });

                    var activeCatalogVersion = currentCatalog ? currentCatalog.versions.find(function(catalogVersion) {
                        return catalogVersion.active;
                    }) : null;

                    if (!activeCatalogVersion) {
                        throw Error('Invalid URI ', uriContext, ". Cannot find catalog version.");
                    }

                    return activeCatalogVersion.version;
                });
            }.bind(this));
        };

        this.getActiveContentCatalogVersionByCatalogId = function(contentCatalogId) {
            return this._getContext().then(function(uriContext) {
                return this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    var currentCatalog = catalogs.find(function(catalog) {
                        return catalog.catalogId === contentCatalogId;
                    });

                    var currentCatalogVersion = (currentCatalog) ? currentCatalog.versions.find(function(catalogVersion) {
                        return catalogVersion.active;
                    }) : null;

                    if (!currentCatalogVersion) {
                        throw Error('Invalid content catalog ', contentCatalogId, ". Cannot find any active catalog version.");
                    }

                    return currentCatalogVersion.version;
                });
            }.bind(this));
        };

        this.getContentCatalogVersion = function(_uriContext) {
            return this._getContext(_uriContext).then(function(uriContext) {
                return this.getContentCatalogsForSite(uriContext[CONTEXT_SITE_ID]).then(function(catalogs) {
                    var catalog = catalogs.find(function(c) {
                        return c.catalogId === uriContext[CONTEXT_CATALOG];
                    });
                    if (!catalog) {
                        throw new Error("no catalog " + uriContext[CONTEXT_CATALOG] + " found for site " + uriContext[CONTEXT_SITE_ID]);
                    }
                    var catalogVersion = catalog.versions.find(function(version) {
                        return version.version === uriContext[CONTEXT_CATALOG_VERSION];
                    });
                    if (!catalogVersion) {
                        throw new Error("no catalogVersion " + uriContext[CONTEXT_CATALOG_VERSION] + " for catalog " + uriContext[CONTEXT_CATALOG] + " and site " + uriContext[CONTEXT_SITE_ID]);
                    }
                    return catalogVersion;
                }, function(error) {
                    $log.error(error);
                });
            }.bind(this));
        };

        this.getDefaultSiteForContentCatalog = function(contentCatalogId) {
            return siteService.getSites().then(function(sites) {
                var defaultSitesForCatalog = sites.filter(function(site) {
                    // ContentCatalogs in the site object are sorted. The last one is considered
                    // the default one for a given site. 
                    var siteDefaultContentCatalog = lodash.last(site.contentCatalogs);
                    return siteDefaultContentCatalog && (siteDefaultContentCatalog === contentCatalogId);
                });

                if (defaultSitesForCatalog.length === 0) {
                    $log.warn("[catalogService] - No default site found for content catalog ", contentCatalogId);
                } else if (defaultSitesForCatalog.length > 1) {
                    $log.warn("[catalogService] - Many default sites found for content catalog ", contentCatalogId);
                }

                return defaultSitesForCatalog[0];
            });
        };

        this.getCatalogVersionByUuid = function(catalogVersionUuid, siteId) {
            return this.getAllContentCatalogsGroupedById().then(function(contentCatalogsGrouped) {
                var catalogs = lodash.reduce(contentCatalogsGrouped, function(allCatalogs, siteCatalogs) {
                    return allCatalogs.concat(siteCatalogs);
                }, []);

                var catalogVersionFound = lodash.flatten(catalogs.map(function(catalog) {
                    return lodash.cloneDeep(catalog.versions).map(function(version) {
                        version.catalogName = catalog.name;
                        version.catalogId = catalog.catalogId;
                        return version;
                    });
                })).filter(function(version) {
                    return catalogVersionUuid === version.uuid && (!siteId || siteId === version.siteDescriptor.uid);
                })[0];

                if (!catalogVersionFound) {
                    var errorMessage = 'Cannot find catalog version with UUID ' + catalogVersionUuid + (siteId ? ' in site ' + siteId : '');
                    throw new Error(errorMessage);
                }

                return this.getDefaultSiteForContentCatalog(catalogVersionFound.catalogId).then(function(defaultSite) {
                    catalogVersionFound.siteId = defaultSite.uid;
                    return catalogVersionFound;
                });
            }.bind(this));
        };

        // =====================================================================================================================
        //  Product Catalogs 
        // =====================================================================================================================

        this.getProductCatalogsForSite = function(siteUID) {
            return productCatalogsCache[siteUID] ? $q.when(productCatalogsCache[siteUID]) : productCatalogRestService.get({
                siteUID: siteUID
            }).then(function(catalogsDTO) {
                productCatalogsCache[siteUID] = catalogsDTO.catalogs;
                return productCatalogsCache[siteUID];
            }.bind(this));
        };

        this.getActiveProductCatalogVersionByCatalogId = function(productCatalogId) {
            return this.getProductCatalogsForSite(CONTEXT_SITE_ID).then(function(catalogs) {
                var currentCatalog = catalogs.find(function(catalog) {
                    return catalog.catalogId === productCatalogId;
                });

                var currentCatalogVersion = (currentCatalog) ? currentCatalog.versions.find(function(catalogVersion) {
                    return catalogVersion.active;
                }) : null;

                if (!currentCatalogVersion) {
                    throw Error('Invalid product catalog ', productCatalogId, ". Cannot find any active catalog version.");
                }

                return currentCatalogVersion.version;
            });
        };

        // =====================================================================================================================
        //  Helper Methods
        // =====================================================================================================================

        this.getCatalogVersionUUid = function(_uriContext) {
            return this.getContentCatalogVersion(_uriContext).then(function(catalogVersion) {
                return catalogVersion.uuid;
            });
        };

        this.returnActiveCatalogVersionUIDs = function(catalogs) {
            return catalogs.reduce(function(accumulator, catalog) {
                accumulator.push(catalog.versions.find(function(version) {
                    return version.active;
                }).uuid);
                return accumulator;
            }, []);
        };

        this.retrieveUriContext = function(_uriContext) {
            return this._getContext(_uriContext);
        };

        this._getContext = function(_uriContext) {
            return _uriContext ? $q.when(_uriContext) : sharedDataService.get('experience').then(function(experience) {
                if (!experience) {
                    throw "catalogService was not provided with a uriContext and could not retrive an experience from sharedDataService";
                }
                return urlService.buildUriContext(experience.siteDescriptor.uid, experience.catalogDescriptor.catalogId, experience.catalogDescriptor.catalogVersion);
            });
        };

        this.gatewayId = "catalogService";
        gatewayProxy.initForService(this);

        systemEventService.registerEventHandler(EVENTS.PAGE_CREATED, clearContentCatalogCache);
        systemEventService.registerEventHandler(EVENTS.PAGE_DELETED, clearContentCatalogCache);

        systemEventService.registerEventHandler(EVENTS.AUTHORIZATION_SUCCESS, function(evtId, evtData) {
            if (evtData.userHasChanged) {
                clearAllCaches();
            }
        }.bind(this));
    });
