/*******************************************************************************
 * Copyright (c) 2017 Centrum Wiskunde & Informatica
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.vcs.workingcopy.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.scava.platform.logging.OssmeterLogger;
import org.eclipse.scava.platform.vcs.workingcopy.manager.Churn;
import org.eclipse.scava.platform.vcs.workingcopy.manager.WorkingCopyCheckoutException;
import org.eclipse.scava.platform.vcs.workingcopy.manager.WorkingCopyManager;
import org.eclipse.scava.repository.model.VcsRepository;
import org.eclipse.scava.repository.model.vcs.git.GitRepository;

public class GitWorkingCopyManager implements WorkingCopyManager {
  private Logger loggerOssmeter = OssmeterLogger.getLogger("GitWorkingCopyManager");

  public GitWorkingCopyManager() {
    // DO NOT REMOVE
  }

  @Override
  public boolean appliesTo(VcsRepository repository) {
    return repository instanceof GitRepository;
  }

  @Override
  public synchronized void checkout(File workingDirectory, VcsRepository repository, String revision)
      throws WorkingCopyCheckoutException {
    try {
      loggerOssmeter.info("Checkout " + repository.getUrl() + " in revision " + revision);
      Instant start = Instant.now();
      if (workingDirectory.exists()) {
        // we assume we did it before and will now just pull
        Process p = Runtime.getRuntime().exec(new String[] { "git", "pull"}, new String[] { }, workingDirectory);
        if (!p.waitFor(5, TimeUnit.MINUTES)) {
          loggerOssmeter.error("Couldn't complete git pull in 5 minutes. Killing and retrying...");
          p.destroy();
          checkout(workingDirectory, repository, revision);
        }
      }
      else {
        // we clone
        Process p = Runtime.getRuntime().exec(new String[] { "git", "clone", repository.getUrl(), workingDirectory.getAbsolutePath() });
        p.waitFor();
      }
      Process p = Runtime.getRuntime().exec(new String[] {"git", "checkout", revision }, null, workingDirectory);
      p.waitFor();
      Instant end = Instant.now();
      loggerOssmeter.info("Checkout completed in " + Duration.between(start, end).getSeconds() + " seconds.");
    } catch (IOException | InterruptedException e) {
      throw new WorkingCopyCheckoutException(repository, revision, e);
    }
  }

  @Override
  public synchronized List<Churn> getDiff(File workingDirectory, String lastRevision) {
	  List<Churn> result = new ArrayList<>();
	  List<String> commandArgs = new ArrayList<>(Arrays.asList(new String[] { "git", "show", "--numstat" }));
	  
	  if (lastRevision != null) {
		  commandArgs.add(2, lastRevision);
	  }
		try {
		  
		  /* 
		   * this little workaround makes sure the indexes we get for the diffs is in the form
		   * workingCopyRoot+"/"+itemPath (relative - in the sense how I made it relative in the SVNManager)
		   */
		  for (String path: workingDirectory.list()) {
			  // I hate this!!! :(
			  if (!path.contains(".DS_Store")) {
				  commandArgs.add(path);
			  }
		  }
		  ProcessBuilder pb = new ProcessBuilder(commandArgs);
		  pb.redirectErrorStream(true);
		  pb.directory(workingDirectory);
		  final Process p = pb.start();
		  
		  try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			  String line;
			  while ((line = reader.readLine()) != null) {
				  String[] lineParts = line.split("\\s+");
				  if (lineParts.length == 3 && lineParts[0].matches("\\d+") && lineParts[1].matches("\\d+")) {
					  int addedLines = Integer.parseInt(lineParts[0]);
					  int deletedLines = Integer.parseInt(lineParts[1]);
					  result.add(new Churn(lineParts[2], addedLines, deletedLines));
				  } 
			  }
		  } 
		  catch (IOException e) {
			  throw new RuntimeException(e);
		  }
		  p.waitFor();
		} 
		catch (IOException | InterruptedException e) {
		  throw new RuntimeException(e);
		}
		return result;
  }
}
