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

package org.openmrs.mobile.activities.addeditpatient;

import android.util.Log;
import android.widget.Spinner;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.PatientIdentifierTypeDataService;
import org.openmrs.mobile.data.impl.PersonAttributeTypeDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {

	private final AddEditPatientContract.View patientRegistrationView;

	private PatientDataService patientDataService;
	private ConceptDataService conceptDataService;
	private PersonAttributeTypeDataService personAttributeTypeDataService;
	private PatientIdentifierTypeDataService patientIdentifierTypeDataService;
	private LocationDataService locationDataService;
	private Patient patient;
	private String patientToUpdateUuid;
	private List<String> mCounties;
	private boolean registeringPatient = false;
	private OpenMRS instance = OpenMRS.getInstance();
	private String locationUuid;
	private String conceptUuid;

	public AddEditPatientPresenter(AddEditPatientContract.View patientRegistrationView, List<String> counties,
			String patientToUpdateUuid) {
		this(patientRegistrationView, counties, patientToUpdateUuid, null);
	}

	public AddEditPatientPresenter(AddEditPatientContract.View patientRegistrationView, List<String> counties,
			String patientToUpdateUuid, Patient patient) {
		super();

		this.patientDataService = dataAccess().patient();
		this.conceptDataService = dataAccess().concept();
		this.patientIdentifierTypeDataService = dataAccess().patientIdentifierType();
		this.personAttributeTypeDataService = dataAccess().personAttributeType();
		this.locationDataService = dataAccess().location();

		this.patientRegistrationView = patientRegistrationView;
		this.patientRegistrationView.setPresenter(this);
		this.mCounties = counties;
		this.patient = patient;
		this.patientToUpdateUuid = patientToUpdateUuid;
		this.mCounties = counties;

	}

	private boolean validate(Patient patient) {

		boolean familyNameError = false;
		boolean lastNameError = false;
		boolean dateOfBirthError = false;
		boolean genderError = false;
		boolean countyError = false;
		boolean patientFileNumberError = false;
		boolean civilStatusError = false;
		boolean occupationError = false;
		boolean subCountyError = false;
		boolean nationalityError = false;
		boolean patientIdNoError = false;
		boolean clinicError = false;
		boolean wardError = false;
		boolean phonenumberError = false;
		boolean kinNameError = false;
		boolean kinRelationshipError = false;
		boolean kinPhonenumberError = false;
		boolean kinResidenceError = false;

		patientRegistrationView
				.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, countyError, genderError,
						patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
						patientIdNoError, clinicError, wardError, phonenumberError, kinNameError, kinRelationshipError,
						kinPhonenumberError, kinResidenceError);

		// Validate names
		if (StringUtils.isBlank(patient.getPerson().getName().getGivenName())) {
			familyNameError = true;
		}
		if (StringUtils.isBlank(patient.getPerson().getName().getFamilyName())) {
			lastNameError = true;
		}

		// Validate date of birth
		if (StringUtils.isBlank(patient.getPerson().getBirthdate())) {
			dateOfBirthError = true;
		}

		// Validate gender
		if (StringUtils.isBlank(patient.getPerson().getGender())) {
			genderError = true;
		}

		//Validate File Number
		if (StringUtils.isBlank(patient.getIdentifier().getIdentifier())) {
			patientFileNumberError = true;
		}

		// Validate telephone number
		if (patient.getPerson().getAttributes() != null && patient.getPerson().getAttributes().size() > 0) {
			for (PersonAttribute personAttribute : patient.getPerson().getAttributes()) {
				if (personAttribute.getAttributeType().getUuid().equalsIgnoreCase(ApplicationConstants
						.RequiredPersonAttributes.TELEPHONE_NUMBER_UUID)) {
					phonenumberError = personAttribute.getValue() == null;
					break;
				} else {
					phonenumberError = true;
				}
			}
		} else {
			phonenumberError = true;
		}

		boolean result =
				!familyNameError && !lastNameError && !dateOfBirthError && !countyError && !genderError
						&& !patientFileNumberError && !civilStatusError && !occupationError && !subCountyError &&
						!nationalityError && !patientIdNoError && !clinicError && !wardError && !phonenumberError
						&& !kinNameError && !kinRelationshipError && !kinPhonenumberError && !kinResidenceError;
		if (result) {
			this.patient = patient;
			return true;
		} else {
			patientRegistrationView
					.setErrorsVisibility(familyNameError, lastNameError, dateOfBirthError, countyError, genderError,
							patientFileNumberError, civilStatusError, occupationError, subCountyError, nationalityError,
							patientIdNoError, clinicError, wardError, phonenumberError, kinNameError, kinRelationshipError,
							kinPhonenumberError, kinResidenceError);
			return false;
		}
	}

	@Override
	public void subscribe() {
		if (!patientToUpdateUuid.isEmpty()) {
			getPatientToUpdate(patientToUpdateUuid);
		} else {
			getPersonAttributeTypes();
		}
	}

	@Override
	public void getPatientToUpdate(String patientToUpdateUuid) {
		patientRegistrationView.showPageSpinner(true);
		DataService.GetCallback<Patient> singleCallback = new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient entity) {
				patientRegistrationView.showPageSpinner(false);
				if (entity != null) {
					patientRegistrationView.fillFields(entity);
					setPatient(entity);
					getPersonAttributeTypes();
				} else {
					patientRegistrationView.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants
							.toastMessages.fetchWarningMessage, ToastUtil.ToastType.WARNING);
				}
			}

			@Override
			public void onError(Throwable t) {
				patientRegistrationView.showPageSpinner(false);
				Log.e("User Error", "Error", t.fillInStackTrace());
				patientRegistrationView.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants
						.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};

		//Just check if the identifier are the same. If not it saves the patient.
		patientDataService.getByUuid(patientToUpdateUuid, QueryOptions.FULL_REP, singleCallback);
	}

	@Override
	public void confirmPatient(Patient patient) {
		if (!registeringPatient && validate(patient)) {
			patientRegistrationView.hideSoftKeys();
			registeringPatient = true;
			if (Resource.isLocalUuid(patient.getUuid())) {
				findSimilarPatients(patient);
			} else {
				addEditPatient(patient);
			}
		} else {
			patientRegistrationView.showPageSpinner(false);
			patientRegistrationView.scrollToTop();
		}
	}

	@Override
	public void finishAddPatientActivity() {
		patientRegistrationView.finishAddPatientActivity();
	}

	@Override
	public void addEditPatient(Patient patient) {
		patientRegistrationView.showPageSpinner(true);
		setRegistering(true);
		DataService.GetCallback<Patient> getSingleCallback = new DataService.GetCallback<Patient>() {
			@Override
			public void onCompleted(Patient entity) {
				setRegistering(false);
				if (entity != null) {
					patientRegistrationView.finishAddPatientActivity();
					patientRegistrationView.startPatientDashboardActivity(entity);
				} else {
					patientRegistrationView
							.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
									.addWarningMessage, ToastUtil.ToastType.WARNING);
				}
			}

			@Override
			public void onError(Throwable t) {
				setRegistering(false);
				patientRegistrationView.showPageSpinner(false);
				patientRegistrationView
						.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants.toastMessages
								.addErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};

		if (Resource.isLocalUuid(patient.getUuid())) {
			patientDataService.create(patient, getSingleCallback);
		} else {
			patientDataService.update(patient, getSingleCallback);
		}

	}

	public void findSimilarPatients(Patient patient) {
		PagingInfo pagingInfo = PagingInfo.DEFAULT;
		DataService.GetCallback<List<Patient>> callback = new DataService.GetCallback<List<Patient>>() {
			@Override
			public void onCompleted(List<Patient> patients) {
				patientRegistrationView.showPageSpinner(false);
				if (patients.isEmpty()) {
					addEditPatient(patient);
				} else {
					patientRegistrationView.showSimilarPatientDialog(patients, patient);
				}
			}

			@Override
			public void onError(Throwable t) {
				patientRegistrationView.showPageSpinner(false);
				Log.e("User Error", "Error", t.fillInStackTrace());
				patientRegistrationView.showToast(ApplicationConstants.entityName.PATIENTS + ApplicationConstants
						.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
			}
		};

		//Just check if the identifier are the same. If not it saves the patient.
		patientDataService
				.getByIdentifier(patient.getIdentifier().getIdentifier(), QueryOptions.FULL_REP, pagingInfo, callback);
	}

	@Override
	public void getConceptAnswer(String uuid, Spinner dropdown) {
		conceptDataService.getByUuid(uuid, QueryOptions.FULL_REP, new DataService.GetCallback<Concept>() {
			@Override
			public void onCompleted(Concept concept) {
				if (concept != null) {
					if (concept.getDisplay().equalsIgnoreCase(ApplicationConstants.CIVIL_STATUS_DISPLAY)) {
						dropdown.setPrompt(ApplicationConstants.CIVIL_STATUS);
					} else {
						dropdown.setPrompt(ApplicationConstants.KIN_RELATIONSHIP);
					}

					if (concept.getAnswers() != null) {
						patientRegistrationView.updateConceptAnswerView(dropdown, concept.getAnswers());
					}
				}

			}

			@Override
			public void onError(Throwable t) {
				ToastUtil.error(t.getMessage());
			}
		});
	}

	public void getPatientIdentifierTypes() {
		DataService.GetCallback<List<PatientIdentifierType>> callback =
				new DataService.GetCallback<List<PatientIdentifierType>>() {
					@Override
					public void onCompleted(List<PatientIdentifierType> entities) {
						if (!entities.isEmpty()) {
							for (int i = 0; i < entities.size(); i++) {
								if (entities.get(i).getRequired()) {
									patientRegistrationView.setPatientIdentifierType(entities.get(i));
								}
							}
						}
					}

					@Override
					public void onError(Throwable t) {
						Log.e("Identifier Type Error", "Error", t.fillInStackTrace());
						patientRegistrationView
								.showToast(ApplicationConstants.entityName.IDENTIFIER_TPYES
										+ ApplicationConstants.toastMessages
										.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		patientIdentifierTypeDataService
				.getAll(new QueryOptions.Builder().cacheKey(ApplicationConstants.CacheKays.PERSON_IDENTIFIER_TYPE).build(),
						null, callback);
	}

	@Override
	public List<PersonAttributeType> getPersonAttributeTypes() {
		patientRegistrationView.showPageSpinner(true);
		final List<PersonAttributeType> personAttributeTypes = new ArrayList<>();

		QueryOptions options = new QueryOptions.Builder()
				.cacheKey(ApplicationConstants.CacheKays.PERSON_ATTRIBUTE_TYPE)
				.customRepresentation(RestConstants.Representations.FULL)
				.build();
		personAttributeTypeDataService
				.getAll(options, null, new DataService.GetCallback<List<PersonAttributeType>>() {
					@Override
					public void onCompleted(List<PersonAttributeType> entities) {

						if (!entities.isEmpty()) {
							for (int q = 0; q < createUnwantedPersonAttributes().size(); q++) {
								String unwantedUuid = createUnwantedPersonAttributes().get(q);

								for (int i = 0; i < entities.size(); i++) {
									String uuid = entities.get(i).getUuid();
									if (uuid.equalsIgnoreCase(unwantedUuid)) {
										entities.remove(i);
									}
								}
							}
							personAttributeTypes.addAll(entities);
							patientRegistrationView.loadPersonAttributeTypes(personAttributeTypes);
							patientRegistrationView.showPageSpinner(false);
						} else {
							patientRegistrationView.showPageSpinner(true);
							patientRegistrationView
									.showToast(ApplicationConstants.entityName.ATTRIBUTE_TPYES + ApplicationConstants
											.toastMessages.fetchWarningMessage, ToastUtil.ToastType.WARNING);
						}
					}

					@Override
					public void onError(Throwable t) {
						ToastUtil.error(t.getMessage());
					}
				});
		return personAttributeTypes;
	}

	@Override
	public void getLoginLocation() {
		if (!instance.getLocation().equalsIgnoreCase(null)) {
			locationUuid = instance.getLocation();
		}
		DataService.GetCallback<Location> getSingleCallback =
				new DataService.GetCallback<Location>() {
					@Override
					public void onCompleted(Location entity) {
						if (entity != null) {
							patientRegistrationView.setLoginLocation(entity);
						}
					}

					@Override
					public void onError(Throwable t) {
						patientRegistrationView
								.showToast(ApplicationConstants.entityName.LOCATION + ApplicationConstants
										.toastMessages.fetchErrorMessage, ToastUtil.ToastType.ERROR);
					}
				};
		locationDataService.getByUuid(locationUuid, QueryOptions.FULL_REP, getSingleCallback);
	}

	@Override
	public <T> T searchPersonAttributeValueByType(PersonAttributeType personAttributeType) {
		if (getPatient() != null && getPatient().getPerson().getAttributes() != null) {
			for (PersonAttribute personAttribute : getPatient().getPerson().getAttributes()) {
				if (personAttribute.getAttributeType() != null) {
					if (personAttribute.getAttributeType().getUuid()
							.equalsIgnoreCase(personAttributeType.getUuid())) {
						if (personAttribute.getValue() == null) {
							if (personAttribute.getDisplay() != null) {
								return (T)StringUtils.extractDisplayValue(personAttribute.getDisplay(), 1);
							}

							if (personAttribute.getStringValue() != null) {
								return (T)personAttribute.getStringValue();
							}
						}

						return (T)personAttribute.getValue();
					}
				} else {
					if (personAttributeType.getDisplay()
							.equalsIgnoreCase(
									StringUtils.extractDisplayValue(
											personAttribute.getDisplay(), 0))) {
						return (T)StringUtils.extractDisplayValue(personAttribute.getDisplay(), 1);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Patient getPatient() {
		return patient;
	}

	@Override
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public boolean isRegisteringPatient() {
		return registeringPatient;
	}

	@Override
	public void setRegistering(boolean registering) {
		this.registeringPatient = registering;
	}

	private ArrayList<String> createUnwantedPersonAttributes() {
		ArrayList<String> unwantedPersonAttributes = new ArrayList<>();
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.TEST_PATIENT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.UNKNOWN_PATIENT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.RACE_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.HEALTH_CENTER_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.HEALTH_DISTRICT_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.FUNDING_SPONSOR_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.BIRTH_PLACE_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.IS_DECEASED_UUID);
		unwantedPersonAttributes.add(ApplicationConstants.unwantedPersonAttributes.FIRST_LANGUAGE_UUID);

		return unwantedPersonAttributes;
	}

}
