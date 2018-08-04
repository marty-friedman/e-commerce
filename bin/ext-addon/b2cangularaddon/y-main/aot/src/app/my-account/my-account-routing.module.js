var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UpdateEmailComponent } from './update-email.component';
import { UpdatePasswordComponent } from './update-password.component';
import { UpdateProfileComponent } from './update-profile.component';
var MyAccountRoutingModule = (function () {
    function MyAccountRoutingModule() {
    }
    return MyAccountRoutingModule;
}());
MyAccountRoutingModule = __decorate([
    NgModule({
        imports: [RouterModule.forChild([
                {
                    path: 'my-account/update-email',
                    component: UpdateEmailComponent
                },
                {
                    path: 'my-account/update-password',
                    component: UpdatePasswordComponent
                },
                {
                    path: 'my-account/update-profile',
                    component: UpdateProfileComponent
                }
            ])],
        exports: [RouterModule]
    })
], MyAccountRoutingModule);
export { MyAccountRoutingModule };
//# sourceMappingURL=my-account-routing.module.js.map