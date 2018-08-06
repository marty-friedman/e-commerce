package org.merchandise.facades.populators;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

public class CustomerInternalPopulator implements Populator<CustomerModel,CustomerData> {
	@Override
	public void populate(CustomerModel customerModel, CustomerData customerData) throws ConversionException {
		customerData.setInternal(Optional.ofNullable(customerModel.getInternal()).orElse(Boolean.FALSE));
	}
}
