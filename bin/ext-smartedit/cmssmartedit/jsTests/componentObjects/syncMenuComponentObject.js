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
    var obj = {};

    obj.constants = {
        BUTTON_SELECTOR: '[data-item-key="se.cms.pageSyncMenu"] > button'
    };

    obj.elements = {
        getPanel: function() {
            return element(by.css("synchronization-panel"));
        },
        getPanelHeader: function() {
            return element(by.css(".se-sync-panel-header__text > span"));
        },
        getSyncCautionIcon: function() {
            return element(by.css(".se-toolbar-menu-ddlb--button__caution"));
        }
    };

    obj.actions = {
        click: function() {
            return browser.switchToParent().then(function() {
                browser.click(obj.constants.BUTTON_SELECTOR);
                browser.click(obj.constants.BUTTON_SELECTOR); //Open and close dropdown to ensure the menu is made available
                return browser.click(obj.constants.BUTTON_SELECTOR);
            });
        }
    };

    obj.assertions = {
        syncCautionIconIsDisplayed: function() {
            expect(browser.isPresent(obj.elements.getSyncCautionIcon())).toBe(true);
        },
        syncCautionIconIsHidden: function() {
            expect(browser.isAbsent(obj.elements.getSyncCautionIcon())).toBe(true);
        },
        syncPanelIsPresent: function() {
            expect(browser.isPresent(obj.elements.getPanel())).toBe(true);
        },
        syncPanelHeaderContains: function(text) {
            expect(obj.elements.getPanelHeader().getText()).toEqual(text);
        }
    };

    return obj;
}());
