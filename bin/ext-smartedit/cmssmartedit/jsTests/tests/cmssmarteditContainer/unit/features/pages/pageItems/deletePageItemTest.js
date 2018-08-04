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
describe('DeletePageItemController', function() {

    // tested controller
    var $q,
        DeletePageItemController,
        scope,
        spy_deletePageService,
        spy_systemEventService;

    // mocked data
    var mocked_event_name = "EVENT_CONTENT_CATALOG_UPDATE",
        mocked_response = "MOCKED_RESPONSE",
        mocked_pageUid = "MOCKED_SELECTED_ITEM_UID",
        mocked_pageInfo = {
            uid: mocked_pageUid,
            uriContext: 'someUriContext'
        };

    beforeEach(function() {

        module('deletePageItemModule', function($provide) {
            spy_deletePageService = jasmine.createSpyObj('deletePageService', ['isDeletePageEnabled', 'deletePage']);
            $provide.value('deletePageService', spy_deletePageService);
            spy_systemEventService = jasmine.createSpyObj('systemEventService', ['sendEvent']);
            $provide.value('systemEventService', spy_systemEventService);
            $provide.value('EVENT_CONTENT_CATALOG_UPDATE', mocked_event_name);
        });

        inject(function($rootScope, $componentController, _$q_) {
            $q = _$q_;
            scope = $rootScope.$new();
            DeletePageItemController = $componentController(
                'deletePageItem', {
                    $scope: scope
                }, {
                    pageInfo: mocked_pageInfo
                }
            );
        });

    });

    describe('$onChanges', function() {
        it('sets "isDeletePageEnabled" with result returned by deletePageService for a given pageUid', function() {

            // Given
            spy_deletePageService.isDeletePageEnabled.and.returnValue($q.resolve(mocked_response));

            // When
            DeletePageItemController.$onChanges();
            scope.$digest();

            // Assert
            expect(spy_deletePageService.isDeletePageEnabled).toHaveBeenCalledWith(mocked_pageUid);
            expect(DeletePageItemController.isDeletePageEnabled).toBe(mocked_response);

        });
    });

    describe('onClickOnDeletePage', function() {
        it('sends an event when the soft deletion of a page is resolved', function() {

            // Given
            spy_deletePageService.deletePage.and.returnValue($q.resolve(mocked_response));

            // When
            DeletePageItemController.onClickOnDeletePage();
            scope.$digest();

            // Assert
            expect(spy_deletePageService.deletePage).toHaveBeenCalledWith(mocked_pageInfo, mocked_pageInfo.uriContext);
            expect(spy_systemEventService.sendEvent).toHaveBeenCalledWith(mocked_event_name, mocked_response);

        });
    });

});
