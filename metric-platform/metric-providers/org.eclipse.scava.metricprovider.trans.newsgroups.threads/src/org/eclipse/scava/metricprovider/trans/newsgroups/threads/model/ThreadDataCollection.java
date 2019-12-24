package org.eclipse.scava.metricprovider.trans.newsgroups.threads.model;

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class ThreadDataCollection extends PongoCollection<ThreadData> {
	
	public ThreadDataCollection(DBCollection dbCollection) {
		super(dbCollection);
		createIndex("threadId");
	}
	
	public Iterable<ThreadData> findById(String id) {
		return new IteratorIterable<ThreadData>(new PongoCursorIterator<ThreadData>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	public Iterable<ThreadData> findByThreadId(String q) {
		return new IteratorIterable<ThreadData>(new PongoCursorIterator<ThreadData>(this, dbCollection.find(new BasicDBObject("threadId", q + ""))));
	}
	
	public ThreadData findOneByThreadId(String q) {
		ThreadData threadData = (ThreadData) PongoFactory.getInstance().createPongo(dbCollection.findOne(new BasicDBObject("threadId", q + "")));
		if (threadData != null) {
			threadData.setPongoCollection(this);
		}
		return threadData;
	}
	

	public long countByThreadId(String q) {
		return dbCollection.count(new BasicDBObject("threadId", q + ""));
	}
	
	@Override
	public Iterator<ThreadData> iterator() {
		return new PongoCursorIterator<ThreadData>(this, dbCollection.find());
	}
	
	public void add(ThreadData threadData) {
		super.add(threadData);
	}
	
	public void remove(ThreadData threadData) {
		super.remove(threadData);
	}
	
}