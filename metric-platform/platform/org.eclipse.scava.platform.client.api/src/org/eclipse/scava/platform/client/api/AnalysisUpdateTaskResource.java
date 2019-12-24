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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.scava.platform.Configuration;
import org.eclipse.scava.platform.Platform;
import org.eclipse.scava.platform.analysis.AnalysisTaskService;
import org.eclipse.scava.platform.analysis.data.model.AnalysisTask;
import org.eclipse.scava.platform.analysis.data.types.AnalysisExecutionMode;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.Mongo;

public class AnalysisUpdateTaskResource extends ServerResource {

	@Put
	public Representation updateAnalysisTask(Representation entity) {
		try {
			Platform platform = Platform.getInstance();
			AnalysisTaskService service = platform.getAnalysisRepositoryManager().getTaskService();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(entity.getText());
	
			String oldAnalysisTaskId = jsonNode.get("oldAnalysisTaskId").toString().replace("\"", "");
			String analysisTaskId = jsonNode.get("analysisTaskId").toString().replace("\"", "");
			String taskLabel = jsonNode.get("label").toString().replace("\"", "");
			String taskType = jsonNode.get("type").toString().replace("\"", "");
	
			Date taskStartDate = new SimpleDateFormat("dd/MM/yyyy")
					.parse(jsonNode.get("startDate").toString().replace("\"", ""));
	
			AnalysisTask newTask = new AnalysisTask();
			newTask.setAnalysisTaskId(analysisTaskId);
			newTask.setLabel(taskLabel);
			newTask.setStartDate(taskStartDate);
	
			if (taskType.equals(AnalysisExecutionMode.SINGLE_EXECUTION.name())) {
				newTask.setType(AnalysisExecutionMode.SINGLE_EXECUTION.name());
				Date taskEndDate = new SimpleDateFormat("dd/MM/yyyy")
						.parse(jsonNode.get("endDate").toString().replace("\"", ""));
				newTask.setEndDate(taskEndDate);
			} else if (taskType.equals(AnalysisExecutionMode.CONTINUOUS_MONITORING.name())) {
				newTask.setType(AnalysisExecutionMode.CONTINUOUS_MONITORING.name());
			}
	
			List<String> metricsProviders = new ArrayList<>();
			for (JsonNode metricProvider : (ArrayNode) jsonNode.get("metricProviders")) {
				metricsProviders.add(metricProvider.toString().replace("\"", ""));
			}
			
			service.validateMetricProviders(metricsProviders);
	
			AnalysisTask updatedTask = service.updateAnalysisTask(oldAnalysisTaskId, newTask, metricsProviders);
	
			StringRepresentation rep = new StringRepresentation(updatedTask.getDbObject().toString());
			rep.setMediaType(MediaType.APPLICATION_JSON);
			getResponse().setStatus(Status.SUCCESS_CREATED);
			return rep;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			StringRepresentation rep = new StringRepresentation("");
			rep.setMediaType(MediaType.APPLICATION_JSON);
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return rep;
		}
	}
}
