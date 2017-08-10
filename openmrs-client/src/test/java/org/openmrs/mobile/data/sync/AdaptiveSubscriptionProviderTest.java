package org.openmrs.mobile.data.sync;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.impl.RecordInfoDbService;
import org.openmrs.mobile.data.rest.RestService;
import org.openmrs.mobile.models.BaseOpenmrsAuditableObject;
import org.openmrs.mobile.models.PullSubscription;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class AdaptiveSubscriptionProviderTest<E extends BaseOpenmrsAuditableObject,
		DS extends DbService<E>, RS extends RestService<E>,
		P extends AdaptiveSubscriptionProvider<E, DS, RS>> {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock DbService<E> dbService;
	@Mock RestService<E> restService;
	@Mock DbService<RecordInfo> recordInfoDbService;

	P provider;

	protected abstract P getProvider();

	protected abstract PullSubscription createSubscriptionRecord();

	protected abstract E createEntity();

	@Before
	public void before() {
		this.provider = getProvider();
	}

	@Test
	public void pull_shouldRequestFullTableWhenEmpty() throws Exception {
		/*
		Call<Results<E>> call = mock(Call.class);
		Response<Results<E>> response = Response.success(null);

		when(dbService.getCount(any())).thenReturn(0L);
		when(restService.getAll(any(), any())).thenReturn(call);
		when(call.execute()).thenReturn(response);

		provider.pull(createSubscriptionRecord());

		verify(restService).getAll(any(), any());

		verify(restService, never()).getRecordInfo();
		verify(dbService, never()).saveAll(any());
		*/
	}

	@Test
	public void pull_shouldRequestIncrementalChangesWhenTableIsNotEmpty() throws Exception {
		/*
		E entity = createEntity();
		Call<Results<RecordInfo>> call = mock(Call.class);
		Response<Results<RecordInfo>> response = Response.success(
				new Results<RecordInfo>(Arrays.asList(recordInfoFromEntity(entity)))
		);

		when(dbService.getCount(any())).thenReturn(1L);
		when(restService.getRecordInfo()).thenReturn(call);
		when(call.execute()).thenReturn(response);
		//when(dbService.saveAll())

		provider.pull(createSubscriptionRecord());

		verify(restService).getRecordInfo();
		verify(recordInfoDbService).saveAll(any());

		verify(restService, never()).getAll(any(), any());
		verify(dbService, never()).saveAll(any());
		*/
	}

	@Test
	public void pullTable_shouldNotProcessDeletionsWhenProcessDeletionsIsFalse() throws Exception {

	}

	@Test
	public void pullTable_shouldProcessDeletionsWhenProcessDeletionsIsTrue() throws Exception {

	}

	@Test
	public void pullTable_shouldRemoveDeletedRecords() throws Exception {

	}

	@Test
	public void pullTable_shouldAddNewRecords() throws Exception {

	}

	@Test
	public void pullIncremental_shouldSwitchToFullRequestWhenIncrementalHasTooManyChanges() throws Exception {

	}

	@Test
	public void pullIncremental_shouldRemoveDeletedRecords() throws Exception {

	}

	@Test
	public void pull_should() throws Exception {

	}
}
