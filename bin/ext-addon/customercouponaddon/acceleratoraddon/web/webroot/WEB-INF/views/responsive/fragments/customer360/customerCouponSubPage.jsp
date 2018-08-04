<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />


<c:choose>
	<c:when test="${empty customerCoupons}">
		<div class="asm__customer360-noCoupons">
			<spring:theme code="text.asm.${type}.nocustomercoupons"/>
		</div>
	</c:when>
	<c:otherwise>
		<c:forEach items="${customerCoupons}" var="coupon">
			<div class="asm-customer360-promotions-item clearfix">
				<div class="asm-customer360-promotions-item-title">
					<spring:theme code="text.asm.customercoupon.idname" arguments="${coupon.name},${coupon.couponCode}" argumentSeparator=","/>
				</div>
				<div id="${coupon.couponCode}show">
					<spring:url value="/asm/customer-coupon/${ycommerce:encodeUrl(coupon.couponCode)}" var="actionUrl" htmlEscape="false"/>
					<a href="javascript:;" id="${coupon.couponCode}" class="asm-customer360-promotions-addToCart js-coupons-action" 
							data-action="${action}" data-url="${actionUrl}">
						<spring:theme code="text.asm.customercoupon.${action}"/>
					</a>
				</div>
				<div class="asm-customer360-promotions-item-desc">
					<spring:theme code="${coupon.description}"/>
				</div>
			</div>
		</c:forEach>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	ACC.customer360.handleCouponAction();
</script>
