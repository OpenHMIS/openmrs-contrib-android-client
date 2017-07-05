package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.ConceptSearchResult;
import org.openmrs.mobile.models.ConceptSearchResult_Table;

public class ConceptSearchDbService extends BaseDbService<ConceptSearchResult>
		implements DbService<ConceptSearchResult> {
	@Override
	protected ModelAdapter<ConceptSearchResult> getEntityTable() {
		return (ConceptSearchResult_Table)FlowManager.getInstanceAdapter(ConceptSearchResult.class);
	}
}
