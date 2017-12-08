package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class VisitPhotoDataService
		extends BaseDataService<VisitPhoto, VisitPhotoDbService, VisitPhotoRestServiceImpl>
		implements DataService<VisitPhoto> {

	private static final String TAG = VisitPhotoDataService.class.getSimpleName();

	@Inject
	public VisitPhotoDataService() {
	}

	public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback,
				QueryOptions.REMOTE,
				() -> {
					VisitPhoto result = dbService.save(visitPhoto);
					syncLogService.save(result, SyncAction.NEW);
					return result;
				},
				() -> restService.upload(visitPhoto),
				(e) -> {
					visitPhoto.setObservation(e.getObservation());
					dbService.save(visitPhoto);
				});
	}

	public void downloadPhotoMetadata(String visitUuid,
			QueryOptions options, ObsDataService obsDataService, GetCallback<List<Observation>> callback) {
		obsDataService.getVisitPhotoObservations(visitUuid, options, callback);
	}

	public void downloadPhotoImage(VisitPhoto photo, String view, QueryOptions options,
			@NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, options,
				() -> dbService.getPhotoByObservation(photo.getObservation().getUuid()),
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

	public List<VisitPhoto> getByVisit(@NonNull Visit visit) {
		checkNotNull(visit);

		return dbService.getByVisit(visit);
	}

	public void purgeLocalInstance(@NonNull VisitPhoto entity) {
		dbService.delete(entity.getUuid());
	}
}
