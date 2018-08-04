/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2badmincockpit.services.config.impl;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.cockpit.model.editor.UIEditor;
import de.hybris.platform.cockpit.model.listview.CellRenderer;
import de.hybris.platform.cockpit.model.listview.TableModel;
import de.hybris.platform.cockpit.model.listview.ValueHandler;
import de.hybris.platform.cockpit.model.listview.impl.DefaultColumnDescriptor;
import de.hybris.platform.cockpit.model.search.impl.ResultObjectWrapper;
import de.hybris.platform.cockpit.services.config.impl.AbstractColumnConfiguration;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

public class B2BPermissionTypeColumn extends AbstractColumnConfiguration
{
	private DefaultColumnDescriptor colDescr = null;
	private CellRenderer renderer = null;

	public B2BPermissionTypeColumn()
	{
		super();
	}

	public B2BPermissionTypeColumn(final String name)
	{
		super();
		this.name = name;
	}

	@Override
	public UIEditor getCellEditor()
	{
		return null;
	}

	@Override
	public CellRenderer getCellRenderer()
	{
		if (this.renderer == null)
		{
			this.renderer = new PermissionCustomCellRenderer();
		}
		return this.renderer;
	}

	@Override
	public DefaultColumnDescriptor getColumnDescriptor()
	{
		if (this.colDescr == null)
		{
			this.colDescr = new DefaultColumnDescriptor(this.getName());
			this.colDescr.setEditable(this.isEditable());
			this.colDescr.setSelectable(this.isSelectable());
			this.colDescr.setSortable(this.isSortable());
			this.colDescr.setVisible(this.isVisible());
		}
		return this.colDescr;
	}

	@Override
	public ValueHandler getValueHandler()
	{
		return null;
	}

	private class PermissionCustomCellRenderer implements CellRenderer
	{
		@Override
		public void render(final TableModel model, final int colIndex, final int rowIndex, final Component parent)
		{

			final ResultObjectWrapper result = (ResultObjectWrapper) model.getListComponentModel().getListModel().getElements()
					.get(rowIndex);
			final B2BPermissionModel b2bPermissionModel = (B2BPermissionModel) result.getObject();

			parent.appendChild(new Label(b2bPermissionModel.getItemtype()));
		}

	}

	@Override
	public void setCellEditor(final UIEditor editor)
	{
		// YTODO Auto-generated method stub
	}

	@Override
	public void setLanguages(final List<LanguageModel> languages)
	{
		// YTODO Auto-generated method stub
	}

}
