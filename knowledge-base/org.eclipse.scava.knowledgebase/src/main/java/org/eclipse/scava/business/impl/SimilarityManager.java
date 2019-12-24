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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.eclipse.scava.business.IAggregatedSimilarityCalculator;
import org.eclipse.scava.business.ISimilarityCalculator;
import org.eclipse.scava.business.ISimilarityManager;
import org.eclipse.scava.business.ISingleSimilarityCalculator;
import org.eclipse.scava.business.integration.ArtifactRepository;
import org.eclipse.scava.business.integration.RelationRepository;
import org.eclipse.scava.business.integration.RelationTypeRepository;
import org.eclipse.scava.business.model.Artifact;
import org.eclipse.scava.business.model.Relation;
import org.eclipse.scava.business.model.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

/**
 * @author Juri Di Rocco
 *
 */
@Service
public class SimilarityManager implements ISimilarityManager {

	private static final Logger logger = LoggerFactory.getLogger(SimilarityManager.class);
	@Autowired
	private ArtifactRepository artifactRepository;

	@Autowired
	private RelationRepository relationRepository;

	@Autowired
	private RelationTypeRepository relationTypeRepository;

	@Autowired
	MongoOperations mongoOperations;
	
	
	List<ISimilarityCalculator> simCalcs;
	@Autowired
	private ReadmeSimilarityCalculator readme;
	@Autowired
	private CROSSRecSimilarityCalculator crossRec;
	@Autowired
	private CROSSSimSimilarityCalculator crossSim;
	@Autowired
	private DependencySimilarityCalculator dependendy;
	@Autowired
	private CompoundSimilarityCalculator compound;
	
	@Override
	public void storeSimilarityDistances() {
		simCalcs = new ArrayList();
		simCalcs.add(readme);
		simCalcs.add(crossRec);
		simCalcs.add(crossSim);
		simCalcs.add(dependendy);
		simCalcs.add(compound);
		storeSimilarityDistances(simCalcs);
	}
	
	@Override
	public void storeSimilarityDistances(List<ISimilarityCalculator> simCalcs) {
		for (ISimilarityCalculator similarityCalculator : simCalcs) {
			try {
				logger.info("Computing distance matrix for: {}", similarityCalculator.getSimilarityName());
				createAndStoreDistanceMatrix(similarityCalculator);
				logger.info("Computed distance matrix for: {}", similarityCalculator.getSimilarityName());
			}
			catch (Exception e) {
				logger.error("error when computing {} distances: {}", similarityCalculator.getSimilarityName(), e.getMessage());
			}
		}
	}
	
	@Override
	public Set<Relation> getSimilarProjectsRelations(Artifact prj1, ISimilarityCalculator similarityCalculator) {
		Query q1 = new Query(Criteria.where("type.name").is(similarityCalculator.getSimilarityName()).orOperator(
				Criteria.where("toArtifact.$id").is(new ObjectId(prj1.getId())),
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj1.getId()))));
		q1.with(new Sort(Sort.Direction.DESC, "value"));
		List<Relation> r1 = mongoOperations.find(q1, Relation.class);
		return new HashSet<>(r1);
	}

	private RelationType getRelationType(String name) {
		RelationType rt = relationTypeRepository.findOneByName(name);
		if (rt != null)
			return rt;
		rt = new RelationType();
		rt.setName(name);
		relationTypeRepository.save(rt);
		return rt;
	}

	@Override
	public List<Artifact> getSimilarProjects(Artifact prj1, double threshold) {
		Query q1 = new Query(Criteria.where("value").gt(threshold).orOperator(
				Criteria.where("toArtifact.$id").is(new ObjectId(prj1.getId())),
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj1.getId()))));
		q1.with(new Sort(Sort.Direction.DESC, "value"));
		List<Relation> r1 = mongoOperations.find(q1, Relation.class);
		List<Artifact> results = new ArrayList<>();
		for (Relation rel : r1) {
			if (rel.getFromProject().getId().equals(prj1.getId()))
				results.add(rel.getToProject());
			else
				results.add(rel.getFromProject());
		}
		return results;
	}

	// TODO add hashmap
	/*
	 * @param simCalculator If it is null, method uses SimRank Algorithm
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.scava.business.ISimilarityManager#createAndStoreDistanceMatrix(org.scava.
	 * business.ISimilarityCalculator)
	 */
	@Override
	public void createAndStoreDistanceMatrix(ISimilarityCalculator simCalculator) {
		List<Relation> dbd = relationRepository.findAllByTypeName(simCalculator.getSimilarityName());
		logger.info("#Reletaion to be deleted {}", dbd.size());
		relationRepository.delete(dbd);
		storeDistanceMatrix(createDistanceMatrix(simCalculator), simCalculator);
	}

	public Map<String, Double> getSimilarProjectsOnLine(ISimilarityCalculator simCalculator, Artifact art) {
		List<Artifact> artifacts = appliableProjects(simCalculator);
		artifacts.add(art);
		if (simCalculator instanceof ISingleSimilarityCalculator) {
			ISingleSimilarityCalculator singleCalculator = (ISingleSimilarityCalculator) simCalculator;
			Artifact[] artifactsArray = new Artifact[artifacts.size()];
			artifactsArray = artifacts.toArray(artifactsArray);
			Map<String, Double> p = Maps.newHashMap();
			for (int i = 0; i < artifactsArray.length - 1; i++) {
				double similarity = 0;
				try {
					similarity = singleCalculator.calculateSimilarity(artifactsArray[i], art);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				p.put(artifactsArray[i].getFullName(), similarity);
			}
			return p;
		} else {
			IAggregatedSimilarityCalculator aggregateSimilarityCalculator = (IAggregatedSimilarityCalculator) simCalculator;
			Map<String, String> map = new HashMap<>();
			map.put("committers", "true");
			map.put("deps", "true");
			map.put("stargazers", "true");
			map.put("freqDeps", "129");
			Table<String, String, Double> distanceMatrix = aggregateSimilarityCalculator
					.calculateAggregatedSimilarityValues(artifacts, map);
			Map<String, Double> p = distanceMatrix.column(art.getFullName());
			p.putAll(distanceMatrix.row(art.getFullName()));
			return p;

		}
	}

	public Table<String, String, Double> createDistanceMatrix(ISimilarityCalculator simCalculator) {
		List<Artifact> artifacts = appliableProjects(simCalculator);
		if (simCalculator instanceof ISingleSimilarityCalculator) {
			ISingleSimilarityCalculator singleCalculator = (ISingleSimilarityCalculator) simCalculator;
			Table<String, String, Double> distanceMatrix = HashBasedTable.create();
			Artifact[] artifactsArray = new Artifact[artifacts.size()];
			artifactsArray = artifacts.toArray(artifactsArray);
			for (int i = 0; i < artifactsArray.length - 1; i++) {
				for (int j = i + 1; j < artifactsArray.length; j++) {
					double similarity = 0;
					try {
						similarity = singleCalculator.calculateSimilarity(artifactsArray[i], artifactsArray[j]);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					distanceMatrix.put(artifactsArray[i].getFullName(), artifactsArray[j].getFullName(), similarity);

				}
				if (i % 10 == 0)  logger.info("Computing {} of {} similarity", i, artifactsArray.length);
			}
			return distanceMatrix;
		} else {
			IAggregatedSimilarityCalculator aggregateSimilarityCalculator = (IAggregatedSimilarityCalculator) simCalculator;
			Map<String, String> map = new HashMap<>();
			map.put("committers", "true");
			map.put("deps", "true");
			map.put("stargazers", "true");
			map.put("freqDeps", "129");
			Table<String, String, Double> distanceMatrix = aggregateSimilarityCalculator
					.calculateAggregatedSimilarityValues(artifacts, map);
			return distanceMatrix;

		}
	}

	public void storeDistanceMatrix(Table<String, String, Double> distanceMatrix, ISimilarityCalculator simCalculator) {
		List<Artifact> artifacts = appliableProjects(simCalculator);
		Artifact[] artifactsArray = new Artifact[artifacts.size()];
		artifactsArray = artifacts.toArray(artifactsArray);
		for (int i = 0; i < artifactsArray.length - 1; i++) {
			for (int j = i + 1; j < artifactsArray.length; j++) {
				double similarity = 0;
				try {
					if (distanceMatrix.contains(artifactsArray[i].getFullName(), artifactsArray[j].getFullName()))
						similarity = distanceMatrix.get(artifactsArray[i].getFullName(),
								artifactsArray[j].getFullName());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				RelationType relType = getRelationType(simCalculator.getSimilarityName());
				Relation rel = new Relation();
				rel.setType(relType);
				rel.setFromProject(artifactsArray[i]);
				rel.setToProject(artifactsArray[j]);
				rel.setValue(similarity);
				relationRepository.save(rel);
			}
		}

	}

	@Override
	public Table<String, String, Double> getDistanceMatrix(ISimilarityCalculator simCalculator) {
		List<Artifact> arts = artifactRepository.findAll();
		Table<String, String, Double> result = HashBasedTable.create();
		int count = 1;
		for (Artifact artifact : arts) {

			List<Relation> relList = relationRepository.findByToArtifactIdAndTypeName(new ObjectId(artifact.getId()),
					simCalculator.getSimilarityName());
			logger.info("Relations are extracted: " + count);
			count++;
			for (Relation relation : relList) {
				result.put(relation.getFromProject().getId(), relation.getToProject().getId(), relation.getValue());
			}
			artifactRepository.findAll().forEach(z -> result.put(z.getId(), z.getId(), 1.0));
		}
		return result;
	}

	@Override
	public Relation getRelation(Artifact prj1, Artifact prj2, ISimilarityCalculator simCalculator) {
		Query q2 = new Query(Criteria.where("type.name").is(simCalculator.getSimilarityName()).orOperator(
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj2.getId())).and("toArtifact.$id")
						.is(new ObjectId(prj1.getId())),
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj1.getId())).and("toArtifact.$id")
						.is(new ObjectId(prj2.getId()))));
		return mongoOperations.findOne(q2, Relation.class);

	}

	@Override
	public void deleteRelations(ISimilarityCalculator simCalculator) {
		Query q2 = new Query(Criteria.where("type.name").is(simCalculator.getSimilarityName()));
		List<Relation> arts = mongoOperations.find(q2, Relation.class);
		relationRepository.delete(arts);

	}

	@Override
	public List<Relation> getRelations(ISimilarityCalculator simCalculator) {
		Query q2 = new Query(Criteria.where("type.name").is(simCalculator.getSimilarityName()));
		return mongoOperations.find(q2, Relation.class);

	}

	@Override
	public List<Artifact> getSimilarProjects(Artifact prj1, ISimilarityCalculator similarityCalculator, int numResult) {

		Query q1 = new Query(Criteria.where("type.name").is(similarityCalculator.getSimilarityName()).orOperator(
				Criteria.where("toArtifact.$id").is(new ObjectId(prj1.getId())),
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj1.getId()))));
		q1.with(new Sort(Sort.Direction.DESC, "value"));
		q1.limit(numResult);
		List<Relation> r1 = mongoOperations.find(q1, Relation.class);
		List<Artifact> results = new ArrayList<>();
		for (Relation rel : r1) {
			if (rel.getFromProject().getId().equals(prj1.getId()))
				// results.add(rel.getToProject());
				results.add(artifactRepository.findOne(rel.getToProject().getId()));
			else
				// results.add(rel.getFromProject());
				results.add(artifactRepository.findOne(rel.getFromProject().getId()));
		}
		return results;
	}

	@Override
	public List<Artifact> getSimilarProjects(Artifact prj1, int numResult) {
		Query q1 = new Query(new Criteria().orOperator(Criteria.where("toArtifact.$id").is(new ObjectId(prj1.getId())),
				Criteria.where("fromArtifact.$id").is(new ObjectId(prj1.getId()))));
		q1.with(new Sort(Sort.Direction.DESC, "value"));
		q1.limit(numResult);
		List<Relation> r1 = mongoOperations.find(q1, Relation.class);
		List<Artifact> results = new ArrayList<>();
		for (Relation rel : r1) {
			if (rel.getFromProject().getId().equals(prj1.getId()))
				results.add(rel.getToProject());
			else
				results.add(rel.getFromProject());
		}
		return results;
	}

	@Override
	public List<Artifact> appliableProjects(ISimilarityCalculator simCalculator) {
		return artifactRepository.findAll().stream().filter(z -> simCalculator.appliesTo(z))
				.collect(Collectors.toList());
	}

}
