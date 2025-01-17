// SPDX-License-Identifier: MIT
package com.daimler.sechub.sereco.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.daimler.sechub.sereco.metadata.Classification;
import com.daimler.sechub.sereco.metadata.Detection;
import com.daimler.sechub.sereco.metadata.MetaDataAccess;
import com.daimler.sechub.sereco.metadata.Severity;
import com.daimler.sechub.sereco.metadata.Vulnerability;

public class AssertVulnerabilities {
	private List<Vulnerability> vulnerabilities = new ArrayList<>();

	AssertVulnerabilities(List<Vulnerability> list) {
		this.vulnerabilities.addAll(list);
	}

	public static AssertVulnerabilities assertVulnerabilities(List<Vulnerability> list) {
		return new AssertVulnerabilities(list);
	}

	public VulnerabilityFinder vulnerability() {
		return new VulnerabilityFinder();
	}

	public class VulnerabilityFinder {

		private Vulnerability search;

		private VulnerabilityFinder() {
			/* we use null values for search to search only for wanted parts...,+ some defaults (empty list) and empty description */
			String url = null;
			String type = null;
			Severity severity = null;
			List<Detection> list = new ArrayList<>();
			String description = "";
			Classification classification = null;

			search = MetaDataAccess.createVulnerability(url, type, severity, list, description, classification);
			;
		}

		private List<Vulnerability> find(StringBuilder metaInfo) {
			List<Vulnerability> list = find(metaInfo, false);
			if (list.isEmpty()) {
				find(metaInfo, true);
			}
			return list;
		}

		private boolean isEitherNullInSearchOrEqual(Object search, Object data) {
			if (search == null) {
				return true;
			}
			return TestUtils.equals(search, data);
		}

		private boolean isEitherNullInSearchOrContains(String string, String partWhichShallBeContained) {
			if (search == null) {
				return true;
			}
			return TestUtils.contains(string, partWhichShallBeContained);
		}

		private List<Vulnerability> find(StringBuilder message, boolean findClosest) {
			List<Vulnerability> matching = new ArrayList<>();
			SearchTracer trace = new SearchTracer();
			for (Vulnerability v : AssertVulnerabilities.this.vulnerabilities) {

				boolean contained = isEitherNullInSearchOrEqual(search.getSeverity(), v.getSeverity()) && trace.done(v, "severity");
				contained = contained && isEitherNullInSearchOrEqual(search.getUrl(), v.getUrl()) && trace.done(v, "url");
				contained = contained && isEitherNullInSearchOrEqual(search.getType(), v.getType()) && trace.done(v, "type");
				contained = contained && isEitherNullInSearchOrContains(v.getDescription(), search.getDescription()) && trace.done(v, "description");
				contained = contained && isEitherNullInSearchOrEqual(search.getClassification(), v.getClassification()) && trace.done(v, "classification");
				if (contained) {
					matching.add(v);
				}
			}
			if (findClosest) {
				message.append("Closest vulnerability was:\n" + trace.getClosest() + "\nThere was last ok:" + trace.getClosestLastCheck());
			}
			return matching;
		}

		private class SearchTracer {

			private int count = 0;
			private Vulnerability closest;
			private Vulnerability last;
			private int closestCount = 0;
			private String closestLastCheck;

			boolean done(Vulnerability v, String description) {
				if (last != v) {
					count = 0;
				}
				last = v;
				count++;
				if (count > closestCount) {
					closestCount = count;
					closest = v;
					closestLastCheck = description;
				}
				return true;
			}

			public Vulnerability getClosest() {
				return closest;
			}

			public String getClosestLastCheck() {
				return closestLastCheck;
			}

		}

		public VulnerabilityFinder withSeverity(Severity severity) {
			search.setSeverity(severity);
			return this;
		}

		public VulnerabilityFinder withURL(String url) {
			search.setUrl(url);
			return this;
		}

		public VulnerabilityFinder withType(String type) {
			search.setType(type);
			return this;
		}

		public VulnerabilityFinder withDescriptionContaining(String descriptionPart) {
			search.setDescription(descriptionPart);
			return this;
		}

		public AssertClassification classifiedBy() {
			return new AssertClassification();
		}

		public AssertVulnerabilities isNotContained() {
			return isContained(0);
		}

		public AssertVulnerabilities isContained() {
			return isContained(1);
		}

		private AssertVulnerabilities isContained(int expectedAmount) {
			StringBuilder message = new StringBuilder();
			List<Vulnerability> matches = find(message);
			if (matches.size() == expectedAmount) {
				return AssertVulnerabilities.this;
			}
			StringBuilder sb = new StringBuilder();
			for (Vulnerability v : vulnerabilities) {
				sb.append(v.toString());
				sb.append("\n");
			}
			assertEquals("Not found expected amount of vulnerabilities for given search.\nSearched for:\n" + search + " \n" + message.toString() + "\n",
					expectedAmount, matches.size());
			throw new IllegalStateException("Test must fail before by assertEquals!");
		}

		public class AssertClassification {

			private Classification classification;

			private AssertClassification() {
				classification = new Classification();
				MetaDataAccess.setClassification(VulnerabilityFinder.this.search, classification);
			}

			public AssertClassification hipaa(String hipaa) {
				classification.setHipaa(hipaa);
				return this;
			}

			public AssertClassification owaspProactiveControls(String owaspProactiveControls) {
				classification.setOwaspProactiveControls(owaspProactiveControls);
				return this;
			}

			public AssertClassification pci31(String pci31) {
				classification.setPci31(pci31);
				return this;
			}

			public AssertClassification pci32(String pci32) {
				classification.setPci32(pci32);
				return this;
			}

			public AssertClassification cwe(String cwe) {
				classification.setCwe(cwe);
				return this;
			}

			public AssertClassification capec(String capec) {
				classification.setCapec(capec);
				return this;
			}

			public AssertClassification owasp(String owasp) {
				classification.setOwasp(owasp);
				return this;
			}

			public AssertClassification wasc(String wasc) {
				classification.setWasc(wasc);
				return this;
			}

			public VulnerabilityFinder and() {
				return VulnerabilityFinder.this;
			}

		}

	}

	public AssertVulnerabilities hasVulnerabilities(int expectedAmount) {
		assertEquals("Amount of vulnerabilities differs", expectedAmount, vulnerabilities.size());
		return this;

	}

}