angular.module('personalizationsmarteditShowComponentInfoListModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditRestServiceModule',
        'personalizationsmarteditDataUtils',
        'personalizationsmarteditComponentHandlerServiceModule'
    ])
    .controller('personalizationsmarteditShowComponentInfoListController', function(personalizationsmarteditContextService, personalizationsmarteditUtils, personalizationsmarteditRestService, personalizationsmarteditMessageHandler, $filter, PaginationHelper, personalizationsmarteditComponentHandlerService) {
        var self = this;
        //Methods
        this.isCustomizationFromCurrentCatalog = function(customization) {
            if (customization) {
                return personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, personalizationsmarteditContextService.getSeData());
            }
            return false;
        };

        this.customizationVisible = function() {
            if (self.actions) {
                return !self.isContainerIdEmpty && self.actions.length > 0;
            }
            return false;
        };

        var getCustomizationsFilterObject = function() {
            return {
                currentSize: self.initPageSize,
                currentPage: self.pagination.page + 1
            };
        };

        var getAllActionsAffectingContainerId = function(containerId, filter) {
            personalizationsmarteditRestService.getCxCmsAllActionsForContainer(containerId, filter)
                .then(function successCallback(response) {
                    self.actions = self.actions || {};
                    var results = response.actions || {};
                    results.forEach(function(result) {
                        result.customization = {};
                        result.customization.catalog = result.actionCatalog;
                        result.customization.catalogVersion = result.actionCatalogVersion;
                        personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(result.customization);
                    });
                    Array.prototype.push.apply(self.actions, results);
                    self.pagination = new PaginationHelper(response.pagination);
                    self.moreCustomizationsRequestProcessing = false;
                }, function errorCallback() {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingactions'));
                    self.moreCustomizationsRequestProcessing = false;
                });
        };

        this.addMoreItems = function() {
            if ((self.pagination.page < self.pagination.totalPages - 1) && !self.moreCustomizationsRequestProcessing && !self.isContainerIdEmpty) {
                self.moreCustomizationsRequestProcessing = true;
                getAllActionsAffectingContainerId(self.containerSourceId, getCustomizationsFilterObject());
            }
        };

        //Lifecycle methods
        this.$onInit = function() {
            self.initPageSize = 25;
            self.moreCustomizationsRequestProcessing = false;
            self.isContainerIdEmpty = !self.component.containerId;
            self.pagination = new PaginationHelper();
            self.pagination.reset();
            self.containerSourceId = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId(self.component.containerId);
        };
    })
    .component('personalizationsmarteditShowComponentInfoList', {
        templateUrl: 'personalizationsmarteditShowComponentInfoListTemplate.html',
        controller: 'personalizationsmarteditShowComponentInfoListController',
        controllerAs: 'ctrl',
        transclude: false,
        bindings: {
            component: '<'
        }
    });
