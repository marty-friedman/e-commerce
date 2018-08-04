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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.ConfigurationClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.cps.session.impl.CookieHandlerImpl;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl.CommerceExternalConfigurationStrategyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.NewCookie;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class CharonFacadeImplTest
{
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String EXTERNAL_CONFIG_STRING = "external configuration string";
	private static final Integer kbId = Integer.valueOf(1234);
	CharonFacadeImpl classUnderTest;
	private CookieHandlerImpl cookieHandler;
	private final CPSConfiguration configuration = new CPSConfiguration();
	private final CPSItem rootItem = new CPSItem();
	private final CPSCharacteristicGroup group = new CPSCharacteristicGroup();
	private final CPSCharacteristic characteristic = new CPSCharacteristic();
	private final CPSCharacteristic characteristic2 = new CPSCharacteristic();
	private final List<NewCookie> responseCookies = new ArrayList<NewCookie>();
	private final CPSExternalConfiguration externalConfiguration = new CPSExternalConfiguration();
	private final CPSCommerceExternalConfiguration externalConfigurationCommerceFormat = new CPSCommerceExternalConfiguration();
	private final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy = new CommerceExternalConfigurationStrategyImpl();

	@Mock
	private NewCookie cookie;

	@Mock
	private ConfigurationClient client;

	@Mock
	private YaasServiceFactory yaasServiceFactory;

	@Mock
	private I18NService i18NService;

	private static final String cookieName = "CookieName";
	private static final String cookieValue = "CookieValue";
	private static final String cookieAsString = "CookieName=CookieValue";
	private static final String cfgId = "99";
	private static final String subItemCsticValue = "SubItemCsticValue";
	private static final String subItemCstic = "SubItemCstic";
	private static final String lang = Locale.ENGLISH.getLanguage();
	private static final String eTag = "\"TheEtag\"";

	private final RequestErrorHandler errorHandler = new RequestErrorHandlerImpl();
	private String itemId;
	private String groupId;
	private String csticId;
	private String csticId2;
	private CPSCharacteristicInput changes;
	@Mock
	private ObjectMapper objectMapperMock;
	@Mock
	private Observable<RawResponse<CPSConfiguration>> rawResponse;
	private Observable<String> emptyResponseObservable;
	private Observable<RawResponse> eTagRawResponseObservable;
	@Mock
	private RawResponse<String> eTagRawResponse ;
	private Optional<String> optinalETag = Optional.of(eTag) ;
	@Mock
	private Converter<Configuration, CPSExternalConfiguration> externalConfigConverter;

	@Mock
	private CPSContextSupplier contextSupplier;
	private Configuration extConfigurationTypedFormat;

	@Mock
	private CPSSessionCache cpsSessionCache;
	private final List<String> cookieList = new ArrayList<>();

	@Mock
	private RuntimeException runtimeExceptionWrappingTimeout;

	@Mock
	private TimeoutException timeoutException;

	@Mock
	private Throwable runtimeExceptionWrappingNPE;

	@SuppressWarnings("unchecked")
	@Before
	public void initialize() throws ConfigurationEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = Mockito.spy(new CharonFacadeImpl());
		cookieHandler = Mockito.spy(new CookieHandlerImpl());
		classUnderTest.setRequestErrorHandler(errorHandler);
		classUnderTest.setYaasServiceFactory(yaasServiceFactory);
		classUnderTest.setExternalConfigConverter(externalConfigConverter);

		classUnderTest.setContextSupplier(contextSupplier);
		classUnderTest.setI18NService(i18NService);
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);

		Mockito.when(cookie.getName()).thenReturn(cookieName);
		Mockito.when(cookie.getValue()).thenReturn(cookieValue);
		Mockito.when(yaasServiceFactory.lookupService(ConfigurationClient.class)).thenReturn(client);

		Mockito.when(runtimeExceptionWrappingTimeout.getCause()).thenReturn(timeoutException);

		cookieList.add(cookieAsString);
		cookieList.add(cookieAsString);

		Mockito.when(cpsSessionCache.getCookies(Mockito.anyString())).thenReturn(cookieList);
		Mockito.when(cpsSessionCache.getETag(Mockito.anyString())).thenReturn(eTag);
		//2 cookies sufficient, we don't care that their content is the same
		responseCookies.add(cookie);
		responseCookies.add(cookie);
		classUnderTest.setCookieHandler(cookieHandler);
		configuration.setId(cfgId);
		cookieHandler.setCPSSessionCache(cpsSessionCache);
		classUnderTest.setSessionCache(cpsSessionCache);
		cookieHandler.setCookies(cfgId, responseCookies);
		itemId = "1";
		groupId = "Group";
		csticId = "Cstic";
		csticId2 = "Cstic2";
		changes = new CPSCharacteristicInput();
		changes.setValues(new ArrayList<>());

		configuration.setRootItem(rootItem);
		rootItem.setId(itemId);
		rootItem.setCharacteristicGroups(new ArrayList<>());
		rootItem.setSubItems(new ArrayList<>());
		rootItem.setCharacteristics(new ArrayList<CPSCharacteristic>());

		group.setId(groupId);
		addRuntimeCsticGroup(rootItem, group);
		characteristic.setId(csticId);
		characteristic.setPossibleValues(new ArrayList<>());
		characteristic.setValues(new ArrayList<>());
		characteristic2.setId(csticId2);
		characteristic2.setPossibleValues(new ArrayList<>());
		characteristic2.setValues(new ArrayList<>());
		addRuntimeCstic(rootItem, characteristic);

		externalConfiguration.setComplete(false);
		externalConfiguration.setConsistent(true);

		final CPSExternalObjectKey objKey = new CPSExternalObjectKey();
		objKey.setId("PRODUCTCODE");
		final CPSExternalItem extRootItem = new CPSExternalItem();
		extRootItem.setObjectKey(objKey);
		externalConfiguration.setRootItem(extRootItem);

		Mockito.doReturn(configuration).when(classUnderTest).retrieveConfigurationAndSaveResponseAttributes(rawResponse);

		emptyResponseObservable = Observable.from(Arrays.asList("Hello"));		
		eTagRawResponseObservable = Observable.from(Arrays.asList(eTagRawResponse));
		Mockito.when(eTagRawResponse.eTag()).thenReturn(optinalETag);
		Mockito.when(client.updateConfiguration(changes, cfgId, itemId, csticId, cookieAsString, cookieAsString, eTag))
				.thenReturn(eTagRawResponseObservable);
		Mockito.when(client.deleteConfiguration(cfgId, cookieAsString, cookieAsString, eTag)).thenReturn(emptyResponseObservable);

		extConfigurationTypedFormat = new ConfigurationImpl();
		Mockito.when(externalConfigConverter.convert(extConfigurationTypedFormat)).thenReturn(externalConfiguration);
		Mockito.when(client.createRuntimeConfigurationFromExternal(externalConfiguration)).thenReturn(rawResponse);

		classUnderTest.setCommerceExternalConfigurationStrategy(commerceExternalConfigurationStrategy);
		externalConfigurationCommerceFormat.setExternalConfiguration(externalConfiguration);
	}

	protected CPSItem createCPSItem(final String itemId)
	{
		final CPSItem subItem = new CPSItem();
		subItem.setId(itemId);
		subItem.setSubItems(new ArrayList<>());
		final List<CPSCharacteristic> characteristics = new ArrayList<>();
		characteristics.add(createCPSCharacteristic());
		subItem.setCharacteristics(characteristics);
		subItem.setCharacteristicGroups(new ArrayList<>());
		subItem.getCharacteristicGroups().add(new CPSCharacteristicGroup());
		return subItem;
	}

	protected CPSCharacteristic createCPSCharacteristic()
	{
		final CPSCharacteristic characteristic = new CPSCharacteristic();
		characteristic.setId(subItemCstic);
		characteristic.setValues(createListOfCPSValues(characteristic));
		characteristic.setPossibleValues(createListOfPossibleValues());
		return characteristic;
	}

	protected List<CPSPossibleValue> createListOfPossibleValues()
	{
		final List<CPSPossibleValue> possibleValues = new ArrayList<>();
		possibleValues.add(new CPSPossibleValue());
		return possibleValues;
	}

	protected List<CPSValue> createListOfCPSValues(final CPSCharacteristic characteristic)
	{
		final List<CPSValue> values = new ArrayList<>();
		values.add(createCPSValue(characteristic, subItemCsticValue));
		return values;
	}

	protected CPSValue createCPSValue(final CPSCharacteristic characteristic, final String valueName)
	{
		final CPSValue value = new CPSValue();
		value.setValue(valueName);
		return value;
	}

	protected void mockClientCallWithCstic(final String cfgId, final String itemId, final String csticIdentifier)
	{
		
		Mockito.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticIdentifier),
				Mockito.eq(cookieAsString), Mockito.eq(cookieAsString), Mockito.eq(eTag))).thenReturn(eTagRawResponseObservable);

		
	}

	protected void verifyClientCallWithCstic(final String cfgId, final String itemId, final String csticIdentifier,
			final boolean isExpected)
	{
		final VerificationMode mode = isExpected ? Mockito.atLeastOnce() : Mockito.never();
		Mockito.verify(client, mode).updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId),
				Mockito.eq(csticIdentifier), Mockito.eq(cookieAsString), Mockito.eq(cookieAsString), Mockito.eq(eTag));
	}

	protected void addRuntimeCsticGroup(final CPSItem item, final CPSCharacteristicGroup characteristicGroup)
	{
		if (characteristicGroup == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null CharacteristicGroup to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticGroupPresent(item, characteristicGroup.getId()))
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add CharacteristicGroup with already existing id ")
							.append(characteristicGroup.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristicGroups().add(characteristicGroup);
	}

	protected boolean isRuntimeCsticGroupPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristicGroup group : item.getCharacteristicGroups())
		{
			if (group.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isRuntimeCsticPresent(final CPSItem item, final String id)
	{
		for (final CPSCharacteristic characteristic : item.getCharacteristics())
		{
			if (characteristic.getId().equals(id))
			{
				return true;
			}
		}
		return false;
	}

	protected void addRuntimeCstic(final CPSItem item, final CPSCharacteristic characteristic)
	{
		if (characteristic == null)
		{
			throw new IllegalArgumentException(
					new StringBuilder().append("tried to add null Characteristic to Item ").append(item.getId()).toString());
		}
		if (isRuntimeCsticPresent(item, characteristic.getId()))
		{
			throw new IllegalArgumentException(new StringBuilder().append("tried to add Characteristic with already existing id ")
					.append(characteristic.getId()).append(" to Item ").append(item.getId()).toString());
		}
		item.getCharacteristics().add(characteristic);
	}

	@Test
	public void testAssembleCreateDefaultConfigurationRequest()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final CPSCreateConfigInput result = classUnderTest.assembleCreateDefaultConfigurationRequest(kbKey);
		Mockito.verify(contextSupplier).retrieveContext(kbKey.getProductCode());
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductKey());
	}

	@Test
	public void testCookieHandler()
	{
		assertEquals(cookieHandler, classUnderTest.getCookieHandler());
	}

	@Test
	public void testUpdateConfigurationSingleCstic() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		classUnderTest.updateConfiguration(cfgId, itemId, csticId, changes);
		Mockito.verify(client).updateConfiguration(changes, cfgId, itemId, csticId, cookieAsString, cookieAsString, eTag);
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConfigurationSingleCsticWrongNumberOFCookies() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		cookieList.remove(1);
		Mockito.when(cpsSessionCache.getCookies(Mockito.anyString())).thenReturn(cookieList);
		classUnderTest.updateConfiguration(cfgId, itemId, csticId, changes);
	}

	@Test
	public void testCreateCharacteristicInput()
	{
		final CPSValue value = new CPSValue();
		value.setValue("value");
		value.setSelected(true);
		characteristic.getValues().add(value);
		final CPSCharacteristicInput result = classUnderTest.createCharacteristicInput(characteristic);
		assertNotNull(result);
		assertNotNull(result.getValues());
		assertEquals(1, result.getValues().size());
	}

	@Test
	public void testUpdateConfiguration() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		mockClientCallWithCstic(cfgId, itemId, csticId);
		
		classUnderTest.setClient(client);
		assertTrue(classUnderTest.updateConfiguration(configuration));
		verifyClientCallWithCstic(cfgId, itemId, csticId, true);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testUpdateConfigurationTimeOutExceptionHappens() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		Mockito
				.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticId),
						Mockito.eq(cookieAsString), Mockito.eq(cookieAsString), Mockito.eq(eTag)))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.setClient(client);
		classUnderTest.updateConfiguration(configuration);
	}

	@Test(expected = RuntimeException.class)
	public void testUpdateConfigurationRuntimeExceptionHappens() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		Mockito
				.when(client.updateConfiguration(Mockito.any(), Mockito.eq(cfgId), Mockito.eq(itemId), Mockito.eq(csticId),
						Mockito.eq(cookieAsString), Mockito.eq(cookieAsString), Mockito.eq(eTag)))
				.thenThrow(runtimeExceptionWrappingNPE);
		classUnderTest.setClient(client);
		classUnderTest.updateConfiguration(configuration);
	}

	@Test
	public void testUpdateConfigurationMultipleChanges() throws ConfigurationEngineException
	{
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		//add another changed cstic
		addRuntimeCstic(rootItem, characteristic2);
		mockClientCallWithCstic(cfgId, itemId, csticId);
		mockClientCallWithCstic(cfgId, itemId, csticId2);
		classUnderTest.setClient(client);
		assertTrue(classUnderTest.updateConfiguration(configuration));
		verifyClientCallWithCstic(cfgId, itemId, csticId, true);
		verifyClientCallWithCstic(cfgId, itemId, csticId2, true);
	}

	@Test
	public void testUpdateConfigurationNoUpdatePerformed() throws ConfigurationEngineException
	{
		configuration.getRootItem().setCharacteristics(Collections.emptyList());
		assertFalse(classUnderTest.updateConfiguration(configuration));
	}

	@Test
	public void testUpdateCPSCharacteristicForSinglelevel() throws ConfigurationEngineException
	{
		final MutableBoolean updateWasPerformed = new MutableBoolean(false);
		final CPSItem rootItem = configuration.getRootItem();
		final String cfgId = configuration.getId();
		final String itemId = rootItem.getId();

		mockClientCallWithCstic(cfgId, itemId, csticId);
		classUnderTest.setClient(client);
		classUnderTest.updateCPSCharacteristic(updateWasPerformed, rootItem, cfgId, itemId);
		assertTrue(updateWasPerformed.isTrue());
		verifyClientCallWithCstic(cfgId, itemId, csticId, true);
	}

	@Test
	public void testUpdateCPSCharacteristicForMultilevel() throws ConfigurationEngineException
	{
		final List<CPSItem> subItems = new ArrayList<>();
		subItems.add(createCPSItem("4"));
		rootItem.setSubItems(subItems);

		final MutableBoolean updateWasPerformed = new MutableBoolean(false);
		final CPSItem subItem = configuration.getRootItem().getSubItems().get(0);
		final String cfgId = configuration.getId();
		final String subitemId = subItem.getId();

		mockClientCallWithCstic(cfgId, subitemId, subItemCstic);
		classUnderTest.setClient(client);
		classUnderTest.updateCPSCharacteristic(updateWasPerformed, subItem, cfgId, subitemId);
		assertTrue(updateWasPerformed.isTrue());
		verifyClientCallWithCstic(cfgId, subitemId, subItemCstic, true);
	}

	@Test
	public void testGetConfiguration() throws ConfigurationEngineException
	{
		final Observable<CPSConfiguration> configObs = Observable.from(Arrays.asList(configuration));
		Mockito.when(client.getConfiguration(cfgId, lang, cookieAsString, cookieAsString)).thenReturn(configObs);
		classUnderTest.setClient(client);
		classUnderTest.getConfiguration(cfgId);
		Mockito.verify(client).getConfiguration(cfgId, lang, cookieAsString, cookieAsString);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testGetConfigurationTimeOutExceptionHappens() throws ConfigurationEngineException
	{
		Mockito.when(client.getConfiguration(cfgId, lang, cookieAsString, cookieAsString))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.setClient(client);
		classUnderTest.getConfiguration(cfgId);
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final ConfigurationClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testUpdateErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).updateConfiguration(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any());
		classUnderTest.updateConfiguration(cfgId, null, csticId, new CPSCharacteristicInput());
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateDefaultErrorHandlerCalled()
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createDefaultConfiguration(Mockito.any(), Mockito.any());
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		classUnderTest.createDefaultConfiguration(kbKey);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testGetErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).getConfiguration(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		classUnderTest.getConfiguration(cfgId);
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testGetExternalErrorHandlerCalled() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).getExternalConfiguration(Mockito.any(), Mockito.any(), Mockito.any());
		classUnderTest.getExternalConfiguration(cfgId);
	}

	@Test(expected = IllegalStateException.class)
	public void testDeleteErrorHandlerCalled()
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).deleteConfiguration(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		classUnderTest.releaseSession(cfgId);
		Mockito.verify(errorHandler).processDeleteConfigurationError(ex);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationFromExternalErrorHandlerCalled()
			throws JsonParseException, JsonMappingException, IOException
	{
		Mockito.when(objectMapperMock.readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class))
				.thenReturn(externalConfigurationCommerceFormat);
		classUnderTest.setObjectMapper(objectMapperMock);
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createRuntimeConfigurationFromExternal(Mockito.any());
		classUnderTest.createConfigurationFromExternal(EXTERNAL_CONFIG_STRING);
	}

	@Test
	public void testEncodeNull()
	{
		assertNull(classUnderTest.encode(null));
	}

	@Test
	public void testEncode()
	{
		assertEquals("%5BGEN%5D", classUnderTest.encode("[GEN]"));
	}

	@Test
	public void testGetObjectMapper()
	{
		classUnderTest.setObjectMapper(null);
		assertNotNull(classUnderTest.getObjectMapper());
	}

	@Test
	public void testGetExternalConfiguration() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		final Observable<CPSExternalConfiguration> externalConfigObs = Observable.from(Arrays.asList(externalConfiguration));
		Mockito.when(client.getExternalConfiguration(cfgId, cookieAsString, cookieAsString)).thenReturn(externalConfigObs);
		final String result = classUnderTest.getExternalConfiguration(cfgId);
		assertNotNull(result);
		assertTrue(result.contains("complete"));
		assertTrue(result.contains("consistent"));
		assertTrue(result.contains("rootItem"));
		assertFalse(result.contains("non existing field"));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}

	@Test(expected = ConfigurationEngineException.class)
	public void testGetExternalConfigurationTimeOut() throws ConfigurationEngineException
	{
		classUnderTest.setClient(client);
		Mockito.when(client.getExternalConfiguration(cfgId, cookieAsString, cookieAsString))
				.thenThrow(runtimeExceptionWrappingTimeout);
		classUnderTest.getExternalConfiguration(cfgId);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetExternalConfiguration_invalidResponse() throws JsonProcessingException, ConfigurationEngineException
	{
		final Observable<CPSExternalConfiguration> externalConfigObs = Observable.from(Arrays.asList(externalConfiguration));
		Mockito.when(client.getExternalConfiguration(cfgId, cookieAsString, cookieAsString)).thenReturn(externalConfigObs);
		Mockito.when(objectMapperMock.writeValueAsString(Mockito.any()))
				.thenThrow(new InvalidFormatException("message", externalConfiguration, CPSExternalConfiguration.class));
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		classUnderTest.getExternalConfiguration(cfgId);
	}

	@Test
	public void testCreateConfigurationFromExternal() throws JsonParseException, JsonMappingException, IOException
	{
		Mockito.when(objectMapperMock.readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class))
				.thenReturn(externalConfigurationCommerceFormat);
		Mockito.when(client.createRuntimeConfigurationFromExternal(externalConfiguration)).thenReturn(rawResponse);
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(EXTERNAL_CONFIG_STRING);
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(objectMapperMock).readValue(EXTERNAL_CONFIG_STRING, CPSCommerceExternalConfiguration.class);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(externalConfiguration);
	}


	@Test
	public void testCreateConfigurationFromExternal_somKbId()
	{
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(extConfigurationTypedFormat, kbId);
		checkResultAndVerifyMocks(result);
	}

	protected void checkResultAndVerifyMocks(final CPSConfiguration result)
	{
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(externalConfiguration);
	}

	@Test
	public void testReleaseSession()
	{
		classUnderTest.setClient(client);
		classUnderTest.releaseSession(cfgId);
		Mockito.verify(client).deleteConfiguration(cfgId, cookieAsString, cookieAsString, eTag);
		Mockito.verify(cookieHandler).removeCookies(cfgId);
	}

	@Test
	public void testSessionCache()
	{
		classUnderTest.setSessionCache(cpsSessionCache);
		assertEquals(cpsSessionCache, classUnderTest.getSessionCache());
	}

	@Test
	public void testExtConfigurationStrategy()
	{
		assertEquals(commerceExternalConfigurationStrategy, classUnderTest.getCommerceExternalConfigurationStrategy());
	}

	@Test
	public void testCreateConfigurationFromExternalTyped() throws JsonParseException, JsonMappingException, IOException
	{
		Mockito.when(client.createRuntimeConfigurationFromExternal(externalConfiguration)).thenReturn(rawResponse);
		classUnderTest.setClient(client);
		classUnderTest.setObjectMapper(objectMapperMock);
		final CPSConfiguration result = classUnderTest.createConfigurationFromExternal(externalConfiguration);
		assertNotNull(result);
		assertEquals(configuration, result);
		Mockito.verify(client).createRuntimeConfigurationFromExternal(externalConfiguration);

	}

	@Test
	public void testAddParentReferences()
	{
		assertNull("Root item should have no reference to parentconfiguration initially",
				configuration.getRootItem().getParentConfiguration());
		classUnderTest.addParentReferences(configuration);
		assertEquals(configuration, configuration.getRootItem().getParentConfiguration());
	}

	@Test
	public void testAddParentReferencesForSubItems()
	{
		rootItem.setParentConfiguration(configuration);
		final CPSItem subItem = createCPSItem("item id");
		rootItem.getSubItems().add(subItem);
		assertNull(subItem.getParentConfiguration());
		assertNull(subItem.getParentItem());

		final CPSItem subSubItem = createCPSItem("subItem id");
		subItem.getSubItems().add(subSubItem);
		assertNull(subSubItem.getParentConfiguration());
		assertNull(subSubItem.getParentItem());

		classUnderTest.addParentReferencesForSubItems(rootItem);
		assertEquals(configuration, subItem.getParentConfiguration());
		assertEquals(rootItem, subItem.getParentItem());

		assertEquals(configuration, subSubItem.getParentConfiguration());
		assertEquals(subItem, subSubItem.getParentItem());
	}

	@Test
	public void testAddParentReferencesForCharacteristics()
	{
		final CPSItem item = createCPSItem("item id");
		final CPSCharacteristic cstic = item.getCharacteristics().get(0);
		assertNull(cstic.getParentItem());

		classUnderTest.addParentReferencesForCharacteristics(item);
		assertEquals(item, cstic.getParentItem());

	}

	@Test
	public void testAddParentReferencesForCharacteristicGroups()
	{
		final CPSItem item = createCPSItem("item id");
		final CPSCharacteristicGroup csticGroup = item.getCharacteristicGroups().get(0);
		assertNull(csticGroup.getParentItem());

		classUnderTest.addParentReferencesForCharacteristicGroups(item);
		assertEquals(item, csticGroup.getParentItem());

	}

	@Test
	public void testAddParentReferencesForCharacteristicValues()
	{
		final CPSCharacteristic cstic = createCPSCharacteristic();
		final CPSPossibleValue possibleValue = cstic.getPossibleValues().get(0);
		assertNull(possibleValue.getParentCharacteristic());
		final CPSValue value = cstic.getValues().get(0);
		assertNull(value.getParentCharacteristic());

		classUnderTest.addParentReferencesForCharacteristicValues(cstic);
		assertEquals(cstic, possibleValue.getParentCharacteristic());
		assertEquals(cstic, value.getParentCharacteristic());
	}

	@Test
	public void testAddParentReferences_FullHierarchy()
	{
		final CPSItem rootItem = configuration.getRootItem();
		rootItem.getSubItems().add(createCPSItem("subItem id"));
		assertNull("Root item should have no reference to parentconfiguration initially", rootItem.getParentConfiguration());
		final CPSItem subItem = rootItem.getSubItems().get(0);
		classUnderTest.addParentReferences(configuration);
		assertEquals(configuration, rootItem.getParentConfiguration());
		assertEquals(rootItem, rootItem.getCharacteristicGroups().get(0).getParentItem());

		assertEquals(rootItem, subItem.getParentItem());
		assertEquals(configuration, subItem.getParentConfiguration());
		assertEquals(subItem, subItem.getCharacteristicGroups().get(0).getParentItem());
		assertEquals(subItem, subItem.getCharacteristics().get(0).getParentItem());
		final CPSCharacteristic subCharacteristic = subItem.getCharacteristics().get(0);
		assertEquals(subItem, subCharacteristic.getParentItem());
		assertEquals(subCharacteristic, subCharacteristic.getPossibleValues().get(0).getParentCharacteristic());
		assertEquals(subCharacteristic, subCharacteristic.getValues().get(0).getParentCharacteristic());
	}
}
