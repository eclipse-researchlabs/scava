/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.scava.plugin.main;

import org.eclipse.scava.plugin.usermonitoring.UserMonitor;

public interface IMainController {

	void startUserMonitoring();

	UserMonitor getUsermonitor();

	static class OpenArtifactSearchEvent {

	}

	static class OpenLibrarySearchEvent {

	}

	static class RequestCodeRecommendation {

	}

	static class RequestApiDocumentation {

	}
}
