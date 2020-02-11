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
package org.eclipse.scava.plugin.projectsearch;

import org.eclipse.scava.plugin.knowledgebase.access.KnowledgeBaseAccess;
import org.eclipse.scava.plugin.mvc.model.Model;

public class ProjectSearchModel extends Model {
	private final KnowledgeBaseAccess knowledgeBaseAccess;

	public ProjectSearchModel(KnowledgeBaseAccess knowledgeBaseAccess) {
		super();
		this.knowledgeBaseAccess = knowledgeBaseAccess;
	}
	
	public KnowledgeBaseAccess getKnowledgeBaseAccess() {
		return knowledgeBaseAccess;
	}
}
