// SPDX-License-Identifier: MIT
package com.daimler.sechub.adapter.checkmarx.support;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.daimler.sechub.adapter.AdapterException;
import com.daimler.sechub.adapter.checkmarx.CheckmarxAdapterConfig;
import com.daimler.sechub.adapter.checkmarx.CheckmarxAdapterContext;
import com.daimler.sechub.adapter.checkmarx.CheckmarxContext;

public class CheckmarxUploadSupport {

	// https://checkmarx.atlassian.net/wiki/spaces/KC/pages/223313947/Upload+Source+Code+Zip+File+-+POST+projects+id+sourceCode+attachments
	// POST /projects/{id}/sourceCode/attachments and upload the zipped source code
	// https://www.baeldung.com/spring-rest-template-multipart-upload
	public void uploadZippedSourceCode(CheckmarxContext context)
			throws AdapterException {
		CheckmarxAdapterConfig config = context.getConfig();

		FileSystemResource sourceCodeFile = fetchSystemResource(context, config);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("zippedSource", sourceCodeFile);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		String url = context.getAPIURL("projects/" + context.getSessionData().getProjectId() + "/sourceCode/attachments");
		
		RestOperations restTemplate = context.getRestOperations();
		
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
		if (! result.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
			throw context.asAdapterException("Response HTTP status not as expected: "+result.getStatusCode(), null);
		}
	}

	private FileSystemResource fetchSystemResource(CheckmarxAdapterContext context, CheckmarxAdapterConfig config)
			throws AdapterException {
		String pathToZipFile = config.getPathToZipFile();
		/* currently we only provide file pathes... */
		File file = new File(pathToZipFile);
		if (!file.exists()) {
			throw context.asAdapterException("File does not exist:" + pathToZipFile, null);
		}
		return new FileSystemResource(file);
	}
}
