angular.module('personalizationsmarteditComponentHandlerServiceModule', ['smarteditServicesModule'])
    .constant('COMPONENT_CONTAINER_TYPE', 'CxCmsComponentContainer')
    .constant('CONTAINER_SOURCE_ID_ATTR', 'data-smartedit-container-source-id')
    .factory('personalizationsmarteditComponentHandlerService', function(componentHandlerService, CONTAINER_TYPE_ATTRIBUTE, COMPONENT_CONTAINER_TYPE, CONTAINER_ID_ATTRIBUTE, TYPE_ATTRIBUTE, CONTENT_SLOT_TYPE, CONTAINER_SOURCE_ID_ATTR) {

        var self = this;

        self.getParentContainerForComponent = function(component) {
            var parent = component.closest('[' + CONTAINER_TYPE_ATTRIBUTE + '=' + COMPONENT_CONTAINER_TYPE + ']');
            return parent;
        };

        self.getParentContainerIdForComponent = function(component) {
            var parent = component.closest('[' + CONTAINER_TYPE_ATTRIBUTE + '=' + COMPONENT_CONTAINER_TYPE + ']');
            return parent.attr(CONTAINER_ID_ATTRIBUTE);
        };

        self.getParentSlotForComponent = function(component) {
            var parent = component.closest('[' + TYPE_ATTRIBUTE + '=' + CONTENT_SLOT_TYPE + ']');
            return parent;
        };

        self.getParentSlotIdForComponent = function(component) {
            return componentHandlerService.getParentSlotForComponent(component);
        };

        self.getOriginalComponent = function(componentId, componentType) {
            return componentHandlerService.getOriginalComponent(componentId, componentType);
        };

        self.isExternalComponent = function(componentId, componentType) {
            return componentHandlerService.isExternalComponent(componentId, componentType);
        };

        self.getCatalogVersionUuid = function(component) {
            return componentHandlerService.getCatalogVersionUuid(component);
        };

        self.getAllSlotsSelector = function() {
            return componentHandlerService.getAllSlotsSelector();
        };

        self.getFromSelector = function(selector) {
            return componentHandlerService.getFromSelector(selector);
        };

        self.getContainerSourceIdForContainerId = function(containerId) {
            var containerSelector = self.getAllSlotsSelector();
            containerSelector += ' [' + CONTAINER_ID_ATTRIBUTE + '="' + containerId + '"]'; // space at beginning is important
            var container = self.getFromSelector(containerSelector);
            return container[0] ? container[0].getAttribute(CONTAINER_SOURCE_ID_ATTR) : "";
        };

        return self;
    });
