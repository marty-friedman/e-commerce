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
module.exports = function() {

    var pageObject = {};

    var selectors = {
        getPageInfoMenuToolbarItemCssSelector: function() {
            return '[data-item-key="se.cms.pageInfoMenu"]';
        },
        getPageInfoMenuButtonCssSelector: function() {
            return selectors.getPageInfoMenuToolbarItemCssSelector() + ' button.toolbar-action--button';
        },
        getPageInfoMenuButtonSelector: function() {
            return by.css(selectors.getPageInfoMenuButtonCssSelector());
        }
    };

    pageObject.elements = {
        getPageType: function() {
            return browser.findElement('.page-type-code', true);
        },
        getPageTemplate: function() {
            return browser.findElement('.page-template', true);
        },
        getPageInfoMenuToolbarItem: function() {
            return element(by.css(selectors.getPageInfoMenuToolbarItemCssSelector()));
        },
        getEditButton: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('.ySEPageInfoStaticInfoContainer button'));
        },
        getPageNameField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#name-shortstring'));
        },
        getPageLabelField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#label-shortstring'));
        },
        getPageUidField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#uid-shortstring'));
        },
        getPageTitleField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('[data-tab-id="en"] #title-shortstring'));
        },
        getPageCreationTimeField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#creationtime input[disabled]'));
        },
        getPageModifiedTimeField: function() {
            return this.getPageInfoMenuToolbarItem().element(by.css('#modifiedtime input[disabled]'));
        },
        getPageEditorModal: function() {
            return browser.findElement('.modal-dialog', true);
        }
    };

    pageObject.actions = {

        openPageInfoMenu: function() {
            browser.switchToParent();
            return browser.click(selectors.getPageInfoMenuButtonSelector());
        },
        clickEditButton: function() {
            return browser.click(pageObject.elements.getEditButton());
        }
    };

    pageObject.assertions = {

        uidIs: function(expectedUid) {
            browser.switchToParent();
            return browser.wait(function() {
                return pageObject.elements.getPageUidField().getAttribute('value').then(function(actualUid) {
                    return actualUid === expectedUid;
                });
            }, browser.explicitWait, "Expected uid to be " + expectedUid);
        }
    };


    return pageObject;

}();
