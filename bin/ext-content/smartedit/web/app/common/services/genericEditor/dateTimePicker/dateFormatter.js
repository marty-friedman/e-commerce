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
 * @name dateFormatterModule
 * @description
 * # The dateFormatterModule
 *
 * The date formatter module is a module for displaying the date in the desired format.
 *
 */
angular.module('dateFormatterModule', ['seConstantsModule'])
    /**
     * @ngdoc directive
     * @name dateFormatterModule.directive:dateFormatter
     * @description
     * # The dateTimePicker
     * You can pass the desired format in the attributes of this directive and it will be shown. 
     * It is  used with the <input> tag as we cant use date filter with it.
     * for eg- <input type='text'  data-date-formatter  format-type="short">
     * format-type can be short, medium etc.
     * If the format-type is not given in the directive template, by default it uses the short type
     */
    .directive('dateFormatter', function(dateFilter, DATE_CONSTANTS) {
        var defaultFormatType = DATE_CONSTANTS.ANGULAR_FORMAT;
        return {
            require: 'ngModel',
            restrict: 'A',
            scope: {
                formatType: '@'
            },
            link: function(scope, element, attrs, ctrl) {
                ctrl.$parsers.push(function(data) {
                    return dateFilter(data, (scope.formatType || defaultFormatType));
                });

                ctrl.$formatters.push(function(data) {
                    return dateFilter(data, (scope.formatType || defaultFormatType));
                });
            }
        };
    });
