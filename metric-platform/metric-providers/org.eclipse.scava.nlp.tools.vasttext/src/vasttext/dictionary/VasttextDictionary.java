/*-
 * 	* Copyright 2018 Edge Hill University
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *  *
 *  * 	 This file has been modified to support characteristics from VastText.
 */

package vasttext.dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.datavec.api.conf.Configuration;
import org.nd4j.linalg.primitives.Counter;
import org.nd4j.linalg.util.MathUtils;
import org.nd4j.util.Index;

import vasttext.vectorizer.VasttextTextVectorizer;


/**
 * The original author was Adam Gibson, the current version add supports to store:
 * - Labels used in VastText
 * - Maximum size of n-grams
 * - Maximum size of skip-bigrams
 * These last two elements, are used to being able storing the model with a dictionary that
 * can be reused for generating new vectors.
 *
 * @author Adam Gibson
 * @author Adrián Cabrera
 */
public class VasttextDictionary implements Serializable {

	private static final long serialVersionUID = -5981669074190113022L;

    private Counter<String> wordFrequencies = new Counter<>();
    private Counter<String> docFrequencies = new Counter<>();
    private List<String> labels = new ArrayList<String>();
    private int minWordFrequency;
    private Index vocabWords = new Index();
    private double numDocs = 0;
	private int maxNgrams;
	private int maxSkipBigrams;

    /**
     * Instantiate with a given min word frequency
     * @param minWordFrequency
     */
    public VasttextDictionary(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
        emptyText();
    }
    
    public VasttextDictionary()
    {
    	emptyText();
    }

	public void setMaxNgrams(int value)
	{
		maxNgrams=value;
	}

	public int getMaxNgrams()
	{
		return maxNgrams;
	}

	public void setMaxSkipBigrams(int value)
	{
		maxSkipBigrams=value;
	}

	public int getMaxSkipBigrams()
	{
		return maxSkipBigrams;
	}

	/**
     * This method was created to create a vocabulary index that includes a new line, that will
     * solve any problem due to a text input empty
     */
    private void emptyText()
    {
    	if(vocabWords.indexOf("\n") < 0)
    		vocabWords.add("\n");
    }


    public int indexOf(String word)
    {
    	return vocabWords.indexOf(word);
    }

    /**
     * This method returns the index of a label used in a VastText model. It has two modes,
     * one used while the training is active and one when the testing is on going. During training,
     * new labels will be populated into an index. During testing, only labels seen during the training will have
     * a value greater than 0. 
     * @param label
     * @param fitFinished
     * @return
     */
    public int getLabelIndex(String label, boolean fitFinished)
    {
    	if(!labels.contains(label))
    	{
    		if(!fitFinished)
    			labels.add(label);
    		else
    			return -1;	//It means that we found a label in the testing data that we have never seen
    	}
    	return labels.indexOf(label);
    }

    public List<String> getLabels()
    {
    	return labels;
    }


    public void incrementNumDocs(double by) {
        numDocs += by;
    }

    public double numDocs() {
        return numDocs;
    }

    public String wordAt(int i) {
        return vocabWords.get(i).toString();
    }

    public void initialize(Configuration conf) {
        minWordFrequency = conf.getInt(VasttextTextVectorizer.MIN_WORD_FREQUENCY, 5);
    }

    public double wordFrequency(String word) {
        return wordFrequencies.getCount(word);
    }

    public int minWordFrequency() {
        return minWordFrequency;
    }

    public Index vocabWords() {
        return vocabWords;
    }

    public void incrementDocCount(String word) {
        incrementDocCount(word, 1.0);
    }

    public void incrementDocCount(String word, double by) {
        docFrequencies.incrementCount(word, by);

    }

    public void incrementCount(String word) {
        incrementCount(word, 1.0);
    }

    public void incrementCount(String word, double by) {
        wordFrequencies.incrementCount(word, by);
        if (wordFrequencies.getCount(word) >= minWordFrequency && vocabWords.indexOf(word) < 0)
            vocabWords.add(word);
    }

    public double idf(String word) {
        return docFrequencies.getCount(word);
    }

    public double tfidf(String word, double frequency) {
        return MathUtils.tfidf(MathUtils.tf((int) frequency), MathUtils.idf(numDocs, idf(word)));
    }

    public int getMinWordFrequency() {
        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
    }
}
