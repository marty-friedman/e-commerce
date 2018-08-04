<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="hac" uri="/WEB-INF/custom.tld"%>
<html>
	<head>
		<script type="text/javascript" src="<c:url value="/static/js/bpmn/bpmnExport.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/static/js/FileSaver/FileSaver.min.js"/>"></script>
		<title>BPMN EXPORT</title>
	</head>
	<body>
		<div class="prepend-top span-17 colborder" id="content">
			<button id="toggleSidebarButton">&gt;</button>
			<div class="marginLeft marginBottom">
			<h2>BPMN EXPORT</h2>
			<div id="tabs">
				<ul>
					<li><a href="#tabs-1">BPMN Export</a></li>
				</ul>
				<div id="tabs-1">
					<c:set var="fileExportUrl">
						<c:url value="/bpmn/export" />
					</c:set>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					<dl>
						<dt>Process Code</dt>
						<dd>
							<input type="text" name="processName" id="processCode" class="wide" value=""
							 placeholder="Enter process code ..." />
						</dd>
					</dl>
					<input type="button" value="Download" id="download" />
				</div>
			</div>
		</div>
		<span id="result" data-success="<spring:message code='bpmn.export.success' javaScriptEscape='true' />"
		 data-error="<spring:message code='bpmn.export.error' javaScriptEscape='true' />" />
	</body>
</html>

