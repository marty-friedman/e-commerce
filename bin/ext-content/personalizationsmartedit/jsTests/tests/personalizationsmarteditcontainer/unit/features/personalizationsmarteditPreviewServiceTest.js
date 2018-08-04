/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('personalizationsmarteditPreviewService', function() {

    var personalizationsmarteditPreviewService;
    var sharedDataService, personalizationsmarteditRestService, personalizationsmarteditUtils, personalizationsmarteditMessageHandler, personalizationsmarteditContextService;
    var $q, $rootScope, $translate;

    beforeEach(module('sharedDataServiceModule', function($provide) {
        sharedDataService = jasmine.createSpyObj('sharedDataService', ['set', 'get']);
        $provide.value("sharedDataService", sharedDataService);
    }));

    beforeEach(module('personalizationsmarteditRestServiceModule', function($provide) {
        personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getPreviewTicket', 'updatePreviewTicket', 'createPreviewTicket']);
        $provide.value("personalizationsmarteditRestService", personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditCommons', function($provide) {

        $translate = jasmine.createSpyObj('$translate', ['instant']);
        $provide.value('$translate', $translate);
        $provide.value('translateFilter', function(data) {
            return data;
        });

        personalizationsmarteditUtils = jasmine.createSpyObj('personalizationsmarteditUtils', ['getVariationCodes']);
        $provide.value("personalizationsmarteditUtils", personalizationsmarteditUtils);

        personalizationsmarteditMessageHandler = jasmine.createSpyObj('personalizationsmarteditMessageHandler', ['sendError', 'sendInformation', 'sendSuccess', 'sendWarning']);
        $provide.value("personalizationsmarteditMessageHandler", personalizationsmarteditMessageHandler);
    }));

    beforeEach(module('personalizationsmarteditContextServiceModule', function($provide) {
        personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['refreshPreviewData', 'getSeData']);
        $provide.value("personalizationsmarteditContextService", personalizationsmarteditContextService);
    }));

    beforeEach(module('personalizationsmarteditPreviewServiceModule'));
    beforeEach(inject(function(_personalizationsmarteditPreviewService_, _$q_, _$rootScope_) {
        personalizationsmarteditPreviewService = _personalizationsmarteditPreviewService_;
        $q = _$q_;
        $rootScope = _$rootScope_;

        sharedDataService.get.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({});
            return deferred.promise;
        });
    }));

    describe('createPreviewTicket', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.createPreviewTicket).toBeDefined();
        });

    });

    describe('updatePreviewTicketWithVariations', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.updatePreviewTicketWithVariations).toBeDefined();
        });

        it('if called with array should remove personalization data from preview', function() {

            spyOn(personalizationsmarteditPreviewService, 'updatePreviewTicketWithVariations');

            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations.and.returnValue($q.defer().promise);
            // given
            var previewTicketId = 'previewTicketId';
            // when
            personalizationsmarteditPreviewService.removePersonalizationDataFromPreview(previewTicketId);
            // then
            expect(personalizationsmarteditPreviewService.updatePreviewTicketWithVariations).toHaveBeenCalledWith(previewTicketId, []);
        });

    });

    describe('removePersonalizationDataFromPreview', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.removePersonalizationDataFromPreview).toBeDefined();
        });

    });

    describe('storePreviewTicketData', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditPreviewService.storePreviewTicketData).toBeDefined();
        });

        it('should call shareddataservice', function() {
            // given
            var resourcePath = 'previewResourcePath';
            var ticketId = 'previewTicketId';

            // when
            personalizationsmarteditPreviewService.storePreviewTicketData(resourcePath, ticketId);

            // then
            expect(sharedDataService.set).toHaveBeenCalledWith('preview', {
                previewTicketId: ticketId,
                resourcePath: resourcePath
            });
        });

    });


    it('updates preview ticket after successful retrieving ticket with given id', function() {
        // given
        var selectedVariations = ["var1", "var2"];

        var previewTicket = {
            ticketId: "previewTicketId",
            catalog: "catalogId",
            catalogVersion: "catalogVersion",
            language: "languageIsoCode",
            resourcePath: "resourcePath",
            variations: selectedVariations
        };

        personalizationsmarteditRestService.getPreviewTicket.and.returnValue($q.when(previewTicket));
        // when
        personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicket.ticketId, selectedVariations);
        $rootScope.$digest();

        // then
        expect(personalizationsmarteditRestService.getPreviewTicket).toHaveBeenCalledWith(previewTicket.ticketId);
        expect(personalizationsmarteditRestService.updatePreviewTicket).toHaveBeenCalledWith(previewTicket);
    });

    it('creates new preview ticket after unsuccessful retrieving ticket with given id for updating preview ticket call', function() {

        spyOn(personalizationsmarteditPreviewService, 'createPreviewTicket');

        // given
        var selectedVariations = ["var1", "var2"];

        var previewTicket = {
            ticketId: "wrongPreviewTicketId"
        };

        var notFoundResponse = {
            status: 404
        };
        personalizationsmarteditRestService.getPreviewTicket.and.returnValue($q.reject(notFoundResponse));
        personalizationsmarteditUtils.getVariationCodes.and.returnValue(selectedVariations);

        // when
        personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicket.ticketId, selectedVariations);
        $rootScope.$digest();

        // then
        expect(personalizationsmarteditRestService.getPreviewTicket).toHaveBeenCalledWith(previewTicket.ticketId);
        expect(personalizationsmarteditRestService.updatePreviewTicket).not.toHaveBeenCalled();
        expect(personalizationsmarteditPreviewService.createPreviewTicket).toHaveBeenCalledWith(selectedVariations);
    });

    it('error message is being shown through calling personalizationsmarteditMessageHandler when GET for previewTicket returns 400 response status for updating preview ticket call', function() {

        spyOn(personalizationsmarteditPreviewService, 'createPreviewTicket');

        // given
        var selectedVariations = ["var1", "var2"];

        var previewTicket = {
            ticketId: "wrongPreviewTicketId"
        };

        var badRequestResponse = {
            status: 400
        };
        personalizationsmarteditRestService.getPreviewTicket.and.returnValue($q.reject(badRequestResponse));
        personalizationsmarteditUtils.getVariationCodes.and.returnValue(selectedVariations);

        // when
        personalizationsmarteditPreviewService.updatePreviewTicketWithVariations(previewTicket.ticketId, selectedVariations).then(function success() {}, function error() {});
        $rootScope.$digest();

        // then
        expect(personalizationsmarteditRestService.getPreviewTicket).toHaveBeenCalledWith(previewTicket.ticketId);
        expect(personalizationsmarteditRestService.updatePreviewTicket).not.toHaveBeenCalled();
        expect(personalizationsmarteditMessageHandler.sendError).toHaveBeenCalled();
    });

    it('creates preview ticket with default values from SE configuration and given array of variations', function() {
        spyOn(personalizationsmarteditPreviewService, 'storePreviewTicketData');

        // given
        var selectedVariations = ["var1", "var2"];

        var experience = {
            siteDescriptor: {
                name: "some name",
                previewUrl: "/someURI/?someSite=site",
                uid: "some uid"
            },
            catalogDescriptor: {
                name: "some cat name",
                catalogId: "some cat uid",
                catalogVersion: "some cat version"
            },
            languageDescriptor: {
                isocode: "some language isocode",
            },
            time: null
        };

        var configuration = {
            previewTicketURI: 'thepreviewTicketUri'
        };

        var resourcePath = configuration.domain + experience.siteDescriptor.previewUrl;

        var previewTicket = {
            catalog: experience.catalogDescriptor.catalogId,
            catalogVersion: experience.catalogDescriptor.catalogVersion,
            language: experience.languageDescriptor.isocode,
            resourcePath: resourcePath,
            variations: selectedVariations
        };

        var responsePreviewTicket = {
            ticketId: "ticketId",
            resourcePath: resourcePath
        };

        personalizationsmarteditContextService.getSeData.and.returnValue({
            seExperienceData: experience,
            seConfigurationData: configuration
        });

        personalizationsmarteditRestService.createPreviewTicket.and.returnValue($q.when(responsePreviewTicket));

        // when
        personalizationsmarteditPreviewService.createPreviewTicket(selectedVariations);
        $rootScope.$digest();

        // then
        expect(personalizationsmarteditRestService.createPreviewTicket).toHaveBeenCalledWith(previewTicket);
        expect(personalizationsmarteditPreviewService.storePreviewTicketData).toHaveBeenCalledWith(responsePreviewTicket.resourcePath, responsePreviewTicket.ticketId);
        expect(personalizationsmarteditMessageHandler.sendSuccess).toHaveBeenCalled();
    });

    it('shows error message if 400 response status has been returned after creating preview ticket call', function() {
        spyOn(personalizationsmarteditPreviewService, 'storePreviewTicketData');

        // given
        var selectedVariations = ["var1", "var2"];

        var experience = {
            siteDescriptor: {
                name: "some name",
                previewUrl: "/someURI/?someSite=site",
                uid: "some uid"
            },
            catalogDescriptor: {
                name: "some cat name",
                catalogId: "some cat uid",
                catalogVersion: "some cat version"
            },
            languageDescriptor: {
                isocode: "some language isocode",
            },
            time: null
        };

        var configuration = {
            previewTicketURI: 'thepreviewTicketUri'
        };

        var badRequestResponse = {
            status: 400
        };

        personalizationsmarteditContextService.getSeData.and.returnValue({
            seExperienceData: experience,
            seConfigurationData: configuration
        });

        personalizationsmarteditRestService.createPreviewTicket.and.returnValue($q.reject(badRequestResponse));

        // when
        personalizationsmarteditPreviewService.createPreviewTicket(selectedVariations).then(function success() {}, function error() {});
        $rootScope.$digest();

        // then
        expect(personalizationsmarteditMessageHandler.sendError).toHaveBeenCalled();
    });
});
