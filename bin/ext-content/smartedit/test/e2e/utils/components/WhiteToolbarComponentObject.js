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

    var ToolbarObject = {

        actions: {
            clickShowHybridActionToolbarContext: function() {
                browser.click(element(by.id('showHibridActionToolbarContext')));
                return browser.waitForPresence(element(by.id('showHibridActionToolbarContext')));
            },
            clickHideHybridActionToolbarContext: function() {
                browser.click(element(by.id('hideHibridActionToolbarContext')));
                return browser.waitForAbsence(element(by.id('hideHibridActionToolbarContext')));
            }
        },

        assertions: {
            assertButtonPresent: function(title) {
                return browser.waitForPresence(ToolbarObject.elements.getButtonByTitle(title));
            },
            assertButtonNotPresent: function(title) {
                return browser.waitForAbsence(ToolbarObject.elements.getButtonByTitle(title));
            }
        },

        constants: {},

        elements: {
            experienceSelectorToolbar: function() {
                return element(by.css('.ySmartEditExperienceSelectorToolbar'));
            },
            getButtonByTitle: function(title) {
                browser.switchToParent();
                return ToolbarObject.elements.experienceSelectorToolbar().element(by.cssContainingText('button', title));
            },
            renderButton: function() {
                return this.getButtonByTitle('Render Component');
            },
            renderSlotButton: function() {
                return this.getButtonByTitle('Render Slot');
            },
            getToolbarItemContextByKey: function(key) {
                return element(by.id('toolbar_item_context_' + key + '_btn'));
            },
            getToolbarItemContextTextByKey: function(key) {
                return element(by.id('toolbar_item_context_' + key + '_btn')).getText();
            }
        }
    };

    return ToolbarObject;

})();
