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
describe('smarteditContainer permissionService', function() {

    var DUMMY_RULE_NAME = "dummyRuleName";
    var DUMMY_RULE_NAME1 = DUMMY_RULE_NAME + "1";
    var DUMMY_RULE_NAME2 = DUMMY_RULE_NAME + "2";
    var DUMMY_RULE_NAME3 = DUMMY_RULE_NAME + "3";

    var DUMMY_PERMISSION_NAME = "namepace.dummyPermissionName";
    var DUMMY_PERMISSION_NAME1 = DUMMY_PERMISSION_NAME + "1";
    var DUMMY_PERMISSION_NAME2 = DUMMY_PERMISSION_NAME + "2";
    var DUMMY_PERMISSION_NAME3 = DUMMY_PERMISSION_NAME + "3";
    var INVALID_PERMISSION_NAME = "namepace.invalidPermissionName";

    var DUMMY_PERMISSION_CONTEXT1 = {
        permission: "context1"
    };
    var DUMMY_PERMISSION_CONTEXT2 = {
        permission: "context2"
    };

    var gatewayProxy, permissionService, PermissionServiceInterface, SE_PERMISSION_SERVICE_GATEWAY_ID, DEFAULT_RULE_NAME, $q, $rootScope, $log;

    beforeEach(function() {
        jasmine.clock().install();
    });

    beforeEach(module('permissionServiceModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(inject(function(_permissionService_, _PermissionServiceInterface_, _SE_PERMISSION_SERVICE_GATEWAY_ID_, _DEFAULT_RULE_NAME_, _$q_, _$rootScope_, _$log_) {
        permissionService = _permissionService_;
        PermissionServiceInterface = _PermissionServiceInterface_;
        SE_PERMISSION_SERVICE_GATEWAY_ID = _SE_PERMISSION_SERVICE_GATEWAY_ID_;
        DEFAULT_RULE_NAME = _DEFAULT_RULE_NAME_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        $log = _$log_;
    }));

    afterEach(function() {
        jasmine.clock().uninstall();
    });

    it('PermissionService initializes and invokes gatewayProxy', function() {
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(permissionService, ["isPermitted", "clearCache", "registerPermission", "unregisterDefaultRule", "registerDefaultRule", "registerRule", "_registerRule", "_remoteCallRuleVerify", "_registerDefaultRule"], SE_PERMISSION_SERVICE_GATEWAY_ID);
    });

    describe('PermissionService.registerRule', function() {
        it('throws exception if Rule.names does not exist', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    verify: function() {}
                });
            }).toThrow("Rule names must be array");
        });

        it('throws exception if Rule.names is not of type Array', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    names: "not.an.array",
                    verify: function() {}
                });
            }).toThrow("Rule names must be array");
        });

        it('throws exception if Rule.names is empty', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    names: [],
                    verify: function() {}
                });
            }).toThrow("Rule requires at least one name");
        });

        it('throws exception if Rule does not have a verify function', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    names: [DUMMY_RULE_NAME]
                });
            }).toThrow("Rule requires a verify function");
        });

        it('throws exception if Rule.verify is not a function', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    names: [DUMMY_RULE_NAME],
                    verify: "notAFunction"
                });
            }).toThrow("Rule verify must be a function");
        });

        it('throws exception if Rule exists with same name', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            });

            // When/Then
            expect(function() {
                permissionService.registerRule({
                    names: [DUMMY_RULE_NAME],
                    verify: function() {}
                });
            }).toThrow("Rule already exists: " + DUMMY_RULE_NAME);
        });

        it('throws exception if trying to register default rule', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerRule({
                    names: [DEFAULT_RULE_NAME],
                    verify: function() {}
                });
            }).toThrow("Register default rule using permissionService.registerDefaultRule()");
        });

        it('adds Rule to PermissionService', function() {

            // Given
            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            };

            //When
            permissionService.registerRule(dummyRule);

            // Then
            expect(permissionService._getRule(DUMMY_RULE_NAME)).toEqual(jasmine.objectContaining(dummyRule));
        });
    });

    describe('PermissionService.registerDefaultRule', function() {
        it('adds default rule to PermissionService', function() {

            // Given
            var dummyDefaultRule = {
                names: [DEFAULT_RULE_NAME],
                verify: function() {}
            };

            // When
            permissionService.registerDefaultRule(dummyDefaultRule);

            // Then
            expect(permissionService._getRule(DEFAULT_RULE_NAME)).toEqual(jasmine.objectContaining(dummyDefaultRule));
        });

        it('throws exception if default rule name is not DEFAULT_RULE_NAME', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerDefaultRule({
                    names: [DUMMY_RULE_NAME],
                    verify: function() {}
                });
            }).toThrow("Default rule name must be DEFAULT_RULE_NAME");
        });
    });

    describe('PermissionService.unregisterDefaultRule', function() {
        it('removes the default rule', function() {

            // Given
            permissionService.registerDefaultRule({
                names: [DEFAULT_RULE_NAME],
                verify: function() {}
            });

            // When
            permissionService.unregisterDefaultRule();

            // Then
            expect(permissionService._getRule(DEFAULT_RULE_NAME)).toBeUndefined();
        });
    });

    describe('PermissionService.registerPermission', function() {
        it('throws exception if Permission does not have names parameter', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    rules: [DUMMY_RULE_NAME]
                });
            }).toThrow("Permission aliases must be an array");
        });

        it('throws exception if parameter names is not of type Array', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: "not.an.array",
                    rules: []
                });
            }).toThrow("Permission aliases must be an array");
        });

        it('throws exception if Permission.names is empty', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: [],
                    rules: [DUMMY_RULE_NAME]
                });
            }).toThrow("Permission requires at least one alias");
        });

        it('throws exception if Permission does not have rules parameter', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: [DUMMY_PERMISSION_NAME]
                });
            }).toThrow("Permission rules must be an array");
        });

        it('throws exception if parameter rules is not of type Array', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: [DUMMY_PERMISSION_NAME],
                    rules: "not.an.array"
                });
            }).toThrow("Permission rules must be an array");
        });

        it('throws exception if Permission.rules is empty', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: [DUMMY_PERMISSION_NAME],
                    rules: []
                });
            }).toThrow("Permission requires at least one rule");
        });

        it('throws exception if permission exists with same name', function() {

            // Given
            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            };

            permissionService.registerRule(dummyRule);

            var dummyPermission = {
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            };

            permissionService.registerPermission(dummyPermission);

            // When/Then
            expect(function() {
                permissionService.registerPermission(dummyPermission);
            }).toThrow("Permission already exists: " + DUMMY_PERMISSION_NAME);
        });

        it('throws exception if permission name is not a name space', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: ["iAmNotNameSpaced"],
                    rules: [DUMMY_RULE_NAME]
                });
            }).toThrow("Permission aliases must be prefixed with namespace and a full stop");
        });

        it('throws exception if no rule is registered with one of the given rule names', function() {

            // Given/When/Then
            expect(function() {
                permissionService.registerPermission({
                    aliases: [DUMMY_PERMISSION_NAME],
                    rules: [DUMMY_RULE_NAME]
                });
            }).toThrow("Permission found but no rule found named: " + DUMMY_RULE_NAME);
        });

        it('adds permission to PermissionService', function() {

            // Given
            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            };

            permissionService.registerRule(dummyRule);

            var dummyPermission = {
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            };

            // When
            permissionService.registerPermission(dummyPermission);

            // Then
            expect(permissionService.getPermission(DUMMY_PERMISSION_NAME)).toEqual(dummyPermission);
        });
    });

    describe('PermissionService.isPermitted', function() {
        it('throws exception when a permission has no names', function() {

            // Given/Then/When
            expect(function() {
                permissionService.isPermitted([{}]);
            }).toThrow("Requested Permission requires at least one name");
        });

        it('throws error when a rule is not found', function() {

            // Given
            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            };

            permissionService.registerRule(dummyRule);

            var dummyPermission = {
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            };

            permissionService.registerPermission(dummyPermission);

            var rule = permissionService._getRule(DUMMY_RULE_NAME);
            rule.names = [];

            // When/Then
            expect(function() {
                permissionService.isPermitted([{
                    names: [DUMMY_PERMISSION_NAME]
                }]);
            }).toThrow("Permission found but no rule found named: " + DUMMY_RULE_NAME);
        });

        it('returns true when permission exists with verified rule', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME],
                verify: function() {
                    return $q.when(true);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            // Then
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('returns false when permission contains no rules', function() {

            // Given
            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            };

            permissionService.registerRule(dummyRule);

            var dummyPermission = {
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            };

            permissionService.registerPermission(dummyPermission);

            dummyPermission.rules.pop();

            // When/Then
            expect(function() {
                permissionService.isPermitted([{
                    names: [DUMMY_PERMISSION_NAME]
                }]);
            }).toThrow("Permission has no rules");
        });

        it('returns false when at least one rule is false', function() {

            // Given
            var DUMMY_RULE_NAME2_FALSE = DUMMY_RULE_NAME2 + "_false";

            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q.when(true);
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME2_FALSE],
                verify: function() {
                    return $q.when(false);
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME3],
                verify: function() {
                    return $q.when(true);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2_FALSE, DUMMY_RULE_NAME3]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            // Then
            expect(isPermitted).toBeResolvedWithData(false);
        });

        it('returns true when two permissions are requested whose rules are true', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q.when(true);
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME2],
                verify: function() {
                    return $q.when(true);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1],
                rules: [DUMMY_RULE_NAME1]
            });
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME2]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1]
            }, {
                names: [DUMMY_PERMISSION_NAME2]
            }]);

            // Then
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('returns false when at least one permission requested has a false rule', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q.when(true);
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME2],
                verify: function() {
                    return $q.when(true);
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME3],
                verify: function() {
                    return $q.when(false);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1],
                rules: [DUMMY_RULE_NAME1]
            });
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME2]
            });
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME3],
                rules: [DUMMY_RULE_NAME3]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1]
            }, {
                names: [DUMMY_PERMISSION_NAME2]
            }, {
                names: [DUMMY_PERMISSION_NAME3]
            }]);

            // Then
            expect(isPermitted).toBeResolvedWithData(false);
        });

        it('returns false when requested permission does not exist and no default rule is registered', function() {
            expect(function() {
                permissionService.isPermitted([{
                    names: [INVALID_PERMISSION_NAME]
                }]);
            }).toThrow("Permission has no rules");
        });

        it('returns true when requested permission exists and called with permission name as a parameter', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME,
                context: undefined
            }]);
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('is called with two permissions, referencing 1 rule, then rule.verify is called once with with 2 permission name', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2]
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME1,
                context: undefined
            }, {
                name: DUMMY_PERMISSION_NAME2,
                context: undefined
            }]);
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('is called with one permission, referencing 1 rule, then rule.verify is called once with with 1 permission name', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME2]
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME2,
                context: undefined
            }]);
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('only calls the same rule.verify with a given permission name once for a given context', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME],
                context: DUMMY_PERMISSION_CONTEXT1
            }, {
                names: [DUMMY_PERMISSION_NAME],
                context: DUMMY_PERMISSION_CONTEXT1
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME,
                context: DUMMY_PERMISSION_CONTEXT1
            }]);
        });

        it('calls the same rule.verify with a given permission name more than once if the contexts associated to the permission names are different', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME],
                context: DUMMY_PERMISSION_CONTEXT1
            }, {
                names: [DUMMY_PERMISSION_NAME],
                context: DUMMY_PERMISSION_CONTEXT2
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME,
                context: DUMMY_PERMISSION_CONTEXT1
            }, {
                name: DUMMY_PERMISSION_NAME,
                context: DUMMY_PERMISSION_CONTEXT2
            }]);
        });

        it('calls two rules with different permission names but the same context if it is shared', function() {

            // Given
            var verify1 = jasmine.createSpy("verify").and.returnValue($q.when(true));
            var verify2 = jasmine.createSpy("verify").and.returnValue($q.when(true));
            var dummyRule1 = {
                names: [DUMMY_RULE_NAME1],
                verify: verify1
            };

            var dummyRule2 = {
                names: [DUMMY_RULE_NAME2],
                verify: verify2
            };

            permissionService.registerRule(dummyRule1);
            permissionService.registerRule(dummyRule2);

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1],
                rules: [DUMMY_RULE_NAME1]
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME2]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2],
                context: DUMMY_PERMISSION_CONTEXT1
            }]);

            // Then
            expect(verify1).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME1,
                context: DUMMY_PERMISSION_CONTEXT1
            }]);
            expect(verify2).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME2,
                context: DUMMY_PERMISSION_CONTEXT1
            }]);
        });

        it('returns true when requested permission does not exist but default rule is registered and called with permission name as a parameter', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyDefaultRule = {
                names: [DEFAULT_RULE_NAME],
                verify: verify
            };

            permissionService.registerDefaultRule(dummyDefaultRule);

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            // Then
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME,
                context: undefined
            }]);
            expect(isPermitted).toBeResolvedWithData(true);
        });

        it('only calls a rule once when multiple permissions use the same rule', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1],
                rules: [DUMMY_RULE_NAME]
            });
            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1]
            }, {
                names: [DUMMY_PERMISSION_NAME2]
            }]);

            // Then
            expect(isPermitted).toBeResolvedWithData(true);
            expect(verify).toHaveBeenCalledTimes(1);
        });

        it('only calls a rule once when multiple permissions reference the same rule with different names', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2],
                verify: verify
            };

            permissionService.registerRule(dummyRule);

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME1],
                rules: [DUMMY_RULE_NAME1]
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME2],
                rules: [DUMMY_RULE_NAME2]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME1, DUMMY_PERMISSION_NAME2]
            }]);

            // Then
            expect(verify).toHaveBeenCalledTimes(1);
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME1,
                context: undefined
            }, {
                name: DUMMY_PERMISSION_NAME2,
                context: undefined
            }]);
        });

        it('returns false when one asynchronous rule returns false before the other rules return', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q(function(resolve) {
                        setTimeout(function() {
                            resolve(true);
                        }, 5000);
                    });
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME2],
                verify: function() {
                    return $q(function(resolve) {
                        setTimeout(function() {
                            resolve(false);
                        }, 1000);
                    });
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            jasmine.clock().tick(2000);

            // Then
            expect(isPermitted).toBeResolvedWithData(false);
        });

        it('returns error message when one asynchronous rule rejects before the other rules return', function() {

            // Given
            var ERROR_MESSAGE = "error.message";

            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q(function(resolve) {
                        setTimeout(function() {
                            resolve(true);
                        }, 5000);
                    });
                }
            });
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME2],
                verify: function() {
                    return $q(function(resolve, reject) {
                        setTimeout(function() {
                            reject(ERROR_MESSAGE);
                        }, 1000);
                    });
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            jasmine.clock().tick(2000);

            // Then
            expect(isPermitted).toBeRejectedWithData(ERROR_MESSAGE);
        });

        it('caches the result of the first call for the same permission and returns the cached result on the second call', function() {

            // Given
            var verify = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule = {
                names: [DUMMY_RULE_NAME],
                verify: verify
            };

            permissionService.registerRule(dummyRule);

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            // When
            var isPermitted = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            $rootScope.$apply();

            var isPermittedCached = permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            // Then
            // Non-Cached
            expect(isPermitted).toBeResolvedWithData(true);
            expect(verify).toHaveBeenCalledTimes(1);
            expect(verify).toHaveBeenCalledWith([{
                name: DUMMY_PERMISSION_NAME,
                context: undefined
            }]);

            // Cached
            expect(isPermittedCached).toBeResolvedWithData(true);
        });

        it('console logs error message when a rule is rejected', function() {
            // Given
            var ERROR_MESSAGE = "error.message";
            spyOn($log, 'error');

            permissionService.registerRule({
                names: [DUMMY_RULE_NAME1],
                verify: function() {
                    return $q.reject(ERROR_MESSAGE);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME1]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            $rootScope.$apply();

            // Then
            expect($log.error).toHaveBeenCalledWith(ERROR_MESSAGE);
        });
    });

    describe('PermissionService._generateCacheKey', function() {
        it('returns a valid cache key', function() {

            // Given
            var permissions = [{
                name: DUMMY_PERMISSION_NAME1,
                context: {
                    key: "value"
                }
            }, {
                name: DUMMY_PERMISSION_NAME2,
                context: undefined
            }];

            //When
            var key = permissionService._generateCacheKey(permissions);

            // Then
            expect(key).toEqual(JSON.stringify(permissions));
        });

        it('returns the same key for a given set of permission names, regardless of the order', function() {

            // Given
            var permission1 = {
                name: DUMMY_PERMISSION_NAME1,
                context: {
                    key: "value"
                }
            };

            var permission2 = {
                name: DUMMY_PERMISSION_NAME2,
                context: undefined
            };

            var permissions1 = [permission1, permission2];
            var permissions2 = [permission2, permission1];

            //When
            var key1 = permissionService._generateCacheKey(permissions1);
            var key2 = permissionService._generateCacheKey(permissions2);

            // Then
            expect(key1).toEqual(key2);
        });
    });

    describe('PermissionService.hasCachedResult', function() {
        it('returns false when no cache section exists for a given rule', function() {

            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME],
                verify: function() {}
            });

            var rule = permissionService._getRule(DUMMY_RULE_NAME);

            // When
            var hasCachedResult = permissionService.hasCachedResult(rule, null);

            // Then
            expect(hasCachedResult).toBe(false);
        });

        it('returns true when a cache section exists for a given rule and a result exists with the given key', function() {
            // Given
            permissionService.registerRule({
                names: [DUMMY_RULE_NAME],
                verify: function() {
                    return $q.when(true);
                }
            });

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME]
            });

            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            $rootScope.$apply();

            var key = permissionService._generateCacheKey([{
                name: DUMMY_PERMISSION_NAME
            }]);

            // When
            var hasCachedResult = permissionService.hasCachedResult(DUMMY_RULE_NAME, key);

            // Then
            expect(hasCachedResult).toBe(true);
        });
    });

    describe('PermissionService.clearCache', function() {
        it('removes all cached values', function() {

            // Given
            var verify1 = jasmine.createSpy("verify").and.returnValue($q.when(true));
            var verify2 = jasmine.createSpy("verify").and.returnValue($q.when(true));

            var dummyRule1 = {
                names: [DUMMY_RULE_NAME1],
                verify: verify1
            };

            var dummyRule2 = {
                names: [DUMMY_RULE_NAME2],
                verify: verify2
            };

            permissionService.registerRule(dummyRule1);
            permissionService.registerRule(dummyRule2);

            permissionService.registerPermission({
                aliases: [DUMMY_PERMISSION_NAME],
                rules: [DUMMY_RULE_NAME1, DUMMY_RULE_NAME2]
            });

            // When
            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            $rootScope.$apply();

            permissionService.clearCache();

            permissionService.isPermitted([{
                names: [DUMMY_PERMISSION_NAME]
            }]);

            $rootScope.$apply();

            // Then
            expect(verify1).toHaveBeenCalledTimes(2);
            expect(verify2).toHaveBeenCalledTimes(2);
        });
    });
});
