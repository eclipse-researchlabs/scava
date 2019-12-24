package org.eclipse.scava.metricprovider.trans.sentimentclassification.model;

import com.googlecode.pongo.runtime.*;
import com.mongodb.*;
// protected region custom-imports on begin
// protected region custom-imports end

public class SentimentClassificationTransMetric extends PongoDB {
	
	public SentimentClassificationTransMetric() {}
	
	public SentimentClassificationTransMetric(DB db) {
		setDb(db);
	}
	
	protected BugTrackerCommentsSentimentClassificationCollection bugTrackerComments = null;
	protected NewsgroupArticlesSentimentClassificationCollection newsgroupArticles = null;
	
	// protected region custom-fields-and-methods on begin
	// protected region custom-fields-and-methods end
	
	
	public BugTrackerCommentsSentimentClassificationCollection getBugTrackerComments() {
		return bugTrackerComments;
	}
	
	public NewsgroupArticlesSentimentClassificationCollection getNewsgroupArticles() {
		return newsgroupArticles;
	}
	
	
	@Override
	public void setDb(DB db) {
		super.setDb(db);
		bugTrackerComments = new BugTrackerCommentsSentimentClassificationCollection(db.getCollection("SentimentClassificationTransMetric.bugTrackerComments"));
		pongoCollections.add(bugTrackerComments);
		newsgroupArticles = new NewsgroupArticlesSentimentClassificationCollection(db.getCollection("SentimentClassificationTransMetric.newsgroupArticles"));
		pongoCollections.add(newsgroupArticles);
	}
}