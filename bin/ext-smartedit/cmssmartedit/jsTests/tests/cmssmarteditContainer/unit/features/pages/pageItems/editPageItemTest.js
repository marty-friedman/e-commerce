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
describe('EditPageItemController', function() {

    // tested controller
    var $q,
        scope,
        spy_catalogService,
        spy_systemEventService,
        spy_pageEditorModalService,
        EditPageItemController;

    // mocked data
    var mocked_event_name = "EVENT_CONTENT_CATALOG_UPDATE",
        mocked_pageInfo_uid = "MOCKED_PAGE_INGO_UID",
        mocked_pageInfo = {
            uid: mocked_pageInfo_uid
        },
        mocked_response = [{}],
        mocked_uri = "MOCKED_URI";

    beforeEach(function() {

        module('editPageItemModule', function($provide) {
            spy_catalogService = jasmine.createSpyObj('catalogService', ['retrieveUriContext']);
            $provide.value('catalogService', spy_catalogService);
            spy_pageEditorModalService = jasmine.createSpyObj('pageEditorModalService', ['open']);
            $provide.value('pageEditorModalService', spy_pageEditorModalService);
            spy_systemEventService = jasmine.createSpyObj('systemEventService', ['sendEvent']);
            $provide.value('systemEventService', spy_systemEventService);
            $provide.value('EVENT_CONTENT_CATALOG_UPDATE', mocked_event_name);
        });

        inject(function($rootScope, $componentController, _$q_) {
            $q = _$q_;
            scope = $rootScope.$new();
            EditPageItemController = $componentController(
                'editPageItem', {
                    $scope: scope
                }, {
                    pageInfo: mocked_pageInfo
                }
            );
        });

    });

    describe('$onChanges', function() {
        it('calls catalogService to retrieve and set uriContext', function() {

            // Given
            spy_catalogService.retrieveUriContext.and.returnValue($q.when(mocked_uri));

            // When
            EditPageItemController.$onChanges();
            scope.$digest();

            // Assert
            expect(spy_catalogService.retrieveUriContext).toHaveBeenCalled();
            expect(EditPageItemController.pageInfo.uriContext).toBe(mocked_uri);

        });
    });

    describe('onClickOnEdit', function() {
        it('sends an event when the page edition is resolved', function() {

            // Given
            spy_pageEditorModalService.open.and.returnValue($q.resolve(mocked_response));

            // When
            EditPageItemController.onClickOnEdit();
            scope.$digest();

            // Assert
            expect(spy_pageEditorModalService.open).toHaveBeenCalledWith(EditPageItemController.pageInfo);
            expect(spy_systemEventService.sendEvent).toHaveBeenCalledWith(mocked_event_name, mocked_response);

        });
    });

});
