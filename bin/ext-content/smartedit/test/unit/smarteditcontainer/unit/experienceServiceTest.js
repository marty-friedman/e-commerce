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
describe('experienceService', function() {

    var $q, $rootScope, $location, experienceService;
    var siteService, catalogService, languageService, crossFrameEventService, sharedDataService;
    var siteDescriptor, catalogVersionDescriptor, languageDescriptor;

    var PRODUCT_CATALOGS = [{
        catalogId: "catalog1",
        versions: [{
            active: true,
            uuid: "catalog1Version/Online",
            version: "Online"
        }, {
            active: false,
            uuid: "catalog1Version/Staged",
            version: "Staged"
        }]
    }, {
        catalogId: "catalog2",
        versions: [{
            active: true,
            uuid: "catalog2Version/Online",
            version: "Online"
        }, {
            active: false,
            uuid: "catalog2Version/Staged",
            version: "Staged"
        }]
    }];

    var ACTIVE_PRODUCT_CATALOG_VERSIONS = [{
        catalog: "catalog1",
        active: true,
        uuid: "catalog1Version/Online",
        catalogVersion: "Online",
        catalogName: undefined
    }, {
        catalog: "catalog2",
        active: true,
        uuid: "catalog2Version/Online",
        catalogVersion: "Online",
        catalogName: undefined
    }];

    beforeEach(module('crossFrameEventServiceModule', function($provide) {
        crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);

        $provide.value("crossFrameEventService", crossFrameEventService);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ["set", "get"]);
        $provide.value("sharedDataService", sharedDataService);

    }));

    beforeEach(module('experienceServiceModule', function($provide) {
        siteService = jasmine.createSpyObj('siteService', ['getSiteById']);
        siteDescriptor = {
            someProperty: Math.random()
        };

        $provide.value('siteService', siteService);

        catalogService = jasmine.createSpyObj('catalogService', ['getContentCatalogsForSite', 'getProductCatalogsForSite']);
        catalogVersionDescriptor = {
            catalogs: [{
                catalogId: "myCatalogId",
                name: 'myCatalogName',
                versions: [{
                    version: 'myCatalogVersion',
                    active: true
                }]
            }]
        };

        $provide.value('catalogService', catalogService);

        languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite']);
        languageDescriptor = {
            someProperty: Math.random()
        };

        $provide.value('languageService', languageService);

    }));

    beforeEach(inject(function(_$q_, _$rootScope_, _$location_, _experienceService_) {
        $q = _$q_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        experienceService = _experienceService_;
        spyOn(experienceService, 'updateExperiencePageContext');

        catalogService.getProductCatalogsForSite.and.returnValue($q.when(PRODUCT_CATALOGS));

    }));

    it('GIVEN a pageId has been passed to the params WHEN I call buildDefaultExperience THEN it will return an experience with a pageId', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'myCatalogId',
            name: 'myCatalogName',
            versions: [{
                version: 'myCatalogVersion',
                active: true
            }]
        }]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion',
            pageId: 'myPageId'
        });

        // THEN
        expect(promise).toBeResolvedWithData({
            pageId: 'myPageId',
            siteDescriptor: siteDescriptor,
            catalogDescriptor: {
                catalogId: 'myCatalogId',
                catalogVersion: 'myCatalogVersion',
                name: 'myCatalogName',
                siteId: 'mySiteId',
                active: true
            },
            languageDescriptor: languageDescriptor,
            time: null,
            productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
        });


    });

    it('GIVEN pageId has not been passed to the params WHEN I call buildDefaultExperience THEN it will return an experience without a pageId', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'myCatalogId',
            name: 'myCatalogName',
            versions: [{
                version: 'myCatalogVersion',
                active: true
            }]
        }]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });
        $rootScope.$apply();

        // THEN
        expect(promise).toBeResolvedWithData({
            siteDescriptor: siteDescriptor,
            catalogDescriptor: {
                catalogId: 'myCatalogId',
                catalogVersion: 'myCatalogVersion',
                name: 'myCatalogName',
                siteId: 'mySiteId',
                active: true
            },
            languageDescriptor: languageDescriptor,
            time: null,
            productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
        });
        $rootScope.$apply();

    });

    it('GIVEN a siteId, catalogId and catalogVersion, buildDefaultExperience will reconstruct an experience', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'myCatalogId',
            name: 'myCatalogName',
            versions: [{
                version: 'myCatalogVersion',
                active: true
            }]
        }]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeResolvedWithData({
            siteDescriptor: siteDescriptor,
            catalogDescriptor: {
                catalogId: 'myCatalogId',
                catalogVersion: 'myCatalogVersion',
                name: 'myCatalogName',
                siteId: 'mySiteId',
                active: true
            },
            languageDescriptor: languageDescriptor,
            time: null,
            productCatalogVersions: ACTIVE_PRODUCT_CATALOG_VERSIONS
        });

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('mySiteId');
    });

    it('GIVEN a siteId, catalogId and unknown catalogVersion, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'unknownVersion'
        });

        // THEN
        expect(promise).toBeRejectedWithData('no catalogVersionDescriptor found for myCatalogId catalogId and unknownVersion catalogVersion');

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('GIVEN a siteId, unknown catalogId and right catalogVersion, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.when(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'unknownCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeRejectedWithData('no catalogVersionDescriptor found for unknownCatalogId catalogId and myCatalogVersion catalogVersion');

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getContentCatalogsForSite).toHaveBeenCalledWith('mySiteId');
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('GIVEN a wrong siteId, buildDefaultExperience will return a rejected promise', function() {

        //GIVEN
        siteService.getSiteById.and.returnValue($q.reject(siteDescriptor));
        catalogService.getContentCatalogsForSite.and.returnValue($q.when([{
            catalogId: 'someValue',
            catalogVersion: 'someCatalogVersion'
        }, catalogVersionDescriptor]));
        languageService.getLanguagesForSite.and.returnValue($q.when([languageDescriptor, {}]));

        // WHEN
        var promise = experienceService.buildDefaultExperience({
            siteId: 'mySiteId',
            catalogId: 'myCatalogId',
            catalogVersion: 'myCatalogVersion'
        });

        // THEN
        expect(promise).toBeRejected();

        expect(siteService.getSiteById).toHaveBeenCalledWith('mySiteId');
        expect(catalogService.getContentCatalogsForSite).not.toHaveBeenCalled();
        expect(languageService.getLanguagesForSite).not.toHaveBeenCalled();
    });

    it('WHEN updateExperiencePageID is called THEN it retrieves the current experience, changes it and re-initializes the catalog', function() {
        // Arrange
        var newPageId = 'some new page ID';
        var oldPageId = 'some old page ID';
        var url = 'some url';

        var experience = {
            pageId: oldPageId
        };
        var newExperience = {
            pageId: newPageId
        };

        sharedDataService.get.and.returnValue($q.when(experience));
        spyOn(experienceService, 'getExperiencePath').and.returnValue(url);
        spyOn($location, 'path').and.returnValue({
            replace: function() {}
        });

        // Act
        experienceService.updateExperiencePageId(newPageId);
        $rootScope.$digest();

        // Assert
        expect(sharedDataService.get).toHaveBeenCalledWith('experience');
        expect(experienceService.getExperiencePath).toHaveBeenCalledWith(newExperience);
        expect($location.path).toHaveBeenCalledWith(url);
    });

    it('WHEN getExperiencePath is called THEN it returns the right URL', function() {
        // Arrange
        var siteId = 'someSite';
        var catalogId = 'someCatalog';
        var catalogVersion = 'someVersion';
        var pageId = 'somePageId';

        var experience = {
            siteDescriptor: {
                uid: siteId
            },
            catalogDescriptor: {
                catalogId: catalogId,
                catalogVersion: catalogVersion
            },
            pageId: pageId
        };

        // Act
        var result = experienceService.getExperiencePath(experience);

        // Assert
        expect(result).toBe('/storefront/' + siteId + '/' + catalogId + "/" + catalogVersion + '/' + pageId);
    });

    it('WHEN loadExperience is called it creates and reloads the page', function() {
        // GIVEN 
        var siteId = 'someSite';
        var catalogId = 'someCatalog';
        var catalogVersion = 'someVersion';
        var pageId = 'somePageId';
        var expectedUrl = '/storefront/' + siteId + '/' + catalogId + "/" + catalogVersion + '/' + pageId;

        spyOn($location, 'path').and.returnValue({
            replace: function() {}
        });

        // WHEN 
        experienceService.loadExperience({
            siteId: siteId,
            catalogId: catalogId,
            catalogVersion: catalogVersion,
            pageUid: pageId
        });

        // THEN
        expect($location.path).toHaveBeenCalledWith(expectedUrl);
    });
});
