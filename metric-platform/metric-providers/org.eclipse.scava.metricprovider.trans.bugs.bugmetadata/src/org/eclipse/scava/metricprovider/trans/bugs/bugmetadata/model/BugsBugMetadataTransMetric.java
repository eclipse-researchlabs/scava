package org.eclipse.scava.metricprovider.trans.bugs.bugmetadata.model;

import com.googlecode.pongo.runtime.*;
import com.mongodb.*;
// protected region custom-imports on begin
// protected region custom-imports end

public class BugsBugMetadataTransMetric extends PongoDB {
	
	public BugsBugMetadataTransMetric() {}
	
	public BugsBugMetadataTransMetric(DB db) {
		setDb(db);
	}
	
	protected BugDataCollection bugData = null;
	protected CommentDataCollection comments = null;
	
	// protected region custom-fields-and-methods on begin
	// protected region custom-fields-and-methods end
	
	
	public BugDataCollection getBugData() {
		return bugData;
	}
	
	public CommentDataCollection getComments() {
		return comments;
	}
	
	
	@Override
	public void setDb(DB db) {
		super.setDb(db);
		bugData = new BugDataCollection(db.getCollection("BugsBugMetadataTransMetric.bugData"));
		pongoCollections.add(bugData);
		comments = new CommentDataCollection(db.getCollection("BugsBugMetadataTransMetric.comments"));
		pongoCollections.add(comments);
	}
}