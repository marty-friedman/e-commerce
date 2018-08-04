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
angular.module('restrictionsPageListIconModule', ['cmssmarteditContainerTemplates', 'translationServiceModule', 'cmsSmarteditServicesModule'])
    .controller('restrictionsPageListIconController', function(assetsService) {
        this.tooltipI18nKey = 'se.icon.tooltip.visibility';

        this.getIconUrl = function() {
            return assetsService.getAssetsRoot() + (this.numberOfRestrictions > 0 ? '/images/icon_restriction_small_blue.png' :
                '/images/icon_restriction_small_grey.png');
        };

        this.getInterpolationParameters = function() {
            return {
                numberOfRestrictions: this.numberOfRestrictions
            };
        };
    })
    .directive('restrictionsPageListIcon', function() {
        return {
            templateUrl: 'restrictionsPageListIconTemplate.html',
            restrict: 'E',
            controller: 'restrictionsPageListIconController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                numberOfRestrictions: '='
            }
        };
    });
