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
module.exports = (function() {

    var leftToolbarObject = {

        actions: {

            openLeftToolbar: function() {
                return browser.click(leftToolbarObject.elements.getLeftToolbarOpenButton());
            },

            waitForNonPresenceOfLeftToolbar: function() {
                return browser.waitForAbsence('.se-left-nav');
            },

            clickSitesButton: function() {
                return browser.click(leftToolbarObject.elements.getSitesButton());
            },

            closeLeftToolbar: function() {
                return browser.click(leftToolbarObject.elements.getLeftToolbarCloseButton());
            }

        },

        assertions: {

            isLeftToolbarDisplayed: function() {
                return element(by.css('.se-left-nav')).isDisplayed();
            },

            isLeftToolbarPresent: function() {
                leftToolbarObject.actions.waitForNonPresenceOfLeftToolbar();
                return browser.isPresent(by.id('.se-left-nav'));
            },

            localiedFieldIsTranslated: function(by, expectedText) {
                browser.waitUntil(function() {
                    return element(by).getText().then(function(actualText) {
                        return actualText === expectedText;
                    });
                });
            }

        },

        constants: {},

        elements: {

            leftToolbarFirstLevelMenu: function() {
                return element(by.id('hamburger-menu-level1'));
            },

            leftToolbarSecondLevelMenu: function() {
                return element(by.id('hamburger-menu-level2'));
            },

            getLeftToolbarOpenButton: function() {
                return element(by.id('nav-expander'));
            },

            getLeftToolbarCloseButton: function() {
                //return element(by.css('hyicon hyicon-close > se-left-nav__section__close--icon'));
                return element(by.xpath("//span[contains(@class, 'hyicon-close')]"));
            },

            getSitesButton: function() {
                return element(by.css('.se-left-nav a[data-ng-click="showSites()"]'));
            },

        }

    };

    return leftToolbarObject;

})();
