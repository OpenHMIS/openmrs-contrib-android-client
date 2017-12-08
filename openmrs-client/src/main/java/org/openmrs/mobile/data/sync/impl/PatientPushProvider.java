package org.openmrs.mobile.data.sync.impl;

import org.openmrs.mobile.data.db.impl.PatientDbService;
import org.openmrs.mobile.data.db.impl.PersonDbService;
import org.openmrs.mobile.data.db.impl.VisitDbService;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.db.impl.VisitTaskDbService;
import org.openmrs.mobile.data.rest.impl.PatientRestServiceImpl;
import org.openmrs.mobile.data.sync.BasePushProvider;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonAttribute;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.models.VisitTask;

import java.util.List;

import javax.inject.Inject;

public class PatientPushProvider extends BasePushProvider<Patient, PatientDbService, PatientRestServiceImpl> {
	private PersonDbService personDbService;
	private VisitDbService visitDbService;
	private VisitTaskDbService visitTaskDbService;
	private VisitPhotoDbService visitPhotoDbService;

	@Inject
	public PatientPushProvider(PatientDbService patientDbService, PatientRestServiceImpl patientRestService,
			PersonDbService personDbService, VisitDbService visitDbService, VisitTaskDbService visitTaskDbService,
			VisitPhotoDbService visitPhotoDbService) {
		super(patientDbService, patientRestService);

		this.personDbService = personDbService;
		this.visitDbService = visitDbService;
		this.visitTaskDbService = visitTaskDbService;
		this.visitPhotoDbService = visitPhotoDbService;
	}

	@Override
	protected void deleteLocalRelatedRecords(Patient originalEntity, Patient restEntity) {
		List<Visit> visits = visitDbService.getByPatient(originalEntity, null, null);
		if (!visits.isEmpty()) {
			for (Visit visit : visits) {
				visit.setPatient(restEntity);
			}
			visitDbService.saveAll(visits);
		}

		List<VisitTask> visitTasks = visitTaskDbService.getByPatient(originalEntity, null, null);
		if (!visitTasks.isEmpty()) {
			for (VisitTask visitTask : visitTasks) {
				visitTask.setPatient(restEntity);
			}
			visitTaskDbService.saveAll(visitTasks);
		}

		List<VisitPhoto> visitPhotos = visitPhotoDbService.getByPatient(originalEntity.getUuid());
		if (!visitPhotos.isEmpty()) {
			for (VisitPhoto visitPhoto : visitPhotos) {
				visitPhoto.setPatient(restEntity);
			}
			visitPhotoDbService.saveAll(visitPhotos);
		}

		if (!originalEntity.getPerson().getUuid().equalsIgnoreCase(restEntity.getPerson().getUuid())) {
			personDbService.update(originalEntity.getPerson().getUuid(), restEntity.getPerson());
		}

		dbService.deleteLocalRelatedObjects(originalEntity);
	}

	@Override
	protected void preProcess(Patient entity) {
		if (entity.getPerson() != null && entity.getPerson().getAttributes() != null) {
			List<PersonAttribute> attributes = entity.getPerson().getAttributes();
			for (PersonAttribute attribute : attributes) {
				if (attribute.getValue() == null) {
					if (attribute.getStringValue() != null) {
						attribute.setValue(attribute.getStringValue());
					} else if (attribute.getConceptValue() != null) {
						attribute.setValue(attribute.getConceptValue());
					}
				}
			}
		}
	}
}
