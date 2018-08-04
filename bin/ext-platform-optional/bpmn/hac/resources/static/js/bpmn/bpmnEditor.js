$(function()
{
	propertiesEditor.bindHandlers();
// Create a diagram editor under parent node
var oResource = new sap.galilei.model.Resource();
var isPropertyDisabled=false;

$('input[id=upload]').change(function(ev) {
	var file = $("#upload").prop('files')[0];
	if (file) {
		propertiesEditor.setProcessName("");
		propertiesEditor.setProcessClassName("");
		// create reader
		var reader = new FileReader();
		reader.readAsText(file);
		reader.onload = function(e) {
			var oNewResource = new sap.galilei.model.Resource(),
				oReader = new sap.galilei.model.JSONReader();

			sap.modeling.bpmn.ui.Bpmn2GalileiTransformation.prototype.setScript = function(oTarget, oSource, sSourceType){
				var scriptData = oSource.script.__cdata;
				if(scriptData == undefined){
					scriptData = oSource.script.__text;
				}
				this.setTargetProperty(oTarget, "script", scriptData);
			};

			var oTrans = new sap.modeling.bpmn.ui.Bpmn2GalileiTransformation();

			oTrans.setEventDefinitions = function (oTarget, oSource, sSourceType) {
				sap.modeling.bpmn.ui.Bpmn2GalileiTransformation.prototype.setEventDefinitions.call(this,oTarget,oSource,sSourceType);
				var aDefs = this.getSourceReference(oSource, "timerEventDefinition");
				var eventDef = oTarget.eventDefinitions;
				if(eventDef.length > 0 && eventDef.get(0) && eventDef.get(0).type == 'timer'){
					eventDef.get(0).set('timeDuration',aDefs[0].timeDuration);
				}
			};
			oTrans.transformSymbol = function (oSourceSymbol, sSourceType, sSourceDiagramId) {
				var oTargetSymbol = sap.modeling.bpmn.ui.Bpmn2GalileiTransformation.prototype.transformSymbol.call(this,oSourceSymbol, sSourceType, sSourceDiagramId);
				var targetType = oTargetSymbol.classDefinition.qualifiedName;
				if(targetType == "sap.modeling.bpmn.ui.DataObjectSymbol"){
					var targetObjectName = oTargetSymbol.object.name;
					if(targetObjectName.indexOf("processClass")>0){
						var splits = targetObjectName.split(" ");
						propertiesEditor.setProcessClassName(splits[0]);
					}
				}
			};

			oTrans.typeMapping["dataObject"] = "hybris.modeling.bpmn.DataObject";
			oTrans.typeMapping["endEvent"] = "hybris.modeling.bpmn.EndEvent";

			oTrans.propertyMapping["dataObject"]["extensionElements"] = function(oTarget, oSource, sSourceType){
				var extensionElements = oSource.extensionElements;
				if(extensionElements){
					var processContext = extensionElements.processContext;
					if(processContext != undefined){
						var processContextValue = processContext.toString();

						oTarget.isProcessContext = (processContextValue == 'true')?true:false;
					}else{
						oTarget.isProcessContext = false;
					}
					if(oTarget.isProcessContext != true){
						var dataType = extensionElements.dataType;
						if(dataType != undefined){
							oTarget.dataType = dataType.toString();
						}else{
							oTarget.dataType = undefined;
						}

						var required = extensionElements.required;
						if(required != undefined){
							var requiredValue = required.toString();
							oTarget.mandatory = (requiredValue == 'true')?true:false;
						}else{
							oTarget.mandatory = false;
						}
					}
				}
			};
			oTrans.propertyMapping["endEvent"]["extensionElements"] = function(oTarget, oSource, sSourceType){
				var extensionElements = oSource.extensionElements;
				if(extensionElements){
					oTarget.message = extensionElements.message;
				}else{
					oTarget.message = undefined;
				}
			};
			var oPropertyMap = oTrans.propertyMapping["intermediateCatchEvent"];
			oPropertyMap["eventType"] = oTrans.setEventDefinitions;

			oModel = new sap.modeling.bpmn.Model(oResource, {
				name: "BPMN_1",
				displayName: "BPMN Model 1"
			});

			// Creates a diagram
			oDiagram = new sap.modeling.bpmn.ui.Diagram(oResource);
			oModel.diagrams.push(oDiagram);

			oEditor.diagram = oDiagram;
			oEditor.model = oModel;
			
			oTrans.transformDiagram(e.target.result,oModel,oDiagram);
			oEditor.changeDiagram(oDiagram, true);
			
			var typeMap = oTrans.context.additionalInfo.typeMap;
			var sourceObjs = oTrans.context.sourceObjects;
			var targetObjs = oTrans.context.targetObjects;
			var srcVsTargetMap = oTrans.context._mapping;
			$.each( typeMap, function(key,value){
			   
				if(value == "process"){
					var processKey = key;
					var process = sourceObjs[processKey];
					var processName = process._name;
					oDiagram.name = processName;
					propertiesEditor.setProcessName(processName);							                            
				}
			});
		}
	}
});
				
createDiagramEditor("#editor");


function createObject(sClassName, oParam) {
	var oClass = sap.galilei.model.getClass(sClassName);
	if (oClass) {
		return oClass.create(oResource, oParam);
	}
	return undefined;
}

function createDiagramEditor(containerSelector) {

	// Creates a model
	oModel = new sap.modeling.bpmn.Model(oResource, {
		name: "BPMN_1",
		displayName: "BPMN Model 1"
	});

	// Creates a diagram
	oDiagram = new sap.modeling.bpmn.ui.Diagram(oResource);
	oModel.diagrams.push(oDiagram);

	// Creates a default diagram editor with some parameters
	oEditor = new sap.galilei.ui.editor.DiagramEditor(oDiagram, containerSelector, {
		// Define diagram editor extension
		extension: {
			extensionClass: sap.modeling.bpmn.ui.DiagramEditorExtension
		},
		// Define viewer parameters
		viewer: {
			viewBorderWidth: 1,
			showGrid: false,

			showPageLimit: true,
			showZoomTools: true,
			zoomToolVerticalAlignment: sap.galilei.ui.common.HorizontalAlignment.top,
			zoomToolHorizontalAlignment: sap.galilei.ui.common.HorizontalAlignment.left
		},
		
		isReadOnly: isPropertyDisabled
		
	});
	propertiesEditor.currentObjContainer["editor"] = oEditor;
}


// For Pointer mode, select the predefined PointerTool
$("#pointer").on("click", function () {
	oEditor.selectTool(sap.galilei.ui.editor.tool.PointerTool.NAME);
});

// For Lasso mode, select the predefined LassoTool
$("#lasso").on("click", function () {
	oEditor.selectTool(sap.galilei.ui.editor.tool.LassoTool.NAME);
});

// Undo, calls the undo() function of the editor
$("#undo").on("click", function () {
	oEditor.undo();
});

// Redo, calls the redo() function of the editor
$("#redo").on("click", function () {
	oEditor.redo();
});

// Delete, calls the deleteSelectedSymbols() function of the editor
$("#delete").on("click", function () {
	oEditor.deleteSelectedSymbols();
});

// Selects the create StartEventSymbol tool
$("#startEventTool").on("mousedown", function () {
	//console.log("Start event tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.StartEventSymbol");
});

// Selects the create TaskSymbol tool
$("#taskTool").on("mousedown", function () {
	//console.log("Task tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.TaskSymbol");
});

// Selects the create GatewaySymbol tool
$("#gatewayTool").on("mousedown", function () {
	//console.log("Gateway tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.GatewaySymbol");
});

// Selects the create EndEventSymbol tool
$("#intermediateCatechEventTool").on("mousedown", function () {
	//console.log("Intermediate Catch event tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.IntermediateCatchEventSymbol");
});

// Selects the create EndEventSymbol tool
$("#endEventTool").on("mousedown", function () {
	//console.log("End event tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.EndEventSymbol");
});

// Selects the create SequenceFlowSymbol tool
$("#flowTool").on("mousedown", function () {
	//console.log("Flow tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.LinkSymbols");
});

// Selects the create DataObjectSymbol tool
$("#dataobjectTool").on("mousedown", function () {
	//console.log("Flow tool");
	oEditor.selectTool("sap.modeling.bpmn.ui.DataObjectSymbol");
});

// Zoom in, calls the zoomIn() function of the viewer.
$("#zoomIn").on("mousedown", function () {
	oEditor.viewer.zoomIn();
});

// Zoom out, calls the zoomOut() function of the viewer.
$("#zoomOut").on("mousedown", function () {
	oEditor.viewer.zoomOut();
});

// Global view, calls the showGlobalView() function of the viewer.
$("#globalView").on("mousedown", function () {
	oEditor.viewer.showGlobalView();
});

$("#pdf").on("mousedown", function () {
	debugger;
	var printer = new sap.galilei.ui.common.Print();
	printer.printDiagram(oEditor.viewer); 
});

sap.galilei.core.Event.subscribe("symbol.selected", function (oEvent, customArguments) {
	//console.log("propertiesEditor "+propertiesEditor);
	var selectedSymbol = oEvent.sourceSymbol;
	propertiesEditor.setSelectedObject(selectedSymbol.object);
	//console.log(selectedSymbol.object.name);
	return true;
}, oEditor, oEditor, true);
sap.galilei.core.Event.subscribe("symbol.unselected", function (oEvent, customArguments) {
	//console.log("propertiesEditor "+propertiesEditor);
	
	propertiesEditor.setUnSelectedObject();
	//console.log(selectedSymbol.object.name);
	return true;
}, oEditor, oEditor, true);

// Save, generates the JSON format and shows it in the console log
$("#save").on("mousedown",function() {
	var oWriter = new sap.galilei.model.JSONWriter(), // Creates
														// a
														// JSON
														// writer
	oTrans = new sap.modeling.bpmn.ui.GalileiBpmn2Transformation();
	oResult = oWriter.save(oResource); 


	sap.modeling.bpmn.ui.GalileiBpmn2Transformation.prototype.setTimeDuration = function(
			oTarget, oSource, sSourceType) {
		oTarget["timeDuration"] = oSource.timeDuration;
	};

	sap.modeling.bpmn.ui.GalileiBpmn2Transformation.prototype.setDataObjectExtensionElemetns = function(
			oTarget, oSource, sSourceType) {
		if (oSource.dataType != undefined
				| oSource.isProcessContext != undefined
				|| oSource.mandatory != undefined) {
			var extensionElements = {};
			extensionElements.__prefix = "y";
			extensionElements.processContext = (oSource.isProcessContext == true) ? true
					: undefined;
			if (extensionElements.processContext != true) {
				extensionElements.dataType = oSource.dataType;
				extensionElements.mandatory = oSource.mandatory;
			}
			oTarget["extensionElements"] = extensionElements;
		}
	};

	sap.modeling.bpmn.ui.GalileiBpmn2Transformation.prototype.setEndEventExtensionElemetns = function(
			oTarget, oSource, sSourceType) {
		if (oSource.message != undefined) {
			var extensionElements = {};
			extensionElements.__prefix = "y";
			extensionElements.message = oSource.message;
			oTarget["extensionElements"] = extensionElements;
		}
	};

	sap.modeling.bpmn.ui.GalileiBpmn2Transformation.prototype.getEventDefinitionMapping = function() {
		mappingObj = {
			typeProperty : "type",
			typedMapping : {
				"timer" : {
					timeDuration : this.setTimeDuration
				}
			}
		};

		return mappingObj;
	};

	var oTrans = new sap.modeling.bpmn.ui.GalileiBpmn2Transformation();
	oTrans.constants["TARGET_NAMESPACE"] = 'http://www.sap.com/bpmn2/hybris';
	oTrans.writer.xmlNamespaces["y"] = "http://www.sap.com/bpmn2/hybris"

	oTrans.typeMapping["hybris.modeling.bpmn.DataObject"] = "bpmn2.dataObject";
	oTrans.typeMapping["hybris.modeling.bpmn.EndEvent"] = "bpmn2.endEvent";

	oTrans.propertyMapping["sap.modeling.bpmn.EventDefinition"] = oTrans
			.getEventDefinitionMapping();

	oTrans.propertyMapping["hybris.modeling.bpmn.DataObject"] = {
		name : "name",
		documentation : this.setDocumentation,
		isCollection : "isCollection",
		"extensionElements" : oTrans.setDataObjectExtensionElemetns
	};

	oTrans.propertyMapping["hybris.modeling.bpmn.EndEvent"] = {
		name : "name",
		documentation : this.setDocumentation,
		eventDefinitions : this.setEventDefinitions,
		ioSpecification : this.setDataInputs,
		properties : this.setProperties,
		message : oTrans.setEndEventExtensionElemetns
	};
	
	oTrans.delayedAddTargetReference = function (oSource, sMappingId, sReference, vObject, sObjectMappingId) {
		sap.modeling.bpmn.ui.GalileiBpmn2Transformation.prototype.delayedAddTargetReference.call(this,oSource, sMappingId, sReference, vObject, sObjectMappingId);
		var oTarget = this.context.getMappedObject(this.getSourceId(oSource), sMappingId);
		var incomings = oTarget.incoming;
		var outgoings = oTarget.outgoing;
		
		var incomingCount = $.isArray(incomings)?incomings.length:1;
		var outgoingCount = $.isArray(outgoings)?outgoings.length:1;
		console.log(oSource.classDefinition.qualifiedName);
		console.log(oSource.type);
		if(oSource.classDefinition.qualifiedName == "sap.modeling.bpmn.Gateway" && oSource.type == "exclusive"){
			if(incomingCount > 1 && outgoingCount == 1){
				this.setTargetProperty(oTarget, "gatewayDirection", 'Converging');
			}else if(incomingCount == 1 && outgoingCount > 1){
				this.setTargetProperty(oTarget, "gatewayDirection", 'Diverging');
			}
		}
	};
	
	var bpmnXml = oTrans.transformDiagram(
		oEditor.model, oEditor.diagram);
		//cng.sendSocketEvent('editorContentOutput',bpmnXml);
		console.log("BPMN Xml of the model and diagram:");
		console.log(bpmnXml);
		callController(bpmnXml);
	});


// Load a JSON format
$("#load").on("mousedown", function () {
	$("#upload").trigger('click');
});

function callController(bpmnXml){
       var token = $("input[name='_csrf']").val();
       var header = "X-CSRF-TOKEN";
       $(document).ajaxSend(function(e, xhr, options) {
           xhr.setRequestHeader(header, token);
       });
       
       $.ajax({
           type : 'POST',
           contentType: "text/xml; charset=\"utf-8\"",
       	url : "/bpmn/editor/save", 
           data : bpmnXml,
		dataType : 'xml',
           success: function (response) {
           hac.global.notify($("#result").data("success"));
           },
		error: function(jqXHR, error, errorThrown) {
			hac.global.error($("#result").data("error"));
		}
       });
   };
});            