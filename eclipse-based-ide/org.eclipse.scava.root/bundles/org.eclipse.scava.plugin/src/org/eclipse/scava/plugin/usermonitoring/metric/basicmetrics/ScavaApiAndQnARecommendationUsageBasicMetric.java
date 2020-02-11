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

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.scava.plugin.usermonitoring.event.events.scava.ScavaApiAndQnARecommendationUsageEvent;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexType;

@BasedOn(ScavaApiAndQnARecommendationUsageEvent.class)
public class ScavaApiAndQnARecommendationUsageBasicMetric implements IBasicMetric{

	
	private static final String DESCRIPTION = "Level of using CROSSMINER API and Q&A post recommendation function";
	private static final String NAME = "API and Q&A post recommendation usage";
	
	@Override
	public Map<String, Double> process(GraphTraversal<Vertex, Vertex> vertices) {
		
		Map<String, Double> valueMap = new HashMap<>();

		Long count = vertices.asAdmin().clone().has("VertexType", VertexType.SCAVA_API_AND_QNA_USAGE_EVENT).count().next();
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
