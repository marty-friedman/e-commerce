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
import * as angular from 'angular';

/**
 * @ngdoc overview
 * @name administration
 *
 * @description
 * # The administration module
 *
 * The administration module provides services to display and manage configurations
 * that point to web service and the value property contains the URI of the web service or data.
 *
 */
export const AdministrationModule = angular.module('administration', ['functionsModule', 'translationServiceModule', 'ngResource', 'loadConfigModule', 'modalServiceModule', 'confirmationModalServiceModule', 'editorFieldMappingServiceModule', 'seProductCatalogVersionsSelectorModule'])
	.run((editorFieldMappingService: any) => {
		'ngInject';

		editorFieldMappingService.addFieldMapping('ProductCatalogVersionsSelector', null, null, {
			template: 'productCatalogVersionsSelectorWrapperTemplate.html'
		});
	})
    /**
     * @ngdoc service
     * @name administration.ConfigurationEditor
     *
     * @description
     * The Configuration Editor Service is a convenience service that provides the methods to manage configurations within the Configuration Editor UI, such as filtering configurations, adding entries and removing entries.
     */
	.factory('ConfigurationEditor', ($resource: any, copy: any, $q: angular.IQService, convertToArray: any, $log: angular.ILogService, loadConfigManagerService: any, ParseError: any, CONFIGURATION_URI: any, isBlank: any) => {
		'ngInject';

		// Constants
		const ABSOLUTE_URI_NOT_APPROVED = "URI_EXCEPTION";
		const ABSOLUTE_URI_REGEX = /(\"[A-Za-z]+:\/|\/\/)/;

		const ConfigurationEditor = function() {
			this.editorCRUDService = $resource(CONFIGURATION_URI, {}, {
				update: {
					method: 'PUT',
					cache: false,
					isArray: false
				},
				remove: {
					method: 'DELETE',
					cache: false,
					isArray: false
				},
				save: {
					method: 'POST',
					cache: false,
					isArray: false
				}
			});

			this.configuration = [];
		};

		ConfigurationEditor.prototype._reset = function(configurationForm: any) {
			this.configuration = copy(this.pristine);
			if (configurationForm) {
				configurationForm.$setPristine();
			}
			if (this.loadCallback) {
				this.loadCallback();
			}
		};

		ConfigurationEditor.prototype._addError = function(entry: any, type: any, message: any) {
			entry.errors = entry.errors || {};
			entry.errors[type] = entry.errors[type] || [];
			entry.errors[type].push({
				message
			});
		};

		ConfigurationEditor.prototype._addKeyError = function(entry: any, message: any) {
			this._addError(entry, "keys", message);
		};
		ConfigurationEditor.prototype._addValueError = function(entry: any, message: any) {
			this._addError(entry, "values", message);
		};

		ConfigurationEditor.prototype._prettify = function(array: any) {
			const configuration = copy(array);
			configuration.forEach(function(entry: any) {
				try {
					entry.value = JSON.stringify(JSON.parse(entry.value), null, 2);
				} catch (parseError) {
					this._addValueError(entry, 'se.configurationform.json.parse.error');
				}
			}.bind(this));
			return configuration;
		};


        /**
         * for editing purposes
         */
		ConfigurationEditor.prototype.loadAndPresent = function() {
			const deferred = $q.defer();

			loadConfigManagerService.loadAsArray().then(function(response: any) {
				this.pristine = this._prettify(response);
				this._reset();
				deferred.resolve();
			}.bind(this),
				function() {
					$log.log("load failed");
					deferred.reject();
				}
			);
			return deferred.promise;
		};

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#addEntry
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Add Entry method adds an entry to the list of configurations.
         *
         */
		ConfigurationEditor.prototype.addEntry = function() {
			this.configuration.unshift({
				isNew: true
			});
		};

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#removeEntry
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Remove Entry method deletes the specified entry from the list of configurations. The method does not delete the actual configuration, but just removes it from the array of configurations.
         * The entry will be deleted when a user clicks the Submit button but if the entry is new we can are removing it from the configuration
         *
         * @param {Object} entry The object to be deleted
         * @param {Object} configurationForm The form object which is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}
         * that provides methods to monitor and control the state of the form.
         */
		ConfigurationEditor.prototype.removeEntry = function(entry: any, configurationForm: any) {
			if (entry.isNew) {
				this.configuration = this.configuration.filter(function(confEntry: any) {
					return !confEntry.isNew || confEntry.key !== entry.key;
				});
			} else {
				configurationForm.$setDirty();
				entry.toDelete = true;
			}
		};

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#filterConfiguration
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * Method that returns a list of configurations by filtering out only those configurations whose 'toDelete' parameter is set to false.
         *
         * @returns {Object} A list of filtered configurations.
         */
		ConfigurationEditor.prototype.filterConfiguration = function() {
			return this.configuration.filter((instance: any) => {
				return instance.toDelete !== true;
			});
		};

		ConfigurationEditor.prototype._validate = function(entry: any) {
			try {
				if (entry.requiresUserCheck && !entry.isCheckedByUser) {
					throw new Error(ABSOLUTE_URI_NOT_APPROVED);
				}
				return JSON.stringify(JSON.parse(entry.value));
			} catch (parseError) {
				throw new ParseError(entry.value);
			}
		};

		ConfigurationEditor.prototype._isValid = function(configurationForm: any) {
			this.configuration.forEach((entry: any) => {
				delete entry.errors;
			});

			if (configurationForm.$invalid) {
				this.configuration.forEach(function(entry: any) {
					if (isBlank(entry.key)) {
						this._addKeyError(entry, 'se.configurationform.required.entry.error');
						entry.hasErrors = true;
					}
					if (isBlank(entry.value)) {
						this._addValueError(entry, 'se.configurationform.required.entry.error');
						entry.hasErrors = true;
					}
				}.bind(this));
			}
			return configurationForm.$valid && !this.configuration.reduce(function(confHolder: any, nextConfiguration: any) {
				if (confHolder.keys.indexOf(nextConfiguration.key) > -1) {
					this._addKeyError(nextConfiguration, 'se.configurationform.duplicate.entry.error');
					confHolder.errors = true;
				} else {
					confHolder.keys.push(nextConfiguration.key);
				}
				return confHolder;
			}.bind(this), {
					keys: [],
					errors: false
				}).errors;
		};

		ConfigurationEditor.prototype._validateUserInput = function(entry: any) {
			if (entry.value) {
				entry.requiresUserCheck = (entry.value.match(ABSOLUTE_URI_REGEX)) ? true : false;
			}
		};

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#submit
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The Submit method saves the list of available configurations by making a REST call to a web service.
         * The method is called when a user clicks the Submit button in the configuration editor.
         *
         * @param {Object} configurationForm The form object that is an instance of {@link https://docs.angularjs.org/api/ng/type/form.FormController FormController}.
         * It provides methods to monitor and control the state of the form.
         */
		ConfigurationEditor.prototype.submit = function(configurationForm: any) {
			const deferred = $q.defer();
			if (configurationForm.$dirty && this._isValid(configurationForm)) {
				this.configuration.forEach(function(entry: any, i: number) {
					try {
						let payload = copy(entry);
						delete payload.toDelete;
						delete payload.errors;
						const method = entry.toDelete === true ? 'remove' : (payload.isNew === true ? 'save' : 'update');
						payload.secured = false; // needed for yaas configuration service
						delete payload.isNew;
						let params;
						switch (method) {
							case 'save':
								payload.value = this._validate(payload);
								params = {};
								break;
							case 'update':
								payload.value = this._validate(payload);
								params = {
									key: payload.key
								};
								break;
							case 'remove':
								params = {
									key: payload.key
								};
								payload = undefined;
								break;
						}

						this.editorCRUDService[method](params, payload).$promise.then(
							function(entity: any, index: any, meth: string) {
								switch (meth) {
									case 'save':
										delete entity.isNew;
										break;
									case 'remove':
										this.configuration.splice(index, 1);
										break;
								}
							}.bind(this, entry, i, method),
							function() {
								this._addValueError(entry, 'configurationform.save.error');
								deferred.reject();
							}.bind(this)
						);
						entry.hasErrors = false;
					} catch (error) {
						if (error instanceof ParseError) {
							this._addValueError(entry, 'se.configurationform.json.parse.error');
							deferred.reject();
						}
						entry.hasErrors = true;
					}
				}.bind(this));
				deferred.resolve();
				configurationForm.$setPristine();
			} else {
				deferred.reject();
			}
			return deferred.promise;
		};

        /**
         * @ngdoc method
         * @name administration.ConfigurationEditor#init
         * @methodOf administration.ConfigurationEditor
         *
         * @description
         * The init method initializes the configuration editor and loads all the configurations so they can be edited.
         *
         * @param {Function} loadCallback The callback to be executed after loading the configurations.
         */
		ConfigurationEditor.prototype.init = function(loadCallback: any) {
			this.loadCallback = loadCallback;
			const deferred = $q.defer();
			this.loadAndPresent().then(function() {
				deferred.resolve();
			}, function() {
				deferred.reject();
			});
			return deferred.promise;
		};

		return ConfigurationEditor;

	})
	.factory('configurationService', (ConfigurationEditor: any) => {
		'ngInject';
		return new ConfigurationEditor();
	})

	/**
	 * @ngdoc directive
	 *
	 * @name administration.directive:generalConfiguration
	 * @restrict E
	 * @element ANY
	 *
	 * @description
	 * The Generation Configuration directive is an HTML marker. It attaches functions of the Configuration Editor to the
	 * DOM elements of the General Configuration Template in order to display the configuration editor.
	 *
	 */
	.directive('generalConfiguration', (modalService: any, $log: angular.ILogService, MODAL_BUTTON_ACTIONS: any, MODAL_BUTTON_STYLES: any, confirmationModalService: any) => {
		'ngInject';
		return {
			templateUrl: 'generalConfigurationTemplate.html',
			restrict: 'E',
			transclude: true,
			replace: true,
			link: ($scope: any) => {
				$scope.editConfiguration = function() {
					modalService.open({
						title: 'se.modal.administration.configuration.edit.title',
						templateUrl: 'editConfigurationsTemplate.html',
						controller: ['$scope', '$timeout', 'yjQuery', 'configurationService', '$q', 'modalManager',
							/* tslint:disable:no-shadowed-variable */
							function($scope: any, $timeout: angular.ITimeoutService, yjQuery: any, configurationService: any, $q: angular.IQService, modalManager: any) {
								this.isDirty = false;
								$scope.form = {};

								this.onSave = function() {
									$scope.editor.submit($scope.form.configurationForm).then(function() {
										modalManager.close();
									});
								};

								this.onCancel = function() {
									const deferred = $q.defer();

									if (this.isDirty) {
										confirmationModalService.confirm({
											description: 'se.editor.cancel.confirm'
										}).then(function() {
											modalManager.close();
											deferred.resolve();
										}.bind(this), function() {
											deferred.reject();
										});
									} else {
										deferred.resolve();
									}

									return deferred.promise;
								};

								this.init = function() {
									modalManager.setDismissCallback((this.onCancel).bind(this));

									modalManager.setButtonHandler(function(buttonId: string) {
										switch (buttonId) {
											case 'save':
												return this.onSave();
											case 'cancel':
												return this.onCancel();
											default:
												$log.error('A button callback has not been registered for button with id', buttonId);
												break;
										}
									}.bind(this));

									$scope.$watch(function() {
										const isDirty = $scope.form.configurationForm && $scope.form.configurationForm.$dirty;
										const isValid = $scope.form.configurationForm && $scope.form.configurationForm.$valid;
										return {
											isDirty,
											isValid
										};
									}.bind(this), function(obj: any) {
										if (typeof obj.isDirty === 'boolean') {
											if (obj.isDirty) {
												this.isDirty = true;
												modalManager.enableButton('save');
											} else {
												this.isDirty = false;
												modalManager.disableButton('save');
											}
										}
									}.bind(this), true);
								};

								$scope.editor = configurationService;
								$scope.editor.init(function() {
									$timeout(function() {
										yjQuery("textarea").each(
											function() {
												yjQuery(this).height(this.scrollHeight);
											});
									}, 100);
								});
							}
						],
						buttons: [{
							id: 'cancel',
							label: 'se.cms.component.confirmation.modal.cancel',
							style: MODAL_BUTTON_STYLES.SECONDARY,
							action: MODAL_BUTTON_ACTIONS.DISMISS
						}, {
							id: 'save',
							label: 'se.cms.component.confirmation.modal.save',
							action: MODAL_BUTTON_ACTIONS.NONE,
							disabled: true
						}]
					});
				};
			}
		};
	}).name;
