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
describe('cmsNavigationEntryDropdownPopulator - ', function() {

    var navigationNodeEntryTypesService, DropdownPopulatorInterface, cmsNavigationEntryDropdownPopulator, extend, $rootScope, $q;

    var entries = [{
        uid: 'entry-1',
        navigationNodeUid: '1',
        itemId: 'item-1',
        itemType: 'component1',
        position: 0,
        name: 'entry-name-1'
    }, {
        uid: 'entry-2',
        navigationNodeUid: '1',
        itemId: 'item-2',
        itemType: 'component2',
        position: 1,
        name: 'entry-name-2'
    }];

    beforeEach(module('functionsModule'));

    beforeEach(module('navigationNodeEditorCreateEntryModule', function($provide) {

        navigationNodeEntryTypesService = jasmine.createSpyObj('navigationNodeEntryTypesService', ['getNavigationNodeEntryTypes']);
        $provide.value('navigationNodeEntryTypesService', navigationNodeEntryTypesService);

    }));

    beforeEach(inject(function(_cmsNavigationEntryDropdownPopulator_, _$rootScope_, _$q_) {
        cmsNavigationEntryDropdownPopulator = _cmsNavigationEntryDropdownPopulator_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));

    it('GIVEN navigationNodeEntryTypesService returns a list of entries WHEN I call populate of cmsNavigationEntryDropdownPopulator THEN it returns an array of id-label options containing the entries', function() {

        //GIVEN
        navigationNodeEntryTypesService.getNavigationNodeEntryTypes.and.returnValue($q.when(entries));

        //WHEN
        var promise = cmsNavigationEntryDropdownPopulator.populate();

        //THEN
        expect(navigationNodeEntryTypesService.getNavigationNodeEntryTypes).toHaveBeenCalled();
        expect(promise).toBeResolvedWithData([{
            id: 'component1',
            label: 'se.cms.component1'
        }, {
            id: 'component2',
            label: 'se.cms.component2'
        }]);

    });

    it('GIVEN navigationNodeEntryTypesService returns a rejected promise WHEN I call populate of cmsNavigationEntryDropdownPopulator THEN it returns a rejected promise', function() {

        //GIVEN
        navigationNodeEntryTypesService.getNavigationNodeEntryTypes.and.returnValue($q.reject(entries));

        //WHEN
        var promise = cmsNavigationEntryDropdownPopulator.populate();

        //THEN
        expect(navigationNodeEntryTypesService.getNavigationNodeEntryTypes).toHaveBeenCalled();
        expect(promise).toBeRejected();

    });


});
