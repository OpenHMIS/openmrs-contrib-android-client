package org.openmrs.mobile.activities;

import android.util.Log;
import android.view.View;

import org.openmrs.mobile.dagger.DaggerDataAccessComponent;
import org.openmrs.mobile.dagger.DataAccessComponent;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptDataService;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitNoteDataService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;

import java.util.ArrayList;
import java.util.List;

public class BaseDiagnosisPresenter {
	private static final String TAG = BaseDiagnosisPresenter.class.getSimpleName();

	private ConceptDataService conceptDataService;
	private ObsDataService obsDataService;
	private VisitNoteDataService visitNoteDataService;
	private int page = 0;
	private int limit = 20;
	private List<String> obsUuids = new ArrayList<>();
	private DataAccessComponent dataAccess;

	public BaseDiagnosisPresenter() {
		dataAccess = DaggerDataAccessComponent.create();

		this.conceptDataService = dataAccess.concept();
		this.obsDataService = dataAccess.obs();
		this.visitNoteDataService = dataAccess.visitNote();
	}

	public void findConcept(String searchQuery, IBaseDiagnosisFragment base) {
		PagingInfo pagingInfo = new PagingInfo(page, limit);
		conceptDataService.findConcept(searchQuery,
				new QueryOptions.Builder().customRepresentation(RestConstants.Representations.DIAGNOSIS_CONCEPT).build(),
				pagingInfo, new DataService.GetCallback<List<Concept>>() {

					@Override
					public void onCompleted(List<Concept> entities) {
						if (entities.isEmpty()) {
							Concept nonCodedDiagnosis = new Concept();
							nonCodedDiagnosis.setDisplay(searchQuery);
							nonCodedDiagnosis.setValue("Non-Coded:" + searchQuery);
							entities.add(nonCodedDiagnosis);
						}
						base.getBaseDiagnosisView().setSearchDiagnoses(entities);
						base.getLoadingProgressBar().setVisibility(View.GONE);
					}

					@Override
					public void onError(Throwable t) {
						base.getLoadingProgressBar().setVisibility(View.GONE);
						Log.e(TAG, "Error finding concept: " + t.getLocalizedMessage(), t);
					}
				});
	}

	public VisitNote getVisitNote(Visit visit) {
		return visitNoteDataService.getByVisit(visit);
	}

	public void loadObs(Encounter encounter, IBaseDiagnosisFragment base) {
		obsUuids.clear();
		for (Observation obs : encounter.getObs()) {
			getObservation(obs, encounter, base);
		}
	}

	public void saveVisitNote(VisitNote visitNote, IBaseDiagnosisFragment base) {
		visitNoteDataService.save(visitNote, new DataService.GetCallback<VisitNote>() {
			@Override
			public void onCompleted(VisitNote entity) {
				base.setEncounter(entity.getEncounter());

				if (entity.getObservationUuid() != null) {
					base.setObservationUuid(entity.getObservationUuid());
				}

				if (entity.getW12() != null) {
					base.createPatientSummaryMergeDialog(entity.getW12());
				}
			}

			@Override
			public void onError(Throwable t) {
				Log.e(TAG, "Error saving visit note: " + t.getLocalizedMessage(), t);
				base.getBaseDiagnosisView().showTabSpinner(false);
			}
		});
	}

	private void getObservation(Observation obs, Encounter encounter, IBaseDiagnosisFragment base) {
		QueryOptions options = new QueryOptions.Builder()
				.customRepresentation(RestConstants.Representations.OBSERVATION)
				.build();

		obsDataService
				.getByUuid(obs.getUuid(), options, new DataService.GetCallback<Observation>() {
					@Override
					public void onCompleted(Observation entity) {
						if (entity != null) {
							obsUuids.add(entity.getUuid());
							base.getBaseDiagnosisView().createEncounterDiagnosis(entity, entity.getDisplay(),
									entity.getValueCodedName(), obsUuids.size() == encounter.getObs().size());
						}
					}

					@Override
					public void onError(Throwable t) {
						Log.e(TAG, "Error getting Observation: " + t.getLocalizedMessage(), t);
						base.getBaseDiagnosisView().showTabSpinner(false);
					}
				});
	}
}
