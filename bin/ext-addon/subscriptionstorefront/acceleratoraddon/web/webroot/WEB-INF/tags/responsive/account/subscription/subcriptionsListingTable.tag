<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="subscriptions" required="true"
				  type="java.util.List<de.hybris.platform.subscriptionfacades.data.SubscriptionData>" %>
<%@ attribute name="paymentInfoMap" required="false"
				  type="java.util.Map<java.lang.String, de.hybris.platform.commercefacades.order.data.CCPaymentInfoData>" %>
<%@ attribute name="displayActions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="displaySelectAllOption" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="structure" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/account/structure" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/account/subscription/action" %>
<%@ taglib prefix="subscription" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/account/subscription" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<spring:url var="changeSubscriptionStateUrl" value="/my-account/subscription/change-state" htmlEscape="false"/>

<table class="subscriptions-table">
	<tr class="responsive-table-head">
		<th><spring:theme code="text.account.subscriptions.productName" text="Product Name"/></th>
		<th><spring:theme code="text.account.subscriptions.startDate" text="Start Date"/></th>
		<th><spring:theme code="text.account.subscriptions.endDate" text="End Date"/></th>
		<th><spring:theme code="text.account.subscriptions.status" text="Status"/></th>
		<c:if test="${not empty paymentInfoMap}">
			<th><spring:theme code="text.account.subscriptions.paymentDetails" text="Payment Details"/></th>
		</c:if>
		<c:if test="${displayActions}">
			<th><spring:theme code="text.account.orderHistory.actions" text="Actions"/></th>
		</c:if>
		<c:if test="${displaySelectAllOption}">
			<th>
				<input id="selectall" title="selectall" type="checkbox"/>
				<spring:theme code="text.account.subscription.selectAll" text="Select All"/>
			</th>
		</c:if>
	</tr>
	<c:forEach items="${subscriptions}" var="subscription">
		<subscription:subscriptionListingTableItem subscription="${subscription}" paymentInfoMap="${paymentInfoMap}"
																 displayActions="${displayActions}"
																 displaySelectAllOption="${displaySelectAllOption}"/>
	</c:forEach>
</table>
