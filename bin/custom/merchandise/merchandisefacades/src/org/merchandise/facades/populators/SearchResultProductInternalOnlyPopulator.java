package org.merchandise.facades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;

import java.util.Optional;

public class SearchResultProductInternalOnlyPopulator implements Populator<SearchResultValueData, ProductData> {
	@Override
	public void populate(SearchResultValueData searchResultValueData, ProductData productData) throws ConversionException {
		Assert.notNull(searchResultValueData, "Parameter searchResultValueData cannot be null.");
		Assert.notNull(productData, "Parameter productData cannot be null.");
		productData.setInternalOnly(Optional.ofNullable((Boolean) searchResultValueData.getValues().get("internalOnly")).orElse(Boolean.FALSE));
	}
}
