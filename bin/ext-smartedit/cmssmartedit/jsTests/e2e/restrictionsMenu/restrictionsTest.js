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

describe('Restrictions Menu', function() {

    var restrictionsMenu = e2e.pageObjects.RestrictionsMenu;

    describe('Primary Page', function() {

        xit('WHEN the user opens the restrictions menu for a primary page THEN the current page name is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForHomepage();

            // THEN
            expect(restrictionsMenu.elements.getPageNameText())
                .toBe('Homepage', 'Expected restrictions menu to display page name');
        });

        xit('WHEN the user opens the restrictions menu for a primary page THEN the current page display condition is shown as Primary', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForHomepage();

            // THEN
            expect(restrictionsMenu.elements.getPageDisplayConditionsText())
                .toBe('Primary', 'Expected restrictions menu to show display condition Primary');
        });

        xit('WHEN the user opens the restrictions menu for a primary page THEN the description for the Primary display condition is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForHomepage();

            // THEN
            expect(restrictionsMenu.elements.getDisplayConditionsDescriptionText())
                .toBe('This is a primary page and it will be displayed if no other variation page exists', 'Expected restrictions menu to show description for Primary display condition');
        });

        xit('WHEN the user opens the restrictions menu for a primary page THEN the restrictions table is not displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForHomepage();

            // THEN
            expect(restrictionsMenu.elements.getRestrictionsMenuTable().isPresent())
                .not.toBe(true, 'Expected restrictions menu table not to be present');
        });
    });

    describe('Variation Page', function() {

        xit('WHEN the user opens the restrictions menu for a variation page THEN the current page name is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getPageNameText())
                .toBe('Second Page', 'Expected restrictions menu to display page name');
        });

        xit('WHEN the user opens the restrictions menu for a variation page THEN the current page display condition is shown as Variation', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getPageDisplayConditionsText())
                .toBe('Variation', 'Expected restrictions menu to show display condition Variation');
        });

        xit('WHEN the user opens the restrictions menu for a variation page THEN the associated primary page is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getAssociatedPrimaryPageText())
                .toBe('Some Primary Page', 'Expected associated primary page to be "Some Primary Page"');
        });

        xit('WHEN the user opens the restrictions menu for a variation page THEN the restrictions table is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getRestrictionsMenuTable().isPresent())
                .toBe(true, 'Expected restrictions table to be present');
        });

        xit('WHEN the user opens the restrictions menu for a variation page THEN the restrictions table is populated with the restrictions for the page', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getFirstRestrictionNameText())
                .toBe('RESTRICTION A', 'Expected first restriction instance name to be "RESTRICTION A"');
            expect(restrictionsMenu.elements.getFirstRestrictionTypeText())
                .toBe('CatalogRestriction', 'Expected first restriction instance type to be "CatalogRestriction"');
            expect(restrictionsMenu.elements.getFirstRestrictionDescriptionText())
                .toBe('Restriction A Description', 'Expected first restriction description to be "Restriction A Description"');
            expect(restrictionsMenu.elements.getSecondRestrictionNameText())
                .toBe('RESTRICTION B', 'Expected first restriction instance name to be "RESTRICTION B"');
            expect(restrictionsMenu.elements.getSecondRestrictionTypeText())
                .toBe('CatalogRestriction', 'Expected first restriction instance type to be "CatalogRestriction"');
            expect(restrictionsMenu.elements.getSecondRestrictionDescriptionText())
                .toBe('Restriction B Description', 'Expected first restriction description to be "Restriction B Description"');
        });

        xit('WHEN the user opens the restrictions menu for a variation page THEN the ALL restriction criteria label is displayed', function() {
            // WHEN
            restrictionsMenu.actions.openRestrictionsMenuForSecondPage();

            // THEN
            expect(restrictionsMenu.elements.getRestrictionCriteriaLabelText())
                .toContain('ALL', 'Expected restriction criteria to be "ALL"');
        });
    });

});
