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
angular.module('genericEditorFieldModule', ['translationServiceModule', 'ui.bootstrap', 'ui.select', 'ngSanitize', 'seGenericEditorFieldMessagesModule'])
    .controller('genericEditorFieldController', function($scope) {
        this.$onInit = function() {
            //for backward compatibility
            $scope.editor = this.ge.editor;
            $scope.model = this.model;
            $scope.field = this.field;
            $scope.qualifier = this.qualifier;
            $scope.id = this.id;
            $scope.editorStackId = this.ge.editorStackId;
        };
    })
    .component('genericEditorField', {
        templateUrl: 'genericEditorFieldTemplate.html',
        transclude: false,
        require: {
            ge: '^^genericEditor'
        },
        controller: 'genericEditorFieldController',
        controllerAs: 'geField',
        bindings: {
            field: '<',
            model: '=',
            qualifier: '<',
            id: '='
        }
    });
