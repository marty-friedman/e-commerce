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
angular.module("syncPageItemModule", [
        "syncPageModalServiceModule"
    ])

    .controller('SyncPageItemController', function(
        syncPageModalService
    ) {

        this.onClickOnSync = function() {
            syncPageModalService.open(this.pageInfo);
        };

    })

    /**
     * @ngdoc directive
     * @name syncPageItemModule.directive:editPageDropdownItem
     * @scope
     * @restrict E
     *
     * @description
     * syncPageItem builds a drop-down item allowing for the
     * edition of a given CMS page .
     *
     * @param {<Object} pageInfo An object defining the context of the
     * CMS page associated to the editPage item.
     */
    .component(
        'syncPageItem', {
            bindings: {
                pageInfo: '<'
            },
            controller: 'SyncPageItemController',
            templateUrl: 'syncPageItemTemplate.html'
        }
    );
