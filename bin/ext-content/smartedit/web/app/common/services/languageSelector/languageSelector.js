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
 * @name languageSelectorModule
 * @description
 *
 * The language selector module contains a directive which allow the user to select a language.
 *
 * Use the {@link languageServiceModule.service:languageService languageService}
 * to call backend API in order to get the list of supported languages
 */
angular
    .module('languageSelectorModule', ['languageServiceModule', 'ui.select', 'ngSanitize', 'crossFrameEventServiceModule'])
    .controller('languageSelectorController', function(crossFrameEventService, $scope, languageService, SWITCH_LANGUAGE_EVENT) {

        this.setSelectedLanguage = function(item) {
            languageService.setSelectedToolingLanguage(item);
        };

        this.eventHandler = function() {
            return languageService.getResolveLocale().then(function(isoCode) {
                this.selectedLanguage = this.languages.find(function(obj) {
                    return obj.isoCode === isoCode;
                });
            }.bind(this));
        };

        this.$onInit = function() {

            languageService.getToolingLanguages().then(function(data) {
                this.languages = data;
                languageService.getResolveLocale().then(function(isoCode) {
                    this.selectedLanguage = this.languages.find(function(obj) {
                        return obj.isoCode === isoCode;
                    });
                }.bind(this));
            }.bind(this));

            this.unregFn = crossFrameEventService.subscribe(SWITCH_LANGUAGE_EVENT, this.eventHandler.bind(this));
        };

        this.$onDestroy = function() {
            this.unregFn();
        };
    })
    /**
     * @ngdoc directive
     * @name languageSelectorModule.directive:languageSelector
     * @scope
     * @restrict E
     * @element ANY
     * @description
     * Language selector provides a drop-down list which contains a list of supported languages.
     * It is used to select a language and translate the system accordingly.
     */
    .directive(
        'languageSelector',
        function() {
            return {
                templateUrl: 'languageSelectorTemplate.html',
                restrict: 'E',
                transclude: true,
                scope: {},
                bindToController: {},
                controller: 'languageSelectorController',
                controllerAs: 'ctrl'
            };
        });
