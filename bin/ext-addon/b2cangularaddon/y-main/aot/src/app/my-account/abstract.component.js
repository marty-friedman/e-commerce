import { Error } from '../shared/error-handling/error';
var AbstractComponent = (function () {
    function AbstractComponent(userService, validationService) {
        this.userService = userService;
        this.validationService = validationService;
    }
    AbstractComponent.prototype.clearMessage = function (type) {
        switch (type) {
            case 'conf':
                this._successMsgs = null;
                break;
            case 'error':
                this._errorMsgs = null;
                break;
        }
    };
    AbstractComponent.prototype.getValidationError = function (fieldName) {
        return this.validationService.getValidationError(fieldName, this.validationErrors);
    };
    AbstractComponent.prototype.setSuccessMessage = function (message) {
        this._successMsgs = message;
    };
    AbstractComponent.prototype.setErrorMessage = function (message, errors) {
        this._errorMsgs = message;
        this.validationErrors = errors;
    };
    AbstractComponent.prototype.validateFieldEmpty = function (fieldName, fieldValue) {
        if (!fieldValue) {
            this.pushError(fieldName, AbstractComponent.MANDATORY_FIELD_MSG);
        }
    };
    AbstractComponent.prototype.validateFieldEqual = function (fieldName, fieldValue, valueToCompare, errorMsg) {
        if (fieldValue != valueToCompare) {
            this.pushError(fieldName, errorMsg);
        }
    };
    AbstractComponent.prototype.pushError = function (fieldName, errorMsg) {
        this._errorMsgs = AbstractComponent.VALIDATION_ERROR_MSGS;
        this.validationErrors.push(new Error(fieldName, errorMsg));
    };
    AbstractComponent.prototype.clearFormMessages = function () {
        this._successMsgs = [];
        this._errorMsgs = [];
        this.validationErrors = [];
    };
    Object.defineProperty(AbstractComponent.prototype, "successMsgs", {
        get: function () {
            return this._successMsgs;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(AbstractComponent.prototype, "errorMsgs", {
        get: function () {
            return this._errorMsgs;
        },
        enumerable: true,
        configurable: true
    });
    return AbstractComponent;
}());
export { AbstractComponent };
AbstractComponent.VALIDATION_ERROR_MSGS = ['Please correct the errors below.'];
AbstractComponent.MANDATORY_FIELD_MSG = 'This field can not be empty';
AbstractComponent.PASSWORD_MISMATCH_MSG = 'The password is incorrect';
//# sourceMappingURL=abstract.component.js.map