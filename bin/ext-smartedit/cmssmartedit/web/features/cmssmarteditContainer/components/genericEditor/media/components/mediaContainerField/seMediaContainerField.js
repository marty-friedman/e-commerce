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
angular.module('seMediaContainerFieldModule', [
        'seMediaUploadFormModule',
        'seMediaFormatModule',
        'seErrorsListModule',
        'seFileValidationServiceModule'
    ])
    .controller('seMediaContainerFieldController', function(seFileValidationService) {
        this.image = {};
        this.fileErrors = [];

        this.fileSelected = function(files, format) {
            var previousFormat = this.image.format;
            this.resetImage();

            if (files.length === 1) {
                seFileValidationService.validate(files[0], this.fileErrors).then(function() {
                    this.image = {
                        file: files[0],
                        format: format || previousFormat
                    };
                }.bind(this));
            }
        };

        this.resetImage = function() {
            this.fileErrors = [];
            this.image = {};
        };

        this.imageUploaded = function(uuid) {
            if (this.model && this.model[this.qualifier]) {
                this.model[this.qualifier][this.image.format] = uuid;
            } else {
                this.model[this.qualifier] = {};
                this.model[this.qualifier][this.image.format] = uuid;
            }

            this.resetImage();
        };

        this.imageDeleted = function(format) {
            delete this.model[this.qualifier][format];
        };

        this.isFormatUnderEdit = function(format) {
            return format === this.image.format;
        };
    })
    .directive('seMediaContainerField', function() {
        return {
            templateUrl: 'seMediaContainerFieldTemplate.html',
            restrict: 'E',
            controller: 'seMediaContainerFieldController',
            controllerAs: 'ctrl',
            scope: {},
            bindToController: {
                field: '=',
                model: '=',
                editor: '=',
                qualifier: '='
            }
        };
    });
