package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.ConceptClass;
import org.openmrs.mobile.models.ConceptClass_Table;

import javax.inject.Inject;

public class ConceptClassDbService extends BaseDbService<ConceptClass> {
	@Inject
	public ConceptClassDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<ConceptClass> getEntityTable() {
		return (ConceptClass_Table)FlowManager.getInstanceAdapter(ConceptClass.class);
	}
}
