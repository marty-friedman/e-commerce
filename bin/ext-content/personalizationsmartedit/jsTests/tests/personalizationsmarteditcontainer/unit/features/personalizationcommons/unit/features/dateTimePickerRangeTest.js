/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('dateTimePickerRange', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $compile, $rootScope, $translate;

    beforeEach(module('personalizationsmarteditCommons', function($provide) {
        $translate = jasmine.createSpyObj('$translate', ['instant']);
        $provide.value('$translate', $translate);
        $provide.value('translateFilter', function(data) {
            return data;
        });

        $provide.value('isBlank', function(elem) {
            return elem === undefined;
        });

    }));

    beforeEach(inject(function(_$compile_, _$rootScope_, $templateCache) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        var directiveTemplate = $templateCache.get('web/features/personalizationcommons/dateTimePickerRange/dateTimePickerRangeTemplate.html');
        $templateCache.put('dateTimePickerRangeTemplate.html', directiveTemplate);
    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        var element = $compile("<date-time-picker-range name='data-date-time-from-to-key' " +
            "data-date-from='customization.enabledStartDate' " +
            "data-date-to='customization.enabledEndDate' " +
            "data-is-editable='true' date-format='edit.viewDateFormat'>" +
            "</date-time-picker-range>")($rootScope);
        // when
        $rootScope.$digest();
        var subText = "<div class=\"pe-datetime-range\">";
        // then
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
