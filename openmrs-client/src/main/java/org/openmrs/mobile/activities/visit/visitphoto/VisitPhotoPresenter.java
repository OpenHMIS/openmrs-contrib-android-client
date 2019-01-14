/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.visit.visitphoto;

import org.openmrs.mobile.activities.visit.BaseVisitPresenter;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ObsDataService;
import org.openmrs.mobile.data.impl.VisitPhotoDataService;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil.ToastType;

import java.util.Date;
import java.util.List;

public class VisitPhotoPresenter extends BaseVisitPresenter implements VisitContract.VisitPhotos.Presenter {

	private VisitContract.VisitPhotos.View visitPhotoView;
	private String patientUuid, visitUuid;
	private boolean loading;
	private VisitPhotoDataService visitPhotoDataService;
	private ObsDataService obsDataService;
	private VisitPhoto visitPhoto;

	private int numberOfPhotosToDownload = 0;
	private int numberOfPhotosDownloaded = 0;

	private boolean shouldRefreshVisitPhotos = false;

	public VisitPhotoPresenter(VisitContract.VisitPhotos.View visitPhotoView, String patientUuid, String visitUuid) {
		super(visitUuid, visitPhotoView);

		this.visitPhotoView = visitPhotoView;
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;

		this.visitPhotoDataService = dataAccess().visitPhoto();
		this.obsDataService = dataAccess().obs();
	}

	private void getPhotoMetadata(boolean forceRefresh) {
		visitPhotoView.showTabSpinner(true);

		QueryOptions optionsTemp = null;
		if (forceRefresh) {
			optionsTemp = QueryOptions.REMOTE;
		}
		final QueryOptions options = optionsTemp;

		// download all photo metadata
		visitPhotoDataService.downloadPhotoMetadata(visitUuid, options, obsDataService,
				new DataService.GetCallback<List<Observation>>() {
					@Override
					public void onCompleted(List<Observation> observations) {

						getPhotoImages(forceRefresh, observations, options);
					}

					@Override
					public void onError(Throwable t) {
						visitDashboardPageView.showToast(ApplicationConstants.toastMessages.imageDownloadError,
								ToastType.ERROR);
						visitPhotoView.showTabSpinner(false);
						visitDashboardPageView.displayRefreshingData(false);
					}
				});
	}

	private void getPhotoImages(boolean forceRefresh, List<Observation> observations, QueryOptions options) {
		// get local photos
		List<VisitPhoto> visitPhotos = visitPhotoDataService.getByVisit(new Visit(visitUuid));

		if (forceRefresh && observations != null) {
			numberOfPhotosToDownload = observations.size();
		}

		if (observations == null || observations.isEmpty()) {
			visitPhotoView.showTabSpinner(false);
			visitPhotoView.updateVisitImageMetadata(visitPhotos);
			removeRefreshIndicatorIfAllCallsComplete();
			return;
		}

		for (Observation observation : observations) {
			if (searchLocalVisitPhoto(observation, visitPhotos)) {
				visitPhotoView.updateVisitImageMetadata(visitPhotos);
				if (forceRefresh) {
					removeRefreshIndicatorIfAllCallsComplete();
				}
				continue;
			}

			VisitPhoto visitPhoto = new VisitPhoto();
			visitPhoto.setFileCaption(observation.getComment());
			visitPhoto.setDateCreated(observation.getDateCreated());

			visitPhoto.setCreator(observation.getCreator());

			visitPhoto.setObservation(observation);

			// download photo bytes
			visitPhotoDataService.downloadPhotoImage(visitPhoto, ApplicationConstants.THUMBNAIL_VIEW,
					options,
					new DataService.GetCallback<VisitPhoto>() {
						@Override
						public void onCompleted(VisitPhoto entity) {
							if (entity != null && entity.getImageColumn() != null) {
								visitPhoto.setImage(entity.getImageColumn().getBlob());
								visitPhotos.add(visitPhoto);
								visitPhotoView.showTabSpinner(false);

								visitPhotoView.updateVisitImageMetadata(visitPhotos);

								if (forceRefresh) {
									removeRefreshIndicatorIfAllCallsComplete();
								}
							}
						}

						@Override
						public void onError(Throwable t) {
							visitPhotoView.showTabSpinner(false);
							visitDashboardPageView.showToast(t.getMessage(), ToastType.ERROR);

							if (forceRefresh) {
								removeRefreshIndicatorIfAllCallsComplete();
							}
						}
					});
		}

		visitPhotoView.showTabSpinner(false);
	}

	private boolean searchLocalVisitPhoto(Observation obs, List<VisitPhoto> visitPhotos) {
		for (VisitPhoto visitPhoto : visitPhotos) {
			if (visitPhoto.getObservation() != null) {
				if (visitPhoto.getObservation().getUuid().equalsIgnoreCase(obs.getUuid())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void subscribe() {
		initVisitPhoto();
		if (shouldRefreshVisitPhotos) {
			getPhotoMetadata(true);
			shouldRefreshVisitPhotos = false;
		} else {
			getPhotoMetadata(false);
		}
	}

	private void initVisitPhoto() {
		if (visitPhoto != null) {
			return;
		}

		visitPhoto = new VisitPhoto();
		Visit visit = null;
		if (!StringUtils.isNullOrEmpty(visitUuid)) {
			visit = dataAccess().visit().getLocalByUuid(visitUuid, null);
		}
		if (visit == null) {
			visit = new Visit();
			visit.setUuid(visitUuid);
		}

		Provider provider = null;
		String providerUuid = OpenMRS.getInstance().getCurrentUserUuid();
		if (!StringUtils.isNullOrEmpty(providerUuid)) {
			dataAccess().provider().getLocalByUuid(providerUuid, null);
		}
		if (provider == null) {
			provider = new Provider();
			provider.setUuid(OpenMRS.getInstance().getCurrentUserUuid());
		}

		Patient patient = null;
		if (!StringUtils.isNullOrEmpty(patientUuid)) {
			dataAccess().patient().getLocalByUuid(patientUuid, null);
		}
		if (patient == null){
			patient	= new Patient();
			patient.setUuid(patientUuid);
		}

		visitPhoto.setVisit(visit);
		visitPhoto.setProvider(provider);
		visitPhoto.setPatient(patient);

		User currentUser = dataAccess().user().getLocalByUuid(OpenMRS.getInstance().getUserUuid(), null);
		if (currentUser != null) {
			visitPhoto.setCreator(currentUser);
		}
	}

	@Override
	public void uploadPhoto(byte[] image, String description) {
		visitPhotoView.showTabSpinner(true);
		visitPhoto.setImage(image);
		visitPhoto.setFileCaption(description);
		visitPhoto.setDateCreated(new Date());
		visitPhotoDataService.uploadPhoto(visitPhoto, new DataService.GetCallback<VisitPhoto>() {
			@Override
			public void onCompleted(VisitPhoto entity) {
				visitPhoto = null;
				visitPhotoView.showTabSpinner(false);
				visitPhotoView.reset();
				visitPhotoView.refresh();
				subscribe();
			}

			@Override
			public void onError(Throwable t) {
				visitPhotoView.showTabSpinner(false);
				visitDashboardPageView.showToast(ApplicationConstants.toastMessages.imageUploadError, ToastType.ERROR);
			}
		});
	}

	@Override
	public boolean isLoading() {
		return loading;
	}

	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void deletePhoto(VisitPhoto visitPhoto) {
		visitPhotoView.showTabSpinner(true);
		Observation obs = visitPhoto.getObservation();
		obs.setVoided(true);

		obsDataService.purge(obs, new DataService.VoidCallback() {
			@Override
			public void onCompleted() {
				// delete locally saved instance if any (visitphoto not stored online)
				visitPhotoDataService.purgeLocalInstance(visitPhoto);
				visitPhotoView.showTabSpinner(false);
				visitPhotoView.refresh();
				subscribe();
			}

			@Override
			public void onError(Throwable t) {
				visitPhotoView.showTabSpinner(false);
			}
		});
	}

	@Override
	protected void refreshDependentData() {
		numberOfPhotosDownloaded = 0;
		getPhotoMetadata(true);
	}

	@Override
	public void refreshPhotosWhenVisible() {
		shouldRefreshVisitPhotos = true;
	}

	private void removeRefreshIndicatorIfAllCallsComplete() {
		numberOfPhotosDownloaded++;
		if (numberOfPhotosDownloaded == numberOfPhotosToDownload || numberOfPhotosToDownload == 0) {
			visitDashboardPageView.displayRefreshingData(false);
		}
	}
}
