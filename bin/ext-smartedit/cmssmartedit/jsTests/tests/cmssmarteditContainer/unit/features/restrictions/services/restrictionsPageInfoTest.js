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
describe('restrictionsPageInfo', function() {

    var element;

    beforeEach(module('cmssmarteditContainerTemplates'));

    beforeEach(module('restrictionsPageInfoModule'));

    beforeEach(module('pascalprecht.translate', function($translateProvider) {


        $translateProvider.translations('en', {
            'page.displaycondition.primary': 'mocked primary value',
            'page.displaycondition.primary.description': 'mocked primary description',
            'page.displaycondition.variation': 'mocked variation value',
            'page.displaycondition.variation.description': 'mocked variation description'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function($rootScope, $compile) {
        element = templateSetup('<restrictions-page-info data-page-id="pageId" data-page-name="pageName"></restrictions-page-info>', $compile, $rootScope, {
            pageId: 'homepage',
            pageName: 'Homepage'
        });
    }));

    describe('when page is primary', function() {

        beforeEach(inject(function($rootScope, $compile) {
            element = templateSetup('<restrictions-page-info data-page-id="pageId" data-page-name="pageName" data-page-is-primary="pageIsPrimary"></restrictions-page-info>', $compile, $rootScope, {
                pageId: 'homepage',
                pageName: 'Homepage',
                pageIsPrimary: true
            });
        }));

        it('should display the name of the page', function() {
            expect(element.text()).toContain('Homepage');
        });

        it('should display the translated primary value', function() {
            expect(element.find('.ySERestrictionsPageInfoContainer--displayconditions-value').text().trim()).toEqual('mocked primary value');
        });

        it('should display the translated primary description', function() {
            expect(element.find('.ySERestrictionsPageInfoContainer--displayconditions-description').text().trim()).toEqual('mocked primary description');
        });

    });

    describe('when page is variation', function() {

        beforeEach(inject(function($rootScope, $compile) {
            element = templateSetup('<restrictions-page-info data-page-id="pageId" data-page-name="pageName" data-page-is-primary="pageIsPrimary" data-associated-primary-page-name="associatedPrimaryPageName"></restrictions-page-info>', $compile, $rootScope, {
                pageId: 'homepage',
                pageName: 'Homepage',
                pageIsPrimary: false,
                associatedPrimaryPageName: 'Primary Page Name'
            });
        }));

        it('should display the name of the page', function() {
            expect(element.text()).toContain('Homepage');
        });

        it('should display the translated variation value', function() {
            expect(element.find('.ySERestrictionsPageInfoContainer--displayconditions-value').text()).toEqual('mocked variation value');
        });

        it('should not display the translated variation description', function() {
            expect(element.find('.ySERestrictionsPageInfoContainer--displayconditions-description').text().trim()).not.toEqual('mocked variation description');
        });

        it('should display the name of the associated primary page', function() {
            expect(element.find('.ySERestrictionsPageInfoContainer--associatedprimarypage-name').text()).toContain('Primary Page Name');
        });

    });

});
