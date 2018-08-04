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
package de.hybris.platform.chinesepaymentfacades.checkout.impl;

import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;
import de.hybris.platform.chinesepaymentfacades.checkout.ChineseCheckoutFacade;
import de.hybris.platform.chinesepaymentservices.checkout.ChineseCheckoutService;
import de.hybris.platform.chinesepaymentservices.order.service.impl.DefaultChineseOrderService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.PickupOrderEntryGroupData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.strategies.impl.EventPublishingSubmitOrderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Implementation for {@link ChineseCheckoutFacade}. Delivers main functionality for chinese checkout.
 */
public class DefaultChineseCheckoutFacade extends DefaultAcceleratorCheckoutFacade implements ChineseCheckoutFacade
{
	private static final String ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE = "Order with guid %s not found for current user in current BaseStore";

	private Converter<CartModel, CartData> cartConverter;
	private ChineseCheckoutService chineseCheckoutService;
	private DefaultChineseOrderService chineseOrderService;
	private Converter<CartModel, CartData> cartChinesePaymentInfoConverter;
	private EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy;
	private ConfigurationService configurationService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public OrderData createOrder() throws BusinessException
	{

		final CartModel cartModel = getCart();
		if (cartModel != null)
		{
			final UserModel currentUser = getCurrentUserForCheckout();
			if (cartModel.getUser().equals(currentUser) || getCheckoutCustomerStrategy().isAnonymousCheckout())
			{
				beforePlaceOrder(cartModel);
				final OrderModel orderModel = placeOrder(cartModel);
				afterPlaceOrder(cartModel, orderModel);
				return reserveStockAfterPlaceOrder(orderModel);
			}
		}

		return null;
	}
	
	/**
	 * Reserve stock after place order
	 * @param orderModel
	 * @return
	 * @throws BusinessException
	 */
	private OrderData reserveStockAfterPlaceOrder(final OrderModel orderModel) throws BusinessException{
		OrderData orderData = null;
		if (orderModel != null)
		{
			orderData = getOrderConverter().convert(orderModel);
			
			if (configurationService.getConfiguration().getBoolean("order.reserve.stock.enabled", false))
			{
				if (!CollectionUtils.isEmpty(orderData.getPickupOrderGroups()))
				{
					processPickupReserveStock(orderData);
				}
				if (!CollectionUtils.isEmpty(orderData.getDeliveryOrderGroups()))
				{
					processDeliveryReserveStock(orderData);
				}				
			}			
		}
		return orderData;
	}
	
	/**
	 * Process pickup in store reserve stock
	 * @param orderData
	 * @throws BusinessException
	 */
	private void processPickupReserveStock(final OrderData orderData) throws BusinessException{
		for (final PickupOrderEntryGroupData pickupOrderEntryGroupData : orderData.getPickupOrderGroups())
		{
			final PointOfServiceData pointOfServiceData = pickupOrderEntryGroupData.getDeliveryPointOfService();
			final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(
					pointOfServiceData.getName());
			if (!CollectionUtils.isEmpty(pickupOrderEntryGroupData.getEntries()))
			{
				for (final OrderEntryData entry : pickupOrderEntryGroupData.getEntries())
				{
					reserveStock(orderData.getCode(), entry.getProduct().getCode(), entry.getQuantity().intValue(),
							Optional.of(pointOfServiceModel));
				}
			}
		}
	}
	
	/**
	 * Process delivery reserve stock
	 * @param orderData
	 * @throws BusinessException
	 */
	private void processDeliveryReserveStock(final OrderData orderData) throws BusinessException{
		for (final DeliveryOrderEntryGroupData deliveryOrderEntryGroupData : orderData.getDeliveryOrderGroups())
		{
			if (!CollectionUtils.isEmpty(deliveryOrderEntryGroupData.getEntries()))
			{
				for (final OrderEntryData entry : deliveryOrderEntryGroupData.getEntries())
				{
					reserveStock(orderData.getCode(), entry.getProduct().getCode(), entry.getQuantity().intValue(),
							Optional.<PointOfServiceModel> empty());
				}
			}
		}
	}

	@Override
	public CartModel getCart()
	{
		if (getCartFacade().hasSessionCart())
		{
			return getCartService().getSessionCart();
		}
		return null;
	}

	@Override
	public void mergeCart(final CartModel cartModel)
	{
		getCartService().saveOrder(cartModel);
	}

	@Override
	public CartData convertCart(final CartModel cartModel)
	{
		return cartConverter.convert(cartModel);
	}

	@Override
	public void setPaymentMode(final PaymentModeModel paymentMode)
	{
		if (getCart() != null)
		{
			chineseCheckoutService.setPaymentMode(paymentMode, getCart());
		}
	}

	@Override
	public boolean authorizePayment(final String securityCode)
	{
		return chineseCheckoutService.authorizePayment(securityCode, getCart());
	}

	@Override
	public boolean reserveStock(final String orderCode, final String productCode, final int quantity,
			final Optional<PointOfServiceModel> pos) throws InsufficientStockLevelException
	{
		return chineseCheckoutService.reserveStock(orderCode, productCode, quantity, pos);
	}

	@Override
	public CartData getCheckoutCart()
	{
		return getCartChinesePaymentInfoConverter().convert(getCart(), super.getCheckoutCart());
	}

	@Override
	public OrderData getOrderDetailsForCode(final String code)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();

		OrderModel orderModel = null;
		if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			orderModel = getCustomerAccountService().getOrderForCode(code, baseStoreModel);
		}
		else
		{
			try
			{
				orderModel = getCustomerAccountService().getOrderForCode((CustomerModel) getUserService().getCurrentUser(), code,
						baseStoreModel);
			}
			catch (final ModelNotFoundException e)
			{
				throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, code));
			}
		}

		if (orderModel == null)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, code));
		}
		final OrderData orderData = getOrderConverter().convert(orderModel);
		return orderData;
	}

	@Override
	public void deleteStockLevelReservationHistoryEntry(final String code)
	{
		chineseCheckoutService.deleteStockLevelReservationHistoryEntry(code);
	}

	@Override
	public OrderData getOrderByCode(final String code)
	{
		final OrderModel orderModel = chineseCheckoutService.getOrderByCode(code);
		final OrderData orderData = getOrderConverter().convert(orderModel);
		return orderData;
	}

	@Override
	public boolean hasNoChinesePaymentInfo()
	{
		return getCheckoutCart().getChinesePaymentInfo() == null;
	}

	@Override
	public void publishSubmitOrderEvent(final String orderCode)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();

		OrderModel orderModel = null;
		if (getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			orderModel = getCustomerAccountService().getOrderForCode(orderCode, baseStoreModel);
		}
		else
		{
			try
			{
				orderModel = getCustomerAccountService().getOrderForCode((CustomerModel) getUserService().getCurrentUser(),
						orderCode, baseStoreModel);
			}
			catch (final ModelNotFoundException e)
			{
				throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderCode));
			}
		}

		if (orderModel == null)
		{
			throw new UnknownIdentifierException(String.format(ORDER_NOT_FOUND_FOR_USER_AND_BASE_STORE, orderCode));
		}
		
		if (OrderStatus.CREATED.equals(orderModel.getStatus()))
		{
			eventPublishingSubmitOrderStrategy.submitOrder(orderModel);			
		}
	}

	protected Converter<CartModel, CartData> getCartConverter()
	{
		return cartConverter;
	}

	@Required
	public void setCartConverter(final Converter<CartModel, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}

	protected ChineseCheckoutService getChineseCheckoutService()
	{
		return chineseCheckoutService;
	}

	@Required
	public void setChineseCheckoutService(final ChineseCheckoutService chineseCheckoutService)
	{
		this.chineseCheckoutService = chineseCheckoutService;
	}

	protected DefaultChineseOrderService getChineseOrderService()
	{
		return chineseOrderService;
	}

	@Required
	public void setChineseOrderService(final DefaultChineseOrderService chineseOrderService)
	{
		this.chineseOrderService = chineseOrderService;
	}

	protected Converter<CartModel, CartData> getCartChinesePaymentInfoConverter()
	{
		return cartChinesePaymentInfoConverter;
	}

	@Required
	public void setCartChinesePaymentInfoConverter(final Converter<CartModel, CartData> cartChinesePaymentInfoConverter)
	{
		this.cartChinesePaymentInfoConverter = cartChinesePaymentInfoConverter;
	}

	protected EventPublishingSubmitOrderStrategy getEventPublishingSubmitOrderStrategy()
	{
		return eventPublishingSubmitOrderStrategy;
	}

	@Required
	public void setEventPublishingSubmitOrderStrategy(final EventPublishingSubmitOrderStrategy eventPublishingSubmitOrderStrategy)
	{
		this.eventPublishingSubmitOrderStrategy = eventPublishingSubmitOrderStrategy;
	}

	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}
	
	@Required
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	

}
