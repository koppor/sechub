// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.scan.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daimler.sechub.domain.scan.access.ScanAccess.ProjectAccessCompositeKey;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.usecases.admin.user.UseCaseAdministratorUnassignsUserFromProject;

@Service
public class ScanRevokeUserAccessFromProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ScanRevokeUserAccessFromProjectService.class);

	@Autowired
	ScanAccessRepository repository;

	@UseCaseAdministratorUnassignsUserFromProject(@Step(number=2,name="Update authorization parts"))
	public void revokeUserAccessFromProject(String userId, String projectId) {
		ProjectAccessCompositeKey id = new ProjectAccessCompositeKey(userId, projectId);
		repository.deleteById(id);

		LOG.info("Revoked access to project:{} for user:{}",projectId,userId);
	}


}
