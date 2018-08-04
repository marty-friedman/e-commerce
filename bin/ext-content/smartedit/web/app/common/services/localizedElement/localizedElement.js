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
angular.module('localizedElementModule', ['tabsetModule', 'seConstantsModule'])
    .controller('localizedElementController', function($scope, $attrs, VALIDATION_MESSAGE_TYPES) {

        this.$onChanges = function(changes) {
            if (changes.field) {

                this.model = {
                    field: this.field,
                    component: this.ge.editor.component[this.field.qualifier]
                };
                this.languages = this.ge.editor.languages;

                if (this.field) {
                    var inputTemplate = this.inputTemplate ? this.inputTemplate : $attrs.inputTemplate;
                    this.tabs = this.tabs || []; //keep the same tabs reference
                    this.tabs.length = 0;
                    Array.prototype.push.apply(this.tabs, this.languages.map(function(language) {
                        return {
                            id: language.isocode,
                            title: language.isocode.toUpperCase() + (language.required ? "*" : ""),
                            templateUrl: inputTemplate
                        };
                    }));
                }
            }
        };

        var previousMessages;

        this.$doCheck = function() {

            if (this.field.messages !== previousMessages) {
                previousMessages = this.field.messages;
                var messageMap = this.field.messages ? this.field.messages.filter(function(messsage) {
                    return messsage.type === VALIDATION_MESSAGE_TYPES.VALIDATION_ERROR;
                }).reduce(function(holder, next) {
                    holder[next.language] = true;
                    return holder;
                }, {}) : {};

                this.tabs.forEach(function(tab) {
                    var message = messageMap[tab.id];
                    tab.hasErrors = message !== undefined ? message : false;
                });
            }
        };
    })
    .component('localizedElement', {
        templateUrl: 'localizedElementTemplate.html',
        transclude: false,
        require: {
            ge: '^genericEditor'
        },
        controller: 'localizedElementController',
        controllerAs: 'le',
        bindings: {
            field: '<',
            inputTemplate: '<'
        }
    });
