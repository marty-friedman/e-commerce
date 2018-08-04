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
angular.module('externalComponentDecoratorModule', ['componentHandlerServiceModule'])
    .controller('externalComponentDecoratorController', function($element, CONTENT_SLOT_TYPE, componentHandlerService) {

        this.$onInit = function() {

            var parentSlotIdForComponent = componentHandlerService.getParentSlotForComponent($element);
            this.isExtenalSlot = componentHandlerService.isExternalComponent(parentSlotIdForComponent, CONTENT_SLOT_TYPE);

        };

    })
    .directive('externalComponentDecorator', function() {
        return {
            templateUrl: 'externalComponentDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'externalComponentDecoratorController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                active: '=',
                componentAttributes: '<'
            }
        };
    });
