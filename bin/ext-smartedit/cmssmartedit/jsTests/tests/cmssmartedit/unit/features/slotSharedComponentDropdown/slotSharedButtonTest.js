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
describe('slotSharedButtonModule', function() {

    describe('slotSharedButtonController', function() {

        var controller, crossFrameEventService;
        var mocks;

        beforeEach(function() {
            var harness = AngularUnitTestHelper.prepareModule('slotSharedButtonModule')
                .mock('slotSharedService', 'isSlotShared').and.returnResolvedPromise(true)
                .mock('crossFrameEventService', 'subscribe')
                .mockConstant('EVENT_OUTER_FRAME_CLICKED', 'EVENT_OUTER_FRAME_CLICKED')
                .controller('slotSharedButtonController', {});

            controller = harness.controller;
            crossFrameEventService = harness.mocks.crossFrameEventService;
            controller.setRemainOpen = function() {};

            mocks = harness.mocks;
        });

        it('.isPopupOpened is initialized to false', function() {
            expect(controller.isPopupOpened).toEqual(false);
        });

        it('.isPopupOpenedPreviousValue is initialized to false', function() {
            expect(controller.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('will set isPopupOpenedPreviousValue to true when .isPopupOpened is true on a $doCheck() lifecycle call', function() {
            // Given
            controller.isPopupOpenedPreviousValue = false;
            controller.isPopupOpened = true;

            // When
            controller.$doCheck();

            // Then
            expect(controller.isPopupOpenedPreviousValue).toEqual(true);
        });

        it('will set isPopupOpenedPreviousValue to false when .isPopupOpened is false on a $doCheck() lifecycle call', function() {
            // Given
            controller.isPopupOpenedPreviousValue = true;
            controller.isPopupOpened = false;

            // When
            controller.$doCheck();

            // Then
            expect(controller.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('will set isPopupOpenedPreviousValue to false when .isPopupOpened is false on a $doCheck() lifecycle call', function() {
            // Given
            controller.isPopupOpenedPreviousValue = true;
            controller.isPopupOpened = false;

            // When
            controller.$doCheck();

            // Then
            expect(controller.isPopupOpenedPreviousValue).toEqual(false);
        });

        it('will not change when isPopupOpenedPreviousValue and .isPopupOpened are both true on a $doCheck() lifecycle call', function() {
            // Given
            controller.isPopupOpenedPreviousValue = true;
            controller.isPopupOpened = true;

            // When
            controller.$doCheck();

            // Then
            expect(controller.isPopupOpenedPreviousValue).toEqual(true);
        });

        it('will not change when .isPopupOpenedPreviousValue and .isPopupOpened are both false on a $doCheck() lifecycle call', function() {
            // Given
            controller.isPopupOpenedPreviousValue = false;
            controller.isPopupOpened = false;

            // When
            controller.$doCheck();

            // Then
            expect(controller.isPopupOpenedPreviousValue).toEqual(false);
        });

        //TODO: This test cannot be run while there is a $window.location.reload(); in the slotSharedButtonController
        xit('.isPopupOpened is set to false when convertToNonSharedSlotAndCloneComponents is called', function() {
            // Given
            controller.isPopupOpened = true;

            // When
            controller.convertToNonSharedSlotAndCloneComponents();

            // Then
            expect(controller.isPopupOpened).toEqual(false);
        });

        //TODO: This test cannot be run while there is a $window.location.reload(); in the slotSharedButtonController
        xit('.isPopupOpened is set to false when convertToNonSharedSlotAndRemoveComponents is called', function() {
            // Given
            controller.isPopupOpened = true;

            // When
            controller.convertToNonSharedSlotAndRemoveComponents();

            // Then
            expect(controller.isPopupOpened).toEqual(false);
        });
    });
});
