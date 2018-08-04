<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="subscription" required="true" type="de.hybris.platform.subscriptionfacades.data.SubscriptionData" %>
<%@ attribute name="subscriptionProduct" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="upgradable" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isNotCancelledSubscription" required="true" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/product" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<h3 class="subsciptions-section-title"><spring:theme code="text.account.subscription.detail" text="Subscription detail"/></h3>
<table class="subscriptions-table">
	<tr>
		<td><spring:theme code="text.account.subscription.billingFrequency" text="Billing Frequency:"/></td>
		<td>${ycommerce:encodeHTML(subscription.billingFrequency)}</td>
	</tr>
	<tr>
		<td><spring:theme code="product.list.viewplans.price.description.header" text="Price:"/></td>
		<td><product:subscriptionPricesLister subscriptionData="${subscriptionProduct}"/></td>
	</tr>
	<tr>
		<td><spring:theme code="text.account.subscription.contractDuration" text="Contract Duration:"/></td>
		<td>
			${ycommerce:encodeHTML(subscription.contractDuration)}&nbsp;
			${ycommerce:encodeHTML(subscription.contractFrequency)}
		</td>
	</tr>
	<tr>
		<td><spring:theme code="text.account.subscription.cancellable" text="Cancellable:"/></td>
		<td>
			<c:choose>
				<c:when test="${subscription.cancellable}">
					<spring:theme code="text.account.subscription.cancellable.yes" text="yes"/>
				</c:when>
				<c:otherwise>
					<spring:theme code="text.account.subscription.cancellable.no" text="no"/>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<c:if test="${not empty subscriptionProduct.entitlements}">
		<tr>
			<td><spring:theme code="product.list.viewplans.entitlements.header" text="Included:"/></td>
			<td><product:entitlementLister subscriptionData="${subscriptionProduct}" showEntitlementQuantity="true"/></td>
		</tr>
		<tr>
			<td><spring:theme code="product.list.viewplans.usage.charges.description.header" text="Usage Charges:"/></td>
			<td><product:usageChargesLister subscriptionData="${subscriptionProduct}"/></td>
		</tr>
	</c:if>
</table>
