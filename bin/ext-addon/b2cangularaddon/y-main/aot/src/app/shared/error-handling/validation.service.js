var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { Injectable } from '@angular/core';
var ValidationService = (function () {
    function ValidationService() {
    }
    ValidationService.prototype.hasValidationErrors = function (validationErrors) {
        return validationErrors && validationErrors.length > 0;
    };
    ValidationService.prototype.getValidationError = function (fieldName, validationErrors) {
        var validationError;
        if (validationErrors && validationErrors.length > 0) {
            for (var i = 0; i < validationErrors.length; i++) {
                var validationError_1 = validationErrors[i];
                if (validationError_1.subject == fieldName || validationError_1.type == fieldName) {
                    return validationError_1;
                }
            }
        }
        return null;
    };
    return ValidationService;
}());
ValidationService = __decorate([
    Injectable()
], ValidationService);
export { ValidationService };
//# sourceMappingURL=validation.service.js.map