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
        "path": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002FjsTarget",
        "filename": "[name].js",
        "sourceMapFilename": "[file].map"
    },
    "resolve": {
        "modules": [
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp",
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures"
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "smarteditcommons": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fcommon",
            "smartedit": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmartedit"
        }
    },
    "module": {
        "rules": [
            {
                "test": /\.ts$/,
                "loader": "awesome-typescript-loader",
                "options": {
                    "configFileName": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.dev.smartedit.json"
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
        "presmartedit": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Findex.ts",
        "postsmartedit": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fsmarteditbootstrap.ts",
        "systemModule": ".\u002FjsTarget\u002Fweb\u002Fapp\u002Fsmartedit\u002Fmodules\u002FsystemModule\u002Fsystem.ts"
    }
};