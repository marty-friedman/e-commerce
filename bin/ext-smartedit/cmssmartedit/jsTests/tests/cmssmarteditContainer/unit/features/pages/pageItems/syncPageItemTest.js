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
describe('SyncPageItemController', function() {

    var injected,
        mocked,
        SyncPageItemController;

    beforeEach(function() {

        // FIXME: why do we need ot mock all these services?
        var harness = AngularUnitTestHelper.prepareModule('syncPageItemModule')
            .mock('EVENTS', 'PAGE_CHANGE')
            .mock('OVERLAY_RERENDERED_EVENT')
            .mock('gatewayProxy', 'initForService')
            .mock('pageInfoService', 'getPageUUID')
            .mock('timerService', 'createTimer')
            .mock('crossFrameEventService', 'publish')
            .mock('crossFrameEventService', 'subscribe')
            .mock('systemEventService', 'registerEventHandler')
            .mock('synchronizationResource', 'getPageSynchronizationGetRestService')
            .mock('synchronizationResource', 'getPageSynchronizationPostRestService')
            .mock('catalogService', 'getContentCatalogActiveVersion')
            .mock('catalogService', 'isContentCatalogVersionNonActive')
            .mock('syncPageModalService', 'open')
            .controller('SyncPageItemController', {
                pageInfo: {
                    name: "MOCKED_PAGE_INFO"
                },
                uriContext: {
                    name: "MOCKED_URI_CONTEXT"
                }
            });

        injected = harness.injected;
        mocked = harness.mocks;
        SyncPageItemController = harness.controller;

    });

    it("calls syncPageModalService to open a 'sync page' modal", function() {

        // When
        SyncPageItemController.onClickOnSync();
        injected.$rootScope.$digest();

        // Assert
        expect(mocked.syncPageModalService.open).toHaveBeenCalledWith(SyncPageItemController.pageInfo);

    });

});
