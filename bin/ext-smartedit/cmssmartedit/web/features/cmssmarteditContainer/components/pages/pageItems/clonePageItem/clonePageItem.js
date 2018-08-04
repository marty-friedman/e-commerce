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
angular.module("clonePageItemModule", [
        'clonePageWizardServiceModule'
    ])

    .controller('ClonePageItemController', function(
        clonePageWizardService
    ) {

        this.onClickOnClone = function() {
            clonePageWizardService.openClonePageWizard(this.pageInfo);
        };

    })

    /**
     * @ngdoc directive
     * @name clonePageItemModule.directive:clonePageItem
     * @scope
     * @restrict E
     *
     * @description
     * clonePageItem builds an item allowing for the cloning of a given CMS
     * page.
     *
     * @param {<Object} pageInfo An object defining the context of the CMS
     * page associated to the clonePage item.
     */
    .component(
        'clonePageItem', {
            bindings: {
                pageInfo: '<'
            },
            controller: 'ClonePageItemController',
            templateUrl: 'clonePageItemTemplate.html'
        }
    );
