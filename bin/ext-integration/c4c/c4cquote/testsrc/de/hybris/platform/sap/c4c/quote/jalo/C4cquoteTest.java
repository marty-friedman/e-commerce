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
package de.hybris.platform.sap.c4c.quote.jalo;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Tests for the C4cquote extension.
 */
@UnitTest
public class C4cquoteTest {
    /**
     * Edit the local|project.properties to change logging behaviour (properties
     * log4j2.logger.*).
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(C4cquoteTest.class);

    @Before
    public void setUp() {
        // implement here code executed before each test
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * This is a sample test method.
     */
    @Test
    public void testC4cquote() {
        final boolean testTrue = true;
        assertThat(testTrue).isTrue();
    }
}
