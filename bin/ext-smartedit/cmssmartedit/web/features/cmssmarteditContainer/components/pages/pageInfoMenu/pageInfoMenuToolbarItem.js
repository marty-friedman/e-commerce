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
 * @name pageInfoMenuModule
 * @description
 *
 * The page info menu module contains the directive and controller necessary to view the page information menu from the white ribbon..
 *
 * Use the {@link pageInfoMenuModule.directive:pageInfoMenuToolbarItem pageInfoMenuToolbarItem} add this page info toolbar menu.
 *
 */
angular.module('pageInfoMenuModule', ['pageInfoContainerModule'])
    /**
     * @ngdoc directive
     * @name pageInfoMenuModule.directive:pageInfoMenuToolbarItem
     * @scope
     * @restrict E
     * @element page-info-menu-toolbar-item
     *
     * @description
     * Component responsible for displaying the current page's meta data.
     *
     * The component also allows access to the page editor modal.
     *
     */
    .component('pageInfoMenuToolbarItem', {
        templateUrl: 'pageInfoMenuToolbarItemTemplate.html'
    });
