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
angular.module('cmsItemDropdownModule', ['subTypeSelectorModule', 'nestedComponentModule', 'eventServiceModule', 'nestedComponentManagementServiceModule', 'genericEditorStackServiceModule', 'cmsItemDropdownDropdownPopulatorModule'])
    .controller('cmsItemDropdownController', function(CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, ON_EDIT_NESTED_COMPONENT_EVENT, systemEventService, genericEditorStackService, nestedComponentManagementService) {

        // -------------------------------------------------------------------------------------------------
        // Constants
        // -------------------------------------------------------------------------------------------------
        var CREATE_COMPONENT_BUTTON_PRESSED_EVENT_ID = 'CREATE_NESTED_COMPONENT_BUTTON_PRESSED_EVENT';

        // -------------------------------------------------------------------------------------------------
        // Variables
        // -------------------------------------------------------------------------------------------------

        // -------------------------------------------------------------------------------------------------
        // Lifecyle Methods
        // -------------------------------------------------------------------------------------------------
        this.$onInit = function() {
            this.itemTemplateUrl = 'cmsItemSearchTemplate.html';

            this.editorStackId = this.ge.editor.editorStackId;
            this.field.params = this.field.params || {};
            this.field.editorStackId = this.editorStackId;

            this.field.params.catalogId = CONTEXT_CATALOG;
            this.field.params.catalogVersion = CONTEXT_CATALOG_VERSION;

            this.showSubTypesSelector = false;
            this._recompileDom = function() {};

            // Register event handlers. 
            this.componentButtonPressedEventId = CREATE_COMPONENT_BUTTON_PRESSED_EVENT_ID + "_" + this.qualifier;
            this.createComponentButtonUnRegFn = systemEventService.registerEventHandler(this.componentButtonPressedEventId, this.onCreateComponentButtonPressed.bind(this));
            this.editComponentClickedUnRegFn = systemEventService.registerEventHandler(ON_EDIT_NESTED_COMPONENT_EVENT, this.onEditComponentClicked.bind(this));
        };

        this.$onDestroy = function() {
            this.createComponentButtonUnRegFn();
            this.editComponentClickedUnRegFn();
        };

        // -------------------------------------------------------------------------------------------------
        // Event Handlers
        // -------------------------------------------------------------------------------------------------
        this.onCreateComponentButtonPressed = function(eventId, textTyped) {
            this.textTyped = textTyped;
            if (genericEditorStackService.isTopEditorInStack(this.editorStackId, this.id)) {
                if (this.field.subTypes) {
                    var keys = Object.keys(this.field.subTypes);
                    if (keys.length > 1) {
                        // First select the type to create. 
                        this.showSubTypesSelector = true;
                    } else {
                        this.createNestedComponent(keys[0]);
                    }
                }
            }
        };

        this.onSubTypeSelected = function(subtype) {
            this.createNestedComponent(subtype).then(function() {
                this.showSubTypesSelector = false;
            }.bind(this)).catch(function() {
                this.showSubTypesSelector = false;
            }.bind(this));
        }.bind(this);

        this.onEditComponentClicked = function(eventId, payload) {
            if (genericEditorStackService.isTopEditorInStack(this.editorStackId, this.id)) {
                if (this.qualifier === payload.qualifier) {
                    this.editComponent(payload.item);
                }
            }
        };

        // -------------------------------------------------------------------------------------------------
        // Helper Methods
        // -------------------------------------------------------------------------------------------------
        this.configureSeDropdown = function($api) {
            // Template configuration
            var template = "<y-actionable-search-item data-event-id='" + this.componentButtonPressedEventId + "'></y-actionable-search-item>";
            $api.setResultsHeaderTemplate(template);
        }.bind(this);

        this.createNestedComponent = function(componentType) {
            var componentInfo = {
                componentId: null,
                componentUuid: null,
                componentType: componentType,
                content: {
                    name: this.textTyped,
                    catalogVersion: this.model.catalogVersion
                }
            };

            return nestedComponentManagementService.openNestedComponentEditor(componentInfo, this.editorStackId, null).then(function(item) {
                if (this.field.collection) {
                    if (!this.model[this.qualifier]) {
                        this.model[this.qualifier] = [];
                    }

                    this.model[this.qualifier].push(item.uuid);
                } else {
                    this.model[this.qualifier] = item.uuid;
                }

                this._recompileDom();
                this.textTyped = '';
            }.bind(this));
        };

        this.editComponent = function(itemToEdit) {
            var componentInfo = {
                componentId: itemToEdit.uid,
                componentUuid: itemToEdit.uuid,
                componentType: itemToEdit.typeCode,
                content: itemToEdit
            };

            nestedComponentManagementService.openNestedComponentEditor(componentInfo, this.editorStackId, null).then(function() {
                this._recompileDom();
            }.bind(this));
        };

    })
    /**
     * @name cmsItemDropdownModule.directive:cmsItemDropdown
     * @scope
     * @restrict E
     * @element cms-item-dropdown
     * 
     * @description
     * Component wrapper for CMS Item's on top of seDropdown component that upon selection of an option, will print the CMSItem
     * in the provided format.
     * 
     * @param {=Object} field The component field
     * @param {=String} id The component id
     * @param {=Object} model The component model
     * @param {=String} qualifier The qualifier within the structure attribute
     */
    .component('cmsItemDropdown', {
        templateUrl: 'cmsItemDropdownTemplate.html',
        controller: 'cmsItemDropdownController',
        controllerAs: 'cmsItemDropdownCtrl',
        require: {
            ge: '^^genericEditor'
        },
        bindings: {
            field: '=',
            qualifier: '=',
            model: '=',
            id: '='
        }
    });
