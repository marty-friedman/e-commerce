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
 * @name displayConditionsEditorModule
 * @description
 * #displayConditionsEditorModule
 *
 * The displayConditionsEditorModule module contains the
 * {@link displayConditionsEditorModule.directive:displayConditionsEditor displayConditionsEditor} component
 *
 */
angular.module('displayConditionsEditorModule', ['displayConditionsEditorModelModule', 'displayConditionsPageInfoModule', 'displayConditionsPageVariationsModule', 'displayConditionsPrimaryPageModule'])
    .controller('displayConditionsEditorController', function(displayConditionsEditorModel) {
        this.$onInit = function() {
            displayConditionsEditorModel.initModel(this.page.uid);
        };

        this.getPageName = function() {
            return displayConditionsEditorModel.pageName;
        };

        this.getPageType = function() {
            return displayConditionsEditorModel.pageType;
        };

        this.isPagePrimary = function() {
            return displayConditionsEditorModel.isPrimary;
        };

        this.getVariations = function() {
            return displayConditionsEditorModel.variations;
        };

        this.getAssociatedPrimaryPage = function() {
            return displayConditionsEditorModel.associatedPrimaryPage;
        };

        this.getIsAssociatedPrimaryReadOnly = function() {
            return displayConditionsEditorModel.isAssociatedPrimaryReadOnly;
        };

        this.getPrimaryPages = function() {
            return displayConditionsEditorModel.primaryPages;
        };

        this.onPrimaryPageSelect = function(primaryPage) {
            this.page.label = primaryPage.label;
        };
    })
    /**
     * @ngdoc directive
     * @name displayConditionsEditorModule.directive:displayConditionsEditor
     * @scope
     * @restrict E
     * @element display-conditions-editor
     * 
     * @description
     * This component displays information about a page. For instance, it displays the 
     * page type, template, whether it's a primary or variation page, among others. 
     * 
     * @param {<Object} page The page for which to display its information
     */
    .component('displayConditionsEditor', {
        controller: 'displayConditionsEditorController',
        templateUrl: 'displayConditionsEditorTemplate.html',
        bindings: {
            page: '<'
        }
    });
