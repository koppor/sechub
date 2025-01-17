// SPDX-License-Identifier: MIT
package com.daimler.sechub.domain.administration.project;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.daimler.sechub.domain.administration.user.User;

@Entity
@Table(name = Project.TABLE_NAME)
public class Project {

	/* +-----------------------------------------------------------------------+ */
	/* +............................ SQL ......................................+ */
	/* +-----------------------------------------------------------------------+ */
	public static final String TABLE_NAME = "ADM_PROJECT";
	public static final String TABLE_NAME_PROJECT_TO_USER = "ADM_PROJECT_TO_USER";
	public static final String TABLE_NAME_PROJECT_WHITELIST_URI = "ADM_PROJECT_WHITELIST_URI";

	public static final String COLUMN_PROJECT_ID = "PROJECT_ID";
	public static final String COLUMN_PROJECT_OWNER = "PROJECT_OWNER";
	public static final String COLUMN_PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";
	public static final String COLUMN_WHITELIST_URIS = "PROJECT_WHITELIST_URIS";

	/* +-----------------------------------------------------------------------+ */
	/* +............................ JPQL .....................................+ */
	/* +-----------------------------------------------------------------------+ */
	public static final String CLASS_NAME = Project.class.getSimpleName();

	public static final String PROPERTY_USERS = "users";
	public static final String PROPERTY_OWNER = "owner";

	@Id
	@Column(name = COLUMN_PROJECT_ID)
	String id;

	@Column(name = COLUMN_PROJECT_DESCRIPTION)
	String description;

	// no merge cascade or persist, because owner and user in set
	// otherwise leading to "java.lang.IllegalStateException: Multiple representations of the same entity ... are being merged"
	@ManyToMany(cascade = {CascadeType.REMOVE,CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(name = TABLE_NAME_PROJECT_TO_USER)
	Set<User> users = new HashSet<>();

	// we do not CascadeType.Persist or ALL because otherwise user will be persisted again and leads to unique constraint violation
	@ManyToOne(cascade = {CascadeType.REMOVE,CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinColumn(name = COLUMN_PROJECT_OWNER, nullable = false)
	User owner;

	@Column(name = COLUMN_WHITELIST_URIS, nullable = false)
	@ElementCollection(targetClass = URI.class, fetch = FetchType.EAGER)
	@CollectionTable(name = TABLE_NAME_PROJECT_WHITELIST_URI)
	Set<URI> whiteList = new HashSet<>();

	@Version
	@Column(name = "VERSION")
	Integer version;

	/**
	 * Returns white list entries. Why URIs and not URIs and IPs? Because an
	 * IP can be contained as LITERALS inside a URI (v4 and v6)- see {@link URI}
	 * for details
	 * @return a set with white lists.
	 */
	public Set<URI> getWhiteList() {
		return whiteList;
	}

	public Set<User> getUsers() {
		return users;
	}

	public User getOwner() {
		return owner;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}