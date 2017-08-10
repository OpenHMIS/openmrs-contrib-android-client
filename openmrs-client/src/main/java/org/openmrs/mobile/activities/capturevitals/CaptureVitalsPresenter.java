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

package org.openmrs.mobile.activities.capturevitals;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.EncounterDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.Location;

public class CaptureVitalsPresenter extends BasePresenter implements CaptureVitalsContract.Presenter {

	private CaptureVitalsContract.View captureVitalsView;
	private EncounterDataService encounterDataService;
	private LocationDataService locationDataService;

	public CaptureVitalsPresenter(CaptureVitalsContract.View view) {
		this.captureVitalsView = view;
		this.captureVitalsView.setPresenter(this);

		this.encounterDataService = dataAccess().encounter();
		this.locationDataService = dataAccess().location();
	}

	@Override
	public void subscribe() {
	}

	@Override
	public void fetchLocation(String locationUuid) {
		captureVitalsView.showPageSpinner(true);
		DataService.GetCallback<Location> locationDataServiceCallback = new DataService.GetCallback<Location>() {
			@Override
			public void onCompleted(Location location) {
				//set location in the fragment and start loading other fields
				captureVitalsView.setLocation(location);
				captureVitalsView.showPageSpinner(false);
			}

			@Override
			public void onError(Throwable t) {
				captureVitalsView.showPageSpinner(false);
				t.printStackTrace();
			}
		};

		locationDataService.getByUuid(locationUuid, QueryOptions.FULL_REP, locationDataServiceCallback);
	}

	@Override
	public void attemptSave(Encounter encounter) {
		captureVitalsView.showProgressBar(true);
		DataService.GetCallback<Encounter> serverResponceCallback = new DataService.GetCallback<Encounter>() {
			@Override
			public void onCompleted(Encounter encounter) {
				if (encounter == null) {
					captureVitalsView.showProgressBar(false);
				} else {
					captureVitalsView.hideSoftKeys();
					captureVitalsView.disableButton();
					((CaptureVitalsActivity)captureVitalsView.getContext()).finish();
				}
			}

			@Override
			public void onError(Throwable t) {
				captureVitalsView.showProgressBar(false);
				t.printStackTrace();
			}
		};

		encounterDataService.create(encounter, serverResponceCallback);

	}

}

