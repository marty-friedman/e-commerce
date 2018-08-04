(function () {

    "use strict";

    sap.modeling.bpmn.ui.Library = sap.galilei.ui.common.library.defineLibrary({

        // Define full class name
        fullClassName: "sap.modeling.bpmn.ui.Library",

        // Define library name
        libraryName: "Bpmn",

        // define statics
        statics: {

            /**
             * Initializes the library.
             * @function
             * @static
             * @name onInitialize
             * @memberOf sap.modeling.bpmn.ui.Library#
             */
            onInitialize: function () {
                this.addShape("ServiceTaskIcon", {
                    shape: "Path",
                    domClass: "taskIconFill",
                    path: "M31.229,17.736c0.064-0.571,0.104-1.148,0.104-1.736s-0.04-1.166-0.104-1.737l-4.377-1.557c-0.218-0.716-0.504-1.401-0.851-2.05l1.993-4.192c-0.725-0.91-1.549-1.734-2.458-2.459l-4.193,1.994c-0.647-0.347-1.334-0.632-2.049-0.849l-1.558-4.378C17.165,0.708,16.588,0.667,16,0.667s-1.166,0.041-1.737,0.105L12.707,5.15c-0.716,0.217-1.401,0.502-2.05,0.849L6.464,4.005C5.554,4.73,4.73,5.554,4.005,6.464l1.994,4.192c-0.347,0.648-0.632,1.334-0.849,2.05l-4.378,1.557C0.708,14.834,0.667,15.412,0.667,16s0.041,1.165,0.105,1.736l4.378,1.558c0.217,0.715,0.502,1.401,0.849,2.049l-1.994,4.193c0.725,0.909,1.549,1.733,2.459,2.458l4.192-1.993c0.648,0.347,1.334,0.633,2.05,0.851l1.557,4.377c0.571,0.064,1.148,0.104,1.737,0.104c0.588,0,1.165-0.04,1.736-0.104l1.558-4.377c0.715-0.218,1.399-0.504,2.049-0.851l4.193,1.993c0.909-0.725,1.733-1.549,2.458-2.458l-1.993-4.193c0.347-0.647,0.633-1.334,0.851-2.049L31.229,17.736zM16,20.871c-2.69,0-4.872-2.182-4.872-4.871c0-2.69,2.182-4.872,4.872-4.872c2.689,0,4.871,2.182,4.871,4.872C20.871,18.689,18.689,20.871,16,20.871z",
                    transform: "scale(0.5)"
                });

                this.addShape("ScriptTaskIcon", {
                    shape: "Path",
                    domClass: "taskIconFill",
                    path: "M2.021,9.748L2.021,9.748V9.746V9.748zM2.022,9.746l5.771,5.773l-5.772,5.771l2.122,2.123l7.894-7.895L4.143,7.623L2.022,9.746zM12.248,23.269h14.419V20.27H12.248V23.269zM16.583,17.019h10.084V14.02H16.583V17.019zM12.248,7.769v3.001h14.419V7.769H12.248z",
                    transform: "scale(0.5)"
                });

                this.addShape("UserTaskIcon", {
                    shape: "Path",
                    domClass: "taskIconFill",
                    path: "M20.771,12.364c0,0,0.849-3.51,0-4.699c-0.85-1.189-1.189-1.981-3.058-2.548s-1.188-0.454-2.547-0.396c-1.359,0.057-2.492,0.792-2.492,1.188c0,0-0.849,0.057-1.188,0.397c-0.34,0.34-0.906,1.924-0.906,2.321s0.283,3.058,0.566,3.624l-0.337,0.113c-0.283,3.283,1.132,3.68,1.132,3.68c0.509,3.058,1.019,1.756,1.019,2.548s-0.51,0.51-0.51,0.51s-0.452,1.245-1.584,1.698c-1.132,0.452-7.416,2.886-7.927,3.396c-0.511,0.511-0.453,2.888-0.453,2.888h26.947c0,0,0.059-2.377-0.452-2.888c-0.512-0.511-6.796-2.944-7.928-3.396c-1.132-0.453-1.584-1.698-1.584-1.698s-0.51,0.282-0.51-0.51s0.51,0.51,1.02-2.548c0,0,1.414-0.397,1.132-3.68H20.771z",
                    transform: "scale(0.5)"
                });

                this.addShape("ManualTaskIcon", {
                    shape: "Path",
                    domClass: "taskIconFill",
                    path: "M14.296,27.885v-2.013c0,0-0.402-1.408-1.073-2.013c-0.671-0.604-1.274-1.274-1.409-1.61c0,0-0.268,0.135-0.737-0.335s-1.812-2.616-1.812-2.616l-0.671-0.872c0,0-0.47-0.671-1.275-1.342c-0.805-0.672-0.938-0.067-1.476-0.738s0.604-1.275,1.006-1.409c0.403-0.134,1.946,0.134,2.684,0.872c0.738,0.738,0.738,0.738,0.738,0.738l1.073,1.141l0.537,0.201l0.671-1.073l-0.269-2.281c0,0-0.604-2.55-0.737-4.764c-0.135-2.214-0.47-5.703,1.006-5.837s1.007,2.55,1.073,3.489c0.067,0.938,0.806,5.232,1.208,5.568c0.402,0.335,0.671,0.066,0.671,0.066l0.402-7.514c0,0-0.479-2.438,1.073-2.549c0.939-0.067,0.872,1.543,0.872,2.147c0,0.604,0.269,7.514,0.269,7.514l0.537,0.135c0,0,0.402-2.214,0.604-3.153s0.604-2.416,0.537-3.087c-0.067-0.671-0.135-2.348,1.006-2.348s0.872,1.812,0.939,2.415s-0.134,3.153-0.134,3.757c0,0.604-0.738,3.623-0.537,3.824s2.08-2.817,2.349-3.958c0.268-1.141,0.201-3.02,1.408-2.885c1.208,0.134,0.47,2.817,0.402,3.086c-0.066,0.269-0.671,2.349-0.872,2.952s-0.805,1.476-1.006,2.013s0.402,2.349,0,4.629c-0.402,2.281-1.61,5.166-1.61,5.166l0.604,2.08c0,0-1.744,0.671-3.824,0.805C16.443,28.221,14.296,27.885,14.296,27.885z",
                    transform: "scale(0.5)"
                });

                this.addShape("BusinessRuleTaskIcon", {
                    shape: "Path",
                    domClass: "taskIconFill",
                    path: "M25.754,4.626c-0.233-0.161-0.536-0.198-0.802-0.097L12.16,9.409c-0.557,0.213-1.253,0.316-1.968,0.316c-0.997,0.002-2.029-0.202-2.747-0.48C7.188,9.148,6.972,9.04,6.821,8.943c0.056-0.024,0.12-0.05,0.193-0.075L18.648,4.43l1.733,0.654V3.172c0-0.284-0.14-0.554-0.374-0.714c-0.233-0.161-0.538-0.198-0.802-0.097L6.414,7.241c-0.395,0.142-0.732,0.312-1.02,0.564C5.111,8.049,4.868,8.45,4.872,8.896c0,0.012,0.004,0.031,0.004,0.031v17.186c0,0.008-0.003,0.015-0.003,0.021c0,0.006,0.003,0.01,0.003,0.016v0.017h0.002c0.028,0.601,0.371,0.983,0.699,1.255c1.034,0.803,2.769,1.252,4.614,1.274c0.874,0,1.761-0.116,2.583-0.427l12.796-4.881c0.337-0.128,0.558-0.448,0.558-0.809V5.341C26.128,5.057,25.988,4.787,25.754,4.626zM5.672,11.736c0.035,0.086,0.064,0.176,0.069,0.273l0.004,0.054c0.016,0.264,0.13,0.406,0.363,0.611c0.783,0.626,2.382,1.08,4.083,1.093c0.669,0,1.326-0.083,1.931-0.264v1.791c-0.647,0.143-1.301,0.206-1.942,0.206c-1.674-0.026-3.266-0.353-4.509-1.053V11.736zM10.181,24.588c-1.674-0.028-3.266-0.354-4.508-1.055v-2.712c0.035,0.086,0.065,0.176,0.07,0.275l0.002,0.053c0.018,0.267,0.13,0.408,0.364,0.613c0.783,0.625,2.381,1.079,4.083,1.091c0.67,0,1.327-0.082,1.932-0.262v1.789C11.476,24.525,10.821,24.588,10.181,24.588z",
                    transform: "scale(0.5)"
                });

                this.addShape("SendTaskIcon", [
                    { shape: "Path",
                      domClass: "taskIconFill",
                      path: "M28.516,7.167H3.482l12.517,7.108L28.516,7.167zM16.74,17.303C16.51,17.434,16.255,17.5,16,17.5s-0.51-0.066-0.741-0.197L2.5,10.06v14.773h27V10.06L16.74,17.303z",
                      transform: "scale(0.5)"
                    }
                ]);

                this.addShape("ReceiveTaskIcon", [
                    { shape: "Rectangle", domClass: "taskIconStroke", x: 2, y: 4, width: 13, height: 8, stroke: "black", strokeWidth: 1.5, fill: "white" },
                    { shape: "Polyline", domClass: "taskIconStroke", points: "2,4 8.5,8 15,4", stroke: "black", strokeWidth: 1.5, fill: "none" }
                ]);

                // 40 x 40
                this.addShape("ComplexGatewayIcon", [
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 10, y1: 20, x2: 30, y2: 20 },
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 20, y1: 10, x2: 20, y2: 30 },
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 13, y1: 13, x2: 27, y2: 27 },
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 13, y1: 27, x2: 27, y2: 13 }
                ]);

                // 40 x 40
                this.addShape("InclusiveGatewayIcon", {
                    shape: "Circle", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, cx: 20, cy: 20, r: 9, fill: "white"
                });

                // 40 x 40
                this.addShape("ExclusiveGatewayIcon", [
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 13, y1: 13, x2: 27, y2: 27 },
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 13, y1: 27, x2: 27, y2: 13 }
                ]);

                // 40 x 40
                this.addShape("ParallelGatewayIcon", [
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 10, y1: 20, x2: 30, y2: 20 },
                    { shape: "Line", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 3, x1: 20, y1: 10, x2: 20, y2: 30 }
                ]);

                // 40 x 40
                this.addShape("EventBasedGatewayIcon", [
                    { shape: "Circle", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 1.2, cx: 20, cy: 20, r: 11, fill: "none" },
                    { shape: "Circle", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 1.2, cx: 20, cy: 20, r: 8, fill: "none" },
                    { shape: "Polygon", domClass: "gatewayIconStroke", points: "15,19 20,15 25,19 23,24 17,24", strokeWidth: 1.5, fill: "white", stroke: "black" }
                ]);

                // 40 x 40
                this.addShape("ExclusiveEventBasedGatewayIcon", [
                    { shape: "Circle", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 1.2, cx: 20, cy: 20, r: 10, fill: "none" },
                    { shape: "Polygon", domClass: "gatewayIconStroke", points: "14,19 20,14 26,19 23,25 17,25", strokeWidth: 1.5, fill: "white", stroke: "black" }
                ]);

                // 40 x 40
                this.addShape("ParallelEventBasedGatewayIcon", [
                    { shape: "Circle", domClass: "gatewayIconStroke", stroke: "black", strokeWidth: 1.2, cx: 20, cy: 20, r: 10, fill: "none" },
                    { shape: "Polygon", domClass: "gatewayIconStroke", points: "14,18 18,18 18,14 22,14 22,18 26,18 26,22 22,22 22,26 18,26 18,22 14,22", strokeWidth: 1.5, fill: "white", stroke: "black" }
                ]);

    //            // 32 x 32
    //            this.addShape("ComplexGatewayIcon", [
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 11, y1: 11, x2: 21, y2: 21 },
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 11, y1: 21, x2: 21, y2: 11 },
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 8, y1: 16, x2: 24, y2: 16 },
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 16, y1: 8, x2: 16, y2: 24 }
    //            ]);
    //
    //            // 32 x 32
    //            this.addShape("InclusiveGatewayIcon", {
    //                shape: "Circle", stroke: "black", strokeWidth: 2.4, cx: 16, cy: 16, r: 7, fill: "none"
    //            });
    //
    //            // 32 x 32
    //            this.addShape("ExclusiveGatewayIcon", [
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 11, y1: 11, x2: 21, y2: 21 },
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 11, y1: 21, x2: 21, y2: 11 }
    //            ]);
    //
    //            // 32 x 32
    //            this.addShape("ParallelGatewayIcon", [
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 8, y1: 16, x2: 24, y2: 16 },
    //                { shape: "Line", stroke: "black", strokeWidth: 2.4, x1: 16, y1: 8, x2: 16, y2: 24 }
    //            ]);
    //
    //            // 32 x 32
    //            this.addShape("EventBasedGatewayIcon", [
    //                { shape: "Circle", stroke: "black", strokeWidth: 1.2, cx: 16, cy: 16, r: 9, fill: "none" },
    //                { shape: "Circle", stroke: "black", strokeWidth: 1.2, cx: 16, cy: 16, r: 6, fill: "none" },
    //                { shape: "Polygon", points: "16,12 20,15.5 18,19.5 14,19.5 12,15.5", strokeWidth: 1.5, fill: "none", stroke: "black" }
    //            ]);
    //
    //            // 32 x 32
    //            this.addShape("ExclusiveEventBasedGatewayIcon", [
    //                { shape: "Circle", stroke: "black", strokeWidth: 1.2, cx: 16, cy: 16, r: 9, fill: "none" },
    //                { shape: "Polygon", points: "16,12 20,15.5 18,19.5 14,19.5 12,15.5", strokeWidth: 1.5, fill: "none", stroke: "black" }
    //            ]);
    //
    //            // 32 x 32
    //            this.addShape("ParallelEventBasedGatewayIcon", [
    //                { shape: "Circle", stroke: "black", strokeWidth: 1.2, cx: 16, cy: 16, r: 9, fill: "none" },
    //                { shape: "Polygon", points: "10,14 14,14 14,10 18,10 18,14 22,14 22,18 18,18 18,22 14,22 14,18 10,18", strokeWidth: 1.5, fill: "none", stroke: "black" }
    //            ]);

                // 40 x 40
    //            this.addShape("CatchingMessageEventIcon", [
    //                { shape: "Rectangle", x: 9, y: 13, width: 22, height: 14, stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Polyline", points: "9,13 20,19 30,14", stroke: "black", strokeWidth: 2, fill: "none" }
    //            ]);

                // 32 x 32
                this.addShape("StartMessageEventIcon", [
                    { shape: "Rectangle", domClass: "startEventIcon", x: 8, y: 11, width: 16, height: 10, stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Polyline", domClass: "startEventIcon", points: "8,11 16,16.5 24,11", stroke: "black", strokeWidth: 1, fill: "none" }
                ]);

                // 32 x 32
                this.addShape("IntermediateCatchingMessageEventIcon", [
                    { shape: "Rectangle", domClass: "intermediateCatchEventIcon", x: 8, y: 11, width: 16, height: 10, stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Polyline", domClass: "intermediateCatchEventIcon", points: "8,11 16,16.5 24,11", stroke: "black", strokeWidth: 1, fill: "none" }
                ]);

                // 40 x 40
    //            this.addShape("ThrowingMessageEventIcon", [
    //                { shape: "Rectangle", x: 8, y: 12, width: 24, height: 16, stroke: "none", strokeWidth: 2, fill: "black" },
    //                { shape: "Polyline", points: "8,12 20,19 32,12", stroke: "white", strokeWidth: 2, fill: "none" }
    //            ]);

                // 32 x 32
                this.addShape("IntermediateThrowingMessageEventIcon", [
                    { shape: "Path", domClass: "intermediateThrowEventIcon", path: "M28.516,7.167H3.482l12.517,7.108L28.516,7.167zM16.74,17.303C16.51,17.434,16.255,17.5,16,17.5s-0.51-0.066-0.741-0.197L2.5,10.06v14.773h27V10.06L16.74,17.303z",
                      transform: "translate(6, 6) scale(0.625)",
                      stroke: "none", fill: "black"
                    }
                ]);

                // 32 x 32
                this.addShape("EndMessageEventIcon", [
                    { shape: "Path", domClass: "endEventIcon", path: "M28.516,7.167H3.482l12.517,7.108L28.516,7.167zM16.74,17.303C16.51,17.434,16.255,17.5,16,17.5s-0.51-0.066-0.741-0.197L2.5,10.06v14.773h27V10.06L16.74,17.303z",
                        transform: "translate(6, 6) scale(0.625)",
                        stroke: "none", fill: "black"
                    }
                ]);

                // 40 x 40
    //            this.addShape("CatchingTimerEventIcon", {
    //                shape: "Path",
    //                path: "M15.5,2.374C8.251,2.375,2.376,8.251,2.374,15.5C2.376,22.748,8.251,28.623,15.5,28.627c7.249-0.004,13.124-5.879,13.125-13.127C28.624,8.251,22.749,2.375,15.5,2.374zM15.5,25.623C9.909,25.615,5.385,21.09,5.375,15.5C5.385,9.909,9.909,5.384,15.5,5.374c5.59,0.01,10.115,4.535,10.124,10.125C25.615,21.09,21.091,25.615,15.5,25.623zM8.625,15.5c-0.001-0.552-0.448-0.999-1.001-1c-0.553,0-1,0.448-1,1c0,0.553,0.449,1,1,1C8.176,16.5,8.624,16.053,8.625,15.5zM8.179,18.572c-0.478,0.277-0.642,0.889-0.365,1.367c0.275,0.479,0.889,0.641,1.365,0.365c0.479-0.275,0.643-0.887,0.367-1.367C9.27,18.461,8.658,18.297,8.179,18.572zM9.18,10.696c-0.479-0.276-1.09-0.112-1.366,0.366s-0.111,1.09,0.365,1.366c0.479,0.276,1.09,0.113,1.367-0.366C9.821,11.584,9.657,10.973,9.18,10.696zM22.822,12.428c0.478-0.275,0.643-0.888,0.366-1.366c-0.275-0.478-0.89-0.642-1.366-0.366c-0.479,0.278-0.642,0.89-0.366,1.367C21.732,12.54,22.344,12.705,22.822,12.428zM12.062,21.455c-0.478-0.275-1.089-0.111-1.366,0.367c-0.275,0.479-0.111,1.09,0.366,1.365c0.478,0.277,1.091,0.111,1.365-0.365C12.704,22.344,12.54,21.732,12.062,21.455zM12.062,9.545c0.479-0.276,0.642-0.888,0.366-1.366c-0.276-0.478-0.888-0.642-1.366-0.366s-0.642,0.888-0.366,1.366C10.973,9.658,11.584,9.822,12.062,9.545zM22.823,18.572c-0.48-0.275-1.092-0.111-1.367,0.365c-0.275,0.479-0.112,1.092,0.367,1.367c0.477,0.275,1.089,0.113,1.365-0.365C23.464,19.461,23.3,18.848,22.823,18.572zM19.938,7.813c-0.477-0.276-1.091-0.111-1.365,0.366c-0.275,0.48-0.111,1.091,0.366,1.367s1.089,0.112,1.366-0.366C20.581,8.702,20.418,8.089,19.938,7.813zM23.378,14.5c-0.554,0.002-1.001,0.45-1.001,1c0.001,0.552,0.448,1,1.001,1c0.551,0,1-0.447,1-1C24.378,14.949,23.929,14.5,23.378,14.5zM15.501,6.624c-0.552,0-1,0.448-1,1l-0.466,7.343l-3.004,1.96c-0.478,0.277-0.642,0.889-0.365,1.365c0.275,0.479,0.889,0.643,1.365,0.367l3.305-1.676C15.39,16.99,15.444,17,15.501,17c0.828,0,1.5-0.671,1.5-1.5l-0.5-7.876C16.501,7.072,16.053,6.624,15.501,6.624zM15.501,22.377c-0.552,0-1,0.447-1,1s0.448,1,1,1s1-0.447,1-1S16.053,22.377,15.501,22.377zM18.939,21.455c-0.479,0.277-0.643,0.889-0.366,1.367c0.275,0.477,0.888,0.643,1.366,0.365c0.478-0.275,0.642-0.889,0.366-1.365C20.028,21.344,19.417,21.18,18.939,21.455z",
    //                transform: "translate(4.5, 4.5)"
    //            });

                // 32 x 32
                this.addShape("StartTimerEventIcon", [
                    //{ shape: "Circle", cx: 16, cy: 16, r: 8, stroke: "none", fill: "white" },
                    { shape: "Path", domClass: "startEventIconFill",
                        path: "M15.5,2.374C8.251,2.375,2.376,8.251,2.374,15.5C2.376,22.748,8.251,28.623,15.5,28.627c7.249-0.004,13.124-5.879,13.125-13.127C28.624,8.251,22.749,2.375,15.5,2.374zM15.5,25.623C9.909,25.615,5.385,21.09,5.375,15.5C5.385,9.909,9.909,5.384,15.5,5.374c5.59,0.01,10.115,4.535,10.124,10.125C25.615,21.09,21.091,25.615,15.5,25.623zM8.625,15.5c-0.001-0.552-0.448-0.999-1.001-1c-0.553,0-1,0.448-1,1c0,0.553,0.449,1,1,1C8.176,16.5,8.624,16.053,8.625,15.5zM8.179,18.572c-0.478,0.277-0.642,0.889-0.365,1.367c0.275,0.479,0.889,0.641,1.365,0.365c0.479-0.275,0.643-0.887,0.367-1.367C9.27,18.461,8.658,18.297,8.179,18.572zM9.18,10.696c-0.479-0.276-1.09-0.112-1.366,0.366s-0.111,1.09,0.365,1.366c0.479,0.276,1.09,0.113,1.367-0.366C9.821,11.584,9.657,10.973,9.18,10.696zM22.822,12.428c0.478-0.275,0.643-0.888,0.366-1.366c-0.275-0.478-0.89-0.642-1.366-0.366c-0.479,0.278-0.642,0.89-0.366,1.367C21.732,12.54,22.344,12.705,22.822,12.428zM12.062,21.455c-0.478-0.275-1.089-0.111-1.366,0.367c-0.275,0.479-0.111,1.09,0.366,1.365c0.478,0.277,1.091,0.111,1.365-0.365C12.704,22.344,12.54,21.732,12.062,21.455zM12.062,9.545c0.479-0.276,0.642-0.888,0.366-1.366c-0.276-0.478-0.888-0.642-1.366-0.366s-0.642,0.888-0.366,1.366C10.973,9.658,11.584,9.822,12.062,9.545zM22.823,18.572c-0.48-0.275-1.092-0.111-1.367,0.365c-0.275,0.479-0.112,1.092,0.367,1.367c0.477,0.275,1.089,0.113,1.365-0.365C23.464,19.461,23.3,18.848,22.823,18.572zM19.938,7.813c-0.477-0.276-1.091-0.111-1.365,0.366c-0.275,0.48-0.111,1.091,0.366,1.367s1.089,0.112,1.366-0.366C20.581,8.702,20.418,8.089,19.938,7.813zM23.378,14.5c-0.554,0.002-1.001,0.45-1.001,1c0.001,0.552,0.448,1,1.001,1c0.551,0,1-0.447,1-1C24.378,14.949,23.929,14.5,23.378,14.5zM15.501,6.624c-0.552,0-1,0.448-1,1l-0.466,7.343l-3.004,1.96c-0.478,0.277-0.642,0.889-0.365,1.365c0.275,0.479,0.889,0.643,1.365,0.367l3.305-1.676C15.39,16.99,15.444,17,15.501,17c0.828,0,1.5-0.671,1.5-1.5l-0.5-7.876C16.501,7.072,16.053,6.624,15.501,6.624zM15.501,22.377c-0.552,0-1,0.447-1,1s0.448,1,1,1s1-0.447,1-1S16.053,22.377,15.501,22.377zM18.939,21.455c-0.479,0.277-0.643,0.889-0.366,1.367c0.275,0.477,0.888,0.643,1.366,0.365c0.478-0.275,0.642-0.889,0.366-1.365C20.028,21.344,19.417,21.18,18.939,21.455z",
                        stroke: "none", fill: "black",
                        transform: "translate(4.5, 4.5) scale(0.75, 0.75)"
                    }
                ]);

                // 32 x 32
                this.addShape("IntermediateCatchingTimerEventIcon", [
                    //{ shape: "Circle", cx: 16, cy: 16, r: 8, stroke: "none", fill: "white" },
                    { shape: "Path", domClass: "intermediateCatchEventIconFill",
                      path: "M15.5,2.374C8.251,2.375,2.376,8.251,2.374,15.5C2.376,22.748,8.251,28.623,15.5,28.627c7.249-0.004,13.124-5.879,13.125-13.127C28.624,8.251,22.749,2.375,15.5,2.374zM15.5,25.623C9.909,25.615,5.385,21.09,5.375,15.5C5.385,9.909,9.909,5.384,15.5,5.374c5.59,0.01,10.115,4.535,10.124,10.125C25.615,21.09,21.091,25.615,15.5,25.623zM8.625,15.5c-0.001-0.552-0.448-0.999-1.001-1c-0.553,0-1,0.448-1,1c0,0.553,0.449,1,1,1C8.176,16.5,8.624,16.053,8.625,15.5zM8.179,18.572c-0.478,0.277-0.642,0.889-0.365,1.367c0.275,0.479,0.889,0.641,1.365,0.365c0.479-0.275,0.643-0.887,0.367-1.367C9.27,18.461,8.658,18.297,8.179,18.572zM9.18,10.696c-0.479-0.276-1.09-0.112-1.366,0.366s-0.111,1.09,0.365,1.366c0.479,0.276,1.09,0.113,1.367-0.366C9.821,11.584,9.657,10.973,9.18,10.696zM22.822,12.428c0.478-0.275,0.643-0.888,0.366-1.366c-0.275-0.478-0.89-0.642-1.366-0.366c-0.479,0.278-0.642,0.89-0.366,1.367C21.732,12.54,22.344,12.705,22.822,12.428zM12.062,21.455c-0.478-0.275-1.089-0.111-1.366,0.367c-0.275,0.479-0.111,1.09,0.366,1.365c0.478,0.277,1.091,0.111,1.365-0.365C12.704,22.344,12.54,21.732,12.062,21.455zM12.062,9.545c0.479-0.276,0.642-0.888,0.366-1.366c-0.276-0.478-0.888-0.642-1.366-0.366s-0.642,0.888-0.366,1.366C10.973,9.658,11.584,9.822,12.062,9.545zM22.823,18.572c-0.48-0.275-1.092-0.111-1.367,0.365c-0.275,0.479-0.112,1.092,0.367,1.367c0.477,0.275,1.089,0.113,1.365-0.365C23.464,19.461,23.3,18.848,22.823,18.572zM19.938,7.813c-0.477-0.276-1.091-0.111-1.365,0.366c-0.275,0.48-0.111,1.091,0.366,1.367s1.089,0.112,1.366-0.366C20.581,8.702,20.418,8.089,19.938,7.813zM23.378,14.5c-0.554,0.002-1.001,0.45-1.001,1c0.001,0.552,0.448,1,1.001,1c0.551,0,1-0.447,1-1C24.378,14.949,23.929,14.5,23.378,14.5zM15.501,6.624c-0.552,0-1,0.448-1,1l-0.466,7.343l-3.004,1.96c-0.478,0.277-0.642,0.889-0.365,1.365c0.275,0.479,0.889,0.643,1.365,0.367l3.305-1.676C15.39,16.99,15.444,17,15.501,17c0.828,0,1.5-0.671,1.5-1.5l-0.5-7.876C16.501,7.072,16.053,6.624,15.501,6.624zM15.501,22.377c-0.552,0-1,0.447-1,1s0.448,1,1,1s1-0.447,1-1S16.053,22.377,15.501,22.377zM18.939,21.455c-0.479,0.277-0.643,0.889-0.366,1.367c0.275,0.477,0.888,0.643,1.366,0.365c0.478-0.275,0.642-0.889,0.366-1.365C20.028,21.344,19.417,21.18,18.939,21.455z",
                      stroke: "none", fill: "black",
                      transform: "translate(4.5, 4.5) scale(0.75, 0.75)"
                    }
                ]);

                // 40x 40
    //            this.addShape("CatchingConditionalEventIcon", [
    //                { shape: "Rectangle", x: 12, y: 12, width: 16, height: 17, stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Line", x1: 12, x2: 28, y1: 13, y2: 13, stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Line", x1: 12, x2: 28, y1: 18, y2: 18, stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Line", x1: 12, x2: 28, y1: 23, y2: 23, stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Line", x1: 12, x2: 28, y1: 28, y2: 28, stroke: "black", strokeWidth: 2, fill: "none" },
    //            ]);

                // 32x 32
                this.addShape("StartConditionalEventIcon", [
                    { shape: "Rectangle", domClass: "startEventIcon", x: 10, y: 8, width: 12, height: 16, stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Line", domClass: "startEventIcon", x1: 12, x2: 20, y1: 10, y2: 10, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "startEventIcon", x1: 12, x2: 20, y1: 14, y2: 14, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "startEventIcon", x1: 12, x2: 20, y1: 18, y2: 18, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "startEventIcon", x1: 12, x2: 20, y1: 22, y2: 22, stroke: "black", strokeWidth: 1.2, fill: "none" }
                ]);

                // 32x 32
                this.addShape("IntermediateCatchingConditionalEventIcon", [
                    { shape: "Rectangle", domClass: "intermediateCatchEventIcon", x: 10, y: 8, width: 12, height: 16, stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Line", domClass: "intermediateCatchEventIcon", x1: 12, x2: 20, y1: 10, y2: 10, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "intermediateCatchEventIcon", x1: 12, x2: 20, y1: 14, y2: 14, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "intermediateCatchEventIcon", x1: 12, x2: 20, y1: 18, y2: 18, stroke: "black", strokeWidth: 1.2, fill: "none" },
                    { shape: "Line", domClass: "intermediateCatchEventIcon", x1: 12, x2: 20, y1: 22, y2: 22, stroke: "black", strokeWidth: 1.2, fill: "none" }
                ]);

                // 40 x 40
    //            this.addShape("CatchingSignalEventIcon", {
    //                shape: "Polygon", points: "20,10 30,26 10,26", stroke: "black", strokeWidth: 2, fill: "none"
    //            });

                // 32 x 32
                this.addShape("StartSignalEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "16,8 23.5,20 8.5,20", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingSignalEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "16,8 23.5,20 8.5,20", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingSignalEventIcon", {
    //                shape: "Polygon", points: "20,10 30,26 10,26", stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingSignalEventIcon", {
                    shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "16,8 23.5,20 8.5,20", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndSignalEventIcon", {
                    shape: "Polygon", domClass: "endEventIcon", points: "16,8 23.5,20 8.5,20", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 40 x 40
    //            this.addShape("CatchingErrorEventIcon", {
    //                shape: "Polygon", points: "11,30 17,12 22,19 29,11 23,28 18,22", stroke: "black", strokeWidth: 2, fill: "none"
    //            });

                // 32 x 32
                this.addShape("StartErrorEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "7.5,21 13.5,8 17,16 24,10 17,24 13,17", strokeLineJoin: "round", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingErrorEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "7.5,21 13.5,8 17,16 24,10 17,24 13,17", strokeLineJoin: "round", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingErrorEventIcon", {
    //                shape: "Polygon", points: "11,30 17,12 22,19 29,11 23,28 18,22", stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingErrorEventIcon", {
                    shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "7.5,21 13.5,8 17,16 24,10 17,24 13,17", strokeLineJoin: "round", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndErrorEventIcon", {
                    shape: "Polygon", domClass: "endEventIcon", points: "7.5,21 13.5,8 17,16 24,10 17,24 13,17", strokeLineJoin: "round", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 40 x 40
    //            this.addShape("CatchingEscalationEventIcon", {
    //                shape: "Polygon", points: "20,10 28,29 20,21 12,29", stroke: "black", strokeWidth: 2, fill: "none"
    //            });

                // 32 x 32
                this.addShape("StartEscalationEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "16,8 22,24 16,18 10,24", strokeLineJoin: "round", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingEscalationEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "16,8 22,24 16,18 10,24", strokeLineJoin: "round", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingEscalationEventIcon", {
    //                shape: "Polygon", points: "20,10 28,29 20,21 12,29", stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingEscalationEventIcon", {
                    shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "16,8 22,24 16,18 10,24", strokeLineJoin: "round", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndEscalationEventIcon", {
                    shape: "Polygon", domClass: "endEventIcon", points: "16,8 22,24 16,18 10,24", strokeLineJoin: "round", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 40 x 40
    //            this.addShape("CatchingCancelEventIcon", {
    //                shape: "Polygon", points: "10,14 14,10 20,16 26,10 30,14 24,20 30,26 26,30 20,24 14,30 10,26 16,20", stroke: "black", strokeWidth: 2, fill: "none"
    //            });

                // 32 x 32
                this.addShape("StartCancelEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "8,12 12,8 16,12 20,8 24,12 20,16 24,20 20,24 16,20 12,24 8,20 12,16", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingCancelEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "8,12 12,8 16,12 20,8 24,12 20,16 24,20 20,24 16,20 12,24 8,20 12,16", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingCancelEventIcon", {
    //                shape: "Polygon", points: "10,14 14,10 20,16 26,10 30,14 24,20 30,26 26,30 20,24 14,30 10,26 16,20", stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingCancelEventIcon", {
                    shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "8,12 12,8 16,12 20,8 24,12 20,16 24,20 20,24 16,20 12,24 8,20 12,16", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndCancelEventIcon", {
                    shape: "Polygon", domClass: "endEventIcon", points: "8,12 12,8 16,12 20,8 24,12 20,16 24,20 20,24 16,20 12,24 8,20 12,16", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 40 x 40
    //            this.addShape("CatchingCompensationEventIcon", [
    //                { shape: "Polygon", points: "10,20 18,13 18,27", stroke: "black", strokeWidth: 2, fill: "none" },
    //                { shape: "Polygon", points: "20,20 28,13 28,27", stroke: "black", strokeWidth: 2, fill: "none" }
    //            ]);

                // 32 x 32
                this.addShape("StartCompensationEventIcon", [
                    { shape: "Polygon", domClass: "startEventIcon", points: "7,16 14,10 14,22", stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Polygon", domClass: "startEventIcon",points: "15,16 22,10 22,22", stroke: "black", strokeWidth: 1, fill: "white" }
                ]);

                // 32 x 32
                this.addShape("IntermediateCatchingCompensationEventIcon", [
                    { shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "7,16 14,10 14,22", stroke: "black", strokeWidth: 1, fill: "white" },
                    { shape: "Polygon", domClass: "intermediateCatchEventIcon",points: "15,16 22,10 22,22", stroke: "black", strokeWidth: 1, fill: "white" }
                ]);

                // 40 x 40
    //            this.addShape("ThrowingCompensationEventIcon", [
    //                { shape: "Polygon", points: "10,20 18,13 18,27", stroke: "black", strokeWidth: 2, fill: "black" },
    //                { shape: "Polygon", points: "20,20 28,13 28,27", stroke: "black", strokeWidth: 2, fill: "black" }
    //            ]);

                // 32 x 32
                this.addShape("IntermediateThrowingCompensationEventIcon", [
                    { shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "7,16 14,10 14,22", stroke: "none", strokeWidth: 1, fill: "black" },
                    { shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "15,16 22,10 22,22", stroke: "none", strokeWidth: 1, fill: "black" }
                ]);

                // 32 x 32
                this.addShape("EndCompensationEventIcon", [
                    { shape: "Polygon", domClass: "endEventIcon", points: "7,16 14,10 14,22", stroke: "none", strokeWidth: 1, fill: "black" },
                    { shape: "Polygon", domClass: "endEventIcon", points: "15,16 22,10 22,22", stroke: "none", strokeWidth: 1, fill: "black" }
                ]);

                // 40 x 40
    //            this.addShape("CatchingLinkEventIcon", {
    //                shape: "Polygon", points: "12,16 22,16 22,12 30,20 22,28 22,24 12,24", stroke: "black", strokeWidth: 2, fill: "none"
    //            });

                // 32 x 32
                this.addShape("StartLinkEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "8,13 18,13 18,9 25,16 18,23 18,19 8,19", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingLinkEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "8,13 18,13 18,9 25,16 18,23 18,19 8,19", stroke: "black", strokeWidth: 1, fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingLinkEventIcon", {
    //                shape: "Polygon", points: "12,16 22,16 22,12 30,20 22,28 22,24 12,24", stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingLinkEventIcon", {
                    shape: "Polygon", domClass: "intermediateThrowEventIcon", points: "8,13 18,13 18,9 25,16 18,23 18,19 8,19", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndLinkEventIcon", {
                    shape: "Polygon", domClass: "endEventIcon", points: "8,13 18,13 18,9 25,16 18,23 18,19 8,19", stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 40 x 40
    //            this.addShape("CatchingMultipleEventIcon", {
    //                shape: "Polygon",
    //                points: "19.908,8 31.816,16.253 27.108,29.708 13.408,29.708 8,16.253", strokeWidth: 2, fill: "none", stroke: "black"
    //            });

                // 32 x 32
                this.addShape("StartMultipleEventIcon", {
                    shape: "Polygon",
                    domClass: "startEventIcon",
                    points: "16,7.5 24,15 21,23 11,23 8,15", strokeWidth: 1, stroke: "black", fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingMultipleEventIcon", {
                    shape: "Polygon",
                    domClass: "intermediateCatchEventIcon",
                    points: "16,7.5 24,15 21,23 11,23 8,15", strokeWidth: 1, stroke: "black", fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingMultipleEventIcon", {
    //                shape: "Polygon",
    //                points: "19.908,8 31.816,16.253 27.108,29.708 13.408,29.708 8,16.253", strokeWidth: 2, fill: "black", stroke: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingMultipleEventIcon", {
                    shape: "Polygon",
                    domClass: "intermediateThrowEventIcon",
                    points: "16,7.5 24,15 21,23 11,23 8,15", strokeWidth: 1, fill: "black", stroke: "none"
                });

                // 32 x 32
                this.addShape("EndMultipleEventIcon", {
                    shape: "Polygon",
                    domClass: "endEventIcon",
                    points: "16,7.5 24,15 21,23 11,23 8,15", strokeWidth: 1, fill: "black", stroke: "none"
                });

                // 40 x 40
    //            this.addShape("CatchingParallelMultipleEventIcon", {
    //                shape: "Polygon", points: "10,17 17,17 17,10 23,10 23,17 30,17 30,23 23,23 23,30 17,30 17,23 10,23", strokeWidth: 2, fill: "none", stroke: "black"
    //            });

                // 32 x 32
                this.addShape("StartParallelMultipleEventIcon", {
                    shape: "Polygon", domClass: "startEventIcon", points: "8,14 14,14 14,8 18,8 18,14 24,14 24,18 18,18 18,24 14,24 14,18 8,18", strokeWidth: 1, stroke: "black", fill: "white"
                });

                // 32 x 32
                this.addShape("IntermediateCatchingParallelMultipleEventIcon", {
                    shape: "Polygon", domClass: "intermediateCatchEventIcon", points: "8,14 14,14 14,8 18,8 18,14 24,14 24,18 18,18 18,24 14,24 14,18 8,18", strokeWidth: 1, stroke: "black", fill: "white"
                });

                // 40 x 40
    //            this.addShape("ThrowingTerminateEventIcon", {
    //                shape: "Circle", cx: 20, cy: 20, r: 10, stroke: "black", strokeWidth: 2, fill: "black"
    //            });

                // 32 x 32
                this.addShape("IntermediateThrowingTerminateEventIcon", {
                    shape: "Circle", domClass: "intermediateThrowEventIcon", cx: 16, cy: 16, r: 8, stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 32 x 32
                this.addShape("EndTerminateEventIcon", {
                    shape: "Circle", domClass: "endEventIcon", cx: 16, cy: 16, r: 8, stroke: "none", strokeWidth: 1, fill: "black"
                });

                // 12 x 12
                this.addShape("SequenceLoopIcon", {
                    shape: "Path",
                    path: "M0,2L12,2M12,6L0,6M0,10L12,10",
                    stroke: "black",
                    strokeWidth: 1
                });

                // 12 x 12
                this.addShape("ParallelLoopIcon", {
                    shape: "Path",
                    path: "M2,0L2,12M6,12L6,0M10,0L10,12",
                    stroke: "black",
                    strokeWidth: 1
                });

                // 12 x 12
                this.addShape("LoopIcon", {
                    shape: "Path",
                    path: "M24.083,15.5c-0.009,4.739-3.844,8.574-8.583,8.583c-4.741-0.009-8.577-3.844-8.585-8.583c0.008-4.741,3.844-8.577,8.585-8.585c1.913,0,3.665,0.629,5.09,1.686l-1.782,1.783l8.429,2.256l-2.26-8.427l-1.89,1.89c-2.072-1.677-4.717-2.688-7.587-2.688C8.826,3.418,3.418,8.826,3.416,15.5C3.418,22.175,8.826,27.583,15.5,27.583S27.583,22.175,27.583,15.5H24.083z",
                    transform: "scale(0.5) translate(0,-3)"
                });

                // 12 x 12
                this.addShape("CompensationIcon", [
                    { shape: "Polygon", domClass: "subProcessIconStroke", points: "0,6 5.5,0 5.5,12", stroke: "black", strokeWidth: 1, fill: "none" },
                    { shape: "Polygon", domClass: "subProcessIconStroke", points: "6.5,6 12,0 12,12", stroke: "black", strokeWidth: 1, fill: "none" }
                ]);

                // 12 x 12
                this.addShape("CompositeIcon", {
                    shape: "Path",
                    domClass: "subProcessIconStroke",
                    path: "M0,0L0,12L12,12L12,0ZM2,6L10,6M6,2L6,10",
                    stroke: "black",
                    fill: "white",
                    fillOpacity: 0.01,
                    strokeWidth: 1
                });

                // 12 x 12
                this.addShape("AdHocSubProcessIcon", {
                    shape: "Path",
                    domClass: "subProcessIconStroke",
                    path: "M0,6 Q3,2.5 6,6 T12,6",
                    stroke: "black",
                    strokeWidth: 2,
                    strokeLineCap: "round",
                    fill: "none"
                });

                // 12 x 12, isCollection
                this.addShape("MultipleIcon", {
                    shape: "Path",
                    domClass: "dataCollectionIconStroke",
                    path: "M1,0L1,10M5,10L5,0M9,0L9,10",
                    stroke: "#606060",
                    strokeWidth: 2,
                    fill: "none"
                });

                // 12 x 12
                this.addShape("DataInputIcon", {
                    shape: "Path",
                    domClass: "dataInputIconStroke",
                    path: "M9,1 L17,7 L9,13 L9,10 L1,10 L1,4 L9,4z",
                    stroke: "#606060",
                    fill: "none",
                    strokeWidth: 1
                });

                // 12 x 12
                this.addShape("DataOutputIcon", {
                    shape: "Path",
                    domClass: "dataOutputIcon",
                    path: "M9,1 L17,7 L9,13 L9,10 L1,10 L1,4 L9,4z",
                    stroke: "#606060",
                    strokeWidth: 1,
                    fill: "#606060"
                });

                // 32 x 20
                this.addShape("MessageIcon", {
                    shape: "Group",
                    shapes: [
                        { shape: "Rectangle", stroke: "#C4AA4C", strokeWidth: 1, fill: "#FFFFE0", width: 32, height: 20 },
                        { shape: "Panel", shapes: [
                            { shape: "Polyline", stroke: "#C4AA4C", strokeWidth: 1, points: "0,0 16,10 32,0" }
                        ]}
                    ]
                });
            }
        }
    });
}());
