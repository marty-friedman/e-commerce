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

function setupDirectiveTest() {

    setupBackendResponse = function($httpBackend, uri, response) {
        response = response || {};
        $httpBackend.whenGET(uri).respond(response);
    };

    templateSetup = function(template, $compile, $rootScope, scopeAugmentation) {
        directiveScope = $rootScope.$new();
        window.smarteditJQuery.extend(directiveScope, scopeAugmentation || {});

        element = angular.element(template);
        $compile(element)(directiveScope);

        $rootScope.$digest();
        expect(element.scope()).toBe(directiveScope);
        window.smarteditJQuery('body').append(element);
        return element;
    };

};
beforeEach(setupDirectiveTest);

/*
 * This setup method imports custom matchers, such as matchers for Promises like
 * "toBeResolved".
 */
function customMatchers() {

    module('yLoDashModule');
    module('yjqueryModule');
    module('configModule');
    module('templateCacheDecoratorModule');

    var PromiseMatcherHelper = {
        states: {
            RESOLVED: 'resolved',
            REJECTED: 'rejected'
        },
        getPromiseInfo: function(promise) {
            var that = this;
            var rootScope;
            angular.mock.inject(function($injector) {
                rootScope = $injector.get('$rootScope');
            });

            var promiseInfo = {};
            promise.then(function(data) {
                promiseInfo.status = that.states.RESOLVED;
                promiseInfo.data = data;
            }, function(data) {
                promiseInfo.status = that.states.REJECTED;
                promiseInfo.data = data;
            });

            rootScope.$apply(); // Trigger promise resolution
            return promiseInfo;
        },
        getMessageForPromise: function(promiseInfo, expected) {
            return function() {
                var unresolvedMessage = 'Expected promise to be ' + promiseInfo.status;
                var badDataMessage = 'Expected promise resolved data ' + jasmine.pp(promiseInfo.data) + ' to be ' + jasmine.pp(expected);
                return promiseInfo.status !== PromiseMatcherHelper.states.RESOLVED ? unresolvedMessage : badDataMessage;
            };
        }
    };

    jasmine.addMatchers({
        toEqualData: function(util, customEqualityTesters) {
            return {
                compare: function(actual, expected) {
                    var passed = angular.equals(actual, expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + actual + (passed ? '' : ' not') + ' to equal ' + expected
                    };
                }
            };
        },
        toHaveClass: function(util, customEqualityTesters) {
            return {
                compare: function(element, className) {
                    var passed = element.hasClass(className);

                    return {
                        pass: passed,
                        message: 'Expected ' + element + (passed ? '' : ' not') + ' to have class ' + className
                    };
                }
            };
        },
        fail: function(util, customEqualityTesters) {
            return {
                compare: function(actual, errorMessage) {
                    return {
                        pass: false,
                        message: errorMessage
                    };
                }
            };
        },
        toHaveThatManyAlerts: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert span").length;
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + element + (passed ? '' : ' not') + ' to have ' + expected + ' alert(s)'
                    };
                }
            };
        },
        messageToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert-success span").text();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected message' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        alertToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div.alert-danger span").text();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected alert' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        toContainChildElement: function(util, customEqualityTesters) {
            return {
                compare: function(element, childElement) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.find(childElement).length === 1);

                    return {
                        pass: passed,
                        message: 'Expected ' + html + (passed ? '' : ' not') + ' to have child element <' + childElement + '>'
                    };
                }
            };
        },
        toContainElementText: function(util, customEqualityTesters) {
            return {
                compare: function(element, text) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.text().indexOf(text) >= 0);

                    return {
                        pass: passed,
                        message: html + (passed ? ' contains' : ' doesn\'t contain') + ' text "' + text + '"'
                    };
                }
            };
        },
        inputToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find("div input[type=text]").val();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected input' + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        displayToBe: function(util, customEqualityTesters) {
            return {
                compare: function(element, expected) {
                    var actual = element.find('span').html();
                    var passed = (actual === expected);

                    return {
                        pass: passed,
                        message: 'Expected ' + actual + (passed ? '' : ' not') + ' to be ' + expected
                    };
                }
            };
        },
        flagToBeTrue: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/tick.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be true'
                    };
                }
            };
        },
        flagToBeFalse: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/no-tick.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be false'
                    };
                }
            };
        },
        flagToBeUndetermined: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> input[src='http/images/question.png']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected flag' + (passed ? '' : ' not') + ' to be undetermined'
                    };
                }
            };
        },
        toBeInEditMode: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("> div > input[type=text][data-ng-model='editor.temp']").length === 1);

                    return {
                        pass: passed,
                        message: 'Expected' + (passed ? '' : ' not') + ' to be in edit mode'
                    };
                }
            };
        },
        calendarToBeDisplayed: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (element.find("ul.dropdown-menu").css('display') === 'block');

                    return {
                        pass: passed,
                        message: 'Expected calendar' + (passed ? '' : ' not') + ' to be displayed'
                    };
                }
            };
        },
        toHaveAttribute: function(util, customEqualityTesters) {
            return {
                compare: function(element, attributeName, attributeValue) {
                    var html = window.smarteditJQuery("<div>").append(element.clone()).html();
                    var passed = (element.attr(attributeName) === attributeValue);

                    return {
                        pass: passed,
                        message: 'Expected ' + html + (passed ? '' : ' not') + ' to have attribute ' + attributeName + ' as ' + attributeValue
                    };
                }
            };
        },
        toBeRejected: function(util, customEqualityTesters) {
            return {
                compare: function(promise) {
                    var passed = (PromiseMatcherHelper.getPromiseInfo(promise).status === PromiseMatcherHelper.states.REJECTED);

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be rejected'
                    };
                }
            };
        },
        toBeResolved: function(util, customEqualityTesters) {
            return {
                compare: function(promise) {
                    var passed = (PromiseMatcherHelper.getPromiseInfo(promise).status === PromiseMatcherHelper.states.RESOLVED);

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be resolved'
                    };
                }
            };
        },
        toBeRejectedWithData: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var errorMessage = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.REJECTED && angular.equals(promiseInfo.data, expected));

                    return {
                        pass: passed,
                        message: errorMessage
                    };
                }
            };
        },
        toBeResolvedWithData: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var errorMessage = PromiseMatcherHelper.getMessageForPromise(promiseInfo, expected);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.RESOLVED && angular.equals(promiseInfo.data, expected));

                    return {
                        pass: passed,
                        message: errorMessage
                    };
                }
            };
        },
        toExist: function(util, customEqualityTesters) {
            return {
                compare: function(element) {
                    var passed = (window.smarteditJQuery(element).length > 0);

                    return {
                        pass: passed,
                        message: 'Expected element' + (passed ? '' : ' not') + ' to exist'
                    };
                }
            };
        },
        toBeRejectedWithDataContaining: function(util, customEqualityTesters) {
            return {
                compare: function(promise, expected) {
                    var promiseInfo = PromiseMatcherHelper.getPromiseInfo(promise);
                    var passed = (promiseInfo.status === PromiseMatcherHelper.states.REJECTED && promiseInfo.data.some(function(actual) {
                        return angular.equals(actual, expected);
                    }));

                    return {
                        pass: passed,
                        message: 'Expected promise' + (passed ? '' : ' not') + ' to be rejected with data containing ' + expected
                    };
                }
            };
        },
        toBePromise: function(util, customEqualityTesters) {
            return {
                compare: function(object) {
                    var passed = !!object.then;

                    return {
                        pass: passed,
                        message: 'Expected ' + object + (passed ? '' : ' not') + ' to be promise'
                    };
                }
            };
        },
        toBeEmptyFunction: function(util, customEqualityTesters) {
            return {
                compare: function(actual) {

                    //FIXME: find a way to reuse the actual isFunctionEmpty from functionsModule
                    var passed = (typeof actual === 'function' && (actual.toString().match(/\{([\s\S]*)\}/m)[1].trim() === '' || /proxyFunction/g.test(actual.toString().replace(/\s/g, ""))));

                    return {
                        pass: passed,
                        message: 'Expected ' + actual + (passed ? '' : ' not') + ' to be an empty function'
                    };
                }
            };
        }
    });
};


beforeEach(customMatchers);

beforeEach(function() {
    angular.module('translationServiceModule', []);
});

beforeEach(module('translationServiceModule', function($provide) {

    var i18nMap = {};

    var translateMock = function(key) {
        return {
            then: function(callback) {
                return callback(i18nMap[key]);
            },

        };
    };

    translateMock.storageKey = function() {};
    translateMock.storage = function() {};
    translateMock.preferredLanguage = function() {};

    $provide.value("$translate", translateMock);

    $provide.constant("UNDEFINED_LOCALE", 'UNDEFINED');
}));
