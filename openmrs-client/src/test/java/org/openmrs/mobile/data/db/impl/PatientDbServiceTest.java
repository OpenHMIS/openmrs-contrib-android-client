package org.openmrs.mobile.data.db.impl;

import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.models.Patient;

import static org.junit.Assert.*;

public class PatientDbServiceTest extends BaseAuditableDbServiceTest<Patient> {
	@Override
	protected BaseDbService<Patient> getDbService() {
		return null;
	}

	@Override
	protected ModelAsserters.ObjectAsserter<Patient> getAsserter() {
		return ModelAsserters.PATIENT;
	}

	@Test
	public void getByName_shouldSearchByNameWithFullWildcard() throws Exception {

	}

	@Test
	public void getByName_shouldReturnMultipleEntities() throws Exception {

	}

	@Test
	public void getByName_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByName_shouldReturnEmptyListWhenNoEntities() throws Exception {

	}

	@Test
	public void getByName_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByName_shouldThrowExceptionWhenNameIsNull() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldSearchByIdWithExactMatch() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldReturnSingleEntity() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldReturnNullWhenNoEntityFound() throws Exception {

	}

	@Test
	public void getByIdentifier_shouldThrowExceptionWhenIdentifierIsNull() throws Exception {

	}

	@Test
	public void getLastViewed_shouldRequirePaging() throws Exception {

	}

	@Test
	public void getLastViewed_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getLastViewed_shouldReturnResultsInCorrectOrder() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldSearchByExactIdOrNameWithWildcard() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnMultipleResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnSingleResult() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnEmptyListWhenNoResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldReturnPagedResults() throws Exception {

	}

	@Test
	public void getByNameOrIdentifier_shouldThrowExceptionWhenNameOrIdIsNull() throws Exception {

	}
}