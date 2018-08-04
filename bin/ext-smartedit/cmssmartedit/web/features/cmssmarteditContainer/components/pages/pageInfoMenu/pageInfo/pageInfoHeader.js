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
 * @name pageInfoHeaderModule
 * @description
 * This module contains pageInfoHeader component.
 */
angular.module('pageInfoHeaderModule', [])

    /**
     * @ngdoc directive
     * @name pageInfoHeaderModule.pageInfoHeader
     * @description
     * Directive that can render page template and type code.
     *
     * @param {String} pageTypeCode The type code of the page of which to render information
     * @param {String} pageTemplate The template of the page of which to render information
     */
    .component('pageInfoHeader', {
        templateUrl: 'pageInfoHeaderTemplate.html',
        bindings: {
            pageTypeCode: '<',
            pageTemplate: '<'
        }
    });
