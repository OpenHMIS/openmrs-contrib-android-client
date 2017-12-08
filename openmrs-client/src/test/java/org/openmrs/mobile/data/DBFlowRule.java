package org.openmrs.mobile.data;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openmrs.mobile.data.db.AppDatabase;
import org.robolectric.RuntimeEnvironment;

public class DBFlowRule implements TestRule {

	public static DBFlowRule create() {
		return new DBFlowRule();
	}

	private DBFlowRule() {
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				FlowManager.destroy();

				FlowManager.init(
						FlowConfig.builder(RuntimeEnvironment.application)
							.addDatabaseConfig(
								DatabaseConfig.inMemoryBuilder(AppDatabase.class)
										.databaseName("TestDatabase")
										.build())
						.build());
				try {
					base.evaluate();
				} finally {
					FlowManager.destroy();
				}
			}
		};
	}
}
