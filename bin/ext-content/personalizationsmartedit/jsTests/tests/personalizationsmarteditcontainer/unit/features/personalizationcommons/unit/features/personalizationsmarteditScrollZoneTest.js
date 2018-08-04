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
describe('personalizationsmarteditScrollZone', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController;

    beforeEach(module('personalizationsmarteditScrollZone'));
    beforeEach(inject(function(_$componentController_) {
        $componentController = _$componentController_;
    }));

    describe('Component API', function() {

        it('should have proper api when initialized without parameters', function() {
            var ctrl = $componentController('personalizationsmarteditScrollZone', null);

            expect(ctrl.scrollZoneTop).toBe(true);
            expect(ctrl.scrollZoneBottom).toBe(true);
            expect(ctrl.start).toBe(false);
            expect(ctrl.elementToScroll).toEqual({});
            expect(ctrl.scrollZoneVisible).toBe(false);
            expect(ctrl.stopScroll).toBeDefined();
            expect(ctrl.scrollTop).toBeDefined();
            expect(ctrl.scrollBottom).toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.$onDestroy).toBeDefined();
            expect(ctrl.$onChanges).toBeDefined();
        });

        it('should have proper api when initialized with parameters', function() {
            var bindings = {
                scrollZoneVisible: true,
                getElementToScroll: function() {
                    return "testElement";
                }
            };
            var ctrl = $componentController('personalizationsmarteditScrollZone', null, bindings);
            ctrl.$onInit();

            expect(ctrl.scrollZoneTop).toBe(true);
            expect(ctrl.scrollZoneBottom).toBe(true);
            expect(ctrl.start).toBe(false);
            expect(ctrl.elementToScroll).toEqual("testElement");
            expect(ctrl.scrollZoneVisible).toBe(true);
            expect(ctrl.stopScroll).toBeDefined();
            expect(ctrl.scrollTop).toBeDefined();
            expect(ctrl.scrollBottom).toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.$onDestroy).toBeDefined();
            expect(ctrl.$onChanges).toBeDefined();
        });

    });

    describe('$onChanges', function() {

        it('should be defined', function() {
            var ctrl = $componentController('personalizationsmarteditScrollZone', null);
            expect(ctrl.$onChanges).toBeDefined();
        });

        it('should not set properties if called without parameters', function() {
            //given
            var ctrl = $componentController('personalizationsmarteditScrollZone', null);
            ctrl.start = false;
            ctrl.scrollZoneTop = false;
            ctrl.scrollZoneBottom = false;
            // when
            ctrl.$onChanges({});

            //then
            expect(ctrl.start).toBe(false);
            expect(ctrl.scrollZoneTop).toBe(false);
            expect(ctrl.scrollZoneBottom).toBe(false);
        });

        it('should properly set properties if called with parameters', function() {
            //given
            var ctrl = $componentController('personalizationsmarteditScrollZone', null);
            ctrl.start = false;
            ctrl.scrollZoneTop = false;
            ctrl.scrollZoneBottom = false;
            var changes = {
                scrollZoneVisible: {
                    currentValue: true
                }
            };
            // when
            ctrl.$onChanges(changes);

            //then
            expect(ctrl.start).toBe(true);
            expect(ctrl.scrollZoneTop).toBe(true);
            expect(ctrl.scrollZoneBottom).toBe(true);
        });

    });

});
