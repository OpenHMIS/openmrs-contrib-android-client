package org.openmrs.mobile.models.queryModel;

import java.util.ArrayList;
import java.util.List;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;
import org.openmrs.mobile.data.db.AppDatabase;

@QueryModel(database = AppDatabase.class)
public class EntityUuid {

	@Column
	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public static List<String> getUuids(List<EntityUuid> entityUuids) {
		List<String> uuids = new ArrayList<>();
		for (EntityUuid uuid : entityUuids) {
			uuids.add(uuid.getUuid());
		}
		return uuids;
	}
}
