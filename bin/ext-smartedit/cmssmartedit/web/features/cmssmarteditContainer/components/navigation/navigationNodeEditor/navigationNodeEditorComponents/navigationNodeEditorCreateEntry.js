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
angular.module("navigationNodeEditorCreateEntryModule", [
        'resourceLocationsModule',
        'eventServiceModule',
        'entryDropdownMatcherModule',
        'yLoDashModule',
        'resourceModule',
        'genericEditorModule',
        'dropdownPopulatorInterfaceModule',
        'navigationNodeEntryTypesServiceModule'
    ])
    .constant('NAV_ENTRY_EDITOR_ID', 'se-nav-entry-editor')
    .service("navigationEntryStructureService", function($q, $translate) {

        this.getGenericEditorTabStructure = function() {
            var tabStructure = {
                attributes: [{
                    i18nKey: 'se.cms.navigationmanagement.navnode.node.create.entry',
                    cmsStructureType: "EditableDropdown",
                    qualifier: "itemSuperType",
                    placeholder: $translate.instant('se.cms.navigationmanagement.navnode.node.entry.dropdown.title'),
                    required: true
                }, {
                    cmsStructureType: "EntrySearchSelector",
                    qualifier: "itemId",
                    editable: false,
                    required: true
                }],
                category: 'NAVIGATION' // TODO: CHECK IF THIS IS THE RIGHT CATEGORY
            };
            return $q.when(tabStructure);
        };
    })
    /**
     * @ngdoc service
     * @name navigationNodeEditorCreateEntryModule.service:cmsNavigationEntryDropdownPopulator
     * @description implementation of DropdownPopulatorInterface defined in smartedit
     * for "EditableDropdown" cmsStructureType containing options attribute.
     */
    .factory('cmsNavigationEntryDropdownPopulator', function(DropdownPopulatorInterface, $q, extend, navigationNodeEntryTypesService) {

        var cmsNavigationEntryDropdownPopulator = function() {};

        cmsNavigationEntryDropdownPopulator = extend(DropdownPopulatorInterface, cmsNavigationEntryDropdownPopulator);

        var _getDropdownOptions = function() {
            return navigationNodeEntryTypesService
                .getNavigationNodeEntryTypes()
                .then(function(supportedEntryTypes) {
                    var options = [];
                    if (supportedEntryTypes) {
                        supportedEntryTypes.forEach(function(entryType) {
                            options.push({
                                id: entryType.itemType,
                                label: 'se.cms.' + entryType.itemType.toLowerCase()
                            });
                        });
                    }
                    return options;
                });
        };


        cmsNavigationEntryDropdownPopulator.prototype.populate = function() {
            _getDropdownOptions();
            return $q.when(_getDropdownOptions());
        };

        return new cmsNavigationEntryDropdownPopulator();
    })
    .controller("navigationNodeEditorCreateEntryController", function($timeout, NAVIGATION_MANAGEMENT_ENTRIES_RESOURCE_URI, GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, GENERIC_EDITOR_LOADED_EVENT, NAV_ENTRY_EDITOR_ID, systemEventService, LINKED_DROPDOWN, lodash, navigationEntryStructureService) {

        var SAVE_ENTRY_EVENT = 'saveEntry';
        var DELETE_ENTRY_EVENT = 'deleteEntry';

        this.entryTypeCode = "cmsNavigationEntry";

        var editEntry = function(entry) {
            this.entry = entry;
            this.displayEditor = true;
        }.bind(this);

        /*
         * function to make fields editable in structure
         */
        var _enableTabStructureField = function(status) {
            var attributes = this.tabStructure.attributes;
            attributes.forEach(function(structure, index) {
                if (lodash.isMatch(structure, {
                        cmsStructureType: 'EntrySearchSelector'
                    })) {
                    attributes[index].editable = status;
                }
            }.bind(this));
        }.bind(this);

        /*
         * Hide itemId and search selector widget
         */
        var selectedItemTypeDropdownEvent = function(eventId, data) {
            var entity = {};
            entity.id = this.entry ? this.entry.id : undefined;
            entity[data.qualifier] = data.optionObject ? data.optionObject.id : undefined;

            this.navigationNodeEntryData.prepareEntryNodeEditor(entity);
        }.bind(this);

        this.addNewEntry = function() {
            this.entry = undefined;
            this.displayEditor = true;
        };

        this.saveEntry = function() {
            return this.submit().then(
                function(data) {
                    if (data.itemId) {
                        systemEventService.sendAsynchEvent(SAVE_ENTRY_EVENT, data);
                        this.cancelEntry();
                    } else {
                        systemEventService.sendAsynchEvent(GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT, {
                            messages: [{
                                "message": "se.cms.navigationmanagement.node.addentry.no.itemid.message",
                                "subject": "itemId",
                                "type": "ValidationError"
                            }]
                        });
                    }
                }.bind(this));
        };

        this.isNewEntry = function() {
            return !this.entry || (this.entry && !this.entry.id);
        };

        this.isDisabled = function() {
            return !this.isDirty() || !this.isValid();
        };

        this.cancelEntry = function() {
            _enableTabStructureField(false);
            this.entry = undefined;
            this.displayEditor = false;
        };

        var _deleteEntryEvent = function(eventId, handler) {
            if (this.entry && handler.id === this.entry.id) {
                this.cancelEntry();
            } else {
                return;
            }
        }.bind(this);



        this.$onInit = function() {

            this.displayEditor = false;
            this.tabId = NAV_ENTRY_EDITOR_ID;

            // event listener to hide itemId and search selector widget
            this.unregisterSelectHandler = systemEventService.registerEventHandler(this.tabId + LINKED_DROPDOWN, selectedItemTypeDropdownEvent);
            this.unregisterDeleteHandler = systemEventService.registerEventHandler(DELETE_ENTRY_EVENT, _deleteEntryEvent);

            /*
             * Reset generic editor before editing a node entry.
             */
            this.navigationNodeEntryData.prepareEntryNodeEditor = function(entry) {
                if (entry && (!this.entry || entry.id !== this.entry.id)) {
                    var clone = lodash.cloneDeep(entry);
                    delete clone.parent;

                    // If reset is available, reset the editor and start editing until
                    // it's done. Else, start editing right away.
                    if (this.entry && this.reset) {
                        this.reset().then(function() {
                            this.cancelEntry();
                            $timeout(function() {
                                editEntry(clone);
                            });
                        }.bind(this));
                    } else {
                        editEntry(clone);
                        _enableTabStructureField(true);
                    }
                }
            }.bind(this);


            // get the generic tab structure
            navigationEntryStructureService.getGenericEditorTabStructure()
                .then(function(genericEditorTabStructure) {
                    this.tabStructure = genericEditorTabStructure;
                    if (this.entry) {
                        this.navigationNodeEntryData.prepareEntryNodeEditor(this.entry);
                    }
                }.bind(this));


        };
        // destroy listener on destroy controller scope
        this.$onDestroy = function() {
            this.unregisterSelectHandler();
            this.unregisterDeleteHandler();
        };


    })
    .directive('navigationNodeEditorCreateEntry', function() {

        return {
            restrict: 'E',
            transclude: false,
            replace: false,
            templateUrl: 'navigationNodeEditorCreateEntryTemplate.html',
            controller: 'navigationNodeEditorCreateEntryController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                entry: '<?',
                uriContext: '<',
                navigationNodeEntryData: '<',
                isDirty: '&'
            }
        };
    });
