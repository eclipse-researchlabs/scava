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

import org.eclipse.scava.plugin.Activator;
import org.eclipse.scava.plugin.usermonitoring.event.IEventListener;

public class ScavaEventListener implements IScavaEventListener, IEventListener {

	public ScavaEventListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void scavaApiAndQnARecommendationUsage() {
		Activator.getDefault().getEventBus().post(new ScavaApiAndQnARecommendationUsageEvent());

	}

	@Override
	public void scavaCodeRecommendationUsage() {
		Activator.getDefault().getEventBus().post(new ScavaCodeRecommendationUsageEvent());

	}

	@Override
	public void scavaLibrarySearchUsageEvent() {
		Activator.getDefault().getEventBus().post(new ScavaLibrarySearchUsageEvent());

	}
	
	@Override
	public void scavaProjectSearchUsageEvent() {
		Activator.getDefault().getEventBus().post(new ScavaProjectSearchUsageEvent());

	}
	
	@Override
	public void scavaSearchLibraryUpdateUsageEvent() {
		Activator.getDefault().getEventBus().post(new ScavaSearchLibraryUpdateUsageEvent());

	}

}
