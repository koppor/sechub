// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.scan.log;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.daimler.sechub.sharedkernel.APIConstants;
import com.daimler.sechub.sharedkernel.RoleConstants;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.usecases.admin.project.UseCaseAdministratorShowsScanLogsForProject;

@RestController
@EnableAutoConfiguration
@RequestMapping(APIConstants.API_ADMINISTRATION+"project/{projectId}") // API like https://developer.github.com/v3/issues/labels/#create-a-label
@RolesAllowed({RoleConstants.ROLE_SUPERADMIN})
public class ScanLogRestController {

	@Autowired
	private ProjectScanLogService projectScanLogService;


	/* @formatter:off */
	@UseCaseAdministratorShowsScanLogsForProject(@Step(number=1,next=2,name="REST API call to get JSON list",needsRestDoc=true))
	@RequestMapping(path = "/scan/logs", method = RequestMethod.GET, produces= {MediaType.APPLICATION_JSON_UTF8_VALUE,MediaType.APPLICATION_JSON_VALUE})
	public List<ProjectScanLogSummary> getScanLogsForProject(
			@PathVariable("projectId") String projectId
			) {
		/* @formatter:on */
		return projectScanLogService.fetchSummaryLogsFor(projectId);

	}


}
