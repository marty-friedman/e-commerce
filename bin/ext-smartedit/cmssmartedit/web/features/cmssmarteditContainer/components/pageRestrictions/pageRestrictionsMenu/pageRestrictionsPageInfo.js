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
angular.module('restrictionsPageInfoModule', ['pageListServiceModule'])

    .controller('restrictionsPageInfoController', function() {

        this.associatedPrimaryPageLabelI18nKey = "se.cms.label.page.primary";
        this.displayConditionsI18nKey = {
            primary: {
                description: 'page.displaycondition.primary.description',
                value: 'page.displaycondition.primary'
            },
            variation: {
                description: 'page.displaycondition.variation.description',
                value: 'page.displaycondition.variation'
            }
        };
        this.pageDisplayConditionsLabelI18nKey = "se.cms.label.page.display.conditions";
        this.pageNameLabelI18nKey = "se.cms.label.page.name";


        this.getDisplayConditionsValue = function() {
            return this.pageIsPrimary ? "primary" : "variation";
        };

    })

    .directive('restrictionsPageInfo', function() {
        return {
            templateUrl: 'pageRestrictionsPageInfoTemplate.html',
            restrict: 'E',
            controller: 'restrictionsPageInfoController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                pageName: '=',
                pageType: '=',
                pageIsPrimary: '=',
                associatedPrimaryPageName: '='
            }
        };
    });
