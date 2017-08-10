package org.openmrs.mobile.data.db.impl;

import org.junit.Test;
import org.openmrs.mobile.data.ModelAsserters;
import org.openmrs.mobile.data.db.BaseDbService;
import org.openmrs.mobile.data.db.BaseEntityDbServiceTest;
import org.openmrs.mobile.models.Visit;

public class VisitDbServiceTest extends BaseEntityDbServiceTest<Visit> {
	@Override
	protected BaseDbService<Visit> getDbService() {
		return null;
	}

	@Override
	protected ModelAsserters.EntityAsserter<Visit> getAsserter() {
		return ModelAsserters.VISIT;
	}

	@Test
	public void endVisit_shouldSetStopDateAndSave() throws Exception {

	}

	@Test
	public void endVisit_shouldNotResetStopDateIfAlreadySet() throws Exception {

	}

	@Test
	public void endVisit_shouldReturnUpdatedVisit() throws Exception {

	}

	@Test
	public void endVisit_shouldThrowExceptionIfVisitIsNull() throws Exception {

	}
}
