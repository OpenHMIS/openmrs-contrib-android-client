package org.openmrs.mobile.data.db.impl;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.data.DatabaseHelper;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.DbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.ConceptSet;
import org.openmrs.mobile.models.ConceptSetMember;
import org.openmrs.mobile.models.ConceptSetMember_Table;
import org.openmrs.mobile.models.ConceptSet_Table;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConceptSetDbService extends BaseDbService<ConceptSet> implements DbService<ConceptSet> {
	private DatabaseHelper databaseHelper;

	@Inject
	public ConceptSetDbService(Repository repository, DatabaseHelper databaseHelper) {
		super(repository);
		this.databaseHelper = databaseHelper;
	}

	@Override
	protected ModelAdapter<ConceptSet> getEntityTable() {
		return (ConceptSet_Table)FlowManager.getInstanceAdapter(ConceptSet.class);
	}

	protected ConceptSetMember_Table getConceptJoinTable() {
		return (ConceptSetMember_Table)FlowManager.getInstanceAdapter(ConceptSetMember.class);
	}

	public long getSetMemberCount(String setUuid) {
		checkNotNull(setUuid);

		ConceptSetMember_Table table = getConceptJoinTable();

		return repository.count(table, ConceptSetMember_Table.conceptSet_uuid.eq(setUuid));
	}

	public void save(@NonNull ConceptSet set, @NonNull List<Concept> setMembers) {
		checkNotNull(set);
		checkNotNull(setMembers);

		// First save the set
		super.save(set);

		// Next, create all the join table records
		List<ConceptSetMember> records = new ArrayList<>(setMembers.size());
		for (Concept concept : setMembers) {
			ConceptSetMember record = new ConceptSetMember();
			record.setConceptSet(set);
			record.setConcept(concept);

			record.setUuid(databaseHelper.generateUuid(set.getUuid() + concept.getUuid()));

			records.add(record);
		}

		// Delete local members for the concept set that are no longer members
		databaseHelper.diffDelete(ConceptSetMember.class,
				ConceptSetMember_Table.conceptSet_uuid.eq(set.getUuid()),
				records);

		// Save records
		repository.saveAll(getConceptJoinTable(), records);
	}
}
