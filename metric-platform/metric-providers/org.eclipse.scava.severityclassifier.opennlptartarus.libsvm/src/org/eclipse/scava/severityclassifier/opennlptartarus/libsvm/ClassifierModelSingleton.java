/*******************************************************************************
 * Copyright (c) 2019 Edge Hill University
 * Copyright (c) 2017 University of Manchester
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.severityclassifier.opennlptartarus.libsvm;

import org.eclipse.scava.libsvm.svm_predict_nofiles;
import org.eclipse.scava.platform.logging.OssmeterLogger;

import libsvm.svm_model;


class ClassifierModelSingleton {

	private static ClassifierModelSingleton singleton = new ClassifierModelSingleton( );
	private svm_model model;
	protected OssmeterLogger logger;
	
	/* A private Constructor prevents any other 
	 * class from instantiating.
	 */
	private ClassifierModelSingleton(){
		logger = (OssmeterLogger) OssmeterLogger.getLogger("severityclassifier.opennlptartarus.libsvm");
		model = svm_predict_nofiles.parse_args_and_load_model(getClass(), "classifierFiles", "Test-ThreadClustering-5.m", logger);
    }
	   
	/* Static 'instance' method */
	public static ClassifierModelSingleton getInstance( ) {
		return singleton;
	}
	
	/* Other methods protected by singleton-ness */
	public svm_model getModel( ) {
		return model;
	}
	
}
