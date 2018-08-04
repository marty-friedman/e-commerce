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
/**
 * @ngdoc overview
 * @name genericEditorBreadcrumbModule
 *
 * @description
 * This module provides the genericEditorBreadcrumbModule component, which is used to show a breadcrumb on top of the generic editor
 * when there is more than one editor opened on top of each other. This will happen when editing nested components. 
 */
angular.module('genericEditorBreadcrumbModule', ['genericEditorStackServiceModule'])
    .controller('genericEditorBreadcrumbController', function($translate, genericEditorStackService) {

        this.getEditorsStack = function() {
            if (!this.editorsStack) {
                this.editorsStack = genericEditorStackService.getEditorsStack(this.ge.editorStackId);
            }

            return this.editorsStack;
        };

        this.showBreadcrumb = function() {
            return this.getEditorsStack().length > 1;
        };

        this.getComponentName = function(breadcrumbItem) {
            if (!breadcrumbItem.component.name) {
                return $translate.instant('se.breadcrumb.name.empty');
            }

            return breadcrumbItem.component.name;
        };
    })
    /**
     * @ngdoc directive
     * @name genericEditorBreadcrumbModule.component:genericEditorBreadcrumb
     * @element generic-editor-breadcrumb
     *
     * @description
     * Component responsible for rendering a breadcrumb on top of the generic editor. 
     * @param {< String} editorStackId The string that identifies the stack of editors being edited together. 
     */
    .component('genericEditorBreadcrumb', {
        controller: 'genericEditorBreadcrumbController',
        templateUrl: 'genericEditorBreadcrumbTemplate.html',
        require: {
            ge: '^^genericEditor'
        },
        bindings: {}
    });
