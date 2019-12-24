/*******************************************************************************
 * Copyright (c) 2018 Edge Hill University
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.severityclassifier.opennlptartarus.libsvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scava.metricprovider.trans.newsgroups.threads.model.ArticleData;
import org.eclipse.scava.platform.delta.communicationchannel.CommunicationChannelArticle;

public class ClassificationInstanceCollection {

	private Map<String, ClassificationInstance> instanceMap;
	private List<ClassificationInstance> instanceList;

	public ClassificationInstanceCollection() {
		super();
		instanceMap = new HashMap<String, ClassificationInstance>();
		instanceList = new ArrayList<ClassificationInstance>();
	}
	
	public void add(ClassifierMessage classifierMessage) {
		ClassificationInstance classificationInstance;
		if (instanceMap.containsKey(classifierMessage.getComposedId())) {
			classificationInstance = instanceMap.get(classifierMessage.getComposedId());
			classificationInstance.update(classifierMessage);
		} else {
			classificationInstance = new ClassificationInstance(classifierMessage);
			instanceMap.put(classifierMessage.getComposedId(), classificationInstance);
			instanceList.add(classificationInstance);
		}
	}
	
	public void add(FeatureGenerator featureGenerator,
					ClassifierMessage classifierMessage, 
					FeatureIdCollection featureIdCollection) {
		ClassificationInstance classificationInstance;
		if (instanceMap.containsKey(classifierMessage.getComposedId())) {
			classificationInstance = instanceMap.get(classifierMessage.getComposedId());
			classificationInstance.update(classifierMessage);
		} else {
			classificationInstance = new ClassificationInstance(
								featureGenerator, classifierMessage, featureIdCollection);
			instanceMap.put(classifierMessage.getComposedId(), classificationInstance);
			instanceList.add(classificationInstance);
		}
	}
	
	public void add(String url, CommunicationChannelArticle deltaArticle, String threadId, ClassificationInstance classificationInstance) {
		String composedId = getComposedId(url, threadId);
		if (instanceMap.containsKey(composedId)) {
				classificationInstance = instanceMap.get(composedId);
				classificationInstance.update(deltaArticle);
		} else {
			instanceMap.put(composedId, classificationInstance);
			instanceList.add(classificationInstance);
		}
	}
	
	public ClassificationInstance getClassificationInstance(ClassifierMessage classifierMessage) {
		return instanceMap.get(classifierMessage.getComposedId());
	}

	private String getComposedId(String url, String threadId) {
		if ( ( url==null ) || ( threadId==null ) ) 
			System.err.println("Unable to compose ID");
		return url + "#" + threadId;
	}

	public void add(FeatureGenerator featureGenerator, 
					ArticleData articleData,
					String threadId, FeatureIdCollection featureIdCollection) {
		String composedId = getComposedId(articleData, threadId);
		ClassificationInstance classificationInstance;
		if (instanceMap.containsKey(composedId)) {
				classificationInstance = instanceMap.get(composedId);
				classificationInstance.update(featureGenerator, 
											  featureIdCollection);
		} else {
			classificationInstance = new ClassificationInstance(featureGenerator, articleData, 
																threadId, featureIdCollection);
			instanceMap.put(composedId, classificationInstance);
			instanceList.add(classificationInstance);
		}
	}

	private String getComposedId(ArticleData articleData, String threadId) {
		if ( ( articleData.getNewsgroupName()==null ) || ( threadId==null ) ) 
			System.err.println("Unable to compose ID");
		return articleData.getNewsgroupName() + "#" + threadId;
	}

	public List<ClassificationInstance> getInstanceList() {
		return instanceList;
	}

	public void getNGrams(String url, ArticleData articleData) {
		// TODO Auto-generated method stub
		
	}

	public int size() {
		return instanceList.size();
	}

}
