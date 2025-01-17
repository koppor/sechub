// SPDX-License-Identifier: MIT
package com.daimler.sechub.sharedkernel.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ValidationResult{
	protected boolean valid = true;
	private List<String> errors=new ArrayList<>();
	
	public boolean isValid() {
		return valid;
	}
	
	public void addError(String error) {
		errors.add(error);
		valid=false;
	}
	
	public List<String> getErrors() {
		return errors;
	}

	public String getErrorDescription() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> it= errors.iterator();it.hasNext();) {
			String errorString = it.next();
			sb.append(errorString);
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}