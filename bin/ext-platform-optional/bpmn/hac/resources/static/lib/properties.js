(function () {
	propertiesEditor = {
		selectedObject: undefined,
		currentObjContainer: {},
		bindHandlers: function(){
            $('#endEventMessage').on('keyup paste',this.updateEndEventMessage());
			$('#processName').on('keyup paste',this.updateProcessName());
			$('#processClassName').on('keyup paste',this.updateProcessClassName());
			$('#script').on('keyup paste',this.updateScript());
			$('#scriptType').on('keyup paste',this.updateScript());
			
			$("#processClass").on('click',this.enableProcessClassNameEditing());
			$('#dataObjectType').on('keyup paste',this.updateDataObject());
			$('#dataObjectRequired').on('click',this.updateDataObject());
			
			$("#timerYears").on('keyup paste',this.updateTimeEvent());
			$("#timerMonths").on('keyup paste',this.updateTimeEvent());
			$("#timerDays").on('keyup paste',this.updateTimeEvent());
			$("#timerHours").on('keyup paste',this.updateTimeEvent());
			$("#timerMins").on('keyup paste',this.updateTimeEvent());
			$("#timerSec").on('keyup paste',this.updateTimeEvent());
			
			$("#defaultFlow").on('click',this.updateDefaultFlow());
			$("#name").on('keyup paste',this.updateObjectName());
        },
		enableProcessClassNameEditing: function(){
			var self = this;
			var handler = function(oEvent){
				var isChecked = oEvent.currentTarget.checked;
				if(isChecked){
					$('#processClassName').prop('disabled', false);
					$("#name").prop('disabled', true);
					$('#dataObjectType').prop('disabled', true);
					$('#dataObjectRequired').prop('disabled', true);
					$('#processClassName').val("");
					$('#name').val("");
				}else{
					$('#processClassName').prop('disabled', true);
					$("#name").prop('disabled', false);
					$('#dataObjectType').prop('disabled', false);
					$('#dataObjectRequired').prop('disabled', false);
				}
			};
			return handler;
		},
		setProcessName: function(processName){
			$("#processName").val(processName);
		},
		setProcessClassName: function(processClassName){
			$("#processClassName").val(processClassName);
			//this.processClass.setValue(processClass);
		},
		setObjectName: function(name){
			$("#name").val(name);
		},
		setObjectType: function(object){
			selectedObjectsClassDef = object.classDefinition;
			selectedObjectType = selectedObjectsClassDef.qualifiedName;
			type = object.type;
			if(selectedObjectType == "sap.modeling.bpmn.Task"){
				if(type == "script"){
					selectedObjectType = "Script Task";
				}else{
					selectedObjectType = "Normal Task";
				}
			}else if(selectedObjectType == "sap.modeling.bpmn.Gateway"){
				if(type == "exclusive"){
					selectedObjectType = "Exclusive Gateway";
				}else if(type == "eventBased"){
					selectedObjectType = "Event Based Gateway";
				}
			}else if(selectedObjectType == "sap.modeling.bpmn.IntermediateCatchEvent"){
				eventType = object.eventType;
				if(eventType == "timer"){
					selectedObjectType = "Timer Event";
				}else{
					selectedObjectType = "Normal Event";
				}
			}else if(selectedObjectType == "hybris.modeling.bpmn.EndEvent"){
				selectedObjectType = "End Event";
			}else if(selectedObjectType == "hybris.modeling.bpmn.StartEvent"){
				selectedObjectType = "Start Event";
			}else if(selectedObjectType == "hybris.modeling.bpmn.DataObject"){
				selectedObjectType = "Data Object";
			}else if(selectedObjectType == "sap.modeling.bpmn.SequenceFlow"){
				selectedObjectType = "Sequence Flow";
			}
			$('#object').val(selectedObjectType);
		},
		setUnSelectedObject: function(){
			$('#scriptType').val("");
			$('#scriptTypeLabel').hide()
			$('#scriptType').hide();
			$('#script').val("");
			$('#scriptLabel').hide();
			$('#script').hide();
			
			$("#timerYearsLabel").hide();
			$("#timerYears").hide();
			$("#timerYears").val("");
			$("#timerMonthsLabel").hide();
			$("#timerMonths").hide();
			$("#timerMonths").val("");
			$("#timerDaysLabel").show();
			$("#timerDaysLabel").hide();
			$("#timerDays").hide();
			$("#timerDays").val("");
			
			$("#timerHoursLabel").hide();
			$("#timerHours").hide();
			$("#timerHours").val("");
			$("#timerMinsLabel").hide();
			$("#timerMins").hide();
			$("#timerMins").val("");
			$("#timerSecLabel").hide();
			$("#timerSec ").hide();
			$("#timerSec ").val("");
			
			$('#endEventMessage').val("");
			$('#endEventMessageLabel').hide();
			$('#endEventMessage').hide();
			
			$('#defaultFlowLabel').hide();
			$('#defaultFlow').hide();
			
			$('#processClassLabel').hide();
			$('#processClass').hide();
			$('#processClass').prop("checked",false);
			$('#dataObjectRequiredLabel').hide();
			$('#dataObjectRequired').prop("checked",false);
			$('#dataObjectRequired').hide();
			$('#dataObjectTypeLabel').hide();
			$('#dataObjectType').hide();
			$('#dataObjectType').val("");
		},
		setSelectedObject: function(object){
			this.selectedObject = object;
			this.currentObjContainer["currentSelection"] = object;
			
			var displayName = this.selectedObject.displayName;
			
			this.setObjectType(this.selectedObject);
			
			selectedObjectsClassDef = object.classDefinition;
			selectedObjectType = selectedObjectsClassDef.qualifiedName;
			type = object.type;
			
			$("#name").prop('disabled', false);
			if(selectedObjectType == "sap.modeling.bpmn.Task" && type == "script"){
				 $('#scriptTypeLabel').show();
				 $('#scriptType').show();
				 $('#scriptLabel').show();
				 $('#script').show();

				 this.handleScript(this.selectedObject.script);
			}else{
				$('#scriptType').val("");
				$('#scriptTypeLabel').hide()
				$('#scriptType').hide();
				$('#script').val("");
				$('#scriptLabel').hide();
				$('#script').hide();
			}
			if(selectedObjectType == "sap.modeling.bpmn.IntermediateCatchEvent" && object.eventType == "timer"){
				$("#timerYearsLabel").show();
				$("#timerYears").show();
				$("#timerMonthsLabel").show();
				$("#timerMonths").show();
				$("#timerDaysLabel").show();
				$("#timerDays").show();
				
				$("#timerHoursLabel").show();
				$("#timerHours").show();
				$("#timerMinsLabel").show();
				$("#timerMins").show();
				$("#timerSecLabel").show();
				$("#timerSec ").show();
				
				this.handleTimerEvent(object);
				
			}else{
				$("#timerYearsLabel").hide();
				$("#timerYears").hide();
				$("#timerYears").val("");
				$("#timerMonthsLabel").hide();
				$("#timerMonths").hide();
				$("#timerMonths").val("");
				$("#timerDaysLabel").show();
				$("#timerDaysLabel").hide();
				$("#timerDays").hide();
				$("#timerDays").val("");
				
				$("#timerHoursLabel").hide();
				$("#timerHours").hide();
				$("#timerHours").val("");
				$("#timerMinsLabel").hide();
				$("#timerMins").hide();
				$("#timerMins").val("");
				$("#timerSecLabel").hide();
				$("#timerSec ").hide();
				$("#timerSec ").val("");
				
			}
			if(selectedObjectType == "hybris.modeling.bpmn.EndEvent"){
				
				splits = displayName.split("[");
				var message = "";
				if(splits.length > 1){
					displayName = splits[0];
					messagePart = splits[1];
					splits = messagePart.split("]");
					message = splits[0];
				}
				
				$('#endEventMessageLabel').show();
				$('#endEventMessage').show();
				$('#endEventMessage').val(message);
				
			}else{
				$('#endEventMessage').val("");
				$('#endEventMessageLabel').hide();
				$('#endEventMessage').hide();
			}
			
			if(selectedObjectType == "sap.modeling.bpmn.SequenceFlow"){
				this.handleSequenceFlow(object);
			}else{
				$('#defaultFlowLabel').hide();
				$('#defaultFlow').hide();
			}
			
			if(selectedObjectType == "hybris.modeling.bpmn.DataObject"){
				$('#processClassLabel').show();
				$('#processClass').show();
				$('#dataObjectRequiredLabel').show();
				$('#dataObjectRequired').show();
				$('#dataObjectTypeLabel').show();
				$('#dataObjectType').show();
				displayName = this.handleDataObject(object);
			}else{
				$('#processClassLabel').hide();
				$('#processClass').hide();
				$('#dataObjectRequiredLabel').hide();
				$('#dataObjectRequired').hide();
				$('#dataObjectTypeLabel').hide();
				$('#dataObjectType').hide();
			}
			
			if(!displayName){
				displayName = " ";
			}
			
			this.setObjectName(displayName);
		},
		handleDataObject: function(object){
				
			var fullName = object.name;
			var splits = fullName.split(" ");
			var isProcessContext = object.isProcessContext;
			var dataObjectCount = object.rootContainer.data.length;
			var processClassDataObjectExist = false;
			for(i=0;i<dataObjectCount;i++){
				var dataObj = object.rootContainer.data.get(i);
				if(dataObj.classDefinition.qualifiedName == "hybris.modeling.bpmn.DataObject"){
					processClassDataObjectExist = processClassDataObjectExist || !dataObj.isProcessContext;
				}
			}
			if(processClassDataObjectExist){
				$('#processClass').prop('checked', false);
				$('#processClass').prop('disabled', true);
			}else{
				$('#processClass').prop('disabled', false);
			}
			if((isProcessContext != undefined && isProcessContext == false ) || fullName.indexOf("processClass") > 0){
				$('#processClassName').prop('disabled', false);
				$("#name").prop('disabled', true);
				$('#dataObjectType').prop('disabled', true);
				$('#dataObjectRequired').prop('disabled', true);
				$('#dataObjectRequired').prop('checked', false);
				$('#dataObjectType').val("");
				$('#processClass').prop('checked', true);
			}else{
				$('#processClassName').prop('disabled', true);
				$("#name").prop('disabled', false);
				$('#dataObjectType').prop('disabled', false);
				$('#dataObjectRequired').prop('disabled', false);
				
				$('#processClass').prop('checked', false);
				if(splits.length > 0){
					var optionalOrReq = splits[0];
					var type = splits[1];
					if(optionalOrReq == "required"){						
						$('#dataObjectRequired').prop('checked', true);
					}else{
						$('#dataObjectRequired').prop('checked', false);
					}
					$('#dataObjectType').val(type)
					
					return splits[splits.length-1];
				}else{
					return fullName;
				}
			}
		},
		handleSequenceFlow: function(object){
			var source = object.source;
			var sourceClassDef = source.classDefinition;
			var sourceClassType = sourceClassDef.qualifiedName;
			var gatewayType = source.type;
			var direction = source.direction;
			if(direction == undefined && gatewayType == "exclusive"){//It might not have been set...
				var sequenceFlows = source.rootContainer.sequenceFlows;
				var sequenceFlowCount = sequenceFlows.length;
				var counter = 0;
				for(var i=0;i<sequenceFlowCount;i++){
					var sequenceFlow = sequenceFlows.get(i);
					var sourceObject = sequenceFlow.source;
					
					if(source.objectId == sourceObject.objectId){
						counter++;
					}
					if(counter > 1){
						direction = "Diverging";
						break;
					}
				}
			}
			if(sourceClassType == "sap.modeling.bpmn.Gateway" && gatewayType == "exclusive" && direction == "Diverging"){
				var defaultFlow = source.defaultFlow;
				if(defaultFlow){
					var defaultFlowId = defaultFlow.objectId;
					if(defaultFlowId == object.objectId){
						$('#defaultFlow').removeAttr("disabled");
						$('#defaultFlow').prop('checked', true);
					}else if(defaultFlow && defaultFlowId != object.objectId){
						$('#defaultFlow').attr("disabled", true);
						$('#defaultFlow').prop('checked', false);
					}else{
						$('#defaultFlow').removeAttr("disabled");
						$('#defaultFlow').prop('checked', false);
					}
				}else{
					var parentofExclusiveChoice = source.getTargetLinkObjects(0)[0].source.classDefinition.qualifiedName;
					if(parentofExclusiveChoice != undefined && parentofExclusiveChoice == "sap.modeling.bpmn.IntermediateCatchEvent"){						
						$('#defaultFlow').removeAttr("disabled");
						$('#defaultFlow').prop('checked', false);
					}else{
						$('#defaultFlow').attr("disabled", true);
						$('#defaultFlow').prop('checked', false);
					}
				}
				$('#defaultFlowLabel').show();
				$('#defaultFlow').show();
			}else{
				$('#defaultFlowLabel').hide();
				$('#defaultFlow').hide();
			}
		},
		handleTimerEvent: function(object){
			var timeDuration = object.eventDefinitions.get(0).timeDuration;
			
			if(timeDuration){				
				var duration = timeDuration.toString();
				var indexOfT = duration.indexOf('T');
				if(indexOfT > -1){
					var pPart = duration.substring(1,indexOfT);
					var tPart = duration.substring(indexOfT+1);
					if(pPart){
						var yearsSplit = pPart.split(/Y/);
						var next;
						if(yearsSplit.length > 1){
							var years = yearsSplit[0];
							next = yearsSplit[1];
							if(years){
								dateTime = years;
							}else{
								dateTime = "0";
							}
							$("#timerYears").val(years);
						}else{
							next = yearsSplit[0];
						}
						if(next){
							var monthsSplit = next.split(/M/);
							if(monthsSplit.length > 1){
								var months = monthsSplit[0];
								next = monthsSplit[1];
								if(months){
									dateTime = dateTime+"-"+months;
								}else{
									dateTime = dateTime+"-0";
								}
								$("#timerMonths").val(months);
								//this.timerEventMonths.setValue(months);
							}else{
								next = monthsSplit[0];
							}
						}
						if(next){
							var daysSplit = next.split(/D/);
							if(daysSplit.length > 1){
								var days = daysSplit[0];
								next = yearsSplit[1];
								$("#timerDays").val(days);
							}else{
								next = daysSplit[0];
							}
						}
					}
					if(tPart){
						var hrSplit = tPart.split(/H/);
						var next;
						if(hrSplit.length > 1){
							var hours = hrSplit[0];
							next = hrSplit[1];
							$("#timerHours").val(hours);
						}else{
							next = hrSplit[0];
						}
						if(next){						
							var minSplit = next.split(/M/);
							if(minSplit.length > 1){
								var mins = minSplit[0];
								next = minSplit[1];
								$("#timerMins").val(mins);
							}else{
								next = minSplit[0];
							}
						}
						if(next){
							var secSplit = next.split(/S/);
							if(secSplit.length > 1){
								var secs = secSplit[0];
								next = secSplit[1];
								$("#timerSec").val(secs);
							}else{
								next = secSplit[0];
							}
						}
					}
				}
				
			}			
		},
		handleScript: function(scriptTextXml){
			var scriptText = scriptTextXml.substring(scriptTextXml.indexOf('>')+1,scriptTextXml.indexOf('</'));
			var scriptType = scriptTextXml.substring(scriptTextXml.indexOf('type=')+6,scriptTextXml.indexOf('>')-1);
			$('#scriptType').val(scriptType);
			$('#script').val(scriptText);
		},
		updateScript: function(){
			var self = this;
			var handler = function(oEvent){
				var object = self.currentObjContainer["currentSelection"];
				//var newValue = oEvent.currentTarget.value;
				var scriptType = $('#scriptType').val();
				var scriptText =  $('#script').val();
				var scriptTextXml = "<![CDATA[<script type='"+scriptType+"'>"+scriptText+"</script>]]";
				object.script = scriptTextXml;
			};
			return handler;
		},		
		updateEndEventMessage: function(){
			var self = this;
			var handler = function(oEvent){							
				var object = self.currentObjContainer["currentSelection"];
				var newValue = oEvent.currentTarget.value;
				var displayName = object.displayName;
				
				splits = displayName.split("[");
				var message = "";
				if(splits.length > 1){
					displayName = splits[0];
								
				}
				if(newValue){
					object.displayName = displayName+"["+newValue+"]"
					object.name = displayName+"["+newValue+"]"
				}else{
					object.displayName = displayName;
					object.name = displayName;
				}
			};
			return handler;
		},		
		updateDataObject: function(){
			var self = this;
			var handler = function(oEvent){
				var object = self.currentObjContainer["currentSelection"];
				var displayName = object.displayName;
				
				//var source = oEvent.currentTarget.id;
				
				var splits = displayName.split(" ");
				var optionalOrReq = splits[0];
				var type = splits[1];
				var name = splits[3];
				
				type = $("#dataObjectType").val();
				optionalOrReq = $('#dataObjectRequired').is(":checked") ? "required" : "optional";
				object.displayName = optionalOrReq + " " + type + " as " + $("#name").val();
				object.name = optionalOrReq + " " + type + " as " + $("#name").val();
			};
			return handler;
		},
		updateDefaultFlow: function(){
			var self = this;
			var handler = function(oEvent){
				var object = self.currentObjContainer["currentSelection"];
				var isChecked = oEvent.currentTarget.checked;
				
				var source = object.source;
				var sourceClassDef = source.classDefinition;
				var sourceClassType = sourceClassDef.qualifiedName;
				var gatewayType = source.type;
				if(sourceClassType == "sap.modeling.bpmn.Gateway" && gatewayType == "exclusive"){
					if(isChecked){
						source.defaultFlow = object;
						object.displayName = undefined;
						object.name = undefined;
					}else{
						source.defaultFlow = undefined;
					}
				}
			};
			return handler;
		},	
		updateTimeEvent: function(){
			var self = this;
			var handler = function(oEvent){
				var object = self.currentObjContainer["currentSelection"];
				var timeDuration = object.eventDefinitions.get(0).timeDuration;
				var srcField = oEvent.currentTarget.id;
				
				var year = $("#timerYears").val();
				var months = $("#timerMonths").val();
				var days = $("#timerDays").val();
				var hours = $("#timerHours").val();
				var minutes = $("#timerMins").val();
				var seconds = $("#timerSec ").val();
				
				var data = "P";
				if(year != undefined && year.length>0 ){
					data = data + year +"Y";
				}
				if(months != undefined && months.length>0 ){
					data = data + months +"M";
				}
				if(days != undefined && days.length>0 ){
					data = data + days +"D";
				}
				if((hours != undefined && hours.length>0)
					|| (minutes != undefined && minutes.length>0)
					|| (seconds != undefined && seconds.length>0)){
						
					data = data + "T";
					if(hours != undefined && hours.length>0 ){
						data = data + hours +"H";
					}
					if(minutes != undefined && minutes.length>0 ){
						data = data + minutes +"M";
					}
					if(seconds != undefined && seconds.length>0 ){
						data = data + seconds +"S";
					}
				}
				
				if(data.length > 1){					
					var timeDuration = object.eventDefinitions.get(0).timeDuration;
					if(timeDuration == undefined){
						timeDuration = {
							"__text":undefined,
							"_xsi:type":undefined, 
							"__prefix": undefined, 
							"toString": function(){
								return this.__text;
							}
						};
						object.eventDefinitions.get(0)["timeDuration"] = timeDuration;
					}
					timeDuration["__text"] = data;
				}
			};
			return handler;
		},
		updateObjectName: function(){
			var self = this;
			var handler = function(oEvent){
				var object = self.currentObjContainer["currentSelection"];
				var displayName = object.displayName;
				if(selectedObjectType == "hybris.modeling.bpmn.EndEvent"){
					splits = displayName.split("[");
					var message = "";
					if(splits.length > 1){
						//displayName = splits[0];
						messagePart = splits[1];
						splits = messagePart.split("]");
						message = splits[0];
					}
					if(message){
						displayName = oEvent.currentTarget.value + "["+message+"]"
					}else{
						displayName = oEvent.currentTarget.value;
					}
				}else if(selectedObjectType == "hybris.modeling.bpmn.DataObject"){
					var splits = displayName.split(" ");
					if(displayName.indexOf("processClass") > 0){
					}else{
						if(splits.length > 0){
							var optionalOrReq = splits[0];
							var type = splits[1];
							var name = oEvent.currentTarget.value;
							displayName = optionalOrReq +" " + type + " as " + name;
						}else{
							return displayName;
						}
					}
				}else{
					displayName = oEvent.currentTarget.value;
				}
				object.displayName = displayName;
				object.name = displayName;
			};
			return handler;
		},
		updateProcessName: function(){
			var self = this;
			var handler = function(oEvent){
				var editor = self.currentObjContainer["editor"];
				editor.diagram.name = oEvent.currentTarget.value;
			};
			return handler;
		},
		updateProcessClassName: function(){
			var self = this;
			var handler = function(oEvent){
				var dataObj = self.currentObjContainer["currentSelection"];
				var providedName = oEvent.currentTarget.value;
				dataObj.displayName = providedName;
				dataObj.name = providedName  +" as processClass";
				dataObj.isProcessContext = false;
				/*if(providedName.indexOf("as processClass") > 0){
					dataObj.displayName = providedName;
					dataObj.name = providedName;
				}else{
					var expectedName = providedName +" as processClass";
					oEvent.currentTarget.value = expectedName
					dataObj.displayName = expectedName;
					dataObj.name = expectedName;
				}*/
			};
			return handler;
		}
	}
}());