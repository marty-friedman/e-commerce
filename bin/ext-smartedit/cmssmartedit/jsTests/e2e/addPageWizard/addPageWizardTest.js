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
describe('Add Page Wizard', function() {

    var addPageWizard = e2e.pageObjects.AddPageWizard;
    var landingPage = e2e.pageObjects.landingPage;

    beforeEach(function(done) {
        browser.waitForAngularEnabled(false);
        browser.bootstrap().then(function() {
            return browser.waitForContainerToBeReady().then(function() {
                return navigateToFirstCatalogPageList().then(function() {
                    done();
                });
            });
        });
    });

    describe('Type Step', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {
                done();
            });
        });

        it('WHEN "Add New Page" is clicked THEN the Add Page wizard is opened to the Type step', function() {
            expect(addPageWizard.elements.isWindowOpen()).toBeTruthy();
            addPageWizard.assertions.currentStepTextIs('TYPE');
        });

        it('WHEN Add Page wizard is opened THEN the Type step is selected by default AND the available page types are displayed', function() {
            expect(addPageWizard.elements.getContentPageTypeListItem().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getProductPageTypeListItem().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getCategoryPageTypeListItem().isDisplayed()).toBe(true);
        });

        it('GIVEN the wizard is open on the Type tab and the user has not selected a type WHEN the user clicks next THEN the wizard will remain on the Type tab', function() {
            addPageWizard.assertions.cannotClickNext();
        });
    });

    describe('Template Step', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectContentPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return done();
            });

        });

        it('GIVEN the wizard is open on the Type tab and the user has selected a type WHEN the user clicks next THEN the wizard will move to the Template tab AND available template types are displayed', function() {
            addPageWizard.assertions.currentStepTextIs('TEMPLATE');
            expect(addPageWizard.elements.getPageTemplate1ListItem().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getPageTemplate2ListItem().isDisplayed()).toBe(true);
        });

        it('GIVEN the wizard is opened on the Template tab and the user has not selected a template WHEN the user clicks next THEN the wizard will remain on the Template tab', function() {
            addPageWizard.assertions.cannotClickNext();
        });
    });

    describe('Display Condition Step for Content Page', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectContentPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });
        });

        it('GIVEN the wizard is opened on the Template tab and the user has selected a template WHEN the user clicks next THEN the wizard will move to the Display Condition tab', function() {
            addPageWizard.assertions.currentStepTextIs('DISPLAY CONDITION');
        });

        it('GIVEN the wizard is opened to the Template tab for Content Page WHEN the user clicks the Display Condition dropdown THEN he is presented with Primary and Variation options', function() {
            addPageWizard.actions.openConditionDropdown().then(function() {
                addPageWizard.elements.isPrimaryConditionOptionDisplayed();
                addPageWizard.assertions.variationConditionOptionIsDisplayed();
            });
        });

        it('GIVEN the wizard is opened to the Template tab for the Content Page and Primary is selected THEN the dropdown of all content pages is hidden', function() {
            addPageWizard.actions.selectPrimaryCondition().then(function() {
                browser.waitForAbsence(addPageWizard.elements.getPrimaryPageDropdownToggle());
            });
        });

        it('GIVEN the wizard is opened to the Template tab for the Content Page and Variation is selected WHEN the user clicks the Primary Page dropdown THEN he is presented with the dropdown of all content pages', function() {
            addPageWizard.actions.selectVariationCondition().then(function() {
                addPageWizard.elements.isPage1PrimaryPageOptionDisplayed();
            });
        });
    });

    describe('Display Condition Step for Product Page', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectProductPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });
        });

        xit('GIVEN the wizard is opened to the Template tab for Product Page and no other Product Pages exist WHEN the user clicks the Display Condition dropdown THEN he is presented with the variation option', function() {
            addPageWizard.actions.openConditionDropdown().then(function() {

                addPageWizard.elements.isPrimaryConditionOptionNotDisplayed();
                expect(addPageWizard.elements.getVariationConditionOption().isDisplayed()).toBe(true);
            });
        });

        it('GIVEN the wizard is opened to the Template tab for Product Page and Variation is selected WHEN the user clicks the Primary Page dropdown THEN he is presented with the dropdown of the single available Primary page', function() {
            addPageWizard.actions.selectVariationCondition();
            addPageWizard.actions.openConditionDropdown().then(function() {

                addPageWizard.elements.isProductPage1PrimaryPageOptionDisplayed();
            });
        });
    });

    describe('Display Condition Step for Category Page', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectCategoryPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });
        });

        it('GIVEN the wizard is opened to the Template tab for Category Page and another Category Page exists ' +
            'WHEN the user clicks the Display Condition dropdown ' +
            'THEN he is presented with the Primary option',
            function() {
                addPageWizard.actions.openConditionDropdown().then(function() {
                    browser.waitForAbsence(addPageWizard.elements.getPrimaryConditionOption());
                    expect(addPageWizard.elements.getVariationConditionOption().isPresent()).toBe(true);
                });
            });
    });

    describe('Restrictions Step for Content Page', function() {
        beforeEach(function(done) {

            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectContentPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectVariationCondition();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                addPageWizard.actions.enterSomeValidPageInfoForVariationPage();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the wizard is opened to the Display Condition step AND Primary is selected WHEN the user clicks next THEN he is brought to the Page Info step', function() {
            addPageWizard.assertions.currentStepTextIs('RESTRICTIONS');
        });

        it('GIVEN the wizard is on the Restrictions step THEN the restrictions editor is displayed', function() {
            expect(addPageWizard.elements.getPageRestrictionsEditor().isDisplayed()).toBe(true);
        });

        it('GIVEN the wizard is on the Restrictions step with no restrictions, THEN the Done button is disabled', function() {
            expect(addPageWizard.elements.isDoneButtonEnabled()).toBe(false);
        });

        xit('GIVEN the wizard is on the Restrictions step WHEN the user applies a restriction THEN the Done button is enabled', function() {
            addPageWizard.actions.addRestriction().then(function() {
                expect(addPageWizard.elements.isDoneButtonEnabled()).toBe(true);
            });
        });
    });

    describe('Restrictions Step for Category Page', function() {
        beforeEach(function(done) {

            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectCategoryPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectVariationCondition();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                addPageWizard.actions.enterSomeValidPageInfoForVariationPage();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the wizard is opened to the Display Condition step AND Variation is selected WHEN the user clicks next THEN he is brought to the Restrictions step', function() {
            addPageWizard.assertions.currentStepTextIs('RESTRICTIONS');
        });
    });

    describe('Page Info Step for Primary Content Page', function() {
        beforeEach(function(done) {

            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectContentPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPrimaryCondition();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the user is on the Display Condition step AND Primary is selected WHEN he clicks next THEN he is presented with the Page Info step', function() {
            addPageWizard.assertions.currentStepTextIs('INFO');
        });

        it('GIVEN the wizard is open on the Display Condition tab and the user has selected Primary as a Display Condition WHEN the user clicks NEXT the wizard will move to the Info tab', function() {
            expect(addPageWizard.elements.getNameInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getLabelInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getTitleInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getIDInput().isDisplayed()).toBe(true);
        });

        it('GIVEN the user is creating a Content Page with the Primary display condition WHEN the user navigates to the Display Condition step THEN he is presented with an editable and empty label field', function() {
            expect(addPageWizard.elements.getLabelInputEnabled()).toBe(true);
            expect(addPageWizard.elements.getLabelInputText()).toBe('');
        });

        it('GIVEN the wizard is opened on the Page Info and the user has not entered any content WHEN the user clicks submit THEN the user will remain on the Page Info step of the Add Page wizard', function() {
            addPageWizard.actions.clickSubmit().then(function() {
                addPageWizard.assertions.currentStepTextIs('INFO');
                expect(addPageWizard.elements.isWindowOpen()).toBeTruthy();
            });
        });

        it('GIVEN the wizard is opened on the third tab and the user has entered wrong data WHEN the user clicks submit THEN the wizard will show an error message and stays in the third tab', function() {
            addPageWizard.actions.enterPageName('page name');
            addPageWizard.actions.enterLabel('label');
            addPageWizard.actions.enterInvalidUid();
            addPageWizard.actions.enterPageTitle('some title');
            addPageWizard.actions.clickSubmit().then(function() {
                expect(addPageWizard.elements.getErrorMessageText()).toBe('No Trump jokes plz.');
                addPageWizard.assertions.currentStepTextIs('INFO');
                expect(addPageWizard.elements.isWindowOpen()).toBeTruthy();
            });
        });

        it('GIVEN the wizard is opened on the third tab and the user has entered valid data WHEN the user clicks submit THEN the wizard closes', function() {
            addPageWizard.actions.enterSomeValidPageInfo().then(function() {
                addPageWizard.actions.clickSubmit().then(function() {

                    addPageWizard.elements.assertWindowIsClosed();

                });
            });
        });

        it('GIVEN the wizard is opened on the third tab and the user has entered valid data WHEN the user clicks submit THEN the user is redirected to the newly created page', function() {
            addPageWizard.actions.enterSomeValidPageInfo().then(function() {
                addPageWizard.actions.clickSubmit().then(function() {
                    expect(browser.getCurrentUrl()).toContain('valid');

                });
            });
        });
    });

    describe('Page Info Step for Variation Content Page', function() {
        beforeEach(function(done) {

            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectContentPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectVariationCondition();
            }).then(function() {
                return addPageWizard.actions.selectPage1AsPrimaryPage();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the user is on the Restrictions step AND a Restriction is applied WHEN he clicks next THEN he is presented with the Page Info step', function() {
            addPageWizard.assertions.currentStepTextIs('INFO');
        });

        it('GIVEN the user is creating a Content Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is presented with the name, label, and ID fields', function() {
            expect(addPageWizard.elements.getNameInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getTitleInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getIDInput().isDisplayed()).toBe(true);
        });

        it('GIVEN the user is creating a Content Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is presented with a read-only, populated label field', function() {
            expect(addPageWizard.elements.getLabelInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getLabelInputEnabled()).toBe(false);
            expect(addPageWizard.elements.getLabelInputText()).toBe('page1TitleSuffix');
        });
    });

    describe('Page Info Step for Primary Product Page', function() {
        beforeEach(function(done) {
            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectProductPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the user is creating a Product Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is presented with the name, label, and ID fields', function() {
            expect(addPageWizard.elements.getNameInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getTitleInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getIDInput().isDisplayed()).toBe(true);
        });

        it('GIVEN the user is creating a Product Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is not presented with a label field', function() {
            browser.waitForAbsence(addPageWizard.elements.getLabelInput());
        });
    });

    describe('Page Info Step for Variation Category Page', function() {
        beforeEach(function(done) {

            addPageWizard.actions.openAddPageWizard().then(function() {

            }).then(function() {
                return addPageWizard.actions.selectCategoryPageType();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.selectPageTemplate1();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                return addPageWizard.actions.clickNext();
            }).then(function() {
                done();
            });

        });

        it('GIVEN the user is creating a Product Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is presented with the name, label, and ID fields', function() {
            expect(addPageWizard.elements.getNameInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getTitleInput().isDisplayed()).toBe(true);
            expect(addPageWizard.elements.getIDInput().isDisplayed()).toBe(true);
        });

        it('GIVEN the user is creating a Product Page with the Primary display condition WHEN the user navigates to the Page Info step THEN he is not presented with a label field', function() {
            browser.waitForAbsence(addPageWizard.elements.getLabelInput());
        });
    });

    function navigateToFirstCatalogPageList() {
        return landingPage.actions.navigateToFirstOnlineCatalogPageList();
    }
});
