<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:url value="/my-account/order/cancel/${orderData.code}" var="orderCancelUrl" />

<c:if test="${orderData.status.code != 'CANCELLED' and orderData.paymentStatus.code eq 'NOTPAID'}">
	<div class="label-order">
		<a id="orderCancelButton" href="${orderCancelUrl}" class="payment-action"> 
			<spring:theme code="order.detail.button.pay.cancel" text="CancelOrder" htmlEscape="true"/>
		</a>
	</div>
</c:if>
