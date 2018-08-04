angular.module('personalizationsmarteditPromotionModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditCommerceCustomizationModule',
        'personalizationpromotionssmarteditRestServiceModule'
    ])
    .run(function(personalizationsmarteditCommerceCustomizationService, $filter) {
        personalizationsmarteditCommerceCustomizationService.registerType({
            type: 'cxPromotionActionData',
            text: 'personalization.modal.commercecustomization.action.type.promotion',
            template: 'personalizationsmarteditPromotionsTemplate.html',
            confProperty: 'personalizationsmartedit.commercecustomization.promotions.enabled',
            getName: function(action) {
                return $filter('translate')('personalization.modal.commercecustomization.promotion.display.name') + " - " + action.promotionId;
            }
        });
    })
    .controller('personalizationsmarteditPromotionController', function($q, $scope, $filter, personalizationsmarteditRestService, personalizationpromotionssmarteditRestService, personalizationsmarteditMessageHandler) {

        $scope.promotion = null;
        $scope.availablePromotions = [];

        var getPromotions = function() {
            var deferred = $q.defer();

            personalizationsmarteditRestService.getPreviewTicket().then(function successCallback(previewTicket) {
                var catalogsVersions = previewTicket.catalogVersions;
                personalizationpromotionssmarteditRestService.getPromotions(catalogsVersions).then(
                    function successCallback(response) {
                        deferred.resolve(response);
                    },
                    function errorCallback(response) {
                        deferred.reject(response);
                    }
                );
            }, function errorCallback(response) {
                deferred.reject(response);
            });

            return deferred.promise;
        };

        var getAvailablePromotions = function() {
            getPromotions()
                .then(function successCallback(response) {
                    $scope.availablePromotions = response.promotions;
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingpromotions'));
                });

        };

        var buildAction = function(item) {
            return {
                type: 'cxPromotionActionData',
                promotionId: item.code
            };
        };

        var comparer = function(a1, a2) {
            return a1.type === a2.type && a1.promotionId === a2.promotionId;
        };

        $scope.promotionSelected = function(item, uiSelectObject) {
            var action = buildAction(item);
            $scope.addAction(action, comparer);
            uiSelectObject.selected = null;
        };

        $scope.isItemInSelectDisabled = function(item) {
            var action = buildAction(item);
            return $scope.isItemInSelectedActions(action, comparer);
        };

        $scope.initUiSelect = function(uiSelectController) {
            uiSelectController.isActive = function() {
                return false;
            };

            //workaround of existing ui-select issue - remove after upgrade of ui-select library on smartedit side
            $scope.availablePromotions = JSON.parse(JSON.stringify($scope.availablePromotions));
        };

        (function init() {
            getAvailablePromotions();
        })();
    });
