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
describe('personalizationsmarteditContextService', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditContextService;

    beforeEach(module('personalizationsmarteditContextServiceModule'));
    beforeEach(inject(function(_personalizationsmarteditContextService_) {
        personalizationsmarteditContextService = _personalizationsmarteditContextService_;
    }));

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
