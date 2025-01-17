// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.user;

import javax.annotation.security.RolesAllowed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.daimler.sechub.domain.administration.AdministrationEnvironment;
import com.daimler.sechub.sharedkernel.RoleConstants;
import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.logging.AuditLogService;
import com.daimler.sechub.sharedkernel.messaging.DomainMessageFactory;
import com.daimler.sechub.sharedkernel.messaging.DomainMessageService;
import com.daimler.sechub.sharedkernel.messaging.IsSendingAsyncMessage;
import com.daimler.sechub.sharedkernel.messaging.MessageID;
import com.daimler.sechub.sharedkernel.usecases.admin.user.UseCaseAdministratorGrantsAdminRightsToUser;

@Service
@RolesAllowed(RoleConstants.ROLE_SUPERADMIN)
public class UserGrantSuperAdminRightsService {

	private static final Logger LOG = LoggerFactory.getLogger(UserGrantSuperAdminRightsService.class);

	@Autowired
	DomainMessageService eventBusService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuditLogService auditLogService;

	@Autowired
	AdministrationEnvironment administrationEnvironment;

	/* @formatter:off */
	@Validated
	@UseCaseAdministratorGrantsAdminRightsToUser(
			@Step(
					number = 2,
					name = "Service grants user admin rights.",
					next = { 3,	4 },
					description = "The service will grant user admin righs and triggers asynchronous events"))
	/* @formatter:on */
	public void grantSuperAdminRightsFor(String userId) {
		auditLogService.log("Triggered granting admin rights for user {}",userId);

		User user = userRepository.findOrFailUser(userId);

		if (user.isSuperAdmin()) {
			LOG.info("User:{} was already a super administrator, so just ignored",userId);
			return;
		}
		user.superAdmin=true;
		userRepository.save(user);

		requestUserRoleRecalculaton(user);
		informUserBecomesSuperadmin(user);

	}

	@IsSendingAsyncMessage(MessageID.USER_BECOMES_SUPERADMIN)
	private void informUserBecomesSuperadmin(User user) {
		eventBusService.sendAsynchron(DomainMessageFactory.createUserBecomesSuperAdmin(user.getName(), user.getEmailAdress(),administrationEnvironment.getAdministrationBaseURL()));
	}

	@IsSendingAsyncMessage(MessageID.REQUEST_USER_ROLE_RECALCULATION)
	private void requestUserRoleRecalculaton(User user) {
		eventBusService.sendAsynchron(DomainMessageFactory.createRequestRoleCalculation(user.getName()));
	}

}
