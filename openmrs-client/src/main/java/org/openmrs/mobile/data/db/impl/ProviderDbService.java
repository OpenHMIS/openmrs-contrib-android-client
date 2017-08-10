package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseMetadataDbService;
import org.openmrs.mobile.data.db.MetadataDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Provider_Table;

import javax.inject.Inject;

public class ProviderDbService extends BaseMetadataDbService<Provider> implements MetadataDbService<Provider> {
	@Inject
	public ProviderDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Provider> getEntityTable() {
		return (Provider_Table)FlowManager.getInstanceAdapter(Provider.class);
	}
}

