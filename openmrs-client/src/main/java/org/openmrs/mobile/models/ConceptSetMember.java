package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class ConceptSetMember extends BaseOpenmrsObject {
	@ForeignKey(stubbedRelationship = true)
	private ConceptSet conceptSet;

	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	public ConceptSet getConceptSet() {
		return conceptSet;
	}

	public void setConceptSet(ConceptSet conceptSet) {
		this.conceptSet = conceptSet;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}
}
