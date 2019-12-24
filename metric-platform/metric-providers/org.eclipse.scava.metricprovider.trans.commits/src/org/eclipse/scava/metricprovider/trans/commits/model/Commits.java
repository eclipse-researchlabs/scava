/*******************************************************************************
 * Copyright (c) 2018 University of York
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.trans.commits.model;

import com.googlecode.pongo.runtime.*;
import com.mongodb.*;

public class Commits extends PongoDB {
	
	public Commits() {}
	
	public Commits(DB db) {
		setDb(db);
	}
	
	protected RepositoryDataCollection repositories = null;
	protected CommitDataCollection commits = null;
	
	
	
	public RepositoryDataCollection getRepositories() {
		return repositories;
	}
	
	public CommitDataCollection getCommits() {
		return commits;
	}
	
	
	@Override
	public void setDb(DB db) {
		super.setDb(db);
		repositories = new RepositoryDataCollection(db.getCollection("Commits.repositories"));
		pongoCollections.add(repositories);
		commits = new CommitDataCollection(db.getCollection("Commits.commits"));
		pongoCollections.add(commits);
	}
}
