/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.factoid.newsgroups.sentiment;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.historic.newsgroups.sentiment.SentimentHistoricMetricProvider;
import org.eclipse.scava.metricprovider.historic.newsgroups.sentiment.model.NewsgroupsSentimentHistoricMetric;
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

public class NewsgroupsChannelSentimentFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "factoid.newsgroups.sentiment";
	}

	@Override
	public String getFriendlyName() {
		return "Newsgroup Channel Sentiment";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "This plugin generates the factoid regarding sentiments for newsgroups. "
				+ "For example, the average sentiment in all newsgroup channel associated to a project"; 
		// This method will NOT be removed in a later version.
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
		return Arrays.asList(SentimentHistoricMetricProvider.IDENTIFIER);
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.BUGS);
		factoid.setName(getFriendlyName());

		SentimentHistoricMetricProvider sentimentProvider = null;

		for (IMetricProvider m : this.uses) {
			if (m instanceof SentimentHistoricMetricProvider) {
				sentimentProvider = (SentimentHistoricMetricProvider) m;
				continue;
			}
		}

		List<Pongo> sentimentList = sentimentProvider.getHistoricalMeasurements(context, project, delta.getDate(), delta.getDate());
		
		float averageSentiment = getAverageSentiment(sentimentList),
			  sentimentAtThreadBeggining = getSentimentAtThreadBeggining(sentimentList),
			  sentimentAtThreadEnd = getSentimentAtThreadEnd(sentimentList);

		if ( ( averageSentiment > 0.5 )
			   || ( ( sentimentAtThreadEnd - sentimentAtThreadBeggining > 0.25 ) 
					&& ( sentimentAtThreadBeggining > 0.15 ) )
		     ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( ( averageSentiment > 0.25 )
					  || ( ( sentimentAtThreadEnd - sentimentAtThreadBeggining > 0.125 ) 
						 && ( sentimentAtThreadEnd > 0.0 ) )
				  ) {
			factoid.setStars(StarRating.THREE);
		} else if ( ( averageSentiment > 0 )
					  || ( sentimentAtThreadEnd - sentimentAtThreadBeggining > 0 ) ) {
			factoid.setStars(StarRating.TWO);
		} else {
			factoid.setStars(StarRating.ONE);
		}
		
		StringBuffer stringBuffer = new StringBuffer();
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		stringBuffer.append("The average sentimental polarity in all bug-tracking systems " +
							"associated with the project is ");
		if ( averageSentiment > 0.3 )
			stringBuffer.append("positive");
		else if ( averageSentiment > 0.15 )
			stringBuffer.append("weakly positive");
		else if ( averageSentiment > -0.15 )
			stringBuffer.append("neutral");
		else if ( averageSentiment > -0.3 )
			stringBuffer.append("weakly negative");
		else 
			stringBuffer.append("negative");
		stringBuffer.append(" (");
		stringBuffer.append(decimalFormat.format(averageSentiment));
		stringBuffer.append(" with -1 designating entirely negative sentiment " +
				"and 1 designating entirely positive sentiment).\n");
		
		stringBuffer.append("At the beginning of threads, the average sentiment score is ");
		stringBuffer.append(decimalFormat.format(sentimentAtThreadBeggining));
		stringBuffer.append(", while at the end of threads it is ");
		stringBuffer.append(decimalFormat.format(sentimentAtThreadEnd));
		stringBuffer.append(", showing that users ");
		if ( Math.abs( sentimentAtThreadBeggining - sentimentAtThreadEnd ) < 0.15 ) 
			stringBuffer.append("have similar feelings");
		else if ( sentimentAtThreadBeggining < sentimentAtThreadEnd )
			stringBuffer.append("are happier");
		else
			stringBuffer.append("are unhappier");
		stringBuffer.append(" at the end of a discussion ");
		if ( Math.abs( sentimentAtThreadBeggining - sentimentAtThreadEnd ) < 0.15 )
			stringBuffer.append("as");
		else
			stringBuffer.append("than");
		stringBuffer.append(" at the beginning of it.\n");

		factoid.setFactoid(stringBuffer.toString());

	}

	private float getAverageSentiment(List<Pongo> sentimentList) {
		if ( sentimentList.size() > 0 ) {
			NewsgroupsSentimentHistoricMetric sentimentPongo = 
					(NewsgroupsSentimentHistoricMetric) sentimentList.get(0);
			return sentimentPongo.getOverallAverageSentiment();
		}
		return 0;
	}

	private float getSentimentAtThreadBeggining(List<Pongo> sentimentList) {
		if ( sentimentList.size() > 0 ) {
			NewsgroupsSentimentHistoricMetric sentimentPongo = 
					(NewsgroupsSentimentHistoricMetric) sentimentList.get(0);
			return sentimentPongo.getOverallSentimentAtThreadBeggining();
		}
		return 0;
	}
	
	private float getSentimentAtThreadEnd(List<Pongo> sentimentList) {
		if ( sentimentList.size() > 0 ) {
			NewsgroupsSentimentHistoricMetric sentimentPongo = 
					(NewsgroupsSentimentHistoricMetric) sentimentList.get(0);
			return sentimentPongo.getOverallSentimentAtThreadEnd();
		}
		return 0;
	}

}
