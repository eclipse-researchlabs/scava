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
package org.eclipse.scava.plugin.usermonitoring.event.events.scava;

import org.eclipse.scava.plugin.Activator;
import org.eclipse.scava.plugin.usermonitoring.event.IEventListener;

public class ScavaEventListener implements IScavaEventListener, IEventListener {

	public ScavaEventListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void scavaLibraryUsage() {
		Activator.getDefault().getEventBus().post(new ScavaLibraryUsageEvent());

	}

	@Override
	public void scavaSearchSucces() {
		Activator.getDefault().getEventBus().post(new ScavaSearchSuccesEvent());

	}

	@Override
	public void scavaSearchUsage() {
		Activator.getDefault().getEventBus().post(new ScavaSearchUsageEvent());

	}

}
