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
describe('Page Info Menu', function() {

    var pageInfo = e2e.pageObjects.PageInfo;
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;
    var sfBuilder = e2e.se.componentObjects.sfBuilder;

    beforeEach(function() {
        browser.bootstrap(__dirname);
        browser.waitForWholeAppToBeReady();
    });

    //FIXME: should be able to clickthrough
    afterEach(function(done) {
        perspective.select(perspective.PREVIEW_PERSPECTIVE).then(function() {
            browser.waitForAngularEnabled(true);
            done();
        });
    });

    describe('content page', function() {
        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                done();
            });
        });

        it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page type is displayed', function() {
            // GIVEN
            pageInfo.actions.openPageInfoMenu();

            // THEN
            expect(pageInfo.elements.getPageType().getText())
                .toBe('ContentPage', 'Expected page type to be "ContentPage"');
        });

        it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page template is displayed', function() {
            // GIVEN
            pageInfo.actions.openPageInfoMenu();

            // THEN
            expect(pageInfo.elements.getPageTemplate().getText())
                .toBe('AccountPageTemplate', 'Expected page template to be "AccountPageTemplate"');
        });

        it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page info is displayed', function() {
            // GIVEN
            pageInfo.actions.openPageInfoMenu();

            // THEN
            expect(pageInfo.elements.getPageNameField().getAttribute('value'))
                .toBe('Homepage', 'Expected page name to be "Homepage"');
            expect(pageInfo.elements.getPageLabelField().getAttribute('value'))
                .toBe('i-love-pandas', 'Expected page label to be "i-love-pandas"');
            expect(pageInfo.elements.getPageUidField().getAttribute('value'))
                .toBe('homepage', 'Expected page UID to be "homepage"');
            expect(pageInfo.elements.getPageTitleField().getAttribute('value'))
                .toBe('I love pandas', 'Expected page name to be "I love pandas"');
            expect(pageInfo.elements.getPageCreationTimeField().getAttribute('value'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page creation time to be short date format');
            expect(pageInfo.elements.getPageModifiedTimeField().getAttribute('value'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page modification time to be short date format');
        });

        it('GIVEN the user is in the page info menu WHEN the user clicks the Edit button THEN the page editor modal is opened', function() {
            // WHEN
            pageInfo.actions.openPageInfoMenu();
            pageInfo.actions.clickEditButton();

            // THEN
            expect(pageInfo.elements.getPageEditorModal().isPresent())
                .toBe(true, 'Expected Page Editor modal to be opened');

            //FIXME: should be able to clickthrough
            browser.click(element(by.css('.modal-dialog #cancel')));
        });
    });

    describe('product page', function() {
        beforeEach(function(done) {
            storefront.actions.goToSecondPage().then(function() {
                perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                    pageInfo.actions.openPageInfoMenu();
                    done();
                });
            });
        });

        it('GIVEN the user is on a storefront product page WHEN the user opens the page info menu THEN the page info is displayed', function() {
            // THEN
            expect(pageInfo.elements.getPageType().getText())
                .toBe('ProductPage', 'Expected page type to be "ProductPage"');
        });

        it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the page template is displayed', function() {
            // THEN
            expect(pageInfo.elements.getPageTemplate().getText())
                .toBe('ProductPageTemplate', 'Expected page template to be "ProductPageTemplate"');
        });

        it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the page info is displayed', function() {
            // THEN
            expect(pageInfo.elements.getPageNameField().getAttribute('value'))
                .toBe('Some Other Page', 'Expected page name to be "Some Other Page"');
            expect(pageInfo.elements.getPageUidField().getAttribute('value'))
                .toBe('secondpage', 'Expected page UID to be "secondpage"');
            expect(pageInfo.elements.getPageTitleField().getAttribute('value'))
                .toBe('I hate pandas', 'Expected page name to be "I hate pandas"');
            expect(pageInfo.elements.getPageCreationTimeField().getAttribute('value'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
            expect(pageInfo.elements.getPageModifiedTimeField().getAttribute('value'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page name to be short date format');
        });

        it('GIVEN the user navigates the storefront product page (server rendered page) WHEN the user opens the page info menu THEN uid field is present', function() {
            // THEN
            pageInfo.assertions.uidIs("secondpage");
        });
    });

    describe('product page - rich client storefront', function() {
        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                pageInfo.actions.openPageInfoMenu();
                done();
            });
        });

        it('GIVEN the user navigates the storefront product page WHEN the user opens the page info menu THEN uid field is present', function() {
            // WHEN
            browser.switchToIFrame();
            sfBuilder.actions.changePageIdWithoutInteration('secondpage');
            pageInfo.actions.openPageInfoMenu();

            // THEN
            pageInfo.assertions.uidIs("secondpage");
        });
    });
});
