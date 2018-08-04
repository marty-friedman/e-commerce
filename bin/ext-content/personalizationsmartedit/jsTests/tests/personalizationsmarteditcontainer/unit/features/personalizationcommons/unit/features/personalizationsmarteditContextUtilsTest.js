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
describe('personalizationsmarteditContextUtils', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditContextUtils;

    beforeEach(module('personalizationsmarteditContextUtilsModule'));
    beforeEach(inject(function(_personalizationsmarteditContextUtils_) {
        personalizationsmarteditContextUtils = _personalizationsmarteditContextUtils_;
    }));

    describe('clearCustomizeContext', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextUtils.clearCustomizeContext).toBeDefined();
        });

    });

    describe('clearCustomizeContextAndReloadPreview', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview).toBeDefined();
        });

        it('should call proper functions in services', function() {
            // given
            var mockVariations = [{
                name: "1"
            }, {
                name: "2"
            }];
            var mockCustomize = {
                enabled: false,
                selectedCustomization: "test",
                selectedVariations: mockVariations,
                selectedComponents: null
            };
            var mockIFrameUtils = {
                clearAndReloadPreview: function() {
                    return '';
                }
            };
            var mockContextService = {
                getCustomize: function() {
                    return mockCustomize;
                },
                setCustomize: function() {
                    return;
                }
            };

            spyOn(mockIFrameUtils, 'clearAndReloadPreview').and.callThrough();
            spyOn(mockContextService, 'getCustomize').and.callThrough();
            spyOn(mockContextService, 'setCustomize').and.callThrough();
            // when
            personalizationsmarteditContextUtils.clearCustomizeContextAndReloadPreview(mockIFrameUtils, mockContextService);

            // then
            expect(mockIFrameUtils.clearAndReloadPreview).not.toHaveBeenCalled();
            expect(mockContextService.getCustomize).toHaveBeenCalled();
            expect(mockContextService.setCustomize).toHaveBeenCalled();
        });

    });

    describe('clearCombinedViewCustomizeContext', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextUtils.clearCombinedViewCustomizeContext).toBeDefined();
        });

    });

    describe('clearCombinedViewContext', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextUtils.clearCombinedViewContext).toBeDefined();
        });

    });

    describe('clearCombinedViewContextAndReloadPreview', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview).toBeDefined();
        });

        it('should call proper functions in services and set properties to initial values', function() {
            // given
            var mockCombinedView = {
                enabled: true,
                selectedItems: []
            };
            var mockIFrameUtils = {
                clearAndReloadPreview: function() {
                    return '';
                }
            };
            var mockContextService = {
                getCombinedView: function() {
                    return mockCombinedView;
                },
                setCombinedView: function() {
                    return;
                }
            };

            spyOn(mockIFrameUtils, 'clearAndReloadPreview').and.callThrough();
            spyOn(mockContextService, 'getCombinedView').and.callThrough();
            spyOn(mockContextService, 'setCombinedView').and.callThrough();
            // when
            personalizationsmarteditContextUtils.clearCombinedViewContextAndReloadPreview(mockIFrameUtils, mockContextService);

            // then
            expect(mockIFrameUtils.clearAndReloadPreview).toHaveBeenCalled();
            expect(mockContextService.getCombinedView).toHaveBeenCalled();
            expect(mockContextService.setCombinedView).toHaveBeenCalled();
        });

    });

});
