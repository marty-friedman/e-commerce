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
 * @name selectPageTypeModule
 * @description
 * #selectPageTypeModule
 *
 * The selectPageTypeModule module contains the {@link selectPageTypeModule.directive:selectPageType selectPageType} component
 *
 */
angular.module('selectPageTypeModule', ['pageTypeServiceModule', 'l10nModule'])

    /**
     * @ngdoc directive
     * @name selectPageTypeModule.directive:selectPageType
     * @scope
     * @restrict E
     * @element data-select-page-type
     *
     * @description
     * Displays a list of all CMS page types in the system, and allows the user to select one, triggering the on-type-selected callback.
     *
     * @param {Function} onTypeSelected [Required] A callback function that is called when a type is selected from the list.
     * The function is called with a single argument, an object representing the selected page type.
     */
    .component('selectPageType', {
        controller: 'selectPageTypeController',
        templateUrl: 'selectPageTypeTemplate.html',
        bindings: {
            onTypeSelected: '='
        }
    })

    .controller('selectPageTypeController', ['pageTypeService', function(pageTypeService) {

        this.pageTypes = [];
        this.selectedType = null;

        this.selectType = function typeSelected(pageType) {
            this.selectedType = pageType;
            this.onTypeSelected(pageType);
        };

        this.isSelected = function isSelected(pageType) {
            return pageType === this.selectedType;
        };

        this.$onInit = function $onInit() {
            pageTypeService.getPageTypes().then(function(pageTypes) {
                this.pageTypes = pageTypes;
            }.bind(this));

        };

    }]);
