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
describe('personalizationsmarteditComponentHandlerServiceModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var mockHtml = '<div data-smartedit-personalization-action-id="myAction" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div><div data-smartedit-container-source-id="myContainerSource" data-smartedit-container-id="myContainer" data-smartedit-container-type="CxCmsComponentContainer" data-smartedit-component-id="myId" data-smartedit-component-type="myType"></div>';
    var mockHtmlWithSlot = '<div data-smartedit-component-id="anotherIdSlot" data-smartedit-component-type="ContentSlot">' + mockHtml + '</div>';

    var personalizationsmarteditComponentHandlerService, CONTAINER_ID_ATTRIBUTE, ID_ATTRIBUTE;

    beforeEach(module('personalizationsmarteditComponentHandlerServiceModule'));
    beforeEach(inject(function(_personalizationsmarteditComponentHandlerService_, _CONTAINER_ID_ATTRIBUTE_, _ID_ATTRIBUTE_) {
        personalizationsmarteditComponentHandlerService = _personalizationsmarteditComponentHandlerService_;
        CONTAINER_ID_ATTRIBUTE = _CONTAINER_ID_ATTRIBUTE_;
        ID_ATTRIBUTE = _ID_ATTRIBUTE_;

        mockModules.componentHandlerService.getAllSlotsSelector.and.callFake(function() {
            return "";
        });

        mockModules.componentHandlerService.getFromSelector.and.callFake(function(selector) {
            return angular.element(mockHtmlWithSlot).find(selector);
        });
    }));

    describe('getParentContainerIdForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent).toBeDefined();
        });

        it('should return container id if element exists', function() {
            // given
            var element = angular.element(mockHtml);
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(element);
            // then
            expect(ret).toBe("myContainer");
        });

        it('should return undefined if element doesnt exists', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent(element);
            // then
            expect(ret).toBe(undefined);
        });

    });

    describe('getParentContainerForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditComponentHandlerService.getParentContainerForComponent).toBeDefined();
        });

        it('should return container id if element exists', function() {
            // given
            var element = angular.element(mockHtml);
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(element);
            // then
            expect(ret.attr(CONTAINER_ID_ATTRIBUTE)).toBe("myContainer");
        });

        it('should return empty array if element element doesnt exists', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(element);
            // then
            expect(ret.length).toBe(0);
        });

    });

    describe('getParentSlotIdForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent).toBeDefined();
        });

        it('should return null if element doesnt exist', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(element);
            // then
            expect(ret).toBe(undefined);
        });

    });

    describe('getParentSlotForComponent', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditComponentHandlerService.getParentSlotForComponent).toBeDefined();
        });

        it('should return slot id if element exist', function() {
            // given
            var element = angular.element(mockHtmlWithSlot);
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(element);
            // then
            expect(ret.attr(ID_ATTRIBUTE)).toBe("anotherIdSlot");
        });

        it('should return empty array if element doesnt exist', function() {
            // given
            var element = angular.element("");
            // when
            var ret = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(element);
            // then
            expect(ret.length).toBe(0);
        });

    });

    describe('getContainerSourceIdForContainerId', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId).toBeDefined();
        });

        it('should return container source id if container with specific id exist', function() {
            // when
            var ret = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId("myContainer");
            // then
            expect(ret).toBe("myContainerSource");
        });

        it('should return empty string if container with specific id doesnt exist', function() {
            // when
            var ret = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId("myContainerNotExist");
            // then
            expect(ret).toBe("");
        });

    });

});
