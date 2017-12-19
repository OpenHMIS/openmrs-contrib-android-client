package org.openmrs.mobile.activities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.activities.visit.detail.DiagnosisRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.CustomDiagnosesDropdownAdapter;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseDiagnosisFragment<T extends BasePresenterContract>
		extends ACBaseFragment<T> implements IBaseDiagnosisFragment {

	private static long SEARCH_DIAGNOSES_DELAY = 1000;
	protected List<EncounterDiagnosis> primaryDiagnoses = new ArrayList<>(), secondaryDiagnoses = new ArrayList<>();
	protected AutoCompleteTextView searchDiagnosis;
	protected RecyclerView primaryDiagnosesRecycler, secondaryDiagnosesRecycler;
	protected TextView noPrimaryDiagnoses, noSecondaryDiagnoses;
	protected RelativeLayout loadingProgressBar;
	protected LinearLayout diagnosesContent;
	protected int initialPrimaryDiagnosesListHashcode, initialSecondaryDiagnosesListHashcode, initialClinicNoteHashcode;
	protected TextInputEditText clinicalNoteView;
	protected BaseDiagnosisPresenter diagnosisPresenter = new BaseDiagnosisPresenter();
	private Timer timer;
	private Observation observation;
	private Visit visit;
	private boolean firstTimeEdit;
	private CustomFragmentDialog mergePatientSummaryDialog;
	private TextWatcher clinicalNoteListener;
	private Encounter encounter;

	@Override
	public void initializeListeners() {
		diagnosisPresenter.setCancelRunningRequest(false);
		primaryDiagnoses.clear();
		secondaryDiagnoses.clear();
		addDiagnosisListeners();

		// load patient summary merge dialog if need be
		createPatientSummaryMergeDialog(getClinicalNoteView().getText().toString());
	}

	protected IBaseDiagnosisFragment getIBaseDiagnosisFragment() {
		return this;
	}

	public abstract IBaseDiagnosisView getDiagnosisView();

	private void addDiagnosisListeners() {
		searchDiagnosis.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				loadingProgressBar.setVisibility(View.VISIBLE);
				if (timer != null) {
					timer.cancel();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (searchDiagnosis.getText().length() >= 2) {
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							diagnosisPresenter
									.findConcept(searchDiagnosis.getText().toString(), getIBaseDiagnosisFragment());
						}
					}, SEARCH_DIAGNOSES_DELAY);
				} else {
					loadingProgressBar.setVisibility(View.GONE);
				}
			}
		});

		searchDiagnosis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (ViewUtils.getInput(searchDiagnosis) != null) {
					Concept concept =
							(Concept)searchDiagnosis.getAdapter().getItem(position);
					createEncounterDiagnosis(null, ViewUtils.getInput(searchDiagnosis), concept.getValue(),
							true);

					saveVisitNote(getEncounter(), getClinicalNoteView().getText().toString(), visit, true);
				}
			}
		});
	}

	private void addClinicalNoteListener() {
		firstTimeEdit = true;

		if (clinicalNoteListener == null) {
			clinicalNoteListener = new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(final CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(final Editable s) {
					if (s.length() > 0 && !firstTimeEdit) {
						if (visit != null) {
							saveVisitNote(getEncounter(), getClinicalNoteView().getText().toString(), visit, true);
						}
					} else {
						firstTimeEdit = false;
					}
				}
			};
		}

		getClinicalNoteView().addTextChangedListener(clinicalNoteListener);
	}

	private void removeClinicalNoteListener() {
		getClinicalNoteView().removeTextChangedListener(clinicalNoteListener);
	}

	public void mergePatientSummary() {
		String updatedPatientSummary = mergePatientSummaryDialog.getEditNoteTextValue();
		saveVisitNote(getEncounter(), updatedPatientSummary, visit, false);
		setClinicalNoteText(updatedPatientSummary);
	}

	public void createPatientSummaryMergeDialog(String patientSummaryText) {
		removeClinicalNoteListener();
		// This condition will be changed. It's just a way of detecting a conflict that needs to be resolved.
		if (patientSummaryText.contains(ApplicationConstants.PatientSummary.SEARCH_PATIENT_SUMMARY_CONFLICT)) {
			CustomDialogBundle bundle = new CustomDialogBundle();
			bundle.setTitleViewMessage(getString(R.string.merge_patient_summary));
			bundle.setEditNoteTextViewMessage(patientSummaryText);
			bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.MERGE_PATIENT_SUMMARY);
			bundle.setRightButtonText(getString(R.string.dialog_button_confirm));

			mergePatientSummaryDialog = CustomFragmentDialog.newInstance(bundle);
			mergePatientSummaryDialog.show(
					getActivity().getSupportFragmentManager(), ApplicationConstants.DialogTAG.MERGE_PATIENT_SUMMARY_TAG);
		} else {
			setClinicalNoteText(patientSummaryText);
			addClinicalNoteListener();
		}
	}

	public void setDiagnoses(Visit visit) {
		loadingProgressBar.setVisibility(View.VISIBLE);
		diagnosesContent.setVisibility(View.GONE);
		if (this.visit == null) {
			this.visit = visit;
		}

		VisitNote visitNote = diagnosisPresenter.getVisitNote(visit);
		if (visitNote != null) {
			updateEncounterDiagnosis(visitNote);
			return;
		}

		if (visit.getEncounters().size() != 0) {
			for (Encounter encounter : visit.getEncounters()) {
				if (encounter.getVoided() != null && encounter.getVoided()) {
					continue;
				}

				if (encounter.getEncounterType().getUuid()
						.equalsIgnoreCase(ApplicationConstants.EncounterTypeEntity.CLINICAL_NOTE_UUID)) {
					if (encounter.getObs().size() == 0) {
						showNoDiagnoses();
					} else {
						diagnosisPresenter.loadObs(encounter, getIBaseDiagnosisFragment());
					}
					break;
				} else {
					showNoDiagnoses();
				}
			}
		} else {
			showNoDiagnoses();
		}

		initialPrimaryDiagnosesListHashcode = primaryDiagnoses.hashCode();
		initialSecondaryDiagnosesListHashcode = secondaryDiagnoses.hashCode();
	}

	public void setSearchDiagnoses(List<Concept> diagnoses) {
		CustomDiagnosesDropdownAdapter adapter =
				new CustomDiagnosesDropdownAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, diagnoses);
		filterOutExistingDiagnoses(diagnoses);
		searchDiagnosis.setAdapter(adapter);
		searchDiagnosis.showDropDown();
	}

	/**
	 * TODO: Use more effecient search algorithm
	 * @param diagnoses
	 */
	private void filterOutExistingDiagnoses(List<Concept> diagnoses) {
		List<Concept> searchDiagnosis = new ArrayList<>(diagnoses);
		List<String> existingDiagnoses = new ArrayList<>();
		for (EncounterDiagnosis primaryDiagnosis : primaryDiagnoses) {
			existingDiagnoses.add(primaryDiagnosis.getDisplay());
		}

		for (EncounterDiagnosis secondaryDiagnosis : secondaryDiagnoses) {
			existingDiagnoses.add(secondaryDiagnosis.getDisplay());
		}

		for (Concept diagnosis : searchDiagnosis) {
			for (String existingDiagnosis : existingDiagnoses) {
				if (diagnosis.getName() != null && diagnosis.getName().getName() != null &&
						diagnosis.getName().getName().equalsIgnoreCase(existingDiagnosis)
						|| diagnosis.toString().equalsIgnoreCase(existingDiagnosis)) {
					diagnoses.remove(diagnosis);
				}
			}
		}
	}

	public void updateEncounterDiagnosis(VisitNote visitNote) {
		for (EncounterDiagnosis diagnosis : visitNote.getEncounterDiagnoses()) {
			if (diagnosis.getOrder().equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
				primaryDiagnoses.add(diagnosis);
			} else {
				secondaryDiagnoses.add(diagnosis);
			}
		}

		setClinicalNoteText(visitNote.getW12());

		setRecyclerViews();
	}

	public void createEncounterDiagnosis(Observation observation, String diagnosis, String conceptNameId,
			boolean loadRecyclerView) {
		EncounterDiagnosis encounterDiagnosis = new EncounterDiagnosis();
		if (observation != null) {
			if (observation.getDisplay().startsWith(ApplicationConstants.ObservationLocators.DIAGNOSES)) {
				encounterDiagnosis.setCertainty(checkObsCertainty(observation.getDisplay()));
				encounterDiagnosis.setDisplay(observation.getDiagnosisList());
				if (StringUtils.notEmpty(conceptNameId)) {
					encounterDiagnosis.setDiagnosis(ApplicationConstants.DiagnosisStrings.CONCEPT_UUID + conceptNameId);
				} else {
					encounterDiagnosis.setDiagnosis(ApplicationConstants.DiagnosisStrings.NON_CODED +
							observation.getDiagnosisList());
					encounterDiagnosis.setDisplay(ApplicationConstants.DiagnosisStrings.NON_CODED +
							observation.getDiagnosisList());
				}

				if (diagnosis.contains(ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS)) {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
					primaryDiagnoses.add(encounterDiagnosis);

				} else {
					encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
					secondaryDiagnoses.add(encounterDiagnosis);
				}

				encounterDiagnosis.setExistingObs(observation.getUuid());
			} else if (observation.getDisplay().startsWith(ApplicationConstants.ObservationLocators.CLINICAL_NOTE)) {
				setObservation(observation);
			}
		} else {
			encounterDiagnosis.setCertainty(ApplicationConstants.DiagnosisStrings.PRESUMED);
			encounterDiagnosis.setDisplay(diagnosis);
			encounterDiagnosis.setDiagnosis(conceptNameId);
			encounterDiagnosis.setExistingObs(null);
			if (primaryDiagnoses.isEmpty()) {
				encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER);
				primaryDiagnoses.add(encounterDiagnosis);
			} else {
				encounterDiagnosis.setOrder(ApplicationConstants.DiagnosisStrings.SECONDARY_ORDER);
				secondaryDiagnoses.add(encounterDiagnosis);
			}
		}

		if (loadRecyclerView) {
			setRecyclerViews();
		}
	}

	public void setPrimaryDiagnosis(EncounterDiagnosis primaryDiagnosis) {
		if (removeDiagnosis(primaryDiagnosis, secondaryDiagnoses)) {
			primaryDiagnoses.add(primaryDiagnosis);
		}

		setRecyclerViews();
	}

	public void setSecondaryDiagnosis(EncounterDiagnosis secondaryDiagnosis) {
		if (removeDiagnosis(secondaryDiagnosis, primaryDiagnoses)) {
			secondaryDiagnoses.add(secondaryDiagnosis);
		}

		setRecyclerViews();
	}

	public void setDiagnosisCertainty(EncounterDiagnosis diagnosisCertainty) {
		if (diagnosisCertainty.getOrder().equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnoses.size(); i++) {
				if (primaryDiagnoses.get(i).getUuid().equalsIgnoreCase(diagnosisCertainty.getUuid())) {
					primaryDiagnoses.remove(i);
					primaryDiagnoses.add(i, diagnosisCertainty);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnoses.size(); i++) {
				if (secondaryDiagnoses.get(i).getUuid().equalsIgnoreCase(diagnosisCertainty.getUuid())) {
					secondaryDiagnoses.remove(i);
					secondaryDiagnoses.add(i, diagnosisCertainty);
				}
			}
		}
		setRecyclerViews();
	}

	public void removeDiagnosis(EncounterDiagnosis removeDiagnosis, String order) {
		if (order.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			removeDiagnosis(removeDiagnosis, primaryDiagnoses);
		} else {
			removeDiagnosis(removeDiagnosis, secondaryDiagnoses);
		}

		setRecyclerViews();
	}

	private boolean removeDiagnosis(EncounterDiagnosis removeDiagnosis, List<EncounterDiagnosis> diagnoses) {
		int index = -1;
		for (EncounterDiagnosis encounterDiagnosis : diagnoses) {
			if (encounterDiagnosis.getUuid().equalsIgnoreCase(removeDiagnosis.getUuid())) {
				index = diagnoses.indexOf(removeDiagnosis);
			}
		}

		if (index > -1) {
			diagnoses.remove(index);
			return true;
		}

		return false;
	}

	private void setRecyclerViews() {
		if (primaryDiagnoses.isEmpty()) {
			primaryDiagnosesRecycler.setVisibility(View.GONE);
			noPrimaryDiagnoses.setVisibility(View.VISIBLE);
		} else {
			primaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			noPrimaryDiagnoses.setVisibility(View.GONE);
		}

		if (secondaryDiagnoses.isEmpty()) {
			secondaryDiagnosesRecycler.setVisibility(View.GONE);
			noSecondaryDiagnoses.setVisibility(View.VISIBLE);
		} else {
			secondaryDiagnosesRecycler.setVisibility(View.VISIBLE);
			noSecondaryDiagnoses.setVisibility(View.GONE);
		}

		primaryDiagnosesRecycler.setAdapter(
				new DiagnosisRecyclerViewAdapter(getActivity(), primaryDiagnoses, getEncounter(),
						getClinicalNoteView().getText().toString(), visit, getDiagnosisView()));

		secondaryDiagnosesRecycler.setAdapter(
				new DiagnosisRecyclerViewAdapter(getActivity(), secondaryDiagnoses, getEncounter(),
						getClinicalNoteView().getText().toString(), visit, getDiagnosisView()));

		// clear auto-complete input field
		searchDiagnosis.setText(ApplicationConstants.EMPTY_STRING);
		loadingProgressBar.setVisibility(View.GONE);
		diagnosesContent.setVisibility(View.VISIBLE);
	}

	public void saveVisitNote(VisitNote visitNote,  boolean scheduleTask) {
		diagnosisPresenter.saveVisitNote(visitNote, getIBaseDiagnosisFragment(), scheduleTask);
	}

	public void saveVisitNote(Encounter encounter, String clinicalNote, Visit visit, boolean scheduleTask) {
		saveVisitNote(createVisitNote(encounter, clinicalNote, visit), scheduleTask);
	}

	protected VisitNote createVisitNote(Encounter encounter, String clinicalNote, Visit visit) {
		List<EncounterDiagnosis> encounterDiagnoses = new ArrayList<>();
		VisitNote visitNote = new VisitNote();
		visitNote.setUuid(visit.getUuid());
		visitNote.setPersonId(visit.getPatient().getUuid());
		visitNote.setHtmlFormId(ApplicationConstants.EncounterTypeEntity.VISIT_NOTE_FORM_ID);
		visitNote.setCreateVisit("false");
		visitNote.setFormModifiedTimestamp(String.valueOf(System.currentTimeMillis()));
		visitNote.setEncounterModifiedTimestamp("0");
		visitNote.setVisit(visit);
		visitNote.setReturnUrl(ApplicationConstants.EMPTY_STRING);
		visitNote.setCloseAfterSubmission(ApplicationConstants.EMPTY_STRING);
		visitNote.setEncounter(encounter);
		visitNote.setW1(OpenMRS.getInstance().getCurrentUserUuid());
		visitNote.setW3(OpenMRS.getInstance().getParentLocationUuid());
		visitNote.setW5(DateUtils.convertTime(visit.getStartDatetime().getTime(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
		visitNote.setW10(clinicalNote == null ? ApplicationConstants.EMPTY_STRING : clinicalNote);
		visitNote.setW12(clinicalNote == null ? ApplicationConstants.EMPTY_STRING : clinicalNote);

		if (getObservation() != null) {
			visitNote.setObservation(getObservation());
		}

		encounterDiagnoses.addAll(primaryDiagnoses);
		encounterDiagnoses.addAll(secondaryDiagnoses);

		visitNote.setEncounterDiagnoses(encounterDiagnoses);

		return visitNote;
	}

	private void showNoDiagnoses() {
		noPrimaryDiagnoses.setVisibility(View.VISIBLE);
		noSecondaryDiagnoses.setVisibility(View.VISIBLE);
		primaryDiagnosesRecycler.setVisibility(View.GONE);
		secondaryDiagnosesRecycler.setVisibility(View.GONE);
		loadingProgressBar.setVisibility(View.GONE);
		diagnosesContent.setVisibility(View.VISIBLE);
	}

	private String checkObsCertainty(String obsDisplay) {
		if (obsDisplay.contains(ApplicationConstants.ObservationLocators
				.PRESUMED_DIAGNOSIS)) {
			return ApplicationConstants.DiagnosisStrings.PRESUMED;
		} else {
			return ApplicationConstants.DiagnosisStrings.CONFIRMED;
		}
	}

	private void setClinicalNoteText(String text) {
		getClinicalNoteView().setText(text);
		// position cursor at the end of text
		getClinicalNoteView().setSelection(text.length());
	}

	@Override
	public void setPresenter(T presenter) {
		mPresenter = presenter;
	}

	@Override
	public AutoCompleteTextView getSearchDiagnosisView() {
		return searchDiagnosis;
	}

	@Override
	public void setSearchDiagnosisView(AutoCompleteTextView searchDiagnosis) {
		this.searchDiagnosis = searchDiagnosis;
	}

	@Override
	public TextInputEditText getClinicalNoteView() {
		return clinicalNoteView;
	}

	@Override
	public void setClinicalNoteView(TextInputEditText clinicalNoteView) {
		this.clinicalNoteView = clinicalNoteView;
	}

	@Override
	public TextView getNoPrimaryDiagnoses() {
		return noPrimaryDiagnoses;
	}

	@Override
	public void setNoPrimaryDiagnoses(TextView view) {
		this.noPrimaryDiagnoses = view;
	}

	@Override
	public TextView getNoSecondaryDiagnoses() {
		return noSecondaryDiagnoses;
	}

	@Override
	public void setNoSecondaryDiagnoses(TextView view) {
		this.noSecondaryDiagnoses = view;
	}

	@Override
	public RecyclerView getPrimaryDiagnosesRecycler() {
		return primaryDiagnosesRecycler;
	}

	@Override
	public void setPrimaryDiagnosesRecycler(RecyclerView view) {
		this.primaryDiagnosesRecycler = view;
	}

	@Override
	public RecyclerView getSecondaryDiagnosesRecycler() {
		return secondaryDiagnosesRecycler;
	}

	@Override
	public void setSecondaryDiagnosesRecycler(RecyclerView view) {
		this.secondaryDiagnosesRecycler = view;
	}

	@Override
	public RelativeLayout getLoadingProgressBar() {
		return loadingProgressBar;
	}

	@Override
	public void setLoadingProgressBar(RelativeLayout view) {
		this.loadingProgressBar = view;
	}

	@Override
	public LinearLayout getDiagnosesContent() {
		return diagnosesContent;
	}

	public void setDiagnosesContent(LinearLayout diagnosesContent) {
		this.diagnosesContent = diagnosesContent;
	}

	@Override
	public Encounter getEncounter() {
		return encounter;
	}

	@Override
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	@Override
	public Visit getVisit() {
		return visit;
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	@Override
	public Observation getObservation() {
		return observation;
	}

	@Override
	public void setObservation(Observation observation) {
		this.observation = observation;
	}

}
