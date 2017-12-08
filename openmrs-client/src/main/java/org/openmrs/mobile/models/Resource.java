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

import android.support.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openmrs.mobile.utilities.Consumer;
import org.openmrs.mobile.utilities.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Resource implements Serializable {
	private static final long serialVersionUID = 1;

	public static final int LOCAL_UUID_LENGTH = 35;

	@SerializedName("uuid")
	@Expose
	@PrimaryKey
	protected String uuid = generateLocalUuid();

	@SerializedName("display")
	@Expose
	@Column
	protected String display;

	/**
	 * Returns {@code true} if the specified uuid is a local uuid (based on the string length); otherwise, false.
	 * @param uuid The uuid to check
	 * @return True if the uuid is a local uuid, otherwise; false.
	 */
	public static boolean isLocalUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return false;
		}

		return (uuid.length() == LOCAL_UUID_LENGTH);
	}

	/**
	 * Generates a uuid with the last character trimmed off so that the uuid can be differentiated from a server-generated
	 * uuid. Note the while this does reduce the uniqueness of the id, these uuids are intended to only be used until the
	 * resource is saved to the server, at which point this local uuid will be replaced.
	 * @return The local uuid.
	 */
	public static String generateLocalUuid() {
		String result = UUID.randomUUID().toString();

		return result.substring(0, result.length() - 1);
	}

	/**
	 * @return The uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid The uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return The display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @param display The display
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	public void processRelationships() { }

	protected <R extends Resource> void processRelatedObjects(@Nullable List<R> resources) {
		processRelatedObjects(resources, null);
	}

	protected <R extends Resource> void processRelatedObjects(@Nullable List<R> resources, @Nullable Consumer<R> process) {
		if (resources != null && !resources.isEmpty()) {
			for (R r : resources) {
				if (process != null) {
					process.accept(r);
				}

				r.processRelationships();
			}
		}
	}

	protected <E> List<E> loadRelatedObject(Class<E> cls, List<E> field, Supplier<SQLOperator> op) {
		if (field == null) {
			field = new ArrayList<>();
		}

		if (field.isEmpty()) {
			field.addAll(
					SQLite.select()
							.from(cls)
							.where(op.get()).queryList()
			);
		}

		return field;
	}
}
