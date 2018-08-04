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
angular.module("deletePageItemModule", [
        "deletePageServiceModule",
        "eventServiceModule",
        "seConstantsModule"
    ])

    .controller('DeletePageItemController', function(
        deletePageService,
        systemEventService,
        EVENT_CONTENT_CATALOG_UPDATE
    ) {

        this.$onChanges = function() {
            if (this.pageInfo) {
                deletePageService.isDeletePageEnabled(this.pageInfo.uid).then(function(isEnabled) {
                    this.isDeletePageEnabled = isEnabled;
                }.bind(this));
            }
        };

        this.onClickOnDeletePage = function() {

            return deletePageService.deletePage(this.pageInfo, this.pageInfo.uriContext).then(function(response) {
                systemEventService.sendEvent(EVENT_CONTENT_CATALOG_UPDATE, response);
            });
        };

    })

    /**
     * @ngdoc directive
     * @name deletePageItemModule.directive:deletePageItem
     * @scope
     * @restrict E
     *
     * @description
     * deletePageItem builds a dropdown item allowing for the soft
     * deletion of a given CMS page .
     *
     * @param {<Object} pageInfo An object defining the context of the
     * CMS page associated to the deletePage item.
     */
    .component(
        'deletePageItem', {
            bindings: {
                pageInfo: '<'
            },
            controller: 'DeletePageItemController',
            templateUrl: 'deletePageItemTemplate.html'
        }
    );
