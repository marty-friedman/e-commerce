!function(e){function t(a){if(r[a])return r[a].exports;var n=r[a]={i:a,l:!1,exports:{}};return e[a].call(n.exports,n,n.exports,t),n.l=!0,n.exports}var r={};t.m=e,t.c=r,t.d=function(e,r,a){t.o(e,r)||Object.defineProperty(e,r,{configurable:!1,enumerable:!0,get:a})},t.n=function(e){var r=e&&e.__esModule?function(){return e.default}:function(){return e};return t.d(r,"a",r),r},t.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},t.p="",t(t.s=0)}([function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=r(1),n=(r.n(a),r(2));r.n(n);a.module("ysmarteditmodule",["featureServiceModule","perspectiveServiceModule","decoratorServiceModule","abAnalyticsDecoratorModule"]).run(["decoratorService","featureService","perspectiveService",function(e,t,r){"ngInject";e.addMappings({SimpleResponsiveBannerComponent:["abAnalyticsDecorator"],CMSParagraphComponent:["abAnalyticsDecorator"]}),t.addDecorator({key:"abAnalyticsDecorator",nameI18nKey:"ab.analytics.feature.name"}),r.register({key:"abAnalyticsPerspective",nameI18nKey:"ab.analytics.perspective.name",descriptionI18nKey:"ab.analytics.perspective.description",features:["abAnalyticsToolbarItem","abAnalyticsDecorator"],perspectives:[]})}])},function(e,t){e.exports=angular},function(e,t){angular.module("abAnalyticsServiceModule",[]).service("abAnalyticsService",["$q",function(e){this.getABAnalyticsForComponent=function(){return e.when({aValue:30,bValue:70})}}]),angular.module("abAnalyticsDecoratorModule",["ysmarteditmoduleTemplates","ui.bootstrap","pascalprecht.translate","abAnalyticsDecoratorControllerModule"]).directive("abAnalyticsDecorator",function(){return{templateUrl:"abAnalyticsDecoratorTemplate.html",restrict:"C",transclude:!0,replace:!1,controller:"abAnalyticsDecoratorController",controllerAs:"$ctrl",bindToController:{smarteditComponentId:"@",smarteditComponentType:"@",smarteditProperties:"@",active:"<"}}}),angular.module("abAnalyticsDecoratorControllerModule",["abAnalyticsServiceModule"]).controller("abAnalyticsDecoratorController",["abAnalyticsService",function(e){this.title="AB Analytics",this.contentTemplate="abAnalyticsDecoratorContentTemplate.html",this.$onInit=function(){e.getABAnalyticsForComponent(this.smarteditComponentId).then(function(e){this.abAnalytics="A: "+e.aValue+" B: "+e.bValue}.bind(this))}}]),angular.module("ysmarteditmoduleTemplates",[]).run(["$templateCache",function(e){"use strict";e.put("web/features/ysmarteditmodule/abAnalyticsDecorator/abAnalyticsDecoratorContentTemplate.html","<div>\n    <p>\n        <strong>{{ $ctrl.smarteditComponentId | limitTo:16 }}{{ $ctrl.smarteditComponentId.length > 16 ? '...' : ''}}</strong>\n    </p>\n    <p>{{ $ctrl.smarteditComponentType | limitTo:16 }}{{ $ctrl.smarteditComponentType.length > 16 ? '...' : ''}}</p>\n    <p>{{ $ctrl.abAnalytics }}</p>\n</div>"),e.put("web/features/ysmarteditmodule/abAnalyticsDecorator/abAnalyticsDecoratorTemplate.html",'<div>\n    <div class="row" data-uib-popover-template="$ctrl.contentTemplate" data-popover-title="{{$ctrl.title}}" data-popover-placement="\'top\'" data-popover-trigger="\'mouseenter\'">\n        <div data-ng-transclude></div>\n    </div>\n</div>')}])}]);