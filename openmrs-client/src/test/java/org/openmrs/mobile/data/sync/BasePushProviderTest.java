package org.openmrs.mobile.data.sync;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.SyncLogDbService;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.data.sync.impl.EncounterPushProvider;
import org.openmrs.mobile.data.sync.impl.ObservationPushProvider;
import org.openmrs.mobile.data.sync.impl.PatientPushProvider;
import org.openmrs.mobile.data.sync.impl.VisitPushProvider;
import org.openmrs.mobile.data.sync.impl.VisitTaskPushProvider;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.SyncLog;
import org.openmrs.mobile.test.MockErrorResponse;
import org.openmrs.mobile.test.MockSuccessResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public abstract class BasePushProviderTest<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>> {
	@Mock protected SyncLogDbService syncLogDbService;
	@Mock protected DbService<E> dbService;
	@Mock protected RestService<E> restService;

	private static final int EROR_CODE = 505;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	protected List<SyncLog> syncLogEntries = new ArrayList<>();

	protected abstract E getEntity();

	protected void createSyncLogEntries() {
		SyncLog entry = new SyncLog();
		entry.setKey("11-22-33");
		entry.setAction(SyncAction.NEW);
		entry.setType(PatientPushProvider.class.getSimpleName());
		addSyncLogEntry(entry);

		entry = new SyncLog();
		entry.setKey("1a-2b-3c");
		entry.setAction(SyncAction.UPDATED);
		entry.setType(EncounterPushProvider.class.getSimpleName());
		addSyncLogEntry(entry);

		entry = new SyncLog();
		entry.setKey("aa-bb-cc");
		entry.setAction(SyncAction.NEW);
		entry.setType(ObservationPushProvider.class.getSimpleName());
		addSyncLogEntry(entry);

		entry = new SyncLog();
		entry.setKey("ab-bc-cd");
		entry.setAction(SyncAction.UPDATED);
		entry.setType(VisitPushProvider.class.getSimpleName());
		addSyncLogEntry(entry);

		entry = new SyncLog();
		entry.setKey("aba-bcb-cdc");
		entry.setAction(SyncAction.NEW);
		entry.setType(VisitTaskPushProvider.class.getSimpleName());
		addSyncLogEntry(entry);
	}

	protected void addSyncLogEntry(SyncLog syncLog){
		syncLogEntries.add(syncLog);
	}

	protected void mockSuccessfulCalls() {
		mockSuccessfulDbCalls();
		mockSuccessfulRestCalls();
	}

	protected void mockErrorCalls () {
		mockErrorDbCalls();
		mockErrorRestCalls();
	}

	protected void mockSuccessfulDbCalls() {
		Mockito.when(dbService.getByUuid(anyString(), any(QueryOptions.class))).thenReturn(getEntity());
	}

	protected void mockErrorDbCalls() {
		Mockito.when(dbService.getByUuid(anyString(), any(QueryOptions.class))).thenReturn(null);
	}

	protected void mockSuccessfulRestCalls(){
		Mockito.when(restService.create(getEntity())).thenReturn(mockSuccessCall(getEntity()));
		Mockito.when(restService.update(getEntity())).thenReturn(mockSuccessCall(getEntity()));
		Mockito.when(restService.purge(getEntity().getUuid())).thenReturn(mockSuccessCall(getEntity()));
	}

	protected void mockErrorRestCalls(){
		Mockito.when(restService.create(getEntity())).thenReturn(mockErrorCall(EROR_CODE));
		Mockito.when(restService.update(getEntity())).thenReturn(mockErrorCall(EROR_CODE));
		Mockito.when(restService.purge(getEntity().getUuid())).thenReturn(mockErrorCall(EROR_CODE));
	}

	protected void mockDeleteSyncCall() {
		doNothing().when(syncLogDbService).delete(any(SyncLog.class));
	}

	protected  <T> Call<T> mockSuccessCall(T object) {
		return new MockSuccessResponse<>(object);
	}

	protected <T> Call<T> mockErrorCall(int code){
		return new MockErrorResponse<>(code);
	}
}
