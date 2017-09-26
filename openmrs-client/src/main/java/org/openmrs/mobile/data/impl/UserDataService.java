package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.UserDbService;
import org.openmrs.mobile.data.rest.impl.UserRestServiceImpl;
import org.openmrs.mobile.models.User;

import java.util.List;

import javax.inject.Inject;

public class UserDataService extends BaseDataService<User, UserDbService, UserRestServiceImpl> {
	@Inject
	public UserDataService() { }

	public void getByUsername(String username, QueryOptions options, PagingInfo pagingInfo,
			GetCallback<List<User>> callback) {
		executeMultipleCallback(callback, options, pagingInfo,
				() -> dbService.getByUsername(username, options, pagingInfo),
				() -> restService.getByUsername(username, options, pagingInfo));
	}

	public void save(@NonNull User user) {
		dbService.save(user);
	}
}

