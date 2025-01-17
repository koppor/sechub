// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.project;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.daimler.sechub.sharedkernel.RoleConstants;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.usecases.admin.project.UseCaseAdministratorShowsProjectDetails;

@Service
@RolesAllowed(RoleConstants.ROLE_SUPERADMIN)
public class ProjectDetailInformationService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectDetailInformationService.class);

	@Autowired
	ProjectRepository projectRepository;
	
	/* @formatter:off */
	@Validated
	@UseCaseAdministratorShowsProjectDetails(
			@Step(
				number = 2, 
				name = "Service fetches project details.", 
				description = "The service will fetch project details"))
	/* @formatter:on */
	public ProjectDetailInformation fetchDetails(String projectId) {
		LOG.debug("fetching project details for project:{}",projectId);
		Project project = projectRepository.findOrFailProject(projectId);
		
		return new ProjectDetailInformation(project);		
	}
}
