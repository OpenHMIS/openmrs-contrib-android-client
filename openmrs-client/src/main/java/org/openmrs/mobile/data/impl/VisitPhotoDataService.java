package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.RequestStrategy;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.impl.VisitPhotoRestServiceImpl;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.SyncAction;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class VisitPhotoDataService
		extends BaseDataService<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl>
		implements DataService<VisitPhoto> {

	private static final String TAG = VisitPhotoDataService.class.getSimpleName();

	@Inject
	public VisitPhotoDataService() {
	}

	public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, new QueryOptions.Builder().requestStrategy(RequestStrategy.REMOTE_THEN_LOCAL).build(),
				() -> {
					VisitPhoto result = dbService.save(visitPhoto);
					syncLogService.save(result, SyncAction.NEW);
					return result;
				},
				() -> restService.upload(visitPhoto));
	}

	public void downloadPhotoMetadata(String patientUuid, QueryOptions options, ObsDataService obsDataService,
			GetCallback<List<Observation>> callback) {
		obsDataService.getVisitDocumentsObsByPatientAndConceptList(patientUuid, options, callback);
	}

	public void downloadPhotoImage(VisitPhoto photo, String view, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, null,
				() -> dbService.getByUuid(photo.getObservation().getUuid(), null),
				() -> restService.downloadPhoto(photo.getObservation().getUuid(), view),
				(ResponseBody body) -> {
					try {
						photo.setImage(body.bytes());
						return photo;
					} catch (IOException ex) {
						Log.e(TAG, "Error downloading image with obs uuid '" + photo.getObservation().getUuid() + "'", ex);
						return null;
					}
				},
				(e) -> dbService.save(e)
		);
	}

	public List<VisitPhoto> getByVisit(String uuid) {
		return dbService.getByVisit(uuid);
	}
}
