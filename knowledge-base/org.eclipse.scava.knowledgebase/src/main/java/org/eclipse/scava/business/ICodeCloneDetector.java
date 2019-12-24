package org.eclipse.scava.business;

import java.io.IOException;

import org.eclipse.scava.business.dto.ApiCallResult;

import com.harukizaemon.simian.Options;

public interface ICodeCloneDetector {
	ApiCallResult checkClone(String left, String right, Options option) throws IOException;


}
