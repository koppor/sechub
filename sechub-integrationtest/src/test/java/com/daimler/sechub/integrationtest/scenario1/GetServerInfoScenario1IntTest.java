// SPDX-License-Identifier: MIT
package com.daimler.sechub.integrationtest.scenario1;

import static com.daimler.sechub.integrationtest.api.TestAPI.*;
import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.Test;

import com.daimler.sechub.integrationtest.api.IntegrationTestSetup;

public class GetServerInfoScenario1IntTest {

	private static final Pattern PATTERN_ONLY_MAJOR_MINOR_HOTFIX = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+.*");

	@Rule
	public IntegrationTestSetup setup = IntegrationTestSetup.forScenario(Scenario1.class);

	/* +-----------------------------------------------------------------------+ */
	/* +............................ Get server version .......................+ */
	/* +-----------------------------------------------------------------------+ */

	@Test
	public void get_server_version() {
		/* execute */
		String version = as(ANONYMOUS).getServerVersion();

		/* test*/
		assertNotNull(version);
		assertFalse(version.isEmpty());
		/* version must be X.Y.Z and not something ala vX.Y.Z or X.Y.Z-server"*/
		assertFalse(version.startsWith("v"));
		assertFalse(version.endsWith("-server"));
		/* check format is like regexp */
		assertTrue(PATTERN_ONLY_MAJOR_MINOR_HOTFIX.matcher(version).matches());
	}


}
