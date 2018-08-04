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
describe('personalizationsmarteditPagination', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_$compile_, _$rootScope_, $templateCache) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        var directiveTemplate = $templateCache.get('web/features/personalizationcommons/personalizationsmarteditPagination/personalizationsmarteditPaginationTemplate.html');
        $templateCache.put('personalizationsmarteditPaginationTemplate.html', directiveTemplate);
    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        $rootScope.paginationCallback = function() {};
        var element = $compile("<personalizationsmartedit-pagination pages=\"pagination.pages\" " +
            "current-page=\"pagination.currentPage\" page-sizes=\"pagination.pageSizes\" " +
            "current-size=\"pagination.currentSize\" pages-offset=\"pagination.pagesOffset\" " +
            "callback=\"paginationCallback\" />")($rootScope);
        // when
        $rootScope.$digest();
        // then
        var subText = "<div class=\"row\">";
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
