package org.eclipse.scava.metricprovider.trans.detectingcode.model;

import com.mongodb.*;
import java.util.*;
import com.googlecode.pongo.runtime.*;
import com.googlecode.pongo.runtime.querying.*;


public class NewsgroupArticleDetectingCode extends Pongo {
	
	
	
	public NewsgroupArticleDetectingCode() { 
		super();
		NEWSGROUPNAME.setOwningType("org.eclipse.scava.metricprovider.trans.detectingcode.model.NewsgroupArticleDetectingCode");
		ARTICLEID.setOwningType("org.eclipse.scava.metricprovider.trans.detectingcode.model.NewsgroupArticleDetectingCode");
		NATURALLANGUAGE.setOwningType("org.eclipse.scava.metricprovider.trans.detectingcode.model.NewsgroupArticleDetectingCode");
		CODE.setOwningType("org.eclipse.scava.metricprovider.trans.detectingcode.model.NewsgroupArticleDetectingCode");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsGroupName"); 
	public static StringQueryProducer ARTICLEID = new StringQueryProducer("articleId"); 
	public static StringQueryProducer NATURALLANGUAGE = new StringQueryProducer("naturalLanguage"); 
	public static StringQueryProducer CODE = new StringQueryProducer("code"); 
	
	
	public String getNewsGroupName() {
		return parseString(dbObject.get("newsGroupName")+"", "");
	}
	
	public NewsgroupArticleDetectingCode setNewsGroupName(String newsGroupName) {
		dbObject.put("newsGroupName", newsGroupName);
		notifyChanged();
		return this;
	}
	public String getArticleId() {
		return parseString(dbObject.get("articleId")+"", "");
	}
	
	public NewsgroupArticleDetectingCode setArticleId(String articleId) {
		dbObject.put("articleId", articleId);
		notifyChanged();
		return this;
	}
	public String getNaturalLanguage() {
		return parseString(dbObject.get("naturalLanguage")+"", "");
	}
	
	public NewsgroupArticleDetectingCode setNaturalLanguage(String naturalLanguage) {
		dbObject.put("naturalLanguage", naturalLanguage);
		notifyChanged();
		return this;
	}
	public String getCode() {
		return parseString(dbObject.get("code")+"", "");
	}
	
	public NewsgroupArticleDetectingCode setCode(String code) {
		dbObject.put("code", code);
		notifyChanged();
		return this;
	}
	
	
	
	
}