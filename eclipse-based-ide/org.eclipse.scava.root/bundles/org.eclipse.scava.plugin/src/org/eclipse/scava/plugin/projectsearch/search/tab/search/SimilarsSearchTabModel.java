/*********************************************************************
* Copyright c 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse PublicLicense 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.scava.plugin.projectsearch.search.tab.search;

import java.util.Collections;
import java.util.List;

import org.eclipse.scava.plugin.knowledgebase.access.KnowledgeBaseAccess;
import org.eclipse.scava.plugin.knowledgebase.access.SimilarityMethod;
import org.eclipse.scava.plugin.preferences.Preferences;

import io.swagger.client.ApiException;
import io.swagger.client.api.RecommenderRestControllerApi;
import io.swagger.client.model.Artifact;

public class SimilarsSearchTabModel extends SearchTabModel {
	private final Artifact referenceProject;
	private final SimilarityMethod method;

	public SimilarsSearchTabModel(KnowledgeBaseAccess knowledgeBaseAccess, Artifact referenceProject,
			SimilarityMethod method) {
		super(knowledgeBaseAccess);
		this.referenceProject = referenceProject;
		this.method = method;
		hasNextPage = false;
	}

	@Override
	public List<Artifact> getNextPageResults() throws ApiException {
		if (nextPage != 0) {
			return Collections.emptyList();
		}

		RecommenderRestControllerApi recommenderRestController = knowledgeBaseAccess.getRecommenderRestController(Preferences.TIMEOUT_PROJECTSEARCH);
		List<Artifact> projects = recommenderRestController.getSimilarProjectUsingGET(method.name(),
				referenceProject.getId(), pageSize);
		nextPage++;
		return projects;
	}

	@Override
	public String getDescription() {
		return "Similars to \"" + referenceProject.getFullName() + "\" by " + method.name();
	}

}
