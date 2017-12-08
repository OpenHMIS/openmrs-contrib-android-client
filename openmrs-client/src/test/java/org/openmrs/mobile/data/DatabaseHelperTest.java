package org.openmrs.mobile.data;

import android.content.Context;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;
import org.openmrs.mobile.models.queryModel.EntityUuid;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DatabaseHelperTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	Repository repo;

	@Mock
	FlowManager flowManager;

	@Mock
	Context context;

	@InjectMocks
	DatabaseHelper databaseHelper;

	private Location[] testData = {
			createLocation(),
			createLocation(),
			createLocation(),
			createLocation(),
			createLocation()
	};

	@Before
	public void setup() {
		FlowManager.init(context);
	}

	@Test(expected = NullPointerException.class)
	public void diffDelete_shouldThrowExceptionWithNullTable() throws Exception {
		databaseHelper.diffDelete(null, new ArrayList<Location>());
	}

	@Test(expected = NullPointerException.class)
	public void diffDelete_shouldThrowExceptionWithNullExpectedRecords() throws Exception {
		databaseHelper.diffDelete(Location.class, null);
	}

	@Test
	public void diffDelete_shouldIgnoreExpectedRecordsNotFoundInTable() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		List<EntityUuid> results = createResults(testData[0]);

		when(repo.queryCustom(eq(EntityUuid.class), any(), any(IProperty.class),
				isNull(SQLOperator.class))).thenReturn(results);

		databaseHelper.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	@Test
	public void diffDelete_shouldSkipChecksWhenTableIsEmpty() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));

		when(repo.queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class)))
				.thenReturn(new ArrayList<>());

		databaseHelper.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	@Test
	public void diffDelete_shouldDeleteASingleSourceRecordWhenMissingFromExpectedRecords() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		Location toBeDeleted = createLocation();
		List<EntityUuid> results = createResults(toBeDeleted);
		EntityUuid resultToAdd = new EntityUuid();
		resultToAdd.setUuid(models.get(0).getUuid());
		results.add(resultToAdd);

		ArgumentCaptor<SQLOperator> captor = ArgumentCaptor.forClass(SQLOperator.class);

		when(repo.queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class)))
				.thenReturn(results);

		databaseHelper.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class));
		verify(repo, times(1)).deleteAll(any(), captor.capture());

		SQLOperator op = captor.getValue();
		Operator.In<String> inOp = (Operator.In<String>)op;
		Assert.assertNotNull(inOp);
		Assert.assertTrue(inOp.getQuery().contains(toBeDeleted.getUuid()));
		Assert.assertFalse(inOp.getQuery().contains(models.get(0).getUuid()));
	}

	@Test
	public void diffDelete_shouldDeleteMultipleRecordsWhenMissingFromExpectedRecords() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		Location[] toBeDeleted = {
				createLocation(),
				createLocation()
		};
		List<EntityUuid> results = createResults(toBeDeleted);
		EntityUuid resultToAdd = new EntityUuid();
		resultToAdd.setUuid(models.get(0).getUuid());
		results.add(resultToAdd);

		ArgumentCaptor<SQLOperator> captor = ArgumentCaptor.forClass(SQLOperator.class);

		when(repo.queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class)))
				.thenReturn(results);

		databaseHelper.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class));
		verify(repo, times(1)).deleteAll(any(), captor.capture());

		SQLOperator op = captor.getValue();
		Operator.In<String> inOp = (Operator.In<String>)op;
		Assert.assertNotNull(inOp);
		Assert.assertTrue(inOp.getQuery().contains(toBeDeleted[0].getUuid()));
		Assert.assertTrue(inOp.getQuery().contains(toBeDeleted[1].getUuid()));
		Assert.assertFalse(inOp.getQuery().contains(models.get(0).getUuid()));
	}

	@Test
	public void diffDelete_shouldDeleteAllTableRecordsWhenExpectedRecordsIsEmpty() throws Exception {
		databaseHelper.diffDelete(Location.class, new ArrayList<>());

		verify(repo, never()).queryCustom(eq(String.class), any(), any(IProperty.class));
		verify(repo).deleteAll(any(Location_Table.class), Matchers.isNull(SQLOperator.class));
	}

	@Test
	public void diffDelete_shouldNotDeleteWhenAllTableRecordsAreFoundInExpectedRecords() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		List<EntityUuid> results = createResults(models.toArray(new Location[models.size()]));
		when(repo.queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class)))
				.thenReturn(results);

		databaseHelper.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(EntityUuid.class), any(), any(IProperty.class), isNull(SQLOperator.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	private Location createLocation() {
		Location location = new Location();
		location.setUuid(UUID.randomUUID().toString());

		return location;
	}

	private List<EntityUuid> createResults(Location... locations) {
		List<EntityUuid> results = new ArrayList<>(locations.length);

		for (Location location : locations) {
			EntityUuid uuid = new EntityUuid();
			uuid.setUuid(location.getUuid());
			results.add(uuid);
		}

		return results;
	}
}