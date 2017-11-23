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

package org.openmrs.mobile.activities.visit.visittasks;

import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.BaseVisitPresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitPredefinedTaskDataService;
import org.openmrs.mobile.data.impl.VisitTaskDataService;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPredefinedTask;
import org.openmrs.mobile.models.VisitTask;
import org.openmrs.mobile.models.VisitTaskStatus;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitTasksPresenter extends BaseVisitPresenter implements VisitContract.VisitTasksPresenter {

	private VisitContract.VisitTasksView visitTasksView;
	private VisitPredefinedTaskDataService visitPredefinedTaskDataService;
	private VisitTaskDataService visitTaskDataService;
	private VisitDataService visitDataService;
	private String patientUUID;

	private int page = 1;
	private int limit = 10;
	private int numberOfDataCallsCompletedForDataRefresh = 0;
	private final int TOTAL_NUMBER_OF_REFRESH_DATA_CALLS = 2;

	public VisitTasksPresenter(String patientUuid, String visitUuid, VisitContract.VisitTasksView visitTasksView) {
		super(visitUuid, visitTasksView);

		this.visitTasksView = visitTasksView;
		this.patientUUID = patientUuid;

		this.visitPredefinedTaskDataService = dataAccess().visitPredefinedTask();
		this.visitTaskDataService = dataAccess().visitTask();
		this.visitDataService = dataAccess().visit();
	}

	@Override
	public void subscribe() {
		getVisit(false);
		getPredefinedTasks(false);
	}

	private void getPredefinedTasks(boolean forceRefresh) {
		if (!forceRefresh) {
			visitTasksView.showTabSpinner(true);
		}
		PagingInfo pagingInfo = new PagingInfo(page, 100);
		DataService.GetCallback<List<VisitPredefinedTask>> callback = new DataService
				.GetCallback<List<VisitPredefinedTask>>() {
			@Override
			public void onCompleted(List<VisitPredefinedTask> visitPredefinedTasks) {
				visitTasksView.setPredefinedTasks(visitPredefinedTasks);
				visitTasksView.showTabSpinner(false);
				removeRefreshIndicatorIfAllCallsComplete();
			}

			@Override
			public void onError(Throwable t) {
				visitTasksView.showTabSpinner(false);
				visitTasksView
						.showToast(ApplicationConstants.entityName.PREDEFINED_TASKS + ApplicationConstants.toastMessages
								.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};

		QueryOptions options = QueryOptions.FULL_REP;
		if (forceRefresh) {
			options = QueryOptions.REMOTE_FULL_REP;
		}

		visitPredefinedTaskDataService.getAll(options, pagingInfo, callback);
	}

	private void getVisitTasks(boolean forceRefresh) {
		if (!forceRefresh) {
			visitTasksView.showTabSpinner(true);
		}

		QueryOptions optionsTemp = QueryOptions.FULL_REP;
		if (forceRefresh) {
			optionsTemp = QueryOptions.REMOTE_FULL_REP;
		}
		final QueryOptions options = optionsTemp;

		PagingInfo pagingInfo = new PagingInfo(page, limit);
		// get open tasks
		visitTaskDataService.getAll("OPEN", patientUUID, visitUuid, options,
				pagingInfo,
				new DataService.GetCallback<List<VisitTask>>() {
					@Override
					public void onCompleted(List<VisitTask> visitTasksList) {
						visitTasksView.setOpenVisitTasks(visitTasksList);

						// get closed tasks
						visitTaskDataService.getAll("CLOSED", patientUUID, visitUuid, options,
								pagingInfo,
								new DataService.GetCallback<List<VisitTask>>() {
									@Override
									public void onCompleted(List<VisitTask> visitTasksList) {
										visitTasksView.showTabSpinner(false);
										visitTasksView.setClosedVisitTasks(visitTasksList);
										removeRefreshIndicatorIfAllCallsComplete();
									}

									@Override
									public void onError(Throwable t) {
										visitTasksView.showTabSpinner(false);
									}
								});
					}

					@Override
					public void onError(Throwable t) {
						visitTasksView.showTabSpinner(false);
					}
				});

	}

	private void removeRefreshIndicatorIfAllCallsComplete() {
		numberOfDataCallsCompletedForDataRefresh++;
		if (numberOfDataCallsCompletedForDataRefresh == TOTAL_NUMBER_OF_REFRESH_DATA_CALLS) {
			visitDashboardPageView.displayRefreshingData(false);
		}
	}

	@Override
	public void addVisitTasks(VisitTask visitTask) {
		DataService.GetCallback<VisitTask> getSingleCallback =
				new DataService.GetCallback<VisitTask>() {
					@Override
					public void onCompleted(VisitTask entity) {
						if (entity != null) {
							subscribe();
						}
					}

					@Override
					public void onError(Throwable t) {
						visitTasksView
								.showToast(
										ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
												.addErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		visitTaskDataService.create(visitTask, getSingleCallback);
	}

	public void updateVisitTask(VisitTask visitTask) {
		DataService.GetCallback<VisitTask> getSingleCallback = new DataService
				.GetCallback<VisitTask>() {
			@Override
			public void onCompleted(VisitTask entity) {
				if (entity != null) {
					subscribe();
				}
			}

			@Override
			public void onError(Throwable t) {
				visitTasksView
						.showToast(ApplicationConstants.entityName.VISIT_TASKS + ApplicationConstants.toastMessages
								.updateErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};
		visitTaskDataService.update(visitTask, getSingleCallback);
	}

	@Override
	public void createVisitTasksObject(String visitTask) {
		Patient patient = new Patient();
		patient.setUuid(patientUUID);

		Visit visit = new Visit();
		visit.setUuid(visitUuid);

		VisitTask visitTaskEntity = new VisitTask();

		visitTaskEntity.setName(visitTask);
		visitTaskEntity.setStatus(VisitTaskStatus.OPEN);
		visitTaskEntity.setPatient(patient);
		visitTaskEntity.setVisit(visit);

		addVisitTasks(visitTaskEntity);
		visitTasksView.clearTextField();
	}

	private void getVisit(boolean forceRefresh) {
		if (!forceRefresh) {
			visitTasksView.showTabSpinner(true);
		}
		DataService.GetCallback<Visit> getSingleCallback =
				new DataService.GetCallback<Visit>() {
					@Override
					public void onCompleted(Visit entity) {
						visitTasksView.showTabSpinner(false);
						visitTasksView.setVisit(entity);
						if (entity != null) {
							getVisitTasks(forceRefresh);
						}
					}

					@Override
					public void onError(Throwable t) {
						visitTasksView.showTabSpinner(false);
						visitTasksView
								.showToast(ApplicationConstants.entityName.VISITS + ApplicationConstants.toastMessages
										.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		/**
		 *  There is no need to force refresh the visit since that happens as part of
		 *  {@link BaseVisitPresenter#refreshBaseData(DefaultResponseCallbackListener)}  }
		 */
		visitDataService.getByUuid(visitUuid, QueryOptions.FULL_REP, getSingleCallback);
	}

	@Override
	protected void refreshDependentData() {
		numberOfDataCallsCompletedForDataRefresh = 0;
		getVisit(true);
		getPredefinedTasks(true);
	}
}
