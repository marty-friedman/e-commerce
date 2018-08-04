<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld"%>
<html>
	<head>
		<title>BPMN Editor</title>
		<link rel="stylesheet" href="<c:url value="/static/css/table.css"/>" type="text/css" media="screen, projection" />
		<link rel="stylesheet" href="<c:url value="/static/css/maintain/keys.css"/>" type="text/css" media="screen, projection" />
		<link rel="stylesheet" href="<c:url value="/static/css/bpmnEditor.css"/>" type="text/css" media="screen, projection" />
			
					
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/log4javascript.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/common.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/d3.min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/dagre.min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/gravityCoreClient.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/hammer.min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/jspdf.min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/klay.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/thirdparty/uuid.js"></script>
		
		<script type="text/javascript" src="/static/lib/1.10.0/core-min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/model-min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/common.ui-min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/ui.symbol-min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/ui.editor-min.js"></script>
		<script type="text/javascript" src="/static/lib/1.10.0/galilei.bpmn-min.js"></script>	
		<script type="text/javascript" src="/static/lib/library.js"></script>
		
		<script type="text/javascript" src="/static/lib/properties.js"></script>
		<script type="text/javascript" src="/static/lib/hybrisModel.js"></script>
		<script type="text/javascript" src="/static/lib/hybrisdiagram.js"></script>
		<script type="text/javascript" src="/static/lib/hybriseditor.js"></script>
		<script type="text/javascript" src="/static/lib/jquery.cookie.js"></script>
		<script type="text/javascript" src="/static/lib/jquery-ui.min.js"></script>
		<script type="text/javascript" src="<c:url value="/static/js/bpmn/bpmnEditor.js"/>"></script>
	</head>
	<body>
		<div class="prepend-top span-17 colborder" id="content">
			<div id="mainPage">
				<div id="header">
					<div id="tools">
						<input class="tool" id="pointer" type="image" src="/static/cng/images/pointerCursor.png"
						 alt="Pointer" title="pointer" />
						<input class="tool" id="lasso" type="image" src="/static/cng/images/lasso.png" alt="Lasso" title="lasso"/>
						<input class="tool" id="undo" type="image" src="/static/cng/images/Undo.png" alt="Undo" title="undo"/>
						<input class="tool" id="redo" type="image" src="/static/cng/images/Redo.png" alt="Redo" title="redo"/> 
						<input class="tool" id="delete" type="image" src="/static/cng/images/Delete.png" alt="Delete" title="delete"/> 
						<input class="tool" id="startEventTool" type="image" src="/static/cng/images/StartEvent.svg"
						 alt="Start Event" draggable="false" title="Start Event"/>
						<input class="tool" id="taskTool" type="image" src="/static/cng/images/Task.svg"
						 alt="Task" draggable="false" title="Task"/> 
						<input class="tool" id="gatewayTool" type="image" src="/static/cng/images/Gateway.svg"
						 alt="Gateway" draggable="false" title="Gateway"/> 
						<input class="tool" id="intermediateCatechEventTool" type="image"
						 src="/static/cng/images/IntermediateCatchEvent.png" alt="Intermediate Catch Event" draggable="false"
						  title="Intermediate Catch Event"/>
						<input class="tool" id="endEventTool" type="image" src="/static/cng/images/EndEvent.svg"
						 alt="End Event" draggable="false" title="End Event"/> 
						<input class="tool" id="flowTool" type="image" src="/static/cng/images/Flow.png"
						 alt="Flow" draggable="false" title="Flow"/> 
						<input class="tool" id="dataobjectTool" type="image" src="/static/cng/images/DataObject.png"
						 alt="DataObject" draggable="false" title="DataObject"/> 
						<input class="tool" id="save" type="image" src="/static/cng/images/Save.png" alt="Save" /> 
						<input class="tool" id="pdf" type="image" src="/static/cng/images/pdf.png" alt="PDF" />
						<input class="tool" id="upload" type="file" name="files" style="display: none" /> 
						<img class="tool" id="load" src="/static/cng/images/Load.png" alt="Load" style="cursor: pointer" />
					</div>
				</div>
			
				<!-- Adds a parent node for the diagram editor -->
		    
				<div id="editorArea" class="yw-bpmneditor-workspace">
					<div id="editor"></div>
					<div id="properties" class="yw-bpmneditor-properties">
						<fieldset class="yw-bpmneditor-fieldset">
							<legend class="yw-bpmneditor-legend">General Properties</legend>
						
							<label class="yw-bpmneditor z-label" id="objectLabel" for="object">Object</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="object" id="object"
							 style="margin-bottom: .5em;" disabled/><br>
						
							<label class="yw-bpmneditor z-label" id="processNameLabel"  for="processName">Process Name</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="processName" id="processName"
							 style="margin-bottom: .5em;"/><br>
						
							<label class="yw-bpmneditor z-label" id="processClassNameLabel"
							 for="processClassName">Process Class Name</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="processClassName" id="processClassName"
							 style="margin-bottom: .5em;" disabled/><br>
						
							<label class="yw-bpmneditor z-label" id="nameLabel" for="name">Selected Element Name</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="name" id="name" style="margin-bottom: .5em;"/><br>
						</fieldset>

						<fieldset class="yw-bpmneditor-fieldset">
							<legend class="yw-bpmneditor-legend">Special Properties</legend>
						
							<label class="yw-bpmneditor z-label" id="scriptTypeLabel"
							 for="scriptType" style="display:none;">Script Type</label>
							<input class="yw-bpmneditor z-textbox" type="text" name="scriptType" id="scriptType"
							 style="display:none;margin-bottom: .5em;"/>
						
							<label class="yw-bpmneditor z-label" id="scriptLabel" for="script" style="display:none;">Script Text</label>
							<textarea class="yw-bpmneditor z-textbox ye-rows-height" id="script"
							 style="display:none; vertical-align: middle;"></textarea>
						
							<label class="yw-bpmneditor z-label" id="endEventMessageLabel" for="endEventMessage"
							 style="display:none;">End Event Message</label>
							<textarea class="yw-bpmneditor z-textbox ye-rows-height" id="endEventMessage"
							 style="display:none;"></textarea>
						
							<label class="yw-bpmneditor z-label" id="defaultFlowLabel" for="defaultFlow"
							 style="display:none;">Is Default flow?</label>
							<input class="yw-bpmneditor z-checkbox" type="checkbox" name="defaultFlow" id="defaultFlow"
							 style="display:none;"/>
						
							<p>
								<label class="yw-bpmneditor z-label" id="processClassLabel"
								 for="processClass" style="display:none;">Is Process Class?</label>
								<input class="yw-bpmneditor z-checkbox" type="checkbox" name="processClass" id="processClass"
								 style="display:none;"/>
							</p>
							<p>
								<label class="yw-bpmneditor z-label" id="dataObjectRequiredLabel"
								 for="dataObjectRequired" style="display:none;">Is Context Parameter Mandatory?</label>
								<input class="yw-bpmneditor z-checkbox" type="checkbox" name="dataObjectRequired" id="dataObjectRequired"
								 style="display:none;"/>
							</p>
							<p>
								<label class="yw-bpmneditor z-label" id="dataObjectTypeLabel"
								 for="dataObjectType" style="display:none;">Context Parameter Type</label>
								<input class="yw-bpmneditor z-textbox" type="text" name="dataObjectType" id="dataObjectType"
								 style="display:none;"/>
							</p>
							<label class="yw-bpmneditor z-label" id="timerYearsLabel"
							 for="timerYears" style="display:none;">years</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerYears" id="timerYears"
							 style="display:none; margin-bottom: .5em;"/><br>
							<label class="yw-bpmneditor z-label" id="timerMonthsLabel"
							 for="timerMonths" style="display:none;">months</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerMonths" id="timerMonths"
							 style="display:none; margin-bottom: .5em;"/><br>
							<label class="yw-bpmneditor z-label" id="timerDaysLabel"
							 for="timerDays" style="display:none">days</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerDays" id="timerDays"
							 style="display:none; margin-bottom: .5em;"/><br>
						
							<label class="yw-bpmneditor z-label" id="timerHoursLabel"
							 for="timerHours" style="display:none">hours</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerHours" id="timerHours"
							 style="display:none; margin-bottom: .5em;"/><br>
							<label class="yw-bpmneditor z-label" id="timerMinsLabel"
							 for="timerMins" style="display:none">mins</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerMins" id="timerMins"
							 style="display:none; margin-bottom: .5em"/><br>
							<label class="yw-bpmneditor z-label" id="timerSecLabel"
							 for="timerSec" style="display:none;">sec</label><br>
							<input class="yw-bpmneditor z-textbox" type="text" name="timerSec" id="timerSec"
							 style="display:none; margin-bottom: .5em;"/><br>
						</fieldset>
					</div>
				</div>
			</div>
		</div>
		<span id="result" data-success="<spring:message code='bpmn.editor.hybris.success' javaScriptEscape='true' />"
		 data-error="<spring:message code='bpmn.editor.hybris.error' javaScriptEscape='true' />" />
	</body>
</html>

