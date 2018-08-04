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
angular.module("editPageItemModule", [
        "catalogServiceModule",
        "eventServiceModule",
        "pageEditorModalServiceModule",
        "seConstantsModule"
    ])

    .controller('EditPageItemController', function(
        catalogService,
        pageEditorModalService,
        systemEventService,
        EVENT_CONTENT_CATALOG_UPDATE
    ) {

        this.$onChanges = function() {

            if (this.pageInfo) {
                // setting 'uriContext'
                catalogService.retrieveUriContext().then(function(uriContext) {
                    this.pageInfo.uriContext = uriContext;
                }.bind(this));
            }

        };

        this.onClickOnEdit = function() {
            return pageEditorModalService.open(this.pageInfo).then(function(response) {
                systemEventService.sendEvent(EVENT_CONTENT_CATALOG_UPDATE, response);
            });
        };

    })

    /**
     * @ngdoc directive
     * @name editPageItemModule.directive:editPageItem
     * @scope
     * @restrict E
     *
     * @description
     * editPageItem builds an action item allowing for the edition of a given
     * CMS page .
     *
     * @param {<Object} pageInfo An object defining the context of the
     * CMS page associated to the editPage item.
     */
    .component(
        'editPageItem', {
            bindings: {
                pageInfo: '<'
            },
            controller: 'EditPageItemController',
            templateUrl: 'editPageItemTemplate.html'
        }
    );
