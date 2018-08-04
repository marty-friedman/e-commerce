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
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequest;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentCard;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentSource;
import de.hybris.platform.cissapdigitalpayment.service.CisSapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.commerceservices.order.CommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.order.hook.AuthorizePaymentMethodHook;
import de.hybris.platform.commerceservices.order.impl.DefaultCommercePaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.strategies.GenerateMerchantTransactionCodeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SAP Digital Payments specific implementation of {@link CommercePaymentAuthorizationStrategy}
 */
public class SapDigitalCommercePaymentAuthorizationStrategy extends DefaultCommercePaymentAuthorizationStrategy
		implements CommercePaymentAuthorizationStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(SapDigitalCommercePaymentAuthorizationStrategy.class);


	private GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy;
	private I18NService i18nService;
	private CisSapDigitalPaymentService cisSapDigitalPaymentService;
	private SapDigitalPaymentService sapDigitalPaymentService;
	private ModelService modelService;
	private List<AuthorizePaymentMethodHook> authorizePaymentHooks;
	private ConfigurationService configurationService;

	private SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy;

	/**
	 * Authorize the payment amount while placing the order. Calls the SAP DIgital payment client to authorize the
	 * payment, Creates an {@link PaymentTransactionEntryModel} from the authorization result. If the
	 * {@link PaymentTransactionModel}'s transaction status is ACCEPTED or REVIEW, set the payment and order information
	 * to the {@link PaymentTransactionModel}
	 *
	 * @param parameter
	 *           - {@link CommerceCheckoutParameter} containing the cart, payment provider, authorization amount.
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	@Override
	public PaymentTransactionEntryModel authorizePaymentAmount(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		final BigDecimal amount = parameter.getAuthorizationAmount();
		final String paymentProvider = parameter.getPaymentProvider();

		//If the SAP digital payment configuration is missing, call the default authorizePaymentAmount method
		if (null == getSapDigitalPaymentConfigurationStrategy().getSapDigitalPaymentConfiguration())
		{
			return super.authorizePaymentAmount(parameter);
		}

		PaymentTransactionEntryModel transactionEntryModel = null;
		final PaymentInfoModel paymentInfo = cartModel.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel
				&& StringUtils.isNotBlank(((CreditCardPaymentInfoModel) paymentInfo).getSubscriptionId()))
		{
			final Currency currency = getI18nService().getBestMatchingJavaCurrency(cartModel.getCurrency().getIsocode());
			CisSapDigitalPaymentAuthorizationResultList authorizationResultList = null;

			try
			{
				authorizationResultList = getCisSapDigitalPaymentService()
						.authorizePayment(createAuthorizePaymentRequest(((CreditCardPaymentInfoModel) paymentInfo).getSubscriptionId(),
								amount, currency.getCurrencyCode()))
						.toBlocking().first();
			}
			catch (final RuntimeException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error while fetching the authorization response from SAP Digital payment" + e);
				}
				LOG.error("Error while fetching the authorization response from SAP Digital payment" + e.getMessage());
			}

			CisSapDigitalPaymentAuthorizationResult authorizationResult = null;

			//Check if the response received is not empty. If not, take the first from the list
			if (null != authorizationResultList
					&& CollectionUtils.isNotEmpty(authorizationResultList.getCisSapDigitalPaymentAuthorizationResults()))
			{
				authorizationResult = authorizationResultList.getCisSapDigitalPaymentAuthorizationResults().stream().findFirst()
						.get();
			}

			final String merchantTransactionCode = getGenerateMerchantTransactionCodeStrategy().generateCode(cartModel);

			try
			{
				transactionEntryModel = getSapDigitalPaymentService().authorize(merchantTransactionCode, paymentProvider,
						cartModel.getDeliveryAddress(), authorizationResult);
			}
			catch (final RuntimeException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error while creating transaction entry from authorization response received" + e);
				}
				LOG.error("Error while creating transaction entry from authorization response received" + e.getMessage());
			}


			if (transactionEntryModel != null)
			{
				final PaymentTransactionModel paymentTransaction = transactionEntryModel.getPaymentTransaction();

				if (TransactionStatus.ACCEPTED.name().equals(transactionEntryModel.getTransactionStatus())
						|| TransactionStatus.REVIEW.name().equals(transactionEntryModel.getTransactionStatus()))
				{
					paymentTransaction.setOrder(cartModel);
					paymentTransaction.setInfo(paymentInfo);
					getModelService().saveAll(cartModel, paymentTransaction);
				}
				else
				{
					// TransactionStatus is error or reject remove the PaymentTransaction and TransactionEntry
					getModelService().removeAll(Arrays.asList(paymentTransaction, transactionEntryModel));
				}
			}
		}

		return transactionEntryModel;
	}

	/**
	 * Creates the payment authorization request object. SAP Digital payment expects a list of payment authorizations,
	 * wrap the objects around a list and send.
	 *
	 * @param subscriptionId
	 *           - subscription ID
	 * @param amount
	 *           - authorization amount
	 * @param currencyCode
	 *           - currency code
	 *
	 * @return {@link CisSapDigitalPaymentAuthorizationRequestList}
	 */

	protected CisSapDigitalPaymentAuthorizationRequestList createAuthorizePaymentRequest(final String subscriptionId,
			final BigDecimal amount, final String currencyCode)
	{

		final CisSapDigitalPaymentAuthorizationRequestList authorizationRequestList = new CisSapDigitalPaymentAuthorizationRequestList();
		final CisSapDigitalPaymentAuthorizationRequest authorizationRequest = new CisSapDigitalPaymentAuthorizationRequest();
		final CisSapDigitalPaymentCard paymentCard = new CisSapDigitalPaymentCard();
		try
		{
			paymentCard.setPaytCardByDigitalPaymentSrvc(subscriptionId);
			final CisSapDigitalPaymentSource paymentSource = new CisSapDigitalPaymentSource();
			paymentSource.setCisSapDigitalPaymentCard(paymentCard);

			authorizationRequest.setCisSapDigitalPaymentSource(paymentSource);
			authorizationRequest.setAmountInAuthorizationCurrency(amount.toString());
			authorizationRequest.setAuthorizationCurrency(currencyCode);

			final List<CisSapDigitalPaymentAuthorizationRequest> authorizationRequests = new ArrayList<>();
			authorizationRequests.add(authorizationRequest);
			authorizationRequestList.setCisSapDigitalPaymentAuthorizationRequests(authorizationRequests);
		}
		catch (final RuntimeException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Error while creating the Payment authorization request" + e);
			}
			LOG.error("Error while creating the Payment authorization request" + e.getMessage());
		}

		return authorizationRequestList;
	}

	/**
	 * @return the generateMerchantTransactionCodeStrategy
	 */
	@Override
	public GenerateMerchantTransactionCodeStrategy getGenerateMerchantTransactionCodeStrategy()
	{
		return generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @param generateMerchantTransactionCodeStrategy
	 *           the generateMerchantTransactionCodeStrategy to set
	 */
	@Override
	public void setGenerateMerchantTransactionCodeStrategy(
			final GenerateMerchantTransactionCodeStrategy generateMerchantTransactionCodeStrategy)
	{
		this.generateMerchantTransactionCodeStrategy = generateMerchantTransactionCodeStrategy;
	}

	/**
	 * @return the i18nService
	 */
	@Override
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	@Override
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}



	/**
	 * @return the cisSapDigitalPaymentService
	 */
	public CisSapDigitalPaymentService getCisSapDigitalPaymentService()
	{
		return cisSapDigitalPaymentService;
	}

	/**
	 * @param cisSapDigitalPaymentService
	 *           the cisSapDigitalPaymentService to set
	 */
	public void setCisSapDigitalPaymentService(final CisSapDigitalPaymentService cisSapDigitalPaymentService)
	{
		this.cisSapDigitalPaymentService = cisSapDigitalPaymentService;
	}

	/**
	 * @return the modelService
	 */
	@Override
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the authorizePaymentHooks
	 */
	@Override
	public List<AuthorizePaymentMethodHook> getAuthorizePaymentHooks()
	{
		return authorizePaymentHooks;
	}

	/**
	 * @param authorizePaymentHooks
	 *           the authorizePaymentHooks to set
	 */
	@Override
	public void setAuthorizePaymentHooks(final List<AuthorizePaymentMethodHook> authorizePaymentHooks)
	{
		this.authorizePaymentHooks = authorizePaymentHooks;
	}

	/**
	 * @return the configurationService
	 */
	@Override
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Override
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}

	/**
	 * @param sapDigitalPaymentService
	 *           the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}

	/**
	 * @return the sapDigitalPaymentConfigurationStrategy
	 */
	public SapDigitalPaymentConfigurationStrategy getSapDigitalPaymentConfigurationStrategy()
	{
		return sapDigitalPaymentConfigurationStrategy;
	}

	/**
	 * @param sapDigitalPaymentConfigurationStrategy
	 *           the sapDigitalPaymentConfigurationStrategy to set
	 */
	public void setSapDigitalPaymentConfigurationStrategy(
			final SapDigitalPaymentConfigurationStrategy sapDigitalPaymentConfigurationStrategy)
	{
		this.sapDigitalPaymentConfigurationStrategy = sapDigitalPaymentConfigurationStrategy;
	}





}
