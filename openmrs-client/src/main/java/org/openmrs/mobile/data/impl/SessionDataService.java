package org.openmrs.mobile.data.impl;

import org.openmrs.mobile.data.BaseMetadataDataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.db.impl.SessionDbService;
import org.openmrs.mobile.data.rest.impl.SessionRestServiceImpl;
import org.openmrs.mobile.models.Session;

import javax.inject.Inject;

public class SessionDataService extends BaseMetadataDataService<Session, SessionDbService, SessionRestServiceImpl> {
	@Inject
	public SessionDataService() { }

	public void getSession(String username, String password, GetCallback<Session> callback) {
		executeSingleCallback(callback,
				new QueryOptions.Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build(),
				() -> null,
				() -> restService.getSession(username, password));
	}
}
