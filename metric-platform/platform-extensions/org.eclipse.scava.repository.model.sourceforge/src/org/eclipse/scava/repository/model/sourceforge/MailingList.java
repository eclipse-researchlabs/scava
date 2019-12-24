/*******************************************************************************
 * Copyright (c) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.repository.model.sourceforge;


public class MailingList extends org.eclipse.scava.repository.model.CommunicationChannel {
	
	
	
	public MailingList() { 
		super();
		super.setSuperTypes("org.eclipse.scava.repository.model.sourceforge.CommunicationChannel");
	}
	
	
	@Override
	public String getCommunicationChannelType() {
		return "SourceForgeMailingList";
	}

	@Override
	public String getInstanceId() {
		return "";
	}
	
	@Override
	public boolean needsLocalStorage() {
		return false;
	}
	
	
}
