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
var UpdatePasswordComponent = UpdatePasswordComponent_1 = (function (_super) {
    __extends(UpdatePasswordComponent, _super);
    function UpdatePasswordComponent(userService, validationService) {
        var _this = _super.call(this, userService, validationService) || this;
        _this.userService = userService;
        _this.validationService = validationService;
        return _this;
    }
    UpdatePasswordComponent.prototype.onSubmit = function () {
        var _this = this;
        this.validateForm();
        if (this._errorMsgs.length == 0) {
            this.userService.changePassword(this.currentPassword, this.newPassword)
                .then(function () {
                _super.prototype.setSuccessMessage.call(_this, UpdatePasswordComponent_1.UPDATE_SUCCESS_MSGS);
                _this.clearFormFields();
            }, function (errors) {
                _super.prototype.setErrorMessage.call(_this, UpdatePasswordComponent_1.VALIDATION_ERROR_MSGS, errors);
                if (_super.prototype.getValidationError.call(_this, 'PasswordMismatchError')) {
                    _super.prototype.pushError.call(_this, 'currentPassword', UpdatePasswordComponent_1.PASSWORD_MISMATCH_MSG);
                }
            });
        }
    };
    UpdatePasswordComponent.prototype.validateForm = function () {
        _super.prototype.clearFormMessages.call(this);
        _super.prototype.validateFieldEmpty.call(this, 'currentPassword', this.currentPassword);
        _super.prototype.validateFieldEmpty.call(this, 'password', this.newPassword);
        _super.prototype.validateFieldEmpty.call(this, 'confirmedNewPassword', this.confirmedNewPassword);
        _super.prototype.validateFieldEqual.call(this, 'confirmedNewPassword', this.newPassword, this.confirmedNewPassword, UpdatePasswordComponent_1.PASSWORD_CONFIRMATION_DOES_NOT_MATCH_MSG);
    };
    UpdatePasswordComponent.prototype.clearFormFields = function () {
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmedNewPassword = null;
    };
    return UpdatePasswordComponent;
}(AbstractComponent));
UpdatePasswordComponent.UPDATE_SUCCESS_MSGS = ['Your password has been changed'];
UpdatePasswordComponent.PASSWORD_CONFIRMATION_DOES_NOT_MATCH_MSG = 'Password and password confirmation do not match';
UpdatePasswordComponent = UpdatePasswordComponent_1 = __decorate([
    Component({
        providers: [UserService, ValidationService],
        templateUrl: './update-password.component.html'
    }),
    __metadata("design:paramtypes", [UserService, ValidationService])
], UpdatePasswordComponent);
export { UpdatePasswordComponent };
var UpdatePasswordComponent_1;
//# sourceMappingURL=update-password.component.js.map