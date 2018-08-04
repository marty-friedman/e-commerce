module.exports = function(config) {
    config.set(
        {
    "coverageReporter": {
        "dir": "..\u002F..\u002FjsTarget\u002Ftest\u002Fsmartedit\u002Fcoverage\u002F",
        "reporters": [
            {
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
        "outputDir": "jsTarget\u002Ftest\u002Fsmartedit\u002Fjunit\u002F",
        "outputFile": "testReport.xml",
        "suite": ""
    },
    "files": [
        "node_modules\u002Fckeditor\u002Fckeditor.js",
        "node_modules\u002Fjquery\u002Fdist\u002Fjquery.js",
        "node_modules\u002Fangular\u002Fangular.js",
        "node_modules\u002Fangular-resource\u002Fangular-resource.js",
        "node_modules\u002Fangular-cookies\u002Fangular-cookies.js",
        "node_modules\u002Fangular-mocks\u002Fangular-mocks.js",
        "node_modules\u002Fangular-mocks-async\u002Fdist\u002Fangular-mocks-async.js",
        "node_modules\u002Fangular-ui-bootstrap\u002Fdist\u002Fui-bootstrap-tpls.js",
        "node_modules\u002Fangular-translate\u002Fdist\u002Fangular-translate.js",
        "node_modules\u002Fangular-sanitize\u002Fangular-sanitize.js",
        "node_modules\u002Fui-select\u002Fdist\u002Fselect.js",
        "web\u002Fwebroot\u002Fstatic-resources\u002Fthirdparties\u002Fpolyfills\u002F**\u002F*.js",
        "node_modules\u002Flodash\u002Flodash.js",
        "node_modules\u002Fmoment\u002Fmin\u002Fmoment-with-locales.js",
        "node_modules\u002Felement-resize-detector\u002Fdist\u002Felement-resize-detector.js",
        "node_modules\u002Fpopper.js\u002Fdist\u002Fumd\u002Fpopper.js",
        "node_modules\u002Fcrypto-js\u002Fcrypto-js.js",
        "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit\u002F**\u002F*.+(js|ts)",
        "jsTarget\u002Ftemplates.js",
        "jsTarget\u002Fweb\u002Fapp\u002Fcommon\u002F**\u002F*.+(js|ts)",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fdirectives\u002F**\u002F*.+(js|ts)",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fservices\u002F**\u002F*.+(js|ts)",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fmodules\u002FsystemModule\u002Ffeatures\u002F**\u002F*.+(js|ts)",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fmodules\u002FsystemModule\u002Fservices\u002Ftoolbar\u002Ftoolbar.js",
        "test\u002Funit\u002Fsmartedit\u002Funit\u002F**\u002F*.+(js|ts)",
        {
            "pattern": "web\u002Fwebroot\u002Fstatic-resources\u002Fimages\u002F**\u002F*",
            "watched": false,
            "included": false,
            "served": true
        }
    ],
    "exclude": [
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fsmartedit.ts",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002FpartialBackendMocks.js",
        "jsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fsmarteditbootstrap.ts",
        "**\u002Findex.ts",
        "**\u002F*.d.ts"
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
            "crypto-js": "CryptoJS"
        },
        "output": {
            "path": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget",
            "filename": "[name].js",
            "sourceMapFilename": "[file].map"
        },
        "resolve": {
            "modules": [
                "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp",
                "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures",
                "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit"
            ],
            "extensions": [
                ".ts",
                ".js"
            ],
            "alias": {
                "testhelpers": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002Fsmartedit-build\u002Ftest\u002Funit",
                "smarteditcommons": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fcommon",
                "smartedit": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmartedit"
            }
        },
        "module": {
            "rules": [
                {
                    "test": /\.ts$/,
                    "loader": "awesome-typescript-loader",
                    "options": {
                        "configFileName": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.karma.smartedit.json"
                    }
                }
            ]
        },
        "stats": {
            "colors": true,
            "modules": true,
            "reasons": true,
            "errorDetails": true
        },
        "plugins": [
            {
                "apply": (compiler) => { // fixes https://github.com/webpack-contrib/karma-webpack/issues/66
                compiler.plugin('done', (stats) => {
                    if (stats.compilation.errors.length > 0) {
                        throw new Error(stats.compilation.errors.map((err) => err.message || err));
                    }
                });
            }
            }
        ],
        "bail": true
    },
    "basePath": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit",
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
    "singleRun": true,
    "proxies": {
        "\u002Fstatic-resources\u002Fimages\u002F": "\u002Fbase\u002Fstatic-resources\u002Fimages\u002F"
    },
    "plugins": [
        "karma-webpack",
        "karma-jasmine",
        "karma-phantomjs-launcher",
        "karma-junit-reporter",
        "karma-spec-reporter"
    ]
}
    );
};