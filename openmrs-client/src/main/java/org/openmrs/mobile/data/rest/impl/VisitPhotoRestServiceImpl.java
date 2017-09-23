package org.openmrs.mobile.data.rest.impl;

import org.openmrs.mobile.data.rest.BaseRestService;
import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.data.rest.retrofit.VisitPhotoRestService;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class VisitPhotoRestServiceImpl extends BaseRestService<VisitPhoto, VisitPhotoRestService> {
	@Inject
	public VisitPhotoRestServiceImpl() {
	}

	@Override
	protected String getRestPath() {
		return ApplicationConstants.API.REST_ENDPOINT_V2 + "custom";
	}

	@Override
	protected String getEntityName() {
		return "photos";
	}

	public Call<VisitPhoto> upload(VisitPhoto visitPhoto) {
		RequestBody patient =
				RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getPatient().getUuid());
		RequestBody visit = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getVisit().getUuid());
		RequestBody provider =
				RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getProvider().getUuid());
		RequestBody fileCaption = RequestBody.create(MediaType.parse("text/plain"), visitPhoto.getFileCaption());
		RequestBody file = RequestBody.create(MediaType.parse("image/jpeg"), visitPhoto.getImageColumn().getBlob());
		MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", "create.jpg", file);

		return restService.uploadVisitPhoto(buildRestRequestPath(), patient, visit,
				provider, fileCaption, uploadFile);
	}

	public Call<ResponseBody> downloadPhoto(String obsUuid, String view) {
		return restService.downloadVisitPhoto(buildRestRequestPath(), obsUuid, view);
	}

	public Call<Results<RecordInfo>> getPhotoRecordInfo(String obsUuid, String view) {
		return restService.getVisitPhotoRecordInfo(buildRestRequestPath(), obsUuid,
				RestConstants.Representations.RECORD_INFO);
	}
}
