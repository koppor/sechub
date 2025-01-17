// SPDX-License-Identifier: MIT
package com.daimler.sechub.developertools.admin.ui.action.project;

import java.awt.event.ActionEvent;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.daimler.sechub.developertools.admin.ui.UIContext;
import com.daimler.sechub.developertools.admin.ui.action.AbstractUIAction;
import com.daimler.sechub.developertools.admin.ui.cache.InputCacheIdentifier;

public class DeleteProjectAction extends AbstractUIAction {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(DeleteProjectAction.class);

	public DeleteProjectAction(UIContext context) {
		super("Delete project", context);
	}

	@Override
	public void execute(ActionEvent e) {
		Optional<String> projectId = getUserInput("Please enter project ID/name to DELETE", InputCacheIdentifier.PROJECT_ID);
		if (! projectId.isPresent()) {
			return;
		}
		
		if (!confirm("Do you really want to\nDELETE\nproject "+projectId+"?")) {
			output("CANCELED - delete");
			LOG.info("canceled delete of {}",projectId);
			return;
		}
		LOG.info("start delete of {}",projectId);
		String data = getContext().getAdministration().deleteProject(projectId.get());
		output(data);
	}

}