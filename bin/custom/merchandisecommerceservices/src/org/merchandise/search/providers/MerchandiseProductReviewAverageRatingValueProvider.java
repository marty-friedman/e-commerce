package org.merchandise.search.providers;

import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ProductReviewAverageRatingValueProvider;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

public class MerchandiseProductReviewAverageRatingValueProvider extends ProductReviewAverageRatingValueProvider {
	private static final Logger LOGGER = Logger.getLogger(MerchandiseProductReviewAverageRatingValueProvider.class);

	@Override
	protected void addFieldValues(List<FieldValue> fieldValues, IndexedProperty indexedProperty, LanguageModel language, Object value) {
		List<String> rangeNameList = null;
		try {
			rangeNameList = getRangeNameList(indexedProperty, value);
		} catch (final FieldValueProviderException e) {
			LOG.error("Could not get Range value", e);
		}

		String rangeName = null;
		if (CollectionUtils.isNotEmpty(rangeNameList)) {
			rangeName = rangeNameList.get(0);
		}

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				language == null ? null : language.getIsocode());
		final Object valueToPass = (rangeName == null ? value : rangeName);
		for (final String fieldName : fieldNames)
			fieldValues.add(new FieldValue(fieldName, valueToPass));
	}
}
