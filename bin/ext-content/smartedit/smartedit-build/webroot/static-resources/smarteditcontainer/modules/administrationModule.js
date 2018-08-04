!function(t){function e(o){if(i[o])return i[o].exports;var r=i[o]={i:o,l:!1,exports:{}};return t[o].call(r.exports,r,r.exports,e),r.l=!0,r.exports}var i={};e.m=t,e.c=i,e.d=function(t,i,o){e.o(t,i)||Object.defineProperty(t,i,{configurable:!1,enumerable:!0,get:o})},e.n=function(t){var i=t&&t.__esModule?function(){return t.default}:function(){return t};return e.d(i,"a",i),i},e.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},e.p="",e(e.s=87)}({0:function(t,e){t.exports=angular},87:function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=i(88),r=(i.n(o),i(89));i.d(e,"AdministrationModule",function(){return r.a})},88:function(t,e){angular.module("multiProductCatalogVersionSelectorModule",[]).controller("multiProductCatalogVersionSelectorController",["$q",function(t){this.$onInit=function(){this.OriginalProductCatalogs=[],this.productCatalogs.forEach(function(e){e.fetchStrategy={fetchAll:function(){return t.when(e.versions)}},e.versions.forEach(function(t){t.id=t.uuid,t.label=t.version}),e.selectedItem=e.versions.find(function(t){return this.selectedVersions.indexOf(t.uuid)>-1}.bind(this)).uuid}.bind(this))},this.updateModel=function(){var t=this.productCatalogs.map(function(t){return t.selectedItem});this.onSelectionChange({$selectedVersions:t})}.bind(this)}]).component("multiProductCatalogVersionSelector",{templateUrl:"multiProductCatalogVersionSelectorTemplate.html",controller:"multiProductCatalogVersionSelectorController",bindings:{productCatalogs:"<",selectedVersions:"<",onSelectionChange:"&"}}),angular.module("seProductCatalogVersionsSelectorModule",["catalogServiceModule","modalServiceModule","multiProductCatalogVersionSelectorModule","eventServiceModule","yLoDashModule","l10nModule","seDropdownModule"]).constant("PRODUCT_CATALOG_SINGLE_TEMPLATE","productsCatalogSelectSingleTemplate.html").constant("PRODUCT_CATALOG_MULTIPLE_TEMPLATE","productsCatalogSelectMultipleTemplate.html").constant("MULTI_PRODUCT_CATALOGS_UPDATED","MULTI_PRODUCT_CATALOGS_UPDATED").controller("seProductCatalogVersionsSelectorController",["$q","$log","$translate","lodash","l10nFilter","catalogService","modalService","systemEventService","PRODUCT_CATALOG_SINGLE_TEMPLATE","PRODUCT_CATALOG_MULTIPLE_TEMPLATE","MODAL_BUTTON_ACTIONS","MODAL_BUTTON_STYLES","MULTI_PRODUCT_CATALOGS_UPDATED","LINKED_DROPDOWN",function(t,e,i,o,r,n,s,a,l,c,u,d,h,f){this.onClick=function(t,e){this.isTooltipOpen=!1,s.open({title:"se.modal.product.catalog.configuration",size:"md",templateUrl:"multiProductCatalogVersionsConfigurationsTemplate.html",controller:["$q","$log","MULTI_PRODUCT_CATALOGS_UPDATED","modalManager","systemEventService",function(i,o,r,n,s){this.productCatalogs=t,this.selectedCatalogVersions=e,this.onSave=function(){s.sendAsynchEvent(r,this.updatedCatalogVersions),n.close()},this.onCancel=function(){return n.close(),i.when({})},this.updateSelection=function(t){JSON.stringify(t)!==JSON.stringify(this.selectedCatalogVersions)?(this.updatedCatalogVersions=t,n.enableButton("done")):n.disableButton("done")},this.init=function(){n.setDismissCallback(this.onCancel.bind(this)),n.setButtonHandler(function(t){switch(t){case"done":return this.onSave();case"cancel":return this.onCancel();default:o.error("A button callback has not been registered for button with id",t)}}.bind(this))}}],buttons:[{id:"cancel",label:"se.confirmation.modal.cancel",style:d.SECONDARY,action:u.DISMISS},{id:"done",label:"se.confirmation.modal.done",action:u.NONE,disabled:!0}]})},this._updateProductCatalogsModel=function(t,e){this.model[this.qualifier]=e},this.parseSingleCatalogVersion=function(t){var e=[];return t.versions.forEach(function(t){e.push({id:t.uuid,label:t.version})}),e},this._resetSelector=function(t,e){if("previewCatalog"===e.qualifier&&this.initialPreview!==e.optionObject.id){this.initialPreview=e.optionObject.id;var i=e.optionObject.id.split("_")[0];n.getProductCatalogsForSite(i).then(function(t){this.productCatalogs=t,this.reset&&this.reset(),n.returnActiveCatalogVersionUIDs(t).then(function(t){this.model[this.qualifier]=t,this._setContent()}.bind(this))}.bind(this))}},this._buildCatalogNameCatalogVersionString=function(t){var e=t.versions.find(function(t){return this.model[this.qualifier]&&this.model[this.qualifier].indexOf(t.uuid)>-1}.bind(this));return this.model[this.qualifier]&&e?r(t.name)+" ("+e.version+")":""},this.buildMultiProductCatalogVersionsTemplate=function(){return"<div class='se-product-catalogs-tooltip'><div class='se-product-catalogs-tooltip__h' >"+i.instant("se.product.catalogs.selector.headline.tooltip")+"</div>"+this.productCatalogs.reduce(function(t,e){return(t+="<div class='se-product-catalog-info'>"+this._buildCatalogNameCatalogVersionString(e))+"</div>"}.bind(this),"")+"</div>"},this.getMultiProductCatalogVersionsSelectedOptions=function(){return this.productCatalogs?this.productCatalogs.reduce(function(t,e,i){return t+=this._buildCatalogNameCatalogVersionString(e),t+=i<this.productCatalogs.length-1?", ":""}.bind(this),""):""},this._setContent=function(){n.getProductCatalogsForSite(this.initialPreview.split("_")[0]).then(function(e){this.productCatalogs=e,1===this.productCatalogs.length&&(this.fetchStrategy={fetchAll:function(){var e=this.parseSingleCatalogVersion(this.productCatalogs[0]);return t.when(e)}.bind(this)},this.isReady=!0,this.isSingleVersionSelector=!0,this.isMultiVersionSelector=!1),this.productCatalogs.length>1&&(this.$unRegEventForMultiProducts=a.registerEventHandler(h,this._updateProductCatalogsModel.bind(this)),this.isReady=!0,this.isSingleVersionSelector=!1,this.isMultiVersionSelector=!0)}.bind(this))},this.$onInit=function(){this.initialPreview=o.cloneDeep(this.model.previewCatalog),this.initialPreview&&(this.isTooltipOpen=!1,this.isReady=!1,this.isSingleVersionSelector=!1,this.isMultiVersionSelector=!1,this.eventId=(this.id||"")+f,this.$unRegSiteChangeEvent=a.registerEventHandler(this.eventId,this._resetSelector.bind(this)),this._setContent())},this.$onDestroy=function(){this.$unRegSiteChangeEvent&&this.$unRegSiteChangeEvent(),this.$unRegEventForMultiProducts&&this.$unRegEventForMultiProducts()}}]).component("seProductCatalogVersionsSelector",{templateUrl:"productCatalogVersionsSelectorTemplate.html",controller:"seProductCatalogVersionsSelectorController",bindings:{field:"<",qualifier:"<",model:"<",id:"<"}})},89:function(t,e,i){"use strict";i.d(e,"a",function(){return r});var o=i(0),r=(i.n(o),o.module("administration",["functionsModule","translationServiceModule","ngResource","loadConfigModule","modalServiceModule","confirmationModalServiceModule","editorFieldMappingServiceModule","seProductCatalogVersionsSelectorModule"]).run(["editorFieldMappingService",function(t){"ngInject";t.addFieldMapping("ProductCatalogVersionsSelector",null,null,{template:"productCatalogVersionsSelectorWrapperTemplate.html"})}]).factory("ConfigurationEditor",["$resource","copy","$q","convertToArray","$log","loadConfigManagerService","ParseError","CONFIGURATION_URI","isBlank",function(t,e,i,o,r,n,s,a,l){"ngInject";var c=/(\"[A-Za-z]+:\/|\/\/)/,u=function(){this.editorCRUDService=t(a,{},{update:{method:"PUT",cache:!1,isArray:!1},remove:{method:"DELETE",cache:!1,isArray:!1},save:{method:"POST",cache:!1,isArray:!1}}),this.configuration=[]};return u.prototype._reset=function(t){this.configuration=e(this.pristine),t&&t.$setPristine(),this.loadCallback&&this.loadCallback()},u.prototype._addError=function(t,e,i){t.errors=t.errors||{},t.errors[e]=t.errors[e]||[],t.errors[e].push({message:i})},u.prototype._addKeyError=function(t,e){this._addError(t,"keys",e)},u.prototype._addValueError=function(t,e){this._addError(t,"values",e)},u.prototype._prettify=function(t){var i=e(t);return i.forEach(function(t){try{t.value=JSON.stringify(JSON.parse(t.value),null,2)}catch(e){this._addValueError(t,"se.configurationform.json.parse.error")}}.bind(this)),i},u.prototype.loadAndPresent=function(){var t=i.defer();return n.loadAsArray().then(function(e){this.pristine=this._prettify(e),this._reset(),t.resolve()}.bind(this),function(){r.log("load failed"),t.reject()}),t.promise},u.prototype.addEntry=function(){this.configuration.unshift({isNew:!0})},u.prototype.removeEntry=function(t,e){t.isNew?this.configuration=this.configuration.filter(function(e){return!e.isNew||e.key!==t.key}):(e.$setDirty(),t.toDelete=!0)},u.prototype.filterConfiguration=function(){return this.configuration.filter(function(t){return!0!==t.toDelete})},u.prototype._validate=function(t){try{if(t.requiresUserCheck&&!t.isCheckedByUser)throw new Error("URI_EXCEPTION");return JSON.stringify(JSON.parse(t.value))}catch(e){throw new s(t.value)}},u.prototype._isValid=function(t){return this.configuration.forEach(function(t){delete t.errors}),t.$invalid&&this.configuration.forEach(function(t){l(t.key)&&(this._addKeyError(t,"se.configurationform.required.entry.error"),t.hasErrors=!0),l(t.value)&&(this._addValueError(t,"se.configurationform.required.entry.error"),t.hasErrors=!0)}.bind(this)),t.$valid&&!this.configuration.reduce(function(t,e){return t.keys.indexOf(e.key)>-1?(this._addKeyError(e,"se.configurationform.duplicate.entry.error"),t.errors=!0):t.keys.push(e.key),t}.bind(this),{keys:[],errors:!1}).errors},u.prototype._validateUserInput=function(t){t.value&&(t.requiresUserCheck=!!t.value.match(c))},u.prototype.submit=function(t){var o=i.defer();return t.$dirty&&this._isValid(t)?(this.configuration.forEach(function(t,i){try{var r=e(t);delete r.toDelete,delete r.errors;var n=!0===t.toDelete?"remove":!0===r.isNew?"save":"update";r.secured=!1,delete r.isNew;var a=void 0;switch(n){case"save":r.value=this._validate(r),a={};break;case"update":r.value=this._validate(r),a={key:r.key};break;case"remove":a={key:r.key},r=void 0}this.editorCRUDService[n](a,r).$promise.then(function(t,e,i){switch(i){case"save":delete t.isNew;break;case"remove":this.configuration.splice(e,1)}}.bind(this,t,i,n),function(){this._addValueError(t,"configurationform.save.error"),o.reject()}.bind(this)),t.hasErrors=!1}catch(e){e instanceof s&&(this._addValueError(t,"se.configurationform.json.parse.error"),o.reject()),t.hasErrors=!0}}.bind(this)),o.resolve(),t.$setPristine()):o.reject(),o.promise},u.prototype.init=function(t){this.loadCallback=t;var e=i.defer();return this.loadAndPresent().then(function(){e.resolve()},function(){e.reject()}),e.promise},u}]).factory("configurationService",["ConfigurationEditor",function(t){"ngInject";return new t}]).directive("generalConfiguration",["modalService","$log","MODAL_BUTTON_ACTIONS","MODAL_BUTTON_STYLES","confirmationModalService",function(t,e,i,o,r){"ngInject";return{templateUrl:"generalConfigurationTemplate.html",restrict:"E",transclude:!0,replace:!0,link:function(n){n.editConfiguration=function(){t.open({title:"se.modal.administration.configuration.edit.title",templateUrl:"editConfigurationsTemplate.html",controller:["$scope","$timeout","yjQuery","configurationService","$q","modalManager",function(t,i,o,n,s,a){this.isDirty=!1,t.form={},this.onSave=function(){t.editor.submit(t.form.configurationForm).then(function(){a.close()})},this.onCancel=function(){var t=s.defer();return this.isDirty?r.confirm({description:"se.editor.cancel.confirm"}).then(function(){a.close(),t.resolve()}.bind(this),function(){t.reject()}):t.resolve(),t.promise},this.init=function(){a.setDismissCallback(this.onCancel.bind(this)),a.setButtonHandler(function(t){switch(t){case"save":return this.onSave();case"cancel":return this.onCancel();default:e.error("A button callback has not been registered for button with id",t)}}.bind(this)),t.$watch(function(){return{isDirty:t.form.configurationForm&&t.form.configurationForm.$dirty,isValid:t.form.configurationForm&&t.form.configurationForm.$valid}}.bind(this),function(t){"boolean"==typeof t.isDirty&&(t.isDirty?(this.isDirty=!0,a.enableButton("save")):(this.isDirty=!1,a.disableButton("save")))}.bind(this),!0)},t.editor=n,t.editor.init(function(){i(function(){o("textarea").each(function(){o(this).height(this.scrollHeight)})},100)})}],buttons:[{id:"cancel",label:"se.cms.component.confirmation.modal.cancel",style:o.SECONDARY,action:i.DISMISS},{id:"save",label:"se.cms.component.confirmation.modal.save",action:i.NONE,disabled:!0}]})}}}}]).name)}});