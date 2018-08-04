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
 * @name navigationNodeEditorModalServiceModule
 * @description
 * # The navigationNodeEditorModalServiceModule
 *
 * The navigation node editor modal service module provides a service that allows opening an editor modal for a given navigation node or an entry. The editor modal is populated with a save and cancel button, and is loaded with the 
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given navigation node.
 */
angular.module('navigationNodeEditorModalServiceModule', ['modalServiceModule', 'confirmationModalServiceModule'])
    /**
     * @ngdoc service
     * @name navigationNodeEditorModalServiceModule.service:navigationNodeEditorModalService
     *
     * @description
     * Convenience service to open an editor modal window for a given navigation node's data.
     *
     */
    .factory('navigationNodeEditorModalService', function($q, $log, modalService, confirmationModalService, MODAL_BUTTON_STYLES, MODAL_BUTTON_ACTIONS) {

        function NavigationNodeEditorModalService() {}


        /**
         * @ngdoc method
         * @name navigationNodeEditorModalServiceModule.service:navigationNodeEditorModalService#open
         * @methodOf navigationNodeEditorModalServiceModule.service:navigationNodeEditorModalService
         *
         * @description
         * Uses the {@link genericEditorModalService.open genericEditorModalService} to open an editor modal.
         *
         * The editor modal is initialized with a title in the format '<TypeName> Editor', ie: 'Paragraph Editor'. The
         * editor modal is also wired with a save and cancel button.
         *
         * The content of the editor modal is the {@link editorTabsetModule.directive:editorTabset editorTabset}.
         *
         * @param {Object} data The data associated to a navigation node as defined in the platform.
         * @param {Object}  parameters the {@link resourceLocationsModule.object:UriContext UriContext} necessary to perform operations.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        NavigationNodeEditorModalService.prototype.openNodeEditor = function(target, uriContext) {
            return modalService.open({
                title: 'se.cms.navigationmanagement.node.edit.title',
                templateUrl: 'navigationNodeEditorModalTemplate.html',
                controller: ['$scope', 'modalManager', function($scope, modalManager) {
                    this.target = target;
                    this.uriContext = uriContext;

                    this.onCancel = function() {
                        if (this.isDirty()) {
                            return confirmationModalService.confirm({
                                description: 'se.editor.cancel.confirm'
                            }).then(function() {
                                return $q.when({});
                            }, function() {
                                return $q.reject();
                            });
                        } else {
                            return $q.when({});
                        }
                        return $q.when({});
                    };

                    this.init = function() {
                        modalManager.setDismissCallback(this.onCancel.bind(this));

                        modalManager.setButtonHandler(function(buttonId) {
                            switch (buttonId) {
                                case 'save':
                                    return this.submit().then(function() {
                                        modalManager.close();
                                    });
                                case 'cancel':
                                    return this.onCancel();
                                default:
                                    $log.error('A button callback has not been registered for button with id', buttonId);
                                    break;
                            }
                        }.bind(this));

                        $scope.$watch(function() {
                            var isDirty = typeof this.isDirty === 'function' && this.isDirty();
                            return isDirty;
                        }.bind(this), function(isDirty) {
                            if (typeof isDirty === 'boolean') {
                                if (isDirty) {
                                    modalManager.enableButton('save');
                                } else {
                                    modalManager.disableButton('save');
                                }
                            }
                        }.bind(this));

                    };
                }],
                buttons: [{
                    id: 'cancel',
                    label: 'se.cms.navigationmanagement.node.edit.cancel',
                    style: MODAL_BUTTON_STYLES.SECONDARY,
                    action: MODAL_BUTTON_ACTIONS.DISMISS
                }, {
                    id: 'save',
                    label: 'se.cms.navigationmanagement.node.edit.save',
                    action: MODAL_BUTTON_ACTIONS.NONE,
                    disabled: true
                }]
            });
        };

        return new NavigationNodeEditorModalService();
    });
