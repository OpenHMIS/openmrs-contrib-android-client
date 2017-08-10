package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.User_Table;

import java.util.List;

import javax.inject.Inject;

public class UserDbService extends BaseDbService<User> implements DbService<User> {
	@Inject
	public UserDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<User> getEntityTable() {
		return (User_Table)FlowManager.getInstanceAdapter(User.class);
	}

	public List<User> getByUsername(String username, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(User_Table.username.eq(username))
		);
	}
}
