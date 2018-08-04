/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.recommendationwebservices.validators;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.model.CMSSAPOfferRecoComponentModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;

public class OfferRecommendationComponentValidator implements Validator<CMSItemModel> {

	public static final String RECO_TYPE_REQUIRED_ERROR_MSG = "recommendation.type.required";
	public static final String CONTENT_POSITION_REQUIRED_ERROR_MSG = "recommendation.contentposition.required";
	public static final String LEADING_ITEM_TYPE_REQUIRED_ERROR_MSG = "recommendation.leadingitemtype.required";
	public static final String LEADING_ITEM_DS_TYPE_REQUIRED_ERROR_MSG = "recommendation.leadingitemdstype.required";
	public static final String CART_ITEM_DS_TYPE_REQUIRED_ERROR_MSG = "recommendation.cartitemdstype.required";
	
	private ValidationErrorsProvider validationErrorsProvider;

	/**
	 * 
	 * This method validates the fields of the UI Component.
	 * 
	 */
	@Override
	public void validate(final CMSItemModel itemModel) 
	{
		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPOfferRecoComponentModel.RECOTYPE))) {
			addValidatorRule(CMSSAPOfferRecoComponentModel.RECOTYPE, RECO_TYPE_REQUIRED_ERROR_MSG);
		}

		if (itemModel.getProperty(CMSSAPOfferRecoComponentModel.CONTENTPOSITION) == null) {
			addValidatorRule(CMSSAPOfferRecoComponentModel.CONTENTPOSITION,
					CONTENT_POSITION_REQUIRED_ERROR_MSG);
		}
		
		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPOfferRecoComponentModel.LEADINGITEMTYPE))) {
			addValidatorRule(CMSSAPOfferRecoComponentModel.LEADINGITEMTYPE,
					LEADING_ITEM_TYPE_REQUIRED_ERROR_MSG);
		}

		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPOfferRecoComponentModel.LEADINGITEMDSTYPE))) {
			addValidatorRule(CMSSAPOfferRecoComponentModel.LEADINGITEMDSTYPE,
					LEADING_ITEM_DS_TYPE_REQUIRED_ERROR_MSG);
		}

		if (StringUtils.isEmpty(itemModel.getProperty(CMSSAPOfferRecoComponentModel.CARTITEMDSTYPE))) {
			addValidatorRule(CMSSAPOfferRecoComponentModel.CARTITEMDSTYPE,
					CART_ITEM_DS_TYPE_REQUIRED_ERROR_MSG);
		}	
	}

	/**
	 * Creates a validator rule that will output an error message in red below the respected field
	 */
	public void addValidatorRule(String targetField, String errorMsgCode) {

		getValidationErrorsProvider().getCurrentValidationErrors()
				.add(newValidationErrorBuilder() //
						.field(targetField) //
						.errorCode(errorMsgCode) //
						.build());
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider) {
		this.validationErrorsProvider = validationErrorsProvider;
	}

	public ValidationErrorsProvider getValidationErrorsProvider() {
		return this.validationErrorsProvider;
	}
}