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
 * @name pageEditorModalServiceModule
 * @description
 * # The pageEditorModalServiceModule
 *
 * The page editor modal service module provides a service that allows opening an editor modal for a given page. The editor modal is populated with a save and cancel button, and is loaded with the
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given page.
 */
angular.module('pageEditorModalServiceModule', [
        'genericEditorModalServiceModule',
        'pageServiceModule',
        'crossFrameEventServiceModule',
        'synchronizationConstantsModule',
        'contextAwarePageStructureServiceModule',
        'cmsitemsRestServiceModule'
    ])

    /**
     * @ngdoc service
     * @name pageEditorModalServiceModule.service:pageEditorModalService
     *
     * @description
     * Convenience service to open an editor modal window for a given page's data.
     *
     */
    .factory('pageEditorModalService', function($q, genericEditorModalService, crossFrameEventService, contextAwarePageStructureService, cmsitemsRestService, pageService, SYNCHRONIZATION_POLLING) {

        function PageEditorModalService() {}

        /**
         * @ngdoc method
         * @name pageEditorModalServiceModule.service:pageEditorModalService#open
         * @methodOf pageEditorModalServiceModule.service:pageEditorModalService
         *
         * @description
         * Uses the {@link genericEditorModalService.open genericEditorModalService} to open an editor modal.
         *
         * The editor modal is initialized with a title in the format '<TypeName> Editor', ie: 'Paragraph Editor'. The
         * editor modal is also wired with a save and cancel button.
         *
         * The content of the editor modal is the {@link editorTabsetModule.directive:editorTabset editorTabset}.
         *
         * @param {Object} page The data associated to a page as defined in the platform.
         *
         * @returns {Promise} A promise that resolves to the data returned by the modal when it is closed.
         */
        PageEditorModalService.prototype.open = function(page) {

            var editorModalConfiguration = {
                title: 'se.cms.pageeditormodal.editpagetab.title',
                componentId: page.uid,
                componentUuid: page.uuid,
                componentType: page.typeCode
            };

            var pagePromise = cmsitemsRestService.getById(page.uuid);
            var isPagePrimaryPromise = pageService.isPagePrimary(page.uid);

            return $q.all([pagePromise, isPagePrimaryPromise]).then(function(values) {
                editorModalConfiguration.content = values[0];

                return cmsitemsRestService.getById(values[0].masterTemplate).then(function(templateInfo) {
                    editorModalConfiguration.content.template = templateInfo.uid;

                    return contextAwarePageStructureService.getPageStructureForPageEditing(editorModalConfiguration.content.typeCode, editorModalConfiguration.content.uid).then(function(fields) {

                        editorModalConfiguration.structure = fields;
                        var isPagePrimary = values[1];
                        if (isPagePrimary) {
                            editorModalConfiguration.structure.attributes = editorModalConfiguration.structure.attributes.filter(function(field) {
                                return field.qualifier !== 'restrictions' && field.qualifier !== 'onlyOneRestrictionMustApply';
                            });
                        }

                        return genericEditorModalService.open(editorModalConfiguration, null, function() {
                            crossFrameEventService.publish(SYNCHRONIZATION_POLLING.FETCH_SYNC_STATUS_ONCE, page.uuid);
                        });

                    }.bind(this));
                }.bind(this));

            }.bind(this));

        };

        return new PageEditorModalService();
    });
