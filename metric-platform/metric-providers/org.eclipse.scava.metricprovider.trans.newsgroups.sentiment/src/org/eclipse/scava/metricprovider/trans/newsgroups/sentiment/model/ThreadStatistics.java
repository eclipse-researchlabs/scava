package org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class ThreadStatistics extends Pongo {
	
	
	
	public ThreadStatistics() { 
		super();
		NEWSGROUPNAME.setOwningType("org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics");
		THREADID.setOwningType("org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics");
		AVERAGESENTIMENT.setOwningType("org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics");
		STARTSENTIMENT.setOwningType("org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics");
		ENDSENTIMENT.setOwningType("org.eclipse.scava.metricprovider.trans.newsgroups.sentiment.model.ThreadStatistics");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsgroupName"); 
	public static StringQueryProducer THREADID = new StringQueryProducer("threadId"); 
	public static NumericalQueryProducer AVERAGESENTIMENT = new NumericalQueryProducer("averageSentiment");
	public static StringQueryProducer STARTSENTIMENT = new StringQueryProducer("startSentiment"); 
	public static StringQueryProducer ENDSENTIMENT = new StringQueryProducer("endSentiment"); 
	
	
	public String getNewsgroupName() {
		return parseString(dbObject.get("newsgroupName")+"", "");
	}
	
	public ThreadStatistics setNewsgroupName(String newsgroupName) {
		dbObject.put("newsgroupName", newsgroupName);
		notifyChanged();
		return this;
	}
	public String getThreadId() {
		return parseString(dbObject.get("threadId")+"", "");
	}
	
	public ThreadStatistics setThreadId(String threadId) {
		dbObject.put("threadId", threadId);
		notifyChanged();
		return this;
	}
	public float getAverageSentiment() {
		return parseFloat(dbObject.get("averageSentiment")+"", 0.0f);
	}
	
	public ThreadStatistics setAverageSentiment(float averageSentiment) {
		dbObject.put("averageSentiment", averageSentiment);
		notifyChanged();
		return this;
	}
	public String getStartSentiment() {
		return parseString(dbObject.get("startSentiment")+"", "");
	}
	
	public ThreadStatistics setStartSentiment(String startSentiment) {
		dbObject.put("startSentiment", startSentiment);
		notifyChanged();
		return this;
	}
	public String getEndSentiment() {
		return parseString(dbObject.get("endSentiment")+"", "");
	}
	
	public ThreadStatistics setEndSentiment(String endSentiment) {
		dbObject.put("endSentiment", endSentiment);
		notifyChanged();
		return this;
	}
	
	
	
	
}