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
describe('SearchAbstractCMSComponentHandlerService - ', function() {

    var searchAbstractCMSComponentHandlerService;
    var restServiceItemsResource, $q, $rootScope;
    var uriParams = {
        siteId: 'siteId',
        catalogId: 'catalogId',
        catalogVersion: 'catalogVersion'
    };
    var mask = '';

    beforeEach(module('smarteditServicesModule', function($provide) {
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        $provide.value('restServiceFactory', restServiceFactory);
        $provide.value("ITEMS_RESOURCE_URI", "ITEMS_RESOURCE_URI");

        restServiceItemsResource = jasmine.createSpyObj('restServiceItemsResource', ['get']);
        restServiceFactory.get.and.returnValue(restServiceItemsResource);
    }));

    beforeEach(module('searchAbstractCMSComponentHandlerServiceModule'));

    beforeEach(inject(function(_searchAbstractCMSComponentHandlerService_, _$q_, _$rootScope_) {
        searchAbstractCMSComponentHandlerService = _searchAbstractCMSComponentHandlerService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('WHEN getSearchResults is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        restServiceItemsResource.get.and.returnValue($q.when(structure));

        //THEN
        $rootScope.$digest();
        expect(searchAbstractCMSComponentHandlerService.getSearchResults(mask, uriParams)).toBeResolvedWithData(resolvedPromiseData);
    });

    it('WHEN getSearchDropdownProperties is called THEN response should be returned and resolved with given data', function() {

        //WHEN
        var properties = searchAbstractCMSComponentHandlerService.getSearchDropdownProperties();

        //THEN
        expect(properties).toEqual(propertiesResponse);
    });

    var structure = {
        "componentItems": [{
            "creationtime": "2016-07-25T12:57:48+0000",
            "modifiedtime": "2016-07-28T19:41:46+0000",
            "name": "Home Page Nav Link",
            "pk": "8796130608188",
            "typeCode": "CMSLinkComponent",
            "uid": "HomepageNavLink",
            "visible": true
        }, {
            "creationtime": "2016-07-25T12:57:48+0000",
            "modifiedtime": "2016-07-28T19:41:46+0000",
            "name": "Al Merrick Link",
            "pk": "8796129363004",
            "typeCode": "CMSLinkComponent",
            "uid": "AlMerrickLink",
            "visible": true
        }, {
            "creationtime": "2016-07-25T12:57:48+0000",
            "modifiedtime": "2016-07-28T19:41:46+0000",
            "name": "Nike Link",
            "pk": "8796130804796",
            "typeCode": "CMSLinkComponent",
            "uid": "NikeLink",
            "visible": true
        }]
    };

    var resolvedPromiseData = [{
        name: 'Home Page Nav Link',
        id: 'HomepageNavLink',
        typeCode: 'CMSLinkComponent'
    }, {
        name: 'Al Merrick Link',
        id: 'AlMerrickLink',
        typeCode: 'CMSLinkComponent'
    }, {
        name: 'Nike Link',
        id: 'NikeLink',
        typeCode: 'CMSLinkComponent'
    }];

    var propertiesResponse = {
        templateUrl: 'itemSearchHandlerTemplate.html',
        placeHolderI18nKey: 'se.cms.navigationmanagement.navnode.node.entry.dropdown.component.search',
        isPaged: true
    };

});
