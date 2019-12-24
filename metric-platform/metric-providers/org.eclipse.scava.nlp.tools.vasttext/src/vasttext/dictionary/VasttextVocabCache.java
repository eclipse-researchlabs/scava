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

import org.datavec.api.conf.Configuration;
import org.datavec.nlp.metadata.VocabCache;
import org.nd4j.linalg.primitives.Counter;
import org.nd4j.linalg.util.MathUtils;
import org.nd4j.util.Index;

import vasttext.vectorizer.VasttextTextVectorizer;


/**
 * Original version by Adam Gibson which  created a  vocabulary cache used for storing information
 * about vocabulary. In the current version, it implements a method for 
 * getting the index of a word. As well, it becomes serializable, in order to replicate results
 *
 * @author Adam Gibson
 */
public class VasttextVocabCache implements VocabCache, Serializable {
	
	private static final long serialVersionUID = 1L;

    private Counter<String> wordFrequencies = new Counter<>();
    private Counter<String> docFrequencies = new Counter<>();
    private int minWordFrequency;
    private Index vocabWords = new Index();
    private double numDocs = 0;

    /**
     * Instantiate with a given min word frequency
     * @param minWordFrequency
     */
    public VasttextVocabCache(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
    }

    /*
     * Constructor for use with initialize()
     */
    public VasttextVocabCache() {}
    
    public int indexOf(String word)
    {
    	return vocabWords.indexOf(word);
    }
    
    @Override
    public void incrementNumDocs(double by) {
        numDocs += by;
    }

    @Override
    public double numDocs() {
        return numDocs;
    }

    @Override
    public String wordAt(int i) {
        return vocabWords.get(i).toString();
    }

    @Override
    public void initialize(Configuration conf) {
        minWordFrequency = conf.getInt(VasttextTextVectorizer.MIN_WORD_FREQUENCY, 5);
    }

    @Override
    public double wordFrequency(String word) {
        return wordFrequencies.getCount(word);
    }

    @Override
    public int minWordFrequency() {
        return minWordFrequency;
    }

    @Override
    public Index vocabWords() {
        return vocabWords;
    }

    @Override
    public void incrementDocCount(String word) {
        incrementDocCount(word, 1.0);
    }

    @Override
    public void incrementDocCount(String word, double by) {
        docFrequencies.incrementCount(word, by);

    }

    @Override
    public void incrementCount(String word) {
        incrementCount(word, 1.0);
    }

    @Override
    public void incrementCount(String word, double by) {
        wordFrequencies.incrementCount(word, by);
        if (wordFrequencies.getCount(word) >= minWordFrequency && vocabWords.indexOf(word) < 0)
            vocabWords.add(word);
    }

    @Override
    public double idf(String word) {
        return docFrequencies.getCount(word);
    }

    @Override
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
