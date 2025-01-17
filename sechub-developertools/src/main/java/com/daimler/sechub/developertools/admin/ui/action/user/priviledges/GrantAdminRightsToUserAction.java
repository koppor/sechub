// SPDX-License-Identifier: MIT
package com.daimler.sechub.developertools.admin.ui.action.user.priviledges;

import java.awt.event.ActionEvent;
import java.util.Optional;

import com.daimler.sechub.developertools.admin.ui.UIContext;
import com.daimler.sechub.developertools.admin.ui.action.AbstractUIAction;
import com.daimler.sechub.developertools.admin.ui.cache.InputCacheIdentifier;

public class GrantAdminRightsToUserAction extends AbstractUIAction {
	private static final long serialVersionUID = 1L;

	public GrantAdminRightsToUserAction(UIContext context) {
		super("Grant admin rights to user",context);
	}

	@Override
	public void execute(ActionEvent e) {
		Optional<String> userToSignup = getUserInput("Please enter userid who will  gain admin rights",InputCacheIdentifier.USERNAME);
		if (!userToSignup.isPresent()) {
			return;
		}
		String data = getContext().getAdministration().gGrantAdminRightsTo(userToSignup.get());
		output(data);
	}

}