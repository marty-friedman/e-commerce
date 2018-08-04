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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class ConfigModelTest
{

	private final ConfigModel classUnderTest = new ConfigModelStable();

	@Test
	public void testGetMessagesDefault()
	{
		assertNotNull(classUnderTest.getMessages());
		assertTrue(classUnderTest.getMessages().isEmpty());
	}

	@Test(expected = NotImplementedException.class)
	public void testSetMessagesDefault()
	{
		classUnderTest.setMessages(Collections.emptySet());
	}

	@Test(expected = NotImplementedException.class)
	public void testSetKbIdDefault()
	{
		classUnderTest.setKbId("123");
	}

	@Test
	public void testGetKbIdDefault()
	{
		assertNull(classUnderTest.getKbId());
	}


	private static final class ConfigModelStable implements ConfigModel
	{
		@Override
		public Map<String, String> getExtensionMap()
		{
			return null;
		}

		@Override
		public void setExtensionMap(final Map<String, String> extensionMap)
		{
			//empty
		}

		@Override
		public void putExtensionData(final String key, final String value)
		{
			//empty
		}


		@Override
		public String getExtensionData(final String key)
		{
			return null;
		}

		@Override
		public String getId()
		{
			return null;
		}

		@Override
		public void setId(final String id)
		{
			//empty
		}

		@Override
		public String getName()
		{
			return null;
		}

		@Override
		public void setName(final String name)
		{
			//empty
		}

		@Override
		public InstanceModel getRootInstance()
		{
			return null;
		}

		@Override
		public void setRootInstance(final InstanceModel rootInstance)
		{
			//empty
		}

		@Override
		public boolean isConsistent()
		{
			return false;
		}

		@Override
		public void setConsistent(final boolean isConsistent)
		{
			//empty
		}

		@Override
		public boolean isComplete()
		{
			return false;
		}

		@Override
		public void setComplete(final boolean isComplete)
		{
			//empty
		}

		@Override
		public ConfigModel clone()
		{
			try
			{
				return (ConfigModel) super.clone();
			}
			catch (final CloneNotSupportedException e)
			{
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public PriceModel getBasePrice()
		{
			return null;
		}

		@Override
		public void setBasePrice(final PriceModel basePrice)
		{
			//empty
		}

		@Override
		public PriceModel getSelectedOptionsPrice()
		{
			return null;
		}

		@Override
		public void setSelectedOptionsPrice(final PriceModel selectedOptionsPrice)
		{
			//empty
		}

		@Override
		public PriceModel getCurrentTotalPrice()
		{
			return null;
		}

		@Override
		public void setCurrentTotalPrice(final PriceModel currentTotalPrice)
		{
			//empty
		}

		@Override
		public boolean isSingleLevel()
		{
			return false;
		}

		@Override
		public void setSingleLevel(final boolean singleLevel)
		{
			//empty
		}

		@Override
		public void setSolvableConflicts(final List<SolvableConflictModel> solvableConflicts)
		{
			//empty
		}

		@Override
		public List<SolvableConflictModel> getSolvableConflicts()
		{
			return null;
		}

		@Override
		public void setCsticValueDeltas(final List<CsticValueDelta> csticValueDeltas)
		{
			// empty

		}

		@Override
		public List<CsticValueDelta> getCsticValueDeltas()
		{
			return null;
		}

		@Override
		public void setPricingError(final boolean pricingError)
		{
			// empty

		}

		@Override
		public boolean hasPricingError()
		{
			return false;
		}

	}

}
