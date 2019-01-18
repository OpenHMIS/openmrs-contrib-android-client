package org.openmrs.mobile.activities.patientdashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.IBaseDiagnosisFragment;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.CLINICAL_NOTE;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.PRIMARY_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.ObservationLocators.SECONDARY_DIAGNOSIS;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.COUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.SUBCOUNTY;
import static org.openmrs.mobile.utilities.ApplicationConstants.entityName.TELEPHONE;

public class PatientVisitsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private OnAdapterInteractionListener listener;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DateUtils.PATIENT_DASHBOARD_VISIT_DATE_FORMAT);

	private final int VIEW_TYPE_HEADER = 0;
	private final int VIEW_TYPE_ITEM = 1;
	private final int VIEW_TYPE_PROGRESS = 2;

	private List<Visit> visits;
	private LayoutInflater layoutInflater;
	private Visit activeVisit;
	private View activeVisitView;
	private IBaseDiagnosisFragment baseDiagnosisFragment;
	private boolean hasStartedDiagnoses;

	// The minimum amount of items to have below your current scroll position before loading more.
	private int visibleThreshold = 0;
	private int lastVisibleItem, totalItemCount;
	private boolean loading, fullDataSetHasBeenLoaded = false;
	private OnLoadMoreListener onLoadMoreListener;

	private Context context;
	private LinearLayoutManager primaryDiagnosisLayoutManager, secondaryDiagnosisLayoutManager;
	private LinearLayout diagnosesLayout, pastDiagnosisLayout;
	private RelativeLayout singleVisitTitle;
	private TextView clinicalNoteText;
	private TextView primaryDiagnosis;
	private TextView secondaryDiagnosis, noPatientVisits;
	private View initialDiagnosesView;
	private Patient patient;

	public PatientVisitsRecyclerAdapter(RecyclerView visitsRecyclerView, LinkedList<Visit> visits,
			Context context, IBaseDiagnosisFragment baseDiagnosisFragment, OnAdapterInteractionListener listener,
			Patient patient) {
		this.visits = visits;
		this.context = context;
		this.patient = patient;
		this.listener = listener;

		this.layoutInflater = LayoutInflater.from(context);
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
		visitsRecyclerView.setLayoutManager(linearLayoutManager);
		this.baseDiagnosisFragment = baseDiagnosisFragment;
		this.hasStartedDiagnoses = false;

		visitsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				if (fullDataSetHasBeenLoaded) {
					return;
				}

				totalItemCount = linearLayoutManager.getItemCount() - 1;
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
				if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
					// End has been reached
					// Do something
					loading = true;
					if (onLoadMoreListener != null) {
						onLoadMoreListener.onLoadMore();
					}
				}
			}
		});
	}

	// condition for header
	private boolean isPositionHeader(int position) {
		return position == 0;
	}

	// getItem -1 because we have add 1 header
	private Visit getItem(int position) {
		return visits.get(position - 1);
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position)) {
			return VIEW_TYPE_HEADER;
		} else if (visits.get(position - 1) != null) {
			return VIEW_TYPE_ITEM;
		} else {
			return VIEW_TYPE_PROGRESS;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (parent.getContext() == null) {
			return null;
		}
		if (viewType == VIEW_TYPE_HEADER) {
			View personAddressView =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.container_patient_address_info, parent,
							false);
			return new RecyclerViewHeader(personAddressView);
		} else if (viewType == VIEW_TYPE_ITEM) {
			View patientVisitView =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_visits_container, parent,
							false);
			return new PatientVisitViewHolder(patientVisitView);
		} else if (viewType == VIEW_TYPE_PROGRESS) {
			View patientProgressView =
					LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);

			return new ProgressViewHolder(patientProgressView);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof RecyclerViewHeader) {
			RecyclerViewHeader recyclerViewHeader = (RecyclerViewHeader)holder;
			updateContactInformation(recyclerViewHeader);
		} else if (holder instanceof ProgressViewHolder) {
			((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
		} else if (holder instanceof PatientVisitViewHolder) {
			boolean isActiveVisit = false;
			PatientVisitViewHolder viewHolder = (PatientVisitViewHolder)holder;
			viewHolder.setIsRecyclable(false);
			Visit visit = getItem(position);//Subtract one to get the first index taken by the header
			View singleVisitView = layoutInflater.inflate(R.layout.single_patient_visit_container, null);
			diagnosesLayout = (LinearLayout)singleVisitView.findViewById(R.id.diagnosesLayout);
			pastDiagnosisLayout = (LinearLayout)singleVisitView.findViewById(R.id.pastDiagnosisLayout);
			singleVisitTitle = (RelativeLayout)singleVisitView.findViewById(R.id.singleVisitTitle);
			diagnosesLayout.setVisibility(View.GONE);
			pastDiagnosisLayout.setVisibility(View.GONE);
			TextView visitStartDate = (TextView)singleVisitView.findViewById(R.id.startDate);
			clinicalNoteText = (TextView)singleVisitView.findViewById(R.id.clinicalNoteText);
			primaryDiagnosis = (TextView)singleVisitView.findViewById(R.id.primaryDiagnosis);
			secondaryDiagnosis = (TextView)singleVisitView.findViewById(R.id.secondaryDiagnosis);

			//Let's set the visit title
			String startDate = DATE_FORMAT.format(visit.getStartDatetime());

			if (startDate.equalsIgnoreCase(DateUtils.getDateToday(DateUtils.DATE_FORMAT))) {
				startDate = context.getString(R.string.today);
			} else if (startDate.equalsIgnoreCase(DateUtils.getDateYesterday(DateUtils.DATE_FORMAT))) {
				startDate = context.getString(R.string.yesterday);
			}

			if (visit.getStopDatetime() == null) {
				activeVisit = visit;
				isActiveVisit = true;
				singleVisitView.findViewById(R.id.active_visit_badge).setVisibility(View.VISIBLE);
				activeVisitView = singleVisitView;
				diagnosesLayout.setVisibility(View.VISIBLE);
				pastDiagnosisLayout.setVisibility(View.GONE);
				// init diagnoses
				if (initialDiagnosesView != null) {
					((ViewGroup)initialDiagnosesView.getParent()).removeView(initialDiagnosesView);
					singleVisitView = initialDiagnosesView;
				} else {
					initDiagnosesComponents(singleVisitView);
					baseDiagnosisFragment.setVisit(visit);
					hasStartedDiagnoses = false;
					initialDiagnosesView = singleVisitView;
				}
			} else {
				TextView visitDuration = (TextView)singleVisitView.findViewById(R.id.visitDuration);
				visitDuration.setText(context.getString(R.string.visit_duration,
						DateUtils.calculateTimeDifference(visit.getStartDatetime(), visit.getStopDatetime())));
				visitDuration.setVisibility(View.VISIBLE);
				pastDiagnosisLayout.setVisibility(View.VISIBLE);
				diagnosesLayout.setVisibility(View.GONE);
			}

			visitStartDate.setText(startDate);
			((TextView)singleVisitView.findViewById(R.id.visitTimeago))
					.setText(DateUtils.calculateTimeDifference(visit.getStartDatetime(), true));

			//Adding the link to the visit details page
			LinearLayout showVisitDetails = (LinearLayout)singleVisitView.findViewById(R.id.loadVisitDetails);

			if (isActiveVisit) {
				singleVisitTitle.setClickable(true);
				singleVisitTitle.setOnClickListener(v -> loadVisitDetails(visit));
				showVisitDetails.setVisibility(View.VISIBLE);
				showVisitDetails.setOnClickListener(v -> loadVisitDetails(visit));
			} else {
				showVisitDetails.setVisibility(View.GONE);
				singleVisitView.setOnClickListener(v -> loadVisitDetails(visit));
			}

			if (visit.getEncounters().size() == 0) {
				showClinicalNote(new Encounter(), singleVisitView, isActiveVisit);
			} else {
				Encounter tempEncounter = null;
				for (Encounter encounter : visit.getEncounters()) {
					if (encounter.getEncounterType() != null
							&& encounter.getEncounterType().getDisplay().equalsIgnoreCase(
									ApplicationConstants.EncounterTypeDisplays.VISIT_NOTE)) {
						if (encounter.getVoided() != null && encounter.getVoided()) {
							continue;
						}

						tempEncounter = encounter;
						if (activeVisit == visit) {
							baseDiagnosisFragment.setEncounter(encounter);
							break;
						}
					}
				}
				showClinicalNote(tempEncounter != null ? tempEncounter : new Encounter(), singleVisitView,
						isActiveVisit);
			}

			if (!hasStartedDiagnoses && isActiveVisit) {
				baseDiagnosisFragment.initializeListeners();
				baseDiagnosisFragment.setDiagnoses(activeVisit);
				hasStartedDiagnoses = true;
			}

			viewHolder.patientVisitDetailsContainer.addView(singleVisitView);
		}
	}

	public void setLoaded() {
		loading = false;
	}

	public void setFullDataSetHasBeenLoaded() {
		fullDataSetHasBeenLoaded = true;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public void destroy() {
		baseDiagnosisFragment.setVisit(null);
		baseDiagnosisFragment.setEncounter(null);
		baseDiagnosisFragment.setSearchDiagnosisView(null);
		baseDiagnosisFragment.setNoPrimaryDiagnoses(null);
		baseDiagnosisFragment.setNoSecondaryDiagnoses(null);
		baseDiagnosisFragment.setPrimaryDiagnosesRecycler(null);
		baseDiagnosisFragment.setSecondaryDiagnosesRecycler(null);
		baseDiagnosisFragment.setClinicalNoteView(null);
		baseDiagnosisFragment.setLoadingProgressBar(null);
		baseDiagnosisFragment.setDiagnosesContent(null);
		onLoadMoreListener = null;
		visits.clear();
	}

	private void initDiagnosesComponents(View view) {
		if (baseDiagnosisFragment.getSearchDiagnosisView() == null) {
			baseDiagnosisFragment.setSearchDiagnosisView(
					(AutoCompleteTextView)view.findViewById(R.id.searchDiagnosis));
			baseDiagnosisFragment.setNoPrimaryDiagnoses(
					(TextView)view.findViewById(R.id.noPrimaryDiagnosis));
			baseDiagnosisFragment.setNoSecondaryDiagnoses(
					(TextView)view.findViewById(R.id.noSecondaryDiagnosis));
			baseDiagnosisFragment.setPrimaryDiagnosesRecycler(
					(RecyclerView)view.findViewById(R.id.primaryDiagnosisRecyclerView));
			baseDiagnosisFragment.setSecondaryDiagnosesRecycler(
					(RecyclerView)view.findViewById(R.id.secondaryDiagnosisRecyclerView));
			baseDiagnosisFragment.setClinicalNoteView(
					(TextInputEditText)view.findViewById(R.id.editClinicalNote));
			baseDiagnosisFragment.setLoadingProgressBar((RelativeLayout)view.findViewById(R.id.loadingDiagnoses));
			baseDiagnosisFragment.setDiagnosesContent((LinearLayout)view.findViewById(R.id.diagnosesContent));

			primaryDiagnosisLayoutManager = new LinearLayoutManager(context);
			secondaryDiagnosisLayoutManager = new LinearLayoutManager(context);

			baseDiagnosisFragment.getPrimaryDiagnosesRecycler().setLayoutManager(primaryDiagnosisLayoutManager);
			baseDiagnosisFragment.getSecondaryDiagnosesRecycler().setLayoutManager(secondaryDiagnosisLayoutManager);
		}
	}

	public void updateSavingClinicalNoteProgressBar(boolean show) {
		if (activeVisitView != null) {
			activeVisitView.findViewById(R.id.savingProgressBarView).setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}

	public void updateClinicalNoteObs(Observation observation, Encounter encounter) {
		if (baseDiagnosisFragment.getEncounter() == null) {
			baseDiagnosisFragment.setEncounter(encounter);
		}
	}

	private void loadVisitDetails(Visit visit) {
		if (listener != null) {
			listener.onVisitSelected(visit.getUuid());
		}
	}

	private void updateContactInformation(RecyclerViewHeader header) {
		String county, subCounty, address, phone;
		county = subCounty = address = phone = "";

		try {
			for (PersonAttribute personAttribute : patient.getPerson().getAttributes()) {
				if (personAttribute.getDisplay() != null) {
					String displayName = personAttribute.getDisplay().replaceAll("\\s+", "");
					if (displayName.toLowerCase().startsWith(SUBCOUNTY)) {
						subCounty = displayName.split("=")[1];
					} else if (displayName.toLowerCase().startsWith(COUNTY)) {
						county = displayName.split("=")[1];
					} else if (displayName.toLowerCase().startsWith(TELEPHONE)) {
						phone = displayName.split("=")[1];
					}
				} else if (personAttribute.getAttributeType() != null) {
					// this is helpful when a patient has been created offline and not synced up, yet
					String name = personAttribute.getAttributeType().getName();
					if (name.toLowerCase().startsWith(SUBCOUNTY)) {
						subCounty = personAttribute.getStringValue();
					} else if (name.toLowerCase().startsWith(COUNTY)) {
						county = personAttribute.getStringValue();
					} else if (name.toLowerCase().startsWith(TELEPHONE)) {
						phone = personAttribute.getStringValue();
					}
				}
			}
		} catch (Exception e) {
			OpenMRS.getInstance().getLogger().e(StringUtils.toJson(patient), e);
		}

		if (!subCounty.equalsIgnoreCase("")) {
			address += subCounty;
		}

		if (!address.equalsIgnoreCase("")) {
			address += ", " + county;
		} else {
			address += county;
		}

		header.patientAddress.setText(address);
		header.patientPhonenumber.setText(phone);

	}

	private void showClinicalNote(Encounter encounter, View view, boolean isActiveVisit) {
		setExistingDiagnosesContent(encounter, view, isActiveVisit);

		if (isActiveVisit) {
			view.findViewById(R.id.editClinicNoteContainer).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.clinicNoteTextContainer).setVisibility(View.VISIBLE);
		}

		if (clinicalNoteText.getText().length() == 0) {
			clinicalNoteText.setText(context.getString(R.string.no_clinical_note));
		}
	}

	private void setExistingDiagnosesContent(Encounter encounter, View view, boolean isActiveVisit) {
		ArrayList clinicalNoteString;
		String primaryDiagnosisString = "";
		String secondaryDiagnosisString = "";
		for (Observation observation : encounter.getObs()) {
			String observationDisplay = observation.getDisplay();
			if (observationDisplay.contains(PRIMARY_DIAGNOSIS)) {
				if (!primaryDiagnosisString.equalsIgnoreCase("")) {
					primaryDiagnosisString += ", ";
				}
				primaryDiagnosisString += (StringUtils.getConceptName(observationDisplay));
				primaryDiagnosis.setText(primaryDiagnosisString);
			} else if (observationDisplay.contains(SECONDARY_DIAGNOSIS)) {
				if (!secondaryDiagnosisString.equalsIgnoreCase("")) {
					secondaryDiagnosisString += ", ";
				}
				secondaryDiagnosisString += (StringUtils.getConceptName(observationDisplay));
				secondaryDiagnosis.setText(secondaryDiagnosisString);
			} else if (observationDisplay.contains(CLINICAL_NOTE)) {
				clinicalNoteString = StringUtils.splitStrings(observationDisplay, CLINICAL_NOTE);
				if (isActiveVisit) {
					baseDiagnosisFragment.getClinicalNoteView().setText(clinicalNoteString.get(1).toString());
					view.findViewById(R.id.clinicalNoteTitle).setVisibility(View.VISIBLE);
				} else {
					view.findViewById(R.id.clinicalNoteTitle).setVisibility(View.VISIBLE);
					clinicalNoteText.setText(clinicalNoteString.get(1).toString());
				}
			}
		}
	}

	@Override
	public int getItemCount() {
		return visits == null ? 0 : visits.size() + 1;//Add an index for the header
	}

	public interface OnLoadMoreListener {
		void onLoadMore();
	}

	private class RecyclerViewHeader extends RecyclerView.ViewHolder {
		TextView patientAddress;
		TextView patientPhonenumber;

		RecyclerViewHeader(View itemView) {
			super(itemView);
			this.patientAddress = (TextView)itemView.findViewById(R.id.patientAddress);
			this.patientPhonenumber = (TextView)itemView.findViewById(R.id.patientPhonenumber);
		}
	}

	private class PatientVisitViewHolder extends RecyclerView.ViewHolder {
		LinearLayout patientVisitDetailsContainer;

		PatientVisitViewHolder(View view) {
			super(view);
			patientVisitDetailsContainer = (LinearLayout)view.findViewById(R.id.patientVisitDetailsContainer);
		}
	}

	public class ProgressViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public ProgressViewHolder(View view) {
			super(view);
			progressBar = (ProgressBar)view.findViewById(R.id.progressBarItem);
		}
	}

	public interface OnAdapterInteractionListener {

		void onVisitSelected(String visitUuid);
	}
}