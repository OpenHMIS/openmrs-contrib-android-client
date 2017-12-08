/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenHMIS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.mobile.activities.patientlist.PatientListContract;
import org.openmrs.mobile.activities.patientlist.PatientListPresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.PullSubscriptionDbService;
import org.openmrs.mobile.data.impl.PatientListContextDataService;
import org.openmrs.mobile.data.impl.PatientListDataService;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientList;
import org.openmrs.mobile.models.PatientListCondition;
import org.openmrs.mobile.models.PatientListContext;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.test.ACUnitTestBase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PatientListPresenterTest extends ACUnitTestBase {

	@Mock
	private PatientListDataService patientListDataService;
	@Mock
	private PatientListContextDataService patientListContextDataService;
	@Mock
	private PatientListContract.View view;
	@Mock
	private PullSubscriptionDbService pullSubscriptionDbService;

	private PatientListPresenter presenter;

	private List<PatientList> patientLists = new ArrayList<>();
	private List<PatientList> selectedSyncingPatientLists = new ArrayList<>();
	private List<PatientListContext> patientListData;
	private PagingInfo pagingInfo;

	@Before
	public void setUp() {
		presenter = new PatientListPresenter(view, patientListDataService, patientListContextDataService,
				pullSubscriptionDbService);

		PatientList list1 = new PatientList();
		list1.setUuid("11-22-33");
		list1.setName("Female Ward");
		list1.setBodyTemplate("<div class='body_template'></div>");
		list1.setHeaderTemplate(
				"<div class='header_template'>#{p.identifier} {p.fullName} (Gender: {p.gender} - Age {p.age})</div>");

		PatientListCondition condition1 = new PatientListCondition();
		condition1.setConditionOrder(0);
		condition1.setField("p.gender");
		condition1.setOperator("EQUALS");
		condition1.setValue("F");
		List<PatientListCondition> conditions = new ArrayList<>();
		conditions.add(condition1);
		list1.setPatientListConditions(conditions);

		patientLists.add(list1);

		// populate patient list data
		patientListData = new ArrayList<>();
		PatientListContext data = new PatientListContext();
		data.setPatientList(list1);

		Patient patient = new Patient();
		Person person = new Person();
		person.setGender("Female");
		person.setBirthdate("2000-02-21");
		patient.setPerson(person);
		data.setPatient(patient);

		data.setHeaderContent("<div class='header_template'>#001 (Gender: Female - Age 17");
		data.setBodyContent("<div class='body_template'></div>");
		patientListData.add(data);
	}

	@Test
	public void shouldGetPatientList_success() throws Exception {
		when(pullSubscriptionDbService.getAll(any(QueryOptions.class), any(PagingInfo.class)))
				.thenReturn(new ArrayList<PullSubscription>());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				((DataService.GetCallback<List<PatientList>>)invocation.getArguments()[2]).onCompleted(patientLists);
				return null;
			}
		}).when(patientListDataService).getAll(any(QueryOptions.class), any(PagingInfo.class),
				any(DataService.GetCallback.class));

		presenter.getPatientList();
		verify(view).setNoPatientListsVisibility(false);
		verify(view).updatePatientLists(patientLists, selectedSyncingPatientLists);
	}

	@Test
	public void shouldGetPatientList_error() throws Exception {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				((DataService.GetCallback<PatientList>)invocation.getArguments()[2]).onError(new Throwable("error"));
				return null;
			}
		}).when(patientListDataService).getAll(any(QueryOptions.class), any(PagingInfo.class),
				any(DataService.GetCallback.class));

		presenter.getPatientList();
		verify(view).setNoPatientListsVisibility(true);
	}

	@Test
	public void shouldGetPatientListContextModelData_success() throws Exception {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				((DataService.GetCallback<List<PatientListContext>>)invocation.getArguments()[3]).onCompleted
						(patientListData);
				((PagingInfo)invocation.getArguments()[2]).setTotalRecordCount(2);
				return null;
			}
		}).when(patientListContextDataService).getListPatients(anyString(), any(QueryOptions.class), any(PagingInfo.class),
				any(DataService.GetCallback.class));

		//presenter.getPatientListData("11-22-33", 1);
		//verify(view).updatePatientListData(patientListData);
		//verify(view).setNumberOfPatientsView(1);
		//verify(view).setSpinnerVisibility(false);
	}

	@Test
	public void shouldGetPatientListContextModelData_error() throws Exception {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				((DataService.GetCallback<Throwable>)invocation.getArguments()[3]).onError(new Throwable("error"));
				return null;
			}
		}).when(patientListContextDataService).getListPatients(anyString(), any(QueryOptions.class), any(PagingInfo.class),
				any(DataService.GetCallback.class));

		presenter.getPatientListData("11-22-33", 1);
		verify(view).setSpinnerVisibility(false);
		verify(view).setEmptyPatientListVisibility(true);
	}
}
