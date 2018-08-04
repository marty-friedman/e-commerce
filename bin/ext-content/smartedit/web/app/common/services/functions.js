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
 * @ngdoc service
 * @name functionsModule
 *
 * @description
 * provides a list of useful functions that can be used as part of the SmartEdit framework.
 */
angular.module('functionsModule', ['yjqueryModule', 'yLoDashModule', 'ngSanitize'])

    .factory('ParseError', function() {
        var ParseError = function(value) {
            this.value = value;
        };
        return ParseError;
    })


    /**
     * @ngdoc service
     * @name functionsModule.getResourcePath
     *
     * @description 
     * makes url absolute (with provided domain) if not yet
     * 
     * @param {String} domain the domain with witch to prepend the url if not absolute
     * @param {String} url the url to tests
     */
    .factory('getAbsoluteURL', function() {
        return function(domain, url) {
            // url regex
            // scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
            var re = new RegExp(
                "([a-zA-Z0-9]+://)" + // scheme
                "([a-zA-Z0-9_]+:[a-zA-Z0-9_]+@)?" + // user:password
                "([a-zA-Z0-9.-]+\\.[A-Za-z]{2,4})" + // hostname
                "|([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)" + // or ip
                "(:[0-9]+)?" + // port
                "(/.*)?" // everything else
            );

            if (re.exec(url)) {
                return url;
            } else {
                return domain + url;
            }
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.getOrigin
     *
     * @description
     * returns document location origin
     * Some browsers still do not support W3C document.location.origin, this function caters for gap.
     * 
     * @param {String =} url optional any url
     */
    .factory('getOrigin', function() {
        return function(url) {
            if (url) {
                var link = document.createElement('a');
                link.setAttribute('href', url);
                var origin = link.protocol + "//" + link.hostname + (link.port ? ':' + link.port : '');
                link = null; // GC
                return origin;
            } else {
                return window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
            }

        };
    })
    /**
     * @ngdoc service
     * @name functionsModule.isBlank
     *
     * @description
     * <b>isBlank</b> will check if a given string is undefined or null or empty.
     * - returns TRUE for undefined / null/ empty string
     * - returns FALSE otherwise
     *
     * @param {String} inputString any input string.
     *
     * @returns {boolean} true if the string is null else false
     */
    .factory('isBlank', function() {
        return function(value) {
            return (typeof value === 'undefined' || value === null || value === "null" || value.toString().trim().length === 0);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.extend
     *
     * @description
     * <b>extend</b> provides a convenience to either default a new child or "extend" an existing child with the prototype of the parent
     *
     * @param {Class} ParentClass which has a prototype you wish to extend.
     * @param {Class} ChildClass will have its prototype set.
     *
     * @returns {Class} ChildClass which has been extended
     */
    .factory('extend', function() {
        return function(ParentClass, ChildClass) {
            if (!ChildClass) {
                ChildClass = function() {};
            }
            ChildClass.prototype = Object.create(ParentClass.prototype);
            return ChildClass;
        };
    })

    /**
     * @deprecated since 6.6, use {@link https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind bind} instead
     * @ngdoc service
     * @name functionsModule.hitch
     *
     * @description
     * <b>hitch</b> will create a new function that will pass our desired context (scope) to the given function.
     * This method will also pre-bind the given parameters.
     *
     * @param {Object} scope scope that is to be assigned.
     * @param {Function} method the method that needs binding.
     *
     * @returns {Function} a new function thats binded to the given scope
     */
    .factory('hitch', function() {
        return function(scope, method) {

            var argumentArray = Array.prototype.slice.call(arguments); // arguments is not an array
            // (from  http://www.sitepoint.com/arguments-a-javascript-oddity/)

            var preboundArguments = argumentArray.slice(2);

            return function lockedMethod() {

                // from here, "arguments" are the arguments passed to lockedMethod

                var postBoundArguments = Array.prototype.slice.call(arguments);

                return method.apply(scope, preboundArguments.concat(postBoundArguments));

            };

        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.customTimeout
     *
     * @description
     * <b>customTimeout</b> will call the javascrit's native setTimeout method to execute a given function after a specified period of time.
     * This method is better than using $timeout since it is difficult to assert on $timeout during end-to-end testing.
     *
     * @param {Function} func function that needs to be executed after the specified duration.
     * @param {Number} duration time in milliseconds.
     */
    .factory('customTimeout', function($rootScope) {
        return function(func, duration) {
            setTimeout(function() {
                func();
                $rootScope.$digest();
            }, duration);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.copy
     *
     * @description
     * <b>copy</b> will do a deep copy of the given input object.
     *
     * @param {Object} candidate the javaScript value that needs to be deep copied.
     *
     * @returns {Object} A deep copy of the input
     */
    .factory('copy', function() {
        return function(candidate) {
            return JSON.parse(JSON.stringify(candidate));
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.merge
     *
     * @description
     * <b>merge</b> will merge the contents of two objects together into the first object.
     *
     * @param {Object} target any JavaScript object.
     * @param {Object} source any JavaScript object.
     *
     * @returns {Object} a new object as a result of merge
     */
    .factory('merge', function(yjQuery) {
        return function(source, target) {

            yjQuery.extend(source, target);

            return source;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.getQueryString
     *
     * @description
     * <b>getQueryString</b> will convert a given object into a query string.
     *
     * Below is the code snippet for sample input and sample output:
     *
     * <pre>
     * var params = {
     *  key1 : 'value1',
     *  key2 : 'value2',
     *  key3 : 'value3'
     *  }
     *
     *  var output = getQueryString(params);
     *
     *  // The output is '?&key1=value1&key2=value2&key3=value3'
     *
     *</pre>
     *
     * @param {Object} params Object containing a list of params.
     *
     * @returns {String} a query string
     */
    .factory('getQueryString', function() {
        return function(params) {

            var queryString = "";
            if (params) {
                for (var param in params) {
                    queryString += '&' + param + "=" + params[param];
                }
            }
            return "?" + queryString;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.getURI
     *
     * @description
     * Will return the URI part of a URL
     * @param {String} url the URL the URI of which is to be returned
     */
    .factory('getURI', function() {
        return function(url) {
            return url && url.indexOf("?") > -1 ? url.split("?")[0] : url;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.parseQuery
     *
     * @description
     * <b>parseQuery</b> will convert a given query string to an object.
     *
     * Below is the code snippet for sample input and sample output:
     *
     * <pre>
     * var query = '?key1=value1&key2=value2&key3=value3';
     *
     * var output = parseQuery(query);
     *
     * // The output is { key1 : 'value1', key2 : 'value2', key3 : 'value3' }
     *
     *</pre>
     *
     * @param {String} query String that needs to be parsed.
     *
     * @returns {Object} an object containing all params of the given query
     */
    .factory('parseQuery', function() {
        return function(str) {

            var objURL = {};

            str.replace(new RegExp("([^?=&]+)(=([^&]*))?", "g"), function($0, $1, $2, $3) {
                objURL[$1] = $3;
            });
            return objURL;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.trim
     *
     * @description
     * <b>trim</b> will remove spaces at the beginning and end of a given string.
     *
     * @param {String} inputString any input string.
     *
     * @returns {String} the newly modified string without spaces at the beginning and the end
     */
    .factory('trim', function() {

        return function(aString) {
            var regExpBeginning = /^\s+/;
            var regExpEnd = /\s+$/;
            return aString.replace(regExpBeginning, "").replace(regExpEnd, "");
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.convertToArray
     *
     * @description
     * <b>convertToArray</b> will convert the given object to array.
     * The output array elements are an object that has a key and value,
     * where key is the original key and value is the original object.
     *
     * @param {Object} inputObject any input object.
     *
     * @returns {Array} the array created from the input object
     */
    .factory('convertToArray', function() {

        return function(object) {
            var configuration = [];
            for (var key in object) {
                if (key.indexOf('$') !== 0 && key.indexOf('toJSON') !== 0) {
                    configuration.push({
                        key: key,
                        value: object[key]
                    });
                }
            }
            return configuration;
        };

    })

    /**
     * @ngdoc service
     * @name functionsModule.injectJS
     *
     * @description
     * <b>injectJS</b> will inject script tags into html for a given set of sources.
     *
     */
    .factory('injectJS', function() {

        function getInjector() {
            return $script;
        }

        return {
            getInjector: getInjector,

            /**
             * @ngdoc method
             * @name functionsModule.injectJS#execute
             * @methodOf functionsModule.injectJS
             *
             * @description
             * <b>execute</b> will extract a given set of sources from the provided configuration object
             * and then inject each source as a JavaScript source tag and potential callbacks once all the
             * sources are wired.
             *
             * @param {Object} configuration - a given set of configurations.
             * @param {Array} configuration.sources - an array of sources that needs to be added.
             * @param {Function} configuration.callback - Callback to be triggered once all the sources are wired.
             */
            execute: function(conf) {
                var srcs = conf.srcs;
                var index = conf.index;
                var callback = conf.callback;
                if (index === undefined) {
                    index = 0;
                }
                if (srcs[index] !== undefined) {
                    this.getInjector()(srcs[index], function() {
                        if (index + 1 < srcs.length) {
                            this.execute({
                                srcs: srcs,
                                index: index + 1,
                                callback: callback
                            });
                        } else if (typeof callback === 'function') {
                            callback();
                        }
                    }.bind(this));

                }
            }
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.uniqueArray
     *
     * @description
     * <b>uniqueArray</b> will return the first Array argument supplemented with new entries from the second Array argument.
     *
     * @param {Array} array1 any JavaScript array.
     * @param {Array} array2 any JavaScript array.
     */
    .factory('uniqueArray', function() {

        return function(array1, array2) {

            array2.forEach(function(instance) {
                if (array1.indexOf(instance) === -1) {
                    array1.push(instance);
                }
            });

            return array1;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.regExpFactory
     *
     * @description
     * <b>regExpFactory</b> will convert a given pattern into a regular expression.
     * This method will prepend and append a string with ^ and $ respectively replaces
     * and wildcards (*) by proper regex wildcards.
     *
     * @param {String} pattern any string that needs to be converted to a regular expression.
     *
     * @returns {RegExp} a regular expression generated from the given string.
     *
     */
    .factory('regExpFactory', function() {

        return function(pattern) {

            var onlyAlphanumericsRegex = new RegExp(/^[a-zA-Z\d]+$/i);
            var antRegex = new RegExp(/^[a-zA-Z\d\*]+$/i);

            var regexpKey;
            if (onlyAlphanumericsRegex.test(pattern)) {
                regexpKey = ['^', '$'].join(pattern);
            } else if (antRegex.test(pattern)) {
                regexpKey = ['^', '$'].join(pattern.replace(/\*/, '.*'));
            } else {
                regexpKey = pattern;
            }

            return new RegExp(regexpKey, 'g');
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.generateIdentifier
     *
     * @description
     * <b>generateIdentifier</b> will generate a unique string based on system time and a random generator.
     *
     * @returns {String} a unique identifier.
     *
     */
    .factory('generateIdentifier', function() {
        return function() {
            var d = new Date().getTime();
            if (window.performance && typeof window.performance.now === "function") {
                d += window.performance.now(); //use high-precision timer if available
            }
            var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        };
    })




    /**
     * @ngdoc service
     * @name functionsModule.escapeHtml
     *
     * @description
     * <b>escapeHtml</b> will escape &, <, >, " and ' characters .
     *
     * @param {String} a string that needs to be escaped.
     *
     * @returns {String} the escaped string.
     *
     */
    .factory('escapeHtml', function() {
        return function(str) {
            if (typeof str === 'string') {
                return str.replace(/&/g, '&amp;')
                    .replace(/>/g, '&gt;')
                    .replace(/</g, '&lt;')
                    .replace(/"/g, '&quot;')
                    .replace(/'/g, '&apos;');
            } else {
                return str;
            }
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.sanitize
     *
     * @description
     * <b>escapes any harmful scripting from a string, leaves innocuous HTML untouched/b>
     *
     * @param {String} a string that needs to be sanitized.
     *
     * @returns {String} the sanitized string.
     *
     */
    .factory('sanitize', function(isBlank) {
        return function(str) {
            return !isBlank(str) ? str.replace(/(?=[()])/g, '\\') : str;
            //return $sanitize(string);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.sanitizeHTML
     *
     * @description
     * <b>sanitizeHTML</b> will remove breaks and space .
     *
     * @param {String} a string that needs to be escaped.
     *
     * @returns {String} the sanitized HTML.
     *
     */
    .factory('sanitizeHTML', function(isBlank) {
        return function(obj) {
            var result = angular.copy(obj);
            if (!isBlank(result)) {
                result = result.replace(/(\r\n|\n|\r)/gm, '').replace(/>\s+</g, '><').replace(/<\/br\>/g, '');
            }
            return result;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.toPromise
     *
     * @description
     * <b>toPromise</> transforms a function into a function that is guaranteed to return a Promise that resolves to the
     * original return value of the function, rejects with the rejected return value and rejects with an exceptino object when the invocation fails
     */
    .factory('toPromise', function($q, $log) {
        return function(method, context) {
            return function() {
                try {
                    return $q.when(method.apply(context, arguments));
                } catch (e) {
                    $log.error('execution of a method that was turned into a promise failed');
                    $log.error(e);
                    return $q.reject(e);
                }
            };
        };
    })


    /**
     * Checks if `value` is a function.
     *
     * @static
     * @category Objects
     * @param {*} value The value to check.
     * @returns {boolean} Returns `true` if the `value` is a function, else `false`.
     */
    .factory('isFunction', function() {
        return function(value) {
            return typeof value === 'function';
        };
    })

    // check if the value is the ECMAScript language type of Object
    .factory('isObject', function() {
        /** Used to determine if values are of the language type Object */
        var objectTypes = {
            'boolean': false,
            'function': true,
            'object': true,
            'number': false,
            'string': false,
            'undefined': false
        };
        return function(value) {
            return !!(value && objectTypes[typeof value]);
        };
    })

    /**
     * Creates a function that will delay the execution of `func` until after
     * `wait` milliseconds have elapsed since the last time it was invoked.
     * Provide an options object to indicate that `func` should be invoked on
     * the leading and/or trailing edge of the `wait` timeout. Subsequent calls
     * to the debounced function will return the result of the last `func` call.
     *
     * Note: If `leading` and `trailing` options are `true` `func` will be called
     * on the trailing edge of the timeout only if the the debounced function is
     * invoked more than once during the `wait` timeout.
     *
     * @static
     * @category Functions
     * @param {Function} func The function to debounce.
     * @param {number} wait The number of milliseconds to delay.
     * @param {Object} [options] The options object.
     * @param {boolean} [options.leading=false] Specify execution on the leading edge of the timeout.
     * @param {number} [options.maxWait] The maximum time `func` is allowed to be delayed before it's called.
     * @param {boolean} [options.trailing=true] Specify execution on the trailing edge of the timeout.
     * @returns {Function} Returns the new debounced function.
     * @example
     *
     * // avoid costly calculations while the window size is in flux
     * var lazyLayout = lodash.debounce(calculateLayout, 150);
     * yjQuery(window).on('resize', lazyLayout);
     *
     * // execute `sendMail` when the click event is fired, debouncing subsequent calls
     * yjQuery('#postbox').on('click', lodash.debounce(sendMail, 300, {
     *   'leading': true,
     *   'trailing': false
     * });
     *
     * // ensure `batchLog` is executed once after 1 second of debounced calls
     * var source = new EventSource('/stream');
     * source.addEventListener('message', lodash.debounce(batchLog, 250, {
     *   'maxWait': 1000
     * }, false);
     */
    .factory('debounce', function(isFunction, isObject) {
        function TypeError() {

        }

        return function(func, wait, options) {
            var args;
            var maxTimeoutId;
            var result;
            var stamp;
            var thisArg;
            var timeoutId;
            var trailingCall;
            var leading;
            var lastCalled = 0;
            var maxWait = false;
            var trailing = true;
            var isCalled;

            if (!isFunction(func)) {
                throw new TypeError();
            }
            wait = Math.max(0, wait) || 0;
            if (options === true) {
                leading = true;
                trailing = false;
            } else if (isObject(options)) {
                leading = options.leading;
                maxWait = 'maxWait' in options && (Math.max(wait, options.maxWait) || 0);
                trailing = 'trailing' in options ? options.trailing : trailing;
            }
            var delayed = function() {
                var remaining = wait - (Date.now() - stamp);
                if (remaining <= 0) {
                    if (maxTimeoutId) {
                        clearTimeout(maxTimeoutId);
                    }
                    isCalled = trailingCall;
                    maxTimeoutId = timeoutId = trailingCall = undefined;
                    if (isCalled) {
                        lastCalled = Date.now();
                        result = func.apply(thisArg, args);
                        if (!timeoutId && !maxTimeoutId) {
                            args = thisArg = null;
                        }
                    }
                } else {
                    timeoutId = setTimeout(delayed, remaining);
                }
            };

            var maxDelayed = function() {
                if (timeoutId) {
                    clearTimeout(timeoutId);
                }
                maxTimeoutId = timeoutId = trailingCall = undefined;
                if (trailing || (maxWait !== wait)) {
                    lastCalled = Date.now();
                    result = func.apply(thisArg, args);
                    if (!timeoutId && !maxTimeoutId) {
                        args = thisArg = null;
                    }
                }
            };

            return function() {
                args = arguments;
                stamp = Date.now();
                thisArg = this;
                trailingCall = trailing && (timeoutId || !leading);
                var leadingCall;
                var isCalled;

                if (maxWait === false) {
                    leadingCall = leading && !timeoutId;
                } else {
                    if (!maxTimeoutId && !leading) {
                        lastCalled = stamp;
                    }
                    var remaining = maxWait - (stamp - lastCalled);
                    isCalled = remaining <= 0;

                    if (isCalled) {
                        if (maxTimeoutId) {
                            maxTimeoutId = clearTimeout(maxTimeoutId);
                        }
                        lastCalled = stamp;
                        result = func.apply(thisArg, args);
                    } else if (!maxTimeoutId) {
                        maxTimeoutId = setTimeout(maxDelayed, remaining);
                    }
                }
                if (isCalled && timeoutId) {
                    timeoutId = clearTimeout(timeoutId);
                } else if (!timeoutId && wait !== maxWait) {
                    timeoutId = setTimeout(delayed, wait);
                }
                if (leadingCall) {
                    isCalled = true;
                    result = func.apply(thisArg, args);
                }
                if (isCalled && !timeoutId && !maxTimeoutId) {
                    args = thisArg = null;
                }
                return result;
            };
        };
    })

    /**
     * Creates a function that, when executed, will only call the `func` function
     * at most once per every `wait` milliseconds. Provide an options object to
     * indicate that `func` should be invoked on the leading and/or trailing edge
     * of the `wait` timeout. Subsequent calls to the throttled function will
     * return the result of the last `func` call.
     *
     * Note: If `leading` and `trailing` options are `true` `func` will be called
     * on the trailing edge of the timeout only if the the throttled function is
     * invoked more than once during the `wait` timeout.
     *
     * @static
     * @category Functions
     * @param {Function} func The function to throttle.
     * @param {number} wait The number of milliseconds to throttle executions to.
     * @param {Object} [options] The options object.
     * @param {boolean} [options.leading=true] Specify execution on the leading edge of the timeout.
     * @param {boolean} [options.trailing=true] Specify execution on the trailing edge of the timeout.
     * @returns {Function} Returns the new throttled function.
     * @example
     *
     * // avoid excessively updating the position while scrolling
     * var throttled = lodash.throttle(updatePosition, 100);
     * yjQuery(window).on('scroll', throttled);
     *
     * // execute `renewToken` when the click event is fired, but not more than once every 5 minutes
     * yjQuery('.interactive').on('click', lodash.throttle(renewToken, 300000, {
     *   'trailing': false
     * }));
     */
    .factory('throttle', function(debounce, isFunction, isObject) {
        return function(func, wait, options) {
            var leading = true;
            var trailing = true;

            if (!isFunction(func)) {
                throw new TypeError();
            }
            if (options === false) {
                leading = false;
            } else if (isObject(options)) {
                leading = 'leading' in options ? options.leading : leading;
                trailing = 'trailing' in options ? options.trailing : trailing;
            }
            options = {};
            options.leading = leading;
            options.maxWait = wait;
            options.trailing = trailing;

            return debounce(func, wait, options);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.parseHTML
     *
     * @description
     * parses a string HTML into a queriable DOM object, stripping any JavaScript from the HTML.
     *
     * @param {String} stringHTML, the string representation of the HTML to parse
     */
    .factory('parseHTML', function(yjQuery) {
        return function(stringHTML) {
            return yjQuery.parseHTML(stringHTML);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.unsafeParseHTML
     *
     * @description
     * parses a string HTML into a queriable DOM object, preserving any JavaScript present in the HTML.
     * Note - as this preserves the JavaScript present it must only be used on HTML strings originating
     * from a known safe location. Failure to do so may result in an XSS vulnerability.
     *
     * @param {String} stringHTML, the string representation of the HTML to parse
     */
    .factory('unsafeParseHTML', function(yjQuery) {
        return function(stringHTML) {
            return yjQuery.parseHTML(stringHTML, null, true);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.extractFromElement
     *
     * @description
     * parses a string HTML into a queriable DOM object
     *
     * @param {Object} parent, the DOM element from which we want to extract matching selectors
     * @param {String} extractionSelector, the yjQuery selector identifying the elements to be extracted
     */
    .factory('extractFromElement', function(yjQuery) {
        return function(parent, extractionSelector) {
            parent = yjQuery(parent);
            return parent.filter(extractionSelector).add(parent.find(extractionSelector));
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.closeOpenModalsOnBrowserBack
     *
     * @description
     * close any open modal window when a user clicks browser back button
     *
     * @param {Object} modalStack, the $modalStack service of angular-ui.
     */
    .factory('closeOpenModalsOnBrowserBack', function($uibModalStack) {
        return function() {
            if ($uibModalStack.getTop()) {
                $uibModalStack.dismissAll();
            }
        };
    })
    /**
     * @ngdoc service
     * @name functionsModule.service:URIBuilder
     *
     * @description
     * builder or URIs, build() method must be invoked to actually retrieve a URI
     *
     * @param {Object} modalStack, the $modalStack service of angular-ui.
     */
    .factory('URIBuilder', function(lodash) {

        function URIBuilder(uri) {

            this.uri = uri;

            this.build = function() {
                return this.uri;
            };

            /**
             * @ngdoc method
             * @name functionsModule.service:URIBuilder#replaceParams
             * @methodOf functionsModule.service:URIBuilder
             *
             * @description
             * Substitute all placeholders in the URI with the matching values in the given params
             *
             * @param {Object} params a map of placeholder names / values
             */
            this.replaceParams = function(params) {
                var clone = lodash.cloneDeep(this);
                if (params) {
                    //order the keys by descending length
                    var keys = Object.keys(params).sort(function(a, b) {
                        return b.length - a.length;
                    });
                    keys.forEach(function(key) {
                        var re = new RegExp('\\b' + key + '\\b');
                        clone.uri = clone.uri.replace(':' + key, params[key]).replace(re, params[key]);
                    });
                }
                return clone;
            };
        }

        return URIBuilder;
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:getDataFromResponse
     *
     * @description
     * when provided with a response returned from a backend call, will filter the response
     * to retrieve the data of interest.
     *
     * @param {Object} response, response returned from a backend call.
     * @returns {Array} Returns the array from the response.
     */
    .factory('getDataFromResponse', function() {
        return function(response) {
            var dataKey = Object.keys(response).filter(function(key) {
                return response[key] instanceof Array;
            })[0];

            return response[dataKey];
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:getKeyHoldingDataFromResponse
     *
     * @description
     * when provided with a response returned from a backend call, will filter the response
     * to retrieve the key holding the data of interest.
     *
     * @param {Object} response, response returned from a backend call.
     * @returns {String} Returns the name of the key holding the array from the response.
     */
    .factory('getKeyHoldingDataFromResponse', function() {
        return function(response) {
            var dataKey = Object.keys(response).filter(function(key) {
                return response[key] instanceof Array;
            })[0];

            return dataKey;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:resetObject
     *
     * @description
     * Resets a given object's properties' values
     *
     * @param {Object} targetObject, the object to reset
     * @param {Object} modelObject, an object that contains the structure that targetObject should have after a reset

     * @returns {Object} Returns the object that has been reset
     */
    .factory('resetObject', function(copy) {
        return function(targetObject, modelObject) {
            if (!targetObject) {
                targetObject = copy(modelObject);
            } else {
                for (var i in targetObject) {
                    delete targetObject[i];
                }
                angular.extend(targetObject, copy(modelObject));
            }

            return targetObject;
        };
    })
    /**
     * @ngdoc service
     * @name functionsModule.service:isFunctionEmpty
     *
     * @description
     * Will determine whether a function body is empty or should be considered empty for proxying purposes
     *
     * @param {Function} fn, the function to evaluate

     * @returns {Boolean} a boolean.
     */
    .factory('isFunctionEmpty', function() {
        return function(fn) {
            return fn.toString().match(/\{([\s\S]*)\}/m)[1].trim() === '' || /proxyFunction/g.test(fn.toString().replace(/\s/g, ""));
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:isObjectEmptyDeep
     *
     * @description
     * Will check if the object is empty and will return true if each and every property of the object is empty
     *
     * @param {Object} value, the value to evaluate

     * @returns {Boolean} a boolean.
     */
    .factory('isObjectEmptyDeep', function(lodash) {
        return function(value) {
            if (lodash.isObject(value)) {
                for (var key in value) {
                    if (!lodash.isEmpty(value[key])) {
                        return false;
                    }
                }
                return true;
            }
            return lodash.isString(value) ? lodash.isEmpty(value) : lodash.isNil(value);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:isAllTruthy
     *
     * @description
     * Iterate on the given array of Functions, return true if each function returns true
     *
     * @param {Array} arguments the functions
     *
     * @return {Boolean} true if every function returns true
     */
    .factory('isAllTruthy', function() {
        return function() {
            var fns = Array.prototype.slice.call(arguments);
            return function() {
                var args = arguments;
                return fns.every(function(f) {
                    return f.apply(f, args);
                });
            };
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:isAnyTruthy
     *
     * @description
     * Iterate on the given array of Functions, return true if at least one function returns true
     *
     * @param {Array} arguments the functions
     *
     * @return {Boolean} true if at least one function returns true
     */
    .factory('isAnyTruthy', function() {
        return function() {
            var fns = Array.prototype.slice.call(arguments);
            return function() {
                var args = arguments;
                return fns.some(function(f) {
                    return f.apply(f, args);
                });
            };
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:formatDateAsUtc
     *
     * @description
     * Formats provided dateTime as utc.
     *
     * @param {Object|String} dateTime DateTime to format in utc.
     *
     * @return {String} formatted string.
     */
    .factory('formatDateAsUtc', function(DATE_CONSTANTS) {
        return function(dateTime) {
            return moment(dateTime).utc().format(DATE_CONSTANTS.MOMENT_ISO);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.getEncodedString
     *
     * @description
     * <b>getEncodedString</b> will creates a base-64 encoded ASCII string
     * from the String passed as input
     *
     * @returns {String} a base-64 encoded ASCII string.
     *
     */
    .factory('getEncodedString', function() {
        return function(passedString) {
            if (typeof passedString === "string") {
                return btoa(passedString);
            } else {
                throw new Error('getEncodedString called with input of type "' + typeof passedString + '" when only "string" is accepted.');
            }

        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.encode
     *
     * @description
     * will return a encoded value for any JSON object passed as argument
     * @param {object} JSON object to be encoded
     */
    .factory('encode', function() {
        return function(object) {
            /* first we use encodeURIComponent to get percent-encoded UTF-8,
             * then we convert the percent encodings into raw bytes which
             * can be fed into btoa.
             * from https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding
             */
            return btoa(encodeURIComponent(JSON.stringify(object)).replace(/%([0-9A-F]{2})/g,
                function toSolidBytes(match, p1) {
                    return String.fromCharCode('0x' + p1);
                }));
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.service:compareHTMLElementsPosition
     *
     * @description
     * A function to sort an array containing DOM elements according to their position in the DOM
     *
     * @param {key =} key Optional key value to get the
     *
     * @return {Function} the compare function to use with array.sort(compareFunction) to order DOM elements as they would appear in the DOM
     */
    .factory('compareHTMLElementsPosition', function() {
        return function(key) {
            return function(a, b) {
                if (key) {
                    a = a[key];
                    b = b[key];
                }
                if (a === b) {
                    return 0;
                }
                if (!a.compareDocumentPosition) {
                    // support for IE8 and below
                    return a.sourceIndex - b.sourceIndex;
                }
                if (a.compareDocumentPosition(b) & 2) {
                    // b comes before a
                    return 1;
                }
                return -1;
            };
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.isIframe
     *
     * @description
     * <b>isIframe</b> will check if the current document is in an iFrame.
     *
     * @returns {boolean} true if the current document is in an iFrame.
     */
    .factory('isIframe', function($window) {
        return function() {
            return $window.top !== $window;
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.isPointOverElement
     *
     * @description
     * <b>isPointOverElement</b> will check if the given point is over the htmlElement
     * 
     * @param {Object} point point coordinates
     * @param {Number} point.x mouse x position
     * @param {Number} point.y mouse y position
     * @param {HTMLElement} htmlElement htmlElement to test
     *
     * @returns {boolean} true if the given point is over the htmlElement
     */
    .factory('isPointOverElement', function() {
        return function(point, htmlElement) {
            var domRect = htmlElement.getBoundingClientRect();
            return (point.x >= domRect.left && point.x <= domRect.right && point.y >= domRect.top && point.y <= domRect.bottom);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.areIntersecting
     *
     * @description
     * determines whether 2 BoundingClientRect are intersecting even partially
     * 
     * @param {Object} boundingClientRect1 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @param {Object} boundingClientRect2 size of an element and its position relative to the viewport as per {@link https://developer.mozilla.org/en-US/docs/Web/API/Element/getBoundingClientRect API}
     * @returns {boolean} true if there is a partial or total intersection
     */
    .factory('areIntersecting', function() {
        return function(boundingClientRect1, boundingClientRect2) {  
            return !(boundingClientRect2.left > (boundingClientRect1.left + boundingClientRect1.width) ||
                (boundingClientRect2.left + boundingClientRect2.width) < boundingClientRect1.left ||
                boundingClientRect2.top > (boundingClientRect1.top + boundingClientRect1.height) ||
                (boundingClientRect2.top + boundingClientRect2.height) < boundingClientRect1.top);
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.isInExtendedViewPort
     *
     * @description
     * determines whether a DOM element is partially or totally intersecting with the "extended" viewPort
     * the "extended" viewPort is the real viewPort that extends up and down by a margin, in pixels, given by the overridable constant EXTENDED_VIEW_PORT_MARGIN
     * @param {HtmlElement} element a DOM element
     * @returns {boolean} true if the given element is in the extended view port
     */
    .constant('EXTENDED_VIEW_PORT_MARGIN', 1000)
    .factory('isInExtendedViewPort', function($document, yjQuery, areIntersecting, EXTENDED_VIEW_PORT_MARGIN) {
        return function(element) {
            if (!yjQuery.contains($document[0], element)) {
                return false;
            }
            var bounds = yjQuery(element).offset();
            bounds.width = yjQuery(element).outerWidth();
            bounds.height = yjQuery(element).outerHeight();

            var doc = document.scrollingElement || document.documentElement;

            return areIntersecting({
                left: -EXTENDED_VIEW_PORT_MARGIN + doc.scrollLeft,
                width: window.innerWidth + 2 * EXTENDED_VIEW_PORT_MARGIN,
                top: -EXTENDED_VIEW_PORT_MARGIN + doc.scrollTop,
                height: window.innerHeight + 2 * EXTENDED_VIEW_PORT_MARGIN
            }, bounds);
        };
    })
    /**
     * @ngdoc service
     * @name functionsModule.deepIterateOverObjectWith
     *
     * @description
     * Iterates over object and allows to modify a value using callback function.
     * @param {Object} obj an object to iterate.
     * @param {Function} callback callback to apply to each object value.
     * @returns {Object} the object with modified values.
     */
    .factory('deepIterateOverObjectWith', function(lodash) {
        return function deepIterateOverObjectWith(obj, callback) {
            return lodash.reduce(obj, function(result, value, key) {
                if (lodash.isPlainObject(value)) {
                    result[key] = deepIterateOverObjectWith(value, callback);
                } else {
                    result[key] = callback(value);
                }
                return result;
            }.bind(this), {});
        };
    })

    /**
     * @ngdoc service
     * @name functionsModule.deepObjectPropertyDiff
     *
     * @description
     * Returns an object that contains list of fields and for each field it has a boolean value 
     * which is true when the property was modified, added or removed, false otherwise.
     * @param {Object} object The first object to inspect.
     * @param {Object} source The second object to inspect.
     * @returns {Object} the diff object.
     */
    .factory('deepObjectPropertyDiff', function(lodash, deepIterateOverObjectWith) {
        return function(firstObject, secondObject) {
            function CHANGED_PROPERTY() {}

            function NON_CHANGED_PROPERTY() {}

            var mergedObj = lodash.mergeWith(lodash.cloneDeep(firstObject), secondObject, function(prValue, cpValue) {
                if (!lodash.isPlainObject(prValue)) {
                    return !lodash.isEqual(prValue, cpValue) ? CHANGED_PROPERTY : NON_CHANGED_PROPERTY;
                }
            });

            //If the field is not CHANGED_PROPERTY/NON_CHANGED_PROPERTY then it was removed or added.
            var sanitizedObj = deepIterateOverObjectWith(mergedObj, function(value) {
                if (value !== CHANGED_PROPERTY && value !== NON_CHANGED_PROPERTY) {
                    return CHANGED_PROPERTY;
                } else {
                    return value;
                }
            });

            // If it's CHANGED_PROPERTY return true otherwise false.
            return deepIterateOverObjectWith(sanitizedObj, function(value) {
                return value === CHANGED_PROPERTY ? true : false;
            });
        };
    })

    .factory('isInDOM', function($document, yjQuery) {
        return function(component) {
            return yjQuery.contains($document[0], component);
        };
    })

    .constant('readObjectStructureFactory', function() {
        var readObjectStructure = function(json) {

            if (json === undefined || json === null || json.then) {
                return json;
            }

            if (typeof json === 'function') {
                return "FUNCTION";
            } else if (typeof json === 'number') {
                return "NUMBER";
            } else if (typeof json === 'string') {
                return "STRING";
            } else if (typeof json === 'boolean') {
                return "BOOLEAN";
            } else if (window.smarteditLodash.isElement(json)) {
                return "ELEMENT";
            } else if (json.hasOwnProperty('length')) { // jquery or Array
                if (json.forEach) {
                    var arr = [];
                    json.forEach(function(arrayElement) {
                        arr.push(readObjectStructure(arrayElement));
                    });
                    return arr;
                } else {
                    return "JQUERY";
                }
            } else { // JSON
                var clone = {};
                Object.keys(json).forEach(function(directKey) {
                    if (directKey.indexOf("$") !== 0) {
                        clone[directKey] = readObjectStructure(json[directKey]);
                    }
                });
                return clone;
            }
        };
        return readObjectStructure;
    });
