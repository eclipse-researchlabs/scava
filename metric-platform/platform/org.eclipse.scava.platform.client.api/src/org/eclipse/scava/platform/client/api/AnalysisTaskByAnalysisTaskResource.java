/*******************************************************************************
 * Copyright (c) 2018 Softeam
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scava.platform.analysis.AnalysisTaskService;
import org.eclipse.scava.platform.analysis.data.model.AnalysisTask;
import org.eclipse.scava.platform.analysis.data.model.MetricExecution;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class AnalysisTaskByAnalysisTaskResource extends AbstractApiResource {

	@Override
	public Representation doRepresent() {
		AnalysisTaskService service = platform.getAnalysisRepositoryManager().getTaskService();
		
		String analysisTaskId = getQueryValue("analysisTaskId");

		AnalysisTask analysisTask = service.getTaskByAnalysisTaskId(analysisTaskId);
		analysisTask.getDbObject().put("projectId", analysisTask.getProject().getProjectId());

		List<Object> metricExecutions = new ArrayList<>();
		for (MetricExecution metric : analysisTask.getMetricExecutions()) {
			Map<String, String> newMetric = new HashMap<>();
			newMetric.put("metricProviderId", metric.getDbObject().get("metricProviderId").toString());
			newMetric.put("projectId", metric.getDbObject().get("projectId").toString());
			newMetric.put("lastExecutionDate", metric.getDbObject().get("lastExecutionDate").toString());
			metricExecutions.add(newMetric);
		}
		analysisTask.getDbObject().put("metricExecutions", metricExecutions);

		StringRepresentation rep = new StringRepresentation(analysisTask.getDbObject().toString());
		rep.setMediaType(MediaType.APPLICATION_JSON);
		getResponse().setStatus(Status.SUCCESS_OK);
		return rep;
	}

}
