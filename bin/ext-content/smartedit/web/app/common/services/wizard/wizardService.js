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
 * @name wizardServiceModule
 *
 * @description
 * # The wizardServiceModule
 * The wizardServiceModule is a module containing all wizard related services
 * # Creating a modal wizard in a few simple steps
 * 1. Add the wizardServiceModule to your module dependencies
 * 2. Inject {@link wizardServiceModule.modalWizard modalWizard} where you want to use the wizard.
 * 3. Create a new controller for your wizard. This controller will be used for all steps of the wizard.
 * 4. Implement a function in your new controller called <strong>getWizardConfig</strong> that returns a {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig}
 * 5. Use {@link wizardServiceModule.modalWizard#methods_open modalWizard.open()} passing in your new controller
 *
 * <pre>
    angular.module('myModule', ['wizardServiceModule'])
        .controller('myWizardController', ['wizardManager',
            function(wizardManager) {
                this.getWizardConfig = function() {
                    return {
                        steps: [{
                            id: 'step1',
                            name: 'i18n.step1.name',
                            title: 'i18n.step1.title',
                            templateUrl: 'some/template1.html'
                        }, {
                            id: 'step2',
                            name: 'i18n.step2.name',
                            title: 'i18n.step2.title',
                            templateUrl: 'some/template2.html'
                        }]
                    };
                }
            }]
        )
        .service('myService', function(modalWizard) {
            this.doSomething = function() {
                    return modalWizard.open({
                        controller: 'myWizardController'
                    });
            };
        });
 * </pre>
 *
 */
angular.module('wizardServiceModule', ['ui.bootstrap', 'translationServiceModule', 'functionsModule', 'coretemplates', 'modalServiceModule'])

    .factory('wizardActions', function() {

        var defaultAction = {
            id: "wizard_action_id",
            i18n: 'wizard_action_label',
            isMainAction: true,
            enableIfCondition: function() {
                return true;
            },
            executeIfCondition: function() {
                return true;
            },
            execute: function() {}
        };

        function newAction(conf) {
            var defaultCopy = angular.copy(defaultAction);
            return angular.merge(defaultCopy, conf);
        }

        return {

            customAction: function(conf) {
                return newAction(conf);
            },

            done: function(conf) {
                var nextConf = {
                    id: 'ACTION_DONE',
                    i18n: 'se.action.done',
                    execute: function(wizardService) {
                        wizardService.close();
                    }
                };
                angular.merge(nextConf, conf);
                return newAction(nextConf);
            },

            next: function(conf) {
                var nextConf = {
                    id: 'ACTION_NEXT',
                    i18n: 'se.action.next',
                    execute: function(wizardService) {
                        if (this.nextStepId) {
                            wizardService.goToStepWithId(this.nextStepId);
                        } else if (this.nextStepIndex) {
                            wizardService.goToStepWithIndex(this.nextStepIndex);
                        } else {
                            wizardService.goToStepWithIndex(wizardService.getCurrentStepIndex() + 1);
                        }
                    }
                };

                angular.merge(nextConf, conf);
                return newAction(nextConf);
            },

            navBarAction: function(conf) {
                if (!conf.wizardService || conf.destinationIndex === null) {
                    throw "Error initializating navBarAction, must provide the wizardService and destinationIndex fields";
                }

                var nextConf = {
                    id: 'ACTION_GOTO',
                    i18n: 'action.goto',
                    enableIfCondition: function() {
                        return conf.wizardService.getCurrentStepIndex() >= conf.destinationIndex;
                    },
                    execute: function(wizardService) {
                        wizardService.goToStepWithIndex(conf.destinationIndex);
                    }
                };

                angular.merge(nextConf, conf);
                return newAction(nextConf);
            },

            back: function(conf) {
                var nextConf = {
                    id: 'ACTION_BACK',
                    i18n: 'se.action.back',
                    isMainAction: false,
                    execute: function(wizardService) {
                        if (this.backStepId) {
                            wizardService.goToStepWithId(this.backStepId);
                        } else if (this.backStepIndex) {
                            wizardService.goToStepWithIndex(this.backStepIndex);
                        } else {
                            var currentIndex = wizardService.getCurrentStepIndex();
                            if (currentIndex <= 0) {
                                throw "Failure to execute BACK action, no previous index exists!";
                            }
                            wizardService.goToStepWithIndex(currentIndex - 1);
                        }
                    }
                };

                angular.merge(nextConf, conf);
                return newAction(nextConf);
            },

            cancel: function() {
                return newAction({
                    id: 'ACTION_CANCEL',
                    i18n: 'se.action.cancel',
                    isMainAction: false,
                    execute: function(wizardService) {
                        wizardService.cancel();
                    }
                });
            }

        };
    })



    /**
     * @ngdoc service
     * @name wizardServiceModule.modalWizard
     *
     * @description
     * The modalWizard service is used to create wizards that are embedded into the {@link modalServiceModule modalService}
     */
    .service('modalWizard', function(modalService, modalWizardControllerFactory) {

        this.validateConfig = function(config) {
            if (!config.controller) {
                throw "WizardService - initialization exception. No controller provided";
            }
        };

        /**
         * @ngdoc object
         * @name wizardServiceModule.object:WizardStepConfig
         * @description
         * A plain JSON object, representing the configuration options for a single step in a wizard
         */
        /**
         * @ngdoc property
         * @name id
         * @propertyOf wizardServiceModule.object:WizardStepConfig
         * @description
         * An optional unique ID for this step in the wizard. If no ID is provided, one is automatically generated.<br />
         * You may choose to provide an ID, making it easier to reference this step explicitly via the wizard service, or
         * be able to identify for which step a callback is being triggered.
         **/
        /**
         * @ngdoc property
         * @name name
         * @propertyOf wizardServiceModule.object:WizardStepConfig
         * @description An i18n key representing a meaning (short) name for this step.
         * This name will be displayed in the wizard navigation menu.
         **/
        /**
         * @ngdoc property
         * @name title
         * @propertyOf wizardServiceModule.object:WizardStepConfig
         * @description An i18n key, representing the title that will be displayed at the top of the wizard for this step.
         **/
        /**
         * @ngdoc property
         * @name templateUrl
         * @propertyOf wizardServiceModule.object:WizardStepConfig
         * @description The url of the html template for this step
         **/

        /**
         * @ngdoc object
         * @name wizardServiceModule.object:ModalWizardConfig
         * @description
         * A plain JSON object, representing the configuration options for a modal wizard
         */
        /**
         * @ngdoc property
         * @name steps (Array)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An ordered array of {@link wizardServiceModule.object:WizardStepConfig WizardStepConfig}
         **/
        /**
         * @ngdoc property
         * @name isFormValid (Function)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional callback function that receives a single parameter, the current step ID. This callback
         * is used to enable/disable the next action and the done action.
         * The callback should return a boolean to enabled the action. Null, or if this callback is not defined defaults to
         * true (enabled)
         **/
        /**
         * @ngdoc property
         * @name onNext (Function)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional callback function that receives a single parameter, the current step ID.
         * This callback is triggered after the next action is fired. You have the opportunity to halt the Next action by
         * returning promise and rejecting it, otherwise the wizard will continue and load the next step.
         **/
        /**
         * @ngdoc property
         * @name onCancel (Function)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional callback function that receives a single parameter, the current step ID.
         * This callback is triggered after the cancel action is fired. You have the opportunity to halt the cancel action
         * (thereby stopping the wizard from being closed), by returning a promise and rejecting it, otherwise the wizard will
         * continue the cancel action.
         **/
        /**
         * @ngdoc property
         * @name onDone (Function)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional callback function that has no parameters. This callback is triggered after the done
         * action is fired. You have the opportunity to halt the done action (thereby stopping the wizard from being closed),
         * by returning a promise and rejecting it, otherwise the wizard will continue and close the wizard.
         **/
        /**
         * @ngdoc property
         * @name resultFn (Function)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional callback function that has no parameters. This callback is triggered after the done
         * action is fired, and the wizard is about to be closed. If this function is defined and returns a value, this
         * value will be returned in the resolved promise returned by the {@link wizardServiceModule.modalWizard#methods_open modalWizard.open()}
         * This is an easy way to pass a result from the wizard to the caller.
         **/
        /**
         * @ngdoc property
         * @name doneLabel (String)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional i18n key to override the default label for the Done button
         **/
        /**
         * @ngdoc property
         * @name nextLabel (String)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional i18n key to override the default label for the Next button
         **/
        /**
         * @ngdoc property
         * @name backLabel (String)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional i18n key to override the default label for the Back button
         **/
        /**
         * @ngdoc property
         * @name cancelLabel (String)
         * @propertyOf wizardServiceModule.object:ModalWizardConfig
         * @description An optional i18n key to override the default label for the Cancel button
         **/

        /**
         * @ngdoc method
         * @name wizardServiceModule.modalWizard#open
         * @methodOf wizardServiceModule.modalWizard
         *
         * @description
         * Open provides a simple way to create modal wizards, with much of the boilerplate taken care of for you, such as look
         * and feel, and wizard navigation.
         *
         * @param {Object} conf configuration
         * @param {String|function|Array} conf.controller An angular controller which will be the underlying controller
         * for all of the wizard. This controller MUST implement the function <strong>getWizardConfig()</strong> which
         * returns a {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig}.<br />
         * If you need to do any manual wizard manipulation, 'wizardManager' can be injected into your controller.
         * See {@link wizardServiceModule.WizardManager WizardManager}
         * @param {String} conf.controllerAs (OPTIONAL) An alternate controller name that can be used in your wizard step
         * @param {=String=} conf.properties A map of properties to initialize the wizardManager with. They are accessible under wizardManager.properties.
         * templates. By default the controller name is wizardController.

         * @returns {function} {@link https://docs.angularjs.org/api/ng/service/$q promise} that will either be resolved (wizard finished) or
         * rejected (wizard cancelled).
         */
        this.open = function(config) {
            this.validateConfig(config);
            return modalService.open({
                templateUrl: 'modalWizardTemplate.html',
                controller: modalWizardControllerFactory.fromConfig(config),
                controllerAs: 'wizardController'
            });
        };
    })


    .service('modalWizardControllerFactory', function($controller, wizardServiceFactory, wizardActions, MODAL_BUTTON_STYLES, $q) {

        this.fromConfig = function(config) {
            return ['$scope', '$rootScope', 'modalManager',
                function WizardController($scope, $rootScope, modalManager) {

                    var wizardServiceImpl = wizardServiceFactory.newWizardService();

                    wizardServiceImpl.properties = config.properties;

                    angular.extend(this, $controller(config.controller, {
                        $scope: $scope,
                        wizardManager: wizardServiceImpl
                    }));
                    if (config.controllerAs) {
                        $scope[config.controllerAs] = this;
                    }

                    if (typeof this.getWizardConfig !== 'function') {
                        throw "The provided controller must provide a getWizardConfig() function.";
                    }
                    var modalConfig = this.getWizardConfig();
                    var controller = this;

                    this._wizardContext = {
                        _steps: modalConfig.steps
                    };

                    this.executeAction = function(action) {
                        wizardServiceImpl.getActionExecutor().executeAction(action);
                    };

                    function setupNavBar(steps) {
                        controller._wizardContext.navActions = steps.map(function(step, index) {
                            return wizardActions.navBarAction({
                                id: 'NAV-' + step.id,
                                stepIndex: index,
                                wizardService: wizardServiceImpl,
                                destinationIndex: index,
                                i18n: step.name,
                                isCurrentStep: function() {
                                    return this.stepIndex === wizardServiceImpl.getCurrentStepIndex();
                                }
                            });
                        });
                    }

                    function setupModal(modalConfig) {
                        controller._wizardContext.templateOverride = modalConfig.templateOverride;
                        if (modalConfig.cancelAction) {
                            modalManager.setDismissCallback(function() {
                                wizardServiceImpl.getActionExecutor().executeAction(modalConfig.cancelAction);
                                return $q.reject();
                            });
                        }
                        if (modalConfig.cancelAction) {
                            modalManager.setDismissCallback(function() {
                                wizardServiceImpl.getActionExecutor().executeAction(modalConfig.cancelAction);
                                return $q.reject();
                            });
                        }

                        // strategy stuff TODO - move to strategy layer
                        setupNavBar(modalConfig.steps);
                    }

                    function actionToButtonConf(action) {
                        return {
                            id: action.id,
                            style: action.isMainAction ? MODAL_BUTTON_STYLES.PRIMARY : MODAL_BUTTON_STYLES.SECONDARY,
                            label: action.i18n,
                            callback: function() {
                                wizardServiceImpl.getActionExecutor().executeAction(action);
                            }
                        };
                    }

                    wizardServiceImpl.onLoadStep = function(stepIndex, step) {
                        modalManager.title = step.title;
                        controller._wizardContext.templateUrl = step.templateUrl;
                        modalManager.removeAllButtons();
                        (step.actions || []).forEach(function(action) {

                            if (typeof action.enableIfCondition === 'function') {
                                $rootScope.$watch(action.enableIfCondition, function(newVal) {
                                    if (newVal) {
                                        modalManager.enableButton(action.id);
                                    } else {
                                        modalManager.disableButton(action.id);
                                    }
                                });
                            }
                            modalManager.addButton(actionToButtonConf(action));
                        }.bind(this));
                    };

                    wizardServiceImpl.onClose = function(result) {
                        modalManager.close(result);
                    };

                    wizardServiceImpl.onCancel = function() {
                        modalManager.dismiss();
                    };

                    wizardServiceImpl.onStepsUpdated = function(steps) {
                        setupNavBar(steps);
                        controller._wizardContext._steps = steps;
                    };

                    wizardServiceImpl.initialize(modalConfig);
                    setupModal(modalConfig);

                }
            ];
        };
    })


    .service('defaultWizardActionStrategy', function(wizardActions) {

        function applyOverrides(wizardService, action, label, executeCondition, enableCondition) {
            if (label) {
                action.i18n = label;
            }
            if (executeCondition) {
                action.executeIfCondition = function() {
                    return executeCondition(wizardService.getCurrentStepId());
                };
            }
            if (enableCondition) {
                action.enableIfCondition = function() {
                    return enableCondition(wizardService.getCurrentStepId());
                };
            }
            return action;
        }

        this.applyStrategy = function(wizardService, conf) {
            var nextAction = applyOverrides(wizardService, wizardActions.next(), conf.nextLabel, conf.onNext, conf.isFormValid);
            var doneAction = applyOverrides(wizardService, wizardActions.done(), conf.doneLabel, conf.onDone, conf.isFormValid);

            var backConf = conf.backLabel ? {
                i18n: conf.backLabel
            } : null;
            var backAction = wizardActions.back(backConf);

            conf.steps.forEach(function(step, index) {
                step.actions = [];
                if (index > 0) {
                    step.actions.push(backAction);
                }
                if (index === (conf.steps.length - 1)) {
                    step.actions.push(doneAction);
                } else {
                    step.actions.push(nextAction);
                }
            });

            conf.cancelAction = applyOverrides(wizardService, wizardActions.cancel(), conf.cancelLabel, conf.onCancel, null);
            conf.templateOverride = 'modalWizardNavBarTemplate.html';
        };
    })

    .service('wizardServiceFactory', function(WizardService) {
        'ngInject';
        this.newWizardService = function() {
            return new WizardService();
        };
    })


    .factory('WizardService', function($q, defaultWizardActionStrategy, generateIdentifier) {
        'ngInject';

        function validateConfig(config) {
            if (!config.steps || config.steps.length <= 0) {
                throw "Invalid WizardService configuration - no steps provided";
            }

            config.steps.forEach(function(step) {
                if (!step.templateUrl) {
                    throw "Invalid WizardService configuration - Step missing a url: " + step;
                }
            });
        }

        function validateStepUids(steps) {
            var stepIds = {};
            steps.forEach(function(step) {
                if (step.id === undefined && step.id === null) {
                    step.id = generateIdentifier();
                } else {
                    if (stepIds[step.id]) {
                        throw "Invalid (Duplicate) step id: " + step.id;
                    }
                    stepIds[step.id] = step.id;
                }
            });
        }

        /**
         * @ngdoc service
         * @name wizardServiceModule.WizardManager
         *
         * @description
         * The Wizard Manager is a wizard management service that can be injected into your wizard controller.
         *
         */
        function WizardService() {
            // the overridable callbacks
            this.onLoadStep = function() {};
            this.onClose = function() {};
            this.onCancel = function() {};
            this.onStepsUpdated = function() {};
        }

        WizardService.prototype.initialize = function(conf) {

            validateConfig(conf);

            this._actionStrategy = conf.actionStrategy || defaultWizardActionStrategy;
            this._actionStrategy.applyStrategy(this, conf);

            this._currentIndex = 0;
            this._conf = angular.copy(conf);
            this._steps = this._conf.steps;
            this._getResult = conf.resultFn;
            validateStepUids(this._steps);

            this.goToStepWithIndex(0);
        };

        WizardService.prototype.getActionExecutor = function() {
            return this;
        };


        WizardService.prototype.executeAction = function(action) {
            if (action.executeIfCondition) {
                $q.when(action.executeIfCondition()).then(function() {
                    return $q.when(action.execute(this));
                }.bind(this));
            } else {
                return $q.when(action.execute(this));
            }

        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#goToStepWithIndex
         * @methodOf wizardServiceModule.WizardManager
         * @description Navigates the wizard to the given step
         * @param {Number} index The 0-based index from the steps array returned by the wizard controllers getWizardConfig() function
         */
        WizardService.prototype.goToStepWithIndex = function(index) {
            var nextStep = this.getStepWithIndex(index);
            if (nextStep) {
                this.onLoadStep(index, nextStep);
                this._currentIndex = index;
            }
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#goToStepWithId
         * @methodOf wizardServiceModule.WizardManager
         * @description Navigates the wizard to the given step
         * @param {String} id The ID of a step returned by the wizard controllers getWizardConfig() function. Note that if
         * no id was provided for a given step, then one is automatically generated.
         */
        WizardService.prototype.goToStepWithId = function(id) {
            this.goToStepWithIndex(this.getStepIndexFromId(id));
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#addStep
         * @methodOf wizardServiceModule.WizardManager
         * @description Adds an additional step to the wizard at runtime
         * @param {Object} newStep A {@link wizardServiceModule.object:WizardStepConfig WizardStepConfig}
         * @param {Number} index (OPTIONAL) A 0-based index position in the steps array. Default is 0.
         */
        WizardService.prototype.addStep = function(newStep, index) {
            if (newStep.id !== 0 && !newStep.id) {
                newStep.id = generateIdentifier();
            }
            if (!index) {
                index = 0;
            }
            if (this._currentIndex >= index) {
                this._currentIndex++;
            }
            this._steps.splice(index, 0, newStep);
            validateStepUids(this._steps);
            this._actionStrategy.applyStrategy(this, this._conf);
            this.onStepsUpdated(this._steps);
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#removeStepById
         * @methodOf wizardServiceModule.WizardManager
         * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
         * wizard will return to the first step. Removing all the steps will result in an error.
         * @param {String} id The id of the step you wish to remove
         */
        WizardService.prototype.removeStepById = function(id) {
            this.removeStepByIndex(this.getStepIndexFromId(id));
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#removeStepByIndex
         * @methodOf wizardServiceModule.WizardManager
         * @description Remove a step form the wizard at runtime. If you are removing the currently displayed step, the
         * wizard will return to the first step. Removing all the steps will result in an error.
         * @param {Number} index The 0-based index of the step you wish to remove.
         */
        WizardService.prototype.removeStepByIndex = function(index) {
            if (index >= 0 && index < this.getStepsCount()) {
                this._steps.splice(index, 1);
                if (index === this._currentIndex) {
                    this.goToStepWithIndex(0);
                }
                this._actionStrategy.applyStrategy(this, this._conf);
                this.onStepsUpdated(this._steps);
            }
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#close
         * @methodOf wizardServiceModule.WizardManager
         * @description Close the wizard. This will return a resolved promise to the creator of the wizard, and if any
         * resultFn was provided in the {@link wizardServiceModule.object:ModalWizardConfig ModalWizardConfig} the returned
         * value of this function will be passed as the result.
         */
        WizardService.prototype.close = function() {
            var result;
            if (typeof this._getResult === 'function') {
                result = this._getResult();
            }
            this.onClose(result);
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#cancel
         * @methodOf wizardServiceModule.WizardManager
         * @description Cancel the wizard. This will return a rejected promise to the creator of the wizard.
         */
        WizardService.prototype.cancel = function() {
            this.onCancel();
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getSteps
         * @methodOf wizardServiceModule.WizardManager
         * @returns {Array} An array of all the steps in the wizard
         */
        WizardService.prototype.getSteps = function() {
            return this._steps;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getStepIndexFromId
         * @methodOf wizardServiceModule.WizardManager
         * @param {String} id A step ID
         * @returns {Number} The index of the step with the provided ID
         */
        WizardService.prototype.getStepIndexFromId = function(stepId) {
            var index = this._steps.findIndex(function(step) {
                return step.id === stepId;
            });
            return index;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#containsStep
         * @methodOf wizardServiceModule.WizardManager
         * @param {String} id A step ID
         * @returns {Boolean} True if the ID exists in one of the steps
         */
        WizardService.prototype.containsStep = function(stepId) {
            return this.getStepIndexFromId(stepId) >= 0;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getCurrentStepId
         * @methodOf wizardServiceModule.WizardManager
         * @returns {String} The ID of the currently displayed step
         */
        WizardService.prototype.getCurrentStepId = function() {
            return this.getCurrentStep().id;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getCurrentStepIndex
         * @methodOf wizardServiceModule.WizardManager
         * @returns {Number} The index of the currently displayed step
         */
        WizardService.prototype.getCurrentStepIndex = function() {
            return this._currentIndex;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getCurrentStep
         * @methodOf wizardServiceModule.WizardManager
         * @returns {Object} The currently displayed step
         */
        WizardService.prototype.getCurrentStep = function() {
            return this.getStepWithIndex(this._currentIndex);
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getStepsCount
         * @methodOf wizardServiceModule.WizardManager
         * @returns {Number} The number of steps in the wizard. This should always be equal to the size of the array
         * returned by {@link wizardServiceModule.WizardManager#methods_getSteps getSteps()}
         */
        WizardService.prototype.getStepsCount = function() {
            return this._steps.length;
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getStepWithId
         * @methodOf wizardServiceModule.WizardManager
         * @param {String} id The ID of a step
         * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given ID
         */
        WizardService.prototype.getStepWithId = function(id) {
            var index = this.getStepIndexFromId(id);
            if (index >= 0) {
                return this.getStepWithIndex(index);
            }
        };

        /**
         * @ngdoc method
         * @name wizardServiceModule.WizardManager#getStepWithIndex
         * @methodOf wizardServiceModule.WizardManager
         * @param {Number} index The ID of a step
         * @returns {Object} The {@link wizardServiceModule.object:WizardStepConfig step} with the given index
         */
        WizardService.prototype.getStepWithIndex = function(index) {
            if (index >= 0 && index < this.getStepsCount()) {
                return this._steps[index];
            }
            throw ("wizardService.getStepForIndex - Index out of bounds: " + index);
        };

        return WizardService;
    });
