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


    // For each extension in folder web/featureExtensions:
    // Inject an 'import' statement of the barrel file (if it exist) for each frame (cmssmartedit and cmssmarteditContainer)
    grunt.registerTask('injectExtensionsImports', function() {
        var paths = require("../../jsTests/paths");
        var fs = require('fs');

        grunt.file.write(paths.target.featureExtensionsSmartEditImport, '');
        grunt.file.write(paths.target.featureExtensionsSmartEditContainerImport, '');
        grunt.file.expand({
            filter: 'isDirectory'
        }, "web/featureExtensions/*/").forEach(function(dir) {
            var folderName = dir.replace("web/featureExtensions/", "");
            folderName = folderName.substring(0, folderName.indexOf('/'));
            var files = [];
            if (fs.existsSync('web/featureExtensions/' + folderName + '/cmssmartedit/index.ts')) {
                files.push({
                    append: "import '../../featureExtensions/" + folderName + "/cmssmartedit';",
                    input: paths.target.featureExtensionsSmartEditImport
                });
            }
            if (fs.existsSync('web/featureExtensions/' + folderName + '/cmssmarteditContainer/index.ts')) {
                files.push({
                    append: "import '../../featureExtensions/" + folderName + "/cmssmarteditContainer';",
                    input: paths.target.featureExtensionsSmartEditContainerImport
                });
            }
            if (files.length) {
                grunt.config.set('file_append', {
                    'extensions': {
                        files: files
                    }
                });
                grunt.task.run('file_append');
            }
        });
    });

};
