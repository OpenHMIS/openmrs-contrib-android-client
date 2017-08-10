package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientIdentifier_Table;
import org.openmrs.mobile.models.Patient_Table;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.PersonName_Table;

import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class PatientDbService extends BaseDbService<Patient> implements DbService<Patient> {
	@Inject
	public PatientDbService(Repository repository) {
		super(repository);
	}

	@Override
	protected ModelAdapter<Patient> getEntityTable() {
		return (Patient_Table)FlowManager.getInstanceAdapter(Patient.class);
	}

	public List<Patient> getByName(String name, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(findByNameFragment(name))
		);
	}

	public List<Patient> getByIdentifier(String id, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(findById(id))
		);
	}

	public List<Patient> getByNameOrIdentifier(String name, String id, QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.where(findByNameFragment(name)).or(findById(id))
		);
	}

	public List<Patient> getLastViewed(QueryOptions options, PagingInfo pagingInfo) {
		return executeQuery(options, pagingInfo,
				(f) -> f.orderBy(Patient_Table.dateChanged, false)
		);
	}

	private SQLOperator findByNameFragment(@NonNull String name) {
		checkNotNull(name);

		if (!name.startsWith("%")) {
			name = "%" + name;
		}
		if (!name.endsWith("%")) {
			name = name + "%";
		}

		return Patient_Table.person_uuid.in(
				SQLite.select(PersonName_Table.person_uuid)
						.from(PersonName.class)
						.where(Method.group_concat(
								PersonName_Table.givenName,
								PersonName_Table.middleName,
								PersonName_Table.familyName)
							.like(name)
						)
		);
	}

	private SQLOperator findById(String id) {
		return Patient_Table.uuid.in(
				SQLite.select(PatientIdentifier_Table.patient_uuid)
						.from(PatientIdentifier.class)
						.where(PatientIdentifier_Table.identifier.eq(id))
		);
	}
}

