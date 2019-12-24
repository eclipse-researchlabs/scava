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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.scava.platform.util.ExtensionPointHelper;

import com.googlecode.pongo.runtime.PongoCollection;

public class ExtensionPointMetricProviderManager implements IMetricProviderManager {
	
	protected List<IMetricProvider> metricProviders;
	protected final String metricProviderExtensionPointId = "org.eclipse.scava.platform.metricprovider";
	protected final String metricProviderManagerExtensionPointId = "org.eclipse.scava.platform.managers.metricprovider";

	protected List<? extends PongoCollection> metricCollections = null;
	
	public List<IMetricProvider> getMetricProviders() {
		if (metricProviders == null) { // TODO: This needs some better logic. This will not pick up any MPs added during runtime.
			metricProviders = new ArrayList<IMetricProvider>();
			
			// Load the standard MP extensions
			for(IConfigurationElement configurationElement : ExtensionPointHelper.getConfigurationElementsForExtensionPoint(metricProviderExtensionPointId)){
				try {
					metricProviders.add((IMetricProvider) configurationElement.createExecutableExtension("provider"));
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			// Load extension points via custom managers 
//			System.err.println("Custom Extension Point managers: ");
			for(IConfigurationElement configurationElement : ExtensionPointHelper.getConfigurationElementsForExtensionPoint(metricProviderManagerExtensionPointId)){
				try {
					IMetricProviderManager impm = (IMetricProviderManager) configurationElement.createExecutableExtension("manager");
//					System.err.println("\t" + impm.getClass().toString());
					metricProviders.addAll(impm.getMetricProviders());
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
//			System.err.println("Registered metric providers: ");
//			for (IMetricProvider imp : metricProviders) {
//				System.err.println("\t"+ imp.getIdentifier());
//			}
		}
		
		return metricProviders;
	}	
}
