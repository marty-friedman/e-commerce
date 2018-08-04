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
package de.hybris.platform.webservices.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.webservices.AbstractResource;
import org.apache.commons.lang.StringUtils;


/**
 * REST resource implementation for {@link MediaModel} binary content
 * <p/>
 * Allowed methods: POST, PUT
 */
public class MediaContentResource extends AbstractResource<MediaModel>
{
	protected MediaModel mediaModel = null;

	public void setMediaModel(final MediaModel mediaModel)
	{
		this.mediaModel = mediaModel;
	}

	/**
	 * HTTP POST
	 * 
	 * @return {@link Response}
	 */
	@POST
	public Response postMediaContent(final InputStream inputStream)
	{
		if (mediaModel == null)
		{
			getResponse().status(Response.Status.NOT_FOUND);
		}
		else
		{
			final String contentType = getContentType();
			if (StringUtils.isNotEmpty(contentType))
			{
				getParentResource().getServiceLocator()
						.getMediaService().setStreamForMedia(mediaModel, inputStream, mediaModel.getRealFileName(), contentType);
			}
			else
			{
				getParentResource().getServiceLocator().getMediaService().setStreamForMedia(mediaModel, inputStream);
			}
		}
		return getResponse().build();
	}

	private String getContentType()
	{
		final List<String> requestHeader = httpHeaders.getRequestHeader(HttpHeaders.CONTENT_TYPE);
		if (requestHeader.size() == 1)
		{
			return requestHeader.get(0);
		}
		return null;
	}

	/**
	 * HTTP PUT
	 * 
	 * @return {@link Response}
	 */
	@PUT
	public Response putMediaContent(final InputStream inputStream)
	{
		if (mediaModel == null)
		{
			getResponse().status(Response.Status.NOT_FOUND);
		}
		else
		{
			getParentResource().getServiceLocator().getMediaService().setStreamForMedia(mediaModel, inputStream);
		}
		return getResponse().build();
	}

	@Override
	protected MediaModel readResource(final String resourceId)
	{
		// YTODO Auto-generated method stub
		return null;
	}
}
