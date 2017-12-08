package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.models.BaseOpenmrsObject;

import java.util.Collection;
import java.util.List;

/**
 * Repository to interact with a SQLite database via dbflow.
 */
public interface Repository {
	/**
	 * Queries for a single record from the specified table.
	 * @param table The table to query from
	 * @param operators The optional where clauses
	 * @param <M> The model class
	 * @return A single resulting model
	 */
	<M> M querySingle(@NonNull ModelAdapter<M> table, @NonNull SQLOperator... operators);

	/**
	 * Queries from a single value from the specified table.
	 * @param cls The expected class of the resulting value
	 * @param table The table to query from
	 * @param property The db field to select
	 * @param operators The optional where clauses
	 * @param <T> The result class
	 * @param <M> The model class
	 * @return A single resulting value
	 */
	<T, M> T querySingle(@NonNull Class<T> cls, @NonNull ModelAdapter<M> table,
			@NonNull IProperty property, @Nullable SQLOperator... operators);

	/**
	 * Queries multiple models from the specified table.
	 * @param table The table to query from
	 * @param operators The optional where clauses
	 * @param <M> The model class
	 * @return The resulting models
	 */
	<M> List<M> query(@NonNull ModelAdapter<M> table, @Nullable SQLOperator... operators);

	/**
	 * Queries multiple models from the specified query
	 * @param query The query to execute
	 * @param <M> The model class
	 * @return The resulting models
	 */
	<M> List<M> query(@NonNull ModelQueriable<M> query);

	/**
	 * Queries multiple values from the specified table.
	 * @param cls The expected class of the resulting values
	 * @param table The table to query from
	 * @param property The db field to select
	 * @param operators The optional where clauses
	 * @param <T> The value class
	 * @param <M> The model class
	 * @return The resulting values
	 */
	<T, M> List<T> queryCustom(@NonNull Class<T> cls, @NonNull ModelAdapter<M> table,
			@NonNull IProperty property, @Nullable SQLOperator... operators);

	/**
	 * Queries multiple values from the specified table.
	 * @param cls The expected class of the resulting values
	 * @param table The table to query from
	 * @param properties The db fields to select
	 * @param operators The optional where clauses
	 * @param <T> The value class
	 * @param <M> The model class
	 * @return The resulting values
	 */
	<T, M> List<T> queryCustom(@NonNull Class<T> cls, @NonNull ModelAdapter<M> table,
			@NonNull Collection<IProperty> properties, @Nullable SQLOperator... operators);

	/**
	 * Queries multiple values from the specified query.
	 * @param cls The expected class of the resulting values
	 * @param query The query to execute
	 * @param <T> The value class
	 * @param <M> The model class
	 * @return The resulting values
	 */
	<T, M> List<T> queryCustom(@NonNull Class<T> cls, @NonNull ModelQueriable<M> query);

	/**
	 * Gets a count of number records in the specified table that meet the specified where conditions.
	 * @param table The table to query from
	 * @param operators The optional where clauses
	 * @param <M> The model class
	 * @return The record count
	 */
	<M> long count(@NonNull ModelAdapter<M> table, @Nullable SQLOperator... operators);

	/**
	 * Gets a count of number of records in the specified query. 
	 * @param from The query to execute
	 * @param <M> The model class
	 * @return The record count
	 */
	<M> long count(@NonNull ModelQueriable<M> from);

	/**
	 * Updates the model with the specified id.
	 * @param table The table where the model will be updated
	 * @param id The id for the model to update
	 * @param model The updated model
	 * @param <M> The model class
	 * @return {@code true} if the model was found and updated; otherwise, {@code false}
	 */
	<M extends BaseOpenmrsObject> boolean update(@NonNull ModelAdapter<M> table, @NonNull String id, @NonNull M model);

	/**
	 * Saves the specified model.
	 * @param table The table where the model will be saved
	 * @param model The model to save
	 * @param <M> The model class
	 * @return {@code true} if the model was saved; otherwise, {@code false}
	 */
	<M> boolean save(@NonNull ModelAdapter<M> table, @NonNull M model);

	/**
	 * Saves all the specified models.
	 * @param table The table where the models will be saved
	 * @param models The models to save
	 * @param <M> The model class
	 */
	<M> void saveAll(@NonNull ModelAdapter<M> table, @NonNull List<M> models);

	/**
	 * Deletes the specified model.
	 * @param table The table where the model will be deleted
	 * @param model The model to delete
	 * @param <M> The model class
	 * @return {@code true} if the model was deleted; otherwise, {@code false}
	 */
	<M> boolean delete(@NonNull ModelAdapter<M> table, @NonNull M model);

	/**
	 * Deletes all the specified models.
	 * @param table The table where the models will be deleted
	 * @param models The models to delete
	 * @param <M> The model class
	 */
	<M> void deleteAll(@NonNull ModelAdapter<M> table, @NonNull List<M> models);

	/**
	 * Deletes all the models in the specified table that meet the specified where conditions.
	 * @param table The table where the models will be deleted
	 * @param operators The optional where clauses
	 * @param <M> The model class
	 */
	<M> void deleteAll(@NonNull ModelAdapter<M> table, @Nullable SQLOperator... operators);

	/**
	 * Deletes all the data in the specified query
	 * @param query The query to execute
	 * @param <M> The model class
	 */
	<M> void deleteAll(@NonNull ModelQueriable<M> query);
}
