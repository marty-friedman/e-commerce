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
angular.module("entrySearchSelectorModule", ['ui.bootstrap', 'ui.select', 'smarteditServicesModule', 'entryDropdownMatcherModule', 'eventServiceModule', 'searchAbstractCMSComponentHandlerServiceModule', 'searchAbstractPageHandlerServiceModule', 'searchMediaHandlerServiceModule'])
    .controller("entrySearchSelectorController", function($injector, $q, systemEventService, LINKED_DROPDOWN, urlService, isBlank) {

        var HANDLER_PREFIX = 'search';
        var HANDLER_SUFFIX = 'HandlerService';

        /*
         * Event that will be triggered on item type drop down selection change.   
         */
        var selectedItemTypeDropdownEvent = function(key, event) {
            //reset selected values on change

            if (this.reset) {
                var newItemType = this._getItemType(event);
                var forceReset = this.itemType && newItemType !== this.itemType;
                this.reset(forceReset);
            }

            //return if it does not have itemType, otherwise handler will fail
            if (!this.setup(event)) {
                return;
            }

        }.bind(this);

        this._fetchEntity = function(id) {
            return this.handlerService.getItem(id, this.uriContext);
        }.bind(this);

        this._fetchOptions = function(mask) {
            if (this.handlerService) {
                return this.handlerService.getSearchResults(mask, this.uriContext);
            } else {
                return $q.when([]);
            }
        }.bind(this);

        this._fetchPage = function(mask, pageSize, currentPage) {
            if (this.handlerService) {
                return this.handlerService.getPage(mask, pageSize, currentPage, this.uriContext);
            } else {
                return $q.when();
            }
        }.bind(this);

        this.fetchStrategy = {
            fetchEntity: this._fetchEntity
        };

        this.setup = function(event) {
            this.uriContext = urlService.buildUriContext(this.editor.parameters.siteId, this.editor.parameters.catalogId, this.editor.parameters.catalogVersion);
            this.itemType = this._getItemType(event);
            if (this.itemType) {
                // retrieve the search handler for this item type
                var searchHandlerServiceStrategy = HANDLER_PREFIX + this.itemType + HANDLER_SUFFIX;
                if ($injector.has(searchHandlerServiceStrategy)) {
                    this.handlerService = $injector.get(searchHandlerServiceStrategy);
                } else {
                    var errorMessage = "handler not found for " + searchHandlerServiceStrategy;
                    throw errorMessage;
                }

                this.dropdownProperties = this.handlerService.getSearchDropdownProperties();
                this.fetchStrategy = {
                    fetchEntity: this._fetchEntity
                };

                if (this.dropdownProperties.isPaged) {
                    this.fetchStrategy.fetchPage = this._fetchPage;
                    delete this.fetchStrategy.fetchAll;
                } else {
                    delete this.fetchStrategy.fetchPage;
                    this.fetchStrategy.fetchAll = this._fetchOptions;
                }

                this.initialized = true;
            }
            return !isBlank(this.itemType);
        }.bind(this);

        this._getItemType = function(event) {
            return event && this.model ? this.model[event.qualifier] : this.model.itemSuperType;
        };

        this.$onInit = function() {
            this.unRegisterer = systemEventService.registerEventHandler(this.id + LINKED_DROPDOWN, selectedItemTypeDropdownEvent);
        };

        this.$onChanges = function() {

            this.setup();
        };

        this.$onDestroy = function() {
            this.unRegisterer();
        };

        this.dropdownProperties = {};

    })
    .component('entrySearchSelector', {
        transclude: false,
        templateUrl: 'entrySearchSelectorDropdownTemplate.html',
        controller: 'entrySearchSelectorController',
        controllerAs: 'ctrl',
        bindings: {
            model: "<",
            qualifier: "<",
            field: "<",
            id: '<',
            editor: '<'
        }
    });
