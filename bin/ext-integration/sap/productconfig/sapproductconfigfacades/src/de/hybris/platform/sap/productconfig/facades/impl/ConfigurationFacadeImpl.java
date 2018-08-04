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
package de.hybris.platform.sap.productconfig.facades.impl;


import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationFacade}.<br>
 */
public class ConfigurationFacadeImpl extends ConfigurationBaseFacadeImpl implements ConfigurationFacade
{
	private static final Logger LOG = Logger.getLogger(ConfigurationFacadeImpl.class);


	private ConfigConsistenceChecker configConsistenceChecker;
	private boolean conflictGroupProcessing = true;

	/**
	 * This setting is active per default but can be deactivated to ease an upgrade from previous versions.
	 *
	 * @return Are we processing conflict groups (which have been introduced in 6.0)?
	 */
	public boolean isConflictGroupProcessing()
	{
		return conflictGroupProcessing;
	}


	/**
	 * @param configConsistenceChecker
	 *           injects the consistency checker
	 */
	@Required
	public void setConfigConsistenceChecker(final ConfigConsistenceChecker configConsistenceChecker)
	{
		this.configConsistenceChecker = configConsistenceChecker;
	}

	@Override
	public ConfigurationData getConfiguration(final ConfigurationData configData)
	{
		long startTime = 0;
		final String configId = configData.getConfigId();
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			final KBKeyData kbKey = configData.getKbKey();
			String productCode = null;
			if (kbKey != null)
			{
				productCode = kbKey.getProductCode();
			}
			LOG.debug("get configuration by configId [CONFIG_ID='" + configId + "';PRODUCT_CODE='" + productCode + "']");
		}

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		populateConfigDataFromModel(configData, configModel);
		getConfigConsistenceChecker().checkConfiguration(configData);


		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("GET CONFIG in FACADE took " + duration + " ms");
		}
		return configData;
	}





	@Override
	public ConfigurationData getConfiguration(final ProductData productData)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}
		final String baseProductCode = productData.getBaseProduct();
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productData.getCode());

		if (StringUtils.isEmpty(baseProductCode))
		{
			return getConfiguration(kbKey);
		}

		final ConfigModel configModel = getConfigurationService().createConfigurationForVariant(baseProductCode,
				productData.getCode());

		kbKey.setProductCode(baseProductCode);

		final ConfigurationData configData = convert(kbKey, configModel);

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("GET CONFIG FOR VARIANT in FACADE took " + duration + " ms");
		}
		return configData;
	}

	@Override
	public ConfigurationData getConfiguration(final KBKeyData kbKey)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug("get configuration by kbkey [PRODUCT_CODE='" + kbKey.getProductCode() + "']");
		}

		final ConfigModel configModel = getConfigurationService().createDefaultConfiguration(
				new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()));

		final ConfigurationData configData = convert(kbKey, configModel);
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("GET CONFIG in FACADE took " + duration + " ms");
		}
		return configData;
	}

	@Override
	protected ConfigurationData convert(final KBKeyData kbKey, final ConfigModel configModel)
	{
		final ConfigurationData config = super.convert(kbKey, configModel);
		getConfigConsistenceChecker().checkConfiguration(config);

		return config;
	}

	@Override
	public void updateConfiguration(final ConfigurationData configContent)
	{
		long startTime = 0;
		final String configId = configContent.getConfigId();
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug("update configuration [CONFIG_ID='" + configId + "';PRODUCT_CODE='" + configContent.getKbKey().getProductCode()
					+ "']");
		}

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		final PricingData pricingData = getConfigPricing().getPricingData(configModel);
		configContent.setPricing(pricingData);

		final InstanceModel rootInstance = configModel.getRootInstance();

		if (configContent.getGroups() != null)
		{
			for (final UiGroupData uiGroup : configContent.getGroups())
			{
				updateUiGroup(rootInstance, uiGroup);
			}
		}

		getConfigurationService().updateConfiguration(configModel);
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("UPDATE in FACADE took " + duration + " ms");
		}
	}


	protected void updateUiGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{

		final GroupType groupType = uiGroup.getGroupType() != null ? uiGroup.getGroupType() : GroupType.INSTANCE;

		switch (groupType)
		{
			case CSTIC_GROUP:
				// cstic group
				updateCsticGroup(instance, uiGroup);
				break;
			case INSTANCE:
				// (sub)instance
				updateSubInstances(instance, uiGroup);
				break;
			case CONFLICT:
				updateConflictGroup(instance, uiGroup);
				break;
			case CONFLICT_HEADER:
				updateConflictHeader(instance, uiGroup);
				break;
			default:
				throw new IllegalArgumentException("Group type not supported: " + groupType);
		}
	}

	protected void updateConflictHeader(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final List<UiGroupData> conflictGroups = uiGroup.getSubGroups();

		if (instance != null && conflictGroups != null)
		{
			for (final UiGroupData uiSubGroup : conflictGroups)
			{
				updateUiGroup(instance, uiSubGroup);
			}
		}
	}

	protected void updateSubInstances(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final InstanceModel subInstance = retrieveRelatedInstanceModel(instance, uiGroup);
		updateConflictHeader(subInstance, uiGroup);
	}


	protected void updateConflictGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{
		//conflict groups might carry no cstics at all in case conflict solver cannot find the conflicting
		//assumptions
		if (!isConflictGroupProcessing() || uiGroup.getCstics() == null)
		{
			return;
		}


		for (final CsticData cstic : uiGroup.getCstics())
		{
			if (cstic.getType() != UiType.NOT_IMPLEMENTED)
			{
				final InstanceModel instanceCarryingTheConflict = getSubInstance(instance, cstic.getInstanceId());
				if (instanceCarryingTheConflict == null)
				{
					throw new IllegalStateException("No instance found for id: " + cstic.getInstanceId());
				}
				updateCsticModelFromCsticData(instanceCarryingTheConflict, cstic);
			}
		}
	}



	InstanceModel getSubInstance(final InstanceModel instance, final String instanceId)
	{
		final String id = instance.getId();
		if (id != null && id.equals(instanceId))
		{
			return instance;
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			final InstanceModel foundInstance = getSubInstance(subInstance, instanceId);
			if (foundInstance != null)
			{
				return foundInstance;
			}
		}
		return null;
	}



	protected InstanceModel retrieveRelatedInstanceModel(final InstanceModel instance, final UiGroupData uiSubGroup)
	{
		InstanceModel instToReturn = null;
		final String uiGroupId = uiSubGroup.getId();
		if (uiGroupId != null)
		{
			final String instanceId = getUiKeyGenerator().retrieveInstanceId(uiGroupId);
			final List<InstanceModel> subInstances = instance.getSubInstances();
			for (final InstanceModel subInstance : subInstances)
			{
				if (subInstance.getId().equals(instanceId))
				{
					instToReturn = subInstance;
					break;
				}
			}
		}
		return instToReturn;
	}

	protected void updateCsticGroup(final InstanceModel instance, final UiGroupData csticGroup)
	{
		// we need this check for null, in the model the empty lists will be changed to null
		if (csticGroup != null && csticGroup.getCstics() != null)
		{
			for (final CsticData csticData : csticGroup.getCstics())
			{
				if (csticData.getType() != UiType.NOT_IMPLEMENTED)
				{
					updateCsticModelFromCsticData(instance, csticData);
				}
			}
		}
	}

	protected void updateCsticModelFromCsticData(final InstanceModel instance, final CsticData csticData)
	{
		final String csticName = csticData.getName();
		final CsticModel cstic = instance.getCstic(csticName);
		if (cstic == null)
		{
			throw new IllegalStateException("No cstic available at instance " + instance.getId() + " : " + csticName);
		}
		if (cstic.isChangedByFrontend())
		{
			return;
		}
		getCsticTypeMapper().updateCsticModelValuesFromData(csticData, cstic);
	}


	protected ConfigConsistenceChecker getConfigConsistenceChecker()
	{
		return configConsistenceChecker;
	}

	/**
	 * @param b
	 *           Is conflict group processing active?
	 */
	public void setConflictGroupProcessing(final boolean b)
	{
		this.conflictGroupProcessing = b;
	}

	@Override
	public int getNumberOfErrors(final String configId)
	{
		return getConfigurationService().calculateNumberOfIncompleteCsticsAndSolvableConflicts(configId);
	}
}
