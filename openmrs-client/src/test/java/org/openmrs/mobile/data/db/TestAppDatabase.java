package org.openmrs.mobile.data.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TestAppDatabase.NAME, version = TestAppDatabase.VERSION)
public class TestAppDatabase {
	public static final String NAME = "BandaHealth_Test"; // Will get added with a .db extension

	public static final int VERSION = 1;
}