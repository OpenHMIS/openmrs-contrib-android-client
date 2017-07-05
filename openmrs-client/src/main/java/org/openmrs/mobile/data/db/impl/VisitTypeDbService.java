package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.models.VisitType_Table;

public class VisitTypeDbService extends BaseMetadataDbService<VisitType> implements MetadataDbService<VisitType> {

	@Override
	protected ModelAdapter<VisitType> getEntityTable() {
		return (VisitType_Table)FlowManager.getInstanceAdapter(VisitType.class);
	}
}
