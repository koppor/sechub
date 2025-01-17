// SPDX-License-Identifier: MIT
package com.daimler.sechub.sharedkernel.validation;

public interface Validation<T> {

	/**
	 * Validate given target
	 * @param target
	 * @return result, never <code>null</code>
	 */
	public ValidationResult validate(T target);
}
