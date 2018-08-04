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
module.exports = {

    _getAnchorSelector: function(selector) {
        return by.css(selector + " .help-control");
    },
    _getAnchor: function(selector) {
        return element(this._getAnchorSelector(selector));
    },
    getPopover: function() {
        return element(by.css('.popover'));
    },
    hover: function(selector) {
        return browser.actions().mouseMove(this._getAnchor(selector)).perform();
    },
    click: function(selector) {
        return browser.click(this._getAnchorSelector(selector));
    },
    getTitleBox: function() {
        return element(by.css('.popover .popover-title'));
    },
    getTitleText: function() {
        return this.getTitleBox('.popover .popover-title').getText();
    },
    getBodyText: function() {
        return element(by.css('.popover .popover-content')).getText().then(function(text) {
            return text.replace(/\n|\r/g, " ");
        });
    }
};
