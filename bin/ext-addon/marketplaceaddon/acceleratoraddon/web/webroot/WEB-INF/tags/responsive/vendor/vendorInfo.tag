<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="vendor" required="true" type="de.hybris.platform.marketplacefacades.vendor.data.VendorData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url value="${vendor.url}" var="vendorUrl"/>

<c:if test="${not empty vendor}">
	<div>
		<span class="sold-by">
			<spring:theme code="text.store.seller" text="Sold by " />
		</span>
		<span class="text-uppercase">
			<a href="${vendorUrl}">${fn:escapeXml(vendor.name)}</a>
		</span>
	</div>
</c:if>






