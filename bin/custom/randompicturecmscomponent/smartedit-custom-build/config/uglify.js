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
            'dist'
        ],
        config: function(data, conf) {
            return {
                dist: {
                    files: [{
                        expand: true, // Enable dynamic expansion.
                        cwd: 'jsTarget/', // Src matches are relative to this path.
                        src: ['*.js'], // Actual pattern(s) to match.
                        dest: 'web/webroot/randompicturecmscomponent/js/', // Destination path prefix.
                        ext: '.js', // Dest filepaths will have this extension.
                        extDot: 'first' // Extensions in filenames begin after the first dot
                    }],
                    options: {
                        mangle: true //ok since one has ng-annotate beforehand
                    }
                }
            };
        }
    };

};
