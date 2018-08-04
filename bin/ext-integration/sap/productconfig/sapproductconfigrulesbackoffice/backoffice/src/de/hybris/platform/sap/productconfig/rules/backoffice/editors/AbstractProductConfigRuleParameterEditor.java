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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import de.hybris.platform.sap.productconfig.services.ProductCsticAndValueParameterProviderService;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

import com.hybris.cockpitng.core.util.Validate;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.editors.impl.AbstractCockpitEditorRenderer;


/**
 * Abstract implementation of the characteristic and characteristic value editor in the product configuration rules
 */
public abstract class AbstractProductConfigRuleParameterEditor extends AbstractCockpitEditorRenderer<Object>
{
	@Resource(name = "productCsticAndValueParameterProviderService")
	private ProductCsticAndValueParameterProviderService parameterProviderService;

	@Override
	public void render(final Component parent, final EditorContext<Object> context, final EditorListener<Object> listener)
	{
		Validate.notNull("All parameters are mandatory", parent, context, listener);

		final ListModelList<Object> model = prepareModel(context);

		final Combobox box = createCombobox();
		parent.appendChild(box);

		box.setModel(model);
		box.setReadonly(false);
		box.setAutodrop(true);
		box.setDisabled(!context.isEditable());

		box.setItemRenderer(new ProductConfigParameterRenderer());
		box.addEventListener(Events.ON_CHANGE, new ProductConfigParameterOnChangeEventListener(listener));
		box.addEventListener(Events.ON_OPEN, new ProductConfigParameterOnOpenEventListener(listener, box));
	}

	protected ListModelList<Object> prepareModel(final EditorContext<Object> context)
	{
		final Object initialValue = context.getInitialValue();

		final List<Object> allValues = getPossibleValues(context);

		// Add initialValue to allValues, if not already contained
		addInitialValue(allValues, initialValue);

		final ListModelList<Object> model = new ListModelList<Object>(allValues);
		if (initialValue != null)
		{
			model.setSelection(Collections.singletonList(initialValue));
		}
		return model;
	}

	protected Combobox createCombobox()
	{
		return new Combobox();
	}

	protected void addInitialValue(final List<Object> allValues, final Object initialValue)
	{
		if (initialValue != null && !allValues.contains(initialValue))
		{
			allValues.add(0, initialValue);
		}
	}

	protected abstract List<Object> getPossibleValues(final EditorContext<Object> context);

	/**
	 * @return the parameterProviderService
	 */
	protected ProductCsticAndValueParameterProviderService getParameterProviderService()
	{
		return parameterProviderService;
	}

	/**
	 * @param parameterProviderService
	 *           the parameterProviderService to set
	 */
	public void setParameterProviderService(final ProductCsticAndValueParameterProviderService parameterProviderService)
	{
		this.parameterProviderService = parameterProviderService;
	}
}
