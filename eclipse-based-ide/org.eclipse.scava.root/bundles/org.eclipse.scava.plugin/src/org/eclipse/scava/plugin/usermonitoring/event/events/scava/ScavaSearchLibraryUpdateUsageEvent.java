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
package org.eclipse.scava.plugin.usermonitoring.event.events.scava;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.scava.plugin.usermonitoring.event.IDisableableEvent;
import org.eclipse.scava.plugin.usermonitoring.event.events.Event;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexAllocator;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexProperty;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexType;
import org.eclipse.scava.plugin.usermonitoring.metric.metrics.MetricException;

public class ScavaSearchLibraryUpdateUsageEvent extends Event implements IDisableableEvent{
	
	
	public ScavaSearchLibraryUpdateUsageEvent() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Date: " + date + " VertexType: " + VertexType.SCAVA_LIBRARY_UPDATE_USAGE_EVENT;
	}
	
	
	@Override
	public Vertex toNode(VertexAllocator allocator) throws MetricException {
		Vertex eventVertex = allocator.insertVertex("event", new VertexProperty("VertexType", VertexType.SCAVA_LIBRARY_UPDATE_USAGE_EVENT),
				new VertexProperty("TimeStamp", date));

		return eventVertex;
	}

}
