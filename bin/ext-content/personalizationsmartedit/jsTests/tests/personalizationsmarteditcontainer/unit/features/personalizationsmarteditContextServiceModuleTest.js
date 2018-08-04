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
describe('personalizationsmarteditContextServiceModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var mockConfig = {
        test: "test"
    };

    var personalizationsmarteditContextService, personalizationsmarteditContextServiceProxy, scope;

    beforeEach(module('personalizationsmarteditContextServiceModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _personalizationsmarteditContextService_) {
        mockModules.sharedDataService.get.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(mockConfig);
            return deferred.promise;
        });
        mockModules.loadConfigManagerService.loadAsObject.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve(mockConfig);
            return deferred.promise;
        });
        scope = _$rootScope_.$new();
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;
        personalizationsmarteditContextServiceProxy = personalizationsmarteditContextService.getContexServiceProxy();

        //Create spy objects
        spyOn(personalizationsmarteditContextService, 'refreshExperienceData').and.callThrough();
        spyOn(personalizationsmarteditContextService, 'refreshPreviewData').and.callThrough();
        spyOn(personalizationsmarteditContextService, 'refreshConfigurationData').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setSeData').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setCustomize').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setCombinedView').and.callThrough();
        spyOn(personalizationsmarteditContextServiceProxy, 'setPersonalization').and.callThrough();
    }));

    describe('applySynchronization', function() {

        it('after call all objects in contex service are set properly', function() {
            //After object creation properties should have default values
            expect(personalizationsmarteditContextService.seData.pageId).toBe(null);
            expect(personalizationsmarteditContextService.seData.seExperienceData).toBe(null);
            expect(personalizationsmarteditContextService.seData.seConfigurationData).toBe(null);
            expect(personalizationsmarteditContextService.seData.sePreviewData).toBe(null);
            expect(personalizationsmarteditContextService.customize.enabled).toBe(false);
            expect(personalizationsmarteditContextService.customize.selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.customize.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.customize.selectedComponents).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.enabled).toBe(false);
            expect(personalizationsmarteditContextService.combinedView.selectedItems).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize).toBeDefined();
            expect(personalizationsmarteditContextService.combinedView.customize.selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize.selectedComponents).toBe(null);
            expect(personalizationsmarteditContextService.personalization.enabled).toBe(false);

            //Set some mock properties
            var customize = {
                selectedVariations: ["mockVariation"],
                selectedCustomization: {
                    code: "mockCustomization"
                }
            };
            var seData = {
                pageId: "mainpage"
            };
            var combinedView = {
                enabled: true
            };
            var personalization = {
                enabled: true
            };

            personalizationsmarteditContextService.customize = customize;
            personalizationsmarteditContextService.seData = seData;
            personalizationsmarteditContextService.combinedView = combinedView;
            personalizationsmarteditContextService.personalization = personalization;

            //Call method and run digest cycle
            personalizationsmarteditContextService.applySynchronization();
            scope.$digest();

            //Test if methods have been called properly
            expect(personalizationsmarteditContextService.refreshExperienceData).toHaveBeenCalled();
            expect(personalizationsmarteditContextService.refreshPreviewData).toHaveBeenCalled();
            expect(personalizationsmarteditContextService.refreshConfigurationData).toHaveBeenCalled();

            expect(personalizationsmarteditContextServiceProxy.setCustomize).toHaveBeenCalledWith(customize);
            expect(personalizationsmarteditContextServiceProxy.setSeData).toHaveBeenCalledWith(seData);
            expect(personalizationsmarteditContextServiceProxy.setCombinedView).toHaveBeenCalledWith(combinedView);
            expect(personalizationsmarteditContextServiceProxy.setPersonalization).toHaveBeenCalledWith(personalization);

            //Test if properties are set properly
            expect(personalizationsmarteditContextService.customize).toBe(customize);
            expect(personalizationsmarteditContextService.seData).toBe(seData);
            expect(personalizationsmarteditContextService.combinedView).toBe(combinedView);
            expect(personalizationsmarteditContextService.personalization).toBe(personalization);
        });

    });

    describe('seData', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.seData).toBeDefined();
            expect(personalizationsmarteditContextService.seData.pageId).toBe(null);
            expect(personalizationsmarteditContextService.seData.seExperienceData).toBe(null);
            expect(personalizationsmarteditContextService.seData.seConfigurationData).toBe(null);
            expect(personalizationsmarteditContextService.seData.sePreviewData).toBe(null);
        });

        it('should properly set value', function() {
            //given
            var seData = personalizationsmarteditContextService.getSeData();
            seData.pageId = "mockMainPage";
            seData.seExperienceData = {
                mock: "mockValue"
            };
            // when
            personalizationsmarteditContextService.setSeData(seData);
            // then
            expect(personalizationsmarteditContextService.getSeData()).toBe(seData);
        });

    });

    describe('customize', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.customize).toBeDefined();
            expect(personalizationsmarteditContextService.customize.enabled).toBe(false);
            expect(personalizationsmarteditContextService.customize.selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.customize.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.customize.selectedComponents).toBe(null);
        });

        it('should properly set value', function() {
            //given
            var customize = personalizationsmarteditContextService.getCustomize();
            customize.selectedCustomization = {
                code: "mockCustomization"
            };
            customize.selectedVariations = [{
                code: "mockVar1"
            }, {
                code: "mockVar2"
            }];
            customize.enabled = true;
            // when
            personalizationsmarteditContextService.setCustomize(customize);
            // then
            expect(personalizationsmarteditContextService.getCustomize()).toBe(customize);
        });

    });

    describe('combinedView', function() {

        it('should be defined and initialized', function() {
            var customize = personalizationsmarteditContextService.getCustomize();
            expect(personalizationsmarteditContextService.combinedView).toBeDefined();
            expect(personalizationsmarteditContextService.combinedView.enabled).toBe(false);
            expect(personalizationsmarteditContextService.combinedView.selectedItems).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize).toEqual(customize);
            expect(personalizationsmarteditContextService.combinedView.customize).toBeDefined();
            expect(personalizationsmarteditContextService.combinedView.customize.selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize.selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.combinedView.customize.selectedComponents).toBe(null);
        });

        it('should properly set value', function() {
            //given
            var combinedView = personalizationsmarteditContextService.getCombinedView();
            combinedView.enabled = true;
            combinedView.selectedItems = [{}, {}];
            // when
            personalizationsmarteditContextService.setCombinedView(combinedView);
            // then
            expect(personalizationsmarteditContextService.getCombinedView()).toBe(combinedView);
        });

    });

    describe('personalization', function() {

        it('should be defined and initialized', function() {
            expect(personalizationsmarteditContextService.personalization).toBeDefined();
            expect(personalizationsmarteditContextService.personalization.enabled).toBe(false);
        });

        it('should properly set value', function() {
            //given
            var personalization = personalizationsmarteditContextService.getPersonalization();
            personalization.enabled = true;
            // when
            personalizationsmarteditContextService.setPersonalization(personalization);
            // then
            expect(personalizationsmarteditContextService.getPersonalization()).toBe(personalization);
        });

    });

});
