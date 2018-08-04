var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
import { NgModule } from '@angular/core';
import { OAuth2Service } from './oauth2.service';
import { OCCService } from './occ.service';
import { UserService } from './user.service';
import { GlobalVarService } from './global-var.service';
var OCCModule = (function () {
    function OCCModule() {
    }
    return OCCModule;
}());
OCCModule = __decorate([
    NgModule({
        providers: [
            OAuth2Service,
            OCCService,
            UserService,
            GlobalVarService
        ]
    })
], OCCModule);
export { OCCModule };
//# sourceMappingURL=occ.module.js.map