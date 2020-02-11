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
package org.eclipse.scava.plugin.usermonitoring.event.events.eclipse;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.eclipse.scava.plugin.usermonitoring.event.IDisableableEvent;
import org.eclipse.scava.plugin.usermonitoring.event.IForcedFlush;
import org.eclipse.scava.plugin.usermonitoring.event.events.Event;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexAllocator;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexProperty;
import org.eclipse.scava.plugin.usermonitoring.gremlin.database.VertexType;

public class EclipseCloseEvent extends Event implements IForcedFlush{

	public EclipseCloseEvent() {

	}

	@Override
	public String toString() {
		return "Date: " + date + " VertexType: " + VertexType.ECLIPSE_CLOSE_EVENT;
	}

	@Override
	public Vertex toNode(VertexAllocator allocator) {

		Vertex eventVertex = allocator.insertVertex("event", new VertexProperty("VertexType", VertexType.ECLIPSE_CLOSE_EVENT),
				new VertexProperty("TimeStamp", date));

		return eventVertex;
	}

}
