package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.FlowSQLiteOpenHelper;

public class CustomFlowSQliteOpenHelper extends FlowSQLiteOpenHelper {
	public CustomFlowSQliteOpenHelper(DatabaseDefinition databaseDefinition, DatabaseHelperListener listener) {
		super(databaseDefinition, listener);

		/*super(FlowManager.getContext(),
				databaseDefinition.isInMemory() ? nullString : databaseDefinition.getDatabaseFileName(),
				null,
				databaseDefinition.getDatabaseVersion());*/
	}
}
