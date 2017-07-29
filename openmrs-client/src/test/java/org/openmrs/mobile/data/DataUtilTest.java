package org.openmrs.mobile.data;

import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import junit.framework.Assert;

import org.apache.tools.ant.util.CollectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.openmrs.mobile.BuildConfig;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Location_Table;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DataUtilTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	Repository repo;

	@InjectMocks
	DataUtil dataUtil;

	private Location[] testData = {
			createLocation(),
			createLocation(),
			createLocation(),
			createLocation(),
			createLocation()
	};

	@Test(expected = NullPointerException.class)
	public void diffDelete_shouldThrowExceptionWithNullTable() throws Exception {
		dataUtil.diffDelete(null, new ArrayList<Location>());
	}

	@Test(expected = NullPointerException.class)
	public void diffDelete_shouldThrowExceptionWithNullExpectedRecords() throws Exception {
		dataUtil.diffDelete(Location.class, null);
	}

	@Test
	public void diffDelete_shouldIgnoreExpectedRecordsNotFoundInTable() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		List<String> results = createResults(testData[0]);

		when(repo.queryCustom(eq(String.class), any(), any(IProperty.class))).thenReturn(results);

		dataUtil.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(String.class), any(), any(IProperty.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	@Test
	public void diffDelete_shouldSkipChecksWhenTableIsEmpty() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));

		when(repo.queryCustom(eq(String.class), any(), any(IProperty.class))).thenReturn(new ArrayList<>());

		dataUtil.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(String.class), any(), any(IProperty.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	@Test
	public void diffDelete_shouldDeleteASingleSourceRecordWhenMissingFromExpectedRecords() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		Location toBeDeleted = createLocation();
		List<String> results = createResults(toBeDeleted);
		results.add(models.get(0).getUuid());

		ArgumentCaptor<SQLOperator> captor = ArgumentCaptor.forClass(SQLOperator.class);

		when(repo.queryCustom(eq(String.class), any(), any(IProperty.class))).thenReturn(results);

		dataUtil.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(String.class), any(), any(IProperty.class));
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
		List<String> results = createResults(toBeDeleted);
		results.add(models.get(0).getUuid());

		ArgumentCaptor<SQLOperator> captor = ArgumentCaptor.forClass(SQLOperator.class);

		when(repo.queryCustom(eq(String.class), any(), any(IProperty.class))).thenReturn(results);

		dataUtil.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(String.class), any(), any(IProperty.class));
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
		dataUtil.diffDelete(Location.class, new ArrayList<>());

		verify(repo, never()).queryCustom(eq(String.class), any(), any(IProperty.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
		verify(repo).deleteAll(any(Location_Table.class));
	}

	@Test
	public void diffDelete_shouldNotDeleteWhenAllTableRecordsAreFoundInExpectedRecords() throws Exception {
		List<Location> models = new ArrayList<>(Arrays.asList(testData));
		List<String> results = createResults(models.toArray(new Location[models.size()]));
		when(repo.queryCustom(eq(String.class), any(), any(IProperty.class))).thenReturn(results);

		dataUtil.diffDelete(Location.class, models);

		verify(repo).queryCustom(eq(String.class), any(), any(IProperty.class));
		verify(repo, never()).deleteAll(any(), any(SQLOperator.class));
	}

	private Location createLocation() {
		Location location = new Location();
		location.setUuid(UUID.randomUUID().toString());

		return location;
	}

	private List<String> createResults(Location... locations) {
		List<String> results = new ArrayList<>(locations.length);

		for (Location location : locations) {
			results.add(location.getUuid());
		}

		return results;
	}
}