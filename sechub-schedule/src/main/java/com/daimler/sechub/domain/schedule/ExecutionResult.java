// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.schedule;

public enum ExecutionResult {

	/**
	 * No execution triggered
	 */
	NONE,

	/**
	 * Execution was DONE without failure on execution. This information is only
	 * about the execution process - e.g. a Scan which finds vulnerabilities and
	 * found 3 HIGH level CVEs will return OK when the scan was done and the
	 * reporting fulfilled !
	 * 
	 */
	OK,

	/**
	 * Execution was NOT DONE because of with failures - This information is only
	 * about the execution process - e.g. the scan would not find any CVEs but the
	 * scan server is done than FAILED will be returned!
	 */
	FAILED,

	/*
	 * TODO Albert Tregnaghi, 2018-01-30: it would be nice to have a CANCELED in future - but
	 * only when this use case is implemented...
	 */
	;

}
