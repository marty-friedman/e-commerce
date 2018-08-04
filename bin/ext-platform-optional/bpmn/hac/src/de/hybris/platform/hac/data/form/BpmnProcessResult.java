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
package de.hybris.platform.hac.data.form;

public class BpmnProcessResult
{
	private final boolean success;
	private final String messageCode;
	private final String detailMessage;

	public static class BpmnProcessResultBuilder
	{
		private boolean success = true;
		private String messageCode;
		private String detailMessage;

		public BpmnProcessResultBuilder failed()
		{
			this.success = false;
			return this;
		}

		public BpmnProcessResultBuilder withMessageCode(final String messageCode)
		{
			this.messageCode = messageCode;
			return this;
		}

		public BpmnProcessResultBuilder withDetailMessage(final String detailMessage)
		{
			this.detailMessage = detailMessage;
			return this;
		}

		public BpmnProcessResult build()
		{
			return new BpmnProcessResult(this);
		}
	}

	private BpmnProcessResult(final BpmnProcessResultBuilder builder)
	{
		success = builder.success;
		messageCode = builder.messageCode;
		detailMessage = builder.detailMessage;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public String getMessageCode()
	{
		return messageCode;
	}

	public String getDetailMessage()
	{
		return detailMessage;
	}


}
