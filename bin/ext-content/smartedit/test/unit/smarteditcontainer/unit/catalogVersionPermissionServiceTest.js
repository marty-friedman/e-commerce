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
describe('catalogVersionPermissionRestServiceModule', function() {
    var catalogService,
        catalogVersionPermissionRestService,
        catalogVersionPermissionService,
        $q, $rootScope;

    var uriContext = {
        CONTEXT_CATALOG: "fakeCatalogId",
        CONTEXT_CATALOG_VERSION: "Staged",
        CONTEXT_SITE_ID: "SiteId"
    };

    var uriContext_activeCatalogVersion = {
        CONTEXT_CATALOG: "fakeCatalogId",
        CONTEXT_CATALOG_VERSION: "SomeActiveCatalogVersion",
        CONTEXT_SITE_ID: "SiteId"
    };

    var catalogVersionPermissionRestServiceMeaningfulResult = {
        "permissions": [{
            "key": "read",
            "value": "true"
        }, {
            "key": "write",
            "value": "false"
        }]
    };

    var catalogVersionPermissionRestServiceAllFalse = {
        "permissions": [{
            "key": "read",
            "value": "false"
        }, {
            "key": "write",
            "value": "false"
        }]
    };

    /**
     * CatalogVersionPermissionService is mocked to return the response from Permissions web service.
     */
    beforeEach(module('catalogVersionPermissionRestServiceModule', function($provide) {
        catalogVersionPermissionRestService = jasmine.createSpyObj('catalogVersionPermissionRestService', ['getCatalogVersionPermissions']);
        $provide.value('catalogVersionPermissionRestService', catalogVersionPermissionRestService);
    }));

    /**
     * In this section a mock catalogService is created to spy on its get method that returns
     * a mocked experience.
     */
    beforeEach(module('catalogServiceModule', function($provide) {
        catalogService = jasmine.createSpyObj('catalogService', ['retrieveUriContext', 'getActiveContentCatalogVersionByCatalogId']);
        $provide.value('catalogService', catalogService);
    }));

    beforeEach(module('catalogVersionPermissionModule', function($provide) {
        $provide.constant("CONTEXT_CATALOG", 'CONTEXT_CATALOG');
        $provide.constant("CONTEXT_CATALOG_VERSION", 'CONTEXT_CATALOG_VERSION');
        $provide.constant("CONTEXT_SITE_ID", 'CONTEXT_SITE_ID');
    }));

    beforeEach(inject(function(_$q_, _$rootScope_, _catalogVersionPermissionService_) {
        catalogVersionPermissionService = _catalogVersionPermissionService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('GIVEN catalogVersionPermissionRestService that returns empty object WHEN hasReadPermissionOnCurrent/hasWritePermissionOnCurrent is called  THEN it returns false', function() {
        // GIVEN
        catalogVersionPermissionRestService.getCatalogVersionPermissions.and.returnValue($q.when({}));
        catalogService.retrieveUriContext.and.returnValue($q.when(uriContext));
        catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue($q.when(uriContext_activeCatalogVersion.CONTEXT_CATALOG_VERSION));

        // WHEN
        var promiseRead = catalogVersionPermissionService.hasReadPermissionOnCurrent();
        var promiseWrite = catalogVersionPermissionService.hasWritePermissionOnCurrent();
        $rootScope.$digest();

        // THEN
        expect(promiseRead).toBeResolvedWithData(false);
        expect(promiseWrite).toBeResolvedWithData(false);
    });

    it('GIVEN catalogVersionPermissionRestService that returns empty permissions list WHEN hasReadPermission/hasWritePermissionOnCurrent is called  THEN it returns false', function() {
        // GIVEN
        catalogVersionPermissionRestService.getCatalogVersionPermissions.and.returnValue($q.when({
            permissions: []
        }));
        catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue($q.when(uriContext_activeCatalogVersion.CONTEXT_CATALOG_VERSION));

        // WHEN
        var promiseRead = catalogVersionPermissionService.hasReadPermission(uriContext.CONTEXT_CATALOG, uriContext.CONTEXT_CATALOG_VERSION);
        var promiseWrite = catalogVersionPermissionService.hasWritePermission(uriContext.CONTEXT_CATALOG, uriContext.CONTEXT_CATALOG_VERSION);
        $rootScope.$digest();

        // THEN
        expect(promiseRead).toBeResolvedWithData(false);
        expect(promiseWrite).toBeResolvedWithData(false);
    });

    describe('CurrentCatalogPermission', function() {
        beforeEach(function() {
            catalogVersionPermissionRestService.getCatalogVersionPermissions.and.returnValue($q.when(catalogVersionPermissionRestServiceMeaningfulResult));
            catalogService.retrieveUriContext.and.returnValue($q.when(uriContext));
            catalogService.getActiveContentCatalogVersionByCatalogId.and.returnValue($q.when(uriContext_activeCatalogVersion.CONTEXT_CATALOG_VERSION));
        });

        it('WHEN hasWritePermissionOnCurrent is called THEN it retrieves catalog data from experience and calls catalogVersionPermissionRestService to get write permission for the catalog', function() {
            // WHEN

            var promise = catalogVersionPermissionService.hasWritePermissionOnCurrent();
            $rootScope.$digest();

            // THEN
            expect(catalogVersionPermissionRestService.getCatalogVersionPermissions).toHaveBeenCalledWith(
                uriContext.CONTEXT_CATALOG,
                uriContext.CONTEXT_CATALOG_VERSION
            );
            expect(promise).toBeResolvedWithData(false);
        });

        it('WHEN hasReadPermissionOnCurrent is called THEN it retrieves catalog data from experience and then calls catalogVersionPermissionRestService to get read permission for the catalog', function() {
            // WHEN
            var promise = catalogVersionPermissionService.hasReadPermissionOnCurrent();
            $rootScope.$digest();

            // THEN
            expect(catalogVersionPermissionRestService.getCatalogVersionPermissions).toHaveBeenCalledWith(
                uriContext.CONTEXT_CATALOG,
                uriContext.CONTEXT_CATALOG_VERSION
            );
            expect(promise).toBeResolvedWithData(true);
        });

        it('WHEN hasReadPermissionOnCurrent is called and the current catalog is active catalog THEN it retrieves catalog data from experience and then calls catalogVersionPermissionRestService to get read permission for the catalog', function() {
            // GIVEN
            catalogVersionPermissionRestService.getCatalogVersionPermissions.and.returnValue($q.when(catalogVersionPermissionRestServiceAllFalse));
            catalogService.retrieveUriContext.and.returnValue($q.when(uriContext_activeCatalogVersion));

            // WHEN
            var promise = catalogVersionPermissionService.hasReadPermissionOnCurrent();
            $rootScope.$digest();

            // THEN
            expect(catalogVersionPermissionRestService.getCatalogVersionPermissions).toHaveBeenCalledWith(
                uriContext_activeCatalogVersion.CONTEXT_CATALOG,
                uriContext_activeCatalogVersion.CONTEXT_CATALOG_VERSION
            );
            expect(catalogService.getActiveContentCatalogVersionByCatalogId).toHaveBeenCalledWith(
                uriContext_activeCatalogVersion.CONTEXT_CATALOG
            );
            expect(promise).toBeResolvedWithData(true);
        });
    });

    describe('AnyCatalogPermission', function() {
        beforeEach(function() {
            catalogVersionPermissionRestService.getCatalogVersionPermissions.and.returnValue($q.when(catalogVersionPermissionRestServiceMeaningfulResult));
        });

        it('WHEN hasReadPermission is called THEN calls catalogVersionPermissionRestService to get read permission for the catalogId and catalogVersion', function() {
            // WHEN
            var promise = catalogVersionPermissionService.hasReadPermission(uriContext.CONTEXT_CATALOG, uriContext.CONTEXT_CATALOG_VERSION);
            $rootScope.$digest();

            // THEN
            expect(catalogVersionPermissionRestService.getCatalogVersionPermissions).toHaveBeenCalledWith(
                uriContext.CONTEXT_CATALOG,
                uriContext.CONTEXT_CATALOG_VERSION
            );
            expect(promise).toBeResolvedWithData(true);
        });

        it('WHEN hasWritePermission is called THEN calls catalogVersionPermissionRestService to get write permission for the catalogId and catalogVersion', function() {
            // WHEN
            var promise = catalogVersionPermissionService.hasWritePermission(uriContext.CONTEXT_CATALOG, uriContext.CONTEXT_CATALOG_VERSION);
            $rootScope.$digest();

            // THEN
            expect(catalogVersionPermissionRestService.getCatalogVersionPermissions).toHaveBeenCalledWith(
                uriContext.CONTEXT_CATALOG,
                uriContext.CONTEXT_CATALOG_VERSION
            );
            expect(promise).toBeResolvedWithData(false);
        });
    });



});
