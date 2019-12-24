/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.historic.newsgroups.sentiment;

import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.historic.newsgroups.sentiment.model.NewsgroupsSentimentHistoricMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.SentimentTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.NewsgroupsSentimentTransMetric;
import org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics;
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

public class SentimentHistoricMetricProvider extends AbstractHistoricalMetricProvider{

	public final static String IDENTIFIER = SentimentHistoricMetricProvider.class.getCanonicalName();

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
		if (uses.size()!=1) {
			System.err.println("Metric: avgnumberofrequestsreplies failed to retrieve " + 
								"the transient metric it needs!");
		}

		 NewsgroupsSentimentTransMetric sentimentTransMetric = 
				 ((SentimentTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));

		float overallSentiment = 0,
			  startSentiment = 0,
			  endSentiment = 0;
		for (ThreadStatistics threadStatistics: sentimentTransMetric.getThreads()) {
			overallSentiment += threadStatistics.getAverageSentiment();
			String start = threadStatistics.getStartSentiment();
			if (start.equals("__label__positive"))
				startSentiment+=1;
			else if (start.equals("__label__negative"))
				startSentiment-=1;
			String end = threadStatistics.getEndSentiment();
			if (end.equals("__label__positive"))
				endSentiment+=1;
			else if (end.equals("__label__negative"))
				endSentiment-=1;
		}
		long size = sentimentTransMetric.getThreads().size();
		NewsgroupsSentimentHistoricMetric sentiment = new NewsgroupsSentimentHistoricMetric();
		if (size>0) {
			overallSentiment /= size;
			startSentiment /= size;
			endSentiment /= size;
			sentiment.setOverallAverageSentiment(overallSentiment);
			sentiment.setOverallSentimentAtThreadBeggining(startSentiment);
			sentiment.setOverallSentimentAtThreadEnd(endSentiment);
		}
		return sentiment;
	}
			
	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}
	
	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(SentimentTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context = context;
	}

	@Override
	public String getShortIdentifier() {
		return "historic.newsgroups.sentiment";
	}

	@Override
	public String getFriendlyName() {
		return "Overall sentiment of newsgroup articles";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric computes the overall sentiment per repository up to the processing date." +
				"The overall sentiment score could be -1 (negative sentiment), 0 (neutral sentiment) or +1 (positive sentiment)." +
				"In the computation, the sentiment score of each thread contributes equally, independently of its size.";
	}
}
