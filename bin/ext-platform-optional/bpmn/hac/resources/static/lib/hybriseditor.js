(function () {

    "use strict";

    /**
     * @class
     * The diagram editor extension for BPMN
     * @name DiagramEditorExtension
     */
    sap.modeling.bpmn.ui.DiagramEditorExtension = sap.galilei.ui.editor.defineDiagramEditorExtension({

        // Define class name
        fullClassName: "sap.modeling.bpmn.ui.DiagramEditorExtension",

        // Define properties
        properties: {
            // Node symbol names
            START_EVENT_SYMBOL: "sap.modeling.bpmn.ui.StartEventSymbol",
            TASK_SYMBOL: "sap.modeling.bpmn.ui.TaskSymbol",
            GATEWAY_SYMBOL: "sap.modeling.bpmn.ui.GatewaySymbol",
            END_EVENT_SYMBOL: "sap.modeling.bpmn.ui.EndEventSymbol",
            INTERMEDIATE_CATCH_EVENT_SYMBOL: "sap.modeling.bpmn.ui.IntermediateCatchEventSymbol",
            DATA_OBJECT_SYMBOL: "sap.modeling.bpmn.ui.DataObjectSymbol",
            ANNOTATION_SYMBOL: "sap.modeling.bpmn.ui.AnnotationSymbol",
            ASSOCIATION_SYMBOL: "sap.modeling.bpmn.ui.AssociationSymbol",

            // Link symbol names
            SEQUENCE_FLOW_SYMBOL: "sap.modeling.bpmn.ui.SequenceFlowSymbol",
            DATA_ASSOCIATION_SYMBOL: "sap.modeling.bpmn.ui.DataAssociationSymbol",

            // Object names
            EVENT_DEFINITION_OBJECT: "sap.modeling.bpmn.EventDefinition",
            TASK_OBJECT: "sap.modeling.bpmn.Task",
            START_EVENT_OBJECT: "sap.modeling.bpmn.StartEvent",
            END_EVENT_OBJECT: "sap.modeling.bpmn.EndEvent",
			HYBRIS_END_EVENT_OBJECT: "hybris.modeling.bpmn.EndEvent",
            INTERMEDIATE_CATCH_EVENT_OBJECT: "sap.modeling.bpmn.IntermediateCatchEvent",
            GATEWAY_OBJECT: "sap.modeling.bpmn.Gateway",
            DATA_OBJECT_OBJECT: "sap.modeling.bpmn.DataObject",
            SEQUENCE_FLOW_OBJECT: "sap.modeling.bpmn.SequenceFlow",
            DATA_ASSOCIATION_OBJECT: "sap.modeling.bpmn.DataAssociation",

            // Property names
            EVENT_TYPE_PROPERTY: "eventType",
            TYPE_PROPERTY: "type",
            
            // Reference names
            LINK_OBJECT_SOURCE: "source",
            LINK_OBJECT_TARGET: "target",
            START_EVENTS_REFERENCE: "events",
            TASKS_REFERENCE: "activities",
            GATEWAYS_REFERENCE: "gateways",
            END_EVENTS_REFERENCE: "events",
            INTERMEDIATE_CATCH_EVENTS_REFERENCE: "events",
            DATA_OBJECTS_REFERENCE: "data",
            ANNOTATIONS_REFERENCE: "annotations",
            SEQUENCE_FLOWS_REFERENCE: "sequenceFlows",
            DATA_ASSOCIATIONS_REFERENCE: "dataAssociations",
            ASSOCIATIONS_REFERENCE: "associations",

            // Link symbol tool name
            LINK_SYMBOL_TOOL: "sap.modeling.bpmn.ui.LinkSymbols",
            POOL_LANE_SYMBOL_TOOL: "sap.modeling.bpmn.ui.PoolOrLaneSymbol",
            CATCH_BOUNDARY_SYMBOL_TOOL: "sap.modeling.bpmn.ui.CatchOrBoundarySymbol",

            // Model package name
            modelPackageName: "sap.modeling.bpmn",

            // Diagram package name
            diagramPackageName: "sap.modeling.bpmn.ui",

            // Change start event type command
            CHANGE_START_EVENT_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeStartEventType",

            // Change end event type command
            CHANGE_END_EVENT_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeEndEventType",

            // Change task type command
            CHANGE_TASK_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeTaskType",

            // Change gateway type command
            CHANGE_GATEWAY_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeGatewayType",

            // Change intermediate catch event type command
            CHANGE_INTERMEDIATE_CATCH_EVENT_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeIntermediateCatchEventType",

            // Change data object type command
            CHANGE_DATA_OBJECT_TYPE_COMMAND: "Sap.Modeling.Bpmn.ChangeDataObjectType",

            // Intermediate catch event type message
            INTERMEDIATE_CATCH_EVENT_TYPE_MESSAGE: "message",

            // Valid candidate start event types
            START_EVENT_TYPE_CANDIDATES: ["", undefined],

            // Valid candidate end event types
            END_EVENT_TYPE_CANDIDATES: ["", undefined],

            // Valid candidate intermediate catch event types
            INTERMEDIATE_CATCH_EVENT_TYPE_CANDIDATES: ["", undefined],

            ACTIVITY_OBJECTS: undefined,

            INTERMEDIATE_EVENT_OBJECTS: undefined,

            EVENT_OBJECTS: undefined,

            CATCH_EVENT_OBJECTS: undefined,

            DATA_OBJECTS: undefined,

            // The start event default type
            START_EVENT_TYPE_DEFAULT: undefined,

            // The end event default type
            END_EVENT_TYPE_DEFAULT: undefined,

            
            TASK_OBJECT_PARAM: undefined,
            START_EVENT_OBJECT_PARAM: undefined,
            END_EVENT_OBJECT_PARAM: undefined,
            INTERMEDIATE_CATCH_EVENT_OBJECT_PARAM: undefined,
            DATA_OBJECT_OBJECT_PARAM: undefined,
            GATEWAY_OBJECT_PARAM: undefined,
            ANNOTATION_OBJECT_PARAM: undefined,
            ANNOTATION_SYMBOL_PARAM: undefined,
            SEQUENCE_FLOW_OBJECT_PARAM: undefined,
            DATA_ASSOCIATION_OBJECT_PARAM: undefined,
            
            // Gets the list of object property names to exclude when copying object properties from a source object to a target object.
            EXCLUDE_COPY_PROPERTIES: ["type", "objectId", "isProxy", "isProxyAutoResolve", "isVolatile", "isVolatile", "onDelete", "proxiedObjectId", "proxiedObjectResourceId"],

            // Images folder
            imagesFolder: "/static/cng/images",

            // The excluded classes and types
            excludedClasses: undefined
        },

        // Define methods
        methods: {
            /**
             * Performs initialization of the extension.
             * @function
             * @name onInitialize
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             */
            onInitialize: function () {
                // Add a drop shadow filter
                function addGlowFilter(oViewer, idFilter, sColor, nSize) {
                    if (oViewer && idFilter) {
                        var glowFilter = new sap.galilei.ui.common.style.Glow({
                            id: idFilter,
                            color: sColor,
                            size: nSize
                        });
                        glowFilter.create(oViewer);
                    }
                }

                // TODO: find a better way
                // Adds a glow filter for highlight
                addGlowFilter(this.viewer, "filterSelectionGlow", "#7CAAC6", 3);

                this.ACTIVITY_OBJECTS = [
                    this.TASK_OBJECT
                ];

                this.INTERMEDIATE_EVENT_OBJECTS = [
                    this.INTERMEDIATE_CATCH_EVENT_OBJECT
                ];

                this.EVENT_OBJECTS = [
                    this.START_EVENT_OBJECT,
                    this.END_EVENT_OBJECT,
					this.HYBRIS_END_EVENT_OBJECT,
                    this.INTERMEDIATE_CATCH_EVENT_OBJECT,
                ];

                this.CATCH_EVENT_OBJECTS = [
                    this.START_EVENT_OBJECT,
                    this.INTERMEDIATE_CATCH_EVENT_OBJECT
                ];

                this.DATA_OBJECTS = [
                    this.DATA_OBJECT_OBJECT
                ];

                this.START_EVENT_OBJECT_PARAM = { eventType: undefined };
                this.END_EVENT_OBJECT_PARAM = { eventType: undefined };
                this.INTERMEDIATE_CATCH_EVENT_OBJECT_PARAM = { eventType: "message" };
                this.GATEWAY_OBJECT_PARAM = { type: "exclusive" };
                this.ANNOTATION_OBJECT_PARAM = { text: "Note" };

                // Add gradients for the CSS with gradient
                var oGradient;
                // Start event gradient
                oGradient = new sap.galilei.ui.common.style.LinearGradient({
                    id: "startEventGradient",
                    stops: [
                        {offset: "0%", color: "#FFFFFF"},
                        {offset: "65%", color: "#ECFDDC"},
                        {offset: "100%", color: "#DCFEA4"}
                    ]
                });
                oGradient.createGradient(this.viewer);

                // End event gradient
                oGradient.set({
                    id: "endEventGradient",
                    stops: [
                        {offset: "0%", color: "#FFFFFF"},
                        {offset: "65%", color: "#FFE6E5"},
                        {offset: "100%", color: "#FFD5D5"}
                    ]
                });
                oGradient.createGradient(this.viewer);

                // Intermediate event gradient
                oGradient.set({
                    id: "intermediateEventGradient",
                    stops: [
                        {offset: "0%", color: "#FFFFFF"},
                        {offset: "65%", color: "#E7E7ED"},
                        {offset: "100%", color: "#D4D4E8"}
                    ]
                });
                oGradient.createGradient(this.viewer);

                // Task gradient
                oGradient.set({
                    id: "taskGradient",
                    stops: [
                        {offset: "0%", color: "#F3FCFF"},
                        {offset: "65%", color: "#D5EDFF"},
                        {offset: "100%", color: "#C1E4FF"}
                    ]
                });
                oGradient.createGradient(this.viewer);
            },

            

            /**
             * Checks whether a symbol can be created at a specific point under a parent symbol.
             * @function
             * @name canCreateSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {String} sSymbolClass The symbol class qualified name.
             * @param {Object} oCreateParam The input and output parameters. The parameters contain:
             * {
             *     point: (in/out) The view point where the symbol should be created.
             *     parentSymbol: (in/out) The parent symbol.
             * }
             * @returns {Boolean} true if the symbol can be created.
             */
            canCreateSymbol: function (sSymbolClass, oCreateParam) {
                var aPoint,
                    oParentSymbol,
                    sParentSymbolClass;

                if (!this.canCreateInstance(sSymbolClass)) {
                    return false;
                }

                if (!this.editor.defaultCanCreateSymbol(sSymbolClass, oCreateParam)) {
                    return false;
                }

                if (sSymbolClass && oCreateParam) {
                    aPoint = oCreateParam.point;
                    oParentSymbol = oCreateParam.parentSymbol;
                    oCreateParam.parentSymbol = undefined;

                    switch (sSymbolClass) {
                        case this.START_EVENT_SYMBOL:
                        case this.TASK_SYMBOL:
                        case this.GATEWAY_SYMBOL:
                        case this.END_EVENT_SYMBOL:
                        case this.DATA_OBJECT_SYMBOL:
                        case this.INTERMEDIATE_CATCH_EVENT_SYMBOL:
                            // Start, end, task, gateway can be created under lane, composite task or undefined
                            if (oParentSymbol) {
                                sParentSymbolClass = oParentSymbol.classDefinition.qualifiedName;
                                if (sSymbolClass === this.INTERMEDIATE_CATCH_EVENT_SYMBOL) {
                                    // They cannot be created on the parent border
                                    if (oParentSymbol.isPointOnBorder(aPoint, 8)) {
                                        return false;
                                    }
                                }
                                oCreateParam.parentSymbol = oParentSymbol;
                            }
                            break;
                        case this.ANNOTATION_SYMBOL:
                            // Annotation symbol can be created under subprocess, lane
                            if (oParentSymbol && !oParentSymbol.isDiagram) {
                                sParentSymbolClass = oParentSymbol.classDefinition.qualifiedName;
                                oCreateParam.parentSymbol = oParentSymbol;
                            }
                            break;
                    }
                }
                return true;
            },

            /**
             * Checks whether a node symbol can be added to the diagram or a parent symbol
             * @function
             * @name canAttachSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Object} oParent The parent diagram or symbol.
             * @param {Object} oSymbol The target symbol.
             * @param {Object} oAttachParam The attach symbol input and output parameters. The parameters contain:
             * {
             *     point: (in/out) The view point where the symbol should be attached.
             * }
             * @returns {Boolean} True if can attach symbol.
             */
            canAttachSymbol: function(oParent, oSymbol, oAttachParam) {
                var bCanAttach = this.editor.defaultCanAttachSymbol(oParent, oSymbol, oAttachParam),
                    oObject,
                    sObjectClass,
                    oObjectParent,
                    oProperties;

                if (bCanAttach) {
                    oObject = oSymbol.object;
                    if (oObject) {
                        sObjectClass = oObject.classDefinition.qualifiedName;
                        switch (sObjectClass) {
                            case this.START_EVENT_OBJECT:
                            case this.END_EVENT_OBJECT:
                                oProperties = {};
                                oProperties[this.EVENT_TYPE_PROPERTY] = oObject[this.EVENT_TYPE_PROPERTY];
                                break;
                        }
                        if (oProperties) {
                            oObjectParent = this.editor.getParentObjectByParentSymbol(oParent, sObjectClass);
                            bCanAttach = this.editor.canCreateObject(sObjectClass, oObjectParent, oProperties);
                        }
                    }
                }
                return bCanAttach;
            },

            /**
             * Check whether an instance of certain class can be created.
             * If the oParams is specified, they will be used to verify the instance creation.
             * @function
             * @name canCreateInstance
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {String} sClass The class qualified name.
             * @param {Object} oParams The key-value pair of the properties to be set on the new instance. (optional)
             * @returns {Boolean} Returns true if the class and properties are all valid.
             */
            canCreateInstance: function (sClass, oParams) {
                var oClass,
                    sParam;

                if (sClass && this.excludedClasses) {
                    oClass = this.excludedClasses[sClass];
                    if (oClass === true) {
                        return false;
                    }

                    if (oClass && oParams) {
                        for (sParam in oParams) {
                            if (oClass.hasOwnProperty(sParam)) {
                                if (oClass[sParam].indexOf(oParams[sParam]) >= 0) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            },

            /**
             * Check whether a new object with class and properties can be created under certain parent.
             * @function
             * @name canCreateObject
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {String} sObjectClass The object class qualified name.
             * @param {sap.galilei.model.Object} oParent The container object.
             * @param {Object} oProperties The initial properties (key value pairs) to be set to the new object.
             * @returns {Boolean} true if the object can be created and all properties can be set.
             */
            canCreateObject: function (sObjectClass, oParent, oProperties) {
                var sParentClass,
                    sSourceReference,
                    sTargetReference,
                    oSource,
                    oTarget,
                    sSourceClass,
                    sTargetClass;

                // Cannot create under a proxy
                if (oParent && oParent.isProxy) {
                    return false;
                }

                // Verify by editor profile
                if (!this.canCreateInstance(sObjectClass, oProperties)) {
                    return false;
                }

                switch (sObjectClass) {
                    case this.START_EVENT_OBJECT:
                        // Verify the eventType property
                        if (oProperties && oProperties.hasOwnProperty(this.EVENT_TYPE_PROPERTY)) {

                            if (oParent) {
                                sParentClass = oParent.classDefinition.qualifiedName;
                            }
                        }
                        break;

                    case this.END_EVENT_OBJECT:
                    //case this.MESSAGE_END_EVENT_OBJECT:
                        // Verify the eventType property
                        if (oProperties && oProperties.hasOwnProperty(this.EVENT_TYPE_PROPERTY)) {

                            if (oParent) {
                                sParentClass = oParent.classDefinition.qualifiedName;
                            }
                        }
                        break;

                    case this.SEQUENCE_FLOW_OBJECT:

                        if (oProperties) {

                            sSourceReference = this.editor.getLinkObjectSourceReferenceName(sObjectClass);
                            sTargetReference = this.editor.getLinkObjectTargetReferenceName(sObjectClass);

                            oSource = oProperties[sSourceReference];
                            oTarget = oProperties[sTargetReference];

                            sSourceClass = oSource ? oSource.classDefinition.qualifiedName : undefined;
                            sTargetClass = oTarget ? oTarget.classDefinition.qualifiedName : undefined;

                            if (oSource) {
                                if (this.ACTIVITY_OBJECTS.indexOf(sSourceClass) < 0 && this.EVENT_OBJECTS.indexOf(sSourceClass) < 0 && sSourceClass !== this.GATEWAY_OBJECT) {
                                    return false;
                                }
                                if (sSourceClass === this.END_EVENT_OBJECT || sSourceClass === this.HYBRIS_END_EVENT_OBJECT) {
                                    return false;
                                }
                            }

                            if (oTarget) {
                                if (this.ACTIVITY_OBJECTS.indexOf(sTargetClass) < 0 && this.EVENT_OBJECTS.indexOf(sTargetClass) < 0 && sTargetClass !== this.GATEWAY_OBJECT) {
                                    return false;
                                }
                                if (sTargetClass === this.START_EVENT_OBJECT) {
                                    return false;
                                }
                            }

                            // Self link can only be activity or intermediate events
                            if (oSource && oSource === oTarget) {
                                if (this.ACTIVITY_OBJECTS.indexOf(sSourceClass) < 0 && this.INTERMEDIATE_EVENT_OBJECTS.indexOf(sSourceClass) < 0) {
                                    return false;
                                }
                            }
                        }

                        break;

                    case this.DATA_ASSOCIATION_OBJECT:

                        if (oProperties) {

                            sSourceReference = this.editor.getLinkObjectSourceReferenceName(sObjectClass);
                            sTargetReference = this.editor.getLinkObjectTargetReferenceName(sObjectClass);

                            oSource = oProperties[sSourceReference];
                            oTarget = oProperties[sTargetReference];

                            sSourceClass = oSource ? oSource.classDefinition.qualifiedName : undefined;
                            sTargetClass = oTarget ? oTarget.classDefinition.qualifiedName : undefined;

                            if (oSource) {
                                if (this.ACTIVITY_OBJECTS.indexOf(sSourceClass) < 0 && this.EVENT_OBJECTS.indexOf(sSourceClass) < 0 && this.DATA_OBJECTS.indexOf(sSourceClass) < 0) {
                                    return false;
                                }
                            }

                            if (oTarget) {
                                if (this.ACTIVITY_OBJECTS.indexOf(sTargetClass) < 0 && this.EVENT_OBJECTS.indexOf(sTargetClass) < 0 && this.DATA_OBJECTS.indexOf(sTargetClass) < 0) {
                                    return false;
                                }
                            }

                            if (oSource && oTarget) {

                                // Cannot be both activity or event
                                if ((this.ACTIVITY_OBJECTS.indexOf(sSourceClass) >= 0 || this.EVENT_OBJECTS.indexOf(sSourceClass) >= 0) &&
                                    (this.ACTIVITY_OBJECTS.indexOf(sTargetClass) >= 0 || this.EVENT_OBJECTS.indexOf(sTargetClass) >= 0)) {
                                    return false;
                                }

                                // Cannot be both data
                                if (this.DATA_OBJECTS.indexOf(sSourceClass) >= 0 && this.DATA_OBJECTS.indexOf(sTargetClass) >= 0) {
                                    return false;
                                }
                            }
                        }

                        break;
                }

                return true;
            },

            /**
             * Check whether properties can be set to certain object.
             * @function
             * @name canSetObjectProperties
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {sap.galilei.model.Object} oObject The target object.
             * @param {Object} oProperties The properties (key value pairs) to be set to the target object.
             * @returns {Boolean} true if all properties can be set.
             */
            canSetObjectProperties: function (oObject, oProperties) {
                if (oObject && oProperties) {
                    var sObjectClass = oObject.classDefinition.qualifiedName,
                        oParent = oObject.container;
                    if (!this.editor.canCreateObject(sObjectClass, oParent, oProperties)) {
                        return false;
                    }
                }
                return true;
            },

            /**
             * Gets the parent object of a given parent symbol.
             * @function
             * @name getParentObjectByParentSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param oParentSymbol {sap.galilei.ui.diagram.Symbol} The parent symbol.
             * @param sObjectClass {String} The object kind to be put into the new parent.
             * @returns {sap.galilei.model.Object} The candidate parent object.
             */
            getParentObjectByParentSymbol: function (oParentSymbol, sObjectClass) {
                if (oParentSymbol && oParentSymbol.isSwimlaneSymbol) {
                    return undefined;
                }
                return this.editor.defaultGetParentObjectByParentSymbol(oParentSymbol, sObjectClass);
            },

            /**
             * Checks whether a link symbol can be created between the source symbol and the target symbol using the link symbol tool definition.
             * @function
             * @name canCreateLinkSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Symbol} oSourceSymbol The source symbol.
             * @param {Symbol} oTargetSymbol The target symbol.
             * @param {Object} oLinkTool The link symbol tool definition.
             * @returns {boolean} true if the link symbol can be created.
             */
            canCreateLinkSymbol: function (oSourceSymbol, oTargetSymbol, oLinkTool) {
                var sQualifiedSourceName,
                    sQualifiedTargetName,
                    oSourceParentSymbol,
                    oTargetParentSymbol,
                    oSourcePoolSymbol,
                    oTargetPoolSymbol,
                    oDataSymbol,
                    oActivitySymbol,
                    oDataParentSymbol,
                    oActivityParentSymbol,
                    sSourceReference,
                    sTargetReference,
                    bOutter = false,
                    oObjectProperties = {},
                    sLinkObjectClass;

                if (!this.defaultCanCreateLinkSymbol(oSourceSymbol, oTargetSymbol, oLinkTool)) {
                    return false;
                }

                if (oSourceSymbol && oTargetSymbol && oLinkTool) {
                    sQualifiedSourceName = oSourceSymbol.classDefinition.qualifiedName;
                    sQualifiedTargetName = oTargetSymbol.classDefinition.qualifiedName;
                    
                    oSourceParentSymbol = oSourceSymbol.parentSymbol;
                    oTargetParentSymbol = oTargetSymbol.parentSymbol;

                    switch (oLinkTool.linkSymbolClass) {

                        case this.SEQUENCE_FLOW_SYMBOL:

                            sLinkObjectClass = this.SEQUENCE_FLOW_OBJECT;

                            // TODO
                            if (oSourceSymbol.isSwimlaneSymbol || oTargetSymbol.isSwimlaneSymbol) {
                                return false;
                            }

                            // Different parent symbol are not allowed including undefined
                            if (oSourceParentSymbol !== oTargetParentSymbol) {
                                // The only case allowed is different lane symbol within the same pool symbol
                                if (!oSourceParentSymbol || !oTargetParentSymbol || !oSourceParentSymbol.isSwimlaneSymbol || !oTargetParentSymbol.isSwimlaneSymbol) {
                                    return false;
                                }
                                oSourcePoolSymbol = sap.galilei.ui.diagram.SwimlaneSymbol.getRootSwimlaneSymbol(oSourceParentSymbol);
                                oTargetPoolSymbol = sap.galilei.ui.diagram.SwimlaneSymbol.getRootSwimlaneSymbol(oTargetParentSymbol);
                                if (oSourcePoolSymbol !== oTargetPoolSymbol) {
                                    return false;
                                }
                            }

                            break;
                    }

                    sSourceReference = this.editor.getLinkObjectSourceReferenceName(oLinkTool.linkObjectClass || sLinkObjectClass);
                    sTargetReference = this.editor.getLinkObjectTargetReferenceName(oLinkTool.linkObjectClass || sLinkObjectClass);

                    if (sSourceReference && sTargetReference) {
                        oObjectProperties[sSourceReference] = oSourceSymbol.object;
                        oObjectProperties[sTargetReference] = oTargetSymbol.object;

                        if (oLinkTool.linkObjectClass || sLinkObjectClass) {
                            return this.editor.canCreateObject(oLinkTool.linkObjectClass || sLinkObjectClass, oLinkTool.linkObjectParent, oObjectProperties);
                        } else {
                            return true; // Free symbol
                        }
                    }
                }
                return true;
            },

            /**
             * Checks whether a symbol can be insert on a link symbol at the position aPoint.
             * @function
             * @name canInsertSymbolOnLinkSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {String} sSymbolClass The node symbol class qualified name.
             * @param {Object} oCreateParam The input and output parameters. The parameters contain:
             * {
             *     point: (in/out) The view point where the symbol should be created.
             *     linkSymbol: (in/out) The link symbol.
             *     defaultPosition: (in) If true, the create position is not specified.
             * }
             * @returns {Boolean} true if the symbol can be inserted.
             */
            canInsertSymbolOnLinkSymbol: function(sSymbolClass, oCreateParam) {
                var sLinkSymbolClass,
                    sSourceSymbolClass,
                    aDataSymbols;

                oCreateParam = oCreateParam || {};
                if (oCreateParam.linkSymbol && oCreateParam.linkSymbol.object) {
                    sLinkSymbolClass = oCreateParam.linkSymbol.classDefinition.qualifiedName;
                    if (!this.canCreateInstance(sSymbolClass) || !this.canCreateInstance(sLinkSymbolClass)) {
                        return false;
                    }

                    aDataSymbols = [
                        this.DATA_OBJECT_SYMBOL
                    ];
                    switch (sLinkSymbolClass) {
                        case this.SEQUENCE_FLOW_SYMBOL:
                            if (sSymbolClass === this.ANNOTATION_SYMBOL || sSymbolClass === this.START_EVENT_SYMBOL || sSymbolClass === this.END_EVENT_SYMBOL || aDataSymbols.indexOf(sSymbolClass) >= 0) {
                                return false;
                            }
                            break;
                        
                        case this.DATA_ASSOCIATION_SYMBOL:
                            return false;
                    }

                    // Do not insert object on link if the source of the link is a gateway
                    sSourceSymbolClass = oCreateParam.linkSymbol.sourceSymbol && oCreateParam.linkSymbol.sourceSymbol.classDefinition.qualifiedName;
                    if (oCreateParam.defaultPosition && sSourceSymbolClass === this.GATEWAY_SYMBOL) {
                        return false;
                    }
                }
                return true;
            },

            /**
             * Post actions to be done after move node symbols.
             * @function
             * @name postMoveSymbols
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Collection|Array} vSymbols The symbols being moved.
             * @param {Object} oSymbolContainers The containers of the symbols before move, fetched by symbol.objectId.
             */
            postMoveSymbols: function(vSymbols, oSymbolContainers) {
                if (vSymbols && oSymbolContainers) {
                    var oSymbol,
                        i,
                        j,
                        bArray = (vSymbols instanceof Array),
                        aLinkSymbols,
                        oLinkSymbol,
                        sLinkSymbolClass,
                        sLinkObjectClass,
                        sOppositeSymbolClass,
                        sOppositeObjectClass,
                        oSourceSymbol,
                        oTargetSymbol,
                        oObjectParams = {},
                        points,
                        oTool = sap.galilei.ui.editor.tool.getTool(this.LINK_SYMBOL_TOOL),
                        aLinkTools,
                        index,
                        oLinkTool;

                    for (i = 0; i < vSymbols.length; i++) {
                        oSymbol = bArray ? vSymbols[i] : vSymbols.get(i);
                        if (oSymbol && !oSymbol.isLinkSymbol) {
                            aLinkSymbols = oSymbol.getLinkSymbols();
                            if (aLinkSymbols) {
                                for (j = 0; j < aLinkSymbols.length; j++) {
                                    oLinkSymbol = aLinkSymbols[j];
                                    oSourceSymbol = oLinkSymbol.sourceSymbol;
                                    oTargetSymbol = oLinkSymbol.targetSymbol;
                                    sLinkSymbolClass = oLinkSymbol.classDefinition.qualifiedName;
                                    sLinkObjectClass = oLinkSymbol.classDefinition.qualifiedName === this.SEQUENCE_FLOW_SYMBOL ? this.SEQUENCE_FLOW_OBJECT : /*this.MESSAGE_FLOW_OBJECT*/ null;
                                    sOppositeSymbolClass = sLinkSymbolClass === this.SEQUENCE_FLOW_SYMBOL ? /*this.MESSAGE_FLOW_SYMBOL*/null : this.SEQUENCE_FLOW_SYMBOL;
                                    sOppositeObjectClass = sOppositeSymbolClass === this.SEQUENCE_FLOW_SYMBOL ? this.SEQUENCE_FLOW_OBJECT : /*this.MESSAGE_FLOW_OBJECT*/null;
                                    points = oLinkSymbol.points.clone();

                                    if (sLinkSymbolClass === this.SEQUENCE_FLOW_SYMBOL /*|| sLinkSymbolClass === this.MESSAGE_FLOW_SYMBOL*/) {
                                        if (!this.canCreateLinkSymbol(oSourceSymbol, oTargetSymbol, { linkSymbolClass: sLinkSymbolClass, linkObjectClass: sLinkObjectClass })) {
                                            aLinkTools = oTool.getLinkSymbolDefinitions(oSourceSymbol, oTargetSymbol);
                                            if (aLinkTools) {
                                                for (index = 0; index < aLinkTools.length; index++) {
                                                    oLinkTool = aLinkTools[index];
                                                    if (oLinkTool.linkSymbolClass === sOppositeSymbolClass) {
                                                        if (this.canCreateLinkSymbol(oSourceSymbol, oTargetSymbol, { linkSymbolClass: sOppositeSymbolClass, linkObjectClass: sOppositeObjectClass })) {
                                                            oObjectParams[this.editor.getLinkObjectSourceReferenceName(sOppositeObjectClass)] = oSourceSymbol.object;
                                                            oObjectParams[this.editor.getLinkObjectTargetReferenceName(sOppositeObjectClass)] = oTargetSymbol.object;
                                                            if (oLinkTool.linkObjectParam) {
                                                                sap.galilei.core.copyProperties(oObjectParams, oLinkTool.linkObjectParam);
                                                            }
                                                            this.editor.replaceObjectAndSymbol(oLinkSymbol, sOppositeObjectClass, oObjectParams, sOppositeSymbolClass, { sourceSymbol: oSourceSymbol, targetSymbol: oTargetSymbol, points: points });
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },

            /**
             * Selects a link symbol tool definition between the source symbol and target symbol.
             * @function
             * @name selectLinkSymbolDefinition
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Symbol} oSourceSymbol The source symbol.
             * @param {Symbol} oTargetSymbol The target symbol.
             * @returns {Object} The link symbol definition to use.
             */
            selectLinkSymbolDefinition: function (oSourceSymbol, oTargetSymbol) {
                // TODO
                var oTool = sap.galilei.ui.editor.tool.getTool(this.LINK_SYMBOL_TOOL);
                if (oTool && oTool.selectLinkSymbolDefinition) {
                    return oTool.selectLinkSymbolDefinition(oSourceSymbol, oTargetSymbol);
                }
                return { linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL };
            },

           
            /**
             * This function replaces an object by a new object. Optionally, the symbol can also be replaced.
             * @function
             * @name replaceObjectAndSymbol
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {sap.galilei.ui.diagram.Symbol} oSymbol The symbol.
             * @param {String} sObjectClass The object class qualified name.
             * @param {Object} oObjectParams The object parameters.
             * @param {String} sSymbolClass The symbol class qualified name. If it is not specified, the symbol will not be replaced.
             * @param {Object} oSymbolParams The object parameters.
             * @returns {sap.galilei.ui.diagram.Symbol} The new symbol.
             */
            replaceObjectAndSymbol: function (oSymbol, sObjectClass, oObjectParams, sSymbolClass, oSymbolParams) {
                var oNewSymbol = this.editor.defaultReplaceObjectAndSymbol(oSymbol, sObjectClass, oObjectParams, sSymbolClass, oSymbolParams);

                // Removes the subactivity symbols if changing from subprocess to task or call activity
                this.checkSubSymbols(oNewSymbol);
                return oNewSymbol;
            },

            /**
             * This function checks if the subsymbols are still valid, otherwise remove them. It can be used if the symbol changes type.
             * @function
             * @name checkSubSymbols
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {sap.galilei.ui.diagram.Symbol} oSymbol The symbol.
             */
            checkSubSymbols: function (oSymbol) {
                var sClassName,
                    index,
                    oSubsymbol;

                // Removes the subactivity symbols if changing from subprocess to task or call activity
                if (oSymbol && oSymbol.isComposite) {
                    sClassName = oSymbol.classDefinition.qualifiedName;
                    if (sClassName === this.TASK_SYMBOL /*|| sClassName === this.CALL_ACTIVITY_SYMBOL*/ ||
                        (/*sClassName === this.SUBPROCESS_SYMBOL &&*/ oSymbol.object && oSymbol.object.isProxy)) {
                        for (index = oSymbol.symbols.length - 1; index >= 0; index--) {
                            oSubsymbol = oSymbol.symbols.get(index);
                            if (oSubsymbol && !oSubsymbol.isBoundarySymbol) {
                                this.editor.deleteSymbol(oSubsymbol, true, false);
                            }
                        }
                    }
                }
            },

            /**
             * Changes the start event type.
             * @function
             * @name changeStartEventType
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Symbol} oStartEventSymbol The start event symbol.
             * @param {String} sEventType The start event type.
             */
            changeStartEventType: function (oStartEventSymbol, sEventType) {
                var self = this,
                    oStartEvent;

                // Undo action
                function changeStartEventType() {
                    if (oStartEvent.eventDefinitions.length > 0) {
                        oStartEvent.eventDefinitions.clear();
                    }
                    if (sEventType) {
                        oStartEvent.eventDefinitions.push(sap.galilei.model.getClass(self.EVENT_DEFINITION_OBJECT).create(self.resource, { type: sEventType}));
                    }
                }

                if (oStartEventSymbol && oStartEventSymbol.object) {
                    oStartEvent = oStartEventSymbol.object;

                    if (oStartEvent.eventDefinitions) {
                        if (sEventType !== oStartEvent.eventType) {
                            this.resource.applyUndoableAction(changeStartEventType, "Change Start Event Type");
                        }
                    }
                }
            },

            /**
             * Changes the end event type.
             * @function
             * @name changeEndEventType
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Symbol} oEndEventSymbol The end event symbol.
             * @param {String} sEventType The end event type.
             */
            changeEndEventType: function (oEndEventSymbol, sEventType) {
                var self = this,
                    oEndEvent;

                // Undo action
                function changeEndEventType() {
                    if (oEndEvent.eventDefinitions.length > 0) {
                        oEndEvent.eventDefinitions.clear();
                    }
                    if (sEventType) {
                        oEndEvent.eventDefinitions.push(sap.galilei.model.getClass(self.EVENT_DEFINITION_OBJECT).create(self.resource, { type: sEventType}));
                    }
                }

                if (oEndEventSymbol && oEndEventSymbol.object) {
                    oEndEvent = oEndEventSymbol.object;

                    if (oEndEvent.eventDefinitions && sEventType !== oEndEvent.eventType) {
                        this.resource.applyUndoableAction(changeEndEventType, "Change End Event Type");
                    }
                }
            },

            /**
             * Gets the context button pad definition for symbol.
             * @function
             * @name getContextButtonPad
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Object} oSymbol The symbol.
             */
            getContextButtonPad: function (oSymbol) {
                var aButtons,
                    isProxy = oSymbol && oSymbol.object && oSymbol.object.isProxy;

                if (oSymbol) {
                    switch (oSymbol.classDefinition.qualifiedName) {
                        case this.START_EVENT_SYMBOL:
                            aButtons = [];

                            if (this.canCreateInstance(this.TASK_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.TASK_SYMBOL
                                });
                            }

                            if (this.canCreateInstance(this.GATEWAY_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.GATEWAY_SYMBOL
                                });
                            }

                            if (this.canCreateInstance(this.INTERMEDIATE_CATCH_EVENT_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.INTERMEDIATE_CATCH_EVENT_SYMBOL
                                });
                            }

                            aButtons.push({
                                toolName: this.ANNOTATION_SYMBOL
                            });

                            aButtons.push({
                                toolName: this.LINK_SYMBOL_TOOL
                            });

                            if (!isProxy) {
                                aButtons.push({
                                    commandName: this.CHANGE_START_EVENT_TYPE_COMMAND
                                });
                            }

                            break;
                        case this.TASK_SYMBOL:
                        case this.GATEWAY_SYMBOL:
                        case this.INTERMEDIATE_CATCH_EVENT_SYMBOL:
                            aButtons = [];

                            if (this.canCreateInstance(this.TASK_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.TASK_SYMBOL
                                });
                            }

                            if (this.canCreateInstance(this.GATEWAY_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.GATEWAY_SYMBOL
                                });
                            }

                            if (this.canCreateInstance(this.INTERMEDIATE_CATCH_EVENT_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.INTERMEDIATE_CATCH_EVENT_SYMBOL
                                });
                            }

                            if (this.canCreateInstance(this.END_EVENT_SYMBOL)) {
                                aButtons.push({
                                    toolName: this.END_EVENT_SYMBOL
                                });
                            }

                            aButtons.push({
                                toolName: this.ANNOTATION_SYMBOL
                            });

                            aButtons.push({
                                toolName: this.LINK_SYMBOL_TOOL
                            });

                            if (!isProxy) {
                                switch (oSymbol.classDefinition.qualifiedName) {
                                    case this.TASK_SYMBOL:
                                        aButtons.push({
                                            commandName: this.CHANGE_TASK_TYPE_COMMAND
                                        });
                                        break;
                                    case this.GATEWAY_SYMBOL:
                                        aButtons.push({
                                            commandName: this.CHANGE_GATEWAY_TYPE_COMMAND
                                        });
                                        break;
                                    case this.INTERMEDIATE_CATCH_EVENT_SYMBOL:
                                        aButtons.push({
                                            commandName: this.CHANGE_INTERMEDIATE_CATCH_EVENT_TYPE_COMMAND
                                        });
                                        break;
                                }
                            }
                            break;
                        case this.END_EVENT_SYMBOL:
                        case this.DATA_OBJECT_SYMBOL:
                            aButtons = [];                            
                            break;
                    }
                }

                this.addImagesFolder(aButtons);
                return aButtons;
            },

            /**
             * Gets the tools definition. The definition is an array of tool definition.
             * Create node symbol tool definition the parameters:
             * name: <(optional) The tool name>,
             * type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool
             * symbolClass: <The symbol class qualified name>
             * symbolParam: <(optional) The symbol property values>
             * objectClass: < (optional) The object class qualified name>
             * objectParam:  <(optional) The object property values>
             * objectReference: <(optional) The name of the reference where the object should be added>
             * smallIcon: <The small icon URL, usually 16x16>
             * largeIcon: <(optional) The large icon URL, usually 32x32>
             * cursor: <(optional) The cursor URL, usually 32x32>
             *
             * Create link symbol tool definition the parameters:
             * name: <The tool name>
             * type: sap.galilei.ui.editor.tool.Types.createLinkSymbolTool
             * linksDefinition: <Array of supported link symbols>
             * {
             * sourceSymbol: <The source symbol class qualified name>
             * targetSymbol: <The target symbol class qualified name>
             * linkSymbolClass: <The link symbol class qualified name>
             * linkSymbolParam: <(optional) The link symbol property values>
             * linkObjectClass: < (optional) The link object class qualified name>
             * linkObjectParam:  <(optional) The link object property values>
             * linkObjectReference: <(optional) The name of the reference where the link object should be added>
             * }
             * smallIcon: <The small icon URL, usually 16x16>
             * largeIcon: <(optional) The large icon URL, usually 32x32>
             * cursor: <The cursor URL>
             *
             * Normal tool definition the parameters:
             * name: <The tool name>
             * type: sap.galilei.ui.editor.tool.Types.tool
             * canExecute: function (oParam), where oParam contains editor, diagram, symbol
             * execute: function (oParam)
             * smallIcon: <The small icon URL, usually 16x16>
             * largeIcon: <(optional) The large icon URL, usually 32x32>
             * cursor: <The cursor URL>
             * @function
             * @name getToolsDefinition
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Array} The tools definition.
             */
            getToolsDefinition: function () {
                var self = this,
                    oToolsDef;

                oToolsDef = [
                    // StartEvent
                    {
                        name: this.START_EVENT_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.START_EVENT_SYMBOL,
                        objectReference: this.START_EVENTS_REFERENCE,
                        objectParam: this.START_EVENT_OBJECT_PARAM,
                        smallIcon: "StartEvent16.png"
                    },
                    // Task
                    {
                        name: this.TASK_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.TASK_SYMBOL,
                        objectReference: this.TASKS_REFERENCE,
                        objectParam: this.TASK_OBJECT_PARAM,
                        smallIcon: "Task16.png"
                    },
                    // Gateway
                    {
                        name: this.GATEWAY_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.GATEWAY_SYMBOL,
                        objectReference: this.GATEWAYS_REFERENCE,
                        objectParam: this.GATEWAY_OBJECT_PARAM,
                        smallIcon: "Gateway16.png"
                    },
                    // EndEvent
                    {
                        name: this.END_EVENT_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.END_EVENT_SYMBOL,
                        objectReference: this.END_EVENTS_REFERENCE,
                        objectReference: this.END_EVENTS_REFERENCE,
                        objectParam: this.END_EVENT_OBJECT_PARAM,
                        smallIcon: "EndEvent16.png"
                    },
                    // IntermediateCatchEventSymbol
                    {
                        name: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                        objectReference: this.INTERMEDIATE_CATCH_EVENTS_REFERENCE,
                        objectParam: this.INTERMEDIATE_CATCH_EVENT_OBJECT_PARAM,
                        smallIcon: "IntermediateCatchEvent16.png"
                    },
                    // DataObject
                    {
                        name: this.DATA_OBJECT_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.DATA_OBJECT_SYMBOL,
                        objectReference: this.DATA_OBJECTS_REFERENCE,
                        objectParam: this.DATA_OBJECT_OBJECT_PARAM,
                        smallIcon: "DataObject16.png"
                    },
                    // Annotation
                    {
                        name: this.ANNOTATION_SYMBOL,
                        type: sap.galilei.ui.editor.tool.Types.createNodeSymbolTool,
                        symbolClass: this.ANNOTATION_SYMBOL,
                        symbolParam: this.ANNOTATION_SYMBOL_PARAM,
                        objectReference: this.ANNOTATIONS_REFERENCE,
                        objectParam: this.ANNOTATION_OBJECT_PARAM,
                        smallIcon: "Annotation16.png"
                    },
                    // Link symbols
                    {
                        name: this.LINK_SYMBOL_TOOL,
                        type: sap.galilei.ui.editor.tool.Types.createLinkSymbolTool,
                        smallIcon: "SequenceFlow16.png",
                        cursor: "FlowCursor.png",
                        linksDefinition: [
                            // Source: StartEvent
                            {
                                sourceSymbol: this.START_EVENT_SYMBOL,
                                targetSymbol: this.TASK_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.START_EVENT_SYMBOL,
                                targetSymbol: this.GATEWAY_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.START_EVENT_SYMBOL,
                                targetSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.START_EVENT_SYMBOL,
                                targetSymbol: this.DATA_OBJECT_SYMBOL,
                                linkSymbolClass: this.DATA_ASSOCIATION_SYMBOL,
                                linkObjectReference: this.DATA_ASSOCIATIONS_REFERENCE,
                                linkObjectParam: this.DATA_ASSOCIATION_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.START_EVENT_SYMBOL,
                                targetSymbol: this.ANNOTATION_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            // Source: Task
                            {
                                sourceSymbol: this.TASK_SYMBOL,
                                targetSymbol: this.TASK_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.TASK_SYMBOL,
                                targetSymbol: this.GATEWAY_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.TASK_SYMBOL,
                                targetSymbol: this.END_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.TASK_SYMBOL,
                                targetSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.TASK_SYMBOL,
                                targetSymbol: this.ANNOTATION_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            // Source: Gateway
                            {
                                sourceSymbol: this.GATEWAY_SYMBOL,
                                targetSymbol: this.TASK_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.GATEWAY_SYMBOL,
                                targetSymbol: this.GATEWAY_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.GATEWAY_SYMBOL,
                                targetSymbol: this.END_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.GATEWAY_SYMBOL,
                                targetSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.GATEWAY_SYMBOL,
                                targetSymbol: this.ANNOTATION_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            // IntermediateCatchEvent
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.TASK_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.GATEWAY_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.END_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                linkSymbolClass: this.SEQUENCE_FLOW_SYMBOL,
                                linkObjectReference: this.SEQUENCE_FLOWS_REFERENCE,
                                linkObjectParam: this.SEQUENCE_FLOW_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.DATA_OBJECT_SYMBOL,
                                linkSymbolClass: this.DATA_ASSOCIATION_SYMBOL,
                                linkObjectReference: this.DATA_ASSOCIATIONS_REFERENCE,
                                linkObjectParam: this.DATA_ASSOCIATION_OBJECT_PARAM
                            },
                            {
                                sourceSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                targetSymbol: this.ANNOTATION_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            // Annotation
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.START_EVENT_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.TASK_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.GATEWAY_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.DATA_OBJECT_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.END_EVENT_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            },
                            {
                                sourceSymbol: this.ANNOTATION_SYMBOL,
                                targetSymbol: this.INTERMEDIATE_CATCH_EVENT_SYMBOL,
                                linkSymbolClass: this.ASSOCIATION_SYMBOL,
                                linkObjectReference: this.ASSOCIATIONS_REFERENCE
                            }
                        ]
                    }
                ];

                this.addImagesFolder(oToolsDef);
                return oToolsDef;
            },

            /**
             * Gets the commands definition. The definition is an array of command definition.
             * Command definition has the parameters:
             * name: <The command name>
             * displayName: <The command display name>
             * tooltip: <The command tooltip>
             * type: <(optional) The command type>
             * isEnabled: <Indicates if the command is enabled>
             * isHidden: <Indicates if the command is visible>
             * canExecute: function (oParam), where oParam contains editor, diagram, symbol
             * execute: function (oParam)
             * smallIcon: <The small icon URL, usually 16x16>
             * largeIcon: <(optional) The large icon URL, usually 32x32>
             * @function
             * @name getCommandsDefinition
             * @memberOf sap.modeling.bpmn.ui.DiagramEditorExtension#
             * @param {Array} The commands definition.
             */
            getCommandsDefinition: function () {
                var self = this,
                    oCommandsDef;

                oCommandsDef = [
                        {
                            // Define a change task type command
                            name: this.CHANGE_TASK_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangeObjectTypeCommand",
                            attribute: "type",
                            onValueChanged: function () {
                                var index = this.getValueIndex();
                                if (index !== -1 && this.values[index].isChangeObject) {
                                    this.changeObjectType();
                                } else {
                                    this.changeObjectProperty();
                                }
                            },
                            values: [
                                { value: undefined, label: "Normal Task" },
                                { value: "script", label: "Script Task" }
                            ],
                            smallIcon: "ChangeType16.png"
                        },
                        {
                            // Define a change start event type command
                            name: this.CHANGE_START_EVENT_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangePropertyCommand",
                            attribute: "eventType",
                            onValueChanged: function () {
                                if (this.symbol) {
                                    self.changeStartEventType(this.symbol, this.selectedValue);
                                }
                            },
                            values: [
                                { value: undefined, label: "Normal Start Event" }
                            ],
                            smallIcon: "ChangeType16.png"
                        },
                        {
                            // Define a change end event type command
                            name: this.CHANGE_END_EVENT_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangePropertyCommand",
                            attribute: "eventType",
                            onValueChanged: function () {
                                if (this.symbol) {
                                    self.changeEndEventType(this.symbol, this.selectedValue);
                                }
                            },
                            values: [
                                { value: undefined, label: "Normal End Event" }
                            ],
                            smallIcon: "ChangeType16.png"
                        },
                        {
                            // Define a change intermediate catch event type command
                            name: this.CHANGE_INTERMEDIATE_CATCH_EVENT_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangeObjectTypeCommand",
                            attribute: "eventType",
                            onValueChanged: function () {
                                var index = this.getValueIndex();
                                if (index !== -1 && this.values[index].isChangeObject) {
                                    this.changeObjectType();
                                } else {
                                    this.changeObjectProperty();
                                }
                            },
                            values: [
								{ value: undefined, label: "Normal Event" },
                                { value: "timer", label: "Timer Event" }
                            ],
                            smallIcon: "ChangeType16.png"
                        },
						{
                            // Define a change gateway type command
                            name: this.CHANGE_GATEWAY_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangePropertyCommand",
                            attribute: "type",
                            values: [
                                { value: undefined, label: "Normal Gateway" },
                                { value: "exclusive", label: "Exclusive Gateway" },
                                { value: "eventBased", label: "Event-Based Gateway" },
                            ],
                            smallIcon: "ChangeType16.png"
                        },
                        {
                            // Define a change data object type command
                            name: this.CHANGE_DATA_OBJECT_TYPE_COMMAND,
                            className: "sap.galilei.ui.editor.command.ChangeObjectTypeCommand",
                            values: [],
                            smallIcon: "ChangeType16.png"
                        }
                    ];

                this.addImagesFolder(oCommandsDef);
                return oCommandsDef;
            }
        }
    });
}());
