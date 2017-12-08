/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models;

import com.raizlabs.android.dbflow.annotation.Table;

import org.openmrs.mobile.data.db.AppDatabase;

@Table(database = AppDatabase.class)
public class VisitType extends BaseOpenmrsMetadata {
	public VisitType() {

	}

	public VisitType(String display) {
		this.display = display;
	}

	public VisitType(String display, String uuid) {
		this.uuid = uuid;
		this.display = display;
	}

	@Override
	public String toString() {
		return getDisplay();
	}
}
