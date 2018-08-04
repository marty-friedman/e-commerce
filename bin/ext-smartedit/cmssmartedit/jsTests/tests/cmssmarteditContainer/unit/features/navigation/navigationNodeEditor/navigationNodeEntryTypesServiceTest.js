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
/* jshint unused:false, undef:false */
describe('navigationNodeEntryTypesService - ', function() {

    var navigationNodeEntryTypesService;
    var navigationEntryTypesRestService, $q, $rootScope;

    beforeEach(module('resourceModule', function($provide) {
        navigationEntryTypesRestService = jasmine.createSpyObj('navigationEntryTypesRestService', ['get']);
        $provide.value('navigationEntryTypesRestService', navigationEntryTypesRestService);
    }));

    beforeEach(module('navigationNodeEntryTypesServiceModule'));

    beforeEach(inject(function(_navigationNodeEntryTypesService_, _$q_, _$rootScope_) {
        navigationNodeEntryTypesService = _navigationNodeEntryTypesService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('WHEN getSearchResults is called THEN a promise should be returned and resolved with given data', function() {

        //GIVEN
        navigationEntryTypesRestService.get.and.returnValue($q.when(entryTypes));

        //WHEN
        $rootScope.$digest();
        expect(navigationNodeEntryTypesService.getNavigationNodeEntryTypes()).toBeResolvedWithData(resolvedPromiseData);
    });

    var entryTypes = {
        "navigationEntryTypes": [{
            "itemType": "AbstractCMSComponent"
        }, {
            "itemType": "AbstractPage"
        }, {
            "itemType": "Media"
        }]
    };

    var resolvedPromiseData = [{
        itemType: 'AbstractCMSComponent'
    }, {
        itemType: 'AbstractPage'
    }, {
        itemType: 'Media'
    }];

});
