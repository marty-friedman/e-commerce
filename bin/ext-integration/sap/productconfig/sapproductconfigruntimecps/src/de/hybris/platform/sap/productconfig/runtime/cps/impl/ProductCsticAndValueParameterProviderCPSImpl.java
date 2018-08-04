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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provider to deliver the list of cstics, with all possible values, needed for the backoffice rule editor. This
 * implementation is based on the CPS configuration engine.
 */
public class ProductCsticAndValueParameterProviderCPSImpl implements ProductCsticAndValueParameterProvider
{
	private ConfigurationMasterDataService configurationMasterDataService;
	private CharonKbDeterminationFacade charonKbDeterminationFacade;
	private BaseSiteService baseSiteService;
	private CPSBaseSiteProvider cpsBaseSiteProvider;

	@Override
	public Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode)
	{
		try
		{
			setCurrentBaseSite();
			final Integer kbId = getCharonKbDeterminationFacade().getCurrentKbIdForProduct(productCode);
			if (kbId == null)
			{
				throw new IllegalStateException("No master data for the product '" + productCode + "' found");
			}
			final Map<String, CPSMasterDataCharacteristicContainer> characteristics = getCharacteristcs(kbId.toString());

			return characteristics.values().stream()
					.collect(Collectors.toMap(CPSMasterDataCharacteristicContainer::getId, this::getCsticParameters));
		}
		finally
		{
			resetCurrentBaseSite();
		}
	}

	private void resetCurrentBaseSite()
	{
		baseSiteService.setCurrentBaseSite((BaseSiteModel) null, false);
	}

	private void setCurrentBaseSite()
	{
		final BaseSiteModel baseSite = getCpsBaseSiteProvider().getConfiguredBaseSite();

		if ((baseSite == null))
		{
			throw new IllegalStateException("No BaseSite defined for the rule editor");
		}

		baseSiteService.setCurrentBaseSite(baseSite, false);
	}

	protected CsticParameterWithValues getCsticParameters(final CPSMasterDataCharacteristicContainer cstic)
	{
		final CsticParameter csticParameter = new CsticParameter();
		csticParameter.setCsticName(cstic.getId());
		csticParameter.setCsticDescription(cstic.getName());

		final List<ValueParameter> values = getValuesForCstic(cstic.getPossibleValueGlobals());

		final CsticParameterWithValues csticParameterWithValues = new CsticParameterWithValues();
		csticParameterWithValues.setCstic(csticParameter);
		csticParameterWithValues.setValues(values);

		return csticParameterWithValues;
	}

	protected List<ValueParameter> getValuesForCstic(final Map<String, CPSMasterDataPossibleValue> csticValues)
	{
		final List<ValueParameter> values = new ArrayList<>();
		for (final CPSMasterDataPossibleValue valueModel : csticValues.values())
		{
			final ValueParameter value = new ValueParameter();

			value.setValueName(valueModel.getId());
			value.setValueDescription(valueModel.getName());

			values.add(value);
		}
		return values;
	}

	protected Map<String, CPSMasterDataCharacteristicContainer> getCharacteristcs(final String kbId)
	{
		final CPSMasterDataKnowledgeBaseContainer masterData = getConfigurationMasterDataService().getMasterData(kbId);
		if (masterData == null)
		{
			throw new IllegalStateException("No master data for the product with kbId '" + kbId + "' found");
		}
		return masterData.getCharacteristics();
	}

	/**
	 * Set the conifg master data service
	 *
	 * @param configurationMasterDataService
	 *           An instance of the master data service
	 */
	@Required
	public void setConfigMasterDataService(final ConfigurationMasterDataService configurationMasterDataService)
	{
		this.configurationMasterDataService = configurationMasterDataService;
	}

	protected ConfigurationMasterDataService getConfigurationMasterDataService()
	{
		return this.configurationMasterDataService;
	}

	/**
	 * Set the kb determination facade
	 *
	 * @param charonKbDeterminationFacade
	 *           An instance of the determination facade
	 */
	@Required
	public void setCharonKbDeterminationFacade(final CharonKbDeterminationFacade charonKbDeterminationFacade)
	{
		this.charonKbDeterminationFacade = charonKbDeterminationFacade;
	}

	protected CharonKbDeterminationFacade getCharonKbDeterminationFacade()
	{
		return charonKbDeterminationFacade;
	}

	/**
	 * Set the baseSiteService
	 *
	 * @param baseSiteService
	 *           Instance of the base site service
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public CPSBaseSiteProvider getCpsBaseSiteProvider()
	{
		return cpsBaseSiteProvider;
	}

	/**
	 * Set the basesite provider for CPS rules
	 *
	 * @param cpsBaseSiteProvider
	 *           BaseSite assigned to the CPS rules
	 */
	@Required
	public void setCpsBaseSiteProvider(final CPSBaseSiteProvider cpsBaseSiteProvider)
	{
		this.cpsBaseSiteProvider = cpsBaseSiteProvider;
	}


}
