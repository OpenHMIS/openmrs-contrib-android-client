package org.openmrs.mobile.data.rest.retrofit;

import org.openmrs.mobile.data.rest.RestConstants;
import org.openmrs.mobile.models.RecordInfo;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.VisitPhoto;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VisitPhotoRestService {
	@Multipart
	@POST("{restPath}")
	Call<VisitPhoto> uploadVisitPhoto(@Path(value = "restPath", encoded = true) String restPath,
			@Part("patient") RequestBody patient, @Part("visit") RequestBody visit,
			@Part("provider") RequestBody provider, @Part("fileCaption") RequestBody caption,
			@Part MultipartBody.Part request);

	@GET(RestConstants.REST_PATH)
	Call<ResponseBody> downloadVisitPhoto(@Path(value = "restPath", encoded = true) String restPath,
			@Query("obs") String uuid,
			@Query("view") String view);

	@GET(RestConstants.REST_PATH)
	Call<Results<RecordInfo>> getVisitPhotoRecordInfo(@Path(value = "restPath", encoded = true) String restPath,
			@Query("obs") String uuid,
			@Query("view") String view);

}
