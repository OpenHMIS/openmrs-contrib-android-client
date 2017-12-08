package org.openmrs.mobile.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.queryModel.EntityUuid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DatabaseHelper {
	private Repository repository;

	@Inject
	public DatabaseHelper(Repository repository) {
		this.repository = repository;
	}

	/**
	 * Generates a deterministic UUID given the specified source string.
	 * @param source
	 * @return
	 */
	public String generateUuid(@NonNull String source) {
		checkNotNull(source);

		return UUID.nameUUIDFromBytes(source.getBytes()).toString();
	}

	/**
	 * Deletes the records from the specified model's table that are not found in the expected records. Records are compared
	 * using only the uuid values.
	 * @param model The model table to delete the records from
	 * @param expectedRecords The records that should exist in the table
	 * @param <T> The model type
	 */
	public <T extends Resource> void diffDelete(@NonNull Class<T> model, @NonNull List<? extends Resource> expectedRecords) {
		diffDelete(model, (List<SQLOperator>)null, expectedRecords);
	}

	/**
	 * Deletes the records from the specified model's table, using the specified selection criteria, that are not found in
	 * the expected records. Records are compared using only the uuid values.
	 * @param model The model table to delete the records from
	 * @param operator The optional table selection criteria
	 * @param expectedRecords The records that should exist in the table
	 * @param <T> The model type
	 */
	public <T extends Resource> void diffDelete(@NonNull Class<T> model, @Nullable SQLOperator operator,
			@NonNull List<? extends Resource> expectedRecords) {
		List<SQLOperator> operators = null;
		if (operator != null) {
			operators = new ArrayList<>(1);
			operators.add(operator);
		}

		diffDelete(model, operators, expectedRecords);
	}

	/**
	 * Deletes the records from the specified model's table, using the specified selection criteria, that are not found in
	 * the expected records. Records are compared using only the uuid values.
	 * @param model The model table to delete the records from
	 * @param operators The optional table selection criteria
	 * @param expectedRecords The records that should exist in the table
	 * @param <T> The model type
	 */
	public <T extends Resource> void diffDelete(@NonNull Class<T> model, @Nullable List<SQLOperator> operators,
			@NonNull List<? extends Resource> expectedRecords) {
		checkNotNull(model);
		checkNotNull(expectedRecords);

		ModelAdapter<T> table = FlowManager.getModelAdapter(model);

		SQLOperator[] array = null;
		if (operators != null) {
			array = operators.toArray(new SQLOperator[operators.size()]);
		}

		if (expectedRecords.isEmpty()) {
			// Delete all records from source table
			repository.deleteAll(table, array);
		} else {
			// Get the uuid's of the current source records
			List<String> existingRecords = EntityUuid.getUuids(repository.queryCustom(EntityUuid.class, table,
					table.getProperty("uuid"), array));
			if (existingRecords == null || existingRecords.isEmpty()) {
				// There are no records so there is nothing to delete
				return;
			}

			// Put the record uuid's into a map for searching
			Map<String, String> recordMap = new HashMap<>(expectedRecords.size());
			for (Resource record : expectedRecords) {
				recordMap.put(record.getUuid(), "");
			}

			// Find the uuid's that are not in the record list
			List<String> deletes = new ArrayList<>();
			for (String uuid : existingRecords) {
				if (!recordMap.containsKey(uuid)) {
					deletes.add(uuid);
				}
			}

			if (deletes.size() > 0) {
				// Delete the source records that were not found
				repository.deleteAll(table, table.getProperty("uuid").in(deletes));
			}
		}
	}

	/**
	 * Takes a list of entities and returns there UUIDs as a list
	 * @param entities
	 * @return
	 */
	public List<String> getEntityUuids(@NonNull List<? extends Resource> entities) {
		checkNotNull(entities);

		List<String> uuids = new ArrayList<>();
		for (Resource entity : entities) {
			uuids.add(entity.getUuid());
		}
		return uuids;
	}
}
