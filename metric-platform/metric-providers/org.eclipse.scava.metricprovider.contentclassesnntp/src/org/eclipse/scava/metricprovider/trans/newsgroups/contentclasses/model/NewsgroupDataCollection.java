package org.eclipse.scava.metricprovider.trans.newsgroups.contentclasses.model;

import com.googlecode.pongo.runtime.*;
import java.util.*;
import com.mongodb.*;

public class NewsgroupDataCollection extends PongoCollection<NewsgroupData> {
	
	public NewsgroupDataCollection(DBCollection dbCollection) {
		super(dbCollection);
		createIndex("newsgroupName");
	}
	
	public Iterable<NewsgroupData> findById(String id) {
		return new IteratorIterable<NewsgroupData>(new PongoCursorIterator<NewsgroupData>(this, dbCollection.find(new BasicDBObject("_id", id))));
	}
	
	public Iterable<NewsgroupData> findByNewsgroupName(String q) {
		return new IteratorIterable<NewsgroupData>(new PongoCursorIterator<NewsgroupData>(this, dbCollection.find(new BasicDBObject("newsgroupName", q + ""))));
	}
	
	public NewsgroupData findOneByNewsgroupName(String q) {
		NewsgroupData newsgroupData = (NewsgroupData) PongoFactory.getInstance().createPongo(dbCollection.findOne(new BasicDBObject("newsgroupName", q + "")));
		if (newsgroupData != null) {
			newsgroupData.setPongoCollection(this);
		}
		return newsgroupData;
	}
	

	public long countByNewsgroupName(String q) {
		return dbCollection.count(new BasicDBObject("newsgroupName", q + ""));
	}
	
	@Override
	public Iterator<NewsgroupData> iterator() {
		return new PongoCursorIterator<NewsgroupData>(this, dbCollection.find());
	}
	
	public void add(NewsgroupData newsgroupData) {
		super.add(newsgroupData);
	}
	
	public void remove(NewsgroupData newsgroupData) {
		super.remove(newsgroupData);
	}
	
}