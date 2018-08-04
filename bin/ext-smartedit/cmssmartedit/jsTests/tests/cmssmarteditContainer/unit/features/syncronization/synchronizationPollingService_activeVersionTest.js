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
describe("Synchronization polling service with content catalog active version - ", function() {

    var harness, syncPollingService, $q, mockRest, $rootScope;
    var mockTimer, systemEventServiceMock, pageInfoServiceMock, catalogServiceMock, SYNCHRONIZATION_POLLING;

    var synchronizationMockData = unit.mockData.synchronization;
    var pageId1_SyncStatus = new synchronizationMockData().PAGE_ID1_SYNC_STATUS;
    var pageId2_SyncStatus = new synchronizationMockData().PAGE_ID2_SYNC_STATUS;

    beforeEach(function() {

        mockRest = jasmine.createSpyObj('pageSynchronizationGetRestService', ['get']);
        mockTimer = jasmine.createSpyObj('Timer', ['start', 'restart', 'stop', 'isActive']);

        harness = AngularUnitTestHelper.prepareModule('synchronizationPollingServiceModule')
            .mockConstant('OVERLAY_RERENDERED_EVENT', 'mockedOverlayRerenderedEvent')
            .mockConstant('EVENTS', {
                EXPERIENCE_UPDATE: 'EXPERIENCE_UPDATE'
            })
            .mock('gatewayProxy', 'initForService')
            .mock('timerService', 'createTimer').and.returnValue(mockTimer)
            .mock('catalogService', 'isContentCatalogVersionNonActive').and.returnResolvedPromise(false)
            .mock('catalogService', 'getContentCatalogActiveVersion').and.returnResolvedPromise('Online')
            .mock('crossFrameEventService', 'publish')
            .mock('crossFrameEventService', 'subscribe')
            .mock('pageInfoService', 'getPageUUID')
            .mock('systemEventService', 'registerEventHandler')
            .mock('synchronizationResource', 'getPageSynchronizationGetRestService').and.returnValue(mockRest)
            .service('syncPollingService');

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        syncPollingService = harness.service;
        systemEventServiceMock = harness.mocks.systemEventService;
        pageInfoServiceMock = harness.mocks.pageInfoService;
        catalogServiceMock = harness.mocks.catalogService;
    });

    beforeEach(inject(function(_SYNCHRONIZATION_POLLING_) {
        SYNCHRONIZATION_POLLING = _SYNCHRONIZATION_POLLING_;
    }));

    it('getSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', function() {

        //GIVEN
        syncPollingService.syncStatus = pageId2_SyncStatus;
        pageInfoServiceMock.getPageUUID.and.returnValue($q.when('pageId1'));
        mockRest.get.and.returnValue($q.when(pageId1_SyncStatus));
        //WHEN
        var promise = syncPollingService.getSyncStatus('pageId1');

        $rootScope.$digest();

        //THEN
        expect(promise).toBeRejected();
        expect(mockRest.get).not.toHaveBeenCalled();
        expect(syncPollingService.syncStatus).toBe(pageId2_SyncStatus);

    });


    it('fetchSyncStatus will reject, not proceed to rest call and leave the syncStatus unchanged', function() {

        //GIVEN
        syncPollingService.syncStatus = pageId2_SyncStatus;
        pageInfoServiceMock.getPageUUID.and.returnValue($q.when('pageId1'));
        mockRest.get.and.returnValue($q.when(pageId1_SyncStatus));

        //WHEN
        var promise = syncPollingService._fetchSyncStatus();

        //THEN
        expect(promise).toBeRejected();
        expect(mockRest.get).not.toHaveBeenCalled();
        expect(syncPollingService.syncStatus).toEqual(pageId2_SyncStatus);

    });

    it('startSync call without pollingType should restart the timer with SLOW_POLLING_TIME by default', function() {
        mockTimer.isActive.and.returnValue(false);

        syncPollingService.startSync();

        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_POLLING.SLOW_POLLING_TIME);
    });

    it('startSync call with SYNCHRONIZATION_POLLING.SPEED_UP should restart the timer with FAST_FETCH', function() {
        mockTimer.isActive.and.returnValue(false);

        syncPollingService.startSync(SYNCHRONIZATION_POLLING.SPEED_UP);

        expect(mockTimer.restart).toHaveBeenCalledWith(SYNCHRONIZATION_POLLING.FAST_POLLING_TIME);
    });

    it('startSync call should not restart the timer if it\'s active', function() {
        mockTimer.isActive.and.returnValue(true);

        syncPollingService.startSync();

        expect(mockTimer.restart).not.toHaveBeenCalled();
    });

    it('stopSync should stop the timer when it\'s active', function() {
        mockTimer.isActive.and.returnValue(true);

        syncPollingService.stopSync();

        expect(mockTimer.stop).toHaveBeenCalled();
    });

    it('stopSync should not stop the timer when it\'s not active', function() {
        mockTimer.isActive.and.returnValue(false);

        syncPollingService.stopSync();

        expect(mockTimer.stop).not.toHaveBeenCalled();
    });


});
