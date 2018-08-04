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
 * @name navigationNodeEditorModule
 * @description
 *
 * The navigation node editor module provides a directive and controller to manage the navigation node selected on the parent's scope.     
 */
angular.module("navigationNodeEditorModule", ['navigationNodeEditorCreateEntryModule', 'navigationNodeEditorEntryListModule', 'navigationNodeEditorAttributesModule', 'seBreadcrumbModule'])

    /**
     * @ngdoc controller
     * @name navigationNodeEditorModule.controller:navigationNodeEditorController
     *
     * @description
     * The navigation node editor controller is responsible for initializing the shared data that will be managed by other 
     * nested directives. 
     */
    .controller("navigationNodeEditorController", function() {

        this.$onInit = function() {
            this.navigationNodeEntryData = {
                navigationNodeEntry: null,
                prepareEntryNodeEditor: function() {}
            };
        };
    })

    /**
     * @ngdoc directive
     * @name navigationNodeEditorModule.directive:navigationNodeEditor
     *
     * @description
     * The navigation node editor directive is used to edit the navigation node, and displays essentially the entry list directive, 
     * the entry creation directive and the attribute node directive.
     *    
     * The directive expects that the parent passes a node object that reflects the information about the navigation node being edited. 
     */
    .directive('navigationNodeEditor', function() {
        return {
            restrict: 'E',
            transclude: false,
            replace: false,
            templateUrl: 'navigationNodeEditorTemplate.html',
            controller: 'navigationNodeEditorController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                nodeUid: '<',
                parentUid: '<?',
                entryIndex: '<',
                uriContext: '<',
                reset: '=',
                submit: '=',
                isDirty: '='
            }
        };
    });
