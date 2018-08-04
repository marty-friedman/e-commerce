(function (){

    "use strict";

    /**
     * Checks if an object name is the default one (it is not changed).
     * @param oObject
     * @returns {boolean}
     */
    function isDefaultName (oObject) {
        var sName,
            length;

        if (oObject) {
            sName = oObject.displayName;
            length = oObject.classDefinition.displayName.length;
            if (sName && sName.length > length && sName.substring(0, length) === oObject.classDefinition.displayName &&
                sName[length] === " ") {
                sName = sName.substring(length + 1);
                sName = +sName;
                if (!isNaN(sName)) {
                    return true;
                }
            }
        }
        return false;
    }

    var oResource,
        oReader,
        oSymbolClass,
        oDiagramDef = {
            contents: {
                "Sap.Modeling.Bpmn.Diagram": {
                    classDefinition: "sap.galilei.model.Package",
                    displayName: "Sap Modeling Bpmn Diagram",
                    namespaceName: "sap.modeling.bpmn.ui",
                    classifiers: {

                        /**
                         * @class
                         * @name Diagram
                         * The BPMN diagram
                         */
                        "Diagram": {
                            displayName: "Diagram",
                            parent: "sap.galilei.ui.diagram.Diagram"
                        },

                        /**
                         * @class
                         * @name TaskSymbol
                         * The task symbol
                         */
                        "TaskSymbol": {
                            displayName: "Task Symbol",
                            parent: "sap.galilei.ui.diagram.CompositeSymbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.TaskSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "TaskSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.Task" },
                                layoutTemplate: {
                                    mainShape: [
                                        { shape: "RoundedRectangle", r: 8, domClass: "task", stroke: "#428EB0", strokeWidth: 1, fill: "#D9EFFF", width: 100, height: 60, minWidth: 60, minHeight: 40 },
                                        { shape: "Panel", shapes: [
                                            { shape: "Image", useReference: true, href: "{object/type:typeIconUrl}", x: 4, y: 4, width: 16, height: 16 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack", horizontalAlignment: "width", verticalAlignment: "height", innerAlignment: "center", paddingLeft: 4, paddingRight: 4,
                                        shapes: [
                                            { shape: "Text", domClass: "taskName", text: "{object/displayName}", font: "12px Arial, Helvetica, sans-serif", fill: "black", horizontalAlignment: "width", verticalAlignment: "height", isWordWrap: true, isEllipsis: true },
                                            { shape: "Stack", orientation: "horizontal", innerAlignment: "center", padding: 0, margin: 0, innerPadding: 4, shapes: [
                                                { shape: "Image", useReference: true, href: "{object/loopType:loopTypeIconUrl}", isVisible: "{object/loopType:loopTypeIconVisible}", width: 12, height: 12 },
                                                { shape: "Image", useReference: true, href: "#Bpmn.CompensationIcon", isVisible: "{object/isForCompensation}", width: 12, height: 12 }
                                            ]}
                                        ]
                                    }
                                },
                                formatters: {
                                    typeIconUrl: {
                                        "service": "#Bpmn.ServiceTaskIcon",
                                        "script": "#Bpmn.ScriptTaskIcon",
                                    },
                                    loopTypeIconUrl: {
                                        "loop": "#Bpmn.LoopIcon",
                                        "sequence": "#Bpmn.SequenceLoopIcon",
                                        "parallel": "#Bpmn.ParallelLoopIcon"
                                    },
                                    loopTypeIconVisible: function () {
                                        return !!((this.object && ["loop", "sequence", "parallel"].indexOf(this.object.loopType) >= 0));
                                    }
                                }
//                                events: {
//                                    contextmenu: function (oEvent, oSymbol, oExtension) {
//                                        console.log("context menu!");
//                                        oEvent.stopImmediatePropagation();
//                                        oEvent.preventDefault();
//                                        // Hides context button pad
//                                        if (oExtension) {
//                                            oExtension.editor.hideContextButtonPad(this);
//                                        }
//                                    }
//                                }
                            }
                        },

                        /**
                         * @class
                         * @name StartEventSymbol
                         * The start event Symbol
                         */
                        "StartEventSymbol": {
                            displayName: "Start Event Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "StartEventSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "StartEventSymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "StartEventSymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "StartEventSymbol.width": { name: "width", defaultValue: 32 },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "StartEventSymbol.height": { name: "height", defaultValue: 32 },
                                /**
                                 * Indicates whether the name is default or not.
                                 * @memberOf sap.galilei.ui.diagram.StartEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 */
                                "StartEventSymbol.isNotDefaultName": {
                                    name: "isNotDefaultName",
                                    dataType: sap.galilei.model.dataTypes.gBool,
                                    get: function () {
                                        return !isDefaultName(this.object);
                                    }
                                }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.StartEvent" },
                                layoutTemplate: {
                                    displayMode: sap.galilei.ui.common.ShapeLayoutModes.icon,
                                    mainShape: [
                                        { shape: "Circle", domClass: "startEvent", cx: 16, cy: 16, r: 16, stroke: "#288000", strokeWidth: 1, fill: "#ECFDDC", strokeDashArray: "{object/isInterrupting:boundaryStyle}" },
                                        { shape: "Panel", shapes: [
                                            { shape: "Image", useReference: true, href: "{object/eventType:typeIconUrl}", x: 0, y: 0, width: 32, height: 32 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        shapes: [{
                                            shape: "Text",
                                            domClass: "startEventName",
                                            text: "{object/displayName}",
                                            font: "10px Arial, Helvetica, sans-serif",
                                            fill: "black",
                                            horizontalAlignment: "width",
                                            verticalAlignment: "top",
                                            isVisible: "{isNotDefaultName}",
                                            isWordWrap: true
                                        }]
                                    }
                                },
                                formatters: {
                                    typeIconUrl: {
                                    },
                                    boundaryStyle: {
                                        "false": "3,3"
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name IntermediateCatchEventSymbol
                         * The intermediate catch event symbol
                         */
                        "IntermediateCatchEventSymbol": {
                            displayName: "Intermediate Catch Event Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "IntermediateCatchEventSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "IntermediateCatchEventSymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "IntermediateCatchEventSymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "IntermediateCatchEventSymbol.width": { name: "width", defaultValue: 32 },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "IntermediateCatchEventSymbol.height": { name: "height", defaultValue: 32 },
                                /**
                                 * Indicates whether the name is default or not.
                                 * @memberOf sap.galilei.ui.diagram.IntermediateCatchEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 */
                                "IntermediateCatchEventSymbol.isNotDefaultName": {
                                    name: "isNotDefaultName",
                                    dataType: sap.galilei.model.dataTypes.gBool,
                                    get: function () {
                                        return !isDefaultName(this.object);
                                    }
                                }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.IntermediateCatchEvent" },
                                layoutTemplate: {
                                    displayMode: sap.galilei.ui.common.ShapeLayoutModes.icon,
                                    mainShape: [
                                        { shape: "Circle", domClass: "intermediateCatchEvent", cx: 16, cy: 16, r: 16, stroke: "#4A7EBB", strokeWidth: 1, fill: "#E5EEFF" },
                                        { shape: "Panel", shapes: [
                                            { shape: "Circle", domClass: "intermediateCatchEvent2", cx: 16, cy: 16, r: 13, stroke: "#4A7EBB", strokeWidth: 1, fill: "none" },
											{ shape: "Image", useReference: true, href: "{object/eventType:typeIconUrl}", x: 0, y: 0, width: 32, height: 32 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        shapes: [{
                                            shape: "Text",
                                            domClass: "intermediateCatchEventName",
                                            text: "{object/displayName}",
                                            font: "10px Arial, Helvetica, sans-serif",
                                            fill: "black",
                                            horizontalAlignment: "width",
                                            verticalAlignment: "top",
                                            isVisible: "{isNotDefaultName}",
                                            isWordWrap: true
                                        }]
                                    }
                                },
                                formatters: {
                                    typeIconUrl: {
                                        "timer": "#Bpmn.IntermediateCatchingTimerEventIcon",
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name EndEventSymbol
                         * The end event symbol
                         */
                        "EndEventSymbol": {
                            displayName: "End Event Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "EndEventSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "EndEventSymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "EndEventSymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "EndEventSymbol.width": { name: "width", defaultValue: 32 },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "EndEventSymbol.height": { name: "height", defaultValue: 32 },
                                /**
                                 * Indicates whether the name is default or not.
                                 * @memberOf sap.galilei.ui.diagram.EndEventSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 */
                                "EndEventSymbol.isNotDefaultName": {
                                    name: "isNotDefaultName",
                                    dataType: sap.galilei.model.dataTypes.gBool,
                                    get: function () {
                                        return !isDefaultName(this.object);
                                    }
                                }
                            },
                            statics: {
                                objectClass: { value: "hybris.modeling.bpmn.EndEvent" },
                                layoutTemplate: {
                                    displayMode: sap.galilei.ui.common.ShapeLayoutModes.icon,
                                    mainShape: [
                                        { shape: "Circle", cx: 16, cy: 16, r: 16, domClass: "endEvent", stroke: "#CC2424", strokeWidth: 3, fill: "#FFE6E5" },
                                        { shape: "Panel", shapes: [
                                            { shape: "Image", useReference: true, href: "{object/eventType:typeIconUrl}", x: 0, y: 0, width: 32, height: 32 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        shapes: [{
                                            shape: "Text",
                                            domClass: "endEventName",
                                            text: "{object/displayName}",
                                            font: "10px Arial, Helvetica, sans-serif",
                                            fill: "black",
                                            horizontalAlignment: "width",
                                            verticalAlignment: "top",
                                            isVisible: "{isNotDefaultName}",
                                            isWordWrap: true
                                        }]
                                    }
                                },
                                formatters: {
                                    typeIconUrl: {
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name GatewaySymbol
                         * The gateway symbol
                         */
                        "GatewaySymbol": {
                            displayName: "Gateway Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.GatewaySymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "GatewaySymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.GatewaySymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "GatewaySymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.GatewaySymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "GatewaySymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.GatewaySymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 42
                                 */
                                "GatewaySymbol.width": { name: "width", get: function () { return 42; } },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.GatewaySymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 42
                                 */
                                "GatewaySymbol.height": { name: "height", get: function () { return 42; } }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.Gateway" },
                                layoutTemplate: {
                                    displayMode: sap.galilei.ui.common.ShapeLayoutModes.icon,
                                    mainShape: [
                                        { shape: "Polygon", domClass: "gateway", points: "21,1 41,21 21,41 1,21", stroke: "#997C35", strokeWidth: 1, fill: "#FFFFD6", strokeLineJoin: "round" },
                                        { shape: "Panel", shapes: [
                                            { shape: "Image", useReference: true, href: "{object/type:typeIconUrl}", x: 1, y: 1, width: 40, height: 40 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        shapes: [{
                                            shape: "Text",
                                            domClass: "gatewayText",
                                            text: "{object/expression}",
                                            font: "10px Arial, Helvetica, sans-serif",
                                            fill: "black",
                                            horizontalAlignment: "width",
                                            verticalAlignment: "top",
                                            isWordWrap: true
                                        }]
                                    }
                                },
                                formatters: {
                                    typeIconUrl: {
                                        "exclusive": "#Bpmn.ExclusiveGatewayIcon",
                                        "eventBased": "#Bpmn.EventBasedGatewayIcon"
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name DataObjectSymbol
                         * The data object symbol
                         */
                        "DataObjectSymbol": {
                            displayName: "Data Object Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "DataObjectSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "DataObjectSymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "DataObjectSymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 34
                                 */
                                "DataObjectSymbol.width": { name: "width", defaultValue: 34 },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 40
                                 */
                                "DataObjectSymbol.height": { name: "height", defaultValue: 40 },
                                /**
                                 * Indicates whether the name is default or not.
                                 * @memberOf sap.galilei.ui.diagram.DataObjectSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 */
                                "DataObjectSymbol.isNotDefaultName": {
                                    name: "isNotDefaultName",
                                    dataType: sap.galilei.model.dataTypes.gBool,
                                    get: function () {
                                        return !isDefaultName(this.object);
                                    }
                                }
                            },
                            statics: {
                                objectClass: { value: "hybris.modeling.bpmn.DataObject" },
                                layoutTemplate: {
                                    displayMode: sap.galilei.ui.common.ShapeLayoutModes.icon,
                                    mainShape: [
                                        { shape: "Polygon", domClass: "dataObject", points: "26,0 0,0 0,40 34,40 34,8 26,0", stroke: "#606060", strokeWidth: 1, fill: "#EEE" },
                                        { shape: "Panel", shapes: [
                                            { shape: "Polyline", domClass: "dataObjectStroke", points: "26,0 26,8 34,8", stroke: "#606060", strokeWidth: 1, fill: "none" },
                                            { shape: "Image", useReference: true, href: "{object/isCollection:collectionIconUrl}", x: 12, y: 27, width: 12, height: 12 }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        shapes: [{
                                            shape: "Text",
                                            domClass: "dataObjectName",
                                            text: "{object/displayName}",
                                            font: "10px Arial, Helvetica, sans-serif",
                                            fill: "black",
                                            horizontalAlignment: "width",
                                            verticalAlignment: "top",
                                            isVisible: "{isNotDefaultName}",
                                            isWordWrap: true
                                        }]
                                    }
                                },
                                formatters: {
                                    collectionIconUrl: {
                                        "true": "#Bpmn.MultipleIcon"
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name SequenceFlowSymbol
                         * The sequence flow symbol
                         */
                        "SequenceFlowSymbol": {
                            displayName: "Sequence Flow Symbol",
                            parent: "sap.galilei.ui.diagram.LinkSymbol",
                            properties: {
                                /**
                                 * Gets the arrow type.
                                 * @memberOf sap.galilei.ui.diagram.SequenceFlowSymbl#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 */
                                "SequenceFlowSymbl.sourceArrowType": {
                                    name: "sourceArrowType",
                                    dataType: sap.galilei.model.dataTypes.gString,
                                    get: function () {
                                        if (this.object && this.sourceSymbol && this.sourceSymbol.object) {
                                            if (this.sourceSymbol.object.defaultFlow === this.object) {
                                                 return "default";
                                            }
                                            else if (this.object.condition && sap.galilei.model.isInstanceOf(this.sourceSymbol.object, "sap.modeling.bpmn.Activity")) {
                                                return "condition";
                                            }
                                        }
                                    }
                                }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.SequenceFlow" },
                                layoutTemplate: {
                                    domClass: "sequenceFlow",
                                    stroke: "#428EB0",
                                    strokeWidth: 1,
                                    sourceArrow: "{sourceArrowType:sourceArrowMarker}",
                                    lineStyle: "{lineStyle}",
                                    targetArrow: "Arrows.FilledEnd",
                                    sourceContent: {
                                        shape: "Text",
                                        domClass: "sequenceFlowText",
                                        text: "{object/condition}",
                                        font: "10px Arial, Helvetica, sans-serif",
                                        fill: "black",
                                        horizontalAlignment: "middle",
                                        verticalAlignment: "middle",
                                        isEllipsis: false,
                                        isVisible: true
                                    },
									middleContent: {
										shape: "Text",
										text: "{object/displayName}",
										font: "12px Calibri",
										fill: "black",
										horizontalAlignment: "middle",
										verticalAlignment: "middle",
										isEllipsis: true,
										isVisible: true
									}
                                },
                                formatters: {
                                    sourceArrowMarker: {
                                        "default": "Arrows.SlashStart",
                                        "condition": "Arrows.DiamondStart"
                                    }
                                }
//                                events: {
//                                    pointerdown: function (oEvent, oSymbol, oExtension) {
//
//                                    },
//                                    contextmenu: function (oEvent, oSymbol, oExtension) {
//                                        console.log("link symbol context menu!");
//                                        oEvent.stopImmediatePropagation();
//                                        oEvent.preventDefault();
//                                        // Hides context button pad
//                                        if (oExtension) {
//                                            oExtension.editor.hideContextButtonPad(this);
//                                        }
//                                    }
//                                }
                            }
                        },

                        /**
                         * @class
                         * @name DataAssociationSymbol
                         * The data association symbol
                         */
                        "DataAssociationSymbol": {
                            displayName: "Data Association Symbol",
                            parent: "sap.galilei.ui.diagram.LinkSymbol",
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.DataAssociation" },
                                layoutTemplate: {
                                    domClass: "dataAssociation",
                                    stroke: "#428EB0",
                                    strokeWidth: 1,
                                    strokeDashArray: "3,3",
                                    lineStyle: "{lineStyle}",
                                    targetArrow: "Arrows.LineEnd"
                                }
                            }
                        },

                        /**
                         * @class
                         * @name AnnotationSymbol
                         * The annotation symbol
                         */
                        "AnnotationSymbol": {
                            displayName: "Annotation Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.Annotation" },
                                layoutTemplate: {
                                    mainShape: [
                                        { shape: "Rectangle", width: 60, height: 40, stroke: "white", strokeWidth: 1, fill: "white", opacity: 0.7 },
                                        { shape: "Panel", shapes: [
                                            { shape: "Polyline", points: "20,0 0,0 0,60 20,60", stroke: "#666", strokeWidth: 1, verticalAlignment: "height" }
                                        ]}
                                    ],
                                    contentShape: {
                                        shape: "Stack",
                                        horizontalAlignment: "width",
                                        verticalAlignment: "height",
                                        innerAlignment: "middle",
                                        shapes: [
                                            { shape: "Text", margin: 4, text: "{object/text}", isWordWrap: true, isEllipsis: true, font: "12px Arial, Helvetica, sans-serif", fill: "black", horizontalAlignment: "left", verticalAlignment: "middle" }
                                        ]
                                    }
                                }
                            }
                        },

                        /**
                         * @class
                         * @name AssociationSymbol
                         * The association symbol
                         */
                        "AssociationSymbol": {
                            displayName: "Association Symbol",
                            parent: "sap.galilei.ui.diagram.LinkSymbol",
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.Association" },
                                layoutTemplate: {
                                    domClass: "association",
                                    stroke: "#666",
                                    strokeWidth: 1,
                                    strokeDashArray: "3,3",
                                    lineStyle: "{lineStyle}"
                                }
                            }
                        },

                        /**
                         * @class
                         * @name MessageSymbol
                         * The message symbol
                         */
                        "MessageSymbol": {
                            displayName: "Message Symbol",
                            parent: "sap.galilei.ui.diagram.Symbol",
                            properties: {
                                /**
                                 * Indicates whether the symbol size should be adjusted to its content.
                                 * @memberOf sap.galilei.ui.diagram.MessageSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default false
                                 */
                                "MessageSymbol.isAdjustToContent": { name: "isAdjustToContent", defaultValue: false },
                                /**
                                 * Gets or sets the display mode of the symbol.
                                 * The available values are defined in sap.galilei.ui.common.ShapeLayoutModes.
                                 * @memberOf sap.galilei.ui.diagram.MessageSymbol#
                                 * @type {sap.galilei.model.dataTypes.gString}
                                 * @default sap.galilei.ui.common.ShapeLayoutModes.icon
                                 */
                                "MessageSymbol.displayMode": { name: "displayMode", defaultValue: sap.galilei.ui.common.ShapeLayoutModes.icon },
                                /**
                                 * Indicates whether the symbol can be resized.
                                 * @memberOf sap.galilei.ui.diagram.MessageSymbol#
                                 * @type {sap.galilei.model.dataTypes.gBool}
                                 * @default true
                                 */
                                "MessageSymbol.isKeepSize": { name: "isKeepSize", defaultValue: true },
                                /**
                                 * Gets or sets the width of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.MessageSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 32
                                 */
                                "MessageSymbol.width": { name: "width", defaultValue: 32 },
                                /**
                                 * Gets or sets the height of the symbol.
                                 * @memberOf sap.galilei.ui.diagram.MessageSymbol#
                                 * @type {sap.galilei.model.dataTypes.gDouble}
                                 * @default 20
                                 */
                                "MessageSymbol.height": { name: "height", defaultValue: 20 }
                            },
                            statics: {
                                objectClass: { value: "sap.modeling.bpmn.Message" },
                                layoutTemplate: {
                                    mainShape: [
                                        { shape: "Rectangle", stroke: "#C4AA4C", strokeWidth: 1, fill: "#FFFFE0", width: 32, height: 20 },
                                        { shape: "Panel", shapes: [
                                            { shape: "Polyline", stroke: "#C4AA4C", strokeWidth: 1, points: "0,0 16,10 32,0" }
                                        ]}
                                    ]
                                }
                            }
                        }
                    }
                }
            }
        };

    oResource = new sap.galilei.model.Resource();
    oReader = new sap.galilei.model.JSONReader();
    oReader.load(oResource, oDiagramDef);

    // Manually add listener for sub-process symbol reference changes
    oSymbolClass = sap.galilei.model.getClass("sap.modeling.bpmn.ui.SubProcessSymbol");
    if (oSymbolClass) {
        oSymbolClass.addReferenceListener({
            onReferenceChanged: function(oEventArgs){
                var oInstance = oEventArgs.instance;
                if (oInstance && !sap.galilei.model.isInUndoRedo()) {
                    oInstance.isExpanded = !(oInstance.contentSymbols.length === 0);
                }
            }
        }, "symbols");
    }

}());
