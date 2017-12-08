package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Person_Table;

import javax.inject.Inject;

public class PersonDbService extends BaseDbService<Person> {
	@Inject
	public PersonDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Person> getEntityTable() {
		return (Person_Table)FlowManager.getInstanceAdapter(Person.class);
	}
}
