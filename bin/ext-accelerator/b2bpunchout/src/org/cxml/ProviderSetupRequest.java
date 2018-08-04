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

import java.util.ArrayList;
import java.util.List;
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
    "originatorCookie",
    "browserFormPost",
    "followup",
    "selectedService",
    "extrinsic"
})
@XmlRootElement(name = "ProviderSetupRequest")
public class ProviderSetupRequest {

    @XmlElement(name = "OriginatorCookie", required = true)
    protected String originatorCookie;
    @XmlElement(name = "BrowserFormPost")
    protected BrowserFormPost browserFormPost;
    @XmlElement(name = "Followup")
    protected Followup followup;
    @XmlElement(name = "SelectedService", required = true)
    protected String selectedService;
    @XmlElement(name = "Extrinsic")
    protected List<Extrinsic> extrinsic;

    /**
     * Gets the value of the originatorCookie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatorCookie() {
        return originatorCookie;
    }

    /**
     * Sets the value of the originatorCookie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatorCookie(String value) {
        this.originatorCookie = value;
    }

    /**
     * Gets the value of the browserFormPost property.
     * 
     * @return
     *     possible object is
     *     {@link BrowserFormPost }
     *     
     */
    public BrowserFormPost getBrowserFormPost() {
        return browserFormPost;
    }

    /**
     * Sets the value of the browserFormPost property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowserFormPost }
     *     
     */
    public void setBrowserFormPost(BrowserFormPost value) {
        this.browserFormPost = value;
    }

    /**
     * Gets the value of the followup property.
     * 
     * @return
     *     possible object is
     *     {@link Followup }
     *     
     */
    public Followup getFollowup() {
        return followup;
    }

    /**
     * Sets the value of the followup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Followup }
     *     
     */
    public void setFollowup(Followup value) {
        this.followup = value;
    }

    /**
     * Gets the value of the selectedService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSelectedService() {
        return selectedService;
    }

    /**
     * Sets the value of the selectedService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSelectedService(String value) {
        this.selectedService = value;
    }

    /**
     * Gets the value of the extrinsic property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extrinsic property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtrinsic().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Extrinsic }
     * 
     * 
     */
    public List<Extrinsic> getExtrinsic() {
        if (extrinsic == null) {
            extrinsic = new ArrayList<Extrinsic>();
        }
        return this.extrinsic;
    }

}
