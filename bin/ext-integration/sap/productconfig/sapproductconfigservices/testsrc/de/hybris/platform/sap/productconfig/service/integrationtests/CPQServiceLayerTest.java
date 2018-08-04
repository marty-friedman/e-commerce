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
package de.hybris.platform.sap.productconfig.service.integrationtests;

import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.services.impl.ProviderFactoryImpl;
import de.hybris.platform.sap.productconfig.services.impl.ServiceConfigurationValueHelperImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;


@SuppressWarnings("javadoc")
public abstract class CPQServiceLayerTest extends ServicelayerTest
{
	private static Logger LOG = Logger.getLogger(CPQServiceLayerTest.class);

	protected static final String PRODUCT_CODE_YSAP_NOCFG = "YSAP_NOCFG";
	protected static final String PRODUCT_CODE_CPQ_HOME_THEATER = "CPQ_HOME_THEATER";
	protected static final String PRODUCT_CODE_WCEM_MULTILEVEL = "WCEM_MULTILEVEL";
	protected static final String PRODUCT_CODE_CPQ_LAPTOP = "CPQ_LAPTOP";
	protected static final String PRODUCT_CODE_YSAP_SIMPLE_POC = "YSAP_SIMPLE_POC";
	protected static final String TEST_CONFIGURE_SITE = "testConfigureSite";


	@Resource(name = "userService")
	protected UserService realUserService;

	@Resource(name = "sapProductConfigProviderFactory")
	protected ProviderFactoryImpl providerFactory;

	@Resource(name = "sapProductConfigConfigurationService")
	protected ProductConfigurationService cpqService;

	// hybris facades
	@Resource(name = "i18NService")
	private I18NService i18NService;
	@Resource(name = "flexibleSearchService")
	protected FlexibleSearchService flexibleSearchService;
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	@Resource(name = "baseSiteService")
	protected BaseSiteService baseSiteService;

	@Resource(name = "sapProductConfigDefaultConfigurationService")
	protected ProductConfigurationService cpqServiceNoRules;

	@Mock
	protected UserService mockedUserService;
	protected QuoteUserIdentificationStrategy mockedQuoteUserIdentificationStrategy;
	protected CheckoutCustomerStrategy mockedCheckoutCustomerStrategy;
	protected CustomerModel customerModel;

	protected ServiceConfigurationValueHelperImpl serviceConfigValueHelper = new ServiceConfigurationValueHelperImpl();

	protected static final KBKey KB_CPQ_HOME_THEATER;
	protected static final KBKey KB_CPQ_LAPTOP;
	protected static final KBKey KB_Y_SAP_SIMPLE_POC;

	static
	{
		KB_CPQ_HOME_THEATER = new KBKeyImpl(PRODUCT_CODE_CPQ_HOME_THEATER);
		KB_CPQ_LAPTOP = new KBKeyImpl(PRODUCT_CODE_CPQ_LAPTOP);
		KB_Y_SAP_SIMPLE_POC = new KBKeyImpl(PRODUCT_CODE_YSAP_SIMPLE_POC);
	}

	protected void importCsvIfExist(final String csvFile, final String encoding) throws Exception
	{
		final InputStream inStream = CPQServiceLayerTest.class.getResourceAsStream(csvFile);


		if (inStream != null)
		{
			inStream.close();
			importCsv(csvFile, encoding);
		}
		else
		{
			LOG.info("file not found: " + csvFile);
		}
	}


	public static void createCoreData() throws Exception
	{
		// copied from ServicelayerTestLogic.createCoredata()
		// we only need this, but do not want to import the impex file (to save testruntime)
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		//ServicelayerTestLogic.createCoreData();
	}

	protected void prepareCPQData() throws Exception
	{
		final long startTime = System.currentTimeMillis();
		LOG.info("CREATING CORE DATA FOR CPQ-TEST....");

		createCoreData();
		importCPQTestData();

		// normally the base site is derived from the request URL via pattern macthing - in integration test mode we set it active manually
		baseSiteService.setCurrentBaseSite(TEST_CONFIGURE_SITE, false);

		// default in hybris is DE/EUR
		LOG.info("Tests running with locale: " + i18NService.getCurrentLocale().toString());
		final CurrencyModel cur = i18NService.getCurrentCurrency();
		LOG.info("Tests running with Currency: isoCode=" + cur.getIsocode() + "; sapCode=" + cur.getSapCode());

		final long duration = System.currentTimeMillis() - startTime;
		LOG.info("CPQ DATA READY FOR TEST! (" + duration + "ms)");

	}


	protected void useCurrency_USD()
	{
		// force USD currency for tests
		// do no inject via i18nService, as this causes a class cast exception in jalo layer in some scenarios
		// for example, Hybris price factories read currency directly from jalo session ==> class cast exception Currency <-> CurrencyModel
		// instead inject in jalo layer directly. as i18nservice will do the conversion from Currency to CurrencyModel on the fly
		final Currency usd = C2LManager.getInstance().getCurrencyByIsoCode("USD");
		JaloSession.getCurrentSession().getSessionContext().setCurrency(usd);
	}


	protected void useLocale_EN()
	{
		// force english locale for tests
		i18NService.setCurrentLocale(Locale.ENGLISH);
	}


	protected void importCPQUserData() throws ImpExException
	{
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_userTestData.impex", "utf-8");
		customerModel = getFromPersistence("Select {pk} from {Customer} where {uid}='cpq02@sap.com'");
		Mockito.when(mockedUserService.getCurrentUser()).thenReturn(customerModel);
		mockedQuoteUserIdentificationStrategy = Mockito.mock(QuoteUserIdentificationStrategy.class);
		Mockito.when(mockedQuoteUserIdentificationStrategy.getCurrentQuoteUser()).thenReturn(customerModel);
		mockedCheckoutCustomerStrategy = Mockito.mock(CheckoutCustomerStrategy.class);
		Mockito.when(mockedCheckoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customerModel);
	}

	protected void importCPQTestData() throws ImpExException, Exception
	{
		LOG.info("CREATING CPQ DATA FOR CPQ-TEST....");
		importCsv("/sapproductconfigservices/test/sapProductConfig_basic_testData.impex", "utf-8");
		importCsvIfExist("/sapproductconfigrules/test/sapProductConfig_cpqRules_testData.impex", "utf-8");
	}

	@Before
	public void initProviders()
	{
		ensureMockProvider();
	}

	public void ensureMockProvider()
	{
		providerFactory.setConfigurationProviderBeanName("sapProductConfigConfigurationProviderMock");
		providerFactory.setPricingProviderBeanName("sapProductConfigDefaultPricingProvider");
		providerFactory.setAnalyticsProviderBeanName("sapProductConfigDefaultAnalyticsProvider");
		providerFactory.setPricingParametersBeanName("sapProductConfigPricingParameters");
	}

	public void ensureSSCProvider()
	{
		providerFactory.setConfigurationProviderBeanName("sapProductConfigConfigurationProviderSSC");
		providerFactory.setPricingProviderBeanName("sapProductConfigDefaultPricingProvider");
		providerFactory.setAnalyticsProviderBeanName("sapProductConfigDefaultAnalyticsProvider");
		providerFactory
				.setProductCsticAndValueParameterProviderBeanName("sapProductConfigDefaultProductCsticAndValueParameterProviderSSC");
		providerFactory.setPricingProviderBeanName("sapProductConfigPricingParametersSSC");
	}

	public void ensureCPSProvider()
	{
		providerFactory.setConfigurationProviderBeanName("sapProductConfigConfigurationProviderCPS");
		providerFactory.setPricingProviderBeanName("sapProductConfigPricingProviderCPS");
		providerFactory.setAnalyticsProviderBeanName("sapProductConfigDefaultPCIAnalyticsProvider");
		providerFactory
				.setProductCsticAndValueParameterProviderBeanName("sapProductConfigDefaultProductCsticAndValueParameterProviderCPS");
		providerFactory.setPricingProviderBeanName("sapProductConfigPricingParametersCPS");
	}

	/**
	 * Reads a model from persistence via flexible search
	 *
	 * @param flexibleSearchSelect
	 * @return Model
	 */
	protected <T> T getFromPersistence(final String flexibleSearchSelect)
	{
		final SearchResult<Object> searchResult = flexibleSearchService.search(flexibleSearchSelect);
		Assert.assertEquals("FlexSearach Query - " + flexibleSearchSelect + ":", 1, searchResult.getTotalCount());
		return (T) searchResult.getResult().get(0);
	}
}
