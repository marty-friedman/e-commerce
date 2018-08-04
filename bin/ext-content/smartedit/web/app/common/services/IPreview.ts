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
import {Payload} from "../dtos/Payload";

/**
 * @name smarteditServicesModule.interface:IPreviewResponse
 *
 * @description
 * Interface for data representing a storefront preview ticket.
 *
 */
export interface IPreviewResponse {

    /**
     * @name previewTicketId
     * @propertyOf smarteditServicesModule.interface:IPreviewResponse
     * @description
     * Identifier for the preview
     */
	previewTicketId: string;

    /**
     * @name resourcePath
     * @propertyOf smarteditServicesModule.interface:IPreviewResponse
     * @description
     * The URI of the storefront resource
     */
	resourcePath: string;
}


/**
 * @name smarteditServicesModule.interface:IPreviewData
 *
 * @description
 * Interface for data sent to the preview API.
 *
 * Since the preview api is extensible, you can send more fields by adding a new interface that extends this one.
 * All additional members of the Object passed to the preview API will be included in the request.
 */
export interface IPreviewData extends Payload {

    /**
     * @name catalog
     * @propertyOf smarteditServicesModule.interface:IPreviewData
     * @description
     * TODO
     */
	catalog: string;

    /**
     * @name catalogVersion
     * @propertyOf smarteditServicesModule.interface:IPreviewData
     * @description
     * TODO
     */
	catalogVersion: string;

    /**
     * @name language
     * @propertyOf smarteditServicesModule.interface:IPreviewData
     * @description
     * TODO
     */
	language: string;

    /**
     * @name resourcePath
     * @propertyOf smarteditServicesModule.interface:IPreviewData
     * @description
     * TODO
     */
	resourcePath: string;

    /**
     * @name pageId
     * @propertyOf smarteditServicesModule.interface:IPreviewData
     * @description
     * TODO
     */
	pageId: string;

}
