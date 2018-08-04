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
describe('Item Title Media Handler Service', function() {

    var restServiceFactory, itemTitleMediaHandlerService, restServiceMediaSearchResource, $q;

    var MEDIA_RESOURCE_URI = "MEDIA_RESOURCE_URI";

    beforeEach(module('smarteditServicesModule', function($provide) {
        restServiceMediaSearchResource = jasmine.createSpyObj('restServiceMediaSearchResource', ['get']);
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restServiceMediaSearchResource);
        $provide.value('restServiceFactory', restServiceFactory);
        $provide.value(MEDIA_RESOURCE_URI, MEDIA_RESOURCE_URI);
    }));

    beforeEach(module('itemTitleMediaHandlerServiceModule'));

    beforeEach(inject(function(_itemTitleMediaHandlerService_, _$q_) {
        itemTitleMediaHandlerService = _itemTitleMediaHandlerService_;
        $q = _$q_;
    }));

    it('should hit the media API, retrieve the media and return its code', function() {
        var entry = {
            code: 'mediaCode'
        };
        restServiceMediaSearchResource.get.and.returnValue($q.when(entry));

        var uriParameters = {
            CURRENT_CONTEXT_SITE_ID: 'siteId',
            CURRENT_CONTEXT_CATALOG: 'catalogId',
            CURRENT_CONTEXT_CATALOG_VERSION: 'catalogversion'
        };
        itemTitleMediaHandlerService.getItemTitleById("myitem", uriParameters).then(function(response) {
            expect(response).toEqual('mediaCode');
        });
        expect(restServiceFactory.get).toHaveBeenCalledWith(MEDIA_RESOURCE_URI + '/myitem');
        expect(restServiceMediaSearchResource.get).toHaveBeenCalledWith();
    });


});
