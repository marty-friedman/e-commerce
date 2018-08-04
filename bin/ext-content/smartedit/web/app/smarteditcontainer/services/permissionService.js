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
angular.module('permissionServiceModule', [
        'permissionServiceInterfaceModule',
        'gatewayProxyModule',
        'eventServiceModule',
        'functionsModule',
        'crossFrameEventServiceModule'
    ])
    /**
     * @ngdoc object
     * @name permissionServiceModule.object:DEFAULT_RULE_NAME
     * @description
     * The name used to register the default rule.
     */
    .constant('DEFAULT_RULE_NAME', 'se.permission.service.default.rule')
    .factory('permissionService', function($q, $log, extend, DEFAULT_RULE_NAME, EVENTS, SE_PERMISSION_SERVICE_GATEWAY_ID, PermissionServiceInterface, gatewayProxy, systemEventService, crossFrameEventService, isBlank) {
        var rules = [];
        var permissions = [];
        var cachedResults = {};

        var hasCacheRegion = function(ruleName) {
            return cachedResults.hasOwnProperty(ruleName);
        };

        var getCacheRegion = function(ruleName) {
            return cachedResults[ruleName];
        };

        var PermissionService = function() {
            this._registerEventHandlers();
            gatewayProxy.initForService(this, ["isPermitted", "clearCache", "registerPermission", "unregisterDefaultRule", "registerDefaultRule", "registerRule", "_registerRule", "_remoteCallRuleVerify", "_registerDefaultRule"], SE_PERMISSION_SERVICE_GATEWAY_ID);
        };

        PermissionService = extend(PermissionServiceInterface, PermissionService);

        /**
         * This method adds a promise obtained by calling the pre-configured rule.verify function to the rulePromises
         * map if the result does not exist in the rule's cache. Otherwise, a promise that contains the cached result
         * is added.
         *
         * The promise obtained from the rule.verify function is chained to allow short-circuiting the permission
         * verification process. If a rule resolves with a false result or with an error, the chained promise is
         * rejected to stop the verification process without waiting for all other rules to resolve.
         *
         * @param {Object} rulePromises An object that maps rule names to promises.
         * @param {Object} rulePermissionNames An object that maps rule names to permission name arrays.
         * @param {String} ruleName The name of the rule to verify.
         */
        PermissionService.prototype._addRulePromise = function(rulePromises, rulePermissionNames, ruleName) {
            var rule = this._getRule(ruleName);
            var permissionNameObjs = rulePermissionNames[ruleName];
            var cacheKey = this._generateCacheKey(permissionNameObjs);

            var rulePromise;

            if (this.hasCachedResult(ruleName, cacheKey)) {
                rulePromise = $q.when(this._getCachedResult(ruleName, cacheKey));
            } else {
                rulePromise = this._callRuleVerify(rule.names.join("-"), permissionNameObjs).then(function(isPermitted) {
                    return isPermitted ? $q.resolve(true) : $q.reject(false);
                });
            }

            rulePromises[ruleName] = rulePromise;
        };

        /**
         * This method validates a permission name. Permission names need to be prefixed by at least one
         * namespace followed by a "." character to be valid.
         *
         * Example: se.mynamespace is valid.
         * Example: mynamespace is not valid.
         */
        PermissionService.prototype._isPermissionNameValid = function(permissionName) {
            var checkNameSpace = /^[A-Za-z0-9_\-]+\.[A-Za-z0-9_\-\.]+/;
            return checkNameSpace.test(permissionName);
        };

        /**
         * This method returns an object that maps rule names to promises.
         */
        PermissionService.prototype._getRulePromises = function(rulePermissionNames) {
            var rulePromises = {};

            Object.keys(rulePermissionNames).forEach(function(ruleName) {
                this._addRulePromise.call(this, rulePromises, rulePermissionNames, ruleName);
            }.bind(this));

            return rulePromises;
        };

        /**
         * This method returns true if a default rule is already registered.
         *
         * @returns {boolean} true if the default rule has been registered, false otherwise.
         */
        PermissionService.prototype._hasDefaultRule = function() {
            return !!this._getRule(DEFAULT_RULE_NAME);
        };

        /**
         * This method returns the rule's cached result for the given key.
         *
         * @param {Object} ruleName The name of the rule for which to lookup the cached result.
         * @param {String} key The cached key to lookup..
         *
         * @returns {Boolean} The cached result, if it exists, null otherwise.
         */
        PermissionService.prototype._getCachedResult = function(ruleName, key) {
            return hasCacheRegion(ruleName) ? getCacheRegion(ruleName)[key] : null;
        };

        /**
         * This method generates a key to store a rule's result for a given combination of
         * permissions in its cache. It is done by sorting the list of permissions by name
         * and serializing it.
         *
         * @param {Object[]} permissions A list of permissions with a name and context.
         *
         * [{
         *     name: "permission.name"
         *     context: {
         *         key: "value"
         *     }
         * }]
         *
         * @returns {String} The serialized sorted list of permissions.
         */
        PermissionService.prototype._generateCacheKey = function(permissions) {
            return JSON.stringify(permissions.sort(function(permissionA, permissionB) {
                var nameA = permissionA.name;
                var nameB = permissionB.name;

                return nameA === nameB ? 0 : (nameA < nameB ? -1 : 1);
            }));
        };

        /**
         * This method goes through the permission name arrays associated to rule names to remove any duplicate
         * permission names.
         *
         * If one or more permission names with the same context are found in a rule name's permission name array,
         * only one entry is kept.
         */
        PermissionService.prototype._removeDuplicatePermissionNames = function(rulePermissionNames) {
            Object.keys(rulePermissionNames).forEach(function(ruleName) {
                rulePermissionNames[ruleName] = rulePermissionNames[ruleName].filter(function(currentPermission) {
                    var existingPermission = rulePermissionNames[ruleName].find(function(permission) {
                        return permission.name === currentPermission.name;
                    });

                    if (existingPermission === currentPermission) {
                        return true;
                    } else {
                        var existingPermissionContext = existingPermission.context;
                        var currentPermissionContext = currentPermission.context;

                        return JSON.stringify(existingPermissionContext) !== JSON.stringify(currentPermissionContext);
                    }
                });
            });
        };

        /**
         * This method returns an object mapping rule name to permission name arrays.
         *
         * It will iterate through the given permission name object array to extract the permission names and contexts,
         * populate the map and clean it up by removing duplicate permission name and context pairs.
         */
        PermissionService.prototype._mapRuleNameToPermissionNames = function(permissions) {
            var rulePermissionNames = {};

            permissions.forEach(function(permission) {
                if (!permission.names) {
                    throw "Requested Permission requires at least one name";
                }

                var permissionNames = permission.names;
                var permissionContext = permission.context;

                permissionNames.forEach(function(permissionName) {
                    this._populateRulePermissionNames(rulePermissionNames, permissionName, permissionContext);
                }.bind(this));
            }.bind(this));

            this._removeDuplicatePermissionNames(rulePermissionNames);

            return rulePermissionNames;
        };

        /**
         * This method will populate rulePermissionNames with the rules associated to the permission with the given
         * permissionName.
         *
         * If no permission is registered with the given permissionName and a default rule is registered, the default
         * rule is added to rulePermissionNames.
         *
         * If no permission is registered with the given permissionName and no default rule is registered, an error
         * is thrown.
         */
        PermissionService.prototype._populateRulePermissionNames = function(rulePermissionNames, permissionName, permissionContext) {
            var permission = this.getPermission(permissionName);
            var permissionHasRules = !!permission && !!permission.rules && permission.rules.length > 0;

            if (permissionHasRules) {
                permission.rules.forEach(function(ruleName) {
                    this._addPermissionName(rulePermissionNames, ruleName, permissionName, permissionContext);
                }.bind(this));
            } else if (this._hasDefaultRule()) {
                this._addPermissionName(rulePermissionNames, DEFAULT_RULE_NAME, permissionName, permissionContext);
            } else {
                throw "Permission has no rules";
            }
        };

        /**
         * This method will add an object with the permissionName and permissionContext to rulePermissionNames.
         *
         * Since rules can have multiple names, the map will use the first name in the rule's name list as its key.
         * This way, each rule will be called only once for every permission name and context.
         *
         * If the rule associated to a given rule name is already in rulePermissionNames, the permission will be
         * appended to the associated array. Otherwise, the rule name is added to the map and its permission name array
         * is created.
         */
        PermissionService.prototype._addPermissionName = function(rulePermissionNames, ruleName, permissionName, permissionContext) {
            var rule = this._getRule(ruleName);

            if (!rule) {
                throw "Permission found but no rule found named: " + ruleName;
            }

            ruleName = rule.names[0];

            if (!rulePermissionNames.hasOwnProperty(ruleName)) {
                rulePermissionNames[ruleName] = [];
            }

            rulePermissionNames[ruleName].push({
                name: permissionName,
                context: permissionContext
            });
        };

        /**
         * This method returns the rule registered with the given name.
         *
         * @param {String} ruleName The name of the rule to lookup.
         *
         * @returns {Object} rule The rule with the given name, undefined otherwise.
         */
        PermissionService.prototype._getRule = function(ruleName) {
            return rules.find(function(rule) {
                return rule.names.indexOf(ruleName) > -1;
            });
        };

        PermissionService.prototype.getPermission = function(permissionName) {
            return permissions.find(function(permission) {
                return permission.aliases.indexOf(permissionName) > -1;
            });
        };

        PermissionService.prototype.unregisterDefaultRule = function() {
            var defaultRule = this._getRule(DEFAULT_RULE_NAME);

            if (defaultRule) {
                rules.splice(rules.indexOf(defaultRule), 1);
            }
        };

        PermissionService.prototype.registerPermission = function(permissionConfiguration) {
            this._validatePermission(permissionConfiguration);

            permissions.push({
                aliases: permissionConfiguration.aliases,
                rules: permissionConfiguration.rules
            });
        };

        PermissionService.prototype.hasCachedResult = function(ruleName, key) {
            return hasCacheRegion(ruleName) && getCacheRegion(ruleName).hasOwnProperty(key);
        };

        PermissionService.prototype.clearCache = function(eventType, authenticationPayload) {
            var needToClearCache = isBlank(authenticationPayload) || isBlank(authenticationPayload.userHasChanged) || !!authenticationPayload.userHasChanged;
            if (needToClearCache) {
                cachedResults = {};
            }
        };

        PermissionService.prototype.isPermitted = function(permissions) {
            var rulePermissionNames = this._mapRuleNameToPermissionNames(permissions);
            var rulePromises = this._getRulePromises.call(this, rulePermissionNames);

            var onSuccess = function(permissionResults) {
                this._updateCache(rulePermissionNames, permissionResults);
                return true;
            }.bind(this);

            var onError = function(result) {
                if (result === false) {
                    return result;
                } else {
                    $log.error(result);
                    return $q.reject(result);
                }
            };

            return $q.all(rulePromises).then(onSuccess, onError);
        };

        PermissionService.prototype._validationRule = function(ruleConfiguration) {
            ruleConfiguration.names.forEach(function(ruleName) {
                if (this._getRule(ruleName)) {
                    throw "Rule already exists: " + ruleName;
                }
            }.bind(this));
        };

        PermissionService.prototype._validatePermission = function(permissionConfiguration) {
            if (!(permissionConfiguration.aliases instanceof Array)) {
                throw "Permission aliases must be an array";
            }

            if (permissionConfiguration.aliases.length < 1) {
                throw "Permission requires at least one alias";
            }

            if (!(permissionConfiguration.rules instanceof Array)) {
                throw "Permission rules must be an array";
            }

            if (permissionConfiguration.rules.length < 1) {
                throw "Permission requires at least one rule";
            }

            permissionConfiguration.aliases.forEach(function(permissionName) {
                if (this.getPermission(permissionName)) {
                    throw "Permission already exists: " + permissionName;
                }

                if (!this._isPermissionNameValid(permissionName)) {
                    throw "Permission aliases must be prefixed with namespace and a full stop";
                }
            }.bind(this));

            permissionConfiguration.rules.forEach(function(ruleName) {
                if (!this._getRule(ruleName)) {
                    throw "Permission found but no rule found named: " + ruleName;
                }
            }.bind(this));
        };

        PermissionService.prototype._updateCache = function(rulePermissionNames, permissionResults) {
            Object.keys(permissionResults).forEach(function(ruleName) {
                var cacheKey = this._generateCacheKey(rulePermissionNames[ruleName]);
                var cacheValue = permissionResults[ruleName];

                this._addCachedResult(ruleName, cacheKey, cacheValue);
            }.bind(this));
        };

        PermissionService.prototype._addCachedResult = function(ruleName, key, result) {
            if (!hasCacheRegion(ruleName)) {
                cachedResults[ruleName] = {};
            }

            cachedResults[ruleName][key] = result;
        };

        PermissionService.prototype._registerRule = function(ruleConfiguration) {
            this._validationRule(ruleConfiguration);

            if (ruleConfiguration.names && ruleConfiguration.names.length && ruleConfiguration.names.indexOf(DEFAULT_RULE_NAME) > -1) {
                throw "Register default rule using permissionService.registerDefaultRule()";
            }

            rules.push({
                names: ruleConfiguration.names
            });
        };

        PermissionService.prototype._registerDefaultRule = function(ruleConfiguration) {
            this._validationRule(ruleConfiguration);

            if (ruleConfiguration.names && ruleConfiguration.names.length && ruleConfiguration.names.indexOf(DEFAULT_RULE_NAME) === -1) {
                throw "Default rule name must be DEFAULT_RULE_NAME";
            }

            rules.push({
                names: ruleConfiguration.names
            });
        };

        PermissionService.prototype._callRuleVerify = function(ruleKey, permissionNameObjs) {
            if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
                return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
            } else {
                //ask inner application for verify function.
                return this._remoteCallRuleVerify(ruleKey, permissionNameObjs);
            }
        };

        PermissionService.prototype._registerEventHandlers = function() {
            systemEventService.registerEventHandler(EVENTS.AUTHORIZATION_SUCCESS, this.clearCache.bind(this));
            systemEventService.registerEventHandler(EVENTS.EXPERIENCE_UPDATE, this.clearCache.bind(this));
            crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, this.clearCache.bind(this));
        };

        PermissionService.prototype._remoteCallRuleVerify = function() {};

        return new PermissionService();
    });
