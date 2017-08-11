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

import android.support.annotation.NonNull;
import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
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
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitAttribute;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.utilities.ApplicationConstants;
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
		this(addEditVisitView, patientUuid, visitUuid, isEndVisit, null, null, null, null, null, null);
	}

	public AddEditVisitPresenter(@NonNull AddEditVisitContract.View addEditVisitView, String patientUuid,
			String visitUuid, boolean isEndVisit, VisitDataService visitDataService, PatientDataService patientDataService,
			VisitTypeDataService visitTypeDataService, VisitAttributeTypeDataService visitAttributeTypeDataService,
			ConceptAnswerDataService conceptAnswerDataService, LocationDataService locationDataService) {
		super();

		this.addEditVisitView = addEditVisitView;
		this.addEditVisitView.setPresenter(this);
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;
		this.isEndVisit = isEndVisit;

		this.visit = new Visit();

		if (visitDataService == null) {
			this.visitDataService = dataAccess().visit();
		} else {
			this.visitDataService = visitDataService;
		}

		if (visitAttributeTypeDataService == null) {
			this.visitAttributeTypeDataService = dataAccess().visitAttributeType();
		} else {
			this.visitAttributeTypeDataService = visitAttributeTypeDataService;
		}

		if (visitTypeDataService == null) {
			this.visitTypeDataService = dataAccess().visitType();
		} else {
			this.visitTypeDataService = visitTypeDataService;
		}

		if (patientDataService == null) {
			this.patientDataService = dataAccess().patient();
		} else {
			this.patientDataService = patientDataService;
		}

		if (conceptAnswerDataService == null) {
			this.conceptAnswerDataService = dataAccess().conceptAnswer();
		} else {
			this.conceptAnswerDataService = conceptAnswerDataService;
		}

		if (locationDataService == null) {
			this.locationDataService = dataAccess().location();
		} else {
			this.locationDataService = locationDataService;
		}
	}

	@Override
	public void subscribe() {
		loadPatient();
		getLocation();
	}

	private void loadPatient() {
		if(patient != null){
			return;
		}

		addEditVisitView.showPageSpinner(true);
		if (StringUtils.notEmpty(patientUuid)) {
			patientDataService
					.getByUuid(patientUuid, QueryOptions.FULL_REP, new DataService.GetCallback<Patient>() {
						@Override
						public void onCompleted(Patient entity) {
							if (visitUuid != null && visitUuid.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING)) {
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
		visit.setAttributes(attributes);
		if (null != location) {
			visit.setLocation(location.getParentLocation());
		}

		setProcessing(true);

		visitDataService.create(visit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.setSpinnerVisibility(false);
				addEditVisitView.showVisitDetails(entity.getUuid(), true);
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
	public void updateVisit(List<VisitAttribute> attributes) {
		Visit updatedVisit = new Visit();
		updatedVisit.setAttributes(attributes);
		updatedVisit.setVisitType(visit.getVisitType());
		updatedVisit.setStartDatetime(visit.getStartDatetime());

		if (visit.getStopDatetime() != null) {
			updatedVisit.setStopDatetime(visit.getStopDatetime());
		}

		setProcessing(true);
		visitDataService.updateVisit(visit.getUuid(), updatedVisit, new DataService.GetCallback<Visit>() {
			@Override
			public void onCompleted(Visit entity) {
				setProcessing(false);
				addEditVisitView.showVisitDetails(entity.getUuid(), false);
			}

			@Override
			public void onError(Throwable t) {
				setProcessing(false);
				addEditVisitView.showVisitDetails(null, false);
			}
		});
	}

	@Override
	public void endVisit(Visit visit) {
		if (visit.getUuid() == null) {
			return;
		} else {
			if(visit.getStopDatetime() == null) {
				visit.setStopDatetime(new Date());
			}

			visit.setPatient(null);

			visitDataService.endVisit(visit.getUuid(), visit, null, new DataService.GetCallback<Visit>() {
				@Override
				public void onCompleted(Visit entity) {
					addEditVisitView.showPatientDashboard();
				}

				@Override
				public void onError(Throwable t) {
					System.out.println(t.getLocalizedMessage());
					ToastUtil.error(t.getMessage());
				}
			});
		}
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
				if (visitAttribute.getAttributeType().getUuid().equalsIgnoreCase(visitAttributeType.getUuid())) {
					return (T)visitAttribute.getValue();
				}
			}
		}
		return null;
	}
}
