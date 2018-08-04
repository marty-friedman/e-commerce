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
module.exports = function() {

    return {
        targets: [
            'dist',
            'webApplicationInjector',
            'uglifyThirdparties'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                dist: {
                    files: {
                        'web/webroot/static-resources/smarteditloader/js/smarteditloader.js': ['jsTarget/smarteditloader.js'],
                        'web/webroot/static-resources/smarteditcontainer/js/smarteditcontainer.js': ['jsTarget/smarteditcontainer.js'],
                        'web/webroot/static-resources/dist/smartedit/js/presmartedit.js': ['jsTarget/presmartedit.js'],
                        'web/webroot/static-resources/dist/smartedit/js/postsmartedit.js': ['jsTarget/postsmartedit.js'],
                        'web/webroot/static-resources/smarteditcontainer/modules/administrationModule.js': [paths.web.webroot.staticResources.dir + '/smarteditcontainer/modules/administrationModule.js'],
                        'web/webroot/static-resources/smartedit/modules/systemModule.js': [paths.web.webroot.staticResources.dir + '/smartedit/modules/systemModule.js']
                    },
                    options: {
                        mangle: true //ok since one has ng-annotate beforehand
                    }
                },
                //Since uglify properly terminates statements with semi-colon, it thereby sanitizes the not so clean $script js
                webApplicationInjector: {
                    files: {
                        'web/webroot/static-resources/webApplicationInjector.js': ['jsTarget/webApplicationInjector.js'],
                    },
                    options: {
                        mangle: true,

                        output: {
                            /**
                             * This is because CI adds license header, we don't want to keep generating this file with no
                             * header and seeing local file changes in git diff.
                             *
                             * We might consider creating a custom function for this in the future to look specifically for
                             * our license header.
                             * https://github.com/gruntjs/grunt-contrib-uglify
                             */
                            comments: 'all'
                        }
                    }
                },

                uglifyThirdparties: {
                    files: {
                        'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js': ['node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js']
                    },
                    options: {
                        mangle: true
                    }

                }
            };
        }
    };
};
