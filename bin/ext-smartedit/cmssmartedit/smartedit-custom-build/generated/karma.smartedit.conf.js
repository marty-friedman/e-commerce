module.exports = function(config) {
    config.set({
        "singleRun": true,
        "coverageReporter": {
            "dir": "jsTarget\u002Ftest\u002Fcmssmartedit\u002Fcoverage\u002F",
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
            "outputDir": "jsTarget\u002Ftest\u002Fcmssmartedit\u002Fjunit\u002F",
            "outputFile": "testReport.xml"
        },
        "files": [
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002Fsmartedit-build\u002Fwebroot\u002Fstatic-resources\u002Fdist\u002Fsmartedit\u002Fjs\u002Fprelibraries.js",
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002Fsmartedit-build\u002Ftest\u002Funit\u002F**\u002F*.+(js|ts)",
            "jsTests\u002FmockData\u002F**\u002F*.js",
            "jsTests\u002FmockDao\u002F**\u002F*.js",
            "jsTests\u002FmockServices\u002F**\u002F*.js",
            "jsTests\u002FcomponentObjects\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmscommons\u002F**\u002Ftemplates.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmssmartedit\u002F**\u002Ftemplates.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmscommons\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmscommons\u002F**\u002F*.ts",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmssmartedit\u002F**\u002F*.js",
            "jsTarget\u002Fweb\u002Ffeatures\u002Fcmssmartedit\u002F**\u002F*.ts",
            "jsTests\u002Ftests\u002Fcmssmartedit\u002Funit\u002Ffeatures\u002F**\u002F*.js",
            "jsTests\u002Ftests\u002Fcmssmartedit\u002Funit\u002Ffeatures\u002F**\u002F*.ts",
            {
                "pattern": "web\u002Fwebroot\u002Fimages\u002F**\u002F*",
                "watched": false,
                "included": false,
                "served": true
            }
        ],
        "proxies": {
            "\u002Fcmssmartedit\u002Fimages\u002F": "\u002Fbase\u002Fimages\u002F",
            "\u002Fstatic-resources\u002Fimages\u002F": "\u002Fbase\u002Fstatic-resources\u002Fimages\u002F"
        },
        "exclude": [
            "**\u002FrequireLegacyJsFiles.js",
            "**\u002FcmssmarteditApp.ts",
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
                "path": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002FjsTarget",
                "filename": "[name].js",
                "sourceMapFilename": "[file].map"
            },
            "resolve": {
                "modules": [
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002FjsTarget\u002Fweb\u002Fapp",
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures",
                    "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002Fsmartedit-build\u002Ftest\u002Funit"
                ],
                "extensions": [
                    ".ts",
                    ".js"
                ],
                "alias": {
                    "testhelpers": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002Fsmartedit-build\u002Ftest\u002Funit",
                    "cmscommons": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fcmscommons",
                    "cmssmartedit": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fcmssmartedit"
                }
            },
            "module": {
                "rules": [{
                    "test": /\.ts$/,
                    "loader": "awesome-typescript-loader",
                    "options": {
                        "configFileName": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.karma.smartedit.json"
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
        "basePath": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-smartedit\u002Fcmssmartedit",
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
