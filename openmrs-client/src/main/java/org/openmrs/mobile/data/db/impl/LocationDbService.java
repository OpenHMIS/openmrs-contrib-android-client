package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;

import javax.inject.Inject;

public class LocationDbService extends BaseMetadataDbService<Location> implements MetadataDbService<Location> {
	@Inject
	public LocationDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Location> getEntityTable() {
		return (Location_Table)FlowManager.getInstanceAdapter(Location.class);
	}
}

