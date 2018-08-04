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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.util.regex.Pattern;


/**
 * Default implementation of the {@link CsticValueModel}.<br>
 */
public class CsticValueModelImpl extends BaseModelImpl implements CsticValueModel
{

	private static final String NUMERIC_FORMAT = "-?\\d+(\\.\\d+)?(E{1}\\d{1})?";

	private static final ThreadLocal<Pattern> numericFormatCache = new ThreadLocal()
	{
		@Override
		protected Pattern initialValue()
		{
			return Pattern.compile(NUMERIC_FORMAT);
		}
	};

	private String name;
	private String languageDependentName;
	private boolean domainValue;
	private String author;
	private String authorExternal = null;
	private boolean selectable = true;
	private PriceModel deltaPrice = PriceModel.NO_PRICE;
	private PriceModel valuePrice = PriceModel.NO_PRICE;
	private boolean numeric;

	@Override
	public boolean isNumeric()
	{
		return numeric;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String getLanguageDependentName()
	{
		return languageDependentName;
	}

	@Override
	public void setLanguageDependentName(final String languageDependentName)
	{
		this.languageDependentName = languageDependentName;
	}

	@Override
	public boolean isDomainValue()
	{
		return domainValue;
	}

	@Override
	public void setDomainValue(final boolean domainValue)
	{
		this.domainValue = domainValue;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("\nCsticValueModelImpl [name=");
		builder.append(name);
		builder.append(", languageDependentName=");
		builder.append(languageDependentName);
		builder.append(", domainValue=");
		builder.append(domainValue);
		builder.append(", author=");
		builder.append(author);
		builder.append(", deltaPrice=");
		builder.append(deltaPrice);
		builder.append(", valuePrice=");
		builder.append(valuePrice);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CsticValueModelImpl other = (CsticValueModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}

		if (name == null)
		{
			return other.name == null;
		}

		return compareName(other);
	}

	protected boolean compareName(final CsticValueModelImpl other)
	{
		final boolean isEqual = name.equals(other.name);

		if (isEqual || (!isNumeric()))
		{
			return isEqual;
		}

		final Pattern numericFormatPattern = numericFormatCache.get();
		if (!numericFormatPattern.matcher(getName()).matches() || !numericFormatPattern.matcher(other.getName()).matches())
		{
			return false;
		}

		return 0 == Double.compare(Double.parseDouble(getName()), Double.parseDouble(other.getName()));
	}

	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}

	@Override
	public boolean isSelectable()
	{
		return this.selectable;
	}

	@Override
	public void setSelectable(final boolean selectable)
	{
		this.selectable = selectable;
	}

	@Override
	public String getAuthorExternal()
	{
		return authorExternal;
	}

	@Override
	public void setAuthorExternal(final String authorExternal)
	{
		this.authorExternal = authorExternal;
	}

	@Override
	public PriceModel getDeltaPrice()
	{
		return deltaPrice;
	}

	@Override
	public void setDeltaPrice(final PriceModel deltaPrice)
	{
		PriceModel newDeltaPrice = deltaPrice;
		if (newDeltaPrice == null)
		{
			newDeltaPrice = PriceModel.NO_PRICE;
		}
		this.deltaPrice = newDeltaPrice;

	}

	@Override
	public PriceModel getValuePrice()
	{
		return valuePrice;
	}

	@Override
	public void setValuePrice(final PriceModel valuePrice)
	{
		PriceModel newValuePrice = valuePrice;
		if (newValuePrice == null)
		{
			newValuePrice = PriceModel.NO_PRICE;
		}
		this.valuePrice = newValuePrice;

	}


	@Override
	public void setNumeric(final boolean b)
	{
		this.numeric = b;

	}

}
