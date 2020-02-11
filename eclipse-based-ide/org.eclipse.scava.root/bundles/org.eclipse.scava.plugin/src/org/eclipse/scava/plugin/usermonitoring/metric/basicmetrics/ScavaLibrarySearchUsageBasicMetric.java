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
*    Zsolt J�nos Szamosv�lgyi
*    Endre Tam�s V�radi
*    Gerg� Balogh
**********************************************************************/
package org.eclipse.scava.plugin.usermonitoring.metric.basicmetrics;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.scava.plugin.usermonitoring.event.events.scava.ScavaCodeRecommendationUsageEvent;
import org.eclipse.scava.plugin.usermonitoring.event.events.scava.ScavaLibrarySearchUsageEvent;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexType;

@BasedOn(ScavaLibrarySearchUsageEvent.class)
public class ScavaLibrarySearchUsageBasicMetric implements IBasicMetric{
	
	private static final String DESCRIPTION = "Level of using CROSSMINER library search function";
	private static final String NAME = "Library seach";
	
	@Override
	public Map<String, Double> process(GraphTraversal<Vertex, Vertex> vertices) {
		
		Map<String, Double> valueMap = new HashMap<>();

		Long count = vertices.asAdmin().clone().has("VertexType", VertexType.SCAVA_LIBRARY_SEARCH_USAGE_EVENT).count().next();
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
