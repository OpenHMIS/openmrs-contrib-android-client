package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.models.VisitAttributeType;
import org.openmrs.mobile.models.VisitAttributeType_Table;

import javax.inject.Inject;

public class VisitAttributeTypeDbServie extends BaseDbService<VisitAttributeType> implements DbService<VisitAttributeType> {
	@Inject
	public VisitAttributeTypeDbServie() { }

	@Override
	protected ModelAdapter<VisitAttributeType> getEntityTable() {
		return (VisitAttributeType_Table)FlowManager.getInstanceAdapter(VisitAttributeType.class);
	}
}

