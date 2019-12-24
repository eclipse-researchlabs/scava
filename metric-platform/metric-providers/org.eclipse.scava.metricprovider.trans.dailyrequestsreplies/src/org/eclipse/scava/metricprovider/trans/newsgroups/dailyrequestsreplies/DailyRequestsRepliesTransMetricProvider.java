/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.trans.newsgroups.dailyrequestsreplies;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.eclipse.scava.metricprovider.trans.newsgroups.dailyrequestsreplies.model.DayArticles;
import org.eclipse.scava.metricprovider.trans.newsgroups.dailyrequestsreplies.model.NewsgroupsDailyRequestsRepliesTransMetric;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.RequestReplyClassificationTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.model.NewsgroupArticles;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.model.RequestReplyClassificationTransMetric;
import org.eclipse.scava.platform.Date;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.ITransientMetricProvider;
import org.eclipse.scava.platform.MetricProviderContext;
import org.eclipse.scava.platform.delta.ProjectDelta;
import org.eclipse.scava.platform.delta.communicationchannel.CommunicationChannelArticle;
import org.eclipse.scava.platform.delta.communicationchannel.CommunicationChannelDelta;
import org.eclipse.scava.platform.delta.communicationchannel.CommunicationChannelProjectDelta;
import org.eclipse.scava.platform.delta.communicationchannel.PlatformCommunicationChannelManager;
import org.eclipse.scava.repository.model.CommunicationChannel;
import org.eclipse.scava.repository.model.Project;
import org.eclipse.scava.repository.model.cc.eclipseforums.EclipseForum;
import org.eclipse.scava.repository.model.cc.irc.Irc;
import org.eclipse.scava.repository.model.cc.mbox.Mbox;
import org.eclipse.scava.repository.model.cc.nntp.NntpNewsGroup;
import org.eclipse.scava.repository.model.cc.sympa.SympaMailingList;
import org.eclipse.scava.repository.model.sourceforge.Discussion;

import com.mongodb.DB;

public class DailyRequestsRepliesTransMetricProvider implements ITransientMetricProvider<
NewsgroupsDailyRequestsRepliesTransMetric>{

	protected PlatformCommunicationChannelManager communicationChannelManager;

	protected MetricProviderContext context;
	
	protected List<IMetricProvider> uses;

	@Override
	public String getIdentifier() {
		return DailyRequestsRepliesTransMetricProvider.class.getCanonicalName();
	}

	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup) return true;
			if (communicationChannel instanceof EclipseForum) return true;
			if (communicationChannel instanceof Discussion) return true;
			if (communicationChannel instanceof SympaMailingList) return true;
			if (communicationChannel instanceof Irc) return true;
			if (communicationChannel instanceof Mbox) return true;
		}
		return false;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(RequestReplyClassificationTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
		this.communicationChannelManager = context.getPlatformCommunicationChannelManager();
	}

	@Override
	public NewsgroupsDailyRequestsRepliesTransMetric adapt(DB db) {
		return new NewsgroupsDailyRequestsRepliesTransMetric(db);
	}

	@Override
	public void measure(Project project, ProjectDelta projectDelta, NewsgroupsDailyRequestsRepliesTransMetric db) {
		
		String[] daysOfWeek = new String[]{"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

		for (String d : daysOfWeek) {
			DayArticles dayArticles = db.getDayArticles().findOneByName(d);
			if (dayArticles == null) {
				dayArticles = new DayArticles();
				dayArticles.setName(d);
				dayArticles.setNumberOfArticles(0);
				dayArticles.setNumberOfRequests(0);
				dayArticles.setNumberOfReplies(0);
				db.getDayArticles().add(dayArticles);
				db.sync();
			}
		}

		CommunicationChannelProjectDelta delta = projectDelta.getCommunicationChannelDelta();
		
		RequestReplyClassificationTransMetric usedClassifier = 
				((RequestReplyClassificationTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		for ( CommunicationChannelDelta communicationChannelDelta: delta.getCommunicationChannelSystemDeltas()) {
			CommunicationChannel communicationChannel = communicationChannelDelta.getCommunicationChannel();
			
			String ossmeterID = communicationChannel.getOSSMeterId();
			
			List<CommunicationChannelArticle> articles = communicationChannelDelta.getArticles();
			for (CommunicationChannelArticle article: articles) {

				Date date = new Date(article.getDate());
				Calendar cal = Calendar.getInstance();
				cal.setTime(date.toJavaDate());
				int dow = cal.get(Calendar.DAY_OF_WEEK) - 1;
				String dayName = daysOfWeek[dow];

				DayArticles dayArticles = db.getDayArticles().findOneByName(dayName);
				dayArticles.setNumberOfArticles(dayArticles.getNumberOfArticles()+1);
				String requestReplyClass = 
						getRequestReplyClass(usedClassifier, ossmeterID, article);
				if (requestReplyClass.equals("__label__Request"))
					dayArticles.setNumberOfRequests(dayArticles.getNumberOfRequests()+1);
				else if (requestReplyClass.equals("__label__Reply"))
					dayArticles.setNumberOfReplies(dayArticles.getNumberOfReplies()+1);
				db.sync();
			}
		}

		int sumOfArticles = 0,
			sumOfRequests = 0,
			sumOfReplies = 0;

		for (DayArticles dayArticles: db.getDayArticles()) {
			sumOfArticles += dayArticles.getNumberOfArticles();
			sumOfRequests += dayArticles.getNumberOfRequests();
			sumOfReplies += dayArticles.getNumberOfReplies();
		}

		for (DayArticles dayArticles: db.getDayArticles()) {
			
			float percentageOfComments = (float) 0.0;
			if (sumOfArticles > 0)
				percentageOfComments = ( (float) 100 * dayArticles.getNumberOfArticles() ) / sumOfArticles;
			dayArticles.setPercentageOfArticles(percentageOfComments);
			
			float percentageOfRequests = (float) 0.0;
			if (sumOfRequests > 0)
				percentageOfRequests = ( (float) 100 * dayArticles.getNumberOfRequests() ) / sumOfRequests;
			dayArticles.setPercentageOfRequests(percentageOfRequests);
			
			float percentageOfReplies = (float) 0.0;
			if (sumOfReplies > 0)
				percentageOfReplies = ( (float) 100 * dayArticles.getNumberOfReplies() ) / sumOfReplies;
			dayArticles.setPercentageOfReplies(percentageOfReplies);
		}
		
		db.sync();

	}

	private String getRequestReplyClass(RequestReplyClassificationTransMetric usedClassifier, 
							String ossmeterId, CommunicationChannelArticle article) {
		Iterable<NewsgroupArticles> newsgroupArticlesIt = usedClassifier.getNewsgroupArticles().
				find(NewsgroupArticles.NEWSGROUPNAME.eq(ossmeterId), 
						NewsgroupArticles.ARTICLEID.eq(article.getArticleId()));
		NewsgroupArticles newsgroupArticleData = null;
		for (NewsgroupArticles art:  newsgroupArticlesIt) {
			newsgroupArticleData = art;
		}
		if (newsgroupArticleData == null) {
			System.err.println("Newsgroups - Daily Requests Replies -\t" + 
					"there is no classification for article: " + article.getArticleId() +
					"\t belonging to: " + article.getCommunicationChannel().getUrl());
//			System.exit(-1);
		} else
			return newsgroupArticleData.getClassificationResult();
		return "";
	}

	@Override
	public String getShortIdentifier() {
		return "trans.newsgroups.dailyrequestsreplies";
	}

	@Override
	public String getFriendlyName() {
		return "Number of articles, requests and replies per day";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of articles, including those regarded "
				+ "as requests and replies for each day of the week, per newsgroup.";
	}

}
