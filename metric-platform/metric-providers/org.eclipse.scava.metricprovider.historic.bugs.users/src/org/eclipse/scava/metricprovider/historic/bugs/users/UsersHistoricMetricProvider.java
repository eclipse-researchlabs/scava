/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.historic.bugs.users;

import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.historic.bugs.users.model.BugsUsersHistoricMetric;
import org.eclipse.scava.metricprovider.historic.bugs.users.model.DailyBugTrackingData;
import org.eclipse.scava.metricprovider.trans.bugs.activeusers.ActiveUsersTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.bugs.activeusers.model.BugData;
import org.eclipse.scava.metricprovider.trans.bugs.activeusers.model.BugsActiveUsersTransMetric;
import org.eclipse.scava.platform.AbstractHistoricalMetricProvider;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.MetricProviderContext;
import org.eclipse.scava.repository.model.Project;

import com.googlecode.pongo.runtime.Pongo;

public class UsersHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = UsersHistoricMetricProvider.class.getCanonicalName();

	protected MetricProviderContext context;
	
	/**
	 * List of MPs that are used by this MP. These are MPs who have specified that 
	 * they 'provide' data for this MP.
	 */
	protected List<IMetricProvider> uses;
	
	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}
		
	@Override
	public boolean appliesTo(Project project) {
	    return !project.getBugTrackingSystems().isEmpty();	   
	}

	@Override
	public Pongo measure(Project project) {
		BugsUsersHistoricMetric users = new BugsUsersHistoricMetric();
		if (uses.size()==1) {
			BugsActiveUsersTransMetric activeUsers = ((ActiveUsersTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
			int numberOfUsers = 0,
				numberOfActiveUsers = 0,
				numberOfInactiveUsers = 0;
			for (BugData bug: activeUsers.getBugs()) {
				if ((bug.getUsers() > 0) || (bug.getActiveUsers() > 0) || (bug.getInactiveUsers() > 0)) {
					DailyBugTrackingData dailyNewsgroupData = new DailyBugTrackingData();
					dailyNewsgroupData.setBugTrackerId(bug.getBugTrackerId());
					if (bug.getUsers() > 0)
						numberOfUsers += bug.getUsers();
					if (bug.getActiveUsers() > 0)
						numberOfActiveUsers += bug.getActiveUsers();
					if (bug.getInactiveUsers() > 0)
						numberOfInactiveUsers += bug.getInactiveUsers();
					dailyNewsgroupData.setNumberOfUsers(bug.getUsers());
					dailyNewsgroupData.setNumberOfActiveUsers(bug.getActiveUsers());
					dailyNewsgroupData.setNumberOfInactiveUsers(bug.getInactiveUsers());
					users.getBugTrackers().add(dailyNewsgroupData);
				}
			}
			if (numberOfUsers > 0)
			{
				users.setNumberOfUsers(numberOfUsers);
				users.setNumberOfActiveUsers(numberOfActiveUsers);
				users.setNumberOfInactiveUsers(numberOfInactiveUsers);
			}
		}
		return users;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ActiveUsersTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "historic.bugs.users";
	}

	@Override
	public String getFriendlyName() {
		return "Number of users, active and inactive per day per bug tracker";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of users, active and inactive users " +
				"per day for each bug tracker separately.";
	}

}
