<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="subscription" required="true" type="de.hybris.platform.subscriptionfacades.data.SubscriptionData" %>
<%@ attribute name="paymentInfoMap" required="false"
				  type="java.util.Map<java.lang.String, de.hybris.platform.commercefacades.order.data.CCPaymentInfoData>" %>
<%@ attribute name="displayActions" required="false" type="java.lang.Boolean" %>
<%@ attribute name="displaySelectAllOption" required="false" type="java.lang.Boolean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="structure" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/account/structure" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<c:set var="subscriptionId" value="${ycommerce:encodeHTML(subscription.id)}"/>
<c:set var="subscriptionStatus" value="${fn:toUpperCase(subscription.subscriptionStatus)}"/>
<c:set var="isPausedSubscription" value="${subscriptionStatus eq 'PAUSED'}"/>
<c:set var="isActiveSubscription" value="${not isPausedSubscription and subscriptionStatus ne 'CANCELLED'}"/>

<spring:url var="subscriptionDetailsUrl" value="/my-account/subscription/{/subscriptionId}"
				htmlEscape="false">
	<spring:param name="subscriptionId" value="${subscriptionId}"/>
</spring:url>
<spring:url var="productDetailsUrl" value="${subscription.productUrl}" htmlEscape="false"/>
<spring:url var="upgradeSubscriptionComparisionUrl"
				value="/my-account/subscription/upgrades-comparison?subscriptionId=${subscriptionId}"
				htmlEscape="false"/>

<tr>
	<structure:hiddenTitleCell titleCode="text.account.subscriptions.productName" titleDefaultText="Product Name">
		<a href="${productDetailsUrl}">${ycommerce:encodeHTML(subscription.name)}</a>
	</structure:hiddenTitleCell>
	<structure:hiddenTitleCell titleCode="text.account.subscriptions.startDate" titleDefaultText="Start Date">
		<fmt:formatDate value="${subscription.startDate}" dateStyle="long" timeStyle="short" type="date"/>
	</structure:hiddenTitleCell>
	<structure:hiddenTitleCell titleCode="text.account.subscriptions.endDate" titleDefaultText="End Date">
		<fmt:formatDate value="${subscription.endDate}" dateStyle="long" timeStyle="short" type="date"/>
	</structure:hiddenTitleCell>
	<structure:hiddenTitleCell titleCode="text.account.subscriptions.status" titleDefaultText="Status">
		${ycommerce:encodeHTML(subscriptionStatus)}
	</structure:hiddenTitleCell>
	<c:if test="${not empty paymentInfoMap[subscription.paymentMethodId]}">
		<structure:hiddenTitleCell titleCode="text.account.subscriptions.paymentDetails" titleDefaultText="Payment Details">
			<c:set value="${paymentInfoMap[subscription.paymentMethodId]}" var="paymentMethod"/>
			<fmt:formatNumber var="formattedExpiryMonth" type="number" minIntegerDigits="2" value="${paymentMethod.expiryMonth}"/>
			<spring:theme code="text.account.subscriptions.payment.details"
							  arguments="${fn:substring(paymentMethod.cardTypeData.name,0,4)},
														  ${fn:replace(paymentMethod.cardNumber,'*','')},
														  ${formattedExpiryMonth},
														  ${paymentMethod.expiryYear}" htmlEscape="false"/>
		</structure:hiddenTitleCell>
	</c:if>
	<c:if test="${displayActions}">
		<structure:hiddenTitleCell titleCode="text.account.orderHistory.actions" titleDefaultText="Actions">
			<a href="${subscriptionDetailsUrl}" class="manage-link">
				<spring:theme code="text.manage" text="Manage"/>
			</a>
			<c:if test="${isActiveSubscription and
												not empty subscriptionFacade.getUpsellingOptionsForSubscription(subscription.productCode)}">
				<a href="${upgradeSubscriptionComparisionUrl}" class="upgrade-link">
					<spring:theme code="text.account.subscription.upgradeSubscription" text="Upgrade Subscription"/>
				</a>
			</c:if>
			<c:if test="${isPausedSubscription}">
				<spring:theme var="changeSubscriptionStateButtonText" code="text.account.subscription.resumeSubscription"
								  text="Resume Subscription"/>
				<spring:url var="changeSubscriptionStateUrl" value="/my-account/subscription/change-state" htmlEscape="false"/>
				<form:form id="resume-subscription-form" action="${changeSubscriptionStateUrl}" method="post">
					<button type="submit" class="btn btn-primary" title="${changeSubscriptionStateButtonText}">${changeSubscriptionStateButtonText}</button>
					<input type="hidden" name="newState" value="ACTIVE"/>
					<input type="hidden" name="subscriptionId" value="${subscriptionId}"/>
				</form:form>																
			</c:if>
		</structure:hiddenTitleCell>
	</c:if>
	<c:if test="${displaySelectAllOption}">
		<structure:hiddenTitleCell titleCode="text.account.subscription.select" titleDefaultText="Select">
			<form:checkbox path="subscriptionsToChange" value="${subscriptionId}"/>
		</structure:hiddenTitleCell>
	</c:if>
</tr>
