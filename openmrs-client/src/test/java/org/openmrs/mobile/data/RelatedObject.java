package org.openmrs.mobile.data;

import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.openmrs.mobile.models.BaseOpenmrsObject;

public class RelatedObject {
	private Class<? extends BaseOpenmrsObject> modelClass;
	private ModelAdapter<? extends BaseOpenmrsObject> modelTable;
	private BaseOpenmrsObject entity;

	public RelatedObject() {}

	public RelatedObject(Class<? extends BaseOpenmrsObject> modelClass,
			ModelAdapter<? extends BaseOpenmrsObject> modelTable, BaseOpenmrsObject entity) {
		this.modelClass = modelClass;
		this.modelTable = modelTable;
		this.entity = entity;
	}

	public Class<? extends BaseOpenmrsObject> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<? extends BaseOpenmrsObject> modelClass) {
		this.modelClass = modelClass;
	}

	public ModelAdapter<? extends BaseOpenmrsObject> getModelTable() {
		return modelTable;
	}

	public void setModelTable(ModelAdapter<? extends BaseOpenmrsObject> modelTable) {
		this.modelTable = modelTable;
	}

	public BaseOpenmrsObject getEntity() {
		return entity;
	}

	public void setEntity(BaseOpenmrsObject entity) {
		this.entity = entity;
	}
}
