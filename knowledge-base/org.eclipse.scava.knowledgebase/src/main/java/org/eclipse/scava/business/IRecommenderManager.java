/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.business;

import java.util.List;
import java.util.Map;

import org.eclipse.scava.business.dto.Query;
import org.eclipse.scava.business.dto.Recommendation;
import org.eclipse.scava.business.dto.RecommendationType;
import org.eclipse.scava.business.model.Artifact;
import org.eclipse.scava.business.model.Cluster;
import org.springframework.data.domain.Pageable;

/**
 * @author Juri Di Rocco
 *
 */
public interface IRecommenderManager {
	Recommendation getRecommendation(Query query, RecommendationType rt) throws Exception;
	List<Cluster> getClusters(String similarityName, String algorithmsName);
	List<Artifact> getSimilarProjects(String projectId, String similarityFunction, int numOfResult);
	Map<String, Double> getSimilarProjects(Artifact project, String similarityFunction, int numOfResult);
	List<Artifact> getArtifactsByQuery(String projectQuery);
	List<Artifact> getArtifactsByQuery(String projectQuery, Pageable page);
	Cluster getClusterByArtifact(String artId, String simCalculator, String clusterAlgorithm);
}
