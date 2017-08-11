package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.EncounterType_Table;

import javax.inject.Inject;

public class EncounterTypeDbService extends BaseMetadataDbService<EncounterType> {
	@Inject
	public EncounterTypeDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<EncounterType> getEntityTable() {
		return (EncounterType_Table)FlowManager.getInstanceAdapter(EncounterType.class);
	}
}
