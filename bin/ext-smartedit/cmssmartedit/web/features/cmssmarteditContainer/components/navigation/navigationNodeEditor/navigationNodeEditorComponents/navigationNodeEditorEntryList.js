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
 * @name navigationNodeEditorEntryListModule
 * @description
 *
 * The navigation node editor entry list module provides a directive and controller to manage the navigation node entry list.     
 */
angular.module("navigationNodeEditorEntryListModule", ['genericEditorModule', 'seValidationErrorParserModule', 'navigationEntryItemServiceModule', 'eventServiceModule', 'resourceModule', 'yLoDashModule'])

    /**
     * @ngdoc controller
     * @name navigationNodeEditorEntryListModule.controller:navigationNodeEditorEntryListController
     *
     * @description
     * The navigation node editor entry list controller is responsible for loading the entry list for a specific node, 
     * updating an entry's position, removing the entry as an entry on the node and enabling the modification of an entry. 
     *
     */
    .controller("navigationNodeEditorEntryListController", function($q, lodash, navigationEntryItemService, systemEventService, seValidationErrorParser, navigationNodeRestService, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, GENERIC_EDITOR_LOADED_EVENT, NAV_ENTRY_EDITOR_ID) {

        this.entries = [];

        this.isInError = function(entry) {
            return this.errorsMap && Object.keys(this.errorsMap).indexOf(entry.id) > -1;
        };

        this.onSelect = function(entry) {
            this.currentEntry = entry;
            this.navigationNodeEntryData.prepareEntryNodeEditor(entry);
        }.bind(this);

        var SAVE_ENTRY_EVENT = 'saveEntry';
        var DELETE_ENTRY_EVENT = 'deleteEntry';
        var MODIFY_ENTRY_SET = 'modifyEntrySet';

        this.dropdownItems = [{
            key: 'se.cms.navigationmanagement.navnode.edit',
            callback: this.onSelect
        }, {
            key: 'se.cms.navigationmanagement.navnode.move.up',
            condition: function(entry) {
                return this.entries.indexOf(entry) > 0;
            }.bind(this),
            callback: function(entry) {
                var pos = this.entries.indexOf(entry);
                var upperEntry = this.entries[pos - 1];
                this.entries.splice(pos - 1, 2, entry, upperEntry);
                systemEventService.sendAsynchEvent(MODIFY_ENTRY_SET, this.entries);
            }.bind(this)
        }, {
            key: 'se.cms.navigationmanagement.navnode.move.down',
            condition: function(entry) {
                return this.entries.indexOf(entry) < this.entries.length - 1;
            }.bind(this),
            callback: function(entry) {
                var pos = this.entries.indexOf(entry);
                var lowerEntry = this.entries[pos + 1];
                this.entries.splice(pos, 2, lowerEntry, entry);
                systemEventService.sendAsynchEvent(MODIFY_ENTRY_SET, this.entries);
            }.bind(this)
        }, {
            key: 'se.cms.navigationmanagement.navnode.removenode',
            callback: function(entry) {

                var updatedEntries = this.entries.filter(function(curEntry) {
                    return curEntry.id !== entry.id;
                });

                this.entries = updatedEntries;
                systemEventService.sendAsynchEvent(MODIFY_ENTRY_SET, this.entries);
                systemEventService.sendAsynchEvent(DELETE_ENTRY_EVENT, entry);
            }.bind(this)
        }];

        var _pushEntryToList = function(key, entry) {
            navigationEntryItemService.finalizeNavigationEntries([entry], this.uriContext).then(function() {
                var clone = lodash.cloneDeep(entry);

                this.entries = this.entries.map(function(item) {
                    if (item.id === clone.id) {
                        item = clone;
                    }
                    return item;
                });
                if (!this.entries.some(function(item) {
                        return item.id === clone.id;
                    })) {
                    this.entries.push(clone);
                }

                systemEventService.sendAsynchEvent(MODIFY_ENTRY_SET, this.entries);
            }.bind(this));

        }.bind(this);

        var saveNodeErrorsEvent = function(key, validationData) {
            var validationErrors = lodash.cloneDeep(validationData);
            if (!validationData.messages.processed) {
                this.errorsMap = validationErrors.messages.filter(function(validationError) {
                    return validationError.subject === "entries";
                }).map(function(validationError) {
                    //validationError.message contains the "position" of the entry pertaining to the faulty subject
                    var error = seValidationErrorParser.parse(validationError.message);
                    //error contains: subject, message field and position of the entry property
                    validationError.position = error.position;
                    validationError.subject = error.field;
                    return validationError;
                }).reduce(function(holder, next) {

                    var entryUid = this.entries[next.position].id;

                    holder[entryUid] = holder[entryUid] || [];
                    holder[entryUid].push(next);
                    delete next.position;
                    return holder;
                }.bind(this), {});

                if (this.errorsMap && this.currentEntry && this.errorsMap[this.currentEntry.id]) {
                    this.errorsMap[this.currentEntry.id].processed = true;
                    systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                        messages: this.errorsMap[this.currentEntry.id]
                    });
                }
            }
            return $q.when();
        }.bind(this);

        var onEditorLoadEvent = function(key, tabId) {
            if (this.currentEntry && this.errorsMap && this.errorsMap[this.currentEntry.id] && tabId === NAV_ENTRY_EDITOR_ID) {
                this.errorsMap[this.currentEntry.id].processed = true;
                systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                    messages: this.errorsMap[this.currentEntry.id]
                });
            }
            return $q.when();
        }.bind(this);

        this.$onInit = function() {

            if (this.nodeUid) {
                var payload = angular.extend({
                    identifier: this.nodeUid
                }, this.uriContext);

                navigationNodeRestService.get(payload).then(function(response) {
                    var entries = response.entries;

                    navigationEntryItemService.finalizeNavigationEntries(entries, this.uriContext).then(function() {

                        this.entries = response.entries;
                        if (this.entryIndex !== undefined) {
                            this.entry = this.entries[this.entryIndex];
                            this.onSelect(this.entry);
                        }
                    }.bind(this));
                }.bind(this));
            }

            // event listener 
            this.unregisterEntryListListener = systemEventService.registerEventHandler(SAVE_ENTRY_EVENT, _pushEntryToList);
            /* this list directive listens for all errors coming from the main node submission : it may contain errors for multiple entries and multiples fields within it
             * it generates a map of entry index / entry errors
             */
            this.unregisterErrorListener = systemEventService.registerEventHandler(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, saveNodeErrorsEvent);
            this.unregisterEditorLoadListener = systemEventService.registerEventHandler(GENERIC_EDITOR_LOADED_EVENT, onEditorLoadEvent);


        };

        // destroy listener on destroy controller scope
        this.$onDestroy = function() {
            this.unregisterEntryListListener();
            this.unregisterErrorListener();
            this.unregisterEditorLoadListener();
        };

    })

    /**
     * @ngdoc directive
     * @name navigationNodeEditorEntryListModule.directive:navigationNodeEditorEntryList
     *
     * @description
     * The navigation node editor entry list directive is used inside the navigation node editor directive, and displays 
     * a list of navigation node entries and actions to edit, move up, move down and delete an entry.
     *
     * The directive expects that the parent, the navigation node editor, passes a navigationNodeEntryData object and a 
     * node object that reflects the information about the navigation node being edited. Both should be on the parent's scope. 
     */
    .component('navigationNodeEditorEntryList', {
        transclude: false,
        templateUrl: 'navigationNodeEditorEntryListTemplate.html',
        controller: 'navigationNodeEditorEntryListController',
        controllerAs: 'ctrl',
        bindings: {
            entryIndex: '<?',
            nodeUid: '<?',
            uriContext: '<',
            navigationNodeEntryData: '<'
        }
    });
