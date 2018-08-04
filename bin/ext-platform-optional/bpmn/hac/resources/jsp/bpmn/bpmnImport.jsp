<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld"%>
<html>
	<head>
		<script type="text/javascript" src="<c:url value="/static/js/bpmn/bpmnImport.js"/>"></script>
		<title>BPMN Conversion</title>
	</head>
	<body>
		<div class="prepend-top span-17 colborder" id="content" typeAttr-Url="<c:url value="/console/impex/typeAndAttributes" />"
		 allTypes-Url="<c:url value="/console/impex/allTypes" />">
			<button id="toggleSidebarButton">&gt;</button>
			<div class="marginLeft">
				<h2>BPMN <-> Hybris Conversion</h2>
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1">BPMN <-> Hybris content</a></li>
					</ul>
					<div id="tabs-1">
						<c:set var="bpmnImportUrl" >
							<c:url value="/bpmn/convert" />
						</c:set>			
						<form:form method="post" id="contentForm" action="${bpmnImportUrl}" commandName="bpmnImportContent">
							<fieldset>
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
								<form:hidden path="code" value="${bpmnImportContent.code}"/>																							
								<p>
								<input type="submit" value="<spring:message code='transform.bpmn.2.hybris'/>" />
								<c:if test="${not empty bpmnImportContent.result}">
									<input type="submit" id="saveButton" value="Save" onclick="form.action='/bpmn/save';" />								
								</c:if>
								<button id="clearScriptContent" align="right" style="float: right;">Clear content</button>
								</p>
								<div>
									<legend>Source Process XML</legend>
									<div id="textarea-container">
										<textarea id="source" form="contentForm" name="source">${bpmnImportContent.source}</textarea>
									</div>
								</div>
								<div>
									<legend>Result Process XML</legend>
									<textarea id="process" form="contentForm" name="result" style="width: 100%;padding: 0px;"
									 readonly="readonly">${bpmnImportContent.result}</textarea>			
								</div>						
							</fieldset>
						</form:form>
					</div>
				</div>
				<div style="clear: both; margin-bottom: 10px"></div>
				<c:choose>
					<c:when test="${not empty bpmnProcessResult}">
						<c:choose>
							<c:when test="${ bpmnProcessResult.success eq true }">
								<span id="bpmnProcessResult" data-level="notice"
								 data-result="<spring:message code="${ bpmnProcessResult.messageCode }" />"/>
							</c:when>
							<c:otherwise>
								<span id="bpmnProcessResult" data-level="error" data-result=
								"<spring:message code="${ bpmnProcessResult.messageCode }" /> ${bpmnProcessResult.detailMessage}"/>
							</c:otherwise>
						</c:choose>
					</c:when>
				</c:choose>
			</div>
		</div>
		<div class="span-6 last" id="sidebar">
			<div class="prepend-top" id="recent-reviews">
				<h3 class="caps">Page description</h3>
				<div class="box">
					<div class="quiet">
						This page provides ImpEx import functionality. You can import a
						script file or paste a script and validate it before the import.
						<hr />
						<hac:note>
							<strong>Legacy mode</strong>
							<br/>
							Impex import works on Service Layer. If you select this option, then Jalo Layer is used.
						</hac:note>
						<br/>
						<hac:info>
							<strong>Fullscreen mode</strong>
							<br/>
							Press <b>F11</b> when cursor is in the editor to toggle full screen editing.
							<b>Esc</b> can also be used to exit full screen editing.
						</hac:info>
					</div>
				</div>
				<h3 class="caps">See also in the hybris Wiki</h3>
				<div class="box">
					<div class="quiet">
						<ul>
							<li><a href="${wikiImpex}" target="_blank" class="quiet">impex Extension - Technical Guide</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	<div id="tooltip"></div>
	</body>
</html>