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
describe('ClonePageItemController', function() {

    // tested controller
    var ClonePageItemController;

    var injected,
        mocked;

    beforeEach(function() {


        var harness = AngularUnitTestHelper.prepareModule('clonePageItemModule')
            .mock('modalWizard', 'open')
            .mock('clonePageWizardService', 'openClonePageWizard')
            .controller('ClonePageItemController', {
                pageInfo: {}
            });


        injected = harness.injected;
        mocked = harness.mocks;
        ClonePageItemController = harness.controller;

    });

    it("calls clonePageWizardService to display a 'clone page' wizard", function() {

        // When
        ClonePageItemController.onClickOnClone();
        injected.$rootScope.$digest();

        // Assert
        expect(mocked.clonePageWizardService.openClonePageWizard).toHaveBeenCalledWith(ClonePageItemController.pageInfo);

    });

});
