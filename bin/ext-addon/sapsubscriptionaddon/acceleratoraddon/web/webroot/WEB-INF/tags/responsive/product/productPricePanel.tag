<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="isOrderForm" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${empty product.volumePrices}">
		<c:choose>
			<c:when test="${(not empty product.priceRange) and (product.priceRange.minPrice.value ne product.priceRange.maxPrice.value) and ((empty product.baseProduct) or (not empty isOrderForm and isOrderForm))}">
				<span>
					<format:price priceData="${product.priceRange.minPrice}"/>
				</span>
				<span>
					-
				</span>
				<span>
					<format:price priceData="${product.priceRange.maxPrice}"/>
				</span>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${not empty product.subscriptionTerm}">
						<c:forEach items="${product.price.recurringChargeEntries}" var="recurringChargeEntry">
							<p class="price">
								<format:fromPrice priceData="${recurringChargeEntry.price}"/>
							</p>
							<div class="item__code">${product.subscriptionTerm.billingPlan.billingTime.name}</div>
						
                        </c:forEach>
                        <c:forEach items="${product.price.oneTimeChargeEntries}" var="oneTimeChargeEntry">
						<c:if test="${oneTimeChargeEntry.billingTime ne null and oneTimeChargeEntry.billingTime.code eq 'paynow'}">
							<p class="price">
								<format:fromPrice priceData="${oneTimeChargeEntry.price}"/>
							</p>
							<div class="item__code">${oneTimeChargeEntry.billingTime.name}</div>
						</c:if>				
                        </c:forEach>
                    </c:when>
					<c:otherwise>
						<p class="price">
							<format:fromPrice priceData="${product.price}" />
						</p>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<table class="volume__prices" cellpadding="0" cellspacing="0" border="0">
			<thead>
			<th class="volume__prices-quantity"><spring:theme code="product.volumePrices.column.qa"/></th>
			<th class="volume__price-amount"><spring:theme code="product.volumePrices.column.price"/></th>
			</thead>
			<tbody>
			<c:forEach var="volPrice" items="${product.volumePrices}">
				<tr>
					<td class="volume__price-quantity">
						<c:choose>
							<c:when test="${empty volPrice.maxQuantity}">
								${volPrice.minQuantity}+
							</c:when>
							<c:otherwise>
								${volPrice.minQuantity}-${volPrice.maxQuantity}
							</c:otherwise>
						</c:choose>
					</td>
					<td class="volume__price-amount text-right">${fn:escapeXml(volPrice.formattedValue)}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>