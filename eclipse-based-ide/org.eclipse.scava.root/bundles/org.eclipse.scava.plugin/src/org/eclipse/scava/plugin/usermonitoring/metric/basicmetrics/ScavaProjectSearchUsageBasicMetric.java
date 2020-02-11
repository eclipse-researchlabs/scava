/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    Zsolt János Szamosvölgyi
*    Endre Tamás Váradi
*    Gergõ Balogh
**********************************************************************/
package org.eclipse.scava.plugin.usermonitoring.metric.basicmetrics;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.scava.plugin.usermonitoring.event.events.scava.ScavaCodeRecommendationUsageEvent;
import org.eclipse.scava.plugin.usermonitoring.event.events.scava.ScavaProjectSearchUsageEvent;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexType;

@BasedOn(ScavaProjectSearchUsageEvent.class)
public class ScavaProjectSearchUsageBasicMetric implements IBasicMetric{

	private static final String DESCRIPTION = "Level of using CROSSMINER project search function";
	private static final String NAME = "Project usage";
	
	@Override
	public Map<String, Double> process(GraphTraversal<Vertex, Vertex> vertices) {
		
		Map<String, Double> valueMap = new HashMap<>();

		Long count = vertices.asAdmin().clone().has("VertexType", VertexType.SCAVA_PROJECT_SEARCH_USAGE_EVENT).count().next();
		valueMap.put("general", (double) count);

		return valueMap;
	}

	@Override
	public String getDiscription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
}
