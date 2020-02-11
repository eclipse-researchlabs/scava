/*********************************************************************
<<<<<<< HEAD
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

package org.eclipse.scava.plugin.eclipse.startup;

import org.eclipse.scava.plugin.Activator;
import org.eclipse.scava.plugin.main.EclipseInterfaceEvent;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		Activator.getDefault().getEventBus().post(new EclipseInterfaceEvent.WorkbenchStartup(null));
	}

}
