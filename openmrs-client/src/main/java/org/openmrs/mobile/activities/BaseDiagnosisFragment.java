package org.openmrs.mobile.activities;

import android.os.Handler;
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

import org.openmrs.mobile.activities.visit.detail.DiagnosisRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
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

	private static long SEARCH_DIAGNOSES_DELAY = 1000, SAVE_CLINICAL_NOTE_DELAY = 2500;
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
	private String encounterUuid;
	private Visit visit;
	private boolean firstTimeEdit;
	private long lastTextEdit = 0;

	@Override
	public void initializeListeners() {
		primaryDiagnoses.clear();
		secondaryDiagnoses.clear();
		addDiagnosisListeners();
		addClinicalNoteListener();
	}

	protected IBaseDiagnosisFragment getIBaseDiagnosisFragment() {
		return this;
	}

	abstract public IBaseDiagnosisView getDiagnosisView();

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

					getDiagnosisView().saveVisitNote(encounterUuid, clinicalNoteView.getText().toString(), visit);
				}
			}
		});
	}

	private void addClinicalNoteListener() {
		firstTimeEdit = true;

		Handler handler = new Handler();
		Runnable inputCompleteChecker = () -> {
			if (System.currentTimeMillis() > (lastTextEdit + SAVE_CLINICAL_NOTE_DELAY)) {
				saveVisitNote(null != getEncounterUuid() ? getEncounterUuid() : ApplicationConstants.EMPTY_STRING,
						clinicalNoteView.getText().toString(), visit);
			}
		};

		clinicalNoteView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(final CharSequence s, int start, int before, int count) {
				//Remove this to run only once
				handler.removeCallbacks(inputCompleteChecker);
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.length() > 0 && !firstTimeEdit) {
					lastTextEdit = System.currentTimeMillis();
					handler.postDelayed(inputCompleteChecker, SAVE_CLINICAL_NOTE_DELAY);
				} else {
					firstTimeEdit = false;
				}
			}
		});
	}

	public void setDiagnoses(Visit visit) {
		loadingProgressBar.setVisibility(View.VISIBLE);
		diagnosesContent.setVisibility(View.GONE);
		if (this.visit == null) {
			this.visit = visit;
		}

		if (visit.getEncounters().size() != 0) {
			for (Encounter encounter : visit.getEncounters()) {
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
				if (null != diagnosis.getName() &&
						diagnosis.getName().getName().equalsIgnoreCase(existingDiagnosis)
						|| diagnosis.toString().equalsIgnoreCase(existingDiagnosis)) {
					diagnoses.remove(diagnosis);
				}
			}
		}
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
		for (int i = 0; i < secondaryDiagnoses.size(); i++) {
			if (secondaryDiagnoses.get(i) == primaryDiagnosis) {
				secondaryDiagnoses.remove(i);
				primaryDiagnoses.add(primaryDiagnosis);
			}
		}
		setRecyclerViews();
	}

	public void setSecondaryDiagnosis(EncounterDiagnosis secondaryDiagnosis) {
		for (int i = 0; i < primaryDiagnoses.size(); i++) {
			if (primaryDiagnoses.get(i) == secondaryDiagnosis) {
				primaryDiagnoses.remove(i);
				secondaryDiagnoses.add(secondaryDiagnosis);
			}
		}
		setRecyclerViews();
	}

	public void setDiagnosisCertainty(EncounterDiagnosis diagnosisCertainty) {
		if (diagnosisCertainty.getOrder().equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnoses.size(); i++) {
				if (primaryDiagnoses.get(i) == diagnosisCertainty) {
					primaryDiagnoses.remove(i);
					primaryDiagnoses.add(i, diagnosisCertainty);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnoses.size(); i++) {
				if (secondaryDiagnoses.get(i) == diagnosisCertainty) {
					secondaryDiagnoses.remove(i);
					secondaryDiagnoses.add(i, diagnosisCertainty);
				}
			}
		}
		setRecyclerViews();
	}

	public void removeDiagnosis(EncounterDiagnosis removeDiagnosis, String order) {
		if (order.equalsIgnoreCase(ApplicationConstants.DiagnosisStrings.PRIMARY_ORDER)) {
			for (int i = 0; i < primaryDiagnoses.size(); i++) {
				if (primaryDiagnoses.get(i) == removeDiagnosis) {
					primaryDiagnoses.remove(i);
				}
			}
		} else {
			for (int i = 0; i < secondaryDiagnoses.size(); i++) {
				if (secondaryDiagnoses.get(i) == removeDiagnosis) {
					secondaryDiagnoses.remove(i);
				}
			}
		}
		setRecyclerViews();
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
				new DiagnosisRecyclerViewAdapter(getActivity(), primaryDiagnoses, getEncounterUuid(),
						getClinicalNoteView().getText().toString(), visit, getDiagnosisView()));

		secondaryDiagnosesRecycler.setAdapter(
				new DiagnosisRecyclerViewAdapter(getActivity(), secondaryDiagnoses, getEncounterUuid(),
						getClinicalNoteView().getText().toString(), visit, getDiagnosisView()));

		// clear auto-complete input field
		searchDiagnosis.setText(ApplicationConstants.EMPTY_STRING);
		loadingProgressBar.setVisibility(View.GONE);
		diagnosesContent.setVisibility(View.VISIBLE);
	}

	public void saveVisitNote(VisitNote visitNote) {
		diagnosisPresenter.saveVisitNote(visitNote, getIBaseDiagnosisFragment());
	}

	public void saveVisitNote(String encounterUuid, String clinicalNote, Visit visit) {
		saveVisitNote(createVisitNote(encounterUuid, clinicalNote, visit));
	}

	protected VisitNote createVisitNote(String encounterUuid, String clinicalNote, Visit visit) {
		List<EncounterDiagnosis> encounterDiagnoses = new ArrayList<>();
		VisitNote visitNote = new VisitNote();
		visitNote.setPersonId(visit.getPatient().getUuid());
		visitNote.setHtmlFormId(ApplicationConstants.EncounterTypeEntity.VISIT_NOTE_FORM_ID);
		visitNote.setCreateVisit("false");
		visitNote.setFormModifiedTimestamp(String.valueOf(System.currentTimeMillis()));
		visitNote.setEncounterModifiedTimestamp("0");
		visitNote.setVisitId(visit.getUuid());
		visitNote.setReturnUrl(ApplicationConstants.EMPTY_STRING);
		visitNote.setCloseAfterSubmission(ApplicationConstants.EMPTY_STRING);
		visitNote.setEncounterId(encounterUuid == null ? ApplicationConstants.EMPTY_STRING : encounterUuid);
		visitNote.setW1(OpenMRS.getInstance().getCurrentUserUuid());
		visitNote.setW3(OpenMRS.getInstance().getParentLocationUuid());
		visitNote.setW5(DateUtils.convertTime(visit.getStartDatetime().getTime(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
		visitNote.setW10(ApplicationConstants.EMPTY_STRING);
		visitNote.setW12(null == clinicalNote ? ApplicationConstants.EMPTY_STRING : clinicalNote);

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
	public String getEncounterUuid() {
		return encounterUuid;
	}

	@Override
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
	}

	@Override
	public Visit getVisit() {
		return visit;
	}

	@Override
	public void setVisit(Visit visit) {
		this.visit = visit;
	}
}
