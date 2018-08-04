angular.module('personalizationsmarteditCommons')
    .directive('personalizationsmarteditPagination', function() {
        return {
            templateUrl: 'personalizationsmarteditPaginationTemplate.html',
            restrict: 'E',
            scope: {
                callback: "=",
                pages: "=?",
                currentPage: "=?",
                pageSizes: "=?",
                currentSize: "=?",
                pagesOffset: "=?",
                fixedPageSize: "=?",
                incrementByOne: "=?"
            },

            link: function($scope) {

                if (!$scope.callback) {
                    console.error("callback is undefined!"); // tslint:disable-line
                }

                $scope.pages = $scope.pages || [0, 1, 2];
                $scope.currentPage = $scope.currentPage || 0;
                $scope.pageSizes = $scope.pageSizes || [5, 10, 25, 50, 100];
                $scope.currentSize = $scope.currentSize || 10;
                $scope.pagesOffset = $scope.pagesOffset || 1;
                $scope.fixedPageSize = $scope.fixedPageSize || false;
                $scope.incrementByOne = $scope.incrementByOne || false;

                $scope.pageClick = function(newValue) {
                    if ($scope.currentPage !== newValue) {
                        $scope.currentPage = newValue;
                        $scope.callback($scope);
                    }
                };

                $scope.pageSizeClick = function(newValue) {
                    if ($scope.currentSize !== newValue) {
                        $scope.currentSize = newValue;
                        $scope.currentPage = 0;
                        $scope.callback($scope);
                    }
                };

                $scope.hasPrevious = function() {
                    return $scope.currentPage > 0;
                };

                $scope.hasNext = function() {
                    return $scope.currentPage < $scope.pages.length - 1;
                };

                $scope.isActive = function(value) {
                    return $scope.currentPage === value;
                };

                $scope.rightClick = function() {
                    if ($scope.hasNext()) {
                        $scope.currentPage = $scope.incrementByOne ? $scope.currentPage + 1 : $scope.pages.length - 1;
                        $scope.callback($scope);
                    }
                };

                $scope.leftClick = function() {
                    if ($scope.hasPrevious()) {
                        $scope.currentPage = $scope.incrementByOne ? $scope.currentPage - 1 : 0;
                        $scope.callback($scope);
                    }
                };

                $scope.pagesToDisplay = function() {
                    var numberOfPages = 2 * $scope.pagesOffset + 1;
                    if ($scope.pages.length <= numberOfPages) {
                        return $scope.pages;
                    } else {
                        var start = Math.max($scope.currentPage - $scope.pagesOffset, 0);
                        if (start + numberOfPages > $scope.pages.length) {
                            start = $scope.pages.length - numberOfPages;
                        }
                        return $scope.pages.slice(start, start + numberOfPages);
                    }
                };

                $scope.availablePageSizes = function() {
                    return $scope.pageSizes;
                };

                $scope.getCurrentPageSize = function() {
                    return $scope.currentSize;
                };

                $scope.isFixedPageSize = function() {
                    return $scope.fixedPageSize;
                };

                $scope.showArrows = function() {
                    return $scope.pages.length > $scope.pagesOffset * 2 + 1;
                };

            }
        };
    });
