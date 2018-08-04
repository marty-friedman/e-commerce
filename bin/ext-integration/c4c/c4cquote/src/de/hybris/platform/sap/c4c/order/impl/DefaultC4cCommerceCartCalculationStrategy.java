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
package de.hybris.platform.sap.c4c.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;


public class DefaultC4cCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy
{
	
	/**
	 * @deprecated Since 5.2.
	 */
	@Override
	@Deprecated
	public boolean calculateCart(final CartModel cartModel)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		return this.calculateCart(parameter);
	}

	@Override
	public boolean calculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");

		final QuoteModel quoteModel = cartModel.getQuoteReference();
		final CalculationService calcService = getCalculationService();
		boolean recalculated = false;
		if(!(quoteModel != null && quoteModel.getC4cQuoteId() != null && !quoteModel.getC4cQuoteId().isEmpty()) && calcService.requiresCalculation(cartModel))
		{
			
			try
			{
				parameter.setRecalculate(false);
				beforeCalculate(parameter);
				calcService.calculate(cartModel);
				getPromotionsService().updatePromotions(getPromotionGroups(), cartModel, true, AutoApplyMode.APPLY_ALL,
						AutoApplyMode.APPLY_ALL, getTimeService().getCurrentTime());
			}
			catch (final CalculationException calculationException)
			{
				throw new IllegalStateException("Cart model " + cartModel.getCode() + " was not calculated due to: "
						+ calculationException.getMessage(), calculationException);
			}
			finally
			{
				afterCalculate(parameter);
			}
				recalculated = true;
			}

		if (isCalculateExternalTaxes())
		{
			getExternalTaxesService().calculateExternalTaxes(cartModel);
		}
		return recalculated;
	}
}
