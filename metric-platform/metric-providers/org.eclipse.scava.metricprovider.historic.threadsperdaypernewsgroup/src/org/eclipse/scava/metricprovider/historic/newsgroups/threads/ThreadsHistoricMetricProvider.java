/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.historic.newsgroups.threads;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.scava.metricprovider.historic.newsgroups.threads.model.DailyNewsgroupData;
import org.eclipse.scava.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.ActiveUsersTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.model.NewsgroupsActiveUsersTransMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.model.User;
import org.eclipse.scava.metricprovider.trans.newsgroups.threads.ThreadsTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.newsgroups.threads.model.NewsgroupData;
import org.eclipse.scava.metricprovider.trans.newsgroups.threads.model.NewsgroupsThreadsTransMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.threads.model.ThreadData;
import org.eclipse.scava.platform.AbstractHistoricalMetricProvider;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.MetricProviderContext;
import org.eclipse.scava.repository.model.CommunicationChannel;
import org.eclipse.scava.repository.model.Project;
import org.eclipse.scava.repository.model.cc.eclipseforums.EclipseForum;
import org.eclipse.scava.repository.model.cc.irc.Irc;
import org.eclipse.scava.repository.model.cc.mbox.Mbox;
import org.eclipse.scava.repository.model.cc.nntp.NntpNewsGroup;
import org.eclipse.scava.repository.model.cc.sympa.SympaMailingList;
import org.eclipse.scava.repository.model.sourceforge.Discussion;

import com.googlecode.pongo.runtime.Pongo;

public class ThreadsHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = ThreadsHistoricMetricProvider.class.getCanonicalName();

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
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
			if (communicationChannel instanceof Discussion) return true;
			if (communicationChannel instanceof EclipseForum) return true;
			if (communicationChannel instanceof SympaMailingList) return true;
			if (communicationChannel instanceof Irc) return true;
			if (communicationChannel instanceof Mbox) return true;
		}
		return false;
	}

	@Override
	public Pongo measure(Project project) {

		if (uses.size()!=2) {
			System.err.println("Metric: threadsperdaypernewsgroup failed to retrieve " + 
								"the transient metric it needs!");
			System.exit(-1);
		}

		NewsgroupsThreadsTransMetric usedThreads = 
				((ThreadsTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));
		NewsgroupsActiveUsersTransMetric usedUsers = 
				((ActiveUsersTransMetricProvider)uses.get(1)).adapt(context.getProjectDB(project));
		

		HashSet<String> threadIdSet = new HashSet<String>();
		for (ThreadData thread: usedThreads.getThreads())
			threadIdSet.add(thread.getThreadId());
		
		NewsgroupsThreadsHistoricMetric dailyThreads = new NewsgroupsThreadsHistoricMetric();
		

		int sumOfThreads = 0;
		int numberOfArticles = 0,
			numberOfRequests = 0,
			numberOfReplies = 0;

		for (NewsgroupData newsgroups: usedThreads.getNewsgroups()) {
			sumOfThreads += newsgroups.getThreads();
			DailyNewsgroupData newsgroupData = new DailyNewsgroupData();
			newsgroupData.setNewsgroupName(newsgroups.getNewsgroupName());//uses ossmeterID
			newsgroupData.setNumberOfThreads(newsgroups.getThreads());
			dailyThreads.getNewsgroups().add(newsgroupData);
		}

		for (User user: usedUsers.getUsers()) {
			numberOfArticles += user.getArticles();
			numberOfReplies += user.getReplies();
			numberOfRequests += user.getRequests();
		}
		
		dailyThreads.setNumberOfThreads(sumOfThreads);

		float avgArticles = 0,
			  avgReplies = 0,
			  avgRequests = 0;
		
		if (threadIdSet.size()>0) {
			avgArticles = ((float) numberOfArticles) / threadIdSet.size();
			avgReplies = ((float) numberOfReplies) / threadIdSet.size();
			avgRequests = ((float) numberOfRequests) / threadIdSet.size();
		}
		
		dailyThreads.setAverageArticlesPerThread(avgArticles);
		dailyThreads.setAverageRepliesPerThread(avgReplies);
		dailyThreads.setAverageRequestsPerThread(avgRequests);
		
		avgArticles = 0;
		avgReplies = 0;
		avgRequests = 0;

		if (usedUsers.getUsers().size()>0) {
			avgArticles = ((float) numberOfArticles) / usedUsers.getUsers().size();
			avgReplies = ((float) numberOfReplies) / usedUsers.getUsers().size();
			avgRequests = ((float) numberOfRequests) / usedUsers.getUsers().size();
		}

		dailyThreads.setAverageArticlesPerUser(avgArticles);
		dailyThreads.setAverageRepliesPerUser(avgReplies);
		dailyThreads.setAverageRequestsPerUser(avgRequests);

		return dailyThreads;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ThreadsTransMetricProvider.class.getCanonicalName(),
							 ActiveUsersTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "historic.newsgroups.threads";
	}

	@Override
	public String getFriendlyName() {
		return "Number of threads per day per newsgroup";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of threads per day for each newsgroup separately. "
				+ "The metric also computes average values for articles per thread, requests per thread, "
				+ "replies per thread, articles per user, requests per user and replies per user.";
	}
}
