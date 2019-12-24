/*******************************************************************************
 * Copyright (c) 2018 University of York
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.communicationchannel.sourceforge.api;

public class SourceForgeAttachment {
    private String url;
    private long bytes;
    
    public String getUrl() {
        return url;
    }
    
    public long getBytes() {
        return bytes;
    }
}
