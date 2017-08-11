package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Concept_Table;

import java.util.List;

import javax.inject.Inject;

public class ConceptDbService extends BaseDbService<Concept> implements DbService<Concept> {
	@Inject
	public ConceptDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Concept> getEntityTable() {
		return (Concept_Table)FlowManager.getInstanceAdapter(Concept.class);
	}

	public List<Concept> getByName(@NonNull String conceptName, @Nullable QueryOptions options) {
		return null;
	}
}
