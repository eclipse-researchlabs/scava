/*******************************************************************************
 * Copyright (c) 2018 University of York
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform;

import org.eclipse.scava.platform.delta.ProjectDelta;
import org.eclipse.scava.repository.model.Project;

import com.googlecode.pongo.runtime.PongoDB;
import com.mongodb.DB;

public interface ITransientMetricProvider<T extends PongoDB> extends IMetricProvider {
	
	/**
	 * 
	 * @param db
	 * @return
	 */
	public T adapt(DB db);
	
	public void measure(Project project, ProjectDelta delta, T db);
	
}
