/*******************************************************************************
 * Copyright (c) 2018 Softeam
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scava.platform.AbstractFactoidMetricProvider;
import org.eclipse.scava.platform.IHistoricalMetricProvider;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.Platform;
import org.eclipse.scava.platform.analysis.data.model.MetricProvider;
import org.eclipse.scava.platform.analysis.data.model.dto.MetricProviderDTO;
import org.eclipse.scava.platform.analysis.data.types.MetricProviderKind;
import org.eclipse.scava.platform.visualisation.MetricVisualisation;
import org.eclipse.scava.platform.visualisation.MetricVisualisationExtensionPointManager;

public class MetricProviderInitialiser {

	private Platform platform;

	public MetricProviderInitialiser(Platform platform) {
		this.platform = platform;
	}

	public List<MetricProviderDTO> loadMetricProviders() {
		Map<String, MetricProviderDTO> metricsProviders = new HashMap<String, MetricProviderDTO>();

		List<IMetricProvider> platformProvider = this.platform.getMetricProviderManager().getMetricProviders();

		// Create metric providers
		for (IMetricProvider provider : platformProvider) {
			MetricProviderDTO providerData = new MetricProviderDTO();
			if (provider instanceof AbstractFactoidMetricProvider) {
				providerData = this.platform.getAnalysisRepositoryManager().getMetricProviderService().registreMetricProvider(provider.getIdentifier(),
						provider.getFriendlyName(),MetricProviderKind.FACTOID.name() ,provider.getSummaryInformation(), new ArrayList<String>());
			}else if (provider instanceof IHistoricalMetricProvider) {
				List<String> collections = new ArrayList<>();
				collections.add(((IHistoricalMetricProvider) provider).getCollectionName());
				providerData = this.platform.getAnalysisRepositoryManager().getMetricProviderService().registreMetricProvider(provider.getIdentifier(),
						provider.getFriendlyName(),MetricProviderKind.HISTORIC.name(), provider.getSummaryInformation(), collections);
			} else {
				providerData = this.platform.getAnalysisRepositoryManager().getMetricProviderService().registreMetricProvider(provider.getIdentifier(),
						provider.getFriendlyName(),MetricProviderKind.TRANSIENT.name(), provider.getSummaryInformation(), new ArrayList<String>());
			}
			metricsProviders.put(provider.getIdentifier(), providerData);
		}

		// Resolve Dependencies
		List<MetricProviderDTO> metricProviders = new ArrayList<MetricProviderDTO>();
		for (IMetricProvider provider : platformProvider) {
			MetricProviderDTO metricProvider = metricsProviders.get(provider.getIdentifier());
			// Check if metricsProvider has visualization
			MetricVisualisationExtensionPointManager manager = MetricVisualisationExtensionPointManager.getInstance();
			Map<String, MetricVisualisation> mvs = manager.getRegisteredVisualisations();
			boolean found = false;
			for (MetricVisualisation mv : mvs.values()) {
				if (provider.getIdentifier().equals(mv.getMetricId())) {
					metricProvider.setHasVisualisation(true);
					found = true;
					break;
				}
			}
			if (!found) {
				metricProvider.setHasVisualisation(false);
			}
			// Fetch metric-provider dependencies
			if (provider.getIdentifiersOfUses() != null) {
				for (String dependencyId : provider.getIdentifiersOfUses()) {
					MetricProviderDTO metricDependency = metricsProviders.get(dependencyId);
					if (metricDependency != null) {
						metricProvider.getDependOf().add(metricDependency);
					}
				}
			}
			metricProviders.add(metricProvider);
			metricProvider = null;
		}
		return metricProviders;
	}

}
