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
describe('seDropdownService', function() {

    var optionsDropdownPopulator, uriDropdownPopulator, SEDropdownService, componentXDropdownPopulator, componentYdropdownADropdownPopulator, systemEventServ;
    var getKeyHoldingDataFromResponse;
    var $q, $rootScope, $injector;

    var options = [{
        id: 'id1',
        label: 'label1 - sample'
    }, {
        id: 'id2',
        label: 'label2 - sample option'
    }, {
        id: 'id3',
        label: 'label3 - option'
    }];

    var fetchPageResponse = {
        promise: 'some promise',
        someArray: ['A', 'B', 'C']
    };

    var field_with_uri = {
        cmsStructureType: 'EditableDropdown',
        qualifier: 'dropdownA',
        i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
        uri: '/someuri',
        smarteditComponentType: 'componentX'
    };

    var field_with_dependsOn = {
        cmsStructureType: 'EditableDropdown',
        qualifier: 'dropdownA',
        i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
        uri: '/someuri',
        dependsOn: 'dropdown1,dropdown2',
        smarteditComponentType: 'componentX'
    };

    var field_with_none_no_populator = {
        cmsStructureType: 'EditableDropdown',
        qualifier: 'dropdownX',
        i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
        smarteditComponentType: 'componentY'
    };

    var field_with_both = {
        cmsStructureType: 'EditableDropdown',
        qualifier: 'dropdownA',
        i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
        uri: '/someuri',
        options: [],
        smarteditComponentType: 'componentX'
    };

    var field_with_property_type = {
        cmsStructureType: 'SingleProductSelector',
        propertyType: 'customPropertyType',
        qualifier: 'dropdownA',
        i18nKey: 'type.thesmarteditComponentType.product.name',
        required: true
    };

    var model = {
        dropdown1: '1',
        dropdown2: '2',
        dropdownA: 'id1'
    };

    var qualifier = 'dropdownA';
    var id = new Date().valueOf();

    beforeEach(module('gatewayFactoryModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('gatewayProxyModule', function($provide) {

        var gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    angular.module('customService', []);
    beforeEach(module('customService', function($provide) {
        componentXDropdownPopulator = jasmine.createSpyObj('componentXDropdownPopulator', ['populate']);

        componentXDropdownPopulator.populate.and.returnValue(options);
        $provide.value('componentXDropdownPopulator', componentXDropdownPopulator);
    }));

    beforeEach(module('customService', function($provide) {
        componentYdropdownADropdownPopulator = jasmine.createSpyObj('componentYdropdownADropdownPopulator', ['populate']);

        componentYdropdownADropdownPopulator.populate.and.returnValue(options);
        $provide.value('componentYdropdownADropdownPopulator', componentYdropdownADropdownPopulator);
    }));

    beforeEach(module('customService', function($provide) {
        $provide.value('customPropertyTypeDropdownPopulator', {
            type: 'customPropertyTypeDropdownPopulator'
        });
    }));

    beforeEach(module('seDropdownModule'));
    beforeEach(module('eventServiceModule', function($provide) {
        systemEventServ = jasmine.createSpyObj('systemEventService', ['registerEventHandler', 'sendAsynchEvent']);
        $provide.value('systemEventService', systemEventServ);
    }));

    beforeEach(module('optionsDropdownPopulatorModule', function($provide) {
        optionsDropdownPopulator = jasmine.createSpyObj('optionsDropdownPopulator', ['populate']);

        $provide.value('optionsDropdownPopulator', optionsDropdownPopulator);
    }));

    beforeEach(module('functionsModule', function($provide) {
        getKeyHoldingDataFromResponse = jasmine.createSpy('getKeyHoldingDataFromResponse');
        getKeyHoldingDataFromResponse.and.returnValue('someArray');

        $provide.value('getKeyHoldingDataFromResponse', getKeyHoldingDataFromResponse);
    }));

    beforeEach(module('uriDropdownPopulatorModule', function($provide) {
        uriDropdownPopulator = jasmine.createSpyObj('uriDropdownPopulator', ['populate', 'fetchPage']);

        uriDropdownPopulator.populate.and.callFake(function() {
            return $q.when(options);
        });

        uriDropdownPopulator.fetchPage.and.callFake(function() {
            return $q.when(fetchPageResponse);
        });


        $provide.value('uriDropdownPopulator', uriDropdownPopulator);
    }));

    beforeEach(inject(function(_$rootScope_, _$q_, _$injector_, _SEDropdownService_) {

        $q = _$q_;
        $rootScope = _$rootScope_;
        $injector = _$injector_;
        SEDropdownService = _SEDropdownService_;
    }));

    it('seDropdown initializes fine', function() {

        var seDropdown = new SEDropdownService({
            field: field_with_none_no_populator,
            model: model,
            qualifier: qualifier,
            id: id
        });

        expect(seDropdown.field).toEqual(field_with_none_no_populator);
        expect(seDropdown.model).toEqual(model);
        expect(seDropdown.qualifier).toEqual(qualifier);

    });

    describe('init method - ', function() {

        it('GIVEN SEDropdownService is initialized WHEN the field object has both options and uri attributes THEN it throws an error', function() {

            var seDropdown = new SEDropdownService({
                field: field_with_both,
                model: model,
                qualifier: qualifier,
                id: id
            });

            expect(function() {
                return seDropdown.init();
            }).toThrow('se.dropdown.contains.both.uri.and.options');

        });

        it('GIVEN SEDropdownService is initialized WHEN the field object has dependsOn attribute THEN init method must register an event', function() {

            var seDropdown = new SEDropdownService({
                field: field_with_dependsOn,
                model: model,
                qualifier: qualifier,
                id: id
            });

            spyOn(seDropdown, '_respondToChange').and.callFake(function() {});
            uriDropdownPopulator.populate.and.returnValue($q.when(options));
            seDropdown.init();
            $rootScope.$digest();

            expect(systemEventServ.registerEventHandler).toHaveBeenCalledWith(id + 'LinkedDropdown', jasmine.any(Function));
            var respondToChangeCallback = systemEventServ.registerEventHandler.calls.argsFor(0)[1];
            respondToChangeCallback();
            expect(seDropdown._respondToChange).toHaveBeenCalled();

        });

    });

    it('GIVEN SEDropdownService is initialized WHEN fetchAll is called THEN the respective populator is called with the correct payload', function() {

        var searchKey = 'sample';
        var selection = {
            a: 'b'
        };

        var seDropdown = new SEDropdownService({
            field: field_with_uri,
            model: model,
            qualifier: qualifier,
            id: id
        });

        uriDropdownPopulator.populate.and.returnValue($q.when(options.filter(function(option) {
            return option.label.toUpperCase().indexOf(searchKey.toUpperCase()) > -1;
        })));
        seDropdown.init();
        seDropdown.selection = selection;
        seDropdown.fetchAll(searchKey);
        $rootScope.$digest();

        expect(uriDropdownPopulator.populate).toHaveBeenCalledWith({
            field: field_with_uri,
            model: model,
            search: searchKey,
            selection: selection
        });
        expect(seDropdown.items).toEqual([{
            id: 'id1',
            label: 'label1 - sample'
        }, {
            id: 'id2',
            label: 'label2 - sample option'
        }]);
    });

    it('GIVEN SEDropdownService is initialized WHEN triggerAction is called THEN then sendAsynchEvent method is called with correct attributes ', function() {

        var seDropdown = new SEDropdownService({
            field: field_with_uri,
            model: model,
            qualifier: qualifier,
            id: id
        });

        uriDropdownPopulator.populate.and.returnValue($q.when(options));
        seDropdown.init();
        $rootScope.$digest();
        seDropdown.fetchAll();
        $rootScope.$digest();
        seDropdown.triggerAction();
        $rootScope.$digest();

        expect(systemEventServ.sendAsynchEvent).toHaveBeenCalledWith(id + 'LinkedDropdown', {
            qualifier: qualifier,
            optionObject: {
                id: 'id1',
                label: 'label1 - sample'
            }
        });

    });

    it('GIVEN SEDropdownService is initialized WHEN _respondToChange is called and if the fields dependsOn doesnot match the input qualifier THEN then nothing happens (populator not called)', function() {

        var seDropdown = new SEDropdownService({
            field: field_with_uri,
            model: model,
            qualifier: qualifier,
            id: id
        });

        uriDropdownPopulator.populate.and.returnValue($q.when(options));

        seDropdown._respondToChange(qualifier, {
            id: 'id1',
            label: 'label1 - sample'
        });
        $rootScope.$digest();

        expect(uriDropdownPopulator.populate).not.toHaveBeenCalled();

    });

    it('GIVEN SEDropdownService is initialized WHEN _respondToChange is called and if the fields dependsOn matches the input qualifier THEN then reset is called on the child component and a selection is made ready for the next refresh', function() {

        var seDropdown = new SEDropdownService({
            field: field_with_dependsOn,
            model: model,
            qualifier: qualifier,
            id: id
        });

        //2-way binding with child defined function
        seDropdown.reset = function() {};

        spyOn(seDropdown, 'reset').and.returnValue();

        seDropdown.init();
        $rootScope.$digest();

        var changeObject = {
            qualifier: 'dropdown1',
            optionObject: {},
        };

        seDropdown._respondToChange('SomeKey', changeObject);
        $rootScope.$digest();
        expect(seDropdown.reset).toHaveBeenCalled();
        expect(seDropdown.selection).toBe(changeObject.optionObject);

    });

    it('GIVEN SEDropdownService is initialized with a field object that has a propertyType attribute WHEN fetchAll is called THEN the respective populator is called with the correct payload', function() {
        var seDropdown = new SEDropdownService({
            field: field_with_property_type
        });
        seDropdown.init();
        expect(seDropdown.populator.type).toEqual('customPropertyTypeDropdownPopulator');
    });

    it('GIVEN SEDropdown is initialized WHEN fetchPage is called THEN it retrieves and returns the result with the right format', function() {
        // GIVEN
        var seDropdown = new SEDropdownService({
            field: field_with_uri,
            model: model,
            qualifier: qualifier,
            id: id
        });

        var expectedResult = ['A', 'B', 'C'];

        // WHEN
        seDropdown.init();
        $rootScope.$digest();
        var result = seDropdown.fetchPage();
        $rootScope.$digest();

        // THEN
        expect(getKeyHoldingDataFromResponse).toHaveBeenCalledWith(fetchPageResponse);
        result.then(function(value) {
            expect(value.results).toEqualData(expectedResult);
        });
        $rootScope.$digest();
    });

});
