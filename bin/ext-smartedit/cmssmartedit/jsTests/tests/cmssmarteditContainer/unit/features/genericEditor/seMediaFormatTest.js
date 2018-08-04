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
/* jshint unused:false, undef:false */
describe('seMediaFormat', function() {
    var parentScope, scope, element, ctrl;
    var seMediaFormatConstants, seFileValidationServiceConstants;
    var mediaService;

    beforeEach(module('cmssmarteditContainerTemplates'));

    beforeEach(module('pascalprecht.translate', function($translateProvider) {
        $translateProvider.translations('en', {
            'se.media.format.upload': 'Upload',
            'se.media.format.replaceimage': 'Replace Image',
            'se.media.format.under.edit': 'Editing...'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(module('seFileValidationServiceModule'));

    beforeEach(module('mediaServiceModule', function($provide) {
        mediaService = jasmine.createSpyObj('mediaService', ['getMedia']);
        $provide.value('mediaService', mediaService);
    }));


    beforeEach(module('seMediaFormatModule'));

    beforeEach(inject(function($compile, $rootScope, $q, _seMediaFormatConstants_, _seFileValidationServiceConstants_) {
        seMediaFormatConstants = _seMediaFormatConstants_;
        seFileValidationServiceConstants = _seFileValidationServiceConstants_;

        mediaService.getMedia.and.returnValue($q.when({
            code: 'someCode',
            url: '/web/webroot/images/edit_icon.png'
        }));

        parentScope = $rootScope.$new();
        window.smarteditJQuery.extend(parentScope, {
            mediaUuid: 'someUuid',
            mediaFormat: 'someFormat',
            field: {},
            isUnderEdit: false,
            onFileSelect: jasmine.createSpy('onFileSelect')
        });

        element = $compile('<se-media-format ' +
            'data-media-uuid="mediaUuid" ' +
            'data-media-format="mediaFormat" ' +
            'data-field="field" ' +
            'data-is-under-edit="isUnderEdit" ' +
            'data-on-file-select="onFileSelect" ' +
            '</se-media-format>')(parentScope);
        parentScope.$digest();

        scope = element.isolateScope();
        ctrl = scope.ctrl;

    }));

    describe('controller', function() {
        it('should be initialized', function() {
            expect(ctrl.i18nKeys).toBe(seMediaFormatConstants.I18N_KEYS);
            expect(ctrl.acceptedFileTypes).toBe(seFileValidationServiceConstants.ACCEPTED_FILE_TYPES);
        });

        it('should get media if it a uuid is provided', function() {
            expect(ctrl.mediaUuid).toBe('someUuid');
            expect(ctrl.media).toEqual({
                code: 'someCode',
                url: '/web/webroot/images/edit_icon.png'
            });
        });

        it('should clear the media if the uuid is not provided', function() {

            scope.$apply(function() {
                ctrl.mediaUuid = null;
            });
            expect(ctrl.media).toEqual({});
        });
    });

    describe('template', function() {
        it('should display the format', function() {
            expect(element.text()).toContain('someFormat');
        });

        describe('when media uuid present', function() {
            it('should show the media present view', function() {
                expect(element.find('.media-present')).toExist();
                expect(element.find('.media-absent')).not.toExist();
                expect(element.find('.media-is-under-edit')).not.toExist();
            });

            it('should show the image', function() {
                expect(element.find('.thumbnail--image-preview').attr('data-ng-src')).toBe('/web/webroot/images/edit_icon.png');
            });

            it('should show a replace button', function() {
                expect(element.find('.media-selector--preview__left--p').text().trim()).toContain('se.media.format.remove');
            });
        });

        describe('when media uuid absent', function() {
            beforeEach(function() {
                ctrl.mediaUuid = null;
                scope.$digest();
            });

            it('should show the media absent view', function() {
                expect(element.find('.media-present')).not.toExist();
                expect(element.find('.media-absent')).toExist();
                expect(element.find('.media-is-under-edit')).not.toExist();
            });

            it('should show an upload button', function() {
                expect(element.find('.media-absent se-file-selector .label__fileUpload-link').text().trim()).toBe('Upload');
            });
        });

        describe('when under edit', function() {
            beforeEach(function() {
                ctrl.isUnderEdit = true;
                scope.$digest();
            });

            it('should show the media uploading view', function() {
                expect(element.find('.media-present')).not.toExist();
                expect(element.find('.media-absent')).not.toExist();
                expect(element.find('.media-is-under-edit')).toExist();
            });

            it('should show the editing text', function() {
                expect(element.find('.media-is-under-edit').text().trim()).toContain('Upload');
            });
        });
    });
});
