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
describe('uriDropdownPopulator', function() {

    var uriDropdownPopulator, restServiceFactory, restServiceForOptions, $rootScope, $q;

    var payload = {
        field: {
            cmsStructureType: "EditableDropdown",
            qualifier: "dropdownA",
            i18nKey: 'theKey',
            idAttribute: "uid",
            labelAttributes: ["label1", "label2"],
            uri: '/someuri',
        },
        model: {
            dropdown1: "1",
            dropdown2: "2"
        }
    };

    var options = [{
        id: '1',
        label: 'opt1-yes',
        dropdown1: '1',
        dropdown2: '1',
    }, {
        id: '2',
        label: 'opt2-no',
        dropdown1: '1',
        dropdown2: '1',
    }, {
        id: '3',
        label: 'opt3-yes-no',
        dropdown1: '1',
        dropdown2: '2',

    }, {
        id: '4',
        label1: 'opt4-yes-no',
        dropdown1: '1',
        dropdown2: '2',

    }, {
        id: '5',
        label2: 'opt5-yes',
        dropdown1: '1',
        dropdown2: '1',

    }, {
        uid: '6',
        label: 'opt6-yes-no',
        dropdown1: '1',
        dropdown2: '2',
    }];

    beforeEach(module('functionsModule'));
    beforeEach(module('uriDropdownPopulatorModule'));

    beforeEach(module('smarteditServicesModule', function($provide) {

        restServiceForOptions = jasmine.createSpyObj('restServiceForOptions', ['get']);
        restServiceForOptions.get.and.callFake(function(params) {

            var filteredOptions = params ? options.filter(function(option) {
                return option.dropdown1 === params.dropdown1 && option.dropdown2 === params.dropdown2;
            }) : options;

            return $q.when({
                options: filteredOptions
            });
        });

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restServiceForOptions);

        $provide.value('restServiceFactory', restServiceFactory);
    }));

    beforeEach(inject(function(_uriDropdownPopulator_, _$rootScope_, _$q_) {
        uriDropdownPopulator = _uriDropdownPopulator_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));

    it('GIVEN uri populator is called WHEN I call populate method without a dependsOn attribute THEN should return a promise by making a REST call to the uri in the fields attribute and return a list of options', function() {

        var promise = uriDropdownPopulator.populate(payload);

        expect(restServiceFactory.get).toHaveBeenCalledWith('/someuri');
        expect(restServiceForOptions.get).toHaveBeenCalled();

        expect(promise).toBeResolvedWithData(options);

    });

    it('GIVEN uri populator is called WHEN I call populate method with a dependsOn attribute THEN should return a promise by making a REST call to the uri in the fields attribute with the right params and return a list of options', function() {

        payload.field.dependsOn = "dropdown1,dropdown2";
        var promise = uriDropdownPopulator.populate(payload);
        $rootScope.$digest();

        expect(restServiceFactory.get).toHaveBeenCalledWith('/someuri');
        expect(restServiceForOptions.get).toHaveBeenCalledWith({
            dropdown1: "1",
            dropdown2: "2"
        });

        expect(promise).toBeResolvedWithData([{
            id: '3',
            label: 'opt3-yes-no',
            dropdown1: '1',
            dropdown2: '2'
        }, {
            id: '4',
            label: 'opt4-yes-no',
            label1: 'opt4-yes-no',
            dropdown1: '1',
            dropdown2: '2'
        }, {
            id: '6',
            uid: '6',
            label: 'opt6-yes-no',
            dropdown1: '1',
            dropdown2: '2',

        }]);

    });

    it('GIVEN uri populator is called WHEN I call populate method with a search attribute THEN should return a promise by making a REST call to the uri in the fields attribute and return a list of options filtered based on the search string', function() {

        delete payload.field.dependsOn;
        payload.search = "yes";
        var promise = uriDropdownPopulator.populate(payload);
        $rootScope.$digest();

        expect(restServiceFactory.get).toHaveBeenCalledWith('/someuri');
        expect(restServiceForOptions.get).toHaveBeenCalled();

        expect(promise).toBeResolvedWithData([{
            id: '1',
            label: 'opt1-yes',
            dropdown1: '1',
            dropdown2: '1',
        }, {
            id: '3',
            label: 'opt3-yes-no',
            dropdown1: '1',
            dropdown2: '2',

        }, {
            id: '4',
            label: 'opt4-yes-no',
            label1: 'opt4-yes-no',
            dropdown1: '1',
            dropdown2: '2',

        }, {
            id: '5',
            label: 'opt5-yes',
            label2: 'opt5-yes',
            dropdown1: '1',
            dropdown2: '1',

        }, {
            id: '6',
            uid: '6',
            label: 'opt6-yes-no',
            dropdown1: '1',
            dropdown2: '2',

        }]);

    });


});
