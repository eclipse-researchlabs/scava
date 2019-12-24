/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.factoid.newsgroups.size;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.scava.metricprovider.historic.newsgroups.articles.ArticlesHistoricMetricProvider;
import org.eclipse.scava.metricprovider.historic.newsgroups.articles.model.DailyNewsgroupData;
import org.eclipse.scava.metricprovider.historic.newsgroups.articles.model.NewsgroupsArticlesHistoricMetric;
import org.eclipse.scava.metricprovider.historic.newsgroups.newthreads.NewThreadsHistoricMetricProvider;
import org.eclipse.scava.metricprovider.historic.newsgroups.newthreads.model.NewsgroupsNewThreadsHistoricMetric;
import org.eclipse.scava.platform.AbstractFactoidMetricProvider;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.delta.ProjectDelta;
import org.eclipse.scava.platform.factoids.Factoid;
import org.eclipse.scava.platform.factoids.StarRating;
import org.eclipse.scava.repository.model.CommunicationChannel;
import org.eclipse.scava.repository.model.Project;
import org.eclipse.scava.repository.model.cc.eclipseforums.EclipseForum;
import org.eclipse.scava.repository.model.cc.irc.Irc;
import org.eclipse.scava.repository.model.cc.mbox.Mbox;
import org.eclipse.scava.repository.model.cc.nntp.NntpNewsGroup;
import org.eclipse.scava.repository.model.cc.sympa.SympaMailingList;
import org.eclipse.scava.repository.model.sourceforge.Discussion;

import com.googlecode.pongo.runtime.Pongo;

public class NewsgroupsChannelSizeFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "factoid.newsgroups.size";
	}

	@Override
	public String getFriendlyName() {
		return "Newsgroup Channel Size";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "This plugin generates the factoid regarding thread size for newsgroups. "
				+ "For example, the cummulative number of threads"; // This method will NOT be removed in a later version.
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
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(ArticlesHistoricMetricProvider.IDENTIFIER,
							 NewThreadsHistoricMetricProvider.IDENTIFIER);
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.NEWSGROUPS);
		factoid.setName(getFriendlyName());

		ArticlesHistoricMetricProvider articlesProvider = new ArticlesHistoricMetricProvider();
		NewThreadsHistoricMetricProvider newThreadsProvider = new NewThreadsHistoricMetricProvider();

		for (IMetricProvider m : this.uses) {
			if (m instanceof ArticlesHistoricMetricProvider) {
				articlesProvider = (ArticlesHistoricMetricProvider) m;
				continue;
			}
			if (m instanceof NewThreadsHistoricMetricProvider) {
				newThreadsProvider = (NewThreadsHistoricMetricProvider) m;
				continue;
			}
		}

		List<Pongo> articlesList = articlesProvider.getHistoricalMeasurements(context, project, delta.getDate(), delta.getDate()),
					newThreadsList = newThreadsProvider.getHistoricalMeasurements(context, project, delta.getDate(), delta.getDate());
		
//		System.err.println("---SIZE===RETRIEVED PONGOLIST FOR " + articlesList.size() + " DAYS===---");
		
		Map<String, Integer> trackerArticles = new HashMap<String, Integer>();
		int numberOfArticles = getCumulativeNumberOfArticles(articlesList, trackerArticles);

		Map<String, Integer> trackerNewThreads = new HashMap<String, Integer>();
		int numberOfNewThreads = getCumulativeNumberOfThreads(newThreadsList, trackerNewThreads);

		int threshold = 1000;
		
		if ( (numberOfArticles > 10 * threshold) || (numberOfNewThreads > threshold) ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( (2 * numberOfArticles > 10 * threshold) || (2 * numberOfNewThreads > threshold) ) {
			factoid.setStars(StarRating.THREE);
		} else if ( (4 * numberOfArticles > 10 * threshold) || (4 * numberOfNewThreads > threshold) ) {
			factoid.setStars(StarRating.TWO);
		} else
			factoid.setStars(StarRating.ONE);
		
		StringBuffer stringBuffer = new StringBuffer();
		
		int articles = 0, threads = 0;
		for (String tracker: sortByKeys(trackerArticles)) {
			if(trackerArticles.containsKey(tracker))
				articles += trackerArticles.get(tracker);
			if(trackerNewThreads.containsKey(tracker))
				threads += trackerNewThreads.get(tracker);
		}
		stringBuffer.append("The newsgroups of the project contain ");
		stringBuffer.append(threads);
		stringBuffer.append(" threads and ");
		stringBuffer.append(articles);
		stringBuffer.append(" articles, in total.\n");

		factoid.setFactoid(stringBuffer.toString());

	}

	private int getCumulativeNumberOfArticles(List<Pongo> newArticlesList, Map<String, Integer> trackerArticles) {
		int sum = 0;
		if ( newArticlesList.size() > 0 ) {
			NewsgroupsArticlesHistoricMetric newArticlesPongo = 
					(NewsgroupsArticlesHistoricMetric) newArticlesList.get(0);
			for (DailyNewsgroupData newsgroupData: newArticlesPongo.getNewsgroups()) {
				int articles = newsgroupData.getCumulativeNumberOfArticles();
				trackerArticles.put(newsgroupData.getNewsgroupName(), articles);
				sum += articles;
			}
		}
		return sum;
	}
	
	private int getCumulativeNumberOfThreads(List<Pongo> newThreadsList, Map<String, Integer> trackerNewThreads) {
		int sum = 0;
		for (Pongo pongo: newThreadsList) {
			NewsgroupsNewThreadsHistoricMetric commentsPongo = (NewsgroupsNewThreadsHistoricMetric) pongo;
			for (org.eclipse.scava.metricprovider.historic.newsgroups.newthreads.model.DailyNewsgroupData 
					newsgroupData: commentsPongo.getNewsgroups()) {
				int threads = newsgroupData.getCumulativeNumberOfNewThreads();
				trackerNewThreads.put(newsgroupData.getNewsgroupName(), threads);
				sum += threads;
			}
		}
		return sum;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SortedSet<String> sortByKeys(Map<String, ?> map) {
		return new TreeSet(map.keySet());
	}

}
