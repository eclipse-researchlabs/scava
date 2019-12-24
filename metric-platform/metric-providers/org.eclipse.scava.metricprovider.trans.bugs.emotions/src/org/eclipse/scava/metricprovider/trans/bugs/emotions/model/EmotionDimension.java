/*******************************************************************************
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.metricprovider.trans.bugs.emotions.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class EmotionDimension extends Pongo {
	
	
	
	public EmotionDimension() { 
		super();
		BUGTRACKERID.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
		EMOTIONLABEL.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
		NUMBEROFCOMMENTS.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
		CUMULATIVENUMBEROFCOMMENTS.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
		PERCENTAGE.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
		CUMULATIVEPERCENTAGE.setOwningType("org.eclipse.scava.metricprovider.trans.bugs.emotions.model.EmotionDimension");
	}
	
	public static StringQueryProducer BUGTRACKERID = new StringQueryProducer("bugTrackerId"); 
	public static StringQueryProducer EMOTIONLABEL = new StringQueryProducer("emotionLabel"); 
	public static NumericalQueryProducer NUMBEROFCOMMENTS = new NumericalQueryProducer("numberOfComments");
	public static NumericalQueryProducer CUMULATIVENUMBEROFCOMMENTS = new NumericalQueryProducer("cumulativeNumberOfComments");
	public static NumericalQueryProducer PERCENTAGE = new NumericalQueryProducer("percentage");
	public static NumericalQueryProducer CUMULATIVEPERCENTAGE = new NumericalQueryProducer("cumulativePercentage");
	
	
	public String getBugTrackerId() {
		return parseString(dbObject.get("bugTrackerId")+"", "");
	}
	
	public EmotionDimension setBugTrackerId(String bugTrackerId) {
		dbObject.put("bugTrackerId", bugTrackerId);
		notifyChanged();
		return this;
	}
	public String getEmotionLabel() {
		return parseString(dbObject.get("emotionLabel")+"", "");
	}
	
	public EmotionDimension setEmotionLabel(String emotionLabel) {
		dbObject.put("emotionLabel", emotionLabel);
		notifyChanged();
		return this;
	}
	public int getNumberOfComments() {
		return parseInteger(dbObject.get("numberOfComments")+"", 0);
	}
	
	public EmotionDimension setNumberOfComments(int numberOfComments) {
		dbObject.put("numberOfComments", numberOfComments);
		notifyChanged();
		return this;
	}
	public int getCumulativeNumberOfComments() {
		return parseInteger(dbObject.get("cumulativeNumberOfComments")+"", 0);
	}
	
	public EmotionDimension setCumulativeNumberOfComments(int cumulativeNumberOfComments) {
		dbObject.put("cumulativeNumberOfComments", cumulativeNumberOfComments);
		notifyChanged();
		return this;
	}
	public float getPercentage() {
		return parseFloat(dbObject.get("percentage")+"", 0.0f);
	}
	
	public EmotionDimension setPercentage(float percentage) {
		dbObject.put("percentage", percentage);
		notifyChanged();
		return this;
	}
	public float getCumulativePercentage() {
		return parseFloat(dbObject.get("cumulativePercentage")+"", 0.0f);
	}
	
	public EmotionDimension setCumulativePercentage(float cumulativePercentage) {
		dbObject.put("cumulativePercentage", cumulativePercentage);
		notifyChanged();
		return this;
	}
	
	
	
	
}
