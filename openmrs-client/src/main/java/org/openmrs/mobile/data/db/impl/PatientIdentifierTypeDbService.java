package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.PatientIdentifierType;
import org.openmrs.mobile.models.PatientIdentifierType_Table;

import javax.inject.Inject;

public class PatientIdentifierTypeDbService extends BaseMetadataDbService<PatientIdentifierType>
		implements MetadataDbService<PatientIdentifierType> {
	@Inject
	public PatientIdentifierTypeDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<PatientIdentifierType> getEntityTable() {
		return (PatientIdentifierType_Table)FlowManager.getInstanceAdapter(PatientIdentifierType.class);
	}
}

