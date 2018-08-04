// [y] hybris Platform
//
// Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
//
// This software is the confidential and proprietary information of SAP
// ("Confidential Information"). You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the
// license agreement you entered into with SAP.
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery

def fQuery = new FlexibleSearchQuery('SELECT {PK} FROM {ItemVersionMarker}')
def result = flexibleSearchService.search(fQuery)

result.getResult().forEach {
	modelService.remove(it)
}
