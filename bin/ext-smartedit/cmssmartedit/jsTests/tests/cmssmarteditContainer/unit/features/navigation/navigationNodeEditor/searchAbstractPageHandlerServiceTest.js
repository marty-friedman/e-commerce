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
/* jshint unused:false, undef:false */
describe('SearchAbstractPageHandlerService - ', function() {

    var searchAbstractPageHandlerService, mockRestServiceFactory, mockCMSItemsRestService, cmsitemsUri;
    var pageListService, $q, $rootScope;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };
    var mask = '';

    beforeEach(module('functionsModule'));

    beforeEach(module('resourceLocationsModule', function($provide) {
        $provide.constant("CONTEXT_CATALOG", "CURRENT_CONTEXT_CATALOG");
        $provide.constant("CONTEXT_CATALOG_VERSION", "CURRENT_CONTEXT_CATALOG_VERSION");

        cmsitemsUri = jasmine.createSpyObj('cmsitemsUri', ['$get']);
        $provide.provider('cmsitemsUri', cmsitemsUri);

        cmsitemsUri.$get.and.returnValue('/someuri');
    }));

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('searchAbstractPageHandlerServiceModule')
            .mock('restServiceFactory', 'get')
            .service('searchAbstractPageHandlerService');
        searchAbstractPageHandlerService = harness.service;
        mockRestServiceFactory = harness.mocks.restServiceFactory;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;
    });

    beforeEach(function() {
        mockCMSItemsRestService = jasmine.createSpyObj('mockCMSItemsRestService', ['get']);
        mockRestServiceFactory.get.and.returnValue(mockCMSItemsRestService);
    });

    it('WHEN getPage is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        mockCMSItemsRestService.get.and.returnValue($q.when({
            response: pages,
            pagination: 'somePaginationData'
        }));

        //WHEN
        var promise = searchAbstractPageHandlerService.getPage('mask', 10, 0, URI_PROPERTIES);
        $rootScope.$digest();

        //THEN
        expect(mockCMSItemsRestService.get).toHaveBeenCalledWith({
            mask: 'mask',
            pageSize: 10,
            currentPage: 0,
            catalogId: 'catalog',
            catalogVersion: 'catalogVersion',
            typeCode: 'AbstractPage',
            itemSearchParams: 'pageStatus:active',
            sort: 'name:asc'
        });
        expect(promise).toBeResolvedWithData({
            pagination: 'somePaginationData',
            results: [{
                name: 'page1TitleSuffix',
                id: 'auid1',
                typeCode: 'ContentPage'
            }, {
                name: 'welcomePage',
                id: 'uid2',
                typeCode: 'ActionPage'
            }, {
                name: 'Advertise',
                id: 'uid3',
                typeCode: 'MyCustomType'
            }]
        });
    });

    it('WHEN getItem is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        mockCMSItemsRestService.get.and.returnValue($q.when({
            response: pages,
            pagination: 'somePaginationData'
        }));

        //WHEN
        var promise = searchAbstractPageHandlerService.getItem('auid1', URI_PROPERTIES);
        $rootScope.$digest();

        //THEN
        expect(mockCMSItemsRestService.get).toHaveBeenCalledWith({
            mask: 'auid1',
            pageSize: 10,
            currentPage: 0,
            catalogId: 'catalog',
            catalogVersion: 'catalogVersion',
            typeCode: 'AbstractPage',
            itemSearchParams: 'pageStatus:active'
        });
        expect(promise).toBeResolvedWithData({
            name: 'page1TitleSuffix',
            id: 'auid1',
            typeCode: 'ContentPage'
        });
    });

    it('WHEN getSearchDropdownProperties is called THEN response should be returned and resolved with given data', function() {

        //WHEN
        var properties = searchAbstractPageHandlerService.getSearchDropdownProperties();

        //THEN
        expect(properties).toEqual(propertiesResponse);
    });


    var pages = [{
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "PageTemplate",
        name: "page1TitleSuffix",
        typeCode: "ContentPage",
        uid: "auid1"
    }, {
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "ActionTemplate",
        name: "welcomePage",
        typeCode: "ActionPage",
        uid: "uid2"
    }, {
        creationtime: "2016-04-08T21:16:41+0000",
        modifiedtime: "2016-04-08T21:16:41+0000",
        pk: "8796387968048",
        template: "PageTemplate",
        name: "Advertise",
        typeCode: "MyCustomType",
        uid: "uid3"
    }];

    var URI_PROPERTIES = {
        CURRENT_CONTEXT_CATALOG: 'catalog',
        CURRENT_CONTEXT_CATALOG_VERSION: 'catalogVersion'
    };

    var propertiesResponse = {
        templateUrl: 'itemSearchHandlerTemplate.html',
        placeHolderI18nKey: 'se.cms.navigationmanagement.navnode.node.entry.dropdown.page.search',
        isPaged: true
    };

});
