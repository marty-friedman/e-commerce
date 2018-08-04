module.exports = {
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
            "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures"
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "smarteditcommons": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fcommon",
            "smarteditcontainer": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmarteditcontainer"
        }
    },
    "module": {
        "rules": [
            {
                "test": /\.ts$/,
                "loader": "awesome-typescript-loader",
                "options": {
                    "configFileName": "\u002Fsrv\u002Fjenkins\u002Fworkspace\u002Fcommerce-suite-unpacked\u002Fbuild\u002Fsource\u002Fsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.dev.smarteditContainer.json"
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
    "bail": true,
    "entry": {
        "smarteditloader": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmarteditloader\u002Fsmarteditloader.ts",
        "smarteditcontainer": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmarteditcontainer\u002Findex.ts",
        "administrationModule": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmarteditcontainer\u002Fmodules\u002FadministrationModule\u002Findex.ts"
    }
};