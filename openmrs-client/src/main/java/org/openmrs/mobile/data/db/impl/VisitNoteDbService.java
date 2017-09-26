package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.EncounterDiagnosis;
import org.openmrs.mobile.models.EncounterDiagnosis_Table;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.models.VisitNote_Table;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitNoteDbService extends BaseDbService<VisitNote> implements DbService<VisitNote> {
	private static EncounterDiagnosis_Table encounterDiagnosisTable;

	static {
		encounterDiagnosisTable = (EncounterDiagnosis_Table)FlowManager.getInstanceAdapter(EncounterDiagnosis.class);
	}

	@Inject
	public VisitNoteDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<VisitNote> getEntityTable() {
		return (VisitNote_Table)FlowManager.getInstanceAdapter(VisitNote.class);
	}

	public void deleteLocalRelatedObjects(@NonNull VisitNote visitNote) {
		checkNotNull(visitNote);

		repository.deleteAll(encounterDiagnosisTable, EncounterDiagnosis_Table.visitNote_uuid.eq(visitNote.getUuid()),
				new Method("LENGTH", EncounterDiagnosis_Table.uuid).lessThanOrEq(Resource.LOCAL_UUID_LENGTH));
	}

	public VisitNote getByVisit(Visit visit) {
		return repository.querySingle(getEntityTable(), VisitNote_Table.visit_uuid.eq(visit.getUuid()));
	}
}
