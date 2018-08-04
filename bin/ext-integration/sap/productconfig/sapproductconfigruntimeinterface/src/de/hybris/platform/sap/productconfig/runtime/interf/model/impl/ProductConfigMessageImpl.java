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

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;



/**
 * A Message class implementation for CPQ<br>
 * <b>This class is immutable.</b>
 */
public class ProductConfigMessageImpl implements ProductConfigMessage
{

	private final String message;
	private final String key;
	private final ProductConfigMessageSeverity severity;
	private final ProductConfigMessageSource source;
	private final ProductConfigMessageSourceSubType subType;
	private static final int PRIME = 31;



	/**
	 * Default Constructor
	 *
	 * @param message
	 *           localized message
	 * @param key
	 *           key of the message
	 * @param severity
	 *           message severity
	 * @param source
	 *           source of the message
	 * @param subType
	 *           sub type of the message source
	 */
	public ProductConfigMessageImpl(final String message, final String key, final ProductConfigMessageSeverity severity,
			final ProductConfigMessageSource source, final ProductConfigMessageSourceSubType subType)
	{
		super();
		this.message = message;
		this.key = key;
		this.severity = severity;
		this.source = source;
		this.subType = subType;
	}

	@Override
	public String getKey()
	{
		return key;
	}


	@Override
	public String getMessage()
	{
		return message;
	}


	@Override
	public ProductConfigMessageSource getSource()
	{
		return source;
	}


	@Override
	public ProductConfigMessageSeverity getSeverity()
	{
		return severity;
	}

	@Override
	public ProductConfigMessageSourceSubType getSourceSubType()
	{
		return subType;
	}



	@Override
	public int hashCode()
	{
		int result = 1;
		result = PRIME * result + ((source == null) ? 0 : source.hashCode());
		result = PRIME * result + ((key == null) ? 0 : key.hashCode());
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
		final ProductConfigMessageImpl other = (ProductConfigMessageImpl) obj;
		if (source != other.source)
		{
			return false;
		}
		if (key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		else if (!key.equals(other.key))
		{
			return false;
		}
		return true;
	}

}
