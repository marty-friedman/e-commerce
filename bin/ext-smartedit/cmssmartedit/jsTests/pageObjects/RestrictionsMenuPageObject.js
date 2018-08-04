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
(function() {

    var perspective = require('../componentObjects/modeSelectorComponentObject.js');

    var restrictionsMenu = {};

    restrictionsMenu.elements = {
        getRestrictionsMenuToolbarItem: function() {
            return element(by.css('restrictions-menu-toolbar-item'));
        },
        getRestrictionsMenuButton: function() {
            return this.getRestrictionsMenuToolbarItem().element(by.css('button'));
        },
        getRestrictionsPageInfo: function() {
            return this.getRestrictionsMenuToolbarItem().element(by.css('restrictions-page-info'));
        },
        getRestrictionsMenuTable: function() {
            return this.getRestrictionsMenuToolbarItem().element(by.css('restrictions-table'));
        },
        getPageNameText: function() {
            return this.getRestrictionsPageInfo().element(by.css('.ySERestrictionsPageInfoContainer--page-name')).getText().then(function(text) {
                return text.trim();
            });
        },
        getPageDisplayConditionsText: function() {
            return this.getRestrictionsPageInfo().element(by.css('.ySERestrictionsPageInfoContainer--displayconditions-value')).getText().then(function(text) {
                return text.trim();
            });
        },
        getDisplayConditionsDescriptionText: function() {
            return this.getRestrictionsPageInfo().element(by.css('.ySERestrictionsPageInfoContainer--displayconditions-description')).getText().then(function(text) {
                return text.trim();
            });
        },
        getAssociatedPrimaryPageText: function() {
            return this.getRestrictionsPageInfo().element(by.css('.ySERestrictionsPageInfoContainer--associatedprimarypage-name')).getText().then(function(text) {
                return text.trim();
            });
        },
        getFirstRestrictionNameText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-1 .ySERestrictionsNameHeader')).getText().then(function(text) {
                return text.trim();
            });
        },
        getFirstRestrictionTypeText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-1 .ySERestrictionsTypeAndID')).getText().then(function(text) {
                return text.trim();
            });
        },
        getFirstRestrictionDescriptionText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-1 .ySERestrictionsDescription')).getText().then(function(text) {
                return text.trim();
            });
        },
        getSecondRestrictionNameText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-2 .ySERestrictionsNameHeader')).getText().then(function(text) {
                return text.trim();
            });
        },
        getSecondRestrictionTypeText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-2 .ySERestrictionsTypeAndID')).getText().then(function(text) {
                return text.trim();
            });
        },
        getSecondRestrictionDescriptionText: function() {
            return this.getRestrictionsMenuTable().element(by.css('#restriction-2 .ySERestrictionsDescription')).getText().then(function(text) {
                return text.trim();
            });
        },
        getRestrictionCriteriaLabelText: function() {
            return this.getRestrictionsMenuTable().element(by.css('.ySERestrictionsCriteriaLabel')).getText().then(function(text) {
                return text.trim();
            });
        }

    };

    restrictionsMenu.actions = {
        openHomePage: function() {
            browser.get('jsTests/tests/cmssmarteditContainer/e2e/features/restrictionsMenu/restrictionsTest.html');
            browser.waitForWholeAppToBeReady();
        },
        openSecondPage: function() {
            browser.switchToIFrame();
            browser.click(element(by.id('deepLink')), 'Timed out waiting for deep link to be clickable');
            browser.switchToParent();
            browser.waitForWholeAppToBeReady();
        },
        openRestrictionsMenuForHomepage: function() {
            this.openHomePage();
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
            restrictionsMenu.elements.getRestrictionsMenuButton().click();
        },
        openRestrictionsMenuForSecondPage: function() {
            this.openHomePage();
            this.openSecondPage();
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
            restrictionsMenu.elements.getRestrictionsMenuButton().click();
        }
    };

    module.exports = restrictionsMenu;

})();
