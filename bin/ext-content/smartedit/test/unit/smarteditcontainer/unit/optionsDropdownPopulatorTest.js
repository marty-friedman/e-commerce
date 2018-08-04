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
describe('optionsDropdownPopulator', function() {

    var optionsDropdownPopulator, $rootScope;

    var payload = {
        field: {
            cmsStructureType: "EditableDropdown",
            qualifier: "dropdownA",
            i18nKey: 'theKey',
            idAttribute: "uid",
            labelAttributes: ["label1", "label2"],
            options: [{
                id: '1',
                label: 'OptionA1',
            }, {
                id: '2',
                label: 'OptionA2',
            }, {
                id: '3',
                label: 'OptionA3',
            }, {
                id: '4',
                label1: 'OptionA4',
            }, {
                id: '5',
                label2: 'OptionA5',
            }, {
                id: '6',
                label: 'OptionA6 - the label',
                label2: 'OptionA6 - not the label',
            }, {
                uid: '7',
                label: 'OptionA7',
            }],
        },
        model: {
            dropdownA: "1"
        }
    };

    beforeEach(module('functionsModule'));
    beforeEach(module('optionsDropdownPopulatorModule'));

    beforeEach(inject(function(_optionsDropdownPopulator_, _$rootScope_) {
        optionsDropdownPopulator = _optionsDropdownPopulator_;
        $rootScope = _$rootScope_;
    }));

    it('GIVEN options populator is called WHEN I call populate method THEN should return a promise containing list of options in the field object of the payload', function() {

        var promise = optionsDropdownPopulator.populate(payload);

        expect(promise).toBeResolvedWithData([{
            id: '1',
            label: 'OptionA1',
        }, {
            id: '2',
            label: 'OptionA2',
        }, {
            id: '3',
            label: 'OptionA3',
        }, {
            id: '4',
            label: 'OptionA4',
            label1: 'OptionA4'
        }, {
            id: '5',
            label: 'OptionA5',
            label2: 'OptionA5'
        }, {
            id: '6',
            label: 'OptionA6 - the label',
            label2: 'OptionA6 - not the label',
        }, {
            id: '7',
            uid: '7',
            label: 'OptionA7',
        }]);

    });

    it('GIVEN options populator is called WHEN I call populate method with a search attribute THEN should return a promise containing list of filtered options based on the search string', function() {

        payload.search = "A2";
        var promise = optionsDropdownPopulator.populate(payload);

        expect(promise).toBeResolvedWithData([{
            id: '2',
            label: 'OptionA2',
        }]);

    });

});
