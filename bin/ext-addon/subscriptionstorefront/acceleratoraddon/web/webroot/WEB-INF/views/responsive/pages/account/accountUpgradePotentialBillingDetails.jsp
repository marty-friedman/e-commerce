<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="subscription" tagdir="/WEB-INF/tags/addons/subscriptionstorefront/responsive/account" %>

<%--@elvariable id="upgradePreviewData" type="java.util.List<de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData>"--%>

<subscription:viewUpgradeBillingChanges upgradePreviewData="${upgradePreviewData}"/>
