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
angular.module('genericEditorTabModule', ['translationServiceModule', 'ui.bootstrap', 'genericEditorFieldModule', 'localizedElementModule'])
    .controller('genericEditorTabController', function() {
        this.$onInit = function() {
            this.id = this.ge.editor.id;
            this.fields = this.ge.editor.fieldsMap[this.tabId];
            this.component = this.ge.editor.component;
        };
    })
    .component('genericEditorTab', {
        templateUrl: 'genericEditorTabTemplate.html',
        transclude: false,
        require: {
            ge: '^^genericEditor'
        },
        controller: 'genericEditorTabController',
        controllerAs: 'geTab',
        bindings: {
            tabId: '<'
        }
    });
