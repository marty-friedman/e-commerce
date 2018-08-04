/**
 * @fileoverview This file is generated by the Angular template compiler.
 * Do not edit.
 * @suppress {suspiciousCode,uselessCode,missingProperties,missingOverride}
 */
/* tslint:disable */
import * as import0 from '@angular/core';
import * as import1 from '../../../../src/app/my-account/update-email.component';
import * as import2 from '../shared/error-handling/global-alerts.component.ngfactory';
import * as import3 from '../../../../src/app/shared/error-handling/global-alerts.component';
import * as import4 from '@angular/forms';
import * as import5 from '@angular/common';
import * as import6 from '../../../../src/app/shared/occ/user.service';
import * as import7 from 'ngx-cookie/src/cookie.service';
import * as import8 from '@angular/http';
import * as import9 from '../../../../src/app/shared/occ/occ.service';
import * as import10 from '../../../../src/app/shared/error-handling/validation.service';
var styles_UpdateEmailComponent = [];
export var RenderType_UpdateEmailComponent = import0.ɵcrt({
    encapsulation: 2,
    styles: styles_UpdateEmailComponent,
    data: {}
});
function View_UpdateEmailComponent_1(l) {
    return import0.ɵvid(0, [
        (l()(), import0.ɵeld(0, null, null, 4, 'div', [[
                'class',
                'help-block'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, 'span', [[
                'id',
                'currentPassword.field.errors'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, [
            '',
            ''
        ])),
        (l()(), import0.ɵted(null, ['\n                                ']))
    ], null, function (ck, v) {
        var currVal_0 = v.context.ngIf.message;
        ck(v, 3, 0, currVal_0);
    });
}
function View_UpdateEmailComponent_2(l) {
    return import0.ɵvid(0, [
        (l()(), import0.ɵeld(0, null, null, 4, 'div', [[
                'class',
                'help-block'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, 'span', [[
                'id',
                'currentPassword.field.errors'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, [
            '',
            ''
        ])),
        (l()(), import0.ɵted(null, ['\n                                ']))
    ], null, function (ck, v) {
        var currVal_0 = v.context.ngIf.message;
        ck(v, 3, 0, currVal_0);
    });
}
function View_UpdateEmailComponent_3(l) {
    return import0.ɵvid(0, [
        (l()(), import0.ɵeld(0, null, null, 4, 'div', [[
                'class',
                'help-block'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, 'span', [[
                'id',
                'currentPassword.field.errors'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, [
            '',
            ''
        ])),
        (l()(), import0.ɵted(null, ['\n                                ']))
    ], null, function (ck, v) {
        var currVal_0 = v.context.ngIf.message;
        ck(v, 3, 0, currVal_0);
    });
}
export function View_UpdateEmailComponent_0(l) {
    return import0.ɵvid(0, [
        (l()(), import0.ɵeld(0, null, null, 1, 'global-alerts', [], null, [[
                null,
                'onCleared'
            ]
        ], function (v, en, $event) {
            var ad = true;
            var co = v.component;
            if (('onCleared' === en)) {
                var pd_0 = (co.clearMessage($event) !== false);
                ad = (pd_0 && ad);
            }
            return ad;
        }, import2.View_GlobalAlertsComponent_0, import2.RenderType_GlobalAlertsComponent)),
        import0.ɵdid(49152, null, 0, import3.GlobalAlertsComponent, [], {
            confMsgs: [
                0,
                'confMsgs'
            ],
            errorMsgs: [
                1,
                'errorMsgs'
            ]
        }, { onCleared: 'onCleared' }),
        (l()(), import0.ɵted(null, ['\n\n'])),
        (l()(), import0.ɵeld(0, null, null, 127, 'div', [[
                'class',
                'account-section'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n    '])),
        (l()(), import0.ɵeld(0, null, null, 124, 'div', [[
                'class',
                'yCmsContentSlot account-section-content'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n        '])),
        (l()(), import0.ɵeld(0, null, null, 10, 'div', [[
                'class',
                'account-section-header'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n            '])),
        (l()(), import0.ɵeld(0, null, null, 7, 'div', [[
                'class',
                'row'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'div', [[
                'class',
                'container-lg col-md-6'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['电子邮件地址'])),
        (l()(), import0.ɵted(null, ['\n                '])),
        (l()(), import0.ɵted(null, ['\n            '])),
        (l()(), import0.ɵted(null, ['\n        '])),
        (l()(), import0.ɵted(null, ['\n        '])),
        (l()(), import0.ɵeld(0, null, null, 109, 'div', [[
                'class',
                'row'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n            '])),
        (l()(), import0.ɵeld(0, null, null, 106, 'div', [[
                'class',
                'container-lg col-md-6'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                '])),
        (l()(), import0.ɵeld(0, null, null, 103, 'div', [[
                'class',
                'account-section-content'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                    '])),
        (l()(), import0.ɵeld(0, null, null, 100, 'div', [[
                'class',
                'account-section-form'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                        '])),
        (l()(), import0.ɵeld(0, null, null, 97, 'form', [
            [
                'id',
                'updateEmailForm'
            ],
            [
                'novalidate',
                ''
            ]
        ], [
            [
                2,
                'ng-untouched',
                null
            ],
            [
                2,
                'ng-touched',
                null
            ],
            [
                2,
                'ng-pristine',
                null
            ],
            [
                2,
                'ng-dirty',
                null
            ],
            [
                2,
                'ng-valid',
                null
            ],
            [
                2,
                'ng-invalid',
                null
            ],
            [
                2,
                'ng-pending',
                null
            ]
        ], [
            [
                null,
                'ngSubmit'
            ],
            [
                null,
                'submit'
            ],
            [
                null,
                'reset'
            ]
        ], function (v, en, $event) {
            var ad = true;
            var co = v.component;
            if (('submit' === en)) {
                var pd_0 = (import0.ɵnov(v, 29).onSubmit($event) !== false);
                ad = (pd_0 && ad);
            }
            if (('reset' === en)) {
                var pd_1 = (import0.ɵnov(v, 29).onReset() !== false);
                ad = (pd_1 && ad);
            }
            if (('ngSubmit' === en)) {
                var pd_2 = (co.onSubmit() !== false);
                ad = (pd_2 && ad);
            }
            return ad;
        }, null, null)),
        import0.ɵdid(16384, null, 0, import4.ɵbf, [], null, null),
        import0.ɵdid(16384, [[
                'userForm',
                4
            ]
        ], 0, import4.NgForm, [
            [
                8,
                null
            ],
            [
                8,
                null
            ]
        ], null, { ngSubmit: 'ngSubmit' }),
        import0.ɵprd(2048, null, import4.ControlContainer, null, [import4.NgForm]),
        import0.ɵdid(16384, null, 0, import4.NgControlStatusGroup, [import4.ControlContainer], null, null),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵeld(0, null, null, 17, 'div', [[
                'class',
                'form-group'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'label', [
            [
                'class',
                'control-label'
            ],
            [
                'for',
                'profile.email'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['电子邮件地址'])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 5, 'input', [
            [
                'class',
                'text form-control'
            ],
            [
                'id',
                'profile.email'
            ],
            [
                'name',
                'email'
            ],
            [
                'type',
                'text'
            ]
        ], [
            [
                2,
                'ng-untouched',
                null
            ],
            [
                2,
                'ng-touched',
                null
            ],
            [
                2,
                'ng-pristine',
                null
            ],
            [
                2,
                'ng-dirty',
                null
            ],
            [
                2,
                'ng-valid',
                null
            ],
            [
                2,
                'ng-invalid',
                null
            ],
            [
                2,
                'ng-pending',
                null
            ]
        ], [
            [
                null,
                'ngModelChange'
            ],
            [
                null,
                'input'
            ],
            [
                null,
                'blur'
            ],
            [
                null,
                'compositionstart'
            ],
            [
                null,
                'compositionend'
            ]
        ], function (v, en, $event) {
            var ad = true;
            var co = v.component;
            if (('input' === en)) {
                var pd_0 = (import0.ɵnov(v, 42)._handleInput($event.target.value) !== false);
                ad = (pd_0 && ad);
            }
            if (('blur' === en)) {
                var pd_1 = (import0.ɵnov(v, 42).onTouched() !== false);
                ad = (pd_1 && ad);
            }
            if (('compositionstart' === en)) {
                var pd_2 = (import0.ɵnov(v, 42)._compositionStart() !== false);
                ad = (pd_2 && ad);
            }
            if (('compositionend' === en)) {
                var pd_3 = (import0.ɵnov(v, 42)._compositionEnd($event.target.value) !== false);
                ad = (pd_3 && ad);
            }
            if (('ngModelChange' === en)) {
                var pd_4 = ((co.emailAddress = $event) !== false);
                ad = (pd_4 && ad);
            }
            return ad;
        }, null, null)),
        import0.ɵdid(16384, null, 0, import4.DefaultValueAccessor, [
            import0.Renderer,
            import0.ElementRef,
            [
                2,
                import4.COMPOSITION_BUFFER_MODE
            ]
        ], null, null),
        import0.ɵprd(1024, null, import4.NG_VALUE_ACCESSOR, function (p0_0) {
            return [p0_0];
        }, [import4.DefaultValueAccessor]),
        import0.ɵdid(671744, null, 0, import4.NgModel, [
            [
                2,
                import4.ControlContainer
            ],
            [
                8,
                null
            ],
            [
                8,
                null
            ],
            [
                2,
                import4.NG_VALUE_ACCESSOR
            ]
        ], {
            name: [
                0,
                'name'
            ],
            model: [
                1,
                'model'
            ]
        }, { update: 'ngModelChange' }),
        import0.ɵprd(2048, null, import4.NgControl, null, [import4.NgModel]),
        import0.ɵdid(16384, null, 0, import4.NgControlStatus, [import4.NgControl], null, null),
        (l()(), import0.ɵted(null, ['\n\n                                '])),
        (l()(), import0.ɵand(16777216, null, null, 1, null, View_UpdateEmailComponent_1)),
        import0.ɵdid(16384, null, 0, import5.NgIf, [
            import0.ViewContainerRef,
            import0.TemplateRef
        ], { ngIf: [
                0,
                'ngIf'
            ]
        }, null),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵted(null, ['\n\n                            '])),
        (l()(), import0.ɵeld(0, null, null, 17, 'div', [[
                'class',
                'form-group'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'label', [
            [
                'class',
                'control-label'
            ],
            [
                'for',
                'profile.checkEmail'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['再次输入电子邮件地址'])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 5, 'input', [
            [
                'class',
                'text form-control'
            ],
            [
                'id',
                'profile.checkEmail'
            ],
            [
                'name',
                'checkEmail'
            ],
            [
                'type',
                'text'
            ]
        ], [
            [
                2,
                'ng-untouched',
                null
            ],
            [
                2,
                'ng-touched',
                null
            ],
            [
                2,
                'ng-pristine',
                null
            ],
            [
                2,
                'ng-dirty',
                null
            ],
            [
                2,
                'ng-valid',
                null
            ],
            [
                2,
                'ng-invalid',
                null
            ],
            [
                2,
                'ng-pending',
                null
            ]
        ], [
            [
                null,
                'ngModelChange'
            ],
            [
                null,
                'input'
            ],
            [
                null,
                'blur'
            ],
            [
                null,
                'compositionstart'
            ],
            [
                null,
                'compositionend'
            ]
        ], function (v, en, $event) {
            var ad = true;
            var co = v.component;
            if (('input' === en)) {
                var pd_0 = (import0.ɵnov(v, 61)._handleInput($event.target.value) !== false);
                ad = (pd_0 && ad);
            }
            if (('blur' === en)) {
                var pd_1 = (import0.ɵnov(v, 61).onTouched() !== false);
                ad = (pd_1 && ad);
            }
            if (('compositionstart' === en)) {
                var pd_2 = (import0.ɵnov(v, 61)._compositionStart() !== false);
                ad = (pd_2 && ad);
            }
            if (('compositionend' === en)) {
                var pd_3 = (import0.ɵnov(v, 61)._compositionEnd($event.target.value) !== false);
                ad = (pd_3 && ad);
            }
            if (('ngModelChange' === en)) {
                var pd_4 = ((co.checkEmailAddress = $event) !== false);
                ad = (pd_4 && ad);
            }
            return ad;
        }, null, null)),
        import0.ɵdid(16384, null, 0, import4.DefaultValueAccessor, [
            import0.Renderer,
            import0.ElementRef,
            [
                2,
                import4.COMPOSITION_BUFFER_MODE
            ]
        ], null, null),
        import0.ɵprd(1024, null, import4.NG_VALUE_ACCESSOR, function (p0_0) {
            return [p0_0];
        }, [import4.DefaultValueAccessor]),
        import0.ɵdid(671744, null, 0, import4.NgModel, [
            [
                2,
                import4.ControlContainer
            ],
            [
                8,
                null
            ],
            [
                8,
                null
            ],
            [
                2,
                import4.NG_VALUE_ACCESSOR
            ]
        ], {
            name: [
                0,
                'name'
            ],
            model: [
                1,
                'model'
            ]
        }, { update: 'ngModelChange' }),
        import0.ɵprd(2048, null, import4.NgControl, null, [import4.NgModel]),
        import0.ɵdid(16384, null, 0, import4.NgControlStatus, [import4.NgControl], null, null),
        (l()(), import0.ɵted(null, ['\n\n                                '])),
        (l()(), import0.ɵand(16777216, null, null, 1, null, View_UpdateEmailComponent_2)),
        import0.ɵdid(16384, null, 0, import5.NgIf, [
            import0.ViewContainerRef,
            import0.TemplateRef
        ], { ngIf: [
                0,
                'ngIf'
            ]
        }, null),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵted(null, ['\n\n                            '])),
        (l()(), import0.ɵeld(0, null, null, 17, 'div', [[
                'class',
                'form-group'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'label', [
            [
                'class',
                'control-label'
            ],
            [
                'for',
                'profile.pwd'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['密码'])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 5, 'input', [
            [
                'autocomplete',
                'off'
            ],
            [
                'class',
                'text form-control'
            ],
            [
                'id',
                'profile.pwd'
            ],
            [
                'name',
                'password'
            ],
            [
                'type',
                'password'
            ]
        ], [
            [
                2,
                'ng-untouched',
                null
            ],
            [
                2,
                'ng-touched',
                null
            ],
            [
                2,
                'ng-pristine',
                null
            ],
            [
                2,
                'ng-dirty',
                null
            ],
            [
                2,
                'ng-valid',
                null
            ],
            [
                2,
                'ng-invalid',
                null
            ],
            [
                2,
                'ng-pending',
                null
            ]
        ], [
            [
                null,
                'ngModelChange'
            ],
            [
                null,
                'input'
            ],
            [
                null,
                'blur'
            ],
            [
                null,
                'compositionstart'
            ],
            [
                null,
                'compositionend'
            ]
        ], function (v, en, $event) {
            var ad = true;
            var co = v.component;
            if (('input' === en)) {
                var pd_0 = (import0.ɵnov(v, 80)._handleInput($event.target.value) !== false);
                ad = (pd_0 && ad);
            }
            if (('blur' === en)) {
                var pd_1 = (import0.ɵnov(v, 80).onTouched() !== false);
                ad = (pd_1 && ad);
            }
            if (('compositionstart' === en)) {
                var pd_2 = (import0.ɵnov(v, 80)._compositionStart() !== false);
                ad = (pd_2 && ad);
            }
            if (('compositionend' === en)) {
                var pd_3 = (import0.ɵnov(v, 80)._compositionEnd($event.target.value) !== false);
                ad = (pd_3 && ad);
            }
            if (('ngModelChange' === en)) {
                var pd_4 = ((co.password = $event) !== false);
                ad = (pd_4 && ad);
            }
            return ad;
        }, null, null)),
        import0.ɵdid(16384, null, 0, import4.DefaultValueAccessor, [
            import0.Renderer,
            import0.ElementRef,
            [
                2,
                import4.COMPOSITION_BUFFER_MODE
            ]
        ], null, null),
        import0.ɵprd(1024, null, import4.NG_VALUE_ACCESSOR, function (p0_0) {
            return [p0_0];
        }, [import4.DefaultValueAccessor]),
        import0.ɵdid(671744, null, 0, import4.NgModel, [
            [
                2,
                import4.ControlContainer
            ],
            [
                8,
                null
            ],
            [
                8,
                null
            ],
            [
                2,
                import4.NG_VALUE_ACCESSOR
            ]
        ], {
            name: [
                0,
                'name'
            ],
            model: [
                1,
                'model'
            ]
        }, { update: 'ngModelChange' }),
        import0.ɵprd(2048, null, import4.NgControl, null, [import4.NgModel]),
        import0.ɵdid(16384, null, 0, import4.NgControlStatus, [import4.NgControl], null, null),
        (l()(), import0.ɵted(null, ['\n\n                                '])),
        (l()(), import0.ɵand(16777216, null, null, 1, null, View_UpdateEmailComponent_3)),
        import0.ɵdid(16384, null, 0, import5.NgIf, [
            import0.ViewContainerRef,
            import0.TemplateRef
        ], { ngIf: [
                0,
                'ngIf'
            ]
        }, null),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵted(null, ['\n\n                            '])),
        (l()(), import0.ɵeld(0, null, null, 28, 'div', [[
                'class',
                'form-actions'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 25, 'div', [[
                'class',
                'row'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 10, 'div', [[
                'class',
                'col-sm-6 col-sm-push-6'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                        '])),
        (l()(), import0.ɵeld(0, null, null, 7, 'div', [[
                'class',
                'accountActions'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                            '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'button', [
            [
                'class',
                'btn btn-primary btn-block'
            ],
            [
                'type',
                'submit'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                                '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['保存更新'])),
        (l()(), import0.ɵted(null, ['\n\n                                            '])),
        (l()(), import0.ɵted(null, ['\n                                        '])),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵeld(0, null, null, 10, 'div', [[
                'class',
                'col-sm-6 col-sm-pull-6'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                        '])),
        (l()(), import0.ɵeld(0, null, null, 7, 'div', [[
                'class',
                'accountActions'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                            '])),
        (l()(), import0.ɵeld(0, null, null, 4, 'button', [
            [
                'class',
                'btn btn-default btn-block backToHome'
            ],
            [
                'type',
                'button'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                                '])),
        (l()(), import0.ɵeld(0, null, null, 1, null, null, null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['取消'])),
        (l()(), import0.ɵted(null, ['\n                                            '])),
        (l()(), import0.ɵted(null, ['\n                                        '])),
        (l()(), import0.ɵted(null, ['\n                                    '])),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵeld(0, null, null, 3, 'div', [], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                                '])),
        (l()(), import0.ɵeld(0, null, null, 0, 'input', [
            [
                'name',
                'CSRFToken'
            ],
            [
                'type',
                'hidden'
            ],
            [
                'value',
                '2b51bfa7-e433-4941-9f55-16b6d17649a4'
            ]
        ], null, null, null, null, null)),
        (l()(), import0.ɵted(null, ['\n                            '])),
        (l()(), import0.ɵted(null, ['\n                        '])),
        (l()(), import0.ɵted(null, ['\n                    '])),
        (l()(), import0.ɵted(null, ['\n                '])),
        (l()(), import0.ɵted(null, ['\n            '])),
        (l()(), import0.ɵted(null, ['\n        '])),
        (l()(), import0.ɵted(null, ['\n    '])),
        (l()(), import0.ɵted(null, ['\n'])),
        (l()(), import0.ɵted(null, ['\n']))
    ], function (ck, v) {
        var co = v.component;
        var currVal_0 = co.successMsgs;
        var currVal_1 = co.errorMsgs;
        ck(v, 1, 0, currVal_0, currVal_1);
        var currVal_16 = 'email';
        var currVal_17 = co.emailAddress;
        ck(v, 44, 0, currVal_16, currVal_17);
        var currVal_18 = co.getValidationError('newLogin');
        ck(v, 49, 0, currVal_18);
        var currVal_26 = 'checkEmail';
        var currVal_27 = co.checkEmailAddress;
        ck(v, 63, 0, currVal_26, currVal_27);
        var currVal_28 = co.getValidationError('checkEmailAddress');
        ck(v, 68, 0, currVal_28);
        var currVal_36 = 'password';
        var currVal_37 = co.password;
        ck(v, 82, 0, currVal_36, currVal_37);
        var currVal_38 = co.getValidationError('password');
        ck(v, 87, 0, currVal_38);
    }, function (ck, v) {
        var currVal_2 = import0.ɵnov(v, 31).ngClassUntouched;
        var currVal_3 = import0.ɵnov(v, 31).ngClassTouched;
        var currVal_4 = import0.ɵnov(v, 31).ngClassPristine;
        var currVal_5 = import0.ɵnov(v, 31).ngClassDirty;
        var currVal_6 = import0.ɵnov(v, 31).ngClassValid;
        var currVal_7 = import0.ɵnov(v, 31).ngClassInvalid;
        var currVal_8 = import0.ɵnov(v, 31).ngClassPending;
        ck(v, 27, 0, currVal_2, currVal_3, currVal_4, currVal_5, currVal_6, currVal_7, currVal_8);
        var currVal_9 = import0.ɵnov(v, 46).ngClassUntouched;
        var currVal_10 = import0.ɵnov(v, 46).ngClassTouched;
        var currVal_11 = import0.ɵnov(v, 46).ngClassPristine;
        var currVal_12 = import0.ɵnov(v, 46).ngClassDirty;
        var currVal_13 = import0.ɵnov(v, 46).ngClassValid;
        var currVal_14 = import0.ɵnov(v, 46).ngClassInvalid;
        var currVal_15 = import0.ɵnov(v, 46).ngClassPending;
        ck(v, 41, 0, currVal_9, currVal_10, currVal_11, currVal_12, currVal_13, currVal_14, currVal_15);
        var currVal_19 = import0.ɵnov(v, 65).ngClassUntouched;
        var currVal_20 = import0.ɵnov(v, 65).ngClassTouched;
        var currVal_21 = import0.ɵnov(v, 65).ngClassPristine;
        var currVal_22 = import0.ɵnov(v, 65).ngClassDirty;
        var currVal_23 = import0.ɵnov(v, 65).ngClassValid;
        var currVal_24 = import0.ɵnov(v, 65).ngClassInvalid;
        var currVal_25 = import0.ɵnov(v, 65).ngClassPending;
        ck(v, 60, 0, currVal_19, currVal_20, currVal_21, currVal_22, currVal_23, currVal_24, currVal_25);
        var currVal_29 = import0.ɵnov(v, 84).ngClassUntouched;
        var currVal_30 = import0.ɵnov(v, 84).ngClassTouched;
        var currVal_31 = import0.ɵnov(v, 84).ngClassPristine;
        var currVal_32 = import0.ɵnov(v, 84).ngClassDirty;
        var currVal_33 = import0.ɵnov(v, 84).ngClassValid;
        var currVal_34 = import0.ɵnov(v, 84).ngClassInvalid;
        var currVal_35 = import0.ɵnov(v, 84).ngClassPending;
        ck(v, 79, 0, currVal_29, currVal_30, currVal_31, currVal_32, currVal_33, currVal_34, currVal_35);
    });
}
function View_UpdateEmailComponent_Host_0(l) {
    return import0.ɵvid(0, [
        (l()(), import0.ɵeld(0, null, null, 3, 'ng-component', [], null, null, null, View_UpdateEmailComponent_0, RenderType_UpdateEmailComponent)),
        import0.ɵprd(512, null, import6.UserService, import6.UserService, [
            import7.CookieService,
            import8.Http,
            import9.OCCService
        ]),
        import0.ɵprd(512, null, import10.ValidationService, import10.ValidationService, []),
        import0.ɵdid(114688, null, 0, import1.UpdateEmailComponent, [
            import6.UserService,
            import10.ValidationService
        ], null, null)
    ], function (ck, v) {
        ck(v, 3, 0);
    }, null);
}
export var UpdateEmailComponentNgFactory = import0.ɵccf('ng-component', import1.UpdateEmailComponent, View_UpdateEmailComponent_Host_0, {}, {}, []);
//# sourceMappingURL=update-email.component.ngfactory.js.map