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
describe('Page sync toolbar menu test - ', function() {

    var modeSelector = e2e.componentObjects.modeSelector;
    var syncMenu = e2e.componentObjects.syncMenu;
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;
    var sfBuilder = e2e.se.componentObjects.sfBuilder;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function(done) {
        browser.waitForWholeAppToBeReady().then(function() {
            modeSelector.selectAdvancedPerspective().then(function() {
                synchronizationPanel.setupTest();
                done();
            });
        });
    });

    it('GIVEN advanced edit mode is on, WHEN we click on sync toolbar entry THEN a sync panel shows along with a sync button', function() {
        //find and click on sync toolbar entry
        syncMenu.actions.click();
        //assess that sync panel shows
        syncMenu.assertions.syncPanelIsPresent();
        //assess that it is related to pages
        syncMenu.assertions.syncPanelHeaderContains("Synchronize page information, associations and content slots, except shared content slots");
        //assess the sync icon is truly present
        syncMenu.assertions.syncCautionIconIsDisplayed();
        //check the page's checkbox to select all items to sync
        synchronizationPanel.checkItem('All Slots and Page Information');
        //assess that a sync button is clickable
        synchronizationPanel.assertions.assertSyncButtonIsEnabled();
    });

    it('GIVEN the page that has not been sync with target catalog version WHEN we click on sync toolbar entry THEN a sync panel shows without item list and with a enabled sync button', function() {
        // GIVEN
        sfBuilder.actions.changePageIdAndCatalogVersion('secondpage', 'apparel-ukContentCatalog/Staged');

        // WHEN
        syncMenu.actions.click();

        // THEN
        syncMenu.assertions.syncPanelIsPresent();
        syncMenu.assertions.syncPanelHeaderContains("Sync it so that it is available in the Apparel UK Content Catalog - Online");
        syncMenu.assertions.syncCautionIconIsDisplayed();
        synchronizationPanel.assertions.assertItemListIsHidden();
        synchronizationPanel.assertions.assertSyncButtonIsEnabled();
    });

    it('GIVEN the page that has not been sync with target catalog version WHEN we click on sync toolbar entry THEN a sync panel shows without item list and with a enabled sync button', function() {
        // GIVEN
        sfBuilder.actions.changePageIdAndCatalogVersion('otherpage', 'apparel-ukContentCatalog/Staged');

        // WHEN
        syncMenu.actions.click();

        // THEN
        syncMenu.assertions.syncPanelIsPresent();
        syncMenu.assertions.syncPanelHeaderContains("se.cms.synchronization.page.unavailable.items.description");
        syncMenu.assertions.syncCautionIconIsDisplayed();
        synchronizationPanel.assertions.assertItemListIsHidden();
        synchronizationPanel.assertions.assertSyncButtonIsDisabled();
    });

    it('GIVEN the page that has not been sync with the target catalog version AND a sync panel is without item list AND with a enabled sync button WHEN we click on sync button THEN item list is shown and sync button is disabled', function() {
        // GIVEN
        sfBuilder.actions.changePageIdAndCatalogVersion('secondpage', 'apparel-ukContentCatalog/Staged');
        syncMenu.actions.click();
        syncMenu.assertions.syncPanelIsPresent();
        // WHEN
        synchronizationPanel.clickSync();
        // THEN
        syncMenu.assertions.syncCautionIconIsHidden();
        synchronizationPanel.assertions.assertItemListIsVisible();
        synchronizationPanel.assertions.assertSyncButtonIsDisabled();
    });

});
