<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %> 

<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty vendorData}">
	<div class="vendorLogo">
		<c:forEach items="${vendorData.logo}" var="media">
			<c:choose>
				<c:when test="${empty imagerData}">
					<c:set var="imagerData">"${media.width}":"${media.url}"</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="imagerData">${imagerData},"${media.width}":"${media.url}"</c:set>
				</c:otherwise>
			</c:choose>
			<c:if test="${empty altText}">
				<c:set var="altText" value="${fn:escapeXml(media.altText)}" />
			</c:if>
		</c:forEach>

		<c:choose>
			<c:when test="${empty imagerData}">
				<spring:theme code="img.missingProductImage.responsive.product"
					text="/" var="imagePath" />
				<c:choose>
					<c:when test="${originalContextPath ne null}">
						<c:url value="${imagePath}" var="imageUrl" context="${originalContextPath}" />
					</c:when>
					<c:otherwise>
						<c:url value="${imagePath}" var="imageUrl" />
					</c:otherwise>
				</c:choose>
				<img width="140" height="140" src="${imageUrl}"/>
			</c:when>
			<c:otherwise>
				<img width="140" height="140" class="js-responsive-image"
				data-media='{${imagerData}}' alt='${altText}' title='${altText}'
				style="">	
			</c:otherwise>
		</c:choose>

		<div class="rating row detailAverage">
			<div class="col-xs-5 col-md-5"><span><spring:theme code="text.vendor.review.overall" text="Overall&nbsp;" /></span></div>	
			<div class="rating-stars pull-left js-ratingCalc"
				data-rating='{"rating":${vendorData.rating.average},"total":5}'>
				<div class="greyStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star"></span>
					</c:forEach>
				</div>
				<div class="greenStars js-greenStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star active"></span>
					</c:forEach>
				</div>
			</div>
			<div class="col-xs-2 col-md-2">
				<span>(${vendorData.rating.reviewCount})</span>
			</div>		
		</div>
		<div class="rating row detailRating" >
			<div class="col-xs-5 col-md-5"><span><spring:theme code="text.vendor.review.satisfaction" text="Satisfaction&nbsp;" /></span></div>		
			<div class="rating-stars pull-left js-ratingCalc"
				data-rating='{"rating":${vendorData.rating.satisfaction},"total":5}'>
				<div class="greyStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star"></span>
					</c:forEach>
				</div>
				<div class="greenStars js-greenStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star active"></span>
					</c:forEach>
				</div>
			</div>		
		</div>
		<div class="rating row detailRating" >
			<div class="col-xs-5 col-md-5"><span><spring:theme code="text.vendor.review.delivery" text="Delivery&nbsp;" /></span></div>		
			<div class="rating-stars pull-left js-ratingCalc"
				data-rating='{"rating":${vendorData.rating.delivery},"total":5}'>
				<div class="greyStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star"></span>
					</c:forEach>
				</div>
				<div class="greenStars js-greenStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star active"></span>
					</c:forEach>
				</div>
			</div>			
		</div>
		<div class="rating row detailRating" >
			<div class="col-xs-5 col-md-5"><span><spring:theme code="text.vendor.review.communication" text="Communication&nbsp;" /></span></div>				
			<div class="rating-stars pull-left js-ratingCalc"
				data-rating='{"rating":${vendorData.rating.communication},"total":5}'>
				<div class="greyStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star"></span>
					</c:forEach>
				</div>
				<div class="greenStars js-greenStars">
					<c:forEach begin="1" end="5">
						<span class="glyphicon glyphicon-star active"></span>
					</c:forEach>
				</div>
			</div>	
		</div>
		
		<div class="show-reviews"> 
			<a id="show-reviews" href="<spring:url value='/v/${ycommerce:encodeUrl(vendorData.code)}/reviews'/>">
				<spring:theme code="text.vendor.reviews.show" text="Show Reviews" />
			</a>
		</div>
	</div>	
</c:if>