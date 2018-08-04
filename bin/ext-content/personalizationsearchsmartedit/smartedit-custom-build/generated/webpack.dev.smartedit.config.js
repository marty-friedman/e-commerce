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
        "path": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002FjsTarget",
        "filename": "[name].js",
        "sourceMapFilename": "[file].map"
    },
    "resolve": {
        "modules": [
            "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Fapp",
            "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures"
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "personalizationsearchsmarteditcommons": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsearchsmarteditcommons",
            "personalizationsearchsmartedit": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationsearchsmartedit"
        }
    },
    "module": {
        "rules": [{
            "test": /\.ts$/,
            "loader": "awesome-typescript-loader",
            "options": {
                "configFileName": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180308174101_personalizationsearch_artifact_develop_4b928b2\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationsearchsmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.dev.smartedit.json"
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
