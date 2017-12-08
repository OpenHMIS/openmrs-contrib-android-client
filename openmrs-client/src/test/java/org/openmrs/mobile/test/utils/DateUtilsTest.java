/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.test.utils;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.mobile.utilities.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.whenNew;

public class DateUtilsTest{

	private static final String INITIAL_DATA_1 = "1967-03-26T00:00:00.000+0200";
	private static final String INITIAL_DATA_2 = "1990-03-24T00:00";
	private static final String EXPECTED_DATA_1 = "26/03/1967";
	private static final String EXPECTED_DATA_2 = "24/03/1990";
	private static final String INVALID_DATA_3 = "09-07-1697T00:00";
	private static final String INVALID_DATA_4 = "1988/12/20";
	private static final String EXPECTED_LONG_3 = "598597200000";

	private static final String TEST_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	private SimpleDateFormat format;

	@Before
	public void setUp() throws Exception{
		//mock present time to fixed date as (01-01-2017)
			long mockDateMillis = new DateTime(2017,01,01,16,39).getMillis();
			DateTimeUtils.setCurrentMillisFixed(mockDateMillis);
		whenNew(DateTime.class).withNoArguments().thenReturn(new DateTime());

		format = new SimpleDateFormat(TEST_DATE_FORMAT);
	}

	@After
	public void cleanUp(){
		//reset System time millis
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void calculateAgeShouldReturn_FiveDays(){
		String age = DateUtils.calculateAge("2016-12-25");
		assertEquals("7 days",age);
	}

	@Test
	public void calculateAgeShouldReturn_SevenMonths(){
		String age = DateUtils.calculateAge("2016-05-17");
		assertEquals("7 months",age);
	}
	@Test
	public void calculateAgeShouldReturn_TwentyYears(){
		String age = DateUtils.calculateAge("1996-10-11");
		assertEquals("20",age);
	}

	@Test
	public void calculateAgeShouldReturn_NineteenYears(){
		//birthday not reached yet..so still 19yrs
		String age = DateUtils.calculateAge("1997-10-15");
		assertEquals("19",age);
	}

	@Test
	public void shouldReturnAge_TwentySevenYears(){
		//birthday passed
		String age = DateUtils.calculateAge("1990-01-01");
		assertEquals("27",age);
	}

	@Test
	public void calculateTimeDifference_returnsEightSeconds() throws ParseException{
		Date start = format.parse("08/07/2017 14:44:22");
		Date end = format.parse("08/07/2017 14:44:30");
		String result = DateUtils.calculateTimeDifference(start,end,true);
		assertEquals("8 secs",result);
	}

	@Test
	public void calculateTimeDifference_returnsTwoMinutes() throws ParseException{
		Date start = format.parse("11/10/2017 14:44:22");
		Date end = format.parse("11/10/2017 14:46:30");
		String result = DateUtils.calculateTimeDifference(start,end,true);
		assertEquals("2 mins",result);
	}

	@Test
	public void calculateTimeDifference_returnsThreeMonths() throws ParseException{
		Date start = format.parse("18/07/2017 00:44:00");
		Date end = format.parse("20/10/2017 14:41:56");
		String result = DateUtils.calculateTimeDifference(start,end,true);
		assertEquals("3 months",result);
	}

	@Test
	public void calculateTimeDifference_returnsElevenMonths() throws ParseException{
		Date start = format.parse("24/11/2016 00:44:22");
		Date end = format.parse("10/10/2017 14:41:45");
		String result = DateUtils.calculateTimeDifference(start,end,true);
		assertEquals("11 months",result);
	}

	@Test
	public void calculateTimeDifference_returnsFourYears() throws ParseException{
		Date start = format.parse("24/03/2013 00:44:22");
		Date end = format.parse("10/05/2017 14:41:45");
		String result = DateUtils.calculateTimeDifference(start,end,true);
		assertEquals("4 yrs",result);
	}

	@Test
	public void testDateUtils() {
		Long stringToLongResult;
		String longToStringResult;

		stringToLongResult = DateUtils.convertTime(INITIAL_DATA_1);
		longToStringResult = DateUtils.convertTime(stringToLongResult, TimeZone.getTimeZone("GMT+02:00"));
		assertEquals(EXPECTED_DATA_1, longToStringResult);

		stringToLongResult = DateUtils.convertTime(INITIAL_DATA_2, "yyyy-MM-dd'T'HH:mm");
		longToStringResult = DateUtils.convertTime(stringToLongResult);
		assertEquals(EXPECTED_DATA_2, longToStringResult);

		stringToLongResult = DateUtils.convertTime(INVALID_DATA_3);
		assertNotSame(EXPECTED_LONG_3, String.valueOf(stringToLongResult));
	}

	@Test(expected = NullPointerException.class)
	public void shouldFailWithException_convertTimeUsingInvalidData(){
		Long stringToLongResult;
		stringToLongResult = DateUtils.convertTime(INVALID_DATA_4);
		assertNull(stringToLongResult);
	}
}
