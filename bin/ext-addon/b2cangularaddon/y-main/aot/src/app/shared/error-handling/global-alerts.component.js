var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Component, EventEmitter, Input, Output } from '@angular/core';
var GlobalAlertsComponent = (function () {
    function GlobalAlertsComponent() {
        this.onCleared = new EventEmitter();
    }
    Object.defineProperty(GlobalAlertsComponent.prototype, "confMsgs", {
        get: function () {
            return this._confMsgs;
        },
        set: function (confMsgs) {
            this._confMsgs = confMsgs;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(GlobalAlertsComponent.prototype, "infoMsgs", {
        get: function () {
            return this._infoMsgs;
        },
        set: function (infoMsgs) {
            this._infoMsgs = infoMsgs;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(GlobalAlertsComponent.prototype, "errorMsgs", {
        get: function () {
            return this._errorMsgs;
        },
        set: function (errorMsgs) {
            this._errorMsgs = errorMsgs;
        },
        enumerable: true,
        configurable: true
    });
    GlobalAlertsComponent.prototype.hasConfMsgs = function () {
        return this._confMsgs && this._confMsgs.length > 0;
    };
    GlobalAlertsComponent.prototype.hasInfoMsgs = function () {
        return this._infoMsgs && this._infoMsgs.length > 0;
    };
    GlobalAlertsComponent.prototype.hasErrorMsgs = function () {
        return this._errorMsgs && this._errorMsgs.length > 0;
    };
    GlobalAlertsComponent.prototype.clear = function (type) {
        switch (type) {
            case 'conf':
                this._confMsgs = null;
                break;
            case 'info':
                this._infoMsgs = null;
                break;
            case 'error':
                this._errorMsgs = null;
                break;
        }
        this.onCleared.emit(type);
    };
    return GlobalAlertsComponent;
}());
__decorate([
    Output(),
    __metadata("design:type", Object)
], GlobalAlertsComponent.prototype, "onCleared", void 0);
__decorate([
    Input(),
    __metadata("design:type", Array),
    __metadata("design:paramtypes", [Array])
], GlobalAlertsComponent.prototype, "confMsgs", null);
__decorate([
    Input(),
    __metadata("design:type", Array),
    __metadata("design:paramtypes", [Array])
], GlobalAlertsComponent.prototype, "infoMsgs", null);
__decorate([
    Input(),
    __metadata("design:type", Array),
    __metadata("design:paramtypes", [Array])
], GlobalAlertsComponent.prototype, "errorMsgs", null);
GlobalAlertsComponent = __decorate([
    Component({
        selector: 'global-alerts',
        templateUrl: 'global-alerts.component.html'
    })
], GlobalAlertsComponent);
export { GlobalAlertsComponent };
//# sourceMappingURL=global-alerts.component.js.map