/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.addeditvisit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.openmrs.mobile.utilities.ApplicationConstants.EncounterTypeDisplays.AUDITDATA;
import static org.openmrs.mobile.utilities.ApplicationConstants.toastMessages.SAVE_VISIT_END_DATE_ERROR;

import androidx.annotation.NonNull;
import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dagger.DataAccessComponent;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddEditVisitPresenter extends BasePresenter implements AddEditVisitContract.Presenter {

	@NonNull
	private AddEditVisitContract.View addEditVisitView;

	private Visit visit;
	private VisitAttributeTypeDataService visitAttributeTypeDataService;
	private VisitTypeDataService visitTypeDataService;
	private ConceptAnswerDataService conceptAnswerDataService;
	private VisitDataService visitDataService;
	private PatientDataService patientDataService;
	private LocationDataService locationDataService;
	private boolean processing, isEndVisit;
	private String patientUuid, visitUuid;
	private Location location;
	private Patient patient;

	public AddEditVisitPresenter(@NonNull AddEditVisitContract.View addEditVisitView, String patientUuid,
			String visitUuid, boolean isEndVisit) {
		this(addEditVisitView, patientUuid, visitUuid, isEndVisit, null);
	}

	public AddEditVisitPresenter(@NonNull AddEditVisitContract.View addEditVisitView, String patientUuid,
			String visitUuid, boolean isEndVisit, DataAccessComponent dataAccessComponent) {
		super();

		this.addEditVisitView = addEditVisitView;
		this.addEditVisitView.setPresenter(this);
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;
		this.isEndVisit = isEndVisit;

		this.visit = new Visit();

		DataAccessComponent dataAccess = dataAccessComponent;
		if (dataAccess == null) {
			dataAccess = this.dataAccess();
		}

		this.visitDataService = dataAccess.visit();
		this.visitAttributeTypeDataService = dataAccess.visitAttributeType();
		this.visitTypeDataService = dataAccess.visitType();
		this.patientDataService = dataAccess.patient();
		this.conceptAnswerDataService = dataAccess.conceptAnswer();
		this.locationDataService = dataAccess.location();
	}

	@Override
	public void subscribe() {
		loadPatient();
		getLocation();
	}

	private void loadPatient() {
		if (patient != null) {
			return;
		}

		addEditVisitView.showPageSpinner(true);
		if (StringUtils.isNullOrEmpty(patientUuid)) {
			logger.e("Patient UUID is NULL on Add/Edit Visit Presenter");
			addEditVisitView.showToast(ApplicationConstants.entityName.PATIENTS +
					ApplicationConstants.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			return;
		}
		patientDataService
				.getByUuid(patientUuid, QueryOptions.FULL_REP, new DataService.GetCallback<Patient>() {
					@Override
					public void onCompleted(Patient entity) {
						if (visitUuid != null && visitUuid.isEmpty()) {
							// start visit
							visit.setPatient(entity);
							visit.setStartDatetime(new Date());
							addEditVisitView.initView(true);
							loadVisitTypes();
							loadVisitAttributeTypes();
						} else {
							// edit visit
							loadVisit();
						}
					}

					@Override
					public void onError(Throwable t) {
						addEditVisitView.showPageSpinner(false);
						ToastUtil.error(t.getMessage());
					}
				});
	}

	private void loadVisit() {
		visitDataService.getByUuid(visitUuid, QueryOptions.FULL_REP, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				if (entity != null) {
					visit = entity;
					// end visit
					if (isEndVisit) {
						if (visit.getStopDatetime() == null) {
							visit.setStopDatetime(new Date());
						}

						addEditVisitView.loadEndVisitView();
					}
				}

				addEditVisitView.initView(false);

				if (!isEndVisit) {
					loadVisitTypes();
					loadVisitAttributeTypes();
				}
			}

			@Override
			public void onError(Throwable t) {
				addEditVisitView.showPageSpinner(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public List<VisitAttributeType> loadVisitAttributeTypes() {
		final List<VisitAttributeType> visitAttributeTypes = new ArrayList<>();
		QueryOptions options = new QueryOptions.Builder()
				.cacheKey(ApplicationConstants.CacheKays.VISIT_ATTRIBUTE_TYPE)
				.customRepresentation(RestConstants.Representations.FULL)
				.build();
		visitAttributeTypeDataService
				.getAll(options, null, new DataService.GetCallback<List<VisitAttributeType>>() {
					@Override
					public void onCompleted(List<VisitAttributeType> entities) {
						visitAttributeTypes.addAll(entities);
						addEditVisitView.loadVisitAttributeTypeFields(visitAttributeTypes);
						setProcessing(false);
						addEditVisitView.showPageSpinner(false);
					}

					@Override
					public void onError(Throwable t) {
						addEditVisitView.showPageSpinner(false);
						ToastUtil.error(t.getMessage());
					}
				});

		return visitAttributeTypes;
	}

	public void loadVisitTypes() {
		QueryOptions options = new QueryOptions.Builder()
				.cacheKey(ApplicationConstants.CacheKays.VISIT_TYPE)
				.build();

		visitTypeDataService
				.getAll(options, null, new DataService.GetCallback<List<VisitType>>() {
					@Override
					public void onCompleted(List<VisitType> entities) {
						addEditVisitView.updateVisitTypes(entities);
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	/**
	 * TODO: Move to Base class
	 */
	public void getLocation() {
		locationDataService.getByUuid(OpenMRS.getInstance().getLocation(), QueryOptions.FULL_REP,
				new DataService.GetCallback<Location>() {
					@Override
					public void onCompleted(Location entity) {
						location = entity;
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void getConceptAnswer(String uuid, Spinner dropdown) {
		conceptAnswerDataService.getByConceptUuid(uuid, null, new DataService.GetCallback<List<ConceptAnswer>>() {
			@Override
			public void onCompleted(List<ConceptAnswer> entities) {
				addEditVisitView.updateConceptAnswersView(dropdown, entities);
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public Patient getPatient() {
		if (visit != null && null != visit.getPatient()) {
			return visit.getPatient();
		}
		return null;
	}

	@Override
	public Visit getVisit() {
		return visit;
	}

	@Override
	public void startVisit(List<VisitAttribute> attributes) {
		updateExistingAttributes(attributes);
		if (location != null) {
			visit.setLocation(location.getParentLocation());
		}

		setProcessing(true);

		visitDataService.create(visit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.startVisitComplete(entity.getUuid());
			}

			@Override
			public void onError(Throwable t) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public void updateVisit(String visitEndDate, List<VisitAttribute> attributes) {
		updateExistingAttributes(attributes);

		Visit updatedVisit = new Visit();
		updatedVisit.setUuid(visit.getUuid());
		updatedVisit.setAttributes(visit.getAttributes());
		updatedVisit.setVisitType(visit.getVisitType());
		updatedVisit.setStartDatetime(visit.getStartDatetime());

		try {
			if (!StringUtils.isNullOrEmpty(visitEndDate)) {
				updatedVisit.setStopDatetime(DateUtils.SIMPLE_DATE_FORMAT.parse(visitEndDate));
			} else if (visit.getStopDatetime() != null) {
				updatedVisit.setStopDatetime(visit.getStopDatetime());
			}
		} catch (Exception e) {
			logger.e(e.getMessage(), e);
			addEditVisitView.showToast(SAVE_VISIT_END_DATE_ERROR, ToastUtil.ToastType.ERROR);
		}

		//updateExistingAttributes(attributes);

		setProcessing(true);
		visitDataService.updateVisit(visit, updatedVisit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.updateVisitComplete(entity.getUuid(), false);
			}

			@Override
			public void onError(Throwable t) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showToast(t.getMessage(), ToastUtil.ToastType.ERROR);
			}
		});
	}

	private void updateExistingAttributes(List<VisitAttribute> attributes) {
		// void existing attributes
		for (VisitAttribute visitAttribute : visit.getAttributes()) {
			visitAttribute.setVoided(true);
		}

		// append new attributes
		visit.getAttributes().addAll(attributes);
	}

	@Override
	public void endVisit() {
		// andr-405 Check if audit data has been completed before ending a visit on the app
		boolean auditDataFormCompleted = false;
		if (visit.getEncounters() != null && !visit.getEncounters().isEmpty()) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getDisplay() != null && encounter.getDisplay().contains(AUDITDATA)) {
					if (encounter.getObs() != null && !encounter.getObs().isEmpty()) {
						for (Observation obs : encounter.getObs()) {
							if (obs.getDisplay() != null
									&& (obs.getDisplay().equalsIgnoreCase("Audit Data Complete: Yes")
									|| obs.getDisplay().equalsIgnoreCase("Audit Data Complete: No"))) {
								auditDataFormCompleted = true;
								break;
							}
						}
						if (auditDataFormCompleted) {
							break;
						}
					}
				}
			}
		}

		if (!auditDataFormCompleted) {
			addEditVisitView.auditDataNotCompleted(visit.getUuid());
			return;
		}

		if (visit.getStopDatetime() == null) {
			visit.setStopDatetime(new Date());
		}

		visitDataService.endVisit(visit.getUuid(), visit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				addEditVisitView.endVisitComplete();
			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public boolean isProcessing() {
		return processing;
	}

	@Override
	public void setProcessing(boolean processing) {
		this.processing = processing;
	}

	@Override
	public boolean isEndVisit() {
		return isEndVisit;
	}

	@Override
	public <T> T searchVisitAttributeValueByType(VisitAttributeType visitAttributeType) {
		if (getVisit() != null && getVisit().getAttributes() != null) {
			for (VisitAttribute visitAttribute : getVisit().getAttributes()) {
				if(visitAttribute.getVoided() != null && visitAttribute.getVoided()) {
					continue;
				}

				if (visitAttribute.getAttributeType() != null &&
						visitAttribute.getAttributeType().getUuid()
								.equalsIgnoreCase(visitAttributeType.getUuid())) {
					return (T)visitAttribute.getValue();
				} else if (visitAttribute.getAttributeType() == null && visitAttribute.getDisplay() != null) {
					String attributeName = visitAttributeType.getName() != null ? visitAttributeType.getName() :
							visitAttributeType.getDisplay();

					if (visitAttribute.getDisplay().contains(attributeName)) {
						return (T)visitAttribute.getDisplay().split(": ")[1];
					}
				}
			}
		}
		return null;
	}
}
