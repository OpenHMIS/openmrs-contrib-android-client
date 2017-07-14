package org.openmrs.mobile.data.impl;

import android.support.annotation.NonNull;

import org.openmrs.mobile.data.BaseDataService;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.db.impl.VisitPhotoDbService;
import org.openmrs.mobile.data.rest.VisitPhotoRestService;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class VisitPhotoDataService
		extends BaseDataService<VisitPhoto, VisitPhotoDbService, VisitPhotoRestService>
		implements DataService<VisitPhoto> {
	@Override
	protected Class<VisitPhotoRestService> getRestServiceClass() {
		return VisitPhotoRestService.class;
	}

	@Override
	protected VisitPhotoDbService getDbService() {
		return new VisitPhotoDbService();
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "custom";
	}

	@Override
	protected String getEntityName() {
		return "photos";
	}

	public void uploadPhoto(VisitPhoto visitPhoto, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, null,
				() -> dbService.save(visitPhoto),
				() -> {
					RequestBody patient =
							RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getPatient().getUuid());
					RequestBody visit = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getVisit().getUuid());
					RequestBody provider =
							RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getProvider().getUuid());
					RequestBody fileCaption = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getFileCaption());

					return restService.uploadVisitPhoto(buildRestRequestPath(), patient, visit,
							provider, fileCaption, visitPhoto.getRequestImage());
				});
	}

	public void downloadPhoto(String obsUuid, String view, @NonNull GetCallback<VisitPhoto> callback) {
		executeSingleCallback(callback, null,
				() -> dbService.getByUuid(obsUuid, null),
				() -> restService.downloadVisitPhoto(buildRestRequestPath(), obsUuid, view),
				body -> {
					try {
						VisitPhoto photo = new VisitPhoto();
						photo.setDownloadedImage(body.bytes());
						return photo;
					} catch (IOException ex) {
						return null;
					}
				}, (e) -> dbService.save(e)
		);
	}

	@Override
	protected Call<VisitPhoto> _restGetByUuid(String restPath, String uuid, QueryOptions options) {
		return null;
	}

	@Override
	protected Call<Results<VisitPhoto>> _restGetAll(String restPath, QueryOptions options, PagingInfo pagingInfo) {
		return null;
	}

	@Override
	protected Call<VisitPhoto> _restCreate(String restPath, VisitPhoto entity) {
		return null;
	}

	@Override
	protected Call<VisitPhoto> _restUpdate(String restPath, VisitPhoto entity) {
		return null;
	}

	@Override
	protected Call<VisitPhoto> _restPurge(String restPath, String uuid) {
		return null;
	}
}
