// SPDX-License-Identifier: MIT
package com.daimler.sechub.sereco;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.daimler.sechub.sereco.importer.ProductFailureMetaDataBuilder;
import com.daimler.sechub.sereco.importer.ProductImportAbility;
import com.daimler.sechub.sereco.importer.ProductResultImporter;
import com.daimler.sechub.sereco.metadata.MetaData;
import com.daimler.sechub.sereco.metadata.Vulnerability;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Workspace {

	private static final Logger LOG = LoggerFactory.getLogger(Workspace.class);

	private MetaData workspaceMetaData = new MetaData();

	@Autowired
	private List<ProductResultImporter> importers;

	private String id;

	public List<Vulnerability> getVulnerabilties() {
		return workspaceMetaData.getVulnerabilities();
	}

	public Workspace(String id) {
		this.id=id;
	}

	public String getId() {
		return id;
	}

	public void doImport(ImportParameter param) throws IOException {
		if (param == null) {
			throw new IllegalArgumentException("param may not be null!");
		}
		if (param.getImportData() == null) {
			LOG.error("Import data was null for docId:{}, so unable to import.", param.getImportId());
			return;
		}
		if (param.getImportId() == null) {
			LOG.error("Import data was not null, but importId was not set, so unable to import.");
			return;
		}
		boolean atLeastOneImporterWasAbleToImport = false;
		for (ProductResultImporter importer : importers) {
			ProductImportAbility ableToImportForProduct = importer.isAbleToImportForProduct(param);
			if (ProductImportAbility.PRODUCT_FAILED.equals(ableToImportForProduct)) {
				/* means the importer would be able to import, but it is sure that the product failed,
				 * so we add just a critical finding for the product itself
				 */
				ProductFailureMetaDataBuilder builder = new ProductFailureMetaDataBuilder();
				MetaData metaData = builder.forParam(param).build();
				mergeWithWorkspaceData(metaData);
				atLeastOneImporterWasAbleToImport=true;
				break;
			}
			if (ProductImportAbility.ABLE_TO_IMPORT.equals(ableToImportForProduct)) {
				MetaData metaData = importer.importResult(param.getImportData());
				if (metaData == null) {
					LOG.error("Meta data was null for product={}, importer={}, importId={}", param.getProductId(),
							importer.getClass().getSimpleName(), param.getImportId());
					return;
				}
				mergeWithWorkspaceData(metaData);
				atLeastOneImporterWasAbleToImport = true;
			}
		}
		if (!atLeastOneImporterWasAbleToImport) {
			StringBuilder importerNames = new StringBuilder();
			importerNames.append("[");
			for (ProductResultImporter importer : importers) {
				importerNames.append(importer.getClass().getSimpleName());
				importerNames.append(" ");
			}
			importerNames.append("]");

			LOG.error(
					"For meta data from product={} with importId={} no importers was able to import it! Importers used ={}",
					param.getProductId(), param.getImportId(), importerNames);
			throw new IOException(
					"Import failed, no importer was able to import product result: " + param.getProductId());
		}

	}

	private void mergeWithWorkspaceData(MetaData metaData) {
		/* currently a very simple,stupid approach: */
		this.workspaceMetaData.getVulnerabilities().addAll(metaData.getVulnerabilities());
	}

	public String createReport() {
		try {
			return new ObjectMapper().writeValueAsString(workspaceMetaData);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Was not able to write report as json", e);
		}
	}

}
