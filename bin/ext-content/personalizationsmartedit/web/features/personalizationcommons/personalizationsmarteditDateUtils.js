angular.module('personalizationsmarteditCommons')
    .constant('PERSONALIZATION_DATE_FORMATS', {
        SHORT_DATE_FORMAT: 'M/D/YY',
        MODEL_DATE_FORMAT: 'YYYY-MM-DDTHH:mm:SSZ'
    })
    .factory('personalizationsmarteditDateUtils', function($filter, DATE_CONSTANTS, isBlank, PERSONALIZATION_DATE_FORMATS) {
        var utils = {};

        utils.formatDate = function(dateStr, format) {
            format = format || DATE_CONSTANTS.MOMENT_FORMAT;
            if (dateStr) {
                if (dateStr.match && dateStr.match(/^(\d{4})\-(\d{2})\-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\+|\-)(\d{4})$/)) {
                    dateStr = dateStr.slice(0, -2) + ":" + dateStr.slice(-2);
                }
                return moment(new Date(dateStr)).format(format);
            } else {
                return "";
            }
        };

        utils.formatDateWithMessage = function(dateStr, format) {
            format = format || PERSONALIZATION_DATE_FORMATS.SHORT_DATE_FORMAT;
            if (dateStr) {
                return utils.formatDate(dateStr, format);
            } else {
                return $filter('translate')('personalization.toolbar.pagecustomizations.nodatespecified');
            }
        };

        utils.isDateInThePast = function(modelValue) {
            if (isBlank(modelValue)) {
                return false;
            } else {
                return moment(modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isBefore();
            }
        };

        utils.isDateValidOrEmpty = function(modelValue) {
            return isBlank(modelValue) || moment(modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isValid();
        };

        utils.isDateRangeValid = function(startDate, endDate) {
            if (isBlank(startDate) || isBlank(endDate)) {
                return true;
            } else {
                return moment(new Date(startDate)).isSameOrBefore(moment(new Date(endDate)));
            }
        };

        utils.isDateStrFormatValid = function(dateStr, format) {
            format = format || DATE_CONSTANTS.MOMENT_FORMAT;
            if (isBlank(dateStr)) {
                return false;
            } else {
                return moment(dateStr, format, true).isValid();
            }
        };

        return utils;
    }).directive('dateTimePickerRange', function($timeout, languageService, personalizationsmarteditDateUtils, DATE_CONSTANTS) {
        return {
            templateUrl: 'dateTimePickerRangeTemplate.html',
            restrict: 'E',
            transclude: true,
            replace: false,
            scope: {
                name: '=',
                dateFrom: '=',
                dateTo: '=',
                isEditable: '=',
                dateFormat: '='
            },
            link: function($scope, elem) {
                $scope.placeholderText = 'personalization.commons.datetimepicker.placeholder';

                $scope.isFromDateValid = false;
                $scope.isToDateValid = false;

                $scope.isEndDateInThePast = false;

                if ($scope.isEditable) {

                    $scope.getDateOrDefault = function(date) {
                        try {
                            return moment(new Date(date));
                        } catch (err) {
                            return false;
                        }
                    };

                    $scope.getMinToDate = function(date) {
                        if (!personalizationsmarteditDateUtils.isDateInThePast(date)) {
                            return $scope.getDateOrDefault(date);
                        } else {
                            return new moment();
                        }
                    };

                    getFromPickerNode()
                        .datetimepicker({
                            format: DATE_CONSTANTS.MOMENT_FORMAT,
                            showClear: true,
                            showClose: true,
                            useCurrent: false,
                            keepInvalid: true,
                            locale: languageService.getBrowserLocale().split('-')[0]
                        }).on('dp.change', function(e) {
                            var dateFrom = personalizationsmarteditDateUtils.formatDate(e.date);
                            if (personalizationsmarteditDateUtils.isDateValidOrEmpty(dateFrom) &&
                                personalizationsmarteditDateUtils.isDateValidOrEmpty($scope.dateTo) &&
                                !personalizationsmarteditDateUtils.isDateRangeValid(dateFrom, $scope.dateTo)) {
                                dateFrom = angular.copy($scope.dateTo);
                            }
                            $scope.dateFrom = dateFrom;
                        });

                    getToPickerNode()
                        .datetimepicker({
                            format: DATE_CONSTANTS.MOMENT_FORMAT,
                            showClear: true,
                            showClose: true,
                            useCurrent: false,
                            keepInvalid: true,
                            locale: languageService.getBrowserLocale().split('-')[0]
                        }).on('dp.change', function(e) {
                            var dateTo = personalizationsmarteditDateUtils.formatDate(e.date);
                            if (personalizationsmarteditDateUtils.isDateValidOrEmpty(dateTo) &&
                                personalizationsmarteditDateUtils.isDateValidOrEmpty($scope.dateFrom) &&
                                !personalizationsmarteditDateUtils.isDateRangeValid($scope.dateFrom, dateTo)) {
                                dateTo = angular.copy($scope.dateFrom);
                            }
                            $scope.dateTo = dateTo;
                        });

                    $scope.$watch('dateFrom', function() {
                        $scope.isFromDateValid = personalizationsmarteditDateUtils.isDateValidOrEmpty($scope.dateFrom);
                        if (personalizationsmarteditDateUtils.isDateStrFormatValid($scope.dateFrom, DATE_CONSTANTS.MOMENT_FORMAT)) {
                            getToDatetimepicker().minDate($scope.getMinToDate($scope.dateFrom));
                        } else {
                            getToDatetimepicker().minDate(new moment());
                        }
                    }, true);

                    $scope.$watch('dateTo', function() {
                        var dateToValid = personalizationsmarteditDateUtils.isDateValidOrEmpty($scope.dateTo);
                        if (dateToValid) {
                            $scope.isToDateValid = true;
                            $scope.isEndDateInThePast = personalizationsmarteditDateUtils.isDateInThePast($scope.dateTo);
                        } else {
                            $scope.isToDateValid = false;
                            $scope.isEndDateInThePast = false;
                        }
                        if (personalizationsmarteditDateUtils.isDateStrFormatValid($scope.dateTo, DATE_CONSTANTS.MOMENT_FORMAT)) {
                            getFromDatetimepicker().maxDate($scope.getDateOrDefault($scope.dateTo));
                        }
                    }, true);
                }

                function getFromPickerNode() {
                    return elem.querySelectorAll('#date-picker-range-from');
                }

                function getFromDatetimepicker() {
                    return getFromPickerNode().datetimepicker().data("DateTimePicker");
                }

                function getToPickerNode() {
                    return elem.querySelectorAll('#date-picker-range-to');
                }

                function getToDatetimepicker() {
                    return getToPickerNode().datetimepicker().data("DateTimePicker");
                }
            }
        };
    }).directive('isdatevalidorempty', ['isBlank', 'personalizationsmarteditDateUtils', function(isBlank, personalizationsmarteditDateUtils) {
        return {
            restrict: "A",
            require: "ngModel",
            scope: false,
            link: function(scope, element, attributes, ctrl) {
                ctrl.$validators.isdatevalidorempty = function(modelValue) {
                    return personalizationsmarteditDateUtils.isDateValidOrEmpty(modelValue);
                };
            }
        };
    }]);
