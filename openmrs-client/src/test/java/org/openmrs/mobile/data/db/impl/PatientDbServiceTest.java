package org.openmrs.mobile.data.db.impl;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.ModelGenerators;
import org.openmrs.mobile.data.db.BaseAuditableDbServiceTest;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.Repository;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Patient_Table;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.PersonName_Table;

import java.util.List;

import static org.mockito.Mockito.mock;

public class PatientDbServiceTest extends BaseAuditableDbServiceTest<Patient> {
		@InjectMocks private Repository repository;
	private Patient_Table patientTable;

	@Before
	public void beforeTests(){
		patientTable = (Patient_Table) FlowManager.getInstanceAdapter(Patient.class);
	}

	@Override
	protected BaseDbService<Patient> getDbService() {
		return new PatientDbService(repository);
	}

	@Override
	protected ModelAsserters.ObjectAsserter<Patient> getAsserter() {
		return ModelAsserters.PATIENT;
	}

	@Override
	protected ModelGenerators.ModelGenerator<Patient> getGenerator() {
		return ModelGenerators.PATIENT;
	}

	@Test
	public void getByName_shouldSearchByNameWithFullWildcard() throws Exception {
		repository = new RepositoryImpl();
		PatientDbService patientDbService = new PatientDbService(repository);
		List<Patient> patientSearchResults;

		Patient p1 = ModelGenerators.PATIENT.generate(true);
		Patient p2 = ModelGenerators.PATIENT.generate(true);
		Patient p3 = ModelGenerators.PATIENT.generate(true);
		Patient p4 = ModelGenerators.PATIENT.generate(true);

		p1.getPerson().getName().setMiddleName("Mishi");
		p2.getPerson().getName().setGivenName("Mika");
		p3.getPerson().getName().setFamilyName("Mistari");
		p4.getPerson().getName().setGivenName("Rabashmisna");

		repository.save(patientTable,p1);
		repository.save(patientTable,p2);
		repository.save(patientTable,p3);
		repository.save(patientTable,p4);

		//get all names with fragment 'mis' in them
		patientSearchResults = patientDbService.getByName("Mis", null, null);

		Assert.assertNotNull(patientSearchResults);
		Assert.assertFalse(patientSearchResults.isEmpty());
		Assert.assertEquals(3, patientSearchResults.size());
		Assert.assertFalse(patientSearchResults.contains(p2)); //results must NOT have "Mika"
		Assert.assertEquals("Mistari", patientSearchResults.get(1).getPerson().getName().getFamilyName());
		Assert.assertEquals("Rabashmisna", patientSearchResults.get(2).getPerson().getName().getGivenName());
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