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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


/**
 * Default implementtaion of the {@link ProviderFactory}.
 */
@SuppressWarnings(
{ "deprecation", "squid:CallToDeprecatedMethod" })
public class ProviderFactoryImpl implements ProviderFactory, ConfigurationProviderFactory
{
	private static final String ANALYTICS_PROVIDER_BEAN_ALIAS = "sapProductConfigAnalyticsProvider";
	private static final String CONFIG_PROVIDER_BEAN_ALIAS = "sapProductConfigConfigurationProvider";
	private static final String PRICING_PROVIDER_BEAN_ALIAS = "sapProductConfigPricingProvider";
	private static final String PRICING_PARAMETER_BEAN_ALIAS = "sapProductConfigPricingParameters";
	private static final String PRODUCT_CSTIC_VALUE_PARAMETER_PROVIDER_BEAN_ALIAS = "sapProductConfigProductCsticAndValueParameterProvider";
	private static final Logger LOG = Logger.getLogger(ProviderFactoryImpl.class);

	private SessionAccessService sessionAccessService;
	private ApplicationContext applicationContext;

	protected static final String SESSION_CACHE_KEY = ConfigurationProvider.class.getName();
	private String configurationProviderBeanName = CONFIG_PROVIDER_BEAN_ALIAS;
	private String analyticsProviderBeanName = ANALYTICS_PROVIDER_BEAN_ALIAS;
	private String pricingProviderBeanName = PRICING_PROVIDER_BEAN_ALIAS;
	private String pricingParameterBeanName = PRICING_PARAMETER_BEAN_ALIAS;
	private String productCsticAndValueParameterProviderBeanName = PRODUCT_CSTIC_VALUE_PARAMETER_PROVIDER_BEAN_ALIAS;


	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		ConfigurationProvider provider = sessionAccessService.getConfigurationProvider();
		if (provider == null)
		{
			provider = (ConfigurationProvider) createProviderInstance(getConfigurationProviderBeanName());
			sessionAccessService.setConfigurationProvider(provider);
		}
		return provider;
	}

	@Override
	public PricingProvider getPricingProvider()
	{
		PricingProvider provider = sessionAccessService.getPricingProvider();
		if (provider == null)
		{
			provider = (PricingProvider) createProviderInstance(getPricingProviderBeanName());
			sessionAccessService.setPricingProvider(provider);
		}
		return provider;
	}

	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		AnalyticsProvider analyticsProvider = getSessionAccessService().getAnalyticsProvider();
		if (null == analyticsProvider)
		{
			analyticsProvider = (AnalyticsProvider) createProviderInstance(getAnalyticsProviderBeanName());
			getSessionAccessService().setAnalyticsProvider(analyticsProvider);
		}
		return analyticsProvider;
	}

	/**
	 * @param analyticsProviderBeanName
	 *           the analyticsProviderBeanName to set
	 */
	public void setAnalyticsProviderBeanName(final String analyticsProviderBeanName)
	{
		setProviderBeanName();
		this.analyticsProviderBeanName = analyticsProviderBeanName;
	}

	protected void setProviderBeanName()
	{
		final Tenant tenant = Registry.getCurrentTenantNoFallback();
		if (null == tenant || !"junit".equals(tenant.getTenantID()))
		{
			String tenantId = null;
			if (tenant != null)
			{
				tenantId = tenant.getTenantID();
			}
			throw new IllegalStateException(
					"Setting the provider bean is only allowed for the junit tenant; current tenant is " + tenantId);
		}
	}

	@Override
	public ProductCsticAndValueParameterProvider getProductCsticAndValueParameterProvider()
	{
		return (ProductCsticAndValueParameterProvider) createProviderInstance(getProductCsticValueParameterProviderBeanName());
	}

	@Override
	public PricingConfigurationParameter getPricingParameter()
	{
		PricingConfigurationParameter pricinParameter = getSessionAccessService().getPricingConfigurationParameter();
		if (null == pricinParameter)
		{
			pricinParameter = (PricingConfigurationParameter) createProviderInstance(getPricingParameterBeanName());
			getSessionAccessService().setPricingConfigurationParameter(pricinParameter);
		}
		return pricinParameter;

	}


	protected Object createProviderInstance(final String providerBean)
	{
		Object provider;

		ApplicationContext applCtxt = getApplicationContext();

		if (applCtxt == null)
		{
			applCtxt = ServicelayerUtils.getApplicationContext();
			setApplicationContext(applCtxt);
		}

		if (applCtxt == null)
		{
			throw new IllegalStateException("Application Context not available");
		}


		provider = applicationContext.getBean(providerBean);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("created a new provider instance of " + providerBean);
		}

		return provider;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * used for tests
	 */
	void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	public String getConfigurationProviderBeanName()
	{
		return configurationProviderBeanName;
	}

	/**
	 * @param configurationProviderBeanName
	 *           the configurationProviderBeanName to set
	 */
	public void setConfigurationProviderBeanName(final String configurationProviderBeanName)
	{
		setProviderBeanName();
		this.configurationProviderBeanName = configurationProviderBeanName;
	}

	public String getPricingProviderBeanName()
	{
		return pricingProviderBeanName;
	}

	/**
	 * @param pricingProviderBeanName
	 *           the pricingProviderBeanName to set
	 */
	public void setPricingProviderBeanName(final String pricingProviderBeanName)
	{
		setProviderBeanName();
		this.pricingProviderBeanName = pricingProviderBeanName;
	}

	public String getAnalyticsProviderBeanName()
	{
		return analyticsProviderBeanName;
	}

	/**
	 * @param productCsticAndValueParameterProviderBeanName
	 *           the productCsticAndValueParameterProviderBeanName to set
	 */
	public void setProductCsticAndValueParameterProviderBeanName(final String productCsticAndValueParameterProviderBeanName)
	{
		setProviderBeanName();
		this.productCsticAndValueParameterProviderBeanName = productCsticAndValueParameterProviderBeanName;
	}

	protected String getProductCsticValueParameterProviderBeanName()
	{
		return productCsticAndValueParameterProviderBeanName;
	}

	public void setPricingParametersBeanName(final String pricingParameterBeanName)
	{
		this.pricingParameterBeanName = pricingParameterBeanName;
	}

	protected String getPricingParameterBeanName()
	{
		return pricingParameterBeanName;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	/**
	 * @deprecated since 6.5, use {@link ProviderFactory#getConfigurationProvider} instead
	 * @return configuration provider bean
	 **/
	@SuppressWarnings("squid:S1133")
	@Deprecated
	@Override
	public ConfigurationProvider getProvider()
	{
		return getConfigurationProvider();
	}

}
