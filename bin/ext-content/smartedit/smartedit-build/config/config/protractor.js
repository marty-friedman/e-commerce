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

    const MAX_INSTANCES = "max_instances";

    return {
        targets: [
            'run',
            'maxrun'
        ],
        config: function(data, conf) {
            conf = conf || {};

            let maxInstances = grunt.option(MAX_INSTANCES) ? parseInt(grunt.option(MAX_INSTANCES)) :
                (process.env.PROTRACTOR_CHROME_INSTANCES || 5);

            conf.options = {
                // Required to prevent grunt from exiting with a non-zero status in CI
                keepAlive: process.env.PROTRACTOR_KEEP_ALIVE === 'true',
                configFile: global.smartedit.bundlePaths.test.e2e.protractor.conf
            };
            conf.run = {
                // standard e2e
            };

            conf.maxrun = { // multiple instance e2e (more performant)
                options: {
                    args: {
                        capabilities: {
                            shardTestFiles: true,
                            maxInstances: maxInstances,
                            chromeOptions: {
                                args: ['lang=en-US', 'dummy'] //pass a second dummy value to prevent grunt-protractor from trimming the [] when passing to protractor
                            }
                        }
                    }
                }
            };

            return conf;
        }
    };
};
