/*******************************************************************************
 * Copyright (c) 2018 Edge Hill University
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.repository.model.mantis;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class MantisReproducability extends Pongo {
	
	
	
	public MantisReproducability() { 
		super();
		_ID.setOwningType("org.eclipse.scava.repository.model.mantis.MantisReproducability");
		NAME.setOwningType("org.eclipse.scava.repository.model.mantis.MantisReproducability");
		LABEL.setOwningType("org.eclipse.scava.repository.model.mantis.MantisReproducability");
	}
	
	public static StringQueryProducer _ID = new StringQueryProducer("_id"); 
	public static StringQueryProducer NAME = new StringQueryProducer("name"); 
	public static StringQueryProducer LABEL = new StringQueryProducer("label"); 
	
	
	public String get_id() {
		return parseString(dbObject.get("_id")+"", "");
	}
	
	public MantisReproducability set_id(String _id) {
		dbObject.put("_id", _id);
		notifyChanged();
		return this;
	}
	public String getName() {
		return parseString(dbObject.get("name")+"", "");
	}
	
	public MantisReproducability setName(String name) {
		dbObject.put("name", name);
		notifyChanged();
		return this;
	}
	public String getLabel() {
		return parseString(dbObject.get("label")+"", "");
	}
	
	public MantisReproducability setLabel(String label) {
		dbObject.put("label", label);
		notifyChanged();
		return this;
	}
	
	
	
	
}