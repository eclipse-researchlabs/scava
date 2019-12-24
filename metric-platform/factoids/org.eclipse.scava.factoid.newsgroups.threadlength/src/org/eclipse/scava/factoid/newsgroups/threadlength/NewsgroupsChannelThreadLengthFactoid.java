/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.factoid.newsgroups.threadlength;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.historic.newsgroups.threads.ThreadsHistoricMetricProvider;
import org.eclipse.scava.metricprovider.historic.newsgroups.threads.model.NewsgroupsThreadsHistoricMetric;
import org.eclipse.scava.platform.AbstractFactoidMetricProvider;
import org.eclipse.scava.platform.Date;
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

public class NewsgroupsChannelThreadLengthFactoid extends AbstractFactoidMetricProvider{

	protected List<IMetricProvider> uses;
	
	@Override
	public String getShortIdentifier() {
		return "factoid.newsgroups.threadlength";
	}

	@Override
	public String getFriendlyName() {
		return "Newsgroup Channel Thread Length";
		// This method will NOT be removed in a later version.
	}

	@Override
	public String getSummaryInformation() {
		return "This plugin generates the factoid regarding thread length for newsgroups. "
				+ "For example, the average length of discussion per day, month etc. "; 
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
		return Arrays.asList(ThreadsHistoricMetricProvider.IDENTIFIER);
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses = uses;
	}

	@Override
	public void measureImpl(Project project, ProjectDelta delta, Factoid factoid) {
//		factoid.setCategory(FactoidCategory.BUGS);
		factoid.setName(getFriendlyName());

		ThreadsHistoricMetricProvider threadsProvider = null;
		
		for (IMetricProvider m : this.uses) {
			if (m instanceof ThreadsHistoricMetricProvider) {
				threadsProvider = (ThreadsHistoricMetricProvider) m;
				continue;
			}
		}

		List<Pongo> threadsList = threadsProvider.getHistoricalMeasurements(context, project, delta.getDate(), delta.getDate());
		
		float averageComments = 0;

		if ( threadsList.size() > 0 ) {
			NewsgroupsThreadsHistoricMetric bugsPongo = 
					(NewsgroupsThreadsHistoricMetric) threadsList.get(0);
			averageComments = bugsPongo.getAverageArticlesPerThread();
		}

		if ( (averageComments > 0 ) && (averageComments < 5 ) ) {
			factoid.setStars(StarRating.FOUR);
		} else if ( (averageComments > 0 ) && ( averageComments < 10 ) ) {
			factoid.setStars(StarRating.THREE);
		} else if ( (averageComments > 0 ) && ( averageComments < 20 ) ) {
			factoid.setStars(StarRating.TWO);
		} else {
			factoid.setStars(StarRating.ONE);
		}

		StringBuffer stringBuffer = new StringBuffer();
		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		
		stringBuffer.append("Discussions tend to be ");
		if ( (averageComments > 0 ) && (averageComments < 5 ) ) {
			stringBuffer.append("very short");
		} else if ( (averageComments > 0 ) && ( averageComments < 10 ) ) {
			stringBuffer.append("short");
		} else if ( (averageComments > 0 ) && ( averageComments < 20 ) ) {
			stringBuffer.append("quite long");
		} else {
			stringBuffer.append("long");
		}
		stringBuffer.append(", containing approximately ");
		stringBuffer.append( decimalFormat.format(averageComments));
		stringBuffer.append(" articles.\n");

		factoid.setFactoid(stringBuffer.toString());

	}

}
