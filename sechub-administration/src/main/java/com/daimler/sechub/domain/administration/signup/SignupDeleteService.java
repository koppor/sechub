// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.signup;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daimler.sechub.sharedkernel.RoleConstants;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.UserContextService;
import com.daimler.sechub.sharedkernel.usecases.admin.user.UseCaseAdministratorDeletesUser;

@Service
@RolesAllowed(RoleConstants.ROLE_SUPERADMIN)
public class SignupDeleteService {

	private static final Logger LOG = LoggerFactory.getLogger(SignupDeleteService.class);

	@Autowired
	SignupRepository userSelfRegistrationRepository;
	
	@Autowired
	UserContextService userContextService;
	
	@UseCaseAdministratorDeletesUser(@Step(number=2, name="Persistence", description="Existing signup will be deleted"))
	public void delete(String userId) {
		Signup foundByName = userSelfRegistrationRepository.findOrFailSignup(userId);
		userSelfRegistrationRepository.delete(foundByName);
		
		LOG.info("Existing user signup for {} deleted by {}",userId,userContextService.getUserId());
	}

}
