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
module.exports = function(grunt) {

    /**
     * @ngdoc overview
     * @name checkNoForbiddenNameSpaces(T)
     * @description
     * # checkNoForbiddenNameSpaces Task
     * checkNoForbiddenNameSpaces is a task designed to prevent accidentally referencing 3rd party libraries via
     * their default namespace, instead of the angular service wrapper.
     *
     * Current scanned values:
     * - **jquery**: 'jQuery', '$(', '$.', 'window.$'
     * - **lodash**: '_.', 'window._'
     *
     * # Configuration
     * ```js
     * {
     *      pattern: string[]   // array of glob patterns of files to scan
     * }
     * ```
     *
     */

    const taskName = "checkNoForbiddenNameSpaces";

    grunt.registerTask(taskName, 'fails the build if the code contains forbidden napespaces', function() {

        var IGNORE_HINT = "/* forbiddenNameSpaces:false */";

        var VIOLATION_TEMPLATE = "File <%= filePath %> contains forbidden namespace '<%= forbiddenNamespace %>', consider using '<%= allowedNamespace %>'";

        var REGEXP_ROOT = "REGEXP:";

        var containsKey = function(text, key) {

            var escapedKeyForRegexp = null;
            if (key.indexOf(REGEXP_ROOT) === 0) {
                escapedKeyForRegexp = key.replace(REGEXP_ROOT, "");
            } else {
                escapedKeyForRegexp = key.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
            }

            return new RegExp("[\\s]+" + escapedKeyForRegexp, 'g').test(text);
        };

        var defaultMap = {
            'jQuery': 'yjQuery in recipes or window.smarteditJQuery outside angular',
            '$(': 'yjQuery in recipes or window.smarteditJQuery outside angular',
            '$.': 'yjQuery in recipes or window.smarteditJQuery outside angular',
            'window.$': 'yjQuery in recipes or window.smarteditJQuery outside angular',
            '_.': 'lodash in recipes or window.smarteditLodash outside angular',
            'window._': 'lodash in recipes or window.smarteditLodash outside angular',
            'REGEXP:expect\\((.+)\\.isPresent\\(\\)\\)\\.toBeFalsy\\(\\);': 'browser.waitForAbsence(selector | element)',
            'REGEXP:expect\\((.+)\\.isPresent\\(\\)\\)\\.toBe\\(false': 'browser.waitForAbsence(selector | element)'
        };

        var gruntConfig = grunt.config.get(taskName);

        if (!gruntConfig.pattern) {
            grunt.fail.warn("pattern was not provided for task " + taskName);
        }

        var mergedMap = JSON.parse(JSON.stringify(defaultMap));

        if (gruntConfig.map) {
            mergedMap = Object.assign(mergedMap, gruntConfig.map);
        }

        var violations = [];

        grunt.file.expand({
            filter: 'isFile'
        }, gruntConfig.pattern).filter(function(filePath) {
            var fileContent = grunt.file.read(filePath);
            Object.keys(mergedMap).forEach(function(key) {
                if (containsKey(fileContent, key) && fileContent.indexOf(IGNORE_HINT) === -1) {
                    violations.push(grunt.template.process(VIOLATION_TEMPLATE, {
                        data: {
                            filePath: filePath,
                            forbiddenNamespace: key.replace(new RegExp("^" + REGEXP_ROOT), ""),
                            allowedNamespace: mergedMap[key]
                        }
                    }));
                }
            });
        });

        if (violations.length) {
            grunt.log.writeln("At least one file contains a forbidden namespace".yellow);
            violations.forEach(function(violation) {
                grunt.log.writeln(violation.green);
            });
            grunt.fail.warn("Make sure not to commit this!");
        }

    });

};
