<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-account/subscription/" var="subscriptionDetailsUrl" htmlEscape="false"/>
<c:url value="/${subscriptionData.productUrl}" var="productUrl"/>
<div class="account-section-header">
	<spring:theme code="text.account.subscription" text="Subscriptions"/>
</div>
<c:if test="${empty subscriptions}">
	<div class="account-section-content content-empty">
		<spring:theme code="text.account.subscriptions.noSubscriptions" text="You have no subscriptions"/>
	</div>
</c:if>
<c:if test="${not empty subscriptions}">
	<div class="account-section-content	">
		<div class="account-orderhistory">
            <div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th style="padding-left:50px;width: 245px;"><spring:theme code="text.account.subscription.documentNumber" text="Document Number"/></th>
						<th style="padding-left:32px;"><spring:theme code="text.account.subscription.productName" text="Product Name"/></th>
	                    <th><spring:theme code="text.account.subscription.startDate" text="Start Date"/></th>
	                    <th><spring:theme code="text.account.subscription.endDate" text="End Date"/></th>
	                    <th><spring:theme code="text.account.subscription.status" text="Status"/></th>
	                    <th><spring:theme code="text.account.subscription.actions" text="Actions"/></th>
					</tr>
					<c:forEach items="${subscriptions}" var="subscription">              
						<c:url value="${subscription.productUrl}" var="productUrl"/>
						<tr class="responsive-table-item">
								<td class="responsive-table-cell" style="text-align:center">
                                		${subscription.documentNumber}
								</td>
								<td class="responsive-table-cell">
									<a href="${productUrl}">${subscription.name}</a>
								</td>
																					
								<td class="responsive-table-cell">
									<fmt:formatDate value="${subscription.startDate}" dateStyle="long" timeStyle="short" type="date"/>
								</td>
								
								<td class="responsive-table-cell">
									 <fmt:formatDate value="${subscription.endDate}" dateStyle="long" timeStyle="short" type="date"/>
								</td>
								
								<td class="responsive-table-cell">
									 ${subscription.status}
								</td>
								<td class="responsive-table-cell">
									<a href="${subscriptionDetailsUrl}${subscription.id}" class="responsive-table-link">
										<spring:theme code="text.manage" text="Manage"/>
									</a>
								</td>								
						</tr>
					</c:forEach>
				</table>
            </div>
		</div>		
	</div>
</c:if>


