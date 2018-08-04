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
describe('deletePageService', function() {

    // spies
    var deletePageService,
        injected,
        mocked;

    // mocked data
    var MOCKED_URI_CONTEXT = {
        CURRENT_CONTEXT_CATALOG: "MOCKED_CURRENT_CONTEXT_CATALOG",
        CURRENT_CONTEXT_CATALOG_VERSION: "MOCKED_CURRENT_CONTEXT_CATALOG_VERSION",
        CURRENT_CONTEXT_SITE_ID: "MOCKED_CURRENT_SITE_ID"
    };

    var MOCKED_PAGE_INFO = {
        name: "MOCKED_PAGE_NAME",
        uid: "MOCKED_PAGE_UID",
        uuid: "MOCKED_PAGE_UUID"
    };
    var MOCKED_ALERT_PARAMETERS = {
        message: "se.cms.actionitem.page.trash.alert.success.description",
        messagePlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        }
    };
    var MOCKED_PAGE_UID = "MOCKED_PAGE_UID";
    var MOCKED_RESPONSE = "MOCKED_RESPONSE";
    var MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT = {
        description: "se.cms.actionitem.page.trash.confirmation.description.storefront",
        descriptionPlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        title: "se.cms.actionitem.page.trash.confirmation.title"
    };
    var MOCKED_CONFIRMATION_PAYLOAD_PAGELIST = {
        description: "se.cms.actionitem.page.trash.confirmation.description.pagelist",
        descriptionPlaceholders: {
            pageName: MOCKED_PAGE_INFO.name
        },
        title: "se.cms.actionitem.page.trash.confirmation.title"
    };

    var MOCKED_ROUTE_PARAMS = {
        siteId: 'someSiteId',
        catalogId: 'someCatalogId',
        catalogVersion: 'someCatalogVersion'
    };

    var MOCKED_EVENTS = {
        PAGE_DELETED: 'PAGE_DELETED_EVENT'
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('deletePageServiceModule')
            .mock('alertService', 'showSuccess')
            .mock('cmsitemsRestService', 'update')
            .mock('crossFrameEventService', 'publish')
            .mock('pageInfoService', 'getPageUUID')
            .mock('confirmationModalService', 'confirm')
            .mock('pagesVariationsRestService', 'getVariationsForPrimaryPageId')
            .mock('pagesVariationsRestService', 'getVariationsForPrimaryPageId')
            .mock('$routeParams', MOCKED_ROUTE_PARAMS)
            .mock('TRASHED_PAGE_LIST_PATH', 'TRASHED_PAGE_LIST_PATH')
            .mockConstant('EVENTS', MOCKED_EVENTS)
            .service('deletePageService');
        deletePageService = harness.service;
        injected = harness.injected;
        mocked = harness.mocks;
    });

    describe('deletePage', function() {

        beforeEach(function() {
            mocked.pageInfoService.getPageUUID.and.returnValue(injected.$q.when('homepage'));
        });

        it("does not trigger cmsItem update when confirmation is cancelled", function() {

            // Given
            mocked.confirmationModalService.confirm.and.returnValue(injected.$q.reject());

            // When
            deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
            injected.$rootScope.$digest();

            // Assert
            expect(mocked.confirmationModalService.confirm).toHaveBeenCalledWith(MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT);
            expect(mocked.cmsitemsRestService.update).not.toHaveBeenCalled();

            var rejectedPromise = deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
            injected.$rootScope.$digest();
            expect(rejectedPromise).toBeRejected();

        });

        it("does not trigger alertService when cmsItem update is failing", function() {

            // Given
            mocked.confirmationModalService.confirm.and.returnValue(injected.$q.resolve());
            mocked.cmsitemsRestService.update.and.returnValue(injected.$q.reject());

            // When
            deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
            injected.$rootScope.$digest();

            // Assert
            expect(mocked.confirmationModalService.confirm).toHaveBeenCalledWith(MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT);
            expect(MOCKED_PAGE_INFO.identifier).toBe(MOCKED_PAGE_INFO.uuid);
            expect(MOCKED_PAGE_INFO.pageStatus).toBe("DELETED");
            expect(mocked.cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO);
            expect(mocked.alertService.showSuccess).not.toHaveBeenCalled();
            expect(deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT)).toBeRejected();

        });

        it("displays alert when cmsItem update is successful", function() {

            // Given
            mocked.confirmationModalService.confirm.and.returnValue(injected.$q.resolve());
            mocked.cmsitemsRestService.update.and.returnValue(injected.$q.resolve());
            mocked.alertService.showSuccess.and.returnValue(injected.$q.resolve(MOCKED_RESPONSE));

            // When
            deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
            injected.$rootScope.$digest();

            // Assert
            expect(mocked.confirmationModalService.confirm).toHaveBeenCalledWith(MOCKED_CONFIRMATION_PAYLOAD_STOREFRONT);
            expect(MOCKED_PAGE_INFO.identifier).toBe(MOCKED_PAGE_INFO.uuid);
            expect(MOCKED_PAGE_INFO.pageStatus).toBe("DELETED");
            expect(mocked.cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO);
            expect(mocked.alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMETERS);
            expect(mocked.crossFrameEventService.publish).toHaveBeenCalledWith(MOCKED_EVENTS.PAGE_DELETED);
            expect(deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT)).toBeResolved();

        });

        it("displays specific confirmation text when triggered from storefront", function() {

            // Given
            mocked.pageInfoService.getPageUUID.and.returnValue(injected.$q.reject({
                name: "InvalidStorefrontPageError",
            }));

            mocked.confirmationModalService.confirm.and.returnValue(injected.$q.resolve());
            mocked.cmsitemsRestService.update.and.returnValue(injected.$q.resolve());
            mocked.alertService.showSuccess.and.returnValue(injected.$q.resolve(MOCKED_RESPONSE));

            // When
            deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT);
            injected.$rootScope.$digest();

            // Assert
            expect(mocked.confirmationModalService.confirm).toHaveBeenCalledWith(MOCKED_CONFIRMATION_PAYLOAD_PAGELIST);
            expect(MOCKED_PAGE_INFO.identifier).toBe(MOCKED_PAGE_INFO.uuid);
            expect(MOCKED_PAGE_INFO.pageStatus).toBe("DELETED");
            expect(mocked.cmsitemsRestService.update).toHaveBeenCalledWith(MOCKED_PAGE_INFO);
            expect(mocked.alertService.showSuccess).toHaveBeenCalledWith(MOCKED_ALERT_PARAMETERS);
            expect(deletePageService.deletePage(MOCKED_PAGE_INFO, MOCKED_URI_CONTEXT)).toBeResolved();

        });

    });

    describe('isDeletePageEnabled', function() {

        it("returns false when at least one variation page is associated to the indicated pageUid", function() {

            // Given
            mocked.pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(injected.$q.when(['MOCKED_VARIATION_PAGE_UID']));

            // When
            deletePageService.isDeletePageEnabled(MOCKED_PAGE_UID);

            // Assert
            expect(mocked.pagesVariationsRestService.getVariationsForPrimaryPageId).toHaveBeenCalledWith(MOCKED_PAGE_UID);
            expect(deletePageService.isDeletePageEnabled()).toBeResolvedWithData(false);

        });

        it("returns true when no variation pages are associated to the indicated pageUid", function() {

            // Given
            mocked.pagesVariationsRestService.getVariationsForPrimaryPageId.and.returnValue(injected.$q.when([]));

            // When
            deletePageService.isDeletePageEnabled(MOCKED_PAGE_UID);

            // Assert
            expect(mocked.pagesVariationsRestService.getVariationsForPrimaryPageId).toHaveBeenCalledWith(MOCKED_PAGE_UID);
            expect(deletePageService.isDeletePageEnabled(MOCKED_PAGE_UID)).toBeResolvedWithData(true);

        });

    });

});
