<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl"
	value="/my-account/coupons?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}" />

<c:set var="noBorder" value="" />
<c:set var="timePattern" value="yyyy.MM.dd HH:mm" />

<c:if test="${not empty searchPageData.results}">
	<c:set var="noBorder" value="no-border" />
</c:if>

<div class="account-section-header">
	<spring:theme code="text.coupons.page.headline" />
</div>
<c:if test="${empty searchPageData.results}">
	<div class="account-section-content content-empty">
		<spring:theme code="text.coupons.nocoupons" />
	</div>
</c:if>

<c:if test="${not empty searchPageData.results}">
	<div class="account-section-content	">
		<div class="account-mycoupons-pagination">
			<nav:pagination top="true" msgKey="text.coupons.page"
				showCurrentPageInfo="true" hideRefineButton="true"
				supportShowPaged="${isShowPageAllowed}"
				supportShowAll="${isShowAllAllowed}"
				searchPageData="${searchPageData}" searchUrl="${searchUrl}"
				numberPagesShown="${numberPagesShown}" />
		</div>
		<div class="coupon-cards">
			<div class="row">
				<c:forEach items="${searchPageData.results}" var="coupon"
					varStatus="loop">
					<div class="col-xs-12 col-sm-6 col-md-4 coupon-panel">
						<div class="col-md-12 coupon-card">
							<div class="col-md-12 coupon-status">
								<span class="coupon-${coupon.status}"><spring:theme code="coupon.status.${coupon.status}" /></span>
							</div>
							<div class="row col-md-12 coupon-row-boundary">
									<div class="col-md-8 col-xs-8 col-sm-8">
										<div class="coupon-title pull-left">
											<b><c:out value="${coupon.name}" /></b>
										</div>
									</div>
									<div class="col-md-4 col-xs-4 col-sm-4 coupon-notify" >
										<div class="pull-right">
											<div class="coupon-noti"><span><spring:theme code="text.coupons.page.notify" /><br></span></div>
										</div>
									</div>
							</div>
							<div class="row col-md-12 coupon-row-boundary">
								<div class="col-md-12">
									<div class="pull-right">
										<label class="comm-switch">
											<input class="comm-switch-checkbox" type="checkbox" notification="${coupon.notificationOn}" 
												data-coupon-code="${coupon.couponCode}" name="my-checkbox" <c:if test='${coupon.notificationOn}'>checked</c:if>/>
											<span class="comm-switch-label" ></span> <span class="comm-switch-silder"></span> 
										</label>
									</div>
								</div>
							</div>
							<div class="row col-md-12 coupon-row-boundary">
								<div class="coupon-main">
									<div class="col-md-12 coupon-date">
										<spring:theme code="text.coupons.page.effective.date" />
									</div>
									<div class="col-md-12 coupon-date">
										<p>
											<fmt:formatDate value="${coupon.startDate}" pattern="${timePattern}" />
											&nbsp;-&nbsp;
											<fmt:formatDate value="${coupon.endDate}" pattern="${timePattern}" />
										</p>
									</div>
									<div class="col-md-12 coupon-detail">
										<span class="coupon-detail-text"> <c:out value="${coupon.description}" />
										</span>
									</div>
									<div class="col-md-12 coupon-read-more">
										<span class=" coupon-read-more-link">
										    <a href="javascript:void(0);" class="js-coupon-read-more" data-index="${loop.index}" data-colorbox-title="<spring:theme code="text.coupons.colorboxtitle" />">
										        <spring:theme code="text.coupons.readmore" />
										    </a>
										</span>
									</div>
								</div>
							</div>
							<div class="row col-md-12 coupon-row-boundary">
								<div class="col-md-12 coupon-find-product-btn">
										<c:choose>
											<c:when test="${coupon.bindingAnyProduct}">
												<c:set var="findProductsUrl"
													value="c/${coupon.solrRootCategory}?q=%3Arelevance&text=#" />
											</c:when>
											<c:otherwise>
												<c:set var="findProductsUrl"
													value="c/${coupon.solrRootCategory}?q=%3Arelevance%3AcustomerCouponCode%3A${coupon.couponCode}&text=#" />
											</c:otherwise>
										</c:choose>
										<a class="btn btn-primary btn-block" href="${findProductsUrl}"><spring:theme code="coupon.FindProducts" /></a>
								</div>
							</div>
							<div id="coupon-popup-content-${loop.index}"
								class="coupon-popup-content">
								<div class="col-md-12 coupon-card-popup">
									<div class="col-md-6 col-sm-6 col-xs-6 coupon-status">
										<span class="coupon-${coupon.status}"> <spring:theme
												code="coupon.status.${coupon.status}" />
										</span>
									</div>
									<div class="col-md-6 col-sm-6 col-xs-6 coupon-bell"></div>
									<div class="col-md-12 coupon-main-popup">
										<div class="col-md-12 coupon-title-popup">
											<b><c:out value="${coupon.name}" /></b>
										</div>
										<div class="col-md-12 coupon-date">
											<spring:theme code="text.coupons.page.effective.date" />
										</div>
										<div class="col-md-12 coupon-date">
											<p>
												<fmt:formatDate value="${coupon.startDate}"
													pattern="${timePattern}" />
												&nbsp;-&nbsp;
												<fmt:formatDate value="${coupon.endDate}"
													pattern="${timePattern}" />
											</p>
										</div>
										<div class="col-md-12 coupon-detail-popup">
											<c:out value="${coupon.description}" />
										</div>
									</div>
									<div class="row col-md-12 coupon-find-product-btn-popup">
										<div class="col-md-12">
											<c:set var="findProductsUrl"
												value="c/${coupon.solrRootCategory}?q=%3Arelevance%3AcustomerCouponCode%3A${coupon.couponCode}&text=#" />
											<c:if test="${coupon.bindingAnyProduct}">
												<c:set var="findProductsUrl"
													value="c/${coupon.solrRootCategory}?q=%3Arelevance&text=#" />
											</c:if>
											<a class="btn btn-primary btn-block"
												href="${findProductsUrl}"><spring:theme
													code="coupon.FindProducts" /></a>
										</div>
									</div>
								</div>
							</div>

						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		<div class="account-mycoupons-pagination">
			<nav:pagination top="false" msgKey="text.coupons.page"
				showCurrentPageInfo="true" hideRefineButton="true"
				supportShowPaged="${isShowPageAllowed}"
				supportShowAll="${isShowAllAllowed}"
				searchPageData="${searchPageData}" searchUrl="${searchUrl}"
				numberPagesShown="${numberPagesShown}" />
		</div>
	</div>
	<div class="global-alerts">
		<div class="alert alert-dismissable getAccAlert">
			<spring:theme code="text.coupon.notification.tip" htmlEscape="false"/>
		</div>
	</div>
</c:if>


