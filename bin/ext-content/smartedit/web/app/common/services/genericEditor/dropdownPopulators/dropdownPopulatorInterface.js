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
angular.module('dropdownPopulatorInterfaceModule', ['yLoDashModule'])
    /**
     * @ngdoc service
     * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
     *
     * @description
     * Interface describing the contract of a DropdownPopulator fetched through dependency injection by the
     * {@link genericEditorModule.service:GenericEditor GenericEditor} to populate the dropdowns of {@link seDropdownModule.directive:seDropdown seDropdown}.
     */
    .factory('DropdownPopulatorInterface', function(lodash) {

        var DropdownPopulatorInterface = function() {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#populate
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         * @description
         * Will returns a promise resolving to a list of items.
         * this method is deprecated, use {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchAll, fetchAll}.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @returns {object} a list of objects.
         */
        DropdownPopulatorInterface.prototype.populate = function(payload) {
            return this.fetchAll(payload);
        };

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchAll
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         * @deprecated
         * @description
         * Will returns a promise resolving to a list of items.
         * The items must all contain a property <b>id</b>.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {String} payload.field.options The original array of options (used by {@link optionsDropdownPopulatorModule.service:optionsDropdownPopulator optionsDropdownPopulator})
         * @param {String} payload.field.uri The uri used to make a rest call to fetch data (used by {@link uriDropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator})
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {String} payload.field.dependsOn A comma separated list of attributes to include from the model when building the request params
         * @param {String} payload.field.idAttribute The name of the attribute to use when setting the id attribute
         * @param {String} payload.field.labelAttributes A list of attributes to use when setting the label attribute
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @returns {object} a list of objects.
         */
        DropdownPopulatorInterface.prototype.fetchAll = function() {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#fetchPage
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Will returns a promise resolving to a {@link Page.object:Page page} of items.
         * The items must all contain a property <b>id</b>.
         * @param {object} payload contains the field, model and additional attributes.
         * @param {object} payload.field The field descriptor from {@link genericEditorModule.service:GenericEditor GenericEditor} containing information about the dropdown.
         * @param {String} payload.field.options The original array of options (used by {@link optionsDropdownPopulatorModule.service:optionsDropdownPopulator optionsDropdownPopulator})
         * @param {String} payload.field.uri The uri used to make a rest call to fetch data (used by {@link uriDropdownPopulatorModule.service:uriDropdownPopulator uriDropdownPopulator})
         * @param {String} payload.field.dependsOn A comma separated list of attributes to include from the model when building the request params
         * @param {String} payload.field.idAttribute The name of the attribute to use when setting the id attribute
         * @param {String} payload.field.labelAttributes A list of attributes to use when setting the label attribute
         * @param {object} payload.field.params An object containing properties to append as query string while making a call.
         * @param {object} payload.model The full model being edited in {@link genericEditorModule.service:GenericEditor GenericEditor}.
         * @param {object} payload.selection The object containing the full option object that is now selected in a dropdown that we depend on (Optional, see dependsOn property in {@link seDropdownModule.directive:seDropdown seDropdown}).
         * @param {String} payload.search The search key when the user types in the dropdown (optional).
         * @param {String} payload.pageSize number of items in the page.
         * @param {String} payload.currentPage current page number.
         * @returns {object} a {@link Page.object:Page page}
         */
        DropdownPopulatorInterface.prototype.fetchPage = function() {};

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#isPaged
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Specifies whether this populator is meant to work in paged mode as opposed to retrieve lists. Optional, default is false
         */
        DropdownPopulatorInterface.prototype.isPaged = function() {
            return false;
        };

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#populateAttributes
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Populates the id and label property for each item in the list. If the label property is not already set,
         * then we use an ordered list of attributes to use when determining the label for each item.
         * @param {Array} items The array of items to set the id and label attributes on
         * @param {String} idAttribute The name of the id attribute
         * @param {Array} orderedLabelAttributes The ordered list of label attributes
         * @returns {Array} the modified list of items
         */
        DropdownPopulatorInterface.prototype.populateAttributes = function(items, idAttribute, orderedLabelAttributes) {
            return lodash.map(items, function(item) {
                if (idAttribute && lodash.isEmpty(item.id)) {
                    item.id = item[idAttribute];
                }

                if (orderedLabelAttributes && lodash.isEmpty(item.label)) {
                    // Find the first attribute that the item object contains
                    var labelAttribute = lodash.find(orderedLabelAttributes, function(attr) {
                        return !lodash.isEmpty(item[attr]);
                    });

                    // If we found an attribute, set the label
                    if (labelAttribute) {
                        item.label = item[labelAttribute];
                    }
                }

                return item;
            });
        };

        /**
         * @ngdoc method
         * @name DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#search
         * @methodOf DropdownPopulatorInterfaceModule.DropdownPopulatorInterface
         *
         * @description
         * Searches a list and returns only items with a label attribute that matches the search term
         * @param {Array} items The list of items to search
         * @param {Array} searchTerm The search term to filter items by
         * @returns {Array} the filtered list of items
         */
        DropdownPopulatorInterface.prototype.search = function(items, searchTerm) {
            return lodash.filter(items, function(item) {
                return item.label && item.label.toUpperCase().indexOf(searchTerm.toUpperCase()) > -1;
            });
        };

        return DropdownPopulatorInterface;
    });
