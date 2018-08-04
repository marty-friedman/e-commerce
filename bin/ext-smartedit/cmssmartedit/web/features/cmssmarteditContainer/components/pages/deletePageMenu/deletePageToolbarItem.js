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
angular.module("deletePageToolbarItemModule", [
        "pageServiceModule",
        "deletePageServiceModule",
        "eventServiceModule",
        "seConstantsModule",
        "pageFacadeModule"
    ])

    .controller('DeletePageToolbarItemController', function(
        pageService,
        pageFacade,
        deletePageService,
        systemEventService,
        EVENT_CONTENT_CATALOG_UPDATE
    ) {

        this.$onChanges = function() {

            this.ready = false;

            pageFacade.retrievePageUriContext().then(function(pageUriContext) {
                this.uriContext = pageUriContext;
                pageService.getCurrentPageInfo().then(function(pageInfo) {
                    this.pageInfo = pageInfo;
                    deletePageService.isDeletePageEnabled(this.pageInfo.uid).then(function(isEnabled) {
                        this.isDeletePageEnabled = isEnabled;
                        this.ready = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));

        };

        this.onClickOnDeletePage = function() {
            return pageService.getCurrentPageInfo().then(function(pageInfo) {
                return deletePageService.deletePage(pageInfo, this.uriContext).then(function(response) {
                    systemEventService.sendEvent(EVENT_CONTENT_CATALOG_UPDATE, response);
                }.bind(this));
            }.bind(this));
        };

    })

    /**
     * @ngdoc directive
     * @name deletePageItemModule.directive:deletePageToolbarItem
     * @scope
     * @restrict E
     *
     * @description
     * deletePageToolbarItem provides a tooolbar action to Move a page to trash.
     *
     * @param {<Object} toolbarItem An object that represents the toolbar item.
     */
    .component(
        'deletePageToolbarItem', {
            controller: 'DeletePageToolbarItemController',
            templateUrl: 'deletePageToolbarItemTemplate.html',
            bindings: {
                toolbarItem: '<'
            }
        }
    );
