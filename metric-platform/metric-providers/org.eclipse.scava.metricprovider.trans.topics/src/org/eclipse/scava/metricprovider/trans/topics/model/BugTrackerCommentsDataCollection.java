package org.eclipse.scava.metricprovider.trans.topics.model;

import java.util.Iterator;

import com.googlecode.pongo.runtime.IteratorIterable;
import com.googlecode.pongo.runtime.PongoCollection;
import com.googlecode.pongo.runtime.PongoCursorIterator;
import com.googlecode.pongo.runtime.PongoFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class BugTrackerCommentsDataCollection extends PongoCollection<BugTrackerCommentsData> {
	
	public BugTrackerCommentsDataCollection(DBCollection dbCollection) {
		super(dbCollection);
		createIndex("bugTrackerId");
	}
	
	public Iterable<BugTrackerCommentsData> findById(String id) {
		return new IteratorIterable<BugTrackerCommentsData>(new PongoCursorIterator<BugTrackerCommentsData>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	public Iterable<BugTrackerCommentsData> findByBugTrackerId(String q) {
		return new IteratorIterable<BugTrackerCommentsData>(new PongoCursorIterator<BugTrackerCommentsData>(this, dbCollection.find(new BasicDBObject("bugTrackerId", q + ""))));
	}
	
	public BugTrackerCommentsData findOneByBugTrackerId(String q) {
		BugTrackerCommentsData bugTrackerCommentsData = (BugTrackerCommentsData) PongoFactory.getInstance().createPongo(dbCollection.findOne(new BasicDBObject("bugTrackerId", q + "")));
		if (bugTrackerCommentsData != null) {
			bugTrackerCommentsData.setPongoCollection(this);
		}
		return bugTrackerCommentsData;
	}
	

	public long countByBugTrackerId(String q) {
		return dbCollection.count(new BasicDBObject("bugTrackerId", q + ""));
	}
	
	@Override
	public Iterator<BugTrackerCommentsData> iterator() {
		return new PongoCursorIterator<BugTrackerCommentsData>(this, dbCollection.find());
	}
	
	public void add(BugTrackerCommentsData bugTrackerCommentsData) {
		super.add(bugTrackerCommentsData);
	}
	
	public void remove(BugTrackerCommentsData bugTrackerCommentsData) {
		super.remove(bugTrackerCommentsData);
	}
	
}