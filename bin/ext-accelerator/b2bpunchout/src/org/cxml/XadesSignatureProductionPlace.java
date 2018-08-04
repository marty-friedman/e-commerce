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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.12 at 07:19:30 PM EDT 
//



package org.cxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xadesCity",
    "xadesStateOrProvince",
    "xadesPostalCode",
    "xadesCountryName"
})
@XmlRootElement(name = "xades:SignatureProductionPlace")
public class XadesSignatureProductionPlace {

    @XmlElement(name = "xades:City")
    protected String xadesCity;
    @XmlElement(name = "xades:StateOrProvince")
    protected String xadesStateOrProvince;
    @XmlElement(name = "xades:PostalCode")
    protected String xadesPostalCode;
    @XmlElement(name = "xades:CountryName")
    protected String xadesCountryName;

    /**
     * Gets the value of the xadesCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXadesCity() {
        return xadesCity;
    }

    /**
     * Sets the value of the xadesCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXadesCity(String value) {
        this.xadesCity = value;
    }

    /**
     * Gets the value of the xadesStateOrProvince property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXadesStateOrProvince() {
        return xadesStateOrProvince;
    }

    /**
     * Sets the value of the xadesStateOrProvince property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXadesStateOrProvince(String value) {
        this.xadesStateOrProvince = value;
    }

    /**
     * Gets the value of the xadesPostalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXadesPostalCode() {
        return xadesPostalCode;
    }

    /**
     * Sets the value of the xadesPostalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXadesPostalCode(String value) {
        this.xadesPostalCode = value;
    }

    /**
     * Gets the value of the xadesCountryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXadesCountryName() {
        return xadesCountryName;
    }

    /**
     * Sets the value of the xadesCountryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXadesCountryName(String value) {
        this.xadesCountryName = value;
    }

}
