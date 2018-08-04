/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customercouponservices.daos;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.CatForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;

import java.util.List;
import java.util.Optional;




/**
 * A DAO for {@link AbstractCouponModel}
 */
public interface CustomerCouponDao
{

	/**
	 * Find paged coupons for provided codes.
	 *
	 * @param customer
	 *           provided customer
	 * @param pageableData
	 *           the pageable data
	 * @return the paged list of customer coupons.
	 */
	de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> findCustomerCouponsByCustomer(
			CustomerModel customer, PageableData pageableData);

	/**
	 * Find promotion source rule for given code
	 *
	 * @param code
	 *           the given rule code
	 * @return an Optional of PromotionSourceRuleModel, otherwise an empty Optional
	 */
	Optional<PromotionSourceRuleModel> findPromotionSourceRuleByCode(String code);

	/**
	 * Find promotion source rule by product
	 *
	 * @param productCode
	 *           product code
	 * @return list of Promotion souce rule model
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByProduct(String productCode);

	/**
	 * Find excluded promotion source rule by product
	 *
	 * @param productCode
	 *           product code
	 * @return list of Promotion souce rule model
	 */
	List<PromotionSourceRuleModel> findExclPromotionSourceRuleByProduct(final String productCode);

	/**
	 * Find promotion source rule by category
	 *
	 * @param categoryCode
	 *           category code
	 * @return list of Promotion souce rule model
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByCategory(String categoryCode);

	/**
	 * Find excluded promotion source rule by category
	 *
	 * @param categoryCode
	 *           category code
	 * @return list of Promotion souce rule model
	 */
	List<PromotionSourceRuleModel> findExclPromotionSourceRuleByCategory(String categoryCode);

	/**
	 * Find customer coupon by promotionSourceRule
	 *
	 * @param code
	 *           promotion source rule
	 * @return return customer coupon list
	 */
	List<CustomerCouponModel> findCustomerCouponByPromotionSourceRule(final String code);

	/**
	 * Find promotionSourceRule by coupon code
	 *
	 * @param coupon
	 *           code
	 * @return return promotionSourceRule list
	 */
	List<PromotionSourceRuleModel> findPromotionSourceRuleByCouponCode(final String code);

	/**
	 * Find product by promotion source rule
	 *
	 * @param code
	 *           Promotion souce rule code
	 * @return list of Promotion souce rule model product code
	 */
	List<ProductForPromotionSourceRuleModel> findProductForPromotionSourceRuleByPromotion(String code);

	/**
	 * Find category by promotion source rule
	 *
	 * @param code
	 *           Promotion souce rule code
	 * @return list of Promotion souce rule model
	 */
	List<CatForPromotionSourceRuleModel> findCategoryForPromotionSourceRuleByPromotion(String code);


	/**
	 * Find coupons for provided codes.
	 *
	 * @param customer
	 *           provided customer
	 * @return the list of customer coupons.
	 */
	List<CustomerCouponModel> findEffectiveCustomerCouponsByCustomer(CustomerModel customer);

	/**
	 * find all CustomerCouponForPromotionSourceRule by given PromotionSourceRuleModel
	 *
	 * @param rule
	 *           the given PromotionSourceRuleModel
	 * @return list of CustomerCouponForPromotionSourceRuleModel
	 */
	List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(PromotionSourceRuleModel rule);

	/**
	 * find all CustomerCouponForPromotionSourceRule by given PromotionSourceRuleModel
	 *
	 * @param rule
	 *           the given PromotionSourceRuleModel
	 * @param moduleName
	 *           the specific module name
	 * @return list of CustomerCouponForPromotionSourceRuleModel
	 */
	List<CustomerCouponForPromotionSourceRuleModel> findAllCusCouponForSourceRules(PromotionSourceRuleModel rule,
			String moduleName);

	/**
	 * find all CustomerCouponForPromotionSourceRule by given PromotionSourceRuleModel
	 *
	 * @param couponCode
	 *           String coupon
	 * @param customer
	 *           CustomerModel customer
	 *
	 * @return check this coupon is effective for customer
	 */
	boolean checkCustomerCouponAvailableForCustomer(String couponCode, CustomerModel customer);

	/**
	 * check customer coupon is asigned
	 *
	 * @param couponCode
	 *           String coupon
	 * @param customer
	 *           CustomerModel customer
	 *
	 * @return check this coupon is effective for customer
	 */
	int countAssignedCouponForCustomer(final String couponCode, final CustomerModel customer);

	/**
	 * find all assignable customer coupons can assign to given customer by search text
	 *
	 * @param customer
	 *           the given customer
	 * @param text
	 *           search text
	 * @return List<CustomerCouponModel> list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findAssignableCoupons(CustomerModel customer, String text);

	/**
	 * find assigned coupons by given customer and search text
	 *
	 * @param customer
	 *           the given customer
	 * @param text
	 *           search text
	 * @return List<CustomerCouponModel> list of CustomerCouponModel
	 */
	List<CustomerCouponModel> findAssignedCouponsByCustomer(CustomerModel customer, String text);

	/**
	 * Find paginated customer coupons by specific customer.
	 *
	 * @param customer
	 *           the specific
	 * @param searchPageData
	 *           the search page data
	 * @return search page data of CustomerCouponModel
	 */
	SearchPageData<CustomerCouponModel> findPaginatedCouponsByCustomer(CustomerModel customer,
			final SearchPageData searchPageData);
}
