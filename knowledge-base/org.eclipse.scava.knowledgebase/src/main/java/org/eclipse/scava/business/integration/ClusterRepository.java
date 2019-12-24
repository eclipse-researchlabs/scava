/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.business.integration;

import java.util.List;

import org.bson.types.ObjectId;
import org.eclipse.scava.business.model.Cluster;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Juri Di Rocco
 *
 */
public interface ClusterRepository extends MongoRepository<Cluster, String> {
	Cluster findOneByArtifactsName(String name);
	Cluster findOneByArtifactsIdAndClusterizationId(ObjectId artifactId, ObjectId clusterization);
	List<Cluster> findByClusterizationId(ObjectId clusterizationId);
}
