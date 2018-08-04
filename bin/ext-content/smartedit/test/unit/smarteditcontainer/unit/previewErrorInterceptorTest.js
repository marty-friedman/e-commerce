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
describe('preview resource error interceptor', function() {
    var $httpBackend;
    var previewErrorInterceptor;
    var iFrameManager;
    var PREVIEW_RESOURCE_URI = '/previewwebservices/v1/preview';

    beforeEach(module('previewErrorInterceptorModule', function($provide) {
        iFrameManager = jasmine.createSpyObj('iFrameManager', ['setCurrentLocation']);
        $provide.value('iFrameManager', iFrameManager);
    }));

    beforeEach(module('resourceLocationsModule', function($provide) {
        $provide.value(PREVIEW_RESOURCE_URI, PREVIEW_RESOURCE_URI);
    }));

    beforeEach(inject(function(_previewErrorInterceptor_, _$httpBackend_) {
        previewErrorInterceptor = _previewErrorInterceptor_;
        $httpBackend = _$httpBackend_;
    }));

    it('should match predicate for a xhr request to preview resource uri that returns a 400 response code with a pageId and an unknow identifier error type', function() {
        // GIVEN
        var mockResponse = {
            config: {
                method: 'GET',
                url: PREVIEW_RESOURCE_URI,
                data: {
                    pageId: 1
                }
            },
            data: {
                errors: [{
                    type: 'UnknownIdentifierError'
                }]
            },
            status: 400
        };

        // WHEN
        var matchPredicate = previewErrorInterceptor.predicate(mockResponse);

        // THEN
        expect(matchPredicate).toBe(true);
    });

    it('should not match predicate for a xhr request to a non preview resource uri with a 400 response code', function() {
        // GIVEN
        var mockResponse = {
            config: {
                method: 'GET',
                url: '/any_url',
                data: {}
            },
            data: {},
            status: 400
        };

        // WHEN
        var matchPredicate = previewErrorInterceptor.predicate(mockResponse);

        // THEN
        expect(matchPredicate).toBe(false);
    });

    it('should set iframeManager current location to null for a match predicate', function() {
        var mockResponse = {
            config: {
                method: 'GET',
                url: PREVIEW_RESOURCE_URI,
                data: {
                    pageId: 1
                }
            },
            data: {
                errors: [{
                    type: 'UnknownIdentifierError'
                }]
            },
            status: 400
        };
        var finalResponse = {
            mockValue: 1
        };
        $httpBackend.expectGET(mockResponse.config.url).respond(finalResponse);

        previewErrorInterceptor.responseError(mockResponse).then(function(success) {
            expect(success.data).toEqual(finalResponse);
        }, function(error) {
            expect(error).fail('the request should have been successful');
        });
        expect(iFrameManager.setCurrentLocation).toHaveBeenCalledWith(null);

        $httpBackend.flush();
    });

});
