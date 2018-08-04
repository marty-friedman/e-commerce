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
/**
 * @ngdoc overview
 * @name deletePageServiceModule
 * @description
 * # The deletePageServiceModule
 *
 * The delete page service module provides the functionality allowing for
 * the soft deletion of a given CMS page.
 */
angular.module('deletePageServiceModule', [
        "alertServiceModule",
        "cmsitemsRestServiceModule",
        "confirmationModalServiceModule",
        "crossFrameEventServiceModule",
        "pagesVariationsRestServiceModule",
        "resourceLocationsModule",
        "smarteditServicesModule"
    ])

    /**
     * @ngdoc service
     * @name deletePageServiceModule.service:deletePageService
     *
     * @description
     * The delete page service provides the functionality necessary to handle
     * the soft deletion of a CMS page.
     */
    .service('deletePageService', function(
        $location,
        $log,
        $q,
        $translate,
        alertService,
        cmsitemsRestService,
        crossFrameEventService,
        pageInfoService,
        confirmationModalService,
        pagesVariationsRestService,
        EVENTS
    ) {

        this._getConfirmationModalDescription = function() {
            var deferred = $q.defer();
            pageInfoService.getPageUUID().then(function(pageUUID) {
                if (pageUUID) {
                    deferred.resolve("se.cms.actionitem.page.trash.confirmation.description.storefront");
                } else {
                    $log.error('deletePageService::deletePage - pageUUID is undefined');
                    deferred.reject();
                }
            }, function() {
                deferred.resolve("se.cms.actionitem.page.trash.confirmation.description.pagelist");
            });
            return deferred.promise;
        };

        /**
         * @ngdoc method
         * @name deletePageServiceModule.service:deletePageService#deletePage
         * @methodOf deletePageServiceModule.service:deletePageService
         *
         * @description
         * This method triggers the soft deletion of a CMS page.
         *
         * @param {Object} pageInfo The page object containing the uuid and the name of the page to be deleted.
         * @param {Object} uriContext A {@link resourceLocationsModule.object:UriContext uriContext}
         */
        this.deletePage = function(pageInfo, _uriContext) {

            var uriContext = {
                catalogId: _uriContext.CURRENT_CONTEXT_CATALOG,
                catalogVersion: _uriContext.CURRENT_CONTEXT_CATALOG_VERSION,
                siteId: _uriContext.CURRENT_CONTEXT_SITE_ID
            };

            return this._getConfirmationModalDescription().then(function(confirmationModalDescription) {
                return confirmationModalService.confirm({
                    description: confirmationModalDescription,
                    descriptionPlaceholders: {
                        pageName: pageInfo.name
                    },
                    title: "se.cms.actionitem.page.trash.confirmation.title"
                }).then(

                    // resolved (confirmationModalService.confirm)
                    function() {

                        pageInfo.identifier = pageInfo.uuid;
                        pageInfo.pageStatus = "DELETED";

                        return cmsitemsRestService.update(
                            pageInfo
                        ).then(function() {
                            crossFrameEventService.publish(EVENTS.PAGE_DELETED);
                            return alertService.showSuccess({
                                message: "se.cms.actionitem.page.trash.alert.success.description",
                                messagePlaceholders: {
                                    pageName: pageInfo.name
                                }
                            });
                        }).then(function() {
                            $location.path("/pages/:siteId/:catalogId/:catalogVersion"
                                .replace(":siteId", uriContext.siteId)
                                .replace(":catalogId", uriContext.catalogId)
                                .replace(":catalogVersion", uriContext.catalogVersion));
                        }, function(response) {
                            response.data.errors.filter(function(error) {
                                return error.type === 'ValidationError';
                            }).forEach(function(error) {
                                var alertMessage = $translate.instant('se.cms.actionitem.page.trash.alert.failure.prefix', {
                                    pageName: pageInfo.name
                                }) + error.subject + " - " + error.message;

                                alertService.showDanger({
                                    message: alertMessage,
                                    timeout: 5000
                                });
                            });
                            return $q.reject(response);
                        });
                    },

                    // rejected (confirmationModalService.confirm)
                    function() {
                        return $q.reject("Error");
                    });
            });
        };

        /**
         * @ngdoc method
         * @name deletePageServiceModule.service:deletePageService#isDeletePageEnabled
         * @methodOf deletePageServiceModule.service:deletePageService
         *
         * @description
         * This method indicates whether the given page can be soft deleted.
         * Only the variation pages and the  primary pages associated with no
         * variation pages are eligible for soft deletion.
         *
         * @param {String} pageUid The unique page identifier for the page to be
         * soft deleted.
         *
         * @returns {Promise} A promise resolved with a boolean indicating
         * whether the selected page can be soft deleted.
         *
         */
        this.isDeletePageEnabled = function(pageUid) {
            return pagesVariationsRestService.getVariationsForPrimaryPageId(pageUid).then(function(variationPagesUids) {
                return (variationPagesUids.length === 0);
            });
        };

    });
