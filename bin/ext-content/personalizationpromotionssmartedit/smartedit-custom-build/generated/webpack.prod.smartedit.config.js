module.exports = {
    "devtool": "none",
    "externals": {
        "angular": "angular",
        "angular-route": "angular-route",
        "angular-translate": "angular-translate",
        "crypto-js": "CryptoJS",
        "smarteditcommons": "smarteditcommons"
    },
    "output": {
        "path": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002FjsTarget",
        "filename": "[name].js",
        "sourceMapFilename": "[file].map"
    },
    "resolve": {
        "modules": [
            "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002FjsTarget\u002Fweb\u002Fapp",
            "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures"
        ],
        "extensions": [
            ".ts",
            ".js"
        ],
        "alias": {
            "personalizationpromotionssmarteditcommons": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationpromotionssmarteditcommons",
            "personalizationpromotionssmartedit": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationpromotionssmartedit"
        }
    },
    "module": {
        "rules": [{
            "test": /\.ts$/,
            "loader": "awesome-typescript-loader",
            "options": {
                "configFileName": "\u002Frp\u002Ftmp\u002Fpipelines\u002F20180313145425_personalization_artifact_develop_b09b137\u002Fprepare\u002Fbuild\u002Fsource\u002Fpersonalizationpromotionssmartedit\u002Fsmartedit-custom-build\u002Fgenerated\u002Ftsconfig.prod.smartedit.json"
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
        "personalizationpromotionssmartedit": ".\u002FjsTarget\u002Fweb\u002Ffeatures\u002Fpersonalizationpromotionssmartedit\u002FpersonalizationpromotionssmarteditApp.ts"
    }
};
