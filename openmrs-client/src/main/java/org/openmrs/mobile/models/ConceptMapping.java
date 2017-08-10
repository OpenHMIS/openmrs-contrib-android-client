package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class ConceptMapping extends BaseOpenmrsObject{
	@Expose
	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	@Expose
	@ForeignKey(stubbedRelationship = true)
	private ConceptReferenceTerm conceptReferenceTerm;

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public ConceptReferenceTerm getConceptReferenceTerm() {
		return conceptReferenceTerm;
	}

	public void setConceptReferenceTerm(ConceptReferenceTerm conceptReferenceTerm) {
		this.conceptReferenceTerm = conceptReferenceTerm;
	}
}
