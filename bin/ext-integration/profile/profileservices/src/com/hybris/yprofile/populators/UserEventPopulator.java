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
package com.hybris.yprofile.populators;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserEventPopulator implements Populator<UserModel, User> {

    public static final String TYPE = "YaaS account";
    private Converter<UserModel, Consumer> profileConsumerConverter;
    private Converter<UserModel, List<Consumer>> profileIdentitiesConverter;
    private Converter<AddressModel, Address> profileAddressConverter;

    @Override
    public void populate(final UserModel userModel, User user) throws ConversionException {

        user.setDate(Utils.formatDate(new Date()));
        user.setBody(getUserBody(userModel));

    }

    protected UserBody getUserBody(final UserModel userModel){
        UserBody userBody = new UserBody();

        userBody.setType(TYPE);
        userBody.setDate(Utils.formatDate(userModel.getCreationtime()));
        userBody.setIdentity(getProfileConsumerConverter().convert(userModel));
        userBody.setIdentities(getProfileIdentitiesConverter().convert(userModel));
        userBody.setMasterData(getUserMasterData(userModel));

        return userBody;
    }


    protected UserMasterData getUserMasterData(final UserModel userModel){

        UserMasterData userMasterData = new UserMasterData();
        Address address = new Address();

        Optional<AddressModel> addressModel = getAddress(userModel);

        if (addressModel.isPresent()) {
            address = getProfileAddressConverter().convert(addressModel.get());
        }

        if (userModel instanceof CustomerModel){
            CustomerModel customerModel = (CustomerModel) userModel;

            if(customerModel.getTitle() != null) {
                address.setTitle(customerModel.getTitle().getCode());
            }

            if (StringUtils.isEmpty(address.getFirstName())) {
                String[] displayName = customerModel.getDisplayName().split(" ");
                if (displayName.length > 1) {
                    address.setFirstName(displayName[0]);
                    address.setLastName(displayName[1]);
                } else {
                    address.setFirstName(customerModel.getDisplayName());
                }
            }
        }

        userMasterData.setAddress(address);

        return userMasterData;
    }

    protected Optional<AddressModel> getAddress(final UserModel userModel){

        if (userModel.getDefaultPaymentAddress() != null) {
            return Optional.ofNullable(userModel.getDefaultPaymentAddress());
        }

        if (userModel.getDefaultShipmentAddress() != null) {
            return Optional.ofNullable(userModel.getDefaultShipmentAddress());
        }

        if (!CollectionUtils.isEmpty(userModel.getAddresses()) && userModel.getAddresses().iterator().hasNext()){
            return Optional.ofNullable(userModel.getAddresses().iterator().next());
        }

        return Optional.empty();

    }

    public Converter<UserModel, Consumer> getProfileConsumerConverter() {
        return profileConsumerConverter;
    }

    @Required
    public void setProfileConsumerConverter(Converter<UserModel, Consumer> profileConsumerConverter) {
        this.profileConsumerConverter = profileConsumerConverter;
    }

    public Converter<UserModel, List<Consumer>> getProfileIdentitiesConverter() {
        return profileIdentitiesConverter;
    }

    @Required
    public void setProfileIdentitiesConverter(final Converter<UserModel, List<Consumer>> profileIdentitiesConverter) {
        this.profileIdentitiesConverter = profileIdentitiesConverter;
    }

    public Converter<AddressModel, Address> getProfileAddressConverter() {
        return profileAddressConverter;
    }

    @Required
    public void setProfileAddressConverter(Converter<AddressModel, Address> profileAddressConverter) {
        this.profileAddressConverter = profileAddressConverter;
    }
}
