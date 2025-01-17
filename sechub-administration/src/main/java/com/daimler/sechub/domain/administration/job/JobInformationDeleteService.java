// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.job;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.daimler.sechub.sharedkernel.Step;
import com.daimler.sechub.sharedkernel.usecases.job.UseCaseSchedulerStartsJob;

@Service
public class JobInformationDeleteService {

	private static final Logger LOG = LoggerFactory.getLogger(JobInformationDeleteService.class);

	@Autowired
	JobInformationRepository repository;

	@Validated
	@UseCaseSchedulerStartsJob(@Step(number = 5, name = "Update admin job info", description = "Deletes store info in admin domain when job is done."))
	public void delete(UUID jobUUID) {
		LOG.debug("deleting job information for job with uuid:{}",jobUUID);
		repository.deleteJobInformationWithJobUUID(jobUUID);
	}

}
