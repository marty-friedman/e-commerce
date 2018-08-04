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
angular.module('optionsDropdownPopulatorModule', ['dropdownPopulatorInterfaceModule'])
    /**
     * @ngdoc service
     * @name optionsDropdownPopulatorModule.service:optionsDropdownPopulator
     * @description
     * implementation of {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface DropdownPopulatorInterface} for "EditableDropdown" cmsStructureType
     * containing options attribute.
     */
    .factory('optionsDropdownPopulator', function(DropdownPopulatorInterface, $q, extend) {

        var optionsDropdownPopulator = function() {};

        optionsDropdownPopulator = extend(DropdownPopulatorInterface, optionsDropdownPopulator);

        /**
         * @ngdoc method
         * @name optionsDropdownPopulatorModule.service:optionsDropdownPopulator#populate
         * @methodOf optionsDropdownPopulatorModule.service:optionsDropdownPopulator
         *
         * @description
         * Implementation of the {@link DropdownPopulatorInterfaceModule.DropdownPopulatorInterface#populate DropdownPopulatorInterface.populate} method
         */
        optionsDropdownPopulator.prototype.populate = function(payload) {
            var options = this.populateAttributes(payload.field.options, payload.field.idAttribute, payload.field.labelAttributes);

            if (payload.search) {
                options = this.search(options, payload.search);
            }

            return $q.when(options);
        };

        return new optionsDropdownPopulator();
    });
