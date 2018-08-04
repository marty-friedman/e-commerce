<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<span class="well-headline-sub"> 
	<spring:theme code="text.consignment.fulfilled.by" htmlEscape="true"/> 
	<a class="link-vendor" href="<spring:url value='/v/${ycommerce:encodeUrl(consignment.vendor.code)}'/>">${fn:escapeXml(consignment.vendor.name)}</a>
</span>
