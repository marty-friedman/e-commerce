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

    const PHANTOMJS_PATTERN = 'node_modules/**/phantomjs*';

    grunt.registerTask("unitSmartedit", 'Executes unit tests for smartedit', function() {
        //if npmtestancillary is not present, phantomjs drivers won't be present
        if (grunt.file.expand({
                filter: 'isFile'
            }, PHANTOMJS_PATTERN).length > 0) {
            grunt.task.run('karma:unitSmartedit');
        } else {
            grunt.log.warn('karma:unitSmartedit grunt phase was not run since no phantomjs driver found under ' + PHANTOMJS_PATTERN);
        }
    });

    grunt.registerTask("unitSmarteditContainer", 'Executes unit tests for smarteditContainer', function() {
        //if npmtestancillary is not present, phantomjs drivers won't be present
        if (grunt.file.expand({
                filter: 'isFile'
            }, PHANTOMJS_PATTERN).length > 0) {
            grunt.task.run('karma:unitSmarteditContainer');
        } else {
            grunt.log.warn('karma:unitSmarteditContainer grunt phase was not run since no phantomjs driver found under ' + PHANTOMJS_PATTERN);
        }

    });
};
