var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Component } from '@angular/core';
import { AbstractComponent } from './abstract.component';
import { UserService } from '../shared/occ/user.service';
import { ValidationService } from '../shared/error-handling/validation.service';
var UpdateProfileComponent = UpdateProfileComponent_1 = (function (_super) {
    __extends(UpdateProfileComponent, _super);
    function UpdateProfileComponent(userService, validationService) {
        var _this = _super.call(this, userService, validationService) || this;
        _this.userService = userService;
        _this.validationService = validationService;
        return _this;
    }
    UpdateProfileComponent.prototype.ngOnInit = function () {
        this.getTitles();
        this.getUser();
    };
    UpdateProfileComponent.prototype.onSubmit = function () {
        var _this = this;
        this.validateForm();
        if (this._errorMsgs.length == 0) {
            this.userService.update(this.user)
                .then(function (user) {
                _this._user = user;
                _super.prototype.setSuccessMessage.call(_this, UpdateProfileComponent_1.UPDATE_SUCCESS_MSGS);
            }, function (errors) {
                _super.prototype.setErrorMessage.call(_this, UpdateProfileComponent_1.VALIDATION_ERROR_MSGS, errors);
            });
        }
    };
    UpdateProfileComponent.prototype.validateForm = function () {
        _super.prototype.clearFormMessages.call(this);
        _super.prototype.validateFieldEmpty.call(this, 'firstName', this.user.firstName);
        _super.prototype.validateFieldEmpty.call(this, 'lastName', this.user.lastName);
    };
    UpdateProfileComponent.prototype.getTitles = function () {
        var _this = this;
        this.userService.getTitles().then(function (titles) { return _this._titles = titles; });
    };
    UpdateProfileComponent.prototype.getUser = function () {
        var _this = this;
        var emailAddress = this.userService.getEmailAddressFromCookie();
        this.userService.getUser(emailAddress)
            .then(function (user) { return _this._user = user; });
    };
    Object.defineProperty(UpdateProfileComponent.prototype, "titles", {
        get: function () {
            return this._titles;
        },
        set: function (titles) {
            this._titles = titles;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(UpdateProfileComponent.prototype, "user", {
        get: function () {
            return this._user;
        },
        set: function (usr) {
            this._user = usr;
        },
        enumerable: true,
        configurable: true
    });
    return UpdateProfileComponent;
}(AbstractComponent));
UpdateProfileComponent.UPDATE_SUCCESS_MSGS = ['Your profile has been updated'];
UpdateProfileComponent = UpdateProfileComponent_1 = __decorate([
    Component({
        providers: [UserService, ValidationService],
        templateUrl: './update-profile.component.html'
    }),
    __metadata("design:paramtypes", [UserService, ValidationService])
], UpdateProfileComponent);
export { UpdateProfileComponent };
var UpdateProfileComponent_1;
//# sourceMappingURL=update-profile.component.js.map