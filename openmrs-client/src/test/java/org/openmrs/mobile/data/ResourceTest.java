package org.openmrs.mobile.data;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.mobile.models.Resource;

import java.util.UUID;

public class ResourceTest {
	@Test
	public void generateLocalUuid_shouldGenerateAUuidWithExpectedLength() throws Exception {
		String trueUuid = UUID.randomUUID().toString();
		Assert.assertEquals(Resource.LOCAL_UUID_LENGTH + 1, trueUuid.length());

		String localUuid = Resource.generateLocalUuid();
		Assert.assertEquals(Resource.LOCAL_UUID_LENGTH, localUuid.length());
	}

	@Test
	public void isLocalUuid_shouldReturnTrueIfLocalUuid() throws Exception {
		String localUuid = Resource.generateLocalUuid();

		Assert.assertTrue(Resource.isLocalUuid(localUuid));
	}

	@Test
	public void isLocalUuid_shouldReturnFalseIfServerUuid() throws Exception {
		String uuid = UUID.randomUUID().toString();

		Assert.assertFalse(Resource.isLocalUuid(uuid));
	}

	@Test
	public void isLocalUuid_shouldReturnFalseIfNullOrEmpty() throws Exception {
		Assert.assertFalse(Resource.isLocalUuid(null));
		Assert.assertFalse(Resource.isLocalUuid(""));
	}

	@Test
	public void new_shouldGenerateLocalUuid() throws Exception {
		Resource r = new Resource();

		Assert.assertNotNull(r.getUuid());
		Assert.assertTrue(Resource.isLocalUuid(r.getUuid()));
	}
}
