package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class ConceptSet extends BaseOpenmrsObject {
	@ForeignKey(stubbedRelationship = true)
	private Concept concept;

	private List<ConceptSetMember> memberConcepts;

	@OneToMany(methods = { OneToMany.Method.ALL}, variableName = "memberConcepts", isVariablePrivate = true)
	List<ConceptSetMember> loadMemberConcepts() {
		memberConcepts = loadRelatedObject(ConceptSetMember.class, memberConcepts,
				() -> ConceptAnswer_Table.concept_uuid.eq(getUuid()));

		return memberConcepts;
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public List<ConceptSetMember> getMemberConcepts() {
		return memberConcepts;
	}

	public void setMemberConcepts(List<ConceptSetMember> memberConcepts) {
		this.memberConcepts = memberConcepts;
	}
}
