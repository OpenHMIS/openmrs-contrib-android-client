package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.ConceptAnswer;
import org.openmrs.mobile.models.ConceptAnswer_Table;

import java.util.List;

import javax.inject.Inject;

public class ConceptAnswerDbService extends BaseDbService<ConceptAnswer> implements DbService<ConceptAnswer> {
	@Inject
	public ConceptAnswerDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<ConceptAnswer> getEntityTable() {
		return (ConceptAnswer_Table)FlowManager.getInstanceAdapter(ConceptAnswer.class);
	}

	public List<ConceptAnswer> getByConceptUuid(@NonNull String conceptUuid, @Nullable QueryOptions options) {
		return executeQuery(options, null,
				(f) -> f.where(ConceptAnswer_Table.concept_uuid.eq(conceptUuid))
		);
	}
}

