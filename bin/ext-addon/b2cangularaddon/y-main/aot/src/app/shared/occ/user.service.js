var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
import { Injectable } from '@angular/core';
import { Http, RequestOptions, URLSearchParams } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { CookieService } from 'ngx-cookie';
import { OCCService } from './occ.service';
var UserService = (function () {
    function UserService(cookieService, http, occService) {
        this.cookieService = cookieService;
        this.http = http;
        this.occService = occService;
    }
    UserService.prototype.getEmailAddressFromCookie = function () {
        return decodeURIComponent(this.cookieService.get("userId"));
    };
    UserService.prototype.getTitles = function () {
        var url = this.getTitlesUrl();
        var headers = this.occService.getOCCHeadersWithAccessToken();
        return this.http.get(url, { headers: headers })
            .toPromise()
            .then(function (response) { return response.json(); })
            .catch(this.handleError);
    };
    UserService.prototype.getTitlesUrl = function () {
        return this.occService.getBaseOCCUrlWithSite() + '/titles?' + this.occService.getLocaleParam();
    };
    UserService.prototype.getUser = function (userId) {
        var url = this.getUsersUrl() + '/' + userId;
        var headers = this.occService.getOCCHeadersWithAccessToken();
        return this.http.get(url, { headers: headers })
            .toPromise()
            .then(function (response) { return response.json(); })
            .catch(this.handleError);
    };
    UserService.prototype.getUsersUrl = function () {
        return this.occService.getBaseOCCUrlWithSite() + '/users';
    };
    UserService.prototype.update = function (user) {
        var url = this.getUsersUrl() + '/' + user.uid;
        var headers = this.occService.getOCCHeadersWithAccessToken();
        return this.http
            .put(url, JSON.stringify(user), { headers: headers })
            .toPromise()
            .then(function () { return user; })
            .catch(this.handleError);
    };
    UserService.prototype.changeLogin = function (newLogin, password) {
        var url = this.getUsersUrl() + '/' + this.getEmailAddressFromCookie() + '/login';
        var params = new URLSearchParams();
        params.set('newLogin', newLogin);
        params.set('password', password);
        return this.sendPutRequest(url, params);
    };
    UserService.prototype.changePassword = function (currentPassword, newPassword) {
        var url = this.getUsersUrl() + '/' + this.getEmailAddressFromCookie() + '/' + 'password';
        var params = new URLSearchParams();
        params.set('old', currentPassword);
        params.set('new', newPassword);
        return this.sendPutRequest(url, params);
    };
    UserService.prototype.sendPutRequest = function (url, params) {
        var headers = this.occService.getOCCHeadersWithAccessToken();
        var requestOptions = new RequestOptions({ headers: headers, params: params });
        return this.http
            .put(url, null, requestOptions)
            .toPromise()
            .catch(this.handleError);
    };
    UserService.prototype.handleError = function (error) {
        var errors = JSON.parse(error._body);
        if (errors.errors.length > 0) {
            return Promise.reject(errors.errors);
        }
        else {
            return Promise.reject(error.message || error);
        }
    };
    return UserService;
}());
UserService = __decorate([
    Injectable(),
    __metadata("design:paramtypes", [CookieService,
        Http,
        OCCService])
], UserService);
export { UserService };
//# sourceMappingURL=user.service.js.map