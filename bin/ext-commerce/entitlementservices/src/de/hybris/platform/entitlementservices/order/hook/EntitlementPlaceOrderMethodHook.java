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
package de.hybris.platform.entitlementservices.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.entitlementservices.data.EmsGrantData;
import de.hybris.platform.entitlementservices.exception.EntitlementFacadeException;
import de.hybris.platform.entitlementservices.facades.EntitlementFacadeDecorator;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implements the CommercePlaceOrderMethodHook to handle grants.
 */
public class EntitlementPlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private static final Logger LOG = Logger.getLogger(EntitlementPlaceOrderMethodHook.class);

	private EntitlementFacadeDecorator entitlementFacadeDecorator;
	private Converter<ProductEntitlementModel, EmsGrantData> productEntitlementEmsGrantConverter;
	private BaseStoreService baseStoreService;


	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
	{
		validateParameterNotNullStandardMessage("commerceOrderResult", orderModel);
		validateParameterNotNullStandardMessage("order", orderModel.getOrder());
		try
		{
			createGrants(orderModel);
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Cannot create grants for order with id='" + orderModel.getOrder().getCode() + "'", e);
		}
	}

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter)
	{
		// Do nothing before place the order
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
	{
		// Do nothing before submit the order
	}

	/**
	 * Creates grants by order.
	 *
	 * @param commerceOrderResult
	 *           the CommerceOrderResult
	 * @return the CommerceOrderResult
	 *
	 * @throws de.hybris.platform.order.InvalidCartException
	 *            if grants creation has been failed.
	 */
	protected CommerceOrderResult createGrants(final CommerceOrderResult commerceOrderResult) throws InvalidCartException
	{
		validateParameterNotNullStandardMessage("commerceOrderResult", commerceOrderResult);
		validateParameterNotNullStandardMessage("order", commerceOrderResult.getOrder());
		final OrderModel orderModel = commerceOrderResult.getOrder();
		try
		{
			for (final AbstractOrderEntryModel entry : orderModel.getEntries())
			{
				final ProductModel productModel = entry.getProduct();
				for (final ProductEntitlementModel productEntitlementModel : productModel.getProductEntitlements())
				{
					createEmsGrantData(orderModel, entry, productEntitlementModel);
				}
			}
		}
		catch (final EntitlementFacadeException | ConversionException e)
		{
			throw new InvalidCartException("Error creating entitlements for order with id='"
					+ commerceOrderResult.getOrder().getCode() + "'", e);
		}
		return commerceOrderResult;
	}

	protected void createEmsGrantData(final OrderModel orderModel, final AbstractOrderEntryModel entry,
			final ProductEntitlementModel productEntitlementModel) throws EntitlementFacadeException
	{
		final EmsGrantData emsGrantData = convert(productEntitlementModel);
		emsGrantData.setUserId(orderModel.getUser().getUid());
		emsGrantData.setOrderCode(orderModel.getCode());
		emsGrantData.setCreatedAt(orderModel.getDate());
		emsGrantData.setBaseStoreUid(getBaseStoreService().getCurrentBaseStore().getUid());
		emsGrantData.setOrderEntryNumber(String.valueOf(entry.getEntryNumber()));
		for (long index = 1; index <= entry.getQuantity(); index++)
		{
			getEntitlementFacadeDecorator().createEntitlement(emsGrantData);
		}
	}

	/**
	 * Convert ProductEntitlementModel to EntitlementData.
	 *
	 * @param entitlement
	 *           ProductEntitlementModel to be converted.
	 * @return EntitlementData
	 */
	protected EmsGrantData convert(final ProductEntitlementModel entitlement)
	{
		return getProductEntitlementEmsGrantConverter().convert(entitlement);
	}

	protected EntitlementFacadeDecorator getEntitlementFacadeDecorator()
	{
		return entitlementFacadeDecorator;
	}

	@Required
	public void setEntitlementFacadeDecorator(final EntitlementFacadeDecorator entitlementFacadeDecorator)
	{
		this.entitlementFacadeDecorator = entitlementFacadeDecorator;
	}

	protected Converter<ProductEntitlementModel, EmsGrantData> getProductEntitlementEmsGrantConverter()
	{
		return productEntitlementEmsGrantConverter;
	}

	@Required
	public void setProductEntitlementEmsGrantConverter(
			final Converter<ProductEntitlementModel, EmsGrantData> productEntitlementEmsGrantConverter)
	{
		this.productEntitlementEmsGrantConverter = productEntitlementEmsGrantConverter;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService service)
	{
		this.baseStoreService = service;
	}

}
