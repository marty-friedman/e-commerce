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

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of the {@link ConfigModel}
 */
public class ConfigModelImpl extends BaseModelImpl implements ConfigModel
{
	private String id;
	private String name;
	private InstanceModel rootInstance;
	private boolean complete;
	private boolean consistent;
	private boolean singleLevel;
	private boolean pricingError;
	private PriceModel basePrice;
	private PriceModel selectedOptionsPrice;
	private PriceModel currentTotalPrice;
	private List<SolvableConflictModel> solvableConflicts;
	private Set<ProductConfigMessage> messages; // initialized lazy
	private List<CsticValueDelta> csticValueDeltas;
	private String kbId;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(final String id)
	{
		this.id = id;
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
	public InstanceModel getRootInstance()
	{
		return rootInstance;
	}

	@Override
	public void setRootInstance(final InstanceModel rootInstance)
	{
		this.rootInstance = rootInstance;
	}

	@Override
	public boolean isComplete()
	{
		return complete;
	}

	@Override
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}

	@Override
	public boolean isConsistent()
	{
		return consistent;
	}

	@Override
	public void setConsistent(final boolean consistent)
	{
		this.consistent = consistent;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("\nConfigModelImpl [id=");
		builder.append(id);
		builder.append(", kbId=");
		builder.append(kbId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", singeLevel=");
		builder.append(singleLevel);
		builder.append(", pricingError=");
		builder.append(pricingError);
		builder.append(", solvableConflicts=");
		builder.append(solvableConflicts);
		builder.append("\nbasePrice=");
		builder.append(basePrice);
		builder.append(",\nselectedOptionsPrice=");
		builder.append(selectedOptionsPrice);
		builder.append(",\ncurrentTotalPrice=");
		builder.append(currentTotalPrice);
		builder.append(",\nrootInstance=");
		builder.append(rootInstance);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = PRIME * result + (complete ? 1231 : 1237);
		result = PRIME * result + (consistent ? 1231 : 1237);
		result = PRIME * result + (pricingError ? 1231 : 1237);
		result = PRIME * result + ((id == null) ? 0 : id.hashCode());
		result = PRIME * result + ((kbId == null) ? 0 : kbId.hashCode());
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((basePrice == null) ? 0 : basePrice.hashCode());
		result = PRIME * result + ((selectedOptionsPrice == null) ? 0 : selectedOptionsPrice.hashCode());
		result = PRIME * result + ((currentTotalPrice == null) ? 0 : currentTotalPrice.hashCode());
		result = PRIME * result + ((rootInstance == null) ? 0 : rootInstance.hashCode());
		result = PRIME * result + (singleLevel ? 1231 : 1237);
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

		final ConfigModelImpl other = (ConfigModelImpl) obj;
		if (!super.equals(other))
		{
			return false;
		}
		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (pricingError != other.pricingError)
		{
			return false;
		}
		if (!objectsEqual(id, other.id))
		{
			return false;
		}
		if (!objectsEqual(kbId, other.kbId))
		{
			return false;
		}
		if (!objectsEqual(name, other.name))
		{
			return false;
		}
		if (!objectsEqual(rootInstance, other.rootInstance))
		{
			return false;
		}
		if (!objectsEqual(basePrice, other.basePrice))
		{
			return false;
		}
		if (!objectsEqual(selectedOptionsPrice, other.selectedOptionsPrice))
		{
			return false;
		}
		if (!objectsEqual(currentTotalPrice, other.currentTotalPrice))
		{
			return false;
		}
		return singleLevel == other.singleLevel;
	}

	protected boolean objectsEqual(final Object obj1, final Object obj2)
	{
		if (obj1 == null)
		{
			if (obj2 != null)
			{
				return false;
			}
		}
		else if (!obj1.equals(obj2))
		{
			return false;
		}

		return true;
	}

	@Override
	public PriceModel getBasePrice()
	{
		return basePrice;
	}

	@Override
	public void setBasePrice(final PriceModel basePrice)
	{
		this.basePrice = basePrice;
	}

	@Override
	public PriceModel getSelectedOptionsPrice()
	{
		return selectedOptionsPrice;
	}

	@Override
	public void setSelectedOptionsPrice(final PriceModel selectedOptionsPrice)
	{
		this.selectedOptionsPrice = selectedOptionsPrice;
	}

	@Override
	public PriceModel getCurrentTotalPrice()
	{
		return currentTotalPrice;
	}

	@Override
	public void setCurrentTotalPrice(final PriceModel currentTotalPrice)
	{
		this.currentTotalPrice = currentTotalPrice;
	}

	@Override
	public boolean isSingleLevel()
	{
		return singleLevel;
	}

	@Override
	public void setSingleLevel(final boolean singleLevel)
	{
		this.singleLevel = singleLevel;
	}

	@Override
	public void setSolvableConflicts(final List<SolvableConflictModel> solvableConflicts)
	{
		this.solvableConflicts = solvableConflicts;

	}

	@Override
	public List<SolvableConflictModel> getSolvableConflicts()
	{
		return solvableConflicts;
	}

	@Override
	public Set<ProductConfigMessage> getMessages()
	{
		if (messages == null)
		{
			messages = new HashSet(4);
		}
		return messages;
	}

	@Override
	public void setMessages(final Set<ProductConfigMessage> messages)
	{
		this.messages = messages;
	}

	@Override
	public void setCsticValueDeltas(final List<CsticValueDelta> csticValueDeltas)
	{
		this.csticValueDeltas = csticValueDeltas;
	}

	@Override
	public List<CsticValueDelta> getCsticValueDeltas()
	{
		if (csticValueDeltas == null)
		{
			csticValueDeltas = new ArrayList();
		}
		return csticValueDeltas;
	}

	@Override
	public String getKbId()
	{
		return kbId;
	}

	@Override
	public void setKbId(final String kbId)
	{
		this.kbId = kbId;
	}

	@Override
	public void setPricingError(final boolean pricingError)
	{
		this.pricingError = pricingError;
	}

	@Override
	public boolean hasPricingError()
	{
		return pricingError;
	}
}
