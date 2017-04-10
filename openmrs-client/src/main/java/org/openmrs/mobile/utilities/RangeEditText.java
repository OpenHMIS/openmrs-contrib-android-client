/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.utilities;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;

;

public class RangeEditText extends AppCompatEditText {
	
	private Double upperlimit;
	private Double lowerlimit;
	private String name;
	
	public RangeEditText(Context context) {
		super(context);
	}
	
	public double getUpperlimit() {
		return upperlimit;
	}
	
	public void setUpperlimit(Double upperlimit) {
		this.upperlimit = upperlimit;
	}
	
	public double getLowerlimit() {
		return lowerlimit;
	}
	
	public void setLowerlimit(Double lowerlimit) {
		this.lowerlimit = lowerlimit;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
