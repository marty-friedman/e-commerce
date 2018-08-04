var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { Injectable } from '@angular/core';
var GlobalVarService = (function () {
    function GlobalVarService() {
        this._siteUid = '';
        this._locale = 'en';
    }
    Object.defineProperty(GlobalVarService.prototype, "siteUid", {
        get: function () {
            return this._siteUid;
        },
        set: function (siteUid) {
            this._siteUid = siteUid;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(GlobalVarService.prototype, "locale", {
        get: function () {
            return this._locale;
        },
        set: function (locale) {
            this._locale = locale;
        },
        enumerable: true,
        configurable: true
    });
    return GlobalVarService;
}());
GlobalVarService = __decorate([
    Injectable()
], GlobalVarService);
export { GlobalVarService };
//# sourceMappingURL=global-var.service.js.map