package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.data.sync.impl.PatientListContextSubscriptionProvider;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.PullSubscription_Table;

import javax.inject.Inject;
import java.util.List;

public class PullSubscriptionDbService extends BaseDbService<PullSubscription> {
	@Inject
	public PullSubscriptionDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<PullSubscription> getEntityTable() {
		return (PullSubscription_Table)FlowManager.getInstanceAdapter(PullSubscription.class);
	}

	public List<PullSubscription> getBySubscriptionClass(String subscriptionClass) {
		return repository.query(getEntityTable(), PullSubscription_Table.subscriptionClass.eq(subscriptionClass));
	}
}
