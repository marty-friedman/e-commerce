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
package de.hybris.platform.webservices;




/**
 * A command is a special kind of response which can be executed as part of a POST request.
 * <p/>
 * Commands are registered at resource classes. Executing a command is done by it's name which must be passed as URL
 * query parameter.
 */
public interface Command<RESOURCE, REQUEST, RESPONSE>
{

	/**
	 * Returns the name of this command.
	 * 
	 * @return name of command
	 */
	String getName();


	/**
	 * Executes a Command operation on an entity.
	 * 
	 * @param resourceEntity
	 *           this is the entity the command is executed for, it is always a Hybris Model
	 * @param requestEntity
	 *           this is a parameter which is passed and shall be applied to the entity, as this is always a DTO it may
	 *           itself provide many single parameters as DTO members
	 * 
	 * @return the entity which shall be send to the client, this can be a DTO, a Hybris Model or null
	 * @throws Exception
	 */
	public RESPONSE execute(final RESOURCE resourceEntity, final REQUEST requestEntity) throws Exception;

}
