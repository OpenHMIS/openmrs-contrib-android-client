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

import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.activities.visit.VisitPresenterImpl;
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
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitPhotoPresenter extends VisitPresenterImpl implements VisitContract.VisitPhotoPresenter {

	private VisitContract.VisitPhotoView visitPhotoView;
	private String patientUuid, visitUuid, providerUuid;
	private boolean loading;
	private VisitPhotoDataService visitPhotoDataService;
	private ObsDataService obsDataService;
	private VisitPhoto visitPhoto;

	public VisitPhotoPresenter(VisitContract.VisitPhotoView visitPhotoView, String patientUuid, String visitUuid,
			String providerUuid) {
		this.visitPhotoView = visitPhotoView;
		this.visitPhotoView.setPresenter(this);
		this.patientUuid = patientUuid;
		this.visitUuid = visitUuid;
		this.providerUuid = providerUuid;

		this.visitPhotoDataService = dataAccess().visitPhoto();
		this.obsDataService = dataAccess().obs();
	}

	private void loadVisitDocumentObservations() {
		visitPhotoView.showTabSpinner(true);
		// get obs for patient.
		obsDataService.getVisitDocumentsObsByPatientAndConceptList(patientUuid, null,
				new DataService.GetCallback<List<Observation>>() {
					@Override
					public void onCompleted(List<Observation> observations) {
						List<VisitPhoto> visitPhotos = new ArrayList<>();
						for (Observation observation : observations) {
							if (null != observation.getEncounter().getVisit().getUuid() &&
									observation.getEncounter().getVisit().getUuid().equalsIgnoreCase(visitUuid)) {
								VisitPhoto visitPhoto = new VisitPhoto();
								visitPhoto.setFileCaption(observation.getComment());
								visitPhoto.setDateCreated(new Date(DateUtils.convertTime(observation.getObsDatetime())));

								User creator = new User();
								creator.setPerson(observation.getPerson());
								visitPhoto.setCreator(creator);

								visitPhoto.setObservation(observation);
								visitPhotos.add(visitPhoto);
							}
						}

						if (!visitPhotos.isEmpty()) {
							visitPhotoView.updateVisitImageMetadata(visitPhotos);
						} else {
							visitPhotoView.showNoVisitPhoto();
						}
						visitPhotoView.showTabSpinner(false);
					}

					@Override
					public void onError(Throwable t) {
						visitPhotoView.showTabSpinner(false);
					}
				});
	}

	@Override
	public void downloadImage(String obsUuid, DataService.GetCallback<byte[]> callback) {
		visitPhotoView.showTabSpinner(true);
		visitPhotoDataService.downloadPhoto(obsUuid, ApplicationConstants.THUMBNAIL_VIEW,
				new DataService.GetCallback<VisitPhoto>() {
					@Override
					public void onCompleted(VisitPhoto entity) {
						callback.onCompleted(entity.getDownloadedImage());
						visitPhotoView.showTabSpinner(false);
					}

					@Override
					public void onError(Throwable t) {
						visitPhotoView.showTabSpinner(false);
						callback.onError(t);
						ToastUtil.error(t.getMessage());
					}
				});
	}

	@Override
	public void subscribe() {
		initVisitPhoto();
		loadVisitDocumentObservations();
	}

	private void initVisitPhoto() {
		if(visitPhoto != null){
			return;
		}

		visitPhoto = new VisitPhoto();
		Visit visit = new Visit();
		visit.setUuid(visitUuid);

		Provider provider = new Provider();
		provider.setUuid(providerUuid);

		Patient patient = new Patient();
		patient.setUuid(patientUuid);

		visitPhoto.setVisit(visit);
		visitPhoto.setProvider(provider);
		visitPhoto.setPatient(patient);
	}

	@Override
	public void uploadImage() {
		visitPhotoView.showTabSpinner(true);
		visitPhotoDataService.uploadPhoto(visitPhoto, new DataService.GetCallback<VisitPhoto>() {
			@Override
			public void onCompleted(VisitPhoto entity) {
				visitPhotoView.showTabSpinner(false);
				visitPhotoView.refresh();
			}

			@Override
			public void onError(Throwable t) {
				visitPhotoView.showTabSpinner(false);
				ToastUtil.error(t.getMessage());
			}
		});
	}

	@Override
	public VisitPhoto getVisitPhoto() {
		return visitPhoto;
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
	public void unsubscribe() {}

	@Override
	public void deleteImage(VisitPhoto visitPhoto) {
		visitPhotoView.showTabSpinner(true);
		Observation obs = visitPhoto.getObservation();
		obs.setVoided(true);
		obsDataService.purge(obs, new DataService.VoidCallback() {
			@Override
			public void onCompleted() {
				visitPhotoView.showTabSpinner(false);
				visitPhotoView.refresh();
			}

			@Override
			public void onError(Throwable t) {
				visitPhotoView.showTabSpinner(false);
			}
		});
	}
}
