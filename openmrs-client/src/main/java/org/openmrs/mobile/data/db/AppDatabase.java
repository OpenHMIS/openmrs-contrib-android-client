package org.openmrs.mobile.data.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.openmrs.mobile.data.sync.impl.ConceptClassSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.DiagnosisConceptSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.EncounterTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.LocationSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientIdentifierTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PatientListSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.PersonAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitAttributeTypeSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitPredefinedTaskSubscriptionProvider;
import org.openmrs.mobile.data.sync.impl.VisitTypeSubscriptionProvider;
import org.openmrs.mobile.models.PullSubscription;

import java.util.ArrayList;
import java.util.List;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
	public static final String NAME = "BandaHealth"; // Will get added with a .db extension

	public static final int VERSION = 2;

	@Migration(version = 2, database = AppDatabase.class, priority = 10)
	public static class RecreateDbMigration extends BaseMigration {
		@Override
		public void migrate(@NonNull DatabaseWrapper database) {
			for (Class<?> cls : FlowManager.getDatabase(AppDatabase.class).getModelClasses()) {
				ModelAdapter modelAdapter = FlowManager.getModelAdapter(cls);
				database.execSQL("DROP TABLE IF EXISTS " + modelAdapter.getTableName());

				database.execSQL(modelAdapter.getCreationQuery());
			}
		}
	}

	@Migration(version = 2, database = AppDatabase.class, priority = 0)
	public static class CreateSubscriptionsMigration extends BaseMigration {
		private static final int SECONDS_IN_MINUTE = 60;
		private static final int MINUTES_IN_HOUR = 60;
		private static final int HOURS_IN_DAY = 24;

		private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
		private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY;

		@Override
		public void migrate(@NonNull DatabaseWrapper database) {
			List<PullSubscription> pullSubscriptions = new ArrayList<>();

			pullSubscriptions.add(
					newPullSub(ConceptClassSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(DiagnosisConceptSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(EncounterTypeSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(LocationSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(PatientIdentifierTypeSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(PatientListSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(PersonAttributeTypeSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(VisitAttributeTypeSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(VisitPredefinedTaskSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);
			pullSubscriptions.add(
					newPullSub(VisitTypeSubscriptionProvider.class.getSimpleName(), 25, SECONDS_IN_DAY)
			);


			FlowManager.getModelAdapter(PullSubscription.class).saveAll(pullSubscriptions, database);
		}

		private PullSubscription newPullSub(String cls, Integer maxIncCount, Integer minInterval) {
			PullSubscription sub = new PullSubscription();
			sub.setForceSyncAfterPush(false);
			sub.setSubscriptionClass(cls);
			sub.setSubscriptionKey(null);
			sub.setMaximumIncrementalCount(maxIncCount);
			sub.setMinimumInterval(minInterval);

			return sub;
		}
	}
}