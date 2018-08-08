module.exports = {
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
        "path": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002FjsTarget",
        "filename": "[name].js",
        "sourceMapFilename": "[file].map"
    },
    "resolve": {
        "modules": [
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Fapp",
            "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures"
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "personalizationsearchsmarteditcommons": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsearchsmarteditcommons",
            "personalizationsearchsmartedit": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsearchsmartedit"
        }
    },
    "module": {
        "rules": [{
            "test": /\.ts$/,
            "loader": "awesome-typescript-loader",
            "options": {
                "configFileName": "\u002Fopt\u002Fhybris_6.7.0.3\u002Fhybris\u002Fbin\u002Fext-content\u002Fpersonalizationsearchsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.dev.smartedit.json"
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
    "bail": true,
    "entry": {
        "personalizationsearchsmartedit": ".\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsearchsmartedit\u002Fpersonalizationsearchsmartedit.ts"
    }
};
