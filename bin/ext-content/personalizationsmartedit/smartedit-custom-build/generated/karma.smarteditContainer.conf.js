module.exports = function(config) {
    config.set({
        "singleRun": true,
        "coverageReporter": {
            "dir": "jsTests\u002Fcoverage\u002F",
            "reporters": [{
                    "type": "html",
                    "subdir": "report-html"
                },
                {
                    "type": "cobertura",
                    "subdir": ".",
                    "file": "cobertura.xml"
                }
            ]
        },
        "junitReporter": {
            "outputDir": "jsTarget\u002Ftest\u002Fpersonalizationsmarteditcontainer\u002Fjunit\u002F",
            "outputFile": "testReport.xml"
        },
        "files": [
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-build\u002Fwebroot\u002Fstatic-resources\u002Fdist\u002Fsmartedit\u002Fjs\u002Fthirdparties.js",
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-build\u002Fwebroot\u002Fstatic-resources\u002Fthirdparties\u002Fckeditor\u002Fckeditor.js",
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit\u002F**\u002F*.+(js|ts)",
            "jsTests\u002FmockData\u002F**\u002F*.js",
            "jsTests\u002FmockDao\u002F**\u002F*.js",
            "jsTests\u002FmockServices\u002F**\u002F*.js",
            "jsTests\u002FcomponentObjects\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsmarteditcontainer\u002F**\u002Ftemplates.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationcommons\u002F**\u002Ftemplates.js",
            "web\u002Ffeatures\u002Fpersonalizationsmarteditcontainer\u002F**\u002F*.js",
            "web\u002Ffeatures\u002Fpersonalizationcommons\u002F**\u002F*.js",
            "jsTests\u002Ftests\u002Fpersonalizationsmarteditcontainer\u002Funit\u002Ffeatures\u002F**\u002F*.js",
            {
                "pattern": "web\u002Fwebroot\u002Ficons\u002F**\u002F*",
                "watched": false,
                "included": false,
                "served": true
            }
        ],
        "proxies": {
            "\u002Fpersonalizationsmartedit\u002Fimages\u002F": "\u002Fbase\u002Fimages\u002F",
            "\u002Fstatic-resources\u002Fimages\u002F": "\u002Fbase\u002Fstatic-resources\u002Fimages\u002F"
        },
        "exclude": [
            "**\u002FrequireLegacyJsFiles.js",
            "**\u002FpersonalizationsmarteditContainerApp.ts",
            "**\u002F*.d.ts",
            "*.d.ts"
        ],
        "webpack": {
            "devtool": "source-map",
            "externals": {
                "jasmine": "jasmine",
                "testutils": "testutils",
                "angular-mocks": "angular-mocks",
                "angular": "angular",
                "angular-route": "angular-route",
                "angular-translate": "angular-translate",
                "crypto-js": "CryptoJS",
                "smarteditcommons": "smarteditcommons"
            },
            "output": {
                "path": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002FjsTarget",
                "filename": "[name].js",
                "sourceMapFilename": "[file].map"
            },
            "resolve": {
                "modules": [
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002FjsTarget\u002Fweb\u002Fapp",
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures",
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit"
                ],
                "extensions": [
                    ".ts",
                    ".js"
                ],
                "alias": {
                    "testhelpers": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit",
                    "personalizationcommons": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationcommons",
                    "personalizationsmarteditcontainer": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsmarteditcontainer"
                }
            },
            "module": {
                "rules": [{
                    "test": /\.ts$/,
                    "loader": "awesome-typescript-loader",
                    "options": {
                        "configFileName": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.karma.smarteditContainer.json"
                    }
                }]
            },
            "stats": {
                "colors": true,
                "modules": true,
                "reasons": true,
                "errorDetails": true
            },
            "plugins": [{
                "apply": (compiler) => { // fixes https://github.com/webpack-contrib/karma-webpack/issues/66
                    compiler.plugin('done', (stats) => {
                        if (stats.compilation.errors.length > 0) {
                            throw new Error(stats.compilation.errors.map((err) => err.message || err));
                        }
                    });
                }
            }],
            "bail": true
        },
        "basePath": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsmartedit",
        "frameworks": [
            "jasmine"
        ],
        "decorators": [
            "karma-phantomjs-launcher",
            "karma-jasmine"
        ],
        "preprocessors": {
            "**\u002F*.ts": [
                "webpack"
            ]
        },
        "reporters": [
            "spec",
            "junit"
        ],
        "specReporter": {
            "suppressPassed": true,
            "suppressSkipped": true
        },
        "port": 9876,
        "colors": true,
        "autoWatch": false,
        "autoWatchBatchDelay": 1000,
        "browsers": [
            "PhantomJS"
        ],
        "plugins": [
            "karma-webpack",
            "karma-jasmine",
            "karma-phantomjs-launcher",
            "karma-junit-reporter",
            "karma-spec-reporter"
        ]
    });
};
