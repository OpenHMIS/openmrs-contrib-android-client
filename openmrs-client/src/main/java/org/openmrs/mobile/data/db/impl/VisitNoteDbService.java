package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.VisitNote;
import org.openmrs.mobile.models.VisitNote_Table;

import javax.inject.Inject;

public class VisitNoteDbService extends BaseDbService<VisitNote> implements DbService<VisitNote> {
	@Inject
	public VisitNoteDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<VisitNote> getEntityTable() {
		return (VisitNote_Table)FlowManager.getInstanceAdapter(VisitNote.class);
	}
}
