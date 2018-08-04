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
 * @deprecated since 6.5
 * @ngdoc overview
 * @name seGenericEditorFieldErrorsModule
 *
 * @description
 * This module provides the seGenericEditorFieldErrors component, which is used to show validation errors.
 */
angular.module('seGenericEditorFieldErrorsModule', [])
    .controller('seGenericEditorFieldErrorsController', function() {
        this.getFilteredErrors = function() {
            return (this.field.errors || []).filter(function(error) {
                return error.language === this.qualifier && !error.format;
            }.bind(this)).map(function(error) {
                return error.message;
            });
        };
    })
    .directive('seGenericEditorFieldErrors', function() {
        return {
            templateUrl: 'seGenericEditorFieldErrorsTemplate.html',
            restrict: 'E',
            scope: {},
            controller: 'seGenericEditorFieldErrorsController',
            controllerAs: 'ctrl',
            bindToController: {
                field: '=',
                qualifier: '='
            }
        };
    });
