/**
 * 
 */
package org.eclipse.scava.crossflow.examples.github.topsearch;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class SearchRepoAuthorCounter extends SearchRepoAuthorCounterBase {

	
	protected final int MAX_NUMBER_OF_COMMITMENTS = 999999;
	
	protected Set<String> alreadySeenJobs = new HashSet<String>();
	
	// < repository-url, number-of-repository-occurrence >
	protected Map<String, Integer> committedRepoMap = new HashMap<String, Integer>(); 
	
	/**
	 * 
	 */
	public SearchRepoAuthorCounter() {
		// do nothing
	}

	private int count(String repoLocation) {	
		System.out.println("count ( " + repoLocation + " )");
		HashSet<String> repoAuthorsSet = new HashSet<String>();
		
		try {
			Repository repo = new FileRepository(new File(repoLocation + "/.git").getCanonicalPath());
			
			// get a list of all known heads, tags, remotes, ...
            Collection<Ref> allRefs = repo.getAllRefs().values();

            // a RevWalk allows to walk over commits based on some filtering that is defined
            try (RevWalk revWalk = new RevWalk( repo )) {
                for( Ref ref : allRefs ) {
                    revWalk.markStart( revWalk.parseCommit( ref.getObjectId() ));
                }
                System.out.println("Walking all commits starting with " + allRefs.size() + " refs: " + allRefs);
                for( RevCommit commit : revWalk ) {
                	repoAuthorsSet.add(commit.getAuthorIdent().getEmailAddress());
                }
            }
		
		} catch (IOException e) {
			System.err.println("\n" + "[" + workflow.getName() + "] " + "Failed to count repository authors. Wrong cloned repository location?");
			e.printStackTrace();
		}	 
		
		return repoAuthorsSet.size();
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
	
	public static void main(String args[]) throws IOException {
		SearchRepoAuthorCounter counter = new SearchRepoAuthorCounter();
		String repoLocation = "/Users/blizzfire/REPOS/CROSSMINER-REPOS/CROSSMINER-PUBLIC/scava/crossflow/org.eclipse.scava.crossflow.examples/../../../CLONED-REPOS/javascript-8213ab1e4140c89a1d7ee5a5efcfd343312de521";
		System.out.println(new File(repoLocation).getCanonicalPath());
		int count = counter.count(repoLocation);
		System.out.println("COUNT: " + count);
	}

	@Override
	public OwnerRepoAuthorCountTuple consumeSearchClonedRepoEntries(OwnerRepoUrlTuple ownerRepoUrlTuple)
			throws Exception {

		OwnerRepoAuthorCountTuple ownerRepoAuthorCountTuple = null;
		
		if ( committedRepoMap.size() == MAX_NUMBER_OF_COMMITMENTS ) {
			// do not commit to any more repositories - sending back
			workflow.getSearchClonedRepoEntries().send( ownerRepoUrlTuple ,this.getClass().getName());
		
		} else {
			// We still have space left for repositories to commit to - considering it
			if ( alreadySeenJobs.contains( ownerRepoUrlTuple.getId() ) ) { 
				// We've seen this job before - assume no-one else wants it
				committedRepoMap.put( ownerRepoUrlTuple.getRepoRemote(), 0 );
			
			} else {
				// We haven't seen this job before
				// Record it and send it back
				alreadySeenJobs.add( ownerRepoUrlTuple.getId() );
				workflow.getSearchClonedRepoEntries().send( ownerRepoUrlTuple ,this.getClass().getName());
			}
			
			if ( committedRepoMap.containsKey( ownerRepoUrlTuple.getRepoRemote() ) ) {
				
				committedRepoMap.replace( ownerRepoUrlTuple.getRepoRemote(), committedRepoMap.get( ownerRepoUrlTuple.getRepoRemote()) + 1 );
				
				int authorCount = count(ownerRepoUrlTuple.getRepoLocal());
				
				ownerRepoAuthorCountTuple = new OwnerRepoAuthorCountTuple();
				ownerRepoAuthorCountTuple.setRepoOwner(ownerRepoUrlTuple.getRepoOwner()); // GitHub owner (user name or organisation)
				ownerRepoAuthorCountTuple.setRepoRemote(ownerRepoUrlTuple.getRepoRemote()); // GitHub repository name
				ownerRepoAuthorCountTuple.setAuthorCount(authorCount); // repository unique author count
				
			}
			
		}
		
		return ownerRepoAuthorCountTuple;
		
	}

}
