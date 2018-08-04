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
package com.sap.hybris.sapcustomerb2b.inbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class DefaultSAPCustomerAddressConsistencyInterceptorTest
{
	@Test
	public void compareAddress()
	{
		final String VALUE1 = "value1";
		final String VALUE2 = "value2";

		final DefaultSAPCustomerAddressConsistencyInterceptor interceptor = new DefaultSAPCustomerAddressConsistencyInterceptor();

		AddressModel source = new AddressModel();
		AddressModel target = new AddressModel();

		// Street name
		source.setStreetname(VALUE1);
		target.setStreetname(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setStreetname(null);
		target.setStreetname(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setStreetname(VALUE1);
		target.setStreetname(VALUE1);

		// Street number
		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setStreetnumber(null);
		target.setStreetnumber(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setStreetnumber(VALUE1);
		target.setStreetnumber(VALUE1);

		// Postal code
		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPostalcode(null);
		target.setPostalcode(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPostalcode(VALUE1);
		target.setPostalcode(VALUE1);

		// Town
		source.setTown(VALUE1);
		target.setTown(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setTown(null);
		target.setTown(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setTown(VALUE1);
		target.setTown(VALUE1);

		// Country
		CountryModel c1 = new CountryModel();
		CountryModel c2 = new CountryModel();

		source.setCountry(c1);
		target.setCountry(c2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setCountry(null);
		target.setCountry(c2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setCountry(c1);
		target.setCountry(c1);

		// Region
		RegionModel r1 = new RegionModel();
		RegionModel r2 = new RegionModel();

		source.setRegion(r1);
		target.setRegion(r2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setRegion(null);
		target.setRegion(r2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setRegion(r1);
		target.setRegion(r1);

		// Phone 1
		source.setPhone1(VALUE1);
		target.setPhone1(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPhone1(null);
		target.setPhone1(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPhone1(VALUE1);
		target.setPhone1(VALUE1);

		// Fax
		source.setFax(VALUE1);
		target.setFax(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setFax(null);
		target.setFax(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setFax(VALUE1);
		target.setFax(VALUE1);

		// PO Box
		source.setPobox(VALUE1);
		target.setPobox(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPobox(null);
		target.setPobox(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setPobox(VALUE1);
		target.setPobox(VALUE1);

		// Cellphone
		source.setCellphone(VALUE1);
		target.setCellphone(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setCellphone(null);
		target.setCellphone(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setCellphone(VALUE1);
		target.setCellphone(VALUE1);

		// District
		source.setDistrict(VALUE1);
		target.setDistrict(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setDistrict(null);
		target.setDistrict(VALUE2);

		Assert.assertTrue(interceptor.compareAddress(source, target));

		source.setDistrict(VALUE1);
		target.setDistrict(VALUE1);

		// Address matches
		Assert.assertFalse(interceptor.compareAddress(source, target));
	}
}
