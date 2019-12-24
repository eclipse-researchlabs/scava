/*******************************************************************************
 * Copyright (c) 2018 University of York
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.platform.communicationchannel.nntp;

import java.io.BufferedReader;
import java.io.Reader;

public class ArticleHeader {
	
	protected String subject = "";
	protected String sender = "";
	protected String date = "";
	protected String newsgroups = "";
	
	public String getNewsgroups() {
		return newsgroups;
	}

	public void setNewsgroups(String newsgroups) {
		this.newsgroups = newsgroups;
	}

	public ArticleHeader(Reader reader) {
		read(reader);
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	protected void read(Reader r) {
		try {
			BufferedReader br = new BufferedReader(r);
			String line = br.readLine();
			while (line != null) {
				if (line.indexOf("Subject:") != -1) {
					subject = line.substring(line.indexOf(':') + 1);
				}
				else if (line.indexOf("From:") != -1) {
					sender = line.substring(line.indexOf(':') + 1);
				}
				else if (line.indexOf("Date:") != -1) {
					date = line.substring(line.indexOf(':') + 1);
				}
				else if (line.indexOf("Newsgroups:") != -1) {
					newsgroups = line.substring(line.indexOf(':') + 1);
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
 
