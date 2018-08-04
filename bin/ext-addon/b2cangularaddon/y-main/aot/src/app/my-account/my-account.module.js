var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ErrorHandlingModule } from '../shared/error-handling/error-handling.module';
import { MyAccountRoutingModule } from './my-account-routing.module';
import { OCCModule } from '../shared/occ/occ.module';
import { UpdateEmailComponent } from './update-email.component';
import { UpdatePasswordComponent } from './update-password.component';
import { UpdateProfileComponent } from './update-profile.component';
var MyAccountModule = (function () {
    function MyAccountModule() {
    }
    return MyAccountModule;
}());
MyAccountModule = __decorate([
    NgModule({
        imports: [
            CommonModule,
            FormsModule,
            ErrorHandlingModule,
            MyAccountRoutingModule,
            OCCModule
        ],
        declarations: [
            UpdateEmailComponent,
            UpdatePasswordComponent,
            UpdateProfileComponent
        ],
        providers: []
    })
], MyAccountModule);
export { MyAccountModule };
//# sourceMappingURL=my-account.module.js.map