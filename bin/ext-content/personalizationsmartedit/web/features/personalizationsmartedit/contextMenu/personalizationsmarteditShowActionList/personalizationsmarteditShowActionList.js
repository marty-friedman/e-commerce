angular.module('personalizationsmarteditShowActionListModule', [
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule',
        'personalizationsmarteditComponentHandlerServiceModule'
    ])
    .controller('personalizationsmarteditShowActionListController', function(personalizationsmarteditContextService, personalizationsmarteditUtils, personalizationsmarteditComponentHandlerService) {
        var self = this;

        //Methods
        this.initItem = function(item) {
            item.visible = false;
            (item.variation.actions || []).forEach(function(elem) {
                if (elem.containerId && elem.containerId === self.containerSourceId) {
                    item.visible = true;
                }
            });
            personalizationsmarteditUtils.getAndSetCatalogVersionNameL10N(item.variation);
        };

        this.isCustomizationFromCurrentCatalog = function(customization) {
            return personalizationsmarteditUtils.isItemFromCurrentCatalog(customization, personalizationsmarteditContextService.getSeData());
        };

        //Lifecycle methods
        this.$onInit = function() {
            self.selectedItems = personalizationsmarteditContextService.getCombinedView().selectedItems;
            self.containerSourceId = personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId(self.component.containerId);
            self.getClassForElement = personalizationsmarteditUtils.getClassForElement;
            self.getLetterForElement = personalizationsmarteditUtils.getLetterForElement;
        };
    })
    .component('personalizationsmarteditShowActionList', {
        templateUrl: 'personalizationsmarteditShowActionListTemplate.html',
        controller: 'personalizationsmarteditShowActionListController',
        controllerAs: 'ctrl',
        transclude: false,
        bindings: {
            component: '<'
        }
    });
