package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.PersonAttributeType;
import org.openmrs.mobile.models.PersonAttributeType_Table;

import javax.inject.Inject;

public class PersonAttributeTypeDbService extends BaseDbService<PersonAttributeType>
		implements DbService<PersonAttributeType> {
	@Inject
	public PersonAttributeTypeDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<PersonAttributeType> getEntityTable() {
		return (PersonAttributeType_Table)FlowManager.getInstanceAdapter(PersonAttributeType.class);
	}
}

