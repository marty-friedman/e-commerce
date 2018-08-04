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
 * @name seGenericEditorFieldMessagesModule
 *
 * @description
 * This module provides the seGenericEditorFieldMessages component, which is used to show validation messages like errors or warnings.
 */
angular.module('seGenericEditorFieldMessagesModule', ['seConstantsModule'])
    .controller('seGenericEditorFieldMessagesController', function(VALIDATION_MESSAGE_TYPES) {

        var previousMessages = null;

        this.getFilteredMessagesByType = function(messageType) {
            return (this.field.messages || []).filter(function(validationMessage) {
                return validationMessage.marker === this.qualifier && !validationMessage.format && validationMessage.type === messageType;
            }.bind(this)).map(function(validationMessage) {
                return validationMessage.message;
            });
        };

        this.$doCheck = function() {
            if (this.field) {
                var currentMessages = angular.toJson(this.field.messages);
                if (previousMessages !== currentMessages) {
                    previousMessages = currentMessages;
                    this.errors = this.getFilteredMessagesByType(VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR);
                    this.warnings = this.getFilteredMessagesByType(VALIDATION_MESSAGE_TYPES.WARNING);
                }
            }
        };
    })
    /**
     * @ngdoc directive
     * @name seGenericEditorFieldMessagesModule.component:seGenericEditorFieldMessages
     * @element se-generic-editor-field-messages
     *
     * @description
     * Component responsible for displaying validation messages like errors or warnings
     * @param {< Object} field The field object that contains array of messages.
     * @param {< String} qualifier For a non-localized field, it is the actual field.qualifier. For a localized field, it is the ISO code of the language.
     */
    .component('seGenericEditorFieldMessages', {
        templateUrl: 'seGenericEditorFieldMessagesTemplate.html',
        controller: 'seGenericEditorFieldMessagesController',
        controllerAs: 'ctrl',
        bindings: {
            field: '<',
            qualifier: '<'
        }
    });
