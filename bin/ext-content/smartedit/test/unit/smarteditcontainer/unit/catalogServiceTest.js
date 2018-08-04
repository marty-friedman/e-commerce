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
describe('catalogService', function() {

    var catalogService, urlService, sharedDataService, systemEventService;
    var siteService, catalogRestService, $q, $rootScope;

    var ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE = {
        "active": true,
        "pageDisplayConditions": [{
            "options": [{
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "ProductPage"
        }, {
            "options": [{
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "CategoryPage"
        }, {
            "options": [{
                "id": "PRIMARY",
                "label": "page.displaycondition.primary",
                "value": "PRIMARY"
            }, {
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "ContentPage"
        }],
        "uuid": "electronicsContentCatalog/Online",
        "version": "Online"
    };
    var ELECTRONICS_CONTENT_CATALOG_1_STAGED_NON_ACTIVE = {
        "active": false,
        "pageDisplayConditions": [{
            "options": [{
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "ProductPage"
        }, {
            "options": [{
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "CategoryPage"
        }, {
            "options": [{
                "id": "PRIMARY",
                "label": "page.displaycondition.primary",
                "value": "PRIMARY"
            }, {
                "id": "VARIATION",
                "label": "page.displaycondition.variation",
                "value": "VARIATION"
            }],
            "typecode": "ContentPage"
        }],
        "thumbnailUrl": "/medias/Homepage.png?context=bWFzdGVyfGltYWdlc3w4ODA0OXxpbWFnZS9wbmd8aW1hZ2VzL2hkYS9oYjgvODc5Njk0MDAwOTUwMi5wbmd8MDAzYmVlMzYxNmVjMjYwMDVlMThlZjc4MWY5ZmE2MWJjMzhhZmNlM2MzODE1ZjM3MzhlOGIzMTBmODI3NDg3Mw&attachment=true",
        "uuid": "electronicsContentCatalog/Staged",
        "version": "Staged"
    };
    var ELECTRONICS_CONTENT_CATALOG_1_MOCK = {
        "catalogId": "electronicsContentCatalog",
        "name": {
            "en": "Electronics Content Catalog",
            "de": "Elektronikkatalog",
            "ja": "エレクトロニクス コンテンツ カタログ",
            "zh": "电子产品内容目录"
        },
        "versions": [ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE, ELECTRONICS_CONTENT_CATALOG_1_STAGED_NON_ACTIVE]
    };
    var ELECTRONICS_CONTENT_CATALOG_2_MOCK = {
        "catalogId": "electronics-euContentCatalog",
        "name": {
            "en": "Electronics Content Catalog EU",
            "de": "Elektronikkatalog EU",
            "ja": "エレクトロニクス コンテンツ カタログ EU",
            "zh": "电子产品内容目录 EU"
        },
        "versions": [{
            "active": true,
            "pageDisplayConditions": [{
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }],
                "typecode": "ProductPage"
            }, {
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }],
                "typecode": "CategoryPage"
            }, {
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }, {
                    "id": "VARIATION",
                    "label": "page.displaycondition.variation",
                    "value": "VARIATION"
                }],
                "typecode": "ContentPage"
            }],
            "uuid": "electronics-euContentCatalog/Online",
            "version": "Online"
        }, {
            "active": false,
            "pageDisplayConditions": [{
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }],
                "typecode": "ProductPage"
            }, {
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }],
                "typecode": "CategoryPage"
            }, {
                "options": [{
                    "id": "PRIMARY",
                    "label": "page.displaycondition.primary",
                    "value": "PRIMARY"
                }],
                "typecode": "ContentPage"
            }],
            "uuid": "electronics-euContentCatalog/Staged",
            "version": "Staged"
        }]
    };
    var CONTENT_CATALOGS_ELECTRONICS_MOCK = {
        "catalogs": [ELECTRONICS_CONTENT_CATALOG_1_MOCK, ELECTRONICS_CONTENT_CATALOG_2_MOCK]
    };

    var APPAREL_CONTENT_CATALOG_1_ONLINE_ACTIVE = {
        "active": true,
        "pageDisplayConditions": [],
        "uuid": "apparelContentCatalog/Online",
        "version": "Online"
    };
    var APPAREL_CONTENT_CATALOG_1_MOCK = {
        "catalogId": "apparelContentCatalog",
        "name": {
            "en": "Apparel Content Catalog",
            "de": "Apparelkatalog"
        },
        "versions": [APPAREL_CONTENT_CATALOG_1_ONLINE_ACTIVE]
    };
    var CONTENT_CATALOGS_APPAREL_MOCK = {
        "catalogs": [APPAREL_CONTENT_CATALOG_1_MOCK]
    };

    var SITES_MOCKS = [{
        uid: 'electronics',
        contentCatalogs: ['fakeCatalogId', ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId],
        name: {
            en: 'Electronics Site',
            de: 'Elektronik-Website'
        }
    }, {
        uid: 'apparel',
        contentCatalogs: ['apparel-deContentCatalog'],
        name: {
            de: 'Bekleidungs-Website DE',
            en: 'Apparel Site DE'
        }
    }];

    var PRODUCT_CATALOGS_MOCKS = {
        "catalogs": [{
            "catalogId": "apparelProductCatalog",
            "name": {
                "en": "Apparel Product Catalog",
                "de": "Produktkatalog Kleidung"
            },
            "versions": [{
                "active": true,
                "uuid": "apparelProductCatalog/Online",
                "version": "Online"
            }, {
                "active": false,
                "uuid": "apparelProductCatalog/Staged",
                "version": "Staged"
            }]
        }]
    };

    var EVENTS = {
        AUTHORIZATION_SUCCESS: 'auth success event'
    };

    beforeEach(module('smarteditServicesModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
        $provide.value('sharedDataService', sharedDataService);

        urlService = jasmine.createSpyObj('urlService', ['buildUriContext']);
        $provide.value('urlService', urlService);
    }));

    beforeEach(module('eventServiceModule', function($provide) {
        systemEventService = jasmine.createSpyObj('systemEventService', ['registerEventHandler']);
        systemEventService.registerEventHandler.and.returnValue(null);
        $provide.value('systemEventService', systemEventService);
        $provide.constant('EVENTS', EVENTS);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        siteService = jasmine.createSpyObj('siteService', ['getSites']);
        $provide.value('siteService', siteService);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        catalogRestService = jasmine.createSpyObj('catalogRestService', ['get']);
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(catalogRestService);
        $provide.value('restServiceFactory', restServiceFactory);
    }));

    beforeEach(module('catalogServiceModule'));

    beforeEach(inject(function(_catalogService_, _$q_, _$rootScope_) {
        catalogService = _catalogService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('GIVEN catalog rest service call fails WHEN I request the list of catalogs THEN it should return a rejected promise', function() {
        // GIVEN
        catalogRESTcallFails();

        // WHEN
        var promise = catalogService.getCatalogsForSite('electronics');

        // THEN
        expect(promise).toBeRejected();
    });

    it('GIVEN content catalogs rest service call succeeds WHEN I request the list of content catalogs THEN it should return a promise resolving to a list of content catalogs', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.getContentCatalogsForSite('electronics');

        // THEN
        expect(promise).toBeResolvedWithData(CONTENT_CATALOGS_ELECTRONICS_MOCK.catalogs);
    });

    //FIXME : this method does not seem to be safe for same catalogversion version name across multiple catalogs
    it('GIVEN content catalogs rest service call succeeds WHEN I request a catalog by siteUID and catalogVersionName THEN it should return a promise resolving to a content catalog', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.getCatalogByVersion('electronics', 'Online');

        // THEN
        expect(promise).toBeResolvedWithData([CONTENT_CATALOGS_ELECTRONICS_MOCK.catalogs[0], CONTENT_CATALOGS_ELECTRONICS_MOCK.catalogs[1]]);
    });

    it('GIVEN current content catalog version in sharedDataService is active WHEN I call isContentCatalogVersionNonActive THEN it should return false', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        sharedDataService.get.and.callFake(function() {
            return $q.when({
                siteDescriptor: {
                    uid: SITES_MOCKS[0].uid
                },
                catalogDescriptor: {
                    catalogId: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
                    catalogVersion: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version // has active: true
                }
            });
        });

        urlService.buildUriContext.and.returnValue({
            CURRENT_CONTEXT_SITE_ID: SITES_MOCKS[0].uid,
            CURRENT_CONTEXT_CATALOG: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
            CURRENT_CONTEXT_CATALOG_VERSION: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version // has active: false
        });

        // WHEN
        var promise = catalogService.isContentCatalogVersionNonActive();

        // THEN
        expect(promise).toBeResolvedWithData(false);
        expect(urlService.buildUriContext).toHaveBeenCalledWith(SITES_MOCKS[0].uid, ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId, ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
    });

    it('GIVEN uriContext has a content catalog version non active WHEN I call isContentCatalogVersionNonActive THEN it should return true', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.isContentCatalogVersionNonActive({
            CURRENT_CONTEXT_SITE_ID: SITES_MOCKS[0].uid,
            CURRENT_CONTEXT_CATALOG: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
            CURRENT_CONTEXT_CATALOG_VERSION: ELECTRONICS_CONTENT_CATALOG_1_STAGED_NON_ACTIVE.version // has active: false
        });

        // THEN
        expect(promise).toBeResolvedWithData(true);
    });

    it('GIVEN current content catalog version in sharedDataService has two content catalog versions (active and non-active) WHEN I call getContentCatalogActiveVersion THEN it should return a promise resolving to the active version', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        sharedDataService.get.and.callFake(function() {
            return $q.when({
                siteDescriptor: {
                    uid: SITES_MOCKS[0].uid
                },
                catalogDescriptor: {
                    catalogId: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
                    catalogVersion: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version
                }
            });
        });

        urlService.buildUriContext.and.returnValue({
            CURRENT_CONTEXT_SITE_ID: SITES_MOCKS[0].uid,
            CURRENT_CONTEXT_CATALOG: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
            CURRENT_CONTEXT_CATALOG_VERSION: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version
        });

        // WHEN
        var promise = catalogService.getContentCatalogActiveVersion();

        // THEN
        expect(promise).toBeResolvedWithData(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
        expect(urlService.buildUriContext).toHaveBeenCalledWith(SITES_MOCKS[0].uid, ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId, ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
    });

    it('GIVEN uriContext has a current content catalog version with two content catalog versions (active and non-active) WHEN I call getContentCatalogActiveVersion THEN it should return a promise resolving to the active version', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.getContentCatalogActiveVersion({
            CURRENT_CONTEXT_SITE_ID: SITES_MOCKS[0].uid,
            CURRENT_CONTEXT_CATALOG: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId
        });

        // THEN
        expect(promise).toBeResolvedWithData(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
    });

    it('GIVEN there is one current content catalog in sharedDataService WHEN I call getActiveContentCatalogVersionByCatalogId THEN it should return a promise resolving to the active version', function() {
        // GIVEN
        catalogRESTcallSucceeds();

        sharedDataService.get.and.callFake(function() {
            return $q.when({
                siteDescriptor: {
                    uid: SITES_MOCKS[0].uid
                },
                catalogDescriptor: {
                    catalogId: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
                    catalogVersion: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version
                }
            });
        });

        urlService.buildUriContext.and.returnValue({
            CURRENT_CONTEXT_SITE_ID: SITES_MOCKS[0].uid,
            CURRENT_CONTEXT_CATALOG: ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId,
            CURRENT_CONTEXT_CATALOG_VERSION: ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version
        });

        // WHEN
        var promise = catalogService.getActiveContentCatalogVersionByCatalogId(ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId);

        // THEN
        expect(promise).toBeResolvedWithData(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
        expect(urlService.buildUriContext).toHaveBeenCalledWith(SITES_MOCKS[0].uid, ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId, ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.version);
    });

    it('GIVEN content catalogs rest service call succeeds WHEN I call getDefaultSiteForContentCatalog with the Electronics content catalog Id THEN it should return a promise resolving to the site that contains the Electronics catalog', function() {
        // GIVEN
        catalogRESTcallSucceeds();
        siteServiceCallSucceeds();

        var promise = catalogService.getDefaultSiteForContentCatalog(ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId);

        // THEN
        expect(promise).toBeResolvedWithData(SITES_MOCKS[0]);
    });

    it('GIVEN content catalogs rest service call succeeds WHEN I call getCatalogVersionByUuid with the Electronics active content catalog uuid THEN it should return a promise resolving to the Electronics active content catalog', function() {
        // GIVEN
        catalogRESTcallSucceeds();
        siteServiceCallSucceeds();

        var promise = catalogService.getCatalogVersionByUuid(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.uuid);

        var expectedCatalog = ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE;
        expectedCatalog.siteId = SITES_MOCKS[0].uid;
        expectedCatalog.catalogName = ELECTRONICS_CONTENT_CATALOG_1_MOCK.name;
        expectedCatalog.catalogId = ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId;
        expect(promise).toBeResolvedWithData(expectedCatalog);
    });

    it('GIVEN content catalogs rest service call succeeds WHEN I call getCatalogVersionByUuid with the Electronics active content catalog uuid THEN it should return a promise resolving to the Electronics active content catalog', function() {
        // GIVEN
        catalogRESTcallSucceeds();
        siteServiceCallSucceeds();

        var promise = catalogService.getCatalogVersionByUuid(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE.uuid, 'electronics');

        var expectedCatalog = angular.copy(ELECTRONICS_CONTENT_CATALOG_1_ONLINE_ACTIVE);
        expectedCatalog.siteDescriptor = SITES_MOCKS[0];
        expectedCatalog.catalogName = ELECTRONICS_CONTENT_CATALOG_1_MOCK.name;
        expectedCatalog.catalogId = ELECTRONICS_CONTENT_CATALOG_1_MOCK.catalogId;
        expectedCatalog.siteId = SITES_MOCKS[0].uid;
        expect(promise).toBeResolvedWithData(expectedCatalog);
    });

    it('GIVEN site service call fails WHEN I request a list of all catalogs grouped THEN it should return rejected promise', function() {
        //GIVEN
        siteServiceCallFails();

        //WHEN
        var promise = catalogService.getAllCatalogsGroupedById();

        //THEN
        expect(promise).toBeRejected();
    });

    it('GIVEN site service call succeeds and catalog REST call fails WHEN I request a list of all catalogs grouped THEN it should return rejected promise', function() {
        //GIVEN
        siteServiceCallSucceeds();
        catalogRESTcallFails();

        //WHEN
        var promise = catalogService.getAllCatalogsGroupedById();

        //THEN
        expect(promise).toBeRejected();
    });

    it('GIVEN product content catalogs rest service call succeeds WHEN I request the list of prodict content catalogs THEN it should return a promise resolving to a list of product content catalogs', function() {
        // GIVEN
        productCatalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.getProductCatalogsForSite();

        // THEN
        expect(promise).toBeResolvedWithData(PRODUCT_CATALOGS_MOCKS.catalogs);
    });

    it('GIVEN product content catalogs rest service call succeeds WHEN I call getActiveProductCatalogVersionByCatalogId with a given product catalog id THEN it should return a promise resolving to the product catalog version', function() {
        // GIVEN
        productCatalogRESTcallSucceeds();

        // WHEN
        var promise = catalogService.getActiveProductCatalogVersionByCatalogId(PRODUCT_CATALOGS_MOCKS.catalogs[0].catalogId);

        // THEN
        expect(promise).toBeResolvedWithData(PRODUCT_CATALOGS_MOCKS.catalogs[0].versions[0].version);
    });

    it('WHEN the catalogService is created THEN the right event listeners are registered', function() {
        expect(systemEventService.registerEventHandler.calls.mostRecent().args[0]).toEqual(EVENTS.AUTHORIZATION_SUCCESS);
    });

    // Helpers functions
    function catalogRESTcallSucceeds() {
        catalogRestService.get.and.callFake(function(siteObj) {
            return siteObj.siteUID === 'electronics' ? $q.when(CONTENT_CATALOGS_ELECTRONICS_MOCK) : $q.when(CONTENT_CATALOGS_APPAREL_MOCK);
        });
    }

    function catalogRESTcallFails() {
        catalogRestService.get.and.returnValue($q.reject());
    }

    function siteServiceCallFails() {
        siteService.getSites.and.returnValue($q.reject());
    }

    function siteServiceCallSucceeds() {
        siteService.getSites.and.returnValue($q.when(SITES_MOCKS));
    }

    function productCatalogRESTcallSucceeds() {
        catalogRestService.get.and.callFake(function() {
            return $q.when(PRODUCT_CATALOGS_MOCKS);
        });
    }

});
