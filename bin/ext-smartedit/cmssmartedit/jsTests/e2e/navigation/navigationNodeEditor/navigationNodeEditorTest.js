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
    var navigationNodeEditorModule = e2e.pageObjects.navigationNodeEditor;
    var navigationTree = e2e.componentObjects.navigationTree;
    var confirmationModal = e2e.componentObjects.confirmationModal;

    describe('Navigation Node Editor - ', function() {

        beforeEach(function() {

            browser.bootstrap(__dirname);

        });

        var navigationNodeEditor = new navigationNodeEditorModule();

        var EDIT = "Edit";
        var MOVE_UP = "Move Up";
        var MOVE_DOWN = "Move Down";
        var DELETE = "Delete";
        var ADD_A_SIBLING = "Add a Sibling";
        var ADD_A_CHILD = "Add a Child";

        var ENTRY_1 = 'ABC ENTRY';
        var ENTRY_2 = 'DEF ENTRY';
        var ENTRY_3 = 'GHI ENTRY';
        var ITEM_SUPER_TYPE = 'itemSuperType';
        var ITEM_ID = 'itemId';
        var TYPE_CMS_COMP = 'se.cms.abstractcmscomponent';
        var TYPE_CMS_PAGE = 'se.cms.abstractpage';
        var TYPE_CMS_MEDIA = 'se.cms.media';

        beforeEach(function(done) {
            return navigationTree.navigateToFirstCatalogNavigationEditor().then(function() {
                done();
            });
        });

        describe('Add Nodes- ', function() {

            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add New Top Level" THEN I expect to see a modal to add a node and the node is created when SAVE is clicked', function() {
                // GIVEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2']);

                // WHEN
                navigationTree.clickAddNewTopLevel();
                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                // THEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2', 'new node name']);
            });

            it('GIVEN I am on the navigation editor of the first catalog and adding a new top level WHEN when I add a new entity by choosing entry type and specific component/page/media THEN I expect the second dropdown to be initialized after any change of the entry type(first dropdown)', function() {
                navigationTree.clickAddNewTopLevel();
                navigationNodeEditor.editNodeName('new node name 123');
                navigationNodeEditor.editNodeTitle('new node title 123');

                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    //select page type
                    navigationNodeEditor.clickItemSuperTypeDropdown();
                    navigationNodeEditor.selectTypeOption(ITEM_SUPER_TYPE, TYPE_CMS_COMP);

                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.component.search');

                    navigationNodeEditor.clickItemIdDropdown();
                    navigationNodeEditor.sendKeys("HomepageNavLink");
                    navigationNodeEditor.selectOptionByLabel(ITEM_ID, "HomepageNavLink");

                    navigationNodeEditor.clickItemSuperTypeDropdown();
                    navigationNodeEditor.selectTypeOption(ITEM_SUPER_TYPE, TYPE_CMS_PAGE);

                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.page.search');

                    navigationNodeEditor.clickItemIdDropdown();
                    navigationNodeEditor.sendKeys("second");
                    navigationNodeEditor.selectOptionByLabel(ITEM_ID, "second", true);

                    navigationNodeEditor.clickItemSuperTypeDropdown();
                    navigationNodeEditor.selectTypeOption(ITEM_SUPER_TYPE, TYPE_CMS_MEDIA);

                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.media.search');

                });

            });


            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add New Top Level" THEN I expect to see a modal to add a node and the node is not created when CANCEL is clicked', function() {
                // GIVEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2']);

                // WHEN
                navigationTree.clickAddNewTopLevel();
                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickCancelButton();
                confirmationModal.actions.confirmConfirmationModal();

                // THEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2']);
            });

            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Sibling" on the top level nodes THEN I expect to see a modal to add a node and the node is not created on the top level', function() {
                // GIVEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2']);

                // WHEN
                navigationTree.clickMoreMenu('node1');
                navigationTree.clickMoreMenuItem('node1', ADD_A_SIBLING);
                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                // THEN
                navigationTree.assertNodeHasChildren(undefined, ['node1', 'node2', 'new node name']);
            });

            xit('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Sibling" on the second level nodes THEN I expect to see a modal to add a node and the node is not created on the second level', function() {

                navigationTree.expand('node1');
                navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7']);
                navigationTree.clickMoreMenu('node5');
                navigationTree.clickMoreMenuItem('node5', ADD_A_SIBLING);

                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                browser.click(navigationNodeEditor.getAddNewEntryButton()); //add new entry
                navigationNodeEditor.clickItemSuperTypeDropdown();
                navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                navigationNodeEditor.clickItemIdDropdown();
                navigationNodeEditor.sendKeys("HomepageNavLink");
                navigationNodeEditor.selectOptionByLabel(ITEM_ID, "HomepageNavLink");
                browser.click(navigationNodeEditor.getSaveEntryButton());
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7', 'new node name']);

                navigationTree.expand('new node name');
                navigationTree.assertNodeHasChildren('new node name', ['ABC Entry']);
            });

            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Child" on the top level node has children and is expanded THEN I expect to see a modal to add a node and the node is created under the parent', function() {

                navigationTree.expand('node1');
                navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7']);
                navigationTree.clickMoreMenu('node1');
                navigationTree.clickMoreMenuItem('node1', ADD_A_CHILD);

                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7', 'new node name']);
            });

            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Child" on a node has no children THEN I expect to see a modal to add a node and the node is created under the parent and the parent is expanded', function() {

                navigationTree.clickAddNewTopLevel();
                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.clickMoreMenu('new node name');
                navigationTree.clickMoreMenuItem('new node name', ADD_A_CHILD);

                navigationNodeEditor.editNodeName('new child node name');
                navigationNodeEditor.editNodeTitle('new child node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasChildren('new node name', ['new child node name']);

            });

            it('GIVEN I am on the navigation editor of the first catalog WHEN the I click on "Add a Child" on a node that is not expanded THEN I expect to see a modal to add a node and the node is created under the parent and the parent is expanded', function() {

                navigationTree.clickMoreMenu('node1');
                navigationTree.clickMoreMenuItem('node1', ADD_A_CHILD);

                navigationNodeEditor.editNodeName('new node name');
                navigationNodeEditor.editNodeTitle('new node title');
                navigationNodeEditor.clickSaveButton();

                navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7', 'new node name']); // assertion successful without expanding means node is expanded

            });


        });

        describe('Entry List- ', function() {

            beforeEach(function(done) {
                navigationTree.clickMoreMenu('node1').then(function() {
                    return navigationTree.clickMoreMenuItem('node1', EDIT);
                }).then(function() {
                    done();
                });
            });

            it('WHEN the page loads THEN it should load 3 entries in the list.', function() {
                navigationNodeEditor.assertNumberOfEntryElements(3);
            });

            it('WHEN the More Menu "Move Down" button is clicked, THEN the first entry should be moved down.', function() {
                navigationNodeEditor.clickMoreMenuOptionByIndex(0, MOVE_DOWN).then(function() {
                    expect(navigationNodeEditor.getEntryTitle(0)).toBe(ENTRY_2);
                    expect(navigationNodeEditor.getEntryTitle(1)).toBe(ENTRY_1);
                });

            });

            it('WHEN the More Menu "Move Up" button is clicked on the second entry, THEN the first entry should be moved up.', function() {
                navigationNodeEditor.clickMoreMenuOptionByIndex(1, MOVE_UP).then(function() {
                    expect(navigationNodeEditor.getEntryTitle(0)).toBe(ENTRY_2);
                    expect(navigationNodeEditor.getEntryTitle(1)).toBe(ENTRY_1);
                });

            });

            it('WHEN the More Menu "Delete" button is clicked on the second entry, THEN entry is removed.', function() {
                navigationNodeEditor.clickMoreMenuOptionByIndex(1, DELETE).then(function() {
                    navigationNodeEditor.assertNumberOfEntryElements(2);
                });

            });
        });

        describe('Update Entries- ', function() {

            beforeEach(function(done) {
                navigationTree.clickMoreMenu('node1').then(function() {
                    return navigationTree.clickMoreMenuItem('node1', EDIT);
                }).then(function() {
                    done();
                });
            });

            xit('WHEN the More Menu "Edit" button is clicked, THEN the entry should be displayed on the right menu panel and can be modified.', function() {
                navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT).then(function() {
                    expect(navigationNodeEditor.getSaveEntryButton().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getCancelEntryButton().isPresent()).toBeTruthy();
                    return navigationNodeEditor.clickItemIdDropdown();
                }).then(function() {
                    return navigationNodeEditor.sendKeys("69SlamLink").then(function() {
                        return navigationNodeEditor.selectOptionByLabel(ITEM_ID, "69SlamLink");
                    });
                }).then(function() {
                    return navigationNodeEditor.saveNodeEntry();
                }).then(function() {
                    navigationNodeEditor.assertNumberOfEntryElements(3);
                    expect(navigationNodeEditor.getEntryTitle(0)).toBe('DEF ENTRY');
                });

            });

            it('WHEN the modal loads THEN Add New Button should be present and after click it should be hidden.', function() {
                expect(navigationNodeEditor.getAddNewEntryButton().isPresent()).toBeTruthy();
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    browser.waitForAbsence(navigationNodeEditor.getAddNewEntryButton());
                });

            });

            it('WHEN the modal loads THEN cancel and add button should be hidden and should be visible on click of Add New Button.', function() {
                browser.waitForAbsence(navigationNodeEditor.getSaveEntryButton());
                browser.waitForAbsence(navigationNodeEditor.getCancelEntryButton());
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    expect(navigationNodeEditor.getSaveEntryButton().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getCancelEntryButton().isPresent()).toBeTruthy();
                });

            });

            it('WHEN the user select the strategy for searching PAGE from the dropdown THEN the search result dropdown should have placeholder with PAGE search message.', function() {
                browser.waitForAbsence(navigationNodeEditor.getItemIdDropdown());
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 3);
                }).then(function() {
                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.page.search');
                });
            });


            it('WHEN the user select the strategy for searching COMPONENT from the dropdown THEN the search result dropdown should have placeholder with COMPONENT search message.', function() {
                browser.waitForAbsence(navigationNodeEditor.getItemIdDropdown());
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                }).then(function() {
                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.component.search');
                });


            });

            it('WHEN the user select the strategy for searching MEDIA from the dropdown THEN the search result dropdown should have placeholder with MEDIA search message.', function() {
                browser.waitForAbsence(navigationNodeEditor.getItemIdDropdown());
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 4);
                }).then(function() {
                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getEntrySearchDropdown().getAttribute('placeholder')).toContain('se.cms.navigationmanagement.navnode.node.entry.dropdown.media.search');
                });


            });

            //infinite scrolling for COMPONENTS
            it('WHEN the user select the strategy for searching COMPONENT from the dropdown THEN the search results will be a list of COMPONENTS and more results are fetch as we scroll', function() {
                browser.waitForAbsence(navigationNodeEditor.getItemIdDropdown());
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                }).then(function() {
                    return navigationNodeEditor.clickItemIdDropdown();

                }).then(function() {
                    expect(navigationNodeEditor.getItemIdDropdown().isPresent()).toBeTruthy();
                    expect(navigationNodeEditor.getItemIdDropdownOptions().count()).toBe(10);

                    browser.scrollToBottom(navigationNodeEditor.getItemIdScrollElement()).then(function() {
                        browser.waitUntil(function() {
                            return navigationNodeEditor.getItemIdDropdownOptions().then(function(options) {
                                return options.length > 10;
                            });
                        }, "couldnot load more items");
                    });
                });


            });

            //errors
            it('WHEN entries in the form has validation errors and SAVE button is clicked THEN the error prone entries must be highlighted', function() {
                navigationNodeEditor.editNodeName('entriesErrors').then(function() {
                    return navigationNodeEditor.clickSaveButton();
                }).then(function() {
                    expect(navigationNodeEditor.getErrorProneEntriesCount()).toBe(2);
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT);
                }).then(function() {
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE).getText()).toBe('this field has error of type 2.');
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 1.');
                });
            });

            it('WHEN entries in the form has validation errors and SAVE button is clicked THEN selecting the error prone entries should display the correct errors in edit mode', function() {
                navigationNodeEditor.editNodeName('entriesErrors').then(function() {
                    return navigationNodeEditor.clickSaveButton();
                }).then(function() {
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT);
                }).then(function() {
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE).getText()).toBe('this field has error of type 2.');
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 1.');
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(1, EDIT);
                }).then(function() {
                    browser.waitForAbsence(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE));
                    browser.waitForAbsence(navigationNodeEditor.getValidationErrorElements(ITEM_ID));
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(2, EDIT);
                }).then(function() {
                    browser.waitForAbsence(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE));
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 3.');
                });
            });

            it('WHEN entries in the form has validation errors and SAVE button is clicked when an entry is in edit mode THEN the errors for the entry being edited must be displayed (if any)', function() {
                navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT).then(function() {
                    return navigationNodeEditor.editNodeName('entriesErrors');
                }).then(function() {
                    return navigationNodeEditor.clickSaveButton();
                }).then(function() {
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE).getText()).toBe('this field has error of type 2.');
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 1.');
                });
            });

            it('WHEN entries in the form has validation errors and SAVE button is clicked after some modification THEN the entry that already has errors is not poluted with multiple errors', function() {
                navigationNodeEditor.editNodeName('entriesErrors').then(function() { //introduce errors
                    return navigationNodeEditor.clickSaveButton(); //click save
                }).then(function() {
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT); // edit first entry
                }).then(function() {
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE).getText()).toBe('this field has error of type 2.'); // expect validation errors
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 1.');
                    return browser.click(navigationNodeEditor.getCancelEntryButton());
                }).then(function() {
                    return browser.click(navigationNodeEditor.getAddNewEntryButton()); // add new entry
                }).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                }).then(function() {
                    return navigationNodeEditor.clickItemIdDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_ID, 2);
                }).then(function() {
                    return navigationNodeEditor.saveNodeEntry();
                }).then(function() {
                    return navigationNodeEditor.clickSaveButton(); //click save (errors still present)
                }).then(function() {
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(0, EDIT); // edit first entry
                }).then(function() {
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_SUPER_TYPE).getText()).toBe('this field has error of type 2.'); // expect validation errors
                    expect(navigationNodeEditor.getValidationErrorElements(ITEM_ID).getText()).toBe('this field has error of type 1.');
                });
            });

            //save and cancel
            xit('WHEN the user clicks SAVE with a valid data THEN Add new Entry button will be visible and the total number of entries will increase from 3 to 4', function() {
                navigationNodeEditor.assertNumberOfEntryElements(3);
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                }).then(function() {
                    return navigationNodeEditor.clickItemIdDropdown();
                }).then(function() {
                    return navigationNodeEditor.sendKeys("HomepageNavLink").then(function() {
                        return navigationNodeEditor.selectOptionByLabel(ITEM_ID, "HomepageNavLink");
                    });
                }).then(function() {
                    return navigationNodeEditor.saveNodeEntry();
                }).then(function() {
                    expect(navigationNodeEditor.getAddNewEntryButton().isPresent()).toBeTruthy();
                    navigationNodeEditor.assertNumberOfEntryElements(4);
                }).then(function() {
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(1, MOVE_DOWN);
                }).then(function() {
                    return navigationNodeEditor.clickMoreMenuOptionByIndex(0, DELETE);
                }).then(function() {
                    return navigationNodeEditor.clickSaveButton();
                }).then(function() {
                    navigationTree.expand('node1');
                    navigationTree.assertNodeHasChildren('node1', ['GHI Entry', 'DEF Entry', 'ABC Entry', 'node4', 'node5', 'node7']);
                });
            });

            it('WHEN the user click CANCEL in dirty state then the list of children is not updated in the navigation tree', function() {
                navigationNodeEditor.assertNumberOfEntryElements(3);
                browser.click(navigationNodeEditor.getAddNewEntryButton()).then(function() {
                    return navigationNodeEditor.clickItemSuperTypeDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_SUPER_TYPE, 2);
                }).then(function() {
                    return navigationNodeEditor.clickItemIdDropdown();
                }).then(function() {
                    return navigationNodeEditor.selectOption(ITEM_ID, 2);
                }).then(function() {
                    return navigationNodeEditor.saveNodeEntry();
                }).then(function() {
                    expect(navigationNodeEditor.getAddNewEntryButton().isPresent()).toBeTruthy();
                    navigationNodeEditor.assertNumberOfEntryElements(4);
                    return navigationNodeEditor.clickCancelButton();
                }).then(function() {
                    return confirmationModal.actions.confirmConfirmationModal(); // confirm cancel
                }).then(function() {
                    navigationTree.expand('node1');
                    navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7']);
                });
            });

        });

        describe('Edit and Update Entries- ', function() {
            it('WHEN I edit an entry and update it and click save, THEN the tree shows the updated entry', function() {
                navigationTree.expand('node1').then(function() {
                    navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'DEF Entry', 'GHI Entry', 'node4', 'node5', 'node7']);
                    return navigationTree.clickMoreMenu('DEF Entry');
                }).then(function() {
                    return navigationTree.clickMoreMenuItem('DEF Entry', EDIT);
                }).then(function() {
                    navigationNodeEditor.assertNumberOfEntryElements(3);
                    return navigationNodeEditor.clickItemIdDropdown();
                }).then(function() {
                    return navigationNodeEditor.sendKeys("AlMerrickLink").then(function() {
                        return navigationNodeEditor.selectOptionByLabel(ITEM_ID, "AlMerrickLink");
                    });
                }).then(function() {
                    return navigationNodeEditor.saveNodeEntry();
                }).then(function() {
                    expect(navigationNodeEditor.getAddNewEntryButton().isPresent()).toBeTruthy();
                    navigationNodeEditor.assertNumberOfEntryElements(3);
                    return navigationNodeEditor.clickSaveButton();
                }).then(function() {
                    navigationTree.assertNodeHasChildren('node1', ['ABC Entry', 'XYZ Entry', 'GHI Entry', 'node4', 'node5', 'node7']);
                });

            });

        });
    });
})();
