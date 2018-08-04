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
angular.module('genericEditorTabServiceModule', [])
    /**
     * @ngdoc service
     * @name genericEditorTabServiceModule.service:genericEditorTabService
     * @description
     * The genericEditorTabService is used to configure the way in which the tabs in the 
     * {@link genericEditorModule.directive:genericEditor genericEditor} directive are rendered. 
     * 
     */
    .service('genericEditorTabService', function() {

        // --------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------
        var MIN_PRIORITY = 0;
        var DEFAULT_TAB_ID = 'default';

        // --------------------------------------------------------------------------------------
        // Variables
        // --------------------------------------------------------------------------------------
        this._tabsConfiguration = {};
        this._defaultTabPredicates = [];

        // --------------------------------------------------------------------------------------
        // Public Methods
        // --------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name genericEditorTabServiceModule.service:genericEditorTabService#configureTab
         * @methodOf genericEditorTabServiceModule.service:genericEditorTabService
         * @description
         * This method stores the configuration of the tab identified by the provided ID. 
         * 
         * @param {String} tabId The ID of the tab to configure. 
         * @param {Object} tabConfiguration The object containing the configuration of the tab. 
         * @param {Number=} tabConfiguration.priority The priority of the tab. Higher numbers represent higher priority. This property is used to 
         * sort tabs. 
         * 
         */
        this.configureTab = function(tabId, tabConfiguration) {
            this._tabsConfiguration[tabId] = tabConfiguration;
        };

        /**
         * @ngdoc method
         * @name genericEditorTabServiceModule.service:genericEditorTabService#getTabConfiguration
         * @methodOf genericEditorTabServiceModule.service:genericEditorTabService
         * @description
         * This method retrieves the configuration of a tab. 
         * 
         * @param {String} tabId The ID of the tab for which to retrieve its configuration. 
         * @return {Object} The configuration of the tab. Can be null if no tab with the provided ID has been configured. 
         * 
         */
        this.getTabConfiguration = function(tabId) {
            var result = this._tabsConfiguration[tabId];
            return (result) ? result : null;
        };

        /**
         * @ngdoc method
         * @name genericEditorTabServiceModule.service:genericEditorTabService#sortTabs
         * @methodOf genericEditorTabServiceModule.service:genericEditorTabService
         * @description
         * This method sorts in place the list of tabs provided. Sorting starts with tab priority. If two or more tabs have the same priority they 
         * will be sorted alphabetically by ID. 
         * 
         * @param {Object[]} tabsToSort The list of tabs to sort. 
         * 
         */
        this.sortTabs = function(tabsToSort) {
            return tabsToSort.sort(function(tab1, tab2) {
                var tab1Priority = getTabPriority(tab1);
                var tab2Priority = getTabPriority(tab2);

                if (tab2Priority - tab1Priority !== 0) {
                    return tab2Priority - tab1Priority; // Sort descending priority
                } else {
                    // Sort alphabetically
                    if (tab1.id < tab2.id) {
                        return -1;
                    } else if (tab1.id > tab2.id) {
                        return 1;
                    }
                    return 0;
                }

            }.bind(this));
        };

        // Meant to be used internally. No ng-doc. 
        this.getComponentTypeDefaultTab = function(componentTypeStructure) {
            var result = null;
            this._defaultTabPredicates.some(function(predicate) {
                result = predicate(componentTypeStructure);
                return (result !== null);
            });

            return (result !== null) ? result : DEFAULT_TAB_ID;
        };

        // Meant to be used internally. No ng-doc. 
        this.addComponentTypeDefaultTabPredicate = function(predicate) {
            if (!predicate || typeof predicate !== 'function') {
                throw new Error('genericEditorTabService - provided predicate must be a function.');
            }

            this._defaultTabPredicates.push(predicate);
        };

        // --------------------------------------------------------------------------------------
        // Helper Methods
        // --------------------------------------------------------------------------------------
        var getTabPriority = function(tab) {
            var tabId = tab.id;
            if (!tabId) {
                throw new Error('genericEditorTabService - Every tab must have an id.');
            }

            var tabPriority = MIN_PRIORITY;
            if (this._tabsConfiguration[tabId] && this._tabsConfiguration[tabId].priority) {
                tabPriority = this._tabsConfiguration[tabId].priority;
            }

            return tabPriority;
        }.bind(this);
    });
