package org.eclipse.scava.crossflow.examples.github.topsearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.scava.crossflow.runtime.utils.CloneUtils;

public class SearchRepoCloner extends SearchRepoClonerBase {
	
protected Set<String> alreadySeenJobs = new HashSet<String>();
	
	// < repository-url, number-of-repository-occurrence >
	protected Map<String, Integer> committedRepoMap = new HashMap<String, Integer>(); 
	
	/**
	 * 
	 */
	public SearchRepoCloner() {
		// do nothing
	}

	/**
	 * @return the alreadySeenJobs
	 */
	public Set<String> getAlreadySeenJobs() {
		return alreadySeenJobs;
	}

	/**
	 * @return the committedRepoMap
	 */
	public Map<String, Integer> getCommittedRepoMap() {
		return committedRepoMap;
	}
	
	public String cloneRepo(String owner, String repo, boolean replace) {
		String ghRepoUrl = "https://github.com/" + owner + "/" + repo;
		final String cloneSource = ghRepoUrl + ".git";
		
		final File cloneRepoDestination = new File(SearchRepoProperties.CLONE_PARENT_DESTINATION + File.separator
				+ CloneUtils.getUniqueRepoFolderName(ghRepoUrl));
		
		System.out.print("\n" + "[" + workflow.getName() + "] " + "Cloning Git repository " + cloneSource + " to " + cloneRepoDestination + " ... ");
			
		if (!cloneRepoDestination.exists()) {
			
			try {
				Files.createDirectories(cloneRepoDestination.toPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
//		NOTE: "git clone --depth 1" does not provide enough commit history -- using JGit cloning by default
//			try {
//				// Try the command-line option first as it supports --depth 1
//				String cloneCmd = "git clone --depth 1 " + cloneSource + " " + cloneRepoDestination.getAbsolutePath();
//				System.out.println("Running: " + cloneCmd);
//				Process process = Runtime.getRuntime().exec(cloneCmd);
//				process.waitFor();
//				
//			}
//			catch (Exception ex) {
//				System.out.println("Falling back to JGit because " + ex.getMessage());
				try {
					Git.cloneRepository()
						.setURI( cloneSource )
						.setDirectory(cloneRepoDestination)
						.call();
				} catch (InvalidRemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		}
		
		return cloneRepoDestination.getAbsolutePath();
	}
	
	@Override
	public OwnerRepoUrlTuple consumeSearchRepos(OwnerRepoTuple ownerRepoTuple) throws Exception {
		OwnerRepoUrlTuple ownerRepoUrlTuple = null;
		
		System.out.println("SearchRepoCloner.consumeSearchRepos( " + ownerRepoTuple + " )");
		if ( committedRepoMap.size() == SearchRepoProperties.MAX_NUMBER_OF_COMMITMENTS ) {
			// do not commit to any more repositories - sending back
			workflow.getSearchRepos().send( ownerRepoTuple, this.getClass().getName() );
		
		} else {
			// We still have space left for repositories to commit to - considering it
			if ( alreadySeenJobs.contains( ownerRepoTuple.getId() ) ) { 
				// We've seen this job before - assume no-one else wants it
				committedRepoMap.put( ownerRepoTuple.getRepoRemote(), 0 );
			
			} else {
				// We haven't seen this job before
				// Record it and send it back
				alreadySeenJobs.add( ownerRepoTuple.getId() );
				workflow.getSearchRepos().send( ownerRepoTuple, this.getClass().getName() );
			}
			
			if ( committedRepoMap.containsKey( ownerRepoTuple.getRepoRemote() ) ) {
				committedRepoMap.replace( ownerRepoTuple.getRepoRemote(), committedRepoMap.get( ownerRepoTuple.getRepoRemote()) + 1 );
//				System.out.println("[" + workflow.getName() + "] " + committedRepoMap.get( ownerRepoTuple.getField1() ) + " occurrences of " + ownerRepoTuple.getField1() );
								
				String clonedRepoLocation = cloneRepo(ownerRepoTuple.getRepoOwner(), ownerRepoTuple.getRepoRemote(), false);
				
				ownerRepoUrlTuple = new OwnerRepoUrlTuple();
				ownerRepoUrlTuple.setRepoOwner(ownerRepoTuple.getRepoOwner()); // github owner
				ownerRepoUrlTuple.setRepoRemote(ownerRepoTuple.getRepoRemote()); // github repo
				ownerRepoUrlTuple.setRepoLocal(clonedRepoLocation); // cloned repository local path

				sendToSearchClonedRepoEntries(ownerRepoUrlTuple);

			}
			
		}
		
		return ownerRepoUrlTuple;
	}

}