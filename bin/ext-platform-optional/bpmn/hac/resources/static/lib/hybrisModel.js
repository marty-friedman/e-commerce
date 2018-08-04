(function (){

    "use strict";

    /**
     * BPMN extension meta-model definition
     * */
    var hybModelDef = {
            contents:
            {
				"hybris.modeling.bpmn.Model":{
					classDefinition: "sap.galilei.model.Package",
                    displayName: "Hybris Modeling Bpmn Model",
                    namespaceName: "hybris.modeling.bpmn",
                    classifiers: {
						"DataObject": {
                            displayName: "Data Object",
                            parent: "sap.modeling.bpmn.Data",
                            properties: {
                                "DataObject.isCollection": { name: "isCollection", dataType: sap.galilei.model.dataTypes.gBool },
								"DataObject.isProcessContext": { name: "isProcessContext", dataType: sap.galilei.model.dataTypes.gBool, defaultValue: true },
								"DataObject.dataType": { name: "dataType", dataType: sap.galilei.model.dataTypes.gString },
								"DataObject.mandatory": { name: "mandatory", dataType: sap.galilei.model.dataTypes.gBool }
                            }
                        },
						"EndEvent": {
                            displayName: "End Event",
                            parent: "sap.modeling.bpmn.Event",
                            properties: {
                                "EndEvent.isThrowing": {
                                    name: "isThrowing",
                                    get: function() {
                                        return true;
                                    }
                                },
								"EndEvent.message":{ name: "messageu", dataType: sap.galilei.model.dataTypes.gString }
                            }
                        }
					}
				}
			}
        },
        oResource = new sap.galilei.model.Resource(),
        oReader = new sap.galilei.model.JSONReader();

    oReader.load(oResource, hybModelDef);

}());

