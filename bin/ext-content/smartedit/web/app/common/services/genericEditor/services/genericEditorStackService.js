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
angular.module('genericEditorStackServiceModule', ['eventServiceModule'])
    .constant('EDITOR_PUSH_TO_STACK_EVENT', 'EDITOR_PUSH_TO_STACK_EVENT')
    .constant('EDITOR_POP_FROM_STACK_EVENT', 'EDITOR_POP_FROM_STACK_EVENT')
    .service('genericEditorStackService', function($log, EDITOR_PUSH_TO_STACK_EVENT, EDITOR_POP_FROM_STACK_EVENT, systemEventService) {

        // --------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------
        // Variables
        // --------------------------------------------------------------------------------------
        this._editorsStacks = {};

        // --------------------------------------------------------------------------------------
        // API
        // --------------------------------------------------------------------------------------
        this.getEditorsStack = function(editorStackId) {
            return this._editorsStacks[editorStackId] || null;
        };

        this.isTopEditorInStack = function(editorStackId, editorId) {
            var result = false;
            var stack = this._editorsStacks[editorStackId];
            if (stack) {
                var topEditor = stack[stack.length - 1];
                result = topEditor && (topEditor.editorId === editorId);
            }

            return result;
        };

        // --------------------------------------------------------------------------------------
        // Helper Methods
        // --------------------------------------------------------------------------------------
        this.pushEditorEventHandler = function(eventId, editorToPushInfo) {
            validateId(editorToPushInfo);

            var stackId = editorToPushInfo.editorStackId;
            if (!this._editorsStacks[stackId]) {
                this._editorsStacks[stackId] = [];
            }

            this._editorsStacks[stackId].push({
                component: editorToPushInfo.component,
                componentType: editorToPushInfo.componentType,
                editorId: editorToPushInfo.editorId
            });
        };

        this.popEditorEventHandler = function(eventId, editorToPopInfo) {
            validateId(editorToPopInfo);

            var stackId = editorToPopInfo.editorStackId;
            var stack = this._editorsStacks[stackId];
            if (!stack) {
                $log.warn('genericEditorStackService - Stack of editors not found. Cannot pop editor.');
                return;
            }

            stack.pop();
            if (stack.length === 0) {
                delete this._editorsStacks[stackId];
            }
        };

        var validateId = function(editorInfo) {
            if (!editorInfo.editorStackId) {
                throw new Error('genericEditorStackService - Must provide a stack id.');
            }
        };

        systemEventService.registerEventHandler(EDITOR_PUSH_TO_STACK_EVENT, this.pushEditorEventHandler.bind(this));
        systemEventService.registerEventHandler(EDITOR_POP_FROM_STACK_EVENT, this.popEditorEventHandler.bind(this));
    });
