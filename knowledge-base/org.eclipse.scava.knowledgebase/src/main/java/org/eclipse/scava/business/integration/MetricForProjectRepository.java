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

import org.eclipse.scava.business.dto.metrics.MetricsForProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Juri Di Rocco
 *
 */
public interface MetricForProjectRepository extends MongoRepository<MetricsForProject, String> {
	List<MetricsForProject> findByUserId(String userId);
	List<MetricsForProject> findByProjectId(String projectId);
	public Page<MetricsForProject> findAll(Pageable pageable);
}
