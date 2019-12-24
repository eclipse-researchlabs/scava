/*******************************************************************************
 * Copyright (C) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.test.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.scava.business.IAggregatedSimilarityCalculator;
import org.eclipse.scava.business.ISimilarityCalculator;
import org.eclipse.scava.business.ISimilarityManager;
import org.eclipse.scava.business.impl.ClaraClulsterCalulator;
import org.eclipse.scava.business.impl.ClusterManager;
import org.eclipse.scava.business.impl.HierarchicalClulsterCalulator;
import org.eclipse.scava.business.impl.KmeansClulsterCalulator;
import org.eclipse.scava.business.impl.OssmeterImporter;
import org.eclipse.scava.business.integration.ArtifactRepository;
import org.eclipse.scava.business.integration.ClusterRepository;
import org.eclipse.scava.business.integration.ClusterizationRepository;
import org.eclipse.scava.business.integration.GithubUserRepository;
import org.eclipse.scava.business.integration.RelationRepository;
import org.eclipse.scava.business.model.Artifact;
import org.eclipse.scava.business.model.Cluster;
import org.eclipse.scava.business.model.Clusterization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.properties")
public class ClusterManagerTest {

	@Autowired
	private ClusterRepository clusterRepository;
	
	@Autowired
	private ClusterizationRepository clusterizationRepository;
	
	@Autowired
	private RelationRepository relationRepository;
	
	@Autowired
	@Qualifier ("Dependency")
	private ISimilarityCalculator simDependencyCalculator;
	

	
	@Autowired
	private HierarchicalClulsterCalulator hierchical;
	
	@Autowired
	private KmeansClulsterCalulator kmedoids;
	
	@Autowired
	private ClaraClulsterCalulator clara;
	
	@Autowired
	private ArtifactRepository artifactRepository;
	
	@Autowired
	private ISimilarityManager similarityManager;
	
	@Autowired
	private ClusterManager clusterManager;

	@Autowired
	OssmeterImporter ossmeterImporter;
	
	@Autowired
	private GithubUserRepository githubUserRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(SimilarityManagerTest.class);
	private List<Artifact> artifacts;
	@Before
	public void testCreateAndStoreDistanceMatrix() {
		artifactRepository.deleteAll();
		githubUserRepository.deleteAll();
		relationRepository.deleteAll();
		clusterRepository.deleteAll();
		clusterizationRepository.deleteAll();
		try {
			ObjectMapper mapper = new ObjectMapper();
			Resource resource = new ClassPathResource("artifacts.json");
			InputStream resourceInputStream = resource.getInputStream();
			artifacts = mapper.readValue(resourceInputStream, new TypeReference<List<Artifact>>(){});
			artifactRepository.save(artifacts);
			for (Artifact artifact : artifacts) {
				ossmeterImporter.storeGithubUser(artifact.getStarred(), artifact.getFullName());
				ossmeterImporter.storeGithubUserCommitter(artifact.getCommitteers(), artifact.getFullName());
			} 
			resourceInputStream.close();
			
			similarityManager.createAndStoreDistanceMatrix(simDependencyCalculator);
			assertEquals(((artifacts.size() * (artifacts.size() -1))/2), 
					relationRepository.findAllByTypeName(simDependencyCalculator.getSimilarityName()).size());
			clusterManager.calculateAndStoreClusterization(simDependencyCalculator, clara);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	@Before
	public void dispose(){
		artifactRepository.deleteAll();
		githubUserRepository.deleteAll();
		clusterRepository.deleteAll();
		relationRepository.deleteAll();
		clusterizationRepository.deleteAll();
	}
	
	
	@Test
	public void testGetClusters() throws Exception {
		List<Cluster> clusters = clusterManager.getClusters(simDependencyCalculator, clara);
		assertNotNull(clusters);
		assertNotEquals(clusters.size(), 0);		
	}

	@Test
	public void testGetClusterFromArtifact() {
		Cluster cluster = clusterManager.getClusterFromArtifact(artifacts.get(0), simDependencyCalculator, clara);
		assertNotNull(cluster);
	}

	@Test
	public void testDeleteClusterization() {
		Clusterization clusterization = clusterManager.getClusterizationBySimilarityMethodLastDate(simDependencyCalculator, clara);
		clusterManager.deleteClusterization(clusterization);
	}

	@Test
	public void testGetOneByArtifactsName() {
		Cluster cluster = clusterManager.getOneByArtifactsName("AlipayOrdersSupervisor-GUI", simDependencyCalculator, clara);
		assertNotNull(cluster);
	}

	@Test
	public void testGetClusterizationBySimilarityMethodLastDate() {
		Clusterization clusterization = clusterManager.getClusterizationBySimilarityMethodLastDate(simDependencyCalculator, clara);
		assertNotNull(clusterization);
	}

	@Test
	public void testGetClusterByArtifactsIdAndClusterizationId() {
		Clusterization clusterization = clusterManager.getClusterizationBySimilarityMethodLastDate(simDependencyCalculator, clara);
		Cluster cluster = clusterManager.getClusterByArtifactsIdAndClusterizationId(artifacts.get(0).getId(),
				clusterization.getId());
		assertNotNull(cluster);
	}

}
