/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.eclipse.scava.business.IClusterCalculator;
import org.eclipse.scava.business.ISimilarityCalculator;
import org.eclipse.scava.business.integration.ArtifactRepository;
import org.eclipse.scava.business.model.Artifact;
import org.eclipse.scava.business.model.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;

/**
 * @author Juri Di Rocco
 *
 */
@Service
@Qualifier("KMeans")
public class KmeansClulsterCalulator implements IClusterCalculator {
	private final static String _CLUSTER_NAME = "KMeans";

	private static final Logger logger = LoggerFactory.getLogger(KmeansClulsterCalulator.class);
	@Autowired
	private ArtifactRepository arifactRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	private List<Artifact> objects = null;
	private Artifact[] medoids;
	private Cluster[] clusters;
	private ISimilarityCalculator sm;
	@Override
	public List<Cluster> calculateCluster(ISimilarityCalculator sm, double partitionOrTreshold) {
		
		// TODO num of cluster as parameter
		
		this.sm = sm;
		
		clusters = new Cluster[new Double(partitionOrTreshold).intValue()];
		for (int i = 0; i < clusters.length; i++) {
			clusters[i] = new Cluster();
		}
		
		objects = arifactRepository.findAll();
		Artifact[] newMedoids = populateRandomMedoids(clusters);
		clusters = execute(newMedoids);
		return Arrays.asList(clusters);
		
	}
	@Override
	public String getClusterName() {
		return _CLUSTER_NAME;
	}

	private Cluster[] execute(Artifact[] initialMedoids) {
		Artifact[] oldMedoids = null;
		medoids = initialMedoids;
		int iter = 1;

		for (int i = 0; i < clusters.length; i++) {
			clusters[i].setMostRepresentative(initialMedoids[i]);
		}

		while (medoidsSwapped(oldMedoids, medoids) && iter > 0) {
			logger.info("Iteration: " + iter);
			oldMedoids = medoids;
//    		if(clusteringMode==ORIGINAL_KMEDOIDS)
			clusters = assignObjectsToClusters(oldMedoids);
//    		else if (clusteringMode == MODIFIED_KMEDOIDS){    			
//    			this.Clusters = assignObjectsToClustersA(oldMedoids);
//        }    		

			medoids = this.getNewMedoids();
			iter -= 1;
		}
		return clusters;
	}

	private Artifact[] getNewMedoids() {
		Artifact[] newMedoids = new Artifact[clusters.length];
		for (int clusterID = 0; clusterID < clusters.length; clusterID++) {
			newMedoids[clusterID] = computeNewMedoid(clusterID);
		}
		return newMedoids;
	}

	private Artifact computeNewMedoid(int clusterID) {
		Artifact newMedoid = null;
		Set<Artifact> tmp = new HashSet<Artifact>();
		// CONTROLLARE COSA FA CLUSTERS
		tmp.addAll(clusters[clusterID].getArtifacts());
		Artifact medoid = clusters[clusterID].getMostRepresentative();
		Set<Artifact> tmp2 = new HashSet<Artifact>();		
		float d = 0.0f;
		float minDistance = 0.0f;

		Map<String, Float> distances = new HashMap<String, Float>();
		distances = readDistanceScores(medoid.getId());
		minDistance = getAverageDistance(distances, tmp);
		newMedoid = medoid;
		tmp.add(medoid);

		for (Artifact object : tmp) {
			tmp2 = new HashSet<Artifact>();
			tmp2.addAll(tmp);
			tmp2.remove(object);
			distances = readDistanceScores(object.getId());
			d = getAverageDistance(distances, tmp2);
			if (d < minDistance) {
				minDistance = d;
				newMedoid = object;
			}
		}
		return newMedoid;
	}

	public float getAverageDistance(Map<String, Float> distances, Set<Artifact> nonMedoids) {
		float sum = 0.0f;
		for (Artifact artifact : nonMedoids) {
			sum += distances.get(artifact.getId());
		}
		float ret = (float) sum / nonMedoids.size();
		return ret;
	}

	private boolean medoidsSwapped(Artifact[] medoids, Artifact[] newMedoids) {
		if (medoids == null || newMedoids == null) {
			return true;
		}
		int length = medoids.length;
		for (int i = 0; i < length; i++) {
			boolean isSameMedoid;
			isSameMedoid = medoids[i].equals(newMedoids[i]);
			if (!isSameMedoid) {
				return true;
			}
		}
		return false;
	}

	private Artifact[] populateRandomMedoids(Cluster[] clusters) {
		List<Artifact> candidateObjects = new ArrayList<Artifact>();
		candidateObjects.addAll(objects);
		Artifact[] medoids = new Artifact[clusters.length];
		for (int i = 0; i < clusters.length; i++) {
			int randElement = (int) Math.floor(Math.random() * candidateObjects.size());
			medoids[i] = candidateObjects.get(randElement);
			clusters[i].setMostRepresentative(medoids[i]);
			candidateObjects.remove(randElement);
		}
		return medoids;
	}

	private Cluster[] assignObjectsToClusters(Artifact[] medoids) {
		int clusterID = 0;

		Set<Artifact> nonMedoids = null;
		Cluster[] tmpClusters = new Cluster[clusters.length];
		Map<String, Float> distance = new HashMap<String, Float>();
		for (int i = 0; i < clusters.length; i++) {
			tmpClusters[i] = new Cluster();
			tmpClusters[i].setArtifacts(new ArrayList<Artifact>());
			tmpClusters[i].setMostRepresentative(medoids[i]);
		}
		Set<Artifact> tmp = new HashSet<Artifact>();
		tmp.addAll(objects);
		for (int i = 0; i < clusters.length; i++)
			tmp.remove(medoids[i]);
		for (Artifact object : tmp) {
			distance = readDistanceScores(object.getId());
			Float[] d = new Float[clusters.length];
			for (int i = 0; i < clusters.length; i++) 
				d[i] = distance.get(medoids[i].getId());
			clusterID = getIndexOfSmallestElement(d);
			if (tmpClusters[clusterID].getMostRepresentative() == null)
				tmpClusters[clusterID].setMostRepresentative(medoids[clusterID]);
			nonMedoids = new HashSet<Artifact>();
			nonMedoids.addAll(tmpClusters[clusterID].getArtifacts());
			nonMedoids.add(object);
			tmpClusters[clusterID].setArtifacts(new ArrayList<Artifact>(nonMedoids));
		}
		return tmpClusters;
	}

	private int getIndexOfSmallestElement(Float[] elements) {
		int length = elements.length;
		float min = elements[0];
		int index = 0;
		for (int i = 0; i < length; i++) {
			if (elements[i] < min) {
				min = elements[i];
				index = i;
			}
		}
		return index;
	}

	private Map<String, Float> readDistanceScores(String object) {
		Map<String, Float> result = new HashMap<String, Float>();
		Query query = new org.springframework.data.mongodb.core.query.Query();
		query.addCriteria(Criteria.where("type.name").is(sm.getSimilarityName())
				.orOperator(Criteria.where("fromArtifact.$id").is(new ObjectId(object)), 
						    Criteria.where("toArtifact.$id").is(new ObjectId(object))));
		DBCollection dbCollection = mongoTemplate.getCollection("relation");
	    DBCursor cursor = dbCollection.find(query.getQueryObject());
	    List<DBObject> list = cursor.toArray();
	    for (DBObject dbObject : list) {
			String toArtifact = ((DBRef)dbObject.get("toArtifact")).getId().toString();
			String fromArtifact = ((DBRef)dbObject.get("fromArtifact")).getId().toString();
			double value = ((double)dbObject.get("value"));
			if (toArtifact.equals(object))  
				result.put(fromArtifact, (float) (1 - value));
			else 
				result.put(toArtifact, (float) (1 - value));
		}
		return result;
	}
}
