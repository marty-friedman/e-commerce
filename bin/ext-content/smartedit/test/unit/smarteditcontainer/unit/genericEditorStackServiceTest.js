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
describe('genericEditorStackServiceModule -', function() {

    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    var EDITOR_TO_PUSH_INFO, COMPONENT, COMPONENT_TYPE, EDITOR_ID, STACK_ID;
    var EDITOR_PUSH_TO_STACK_EVENT, EDITOR_POP_FROM_STACK_EVENT;

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    var systemEventService, genericEditorStackService, $log;

    // --------------------------------------------------------------------------------------
    // Set Up
    // --------------------------------------------------------------------------------------
    beforeEach(function() {
        COMPONENT = 'some component';
        COMPONENT_TYPE = 'some component type';
        EDITOR_ID = 'some editor id';
        STACK_ID = 'some stack id';

        EDITOR_TO_PUSH_INFO = {
            component: COMPONENT,
            componentType: COMPONENT_TYPE,
            editorId: EDITOR_ID
        };
    });

    beforeEach(module('eventServiceModule', function($provide) {
        systemEventService = jasmine.createSpyObj('systemEventService', ['registerEventHandler']);
        $provide.value('systemEventService', systemEventService);
    }));

    beforeEach(module('genericEditorStackServiceModule'));
    beforeEach(inject(function(_$log_, _EDITOR_PUSH_TO_STACK_EVENT_, _EDITOR_POP_FROM_STACK_EVENT_, _genericEditorStackService_) {
        $log = _$log_;
        genericEditorStackService = _genericEditorStackService_;
        EDITOR_PUSH_TO_STACK_EVENT = _EDITOR_PUSH_TO_STACK_EVENT_;
        EDITOR_POP_FROM_STACK_EVENT = _EDITOR_POP_FROM_STACK_EVENT_;

        spyOn($log, 'warn');
    }));

    // --------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------
    it('WHEN the service is started THEN it is properly initialized', function() {
        // THEN
        expect(systemEventService.registerEventHandler).toHaveBeenCalledWith(EDITOR_PUSH_TO_STACK_EVENT, jasmine.any(Function));
        expect(systemEventService.registerEventHandler).toHaveBeenCalledWith(EDITOR_POP_FROM_STACK_EVENT, jasmine.any(Function));
    });

    it('GIVEN there are no editors in the stack WHEN I push a new editor THEN a new stack is created AND the editor is added', function() {
        // GIVEN 
        var pushFn = systemEventService.registerEventHandler.calls.argsFor(0)[1];
        expect(genericEditorStackService.getEditorsStack(STACK_ID)).toBe(null);
        EDITOR_TO_PUSH_INFO.editorStackId = STACK_ID;

        // WHEN 
        pushFn(EDITOR_POP_FROM_STACK_EVENT, EDITOR_TO_PUSH_INFO);

        // THEN
        var stack = genericEditorStackService.getEditorsStack(STACK_ID);
        expect(stack).not.toBe(null);
        expect(stack.length).toBe(1);
        expect(stack[0].component).toBe(EDITOR_TO_PUSH_INFO.component);
        expect(stack[0].componentType).toBe(EDITOR_TO_PUSH_INFO.componentType);
        expect(stack[0].editorId).toBe(EDITOR_TO_PUSH_INFO.editorId);
    });

    it('GIVEN there are editors in a stack WHEN I push a new editor THEN it is added to the stack', function() {
        // GIVEN 
        genericEditorStackService._editorsStacks[STACK_ID] = ['some other component'];

        var pushFn = systemEventService.registerEventHandler.calls.argsFor(0)[1];
        EDITOR_TO_PUSH_INFO.editorStackId = STACK_ID;

        // WHEN 
        pushFn(EDITOR_POP_FROM_STACK_EVENT, EDITOR_TO_PUSH_INFO);

        // THEN
        var stack = genericEditorStackService.getEditorsStack(STACK_ID);
        expect(stack).not.toBe(null);
        expect(stack.length).toBe(2);
        expect(stack[1].component).toBe(EDITOR_TO_PUSH_INFO.component);
        expect(stack[1].componentType).toBe(EDITOR_TO_PUSH_INFO.componentType);
        expect(stack[1].editorId).toBe(EDITOR_TO_PUSH_INFO.editorId);
    });

    it('GIVEN there are no editors in a stack WHEN I try to pop an editor THEN a warning is displayed', function() {
        // GIVEN 
        var popFn = systemEventService.registerEventHandler.calls.argsFor(1)[1];

        // WHEN 
        popFn(EDITOR_PUSH_TO_STACK_EVENT, {
            editorStackId: STACK_ID
        });

        // THEN
        expect($log.warn).toHaveBeenCalledWith('genericEditorStackService - Stack of editors not found. Cannot pop editor.');
    });

    it('GIVEN there is an editor WHEN the editor is popped THEN it is removed from the stack', function() {
        // GIVEN
        genericEditorStackService._editorsStacks[STACK_ID] = ['some other component', 'comp 2'];
        var popFn = systemEventService.registerEventHandler.calls.argsFor(1)[1];

        // WHEN 
        popFn(EDITOR_PUSH_TO_STACK_EVENT, {
            editorStackId: STACK_ID
        });

        // THEN
        var stack = genericEditorStackService.getEditorsStack(STACK_ID);
        expect(stack.length).toBe(1);
    });

    it('GIVEN there are editors in a stack WHEN getEditorsStack is called THEN they are returned', function() {
        // GIVEN 
        var COMP_1 = 'some other component';
        var COMP_2 = 'comp 2';
        genericEditorStackService._editorsStacks[STACK_ID] = [COMP_1, COMP_2];

        // WHEN 
        var stackReturned = genericEditorStackService.getEditorsStack(STACK_ID);

        // THEN
        expect(stackReturned).not.toBe(null);
        expect(stackReturned.length).toBe(2);
        expect(stackReturned[0]).toBe(COMP_1);
        expect(stackReturned[1]).toBe(COMP_2);
    });

    it('GIVEN there are no editors in a stack WHEN isTopEditorInStack is called THEN it returns false', function() {
        // GIVEN  

        // WHEN
        var result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

        // THEN
        expect(result).toBe(false);
    });

    it('GIVEN there are editors in a stack WHEN isTopEditorInStack is called AND it is the top editor THEN it returns true', function() {
        // GIVEN 
        genericEditorStackService._editorsStacks[STACK_ID] = [{
            editorId: 'some other editor'
        }, {
            editorId: EDITOR_ID
        }];

        // WHEN 
        var result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

        // THEN
        expect(result).toBe(true);
    });

    it('GIVEN there are editors in a stack WHEN isTopEditorInStack is called AND it is not the top editor THEN it returns false', function() {
        // GIVEN 
        genericEditorStackService._editorsStacks[STACK_ID] = [{
            editorId: EDITOR_ID
        }, {
            editorId: 'some other editor'
        }];

        // WHEN 
        var result = genericEditorStackService.isTopEditorInStack(STACK_ID, EDITOR_ID);

        // THEN
        expect(result).toBe(false);
    });
});
