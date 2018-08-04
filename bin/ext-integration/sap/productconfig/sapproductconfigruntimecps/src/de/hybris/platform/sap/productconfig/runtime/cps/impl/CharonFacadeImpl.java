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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValueInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CookieHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.util.URLEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Default implementation of {@link CharonFacade}. This bean has prototype scope
 */
public class CharonFacadeImpl implements CharonFacade
{
	private static final Logger LOG = Logger.getLogger(CharonFacadeImpl.class);

	private ConfigurationClientBase clientSetExternally = null;
	private CookieHandler cookieHandler;
	private RequestErrorHandler requestErrorHandler;
	private ObjectMapper objectMapper;
	private final Scheduler scheduler = Schedulers.io();
	private YaasServiceFactory yaasServiceFactory;
	private Converter<Configuration, CPSExternalConfiguration> externalConfigConverter;
	private CPSContextSupplier contextSupplier;
	private final CPSTimer timer = new CPSTimer();
	private CPSSessionCache sessionCache;
	private I18NService i18NService;

	private CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;

	/**
	 * @return the commerceExternalConfigurationStrategy
	 */
	protected CommerceExternalConfigurationStrategy getCommerceExternalConfigurationStrategy()
	{
		return commerceExternalConfigurationStrategy;
	}

	/**
	 * @return the sessionCache
	 */
	protected CPSSessionCache getSessionCache()
	{
		return sessionCache;
	}

	@Override
	public CPSConfiguration createDefaultConfiguration(final KBKey kbKey)
	{
		try
		{
			final CPSCreateConfigInput cloudEngineConfigurationRequest = assembleCreateDefaultConfigurationRequest(kbKey);

			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create default configuration): ", cloudEngineConfigurationRequest);
			}
			timer.start("createDefaultConfiguration");
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient()
					.createDefaultConfiguration(cloudEngineConfigurationRequest, getI18NService().getCurrentLocale().getLanguage());
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateDefaultConfigurationError(ex);
		}
	}

	protected void traceJsonRequestBody(final String prefix, final Object obj)
	{
		try
		{
			LOG.debug(prefix + getObjectMapper().writeValueAsString(obj));
		}
		catch (final JsonProcessingException e)
		{
			LOG.warn("Could not trace " + prefix, e);
		}
	}



	protected CPSConfiguration retrieveConfigurationAndSaveResponseAttributes(
			final Observable<RawResponse<CPSConfiguration>> rawResponse)
	{
		//Stateful calls are required and this is facilitated via cookies. These cookies can be extracted from the RawResponse.
		final RawResponse<CPSConfiguration> response = rawResponse.subscribeOn(getScheduler()).toBlocking().first();
		final CPSConfiguration responseValue = response.content().subscribeOn(getScheduler()).toBlocking().first();
		getCookieHandler().setCookies(responseValue.getId(), response.getSetCookies());
		handleETag(response, responseValue.getId());

		LOG.info("Created configuration with id: " + responseValue.getId());
		if (LOG.isDebugEnabled())
		{
			traceJsonRequestBody("Output for REST call (create default/from external configuration): ", responseValue);
		}
		return responseValue;
	}

	protected void handleETag(final RawResponse<CPSConfiguration> response, final String configId)
	{
		final Optional<String> eTag = response.eTag();
		if (eTag.isPresent())
		{
			sessionCache.setETag(configId, eTag.get());
		}
	}

	protected CPSCreateConfigInput assembleCreateDefaultConfigurationRequest(final KBKey kbKey)
	{
		final CPSCreateConfigInput cloudEngineConfigurationRequest = new CPSCreateConfigInput();
		cloudEngineConfigurationRequest.setProductKey(kbKey.getProductCode());

		final List<CPSContextInfo> context = getContextSupplier().retrieveContext(kbKey.getProductCode());
		cloudEngineConfigurationRequest.setContext(context);

		return cloudEngineConfigurationRequest;
	}

	protected CookieHandler getCookieHandler()
	{
		return cookieHandler;
	}

	/**
	 * @param cookieHandler
	 *           the cookieHandler to set
	 */
	@Required
	public void setCookieHandler(final CookieHandler cookieHandler)
	{
		this.cookieHandler = cookieHandler;
	}

	protected void updateConfiguration(final String cfgId, final String itemId, final String csticId,
			final CPSCharacteristicInput changes) throws ConfigurationEngineException
	{
		final List<String> cookiesAsString = getCookies(cfgId);

		try
		{
			final String csticIdEncoded = encode(csticId);
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (update configuration): ", changes);
			}
			final String eTag = sessionCache.getETag(cfgId);
			timer.start("updateConfiguration/" + cfgId);
			final RawResponse rawResponse = (RawResponse) getClient()
					.updateConfiguration(changes, cfgId, itemId, csticIdEncoded, cookiesAsString.get(0), cookiesAsString.get(1), eTag)
					.subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();
			handleETag(rawResponse, cfgId);
		}
		catch (final HttpException e)
		{
			getRequestErrorHandler().processUpdateConfigurationError(e);
		}
		catch (final RuntimeException e)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(e);
		}
	}



	protected String encode(final String requestParam)
	{
		if (requestParam == null)
		{
			return null;
		}
		final URLEncoder encoder = new URLEncoder();

		return encoder.encode(requestParam, StandardCharsets.UTF_8);
	}

	protected List<String> getCookies(final String cfgId)
	{
		final List<String> cookiesAsString = getCookieHandler().getCookiesAsString(cfgId);

		//We expect 2 cookies thus we need to check the size of the list that we got
		if (cookiesAsString.size() != 2)
		{
			throw new IllegalStateException("We expect exactly 2 cookies");
		}
		return cookiesAsString;
	}

	@Override
	public CPSConfiguration getConfiguration(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getCookies(configId);
			timer.start("getConfiguration/" + configId);
			final CPSConfiguration config = getClient().getConfiguration(configId, getI18NService().getCurrentLocale().getLanguage(),
					cookiesAsString.get(0), cookiesAsString.get(1)).subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Output for REST call (get configuration): ", config);
			}
			addParentReferences(config);
			return config;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetConfigurationError(ex);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex);
			return null;
		}
	}


	@Override
	public boolean updateConfiguration(final CPSConfiguration configuration) throws ConfigurationEngineException
	{
		final CPSItem rootItem = configuration.getRootItem();
		if (rootItem == null)
		{
			throw new IllegalStateException("Root item not available");
		}

		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		final MutableBoolean updateWasDone = new MutableBoolean(false);
		updateCPSCharacteristic(updateWasDone, rootItem, cfgId, itemId);
		return updateWasDone.booleanValue();
	}

	protected void updateCPSCharacteristic(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String itemId) throws ConfigurationEngineException
	{
		handleUpdateOwnCharacteristics(updateWasDone, item, cfgId, itemId);
		handleUpdateSubItems(updateWasDone, item, cfgId);
	}

	protected void handleUpdateSubItems(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId)
			throws ConfigurationEngineException
	{
		final List<CPSItem> subItems = item.getSubItems();
		if (CollectionUtils.isNotEmpty(subItems))
		{
			for (final CPSItem subItem : subItems)
			{
				updateCPSCharacteristic(updateWasDone, subItem, cfgId, subItem.getId());
			}
		}
	}

	protected void handleUpdateOwnCharacteristics(final MutableBoolean updateWasDone, final CPSItem item, final String cfgId,
			final String itemId) throws ConfigurationEngineException
	{
		final List<CPSCharacteristic> characteristics = item.getCharacteristics();

		for (final CPSCharacteristic characteristic : characteristics)
		{
			final CPSCharacteristicInput characteristicInput = createCharacteristicInput(characteristic);
			//multiple updates: We cannot always prevent this as in some environments, the above layers need to send multiple updates
			//(e.g. if unconstrained cstics are involved).
			//Still we raise a log warning as this can cause undesired conflict situations
			if (updateWasDone.isTrue())
			{
				LOG.warn("Multiple updates detected in one request, characteristic involved: " + characteristic.getId());
			}
			updateConfiguration(cfgId, itemId, characteristic.getId(), characteristicInput);
			updateWasDone.setValue(true);
		}
	}


	protected CPSCharacteristicInput createCharacteristicInput(final CPSCharacteristic characteristic)
	{
		final CPSCharacteristicInput characteristicInput = new CPSCharacteristicInput();
		characteristicInput.setValues(new ArrayList<>());
		for (final CPSValue value : characteristic.getValues())
		{
			final CPSValueInput valueInput = new CPSValueInput();
			valueInput.setValue(value.getValue());
			valueInput.setSelected(value.isSelected());
			characteristicInput.getValues().add(valueInput);
		}
		return characteristicInput;
	}


	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}


	/**
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	@Required
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}


	protected ConfigurationClientBase getClient()
	{
		if (clientSetExternally != null)
		{
			return clientSetExternally;
		}
		else
		{
			return yaasServiceFactory.lookupService(ConfigurationClient.class);
		}
	}


	/**
	 * Sets charon client from outside. Only used in test environments
	 *
	 * @param newClient
	 *           Charon client representing REST calls for product configuration.
	 */
	public void setClient(final ConfigurationClientBase newClient)
	{
		clientSetExternally = newClient;
	}


	@Override
	public String getExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		try
		{
			final CPSExternalConfiguration externalConfigStructured = placeGetExternalConfigurationRequest(configId);

			final CPSCommerceExternalConfiguration commerceExternalConfiguration = getCommerceExternalConfigurationStrategy()
					.createCommerceFormatFromCPSRepresentation(externalConfigStructured);

			final String extConfig = getObjectMapper().writeValueAsString(commerceExternalConfiguration);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Output for REST call (get ext configuration): " + extConfig);
			}
			return extConfig;

		}
		catch (final JsonProcessingException e)
		{
			throw new IllegalStateException("External configuration from client cannot be parsed to string", e);
		}
	}

	protected CPSExternalConfiguration placeGetExternalConfigurationRequest(final String configId)
			throws ConfigurationEngineException
	{
		try
		{
			final List<String> cookiesAsString = getCookies(configId);
			timer.start("getExternalConfiguration/" + configId);
			final CPSExternalConfiguration extConfig = getClient()
					.getExternalConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1)).subscribeOn(getScheduler())
					.toBlocking().first();
			timer.stop();
			return extConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processGetExternalConfigurationError(ex);
		}
		catch (final RuntimeException ex)
		{
			getRequestErrorHandler().processConfigurationRuntimeException(ex);
			return null;
		}
	}


	@Override
	public CPSConfiguration createConfigurationFromExternal(final String externalConfiguration)
	{
		final CPSExternalConfiguration externalConfigStructured = convertFromStringToStructured(externalConfiguration);
		return createConfigurationFromExternal(externalConfigStructured);
	}


	@Override
	public CPSConfiguration createConfigurationFromExternal(final CPSExternalConfiguration externalConfigStructured)
	{
		final List<CPSContextInfo> context = getContextSupplier()
				.retrieveContext(externalConfigStructured.getRootItem().getObjectKey().getId());
		externalConfigStructured.setContext(context);

		try
		{
			if (LOG.isDebugEnabled())
			{
				traceJsonRequestBody("Input for REST call (create form external configuration): ", externalConfigStructured);
			}
			timer.start("createConfigurationFromExternal");
			final Observable<RawResponse<CPSConfiguration>> rawResponse = getClient()
					.createRuntimeConfigurationFromExternal(externalConfigStructured);
			final CPSConfiguration cpsConfig = retrieveConfigurationAndSaveResponseAttributes(rawResponse);
			timer.stop();
			addParentReferences(cpsConfig);
			return cpsConfig;
		}
		catch (final HttpException ex)
		{
			return getRequestErrorHandler().processCreateRuntimeConfigurationFromExternalError(ex);
		}
	}

	protected CPSExternalConfiguration convertFromStringToStructured(final String externalConfiguration)
	{
		CPSCommerceExternalConfiguration externalConfigStructuredCommerceFormat;
		try
		{
			externalConfigStructuredCommerceFormat = getObjectMapper().readValue(externalConfiguration,
					CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing from JSON failed", e);

		}
		return getCommerceExternalConfigurationStrategy()
				.extractCPSFormatFromCommerceRepresentation(externalConfigStructuredCommerceFormat);
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}


	@Override
	public void releaseSession(final String configId)
	{
		final List<String> cookiesAsString = getCookies(configId);
		final String eTag = sessionCache.getETag(configId);
		try
		{
			timer.start("releaseSession/" + configId);
			getClient().deleteConfiguration(configId, cookiesAsString.get(0), cookiesAsString.get(1), eTag)
					.subscribeOn(getScheduler()).toBlocking().first();
			timer.stop();
		}
		catch (final HttpException ex)
		{
			getRequestErrorHandler().processDeleteConfigurationError(ex);
		}
		finally
		{
			getCookieHandler().removeCookies(configId);
			//do not remove pricing document input as it is needed in order entry overview case
			getSessionCache().removePricingDocumentResult(configId);
		}
	}


	protected Scheduler getScheduler()
	{
		return scheduler;
	}

	protected YaasServiceFactory getYaasServiceFactory()
	{
		return yaasServiceFactory;
	}

	/**
	 * @param yaasServiceFactory
	 *           the YaasServiceFactory to set
	 */
	@Required
	public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory)
	{
		this.yaasServiceFactory = yaasServiceFactory;
	}

	/**
	 * @return the externalConfigConverter
	 */
	protected Converter<Configuration, CPSExternalConfiguration> getExternalConfigConverter()
	{
		return externalConfigConverter;
	}

	/**
	 * @param externalConfigConverter
	 *           the externalConfigConverter to set
	 */
	@Required
	public void setExternalConfigConverter(final Converter<Configuration, CPSExternalConfiguration> externalConfigConverter)
	{
		this.externalConfigConverter = externalConfigConverter;
	}

	/**
	 * @return the contextSupplier
	 */
	protected CPSContextSupplier getContextSupplier()
	{
		return contextSupplier;
	}

	/**
	 * @param contextSupplier
	 *           the contextSupplier to set
	 */
	public void setContextSupplier(final CPSContextSupplier contextSupplier)
	{
		this.contextSupplier = contextSupplier;
	}

	@Override
	public CPSConfiguration createConfigurationFromExternal(final Configuration externalConfiguration, final Integer kbid)
	{
		final CPSExternalConfiguration externalConfigStructured = getExternalConfigConverter().convert(externalConfiguration);
		externalConfigStructured.setKbId(String.valueOf(kbid));

		return createConfigurationFromExternal(externalConfigStructured);

	}

	/**
	 * @param cpsSessionCache
	 */
	public void setSessionCache(final CPSSessionCache cpsSessionCache)
	{
		this.sessionCache = cpsSessionCache;

	}

	/**
	 * @param commerceExternalConfigurationStrategy
	 */
	public void setCommerceExternalConfigurationStrategy(
			final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy)
	{
		this.commerceExternalConfigurationStrategy = commerceExternalConfigurationStrategy;

	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected void addParentReferences(final CPSConfiguration cpsConfig)
	{
		final CPSItem rootItem = cpsConfig.getRootItem();
		rootItem.setParentConfiguration(cpsConfig);
		addParentReferencesForSubItems(rootItem);
		addParentReferencesForCharacteristics(rootItem);
		addParentReferencesForCharacteristicGroups(rootItem);
	}

	protected void addParentReferencesForSubItems(final CPSItem item)
	{
		final List<CPSItem> subItems = item.getSubItems();
		if (subItems != null)
		{
			for (final CPSItem subItem : subItems)
			{
				subItem.setParentConfiguration(item.getParentConfiguration());
				subItem.setParentItem(item);
				addParentReferencesForCharacteristics(subItem);
				addParentReferencesForCharacteristicGroups(subItem);
				addParentReferencesForSubItems(subItem);
			}
		}
	}

	protected void addParentReferencesForCharacteristics(final CPSItem item)
	{
		final List<CPSCharacteristic> characteristics = item.getCharacteristics();
		if (characteristics != null)
		{
			for (final CPSCharacteristic characteristic : characteristics)
			{
				characteristic.setParentItem(item);
				addParentReferencesForCharacteristicValues(characteristic);
			}
		}
	}

	protected void addParentReferencesForCharacteristicGroups(final CPSItem item)
	{
		final List<CPSCharacteristicGroup> characteristicGroups = item.getCharacteristicGroups();
		if (characteristicGroups != null)
		{
			for (final CPSCharacteristicGroup characteristicGroup : characteristicGroups)
			{
				characteristicGroup.setParentItem(item);
			}
		}
	}

	protected void addParentReferencesForCharacteristicValues(final CPSCharacteristic characteristic)
	{
		final List<CPSValue> values = characteristic.getValues();
		final List<CPSPossibleValue> possibleValues = characteristic.getPossibleValues();
		if (values != null)
		{
			for (final CPSValue value : values)
			{
				value.setParentCharacteristic(characteristic);
			}
		}
		if (possibleValues != null)
		{
			for (final CPSPossibleValue possibleValue : possibleValues)
			{
				possibleValue.setParentCharacteristic(characteristic);
			}
		}
	}

}
