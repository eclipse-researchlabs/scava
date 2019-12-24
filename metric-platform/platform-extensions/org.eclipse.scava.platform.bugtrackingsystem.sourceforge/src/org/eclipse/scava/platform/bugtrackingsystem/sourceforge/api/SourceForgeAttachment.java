/*******************************************************************************
 * Copyright (c) 2018 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.bugtrackingsystem.sourceforge.api;

import java.io.Serializable;

public class SourceForgeAttachment implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4082845722976222497L;
	
	private String url;
    private long bytes;
    
    public String getUrl() {
        return url;
    }
    
    public long getBytes() {
        return bytes;
    }
}
