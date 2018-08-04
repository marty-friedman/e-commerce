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
describe('fetchEnumDataHandler', function() {

    var enumRestService, fetchEnumDataHandler, $rootScope;
    var field = {
        cmsStructureEnumType: 'de.mypackage.Orientation'
    };
    var data = [{
        code: 'code1',
        label: 'Vertical'
    }, {
        code: 'code2',
        label: 'Horizontal'
    }];
    beforeEach(module('smarteditServicesModule', function($provide) {
        enumRestService = jasmine.createSpyObj('enumRestService', ['get']);
        var restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(enumRestService);
        $provide.value('restServiceFactory', restServiceFactory);
    }));

    beforeEach(module('fetchEnumDataHandlerModule'));

    beforeEach(inject(function(_fetchEnumDataHandler_, $q, _$rootScope_) {
        fetchEnumDataHandler = _fetchEnumDataHandler_;
        enumRestService.get.and.returnValue($q.when({
            enums: data
        }));
        $rootScope = _$rootScope_;
    }));

    it('GIVEN enum REST call succeeds WHEN I findByMask with no mask, promise resolves to the full list', function() {

        // WHEN
        var promise = fetchEnumDataHandler.findByMask(field);

        // THEN
        expect(promise).toBeResolvedWithData(data);
    });

    it('GIVEN enum REST call succeeds WHEN I findByMask with a mask, promise resolves to the relevant filtered list', function() {

        // WHEN
        var promise = fetchEnumDataHandler.findByMask(field, 'zo');

        // THEN
        expect(promise).toBeResolvedWithData([{
            code: 'code2',
            label: 'Horizontal'
        }]);
    });

    it('GIVEN a first search, second uses cache', function() {

        // WHEN
        fetchEnumDataHandler.findByMask(field, 'zo');

        $rootScope.$digest();

        expect(enumRestService.get.calls.count()).toBe(1);

        fetchEnumDataHandler.findByMask(field, 'zon');

        $rootScope.$digest();

        expect(enumRestService.get.calls.count()).toBe(1);


    });


});
