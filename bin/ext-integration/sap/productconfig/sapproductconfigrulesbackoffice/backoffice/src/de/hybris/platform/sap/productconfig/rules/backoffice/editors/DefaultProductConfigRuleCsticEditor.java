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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hybris.cockpitng.editors.EditorContext;


/**
 * Default implementation of the characteristic editor in the product configuration rules
 */
public class DefaultProductConfigRuleCsticEditor extends AbstractProductConfigRuleParameterEditor
{
	@Override
	protected List<Object> getPossibleValues(final EditorContext<Object> context)
	{
		List<Object> values;

		final Map<String, Object> parameters = context.getParameters();

		final List<String> productCodeList = (List<String>) parameters
				.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST);
		if (productCodeList != null)
		{
			values = getPossibleValuesForActionParameter(productCodeList);
		}
		else
		{
			final ProductModel productModel = (ProductModel) parameters
					.get(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT);
			values = getPossibleValuesForConditionParameter(productModel);
		}

		Collections.sort(values, (final Object v1, final Object v2) -> v1.toString().compareTo(v2.toString()));

		return values;
	}

	protected List<Object> getPossibleValuesForConditionParameter(final ProductModel productModel)
	{
		final List<Object> values = new ArrayList<>();

		if (productModel != null)
		{
			final String productCode = productModel.getCode();

			addValuesForProductCode(values, productCode);
		}
		return values;
	}

	protected void addValuesForProductCode(final List<Object> values, final String productCode)
	{
		final Map<String, CsticParameterWithValues> csticParametersWithValues = getParameterProviderService()
				.retrieveProductCsticsAndValuesParameters(productCode);

		for (final Map.Entry<String, CsticParameterWithValues> entry : csticParametersWithValues.entrySet())
		{
			final CsticParameterWithValues csticParameterWithValues = entry.getValue();
			final CsticParameter csticParameter = csticParameterWithValues.getCstic();
			final String csticName = csticParameter.getCsticName();
			if (!values.contains(csticName))
			{
				values.add(csticName);
			}
		}
	}

	protected List<Object> getPossibleValuesForActionParameter(final List<String> productCodeList)
	{
		final List<Object> values = new ArrayList<>();
		for (final String productCode : productCodeList)
		{
			addValuesForProductCode(values, productCode);
		}
		return values;
	}

}
