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
var UpdateEmailComponent = UpdateEmailComponent_1 = (function (_super) {
    __extends(UpdateEmailComponent, _super);
    function UpdateEmailComponent(userService, validationService) {
        var _this = _super.call(this, userService, validationService) || this;
        _this.userService = userService;
        _this.validationService = validationService;
        return _this;
    }
    UpdateEmailComponent.prototype.ngOnInit = function () {
        this.emailAddress = this.userService.getEmailAddressFromCookie();
    };
    UpdateEmailComponent.prototype.onSubmit = function () {
        var _this = this;
        this.validateForm();
        if (this._errorMsgs.length == 0) {
            this.userService.changeLogin(this.emailAddress, this.password)
                .then(function () {
                _super.prototype.setSuccessMessage.call(_this, UpdateEmailComponent_1.UPDATE_SUCCESS_MSGS);
                _this.clearFormFields();
            }, function (errors) {
                _super.prototype.setErrorMessage.call(_this, UpdateEmailComponent_1.VALIDATION_ERROR_MSGS, errors);
                if (_this.getValidationError('PasswordMismatchError')) {
                    _super.prototype.pushError.call(_this, 'password', UpdateEmailComponent_1.PASSWORD_MISMATCH_MSG);
                }
            });
        }
    };
    UpdateEmailComponent.prototype.validateForm = function () {
        _super.prototype.clearFormMessages.call(this);
        _super.prototype.validateFieldEmpty.call(this, 'newLogin', this.emailAddress);
        _super.prototype.validateFieldEmpty.call(this, 'checkEmailAddress', this.checkEmailAddress);
        _super.prototype.validateFieldEmpty.call(this, 'password', this.password);
        _super.prototype.validateFieldEqual.call(this, 'checkEmailAddress', this.checkEmailAddress, this.emailAddress, UpdateEmailComponent_1.EMAIL_CONFIRMATION_DOES_NOT_MATCH_MSG);
    };
    UpdateEmailComponent.prototype.clearFormFields = function () {
        this.checkEmailAddress = null;
        this.password = null;
    };
    return UpdateEmailComponent;
}(AbstractComponent));
UpdateEmailComponent.UPDATE_SUCCESS_MSGS = ['Your email has been changed. To continue, please re-login using your new email'];
UpdateEmailComponent.EMAIL_CONFIRMATION_DOES_NOT_MATCH_MSG = 'Email and email confirmation do not match';
UpdateEmailComponent = UpdateEmailComponent_1 = __decorate([
    Component({
        providers: [UserService, ValidationService],
        templateUrl: './update-email.component.html'
    }),
    __metadata("design:paramtypes", [UserService, ValidationService])
], UpdateEmailComponent);
export { UpdateEmailComponent };
var UpdateEmailComponent_1;
//# sourceMappingURL=update-email.component.js.map