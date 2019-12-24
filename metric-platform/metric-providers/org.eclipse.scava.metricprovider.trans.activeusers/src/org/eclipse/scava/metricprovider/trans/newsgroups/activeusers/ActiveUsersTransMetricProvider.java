/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.trans.newsgroups.activeusers;

import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.model.NewsgroupData;
import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.model.NewsgroupsActiveUsersTransMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.activeusers.model.User;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.RequestReplyClassificationTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.model.NewsgroupArticles;
import org.eclipse.scava.metricprovider.trans.requestreplyclassification.model.RequestReplyClassificationTransMetric;
import org.eclipse.scava.platform.Date;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.ITransientMetricProvider;
import org.eclipse.scava.platform.MetricProviderContext;
import org.eclipse.scava.platform.communicationchannel.nntp.NntpUtil;
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

public class ActiveUsersTransMetricProvider implements ITransientMetricProvider<NewsgroupsActiveUsersTransMetric>{

	protected final int STEP = 15;
	
	protected PlatformCommunicationChannelManager communicationChannelManager;

	protected MetricProviderContext context;
	
	protected List<IMetricProvider> uses;

	@Override
	public String getIdentifier() {
		return ActiveUsersTransMetricProvider.class.getCanonicalName();
	}

	@Override
	public boolean appliesTo(Project project) {
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels()) {
			if (communicationChannel instanceof NntpNewsGroup ) return true;
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
	public NewsgroupsActiveUsersTransMetric adapt(DB db) {
		return new NewsgroupsActiveUsersTransMetric(db);
	}

	@Override
	public void measure(Project project, ProjectDelta projectDelta, NewsgroupsActiveUsersTransMetric db) {
		CommunicationChannelProjectDelta delta = projectDelta.getCommunicationChannelDelta();
		
		RequestReplyClassificationTransMetric usedClassifier = 
				((RequestReplyClassificationTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		for ( CommunicationChannelDelta communicationChannelDelta: delta.getCommunicationChannelSystemDeltas()) {
			CommunicationChannel communicationChannel = communicationChannelDelta.getCommunicationChannel();
			String ossmeterID = communicationChannel.getOSSMeterId();
		
			
			NewsgroupData newsgroupData = db.getNewsgroups().findOneByNewsgroupName(communicationChannel.getOSSMeterId());
			if (newsgroupData == null) {
				newsgroupData = new NewsgroupData();
				newsgroupData.setNewsgroupName(ossmeterID);
				newsgroupData.setPreviousUsers(0);
				newsgroupData.setDays(1);
				db.getNewsgroups().add(newsgroupData);
			} else {
				newsgroupData.setPreviousUsers(newsgroupData.getUsers());
				newsgroupData.setDays(newsgroupData.getDays()+1);
			}
			
			List<CommunicationChannelArticle> articles = communicationChannelDelta.getArticles();
			for (CommunicationChannelArticle article: articles) {
				Iterable<User> usersIt = db.getUsers().
						find(User.NEWSGROUPNAME.eq(ossmeterID), 
								User.USERID.eq(article.getUser()));
				User user = null;
				for (User u:  usersIt) {
					user = u;
				}
				if (user == null) {
					user = new User();
					user.setNewsgroupName(ossmeterID);
					user.setUserId(article.getUser());
					user.setLastActivityDate(article.getDate().toString());
					user.setArticles(1);
					String requestReplyClass = getRequestReplyClass(usedClassifier, ossmeterID, article);
					if (requestReplyClass.equals("__label__Reply"))
						user.setReplies(1);
					else if (requestReplyClass.equals("__label__Request"))
						user.setRequests(1);
					db.getUsers().add(user);
				} else {
					java.util.Date javaDate = NntpUtil.parseDate(user.getLastActivityDate());
					Date userDate = new Date(javaDate);
					Date articleDate = new Date(article.getDate());
					if (articleDate.compareTo(userDate)==1)
						user.setLastActivityDate(article.getDate().toString());
					user.setArticles(user.getArticles()+1);
					String requestReplyClass = getRequestReplyClass(usedClassifier, ossmeterID, article);
					if (requestReplyClass.equals("__label__Reply"))
						user.setReplies(user.getReplies()+1);
					else if (requestReplyClass.equals("__label__Request"))
						user.setRequests(user.getRequests()+1);
				}
				db.sync();
			}
			
			Iterable<User> usersIt = db.getUsers().findByNewsgroupName(ossmeterID);
			int users = 0,
				activeUsers = 0,
				inactiveUsers = 0;
			for (User user:  usersIt) {
				Boolean active = true;
				users++;
				java.util.Date javaDate = NntpUtil.parseDate(user.getLastActivityDate());
				if (javaDate!=null) {
					Date date = new Date(javaDate);
					if (projectDelta.getDate().compareTo(date.addDays(STEP)) >0) {
						active=false;
					}
				} else
					active=false;
				if (active) activeUsers++;
				else inactiveUsers++;
			}
			newsgroupData.setActiveUsers(activeUsers);
			newsgroupData.setInactiveUsers(inactiveUsers);
			newsgroupData.setUsers(users);
			db.sync();
		}
	}

	private String getRequestReplyClass(RequestReplyClassificationTransMetric usedClassifier, 
			String ossmeterID, CommunicationChannelArticle article) {
		Iterable<NewsgroupArticles> newsgroupArticlesIt = usedClassifier.getNewsgroupArticles().
				find(NewsgroupArticles.NEWSGROUPNAME.eq(ossmeterID), 
						NewsgroupArticles.ARTICLEID.eq(article.getArticleId()));
		NewsgroupArticles newsgroupArticleData = null;
		for (NewsgroupArticles art:  newsgroupArticlesIt) {
			newsgroupArticleData = art;
		}
		if (newsgroupArticleData == null) {
			System.err.println("Active users metric -\t" + 
					"there is no classification for article: " + article.getArticleId() +
					"\t of newsgroup: " + ossmeterID);
//			System.exit(-1);
		} else
			return newsgroupArticleData.getClassificationResult();
		return "";
	}

	@Override
	public String getShortIdentifier() {
		return "trans.newsgroups.activeusers";
	}

	@Override
	public String getFriendlyName() {
		return "Number of users with new comment in the last 15 days";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the number of users that submitted news comments in the last 15 days, per newsgroup.";
	}

}
