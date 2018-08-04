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
     * @name generateWebpackConfig(T)
     * @description
     * # generateWebpackConfig Task
     * generateWebpackConfig is a task that generates webpack.config.json files from a json properties object
     *
     * # Configuration
     * ```js
     * {
     *      <target>: {
     *          dest: string,  // the output path/filename of the generated webpack file
     *          data: string,  // the json config data
     *          awesomeTsConfigFile: string     // location of tsConfig file for awesome-typescript-loader
     *      }
     * }
     *
     * ```
     */


    const fs = require('fs-extra');
    const path = require('path');
    const serialize = require('serialize-javascript');

    const taskName = 'generateWebpackConfig';

    function validateConfig(config) {
        if (!config.data) {
            grunt.fail.fatal(`${taskName} - invalid config, [data] param is required`);
        }
        if (!config.dest) {
            grunt.fail.fatal(`${taskName} - invalid config, [dest] param is required`);
        }
    }

    // Find awesome-typescript-loader in the config, and add a configFileName to it
    // The format its looking for is something like this:
    // module: {
    //     rules: [{ // Keep this as the first element in the array. It is reference in the webpack.js config
    //         test: /\.ts$/,
    //         loader: 'awesome-typescript-loader'
    //     }]
    // },
    function addAwesomeTypescriptLoaderConfigFile(config, tsConfigFile) {
        if (config.module && config.module.rules) {
            let rules = config.module.rules.filter((rule) => {
                return rule.loader === 'awesome-typescript-loader';
            });
            if (rules.length !== 1) {
                grunt.fail.fatal(`Error adding configFileName [${tsConfigFile}] to awesome-typescript-loader config of webpackConfig`);
            } else {
                let options = rules[0].options || {};
                options.configFileName = path.resolve(tsConfigFile);
                rules[0].options = options;
            }
        }
    }

    grunt.registerMultiTask(taskName, function() {

        grunt.verbose.writeln(`${taskName} config: ${JSON.stringify(this.data)}`);

        validateConfig(this.data);

        if (this.data.awesomeTsConfigFile) {
            addAwesomeTypescriptLoaderConfigFile(this.data.data, this.data.awesomeTsConfigFile);
        }

        const config = this.data;

        // WRITE
        grunt.log.writeln(`Writting to: ${config.dest}`);
        fs.outputFileSync(config.dest, 'module.exports = ' + serialize(config.data, {
            space: 4
        }) + ';');
    });

};
