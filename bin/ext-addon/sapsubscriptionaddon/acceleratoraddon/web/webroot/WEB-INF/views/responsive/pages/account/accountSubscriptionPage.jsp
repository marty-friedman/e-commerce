<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="productaddon"
	tagdir="/WEB-INF/tags/addons/sapsubscriptionaddon/responsive/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<spring:htmlEscape defaultHtmlEscape="true"/>
<c:url value="/${subscriptionData.productUrl}" var="productUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/cancel" var="cancelSubscriptionUrl"/>
<c:url value="/my-account/subscription/${subscriptionData.id}/extend" var="extendSubscriptionUrl"/>
<spring:url value="/my-account/subscriptions" var="subscriptionUrl" />

<common:headline url="${subscriptionUrl}" labelKey="text.account.subscription.detail"/>

<form:form id="subscriptionCancellationForm" name="subscriptionCancellationForm" action="${fn:escapeXml(cancelSubscriptionUrl)}" method="post" commandName="subscriptionCancellationForm">
	<div class="account-section-content	">
		<div class="account-orderhistory">
			<div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr	class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<!-- <th></th> -->
						<th><spring:theme code="text.account.subscription.productName" text="Product Name"/></th>
						<th><spring:theme code="text.account.subscription.price" text="Price"/></th>
						<th><spring:theme code="text.account.subscription.status" text="Status"/></th>
						<th><spring:theme code="text.account.subscription.startDate" text="Start Date"/></th>
						<th><spring:theme code="text.account.subscription.endDate" text="End Date"/></th>
						<th></th>
					</tr>
					<tr class="responsive-table-item">
						<td class="responsive-table-cell"><a href="${productUrl}">${subscriptionData.name}</a></td>
						<td class="responsive-table-cell">
								<c:forEach items="${subscriptionData.pricePlan.recurringChargeEntries}"	var="recurringChargeEntry">
										<format:fromPrice priceData="${recurringChargeEntry.price}"/>
										<div class="item__code">${subscriptionData.billingFrequency}</div>
								</c:forEach><br>								
								<c:forEach items="${subscriptionData.pricePlan.oneTimeChargeEntries}" var="oneTimeChargeEntry">
										<c:if test="${oneTimeChargeEntry.billingTime.code eq 'paynow'}">
											<format:fromPrice priceData="${oneTimeChargeEntry.price}"/>								
										    <div class="item__code">${oneTimeChargeEntry.billingTime.name}</div>
									    </c:if>
								</c:forEach>
						</td>
						<td class="responsive-table-cell"><spring:theme code="text.account.subscriptions.status.${subscriptionData.status}" /></td>
						<td class="responsive-table-cell"><fmt:formatDate value="${subscriptionData.startDate}" dateStyle="long" timeStyle="short" type="date"/></td>
						<td class="responsive-table-cell">
							<c:if test="${not empty subscriptionData.endDate}">
								<fmt:formatDate	value="${subscriptionData.endDate}" dateStyle="long" timeStyle="short" type="date" />
							</c:if>
						</td>
						<c:set var="rcSubStatus" value="${subscriptionData.status}" />
						<td>
							<c:if test="${subscriptionData.status eq 'ACTIVE' and not empty subscriptionData.validTillDate}">
								<button id="cancelRCSubscription" type="submit" class="btn btn-primary btn-block">
									<spring:theme code="text.account.subscription.cancelSubscription" text="Cancel"/>
								</button>
							</c:if>
							<input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
							<input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
							<c:if test="${not empty subscriptionData.validTillDate}">
								 <input	type="hidden" name="subscriptionEndDate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
							</c:if>
							</td>
					</tr>
				</table>
				<br>				
			</div>
		</div>
	</div>
</form:form>
<c:if test="${subscriptionData.status eq 'ACTIVE' and not empty subscriptionData.validTillDate}">
					
						<form:form id="subscriptionExtensionForm" name="subscriptionExtensionForm" commandName="subscriptionExtensionForm" action="${fn:escapeXml(extendSubscriptionUrl)}" method="post">
							<div class="form" id="extendRCSubscription">
								<div class="form-group" style="display: inline-block; margin: 10px; width: 180px;">Extend Subscription By</div>
								<div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
									<input type="text" placeholder="enter valid number" name="extensionPeriod" id="extensionPeriod" class="form-control"/>
								</div>	
								<div class="form-group" style="display: inline-block; margin: 10px; width: 100px;">${fn:escapeXml(subscriptionData.contractFrequency)}</div>						
								<div class="form-group" style="display: inline-block; margin: 10px; width: 300px;">
									<button id="extendSubscription" type="submit"
										class="btn btn-primary btn-block"
										class="btn btn-primary btn-block">
										<spring:theme code="text.account.subscription.extend" text="Extend Subscription" />
									</button>
									<input type="hidden" name="version"	value="${fn:escapeXml(subscriptionData.version)}"/>
									<input type="hidden" name="ratePlanId" value="${fn:escapeXml(subscriptionData.ratePlanId)}"/>
									<input type="hidden" name="billingFrequency" value="${fn:escapeXml(subscriptionData.contractFrequency)}"/>
									<c:if test="${not empty subscriptionData.validTillDate}">
											<input type="hidden" name="validTilldate" value="${fn:escapeXml(subscriptionData.validTillDate)}" />
									</c:if>
								</div>
								<div class="form-group">
									<input id="rcSubUnlimited" name="unlimited" type="checkbox">
									<label class="control-label notification_preference_channel">
			   							<span>Select for Unlimited Subscription</span>	
				   					</label></div>
							</div>
						</form:form>
				</c:if>
