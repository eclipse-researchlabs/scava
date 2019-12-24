/*******************************************************************************
 * Copyright (c) 2017 University of L'Aquila
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.scava.repository.model.eclipse.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scava.platform.Platform;
import org.eclipse.scava.platform.logging.OssmeterLogger;
import org.eclipse.scava.repository.model.Company;
import org.eclipse.scava.repository.model.License;
import org.eclipse.scava.repository.model.Person;
import org.eclipse.scava.repository.model.Project;
import org.eclipse.scava.repository.model.Role;
import org.eclipse.scava.repository.model.VcsRepository;
import org.eclipse.scava.repository.model.bts.bugzilla.Bugzilla;
import org.eclipse.scava.repository.model.cc.eclipseforums.EclipseForum;
import org.eclipse.scava.repository.model.cc.mbox.Mbox;
import org.eclipse.scava.repository.model.cc.wiki.Wiki;
import org.eclipse.scava.repository.model.eclipse.Documentation;
import org.eclipse.scava.repository.model.eclipse.EclipseProject;
import org.eclipse.scava.repository.model.eclipse.MailingList;
import org.eclipse.scava.repository.model.github.GitHubBugTracker;
import org.eclipse.scava.repository.model.importer.IImporter;
import org.eclipse.scava.repository.model.importer.dto.Credentials;
import org.eclipse.scava.repository.model.importer.exception.ProjectUnknownException;
import org.eclipse.scava.repository.model.importer.exception.WrongUrlException;
import org.eclipse.scava.repository.model.vcs.cvs.CvsRepository;
import org.eclipse.scava.repository.model.vcs.git.GitRepository;
import org.eclipse.scava.repository.model.vcs.svn.SvnRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EclipseProjectImporter implements IImporter {
	protected OssmeterLogger logger;
	
	private Pattern forumIdParser=Pattern.compile("frm_id=(\\d+)$");
	private String clientSecret="";
	private String clientId="";
	private String githubToken="";

	public EclipseProjectImporter() {
		logger = (OssmeterLogger) OssmeterLogger.getLogger("importer.eclipse ");
	}

	private boolean isNotNull(JSONObject currentProg, String attribute) {
		if (((JSONArray) currentProg.get(attribute)).isEmpty())
			return false;
		else
			return true;
	}

	private boolean isNotNullObj(JSONObject currentProg, String attribute) {
		if (currentProg.get(attribute) != null)
			return true;
		else
			return false;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Import all eclipse project present in the on-line repository.
	 * 
	 * @param platform Scava Platform object
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void importAll(Platform platform) {
		try {
			logger.info("Retrieving the list of Eclipse projects...");

			// InputStream is = new FileInputStream(new
			// File("C:\\eclipse.json"));
			InputStream is = new URL("http://projects.eclipse.org/json/projects/all").openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
			Iterator iter = obj.entrySet().iterator();
			Map.Entry jsonAr = null;
			if (iter.hasNext())
				jsonAr = (Map.Entry) iter.next();

			Iterator iter2 = ((JSONObject) jsonAr.getValue()).entrySet().iterator();

			while (iter2.hasNext()) {
				Map.Entry entry = (Map.Entry) iter2.next();
				platform.getProjectRepositoryManager().getProjectRepository().getProjects()
						.findOneByShortName((String) entry.getKey());
				try {
					importProject((String) entry.getKey(), platform);
				} catch (ProjectUnknownException e) {
					logger.error("Error when try to import poject: " + entry.getKey());
				}
			}
		} catch (IOException e1) {
			logger.error("EclipseProject error: Unable to retrive eclipse project's list");
		}
		logger.info("Importer has finished!");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void importProjects(Platform platform, int numberfOfProjects) {
		try {

			logger.info("Retrieving the list of Eclipse projects...");
			// InputStream is = new FileInputStream(new
			// File("C:\\eclipse.json"));
			InputStream is = new URL("http://projects.eclipse.org/json/projects/all").openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
			Iterator iter = obj.entrySet().iterator();
			Map.Entry jsonAr = null;
			if (iter.hasNext())
				jsonAr = (Map.Entry) iter.next();

			Iterator iter2 = ((JSONObject) jsonAr.getValue()).entrySet().iterator();
			int MAX_ITERATION = 0;
			while (iter2.hasNext() && MAX_ITERATION <= numberfOfProjects) {
				Map.Entry entry = (Map.Entry) iter2.next();
				platform.getProjectRepositoryManager().getProjectRepository().getProjects()
						.findOneByShortName((String) entry.getKey());
				try {
					importProject((String) entry.getKey(), platform);
				} catch (ProjectUnknownException e) {
					e.printStackTrace();
				}
				MAX_ITERATION++;
			}
		} catch (IOException e1) {
			logger.error("EclipseProject error: Unable to retrive eclipse project's list");
		}
		logger.info("Importer has finished!");
	}

	@Override
	public EclipseProject importProject(String projectId, Platform platform) throws ProjectUnknownException {
		return importProject(projectId, platform, true);
	}
	
	private EclipseProject importProject(String projectId, Platform platform, boolean update) throws ProjectUnknownException {
		String JSON_URL_PROJECT = "https://projects.eclipse.org/json/project/" + projectId;

		EclipseProject project = getProject(projectId, platform);
		boolean projectToBeUpdated = (project != null) ? true : false;
		if (!update && projectToBeUpdated) {
			logger.info(projectId + " project already exists. Skipped!");
			return project;
		}
		project = project == null ? new EclipseProject() : project;

		try {
			URL JSON_URL = new URL(JSON_URL_PROJECT);
			URLConnection conn = JSON_URL.openConnection();
			String sorry = conn.getHeaderField("STATUS");

			if (sorry != null && sorry.startsWith("404"))
				throw new WrongUrlException();
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject rootObject = (JSONObject) JSONValue.parse(jsonText);
			JSONObject currentProg = (JSONObject) ((JSONObject) rootObject.get("projects"))
					.get(projectId);
			//logger.info(currentProg);
			project.setName(isNotNullObj(currentProg, "title") ? currentProg.get("title").toString() : null);

			project.setShortName(projectId);
			project.setParagraphUrl(getParagraphUrl(projectId.replaceAll("-", "\\.")));
			if ((isNotNull(currentProg, "description")))
				project.setDescription(
						((JSONObject) ((JSONArray) currentProg.get("description")).get(0)).get("value").toString());
			if((isNotNull(currentProg, "github_repos"))) {
				JSONArray githubRepos = (JSONArray) currentProg.get("github_repos");
				Iterator<JSONObject> iter = githubRepos.iterator();
				GitHubBugTracker bt = null;
				while (iter.hasNext()) {
					bt = new GitHubBugTracker();
					JSONObject entry = (JSONObject) iter.next();
					String prjId = (String) entry.get("url");
					prjId = prjId.replace("https://github.com/", "");
					
					String owner = prjId.split("/")[0];
					String repo = prjId.split("/")[1];
					
					bt.setUrl("https://api.github.com/repos/" + projectId + "/issues");
					logger.info("Creating a GitHub Bug Tracker Reader. Owner: "+owner+" Repository: "+repo);
					{
						if(githubToken.isEmpty())
						{
							logger.info("Creating a GitHub Bug Tracker with no authentication.");
							bt.setProject(owner, repo);
						}
						else
						{
							logger.info("Creating a GitHub Bug Tracker with authentication.");
							bt.setProject(githubToken, owner, repo);
						}
					}
					project.getBugTrackingSystems().add(bt);
					
				}
			}

			if ((isNotNull(currentProg, "download_url")))
				project.setDownloadsUrl(
						((JSONObject) ((JSONArray) currentProg.get("download_url")).get(0)).get("url").toString());

			if ((isNotNull(currentProg, "website_url")))
				project.setHomePage(
						((JSONObject) ((JSONArray) currentProg.get("website_url")).get(0)).get("url").toString());

			if ((isNotNull(currentProg, "plan_url")))
				project.setProjectplanUrl(
						((JSONObject) ((JSONArray) currentProg.get("plan_url")).get(0)).get("url").toString());

			if ((isNotNull(currentProg, "update_sites")))
				project.setUpdatesiteUrl(
						((JSONObject) ((JSONArray) currentProg.get("update_sites")).get(0)).get("url").toString());

			if ((isNotNull(currentProg, "state")))
				project.setState(((JSONObject) ((JSONArray) currentProg.get("state")).get(0)).get("value").toString());

			if (projectToBeUpdated) {
				project.getCommunicationChannels().clear();
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}
			if ((isNotNullObj(currentProg, "dev_list"))) {
				JSONObject devList = (JSONObject) currentProg.get("dev_list");
				
				String url = (String) devList.get("url");
				if(url.startsWith("news://") || url.startsWith("git://") || url.startsWith("svn://"))
				{
					MailingList mailingList = new MailingList();
					mailingList.setName((String) devList.get("name"));
					mailingList.setUrl((String) devList.get("url"));
					mailingList.setNonProcessable(false);
					project.getCommunicationChannels().add(mailingList);
				}
				else
				{
					Mbox mbox = new Mbox();
					mbox.setUrl("https://download.eclipse.org/scava/datasets/eclipse_mls/mboxes/"+(String) devList.get("name"));
					mbox.setMboxName((String) devList.get("name"));
					mbox.setMboxDescription((String) devList.get("email") + "\t"+ url);
					mbox.setCompressedFileExtension(".mbox.gz");
					project.getCommunicationChannels().add(mbox);
				}
			}

			if ((isNotNull(currentProg, "documentation_url"))) {
				JSONArray bugzillaJsonArray = (JSONArray) currentProg.get("documentation_url");
				for (Object object : bugzillaJsonArray) {
					Documentation documentation_url = new Documentation();
					documentation_url.setUrl((String) ((JSONObject) object).get("url"));
					documentation_url.setNonProcessable(true);
					project.getCommunicationChannels().add(documentation_url);
				}
			}

			if ((isNotNull(currentProg, "wiki_url"))) {
				JSONArray bugzillaJsonArray = (JSONArray) currentProg.get("wiki_url");
				for (Object object : bugzillaJsonArray) {
					Wiki wiki = new Wiki();
					String sApp = (String) ((JSONObject) object).get("url");
					wiki.setUrl(sApp);
					wiki.setNonProcessable(true);
					project.getCommunicationChannels().add(wiki);
				}
			}

			if ((isNotNull(currentProg, "mailing_lists"))) {
				JSONArray mailingLists = (JSONArray) currentProg.get("mailing_lists");
				Iterator<JSONObject> iter = mailingLists.iterator();
				Mbox mbox;
				MailingList mailingList;
				String url;
				while (iter.hasNext()) {
					JSONObject entry = (JSONObject) iter.next();
					url=(String) entry.get("url");
					if(url.startsWith("news://") || url.startsWith("git://") || url.startsWith("svn://"))
					{
						mailingList = new MailingList();
						mailingList.setName((String) entry.get("name"));
						mailingList.setUrl((String) entry.get("url"));
						mailingList.setNonProcessable(false);
						project.getCommunicationChannels().add(mailingList);
					}
					else
					{
						mbox = new Mbox();
						mbox.setUrl("https://download.eclipse.org/scava/datasets/eclipse_mls/mboxes/"+(String) entry.get("name"));
						mbox.setMboxName((String) entry.get("name"));
						mbox.setMboxDescription((String) entry.get("email") + "\t"+ url);
						mbox.setCompressedFileExtension(".mbox.gz");
						project.getCommunicationChannels().add(mbox);
					}
					
				}
			}
			if ((isNotNull(currentProg, "forums"))) {
				JSONArray forums = (JSONArray) currentProg.get("forums");
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iter = forums.iterator();
				
				EclipseForum eclipseForum=null;
				URL FORUM_URL;
				URLConnection connForum;
				String forumStatus;
				Matcher forumIdMatch;

				while (iter.hasNext()) {
					JSONObject entry = (JSONObject) iter.next();
					FORUM_URL = new URL((String) entry.get("url"));
					connForum = FORUM_URL.openConnection();
					forumStatus = connForum.getHeaderField("STATUS");
					if (forumStatus != null && forumStatus.startsWith("404"))
						logger.info("The project " + projectId + " does not seem to have a forum");
					else
					{
						forumIdMatch=forumIdParser.matcher(connForum.getURL().getQuery());
						if(!forumIdMatch.find())
							logger.info("The project " + projectId + " does not seem to have a forum");
						else
						{
							eclipseForum = new EclipseForum();
							eclipseForum.setForum_id(forumIdMatch.group(1));
							eclipseForum.setForum_name((String) entry.get("name"));
							eclipseForum.setUrl((String) entry.get("url"));
							eclipseForum.setClient_secret(clientSecret);
							eclipseForum.setClient_id(clientId);
							project.getCommunicationChannels().add(eclipseForum);
						}
					}
				}
			}
			if ((isNotNull(currentProg, "bugzilla"))) {
				JSONArray bugzillaJsonArray = (JSONArray) currentProg.get("bugzilla");
				for (Object object : bugzillaJsonArray) {
					Bugzilla bugzilla = new Bugzilla();
					bugzilla.setComponent((String) ((JSONObject) object).get("component"));
					bugzilla.setCgiQueryProgram((String) ((JSONObject) object).get("query_url"));
					bugzilla.setUrl("https://bugs.eclipse.org/bugs/xmlrpc.cgi");// (String)((JSONObject)object).get("create_url"));
					bugzilla.setProduct((String) ((JSONObject) object).get("product"));
					project.getBugTrackingSystems().add(bugzilla);
				}
			}
			// END Management of Bug Tracking Systems

			// BEGIN Management of Licenses
			if (projectToBeUpdated) {
				project.getLicenses().clear();
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}

			if ((isNotNull(currentProg, "licenses"))) {
				JSONArray licenses = (JSONArray) currentProg.get("licenses");
				Iterator<JSONObject> iter = licenses.iterator();
				License license = null;
				while (iter.hasNext()) {
					JSONObject entry = (JSONObject) iter.next();
					license = platform.getProjectRepositoryManager().getProjectRepository().getLicenses()
							.findOneByName((String) entry.get("name"));
					if (license == null) {
						license = new License();
						license.setName((String) entry.get("name"));
						license.setUrl((String) entry.get("url"));
						platform.getProjectRepositoryManager().getProjectRepository().getLicenses().add(license);
						project.getLicenses().add(license);
					} else {
						license.setUrl((String) entry.get("url"));
					}
					platform.getProjectRepositoryManager().getProjectRepository().sync();
				}
			}
			// END Management of Licenses

			// BEGIN Management of VcsRepositories
			if (projectToBeUpdated) {
				project.getVcsRepositories().clear();
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}

			if ((isNotNull(currentProg, "source_repo"))) {
				JSONArray source_repo = (JSONArray) currentProg.get("source_repo");
				Iterator<JSONObject> iter = source_repo.iterator();
				while (iter.hasNext()) {
					JSONObject entry = (JSONObject) iter.next();
					VcsRepository repository = null;
					if (((String) entry.get("type")).equals("git") || ((String) entry.get("type")).equals("github")) {
						repository = new GitRepository();
						if (((String) entry.get("type")).equals("github"))
							repository.setUrl((String) entry.get("url"));
						if (((String) entry.get("type")).equals("git")) {
//							String gitUrl = (String) entry.get("url");
//							int gitTest = getResponseCode(gitUrl);
//							if (gitTest != 200) {
//								gitUrl = "http://git.eclipse.org" + (String) entry.get("path");
//								gitUrl = gitUrl.replace("http", "https");
//								gitUrl = gitUrl.replace("gitroot", "r");
//								if (gitUrl.endsWith("git"))
//									gitUrl = gitUrl.substring(0, gitUrl.length() - 4);
//								gitTest = getResponseCode(gitUrl);
//								repository.setUrl(gitUrl);
//
//							} else
//								repository.setUrl(gitUrl);
							repository.setUrl("git://git.eclipse.org" +  entry.get("path").toString());

						}
					} else if (((String) entry.get("type")).equals("svn")
							&& ((String) entry.get("path")).startsWith("/")) {
						repository = new SvnRepository();
						String svnUrl = "http://dev.eclipse.org/" + (String) entry.get("path");

						int svnTest = getResponseCode(svnUrl);
						if (svnTest == 200)
							repository.setUrl((String) entry.get("path"));
						if (svnTest != 200) {
							String[] splitString = projectId.split("\\.");
							String gitUrl = null;
							if (splitString != null && splitString.length > 0) {
								String shortName = splitString[splitString.length - 1];
								gitUrl = "https://git.eclipse.org/c/www.eclipse.org/" + shortName + ".git";
							}
							int gitTest = getResponseCode(gitUrl);
							if (gitTest == 200) {
								repository = new GitRepository();
								repository.setUrl(gitUrl);
							}
						}
					} else if (((String) entry.get("type")).equals("cvs")) {
						repository = new CvsRepository();
					}
					if (repository != null) {
						repository.setName((String) entry.get("name"));
					}
					project.getVcsRepositories().add(repository);
				}
			}
			List<Person> ps = getProjectPersons(platform, projectId.replaceAll("-", "\\."));
			for (Person person : ps) {
				project.getPersons().add(person);
			}
			if (!projectToBeUpdated) {
				platform.getProjectRepositoryManager().getProjectRepository().getProjects().add(project);
			}

			if (projectToBeUpdated) {
				project.getCompanies().clear();
				platform.getProjectRepositoryManager().getProjectRepository().sync();
			}
			project.getCompanies().addAll(getCompany(
					projectId.replaceAll("-", "\\."), platform));
			
			platform.getProjectRepositoryManager().getProjectRepository().sync();
			return project;

		} catch (MalformedURLException e) {
			logger.error("MalformedURLException:" + e.getMessage());
		} catch (IOException e) {
			logger.error("IOException:" + e.getMessage());
		} catch (WrongUrlException e) {
			logger.error("WrongUrlException:" + e.getMessage());
		}
		return null;

	}

	private String getParagraphUrl(String name) {
		return "http://www.eclipse.org/" + name + "/project-info/project-page-paragraph.html";
	}

	private List<Person> getProjectPersons(Platform platform, String projectId) {
		List<Person> result = new ArrayList<Person>();
		org.jsoup.nodes.Document doc;
		String URL_PROJECT = "http://projects.eclipse.org/projects/" + projectId + "/who";

		try {
			doc = Jsoup.connect(URL_PROJECT).timeout(10000).get();
			org.jsoup.nodes.Element e = doc.getElementsByClass("field-name-field-projects-members-members").first()
					.getElementsByClass("field-items").first();

			Elements divUsers = e.getElementsByClass("field-item");
			for (Element iterable_element : divUsers) {
				String roleName = iterable_element.getElementsByTag("h3").text();
				Role gr = platform.getProjectRepositoryManager().getProjectRepository().getRoles()
						.findOneByName(roleName);
				if (gr == null) {
					gr = new Role();
					gr.setName("Eclipse " + roleName);
					platform.getProjectRepositoryManager().getProjectRepository().getRoles().add(gr);
					platform.getProjectRepositoryManager().getProjectRepository().sync();
				}
				roleName = iterable_element.getElementsByTag("h3").first().text();

				Elements usersInRole = iterable_element.getElementsByTag("li");
				for (Element element : usersInRole) {
					String username = element.text();
					String url = "http://projects.eclipse.org/" + element.getElementsByAttribute("href").attr("href");
					Person gu = platform.getProjectRepositoryManager().getProjectRepository().getPersons()
							.findOneByName(username);
					if (gu == null) {
						gu = new Person();
						gu.setName(username);
						gu.setHomePage(url);
						platform.getProjectRepositoryManager().getProjectRepository().getPersons().add(gu);
						platform.getProjectRepositoryManager().getProjectRepository().sync();
					}
					gu.getRoles().add(gr);
					result.add(gu);
				}
			}
		} catch (NullPointerException e) {
			logger.error("Problems occurred during the collection of the persons involved in the project " + projectId);
		} catch (IOException e1) {
			logger.error("Problems occurred during the collection of the persons involved in the project " + projectId);
		}
		return result;
	}

	private ArrayList<Company> getCompany(String projectShortName, Platform platform) {
		ArrayList<Company> result = new ArrayList<Company>();
		org.jsoup.nodes.Document doc;

		String URL_PROJECT = "https://projects.eclipse.org/projects/" + projectShortName + "/who";
		try {
			doc = Jsoup.connect(URL_PROJECT).timeout(10000).get();
			Element first = doc.getElementById("block-system-main")
					.getElementsByClass("field-name-field-active-member-companies").first()
					.getElementsByClass("project-active-members").first();
			if (first != null) {
				Elements e = first.getElementsByTag("li");
				for (int i = 0; i < e.size(); i++) {
					String companyUrl = e.get(i).getElementsByTag("a").attr("href");
					Company company = platform.getProjectRepositoryManager().getProjectRepository().getCompanies()
							.findOneByName(companyUrl);
					if (company == null) {
						company = new Company();
						company.setName(companyUrl);
						company.setUrl(companyUrl);
						platform.getProjectRepositoryManager().getProjectRepository().getCompanies().add(company);
						platform.getProjectRepositoryManager().getProjectRepository().sync();
					}

					result.add(company);
				}
			}
			return result;
		} catch (IOException e1) {
			logger.error(projectShortName + " importer failed to load NNTP news group url");
			return new ArrayList<Company>();
		} catch (Exception e) {
			logger.info("The project " + projectShortName + " does not have a company");
		}
		return result;
	}

	private EclipseProject getProject(String projectId, Platform platform) {
		Iterable<Project> pl = platform.getProjectRepositoryManager().getProjectRepository().getProjects()
				.findByShortName(projectId);
		Iterator<Project> iprojects = pl.iterator();

		Project projectTemp = null;
		EclipseProject project = new EclipseProject();
		while (iprojects.hasNext()) {
			projectTemp = iprojects.next();
			if (projectTemp instanceof EclipseProject) {
				project = (EclipseProject) projectTemp;
				logger.info(
						"-----> project " + projectId + " already in the repository. Its metadata will be updated.");
				return project;
			}
		}
		return null;
	}

	private static int getResponseCode(String urlString) throws MalformedURLException, IOException {
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

	/**
	 * Get the html part relative to projects (because the list-of-projects url is
	 * not xhtml->SAX error)
	 * 
	 * @return
	 * @throws WrongUrlException
	 * @throws Exception
	 */

	private String getProjectIdFromUrl(String url) throws WrongUrlException {

		url = url.replace("http://", "");
		url = url.replace("https://", "");
		url = url.replace("www.", "");
		if (url.startsWith("projects.eclipse.org")) {// ||
														// url.startsWith("eclipse.org"))
														// {

			url = url.replace("projects/", "");
			url = url.replace("projects.", "");
			url = url.replace("eclipse.org/", "");
			if (url.contains("?")) {
				url = url.substring(0, url.indexOf("?"));
			}
			if (url.contains("/")) {
				url = url.substring(0, url.indexOf("/"));
			}
			return url;
		} else
			throw new WrongUrlException();
	}

	@Override
	public EclipseProject importProjectByUrl(String url, Platform platform)
			throws WrongUrlException, ProjectUnknownException {
		return importProject(getProjectIdFromUrl(url), platform);
	}

	@Override
	public boolean isProjectInDBByUrl(String url, Platform platform)
			throws WrongUrlException, ProjectUnknownException, MalformedURLException, IOException {
		return isProjectInDB(getProjectIdFromUrl(url), platform);
	}

	@Override
	public boolean isProjectInDB(String projectId, Platform platform)
			throws ProjectUnknownException, MalformedURLException, IOException {
		Iterable<Project> pl = platform.getProjectRepositoryManager().getProjectRepository().getProjects()
				.findByShortName(projectId);
		Iterator<Project> iprojects = pl.iterator();
		Project projectTemp = null;
		while (iprojects.hasNext()) {
			projectTemp = iprojects.next();
			if (projectTemp instanceof EclipseProject) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setCredentials(Credentials credentials) {
		if(credentials.getCredentialsId().equals("eclipseForums"))
		{	
			if(!credentials.getAuthToken().equals("") || credentials.getAuthToken() != null)
				clientSecret=credentials.getAuthToken();
			if(!credentials.getUsername().equals("") || credentials.getUsername()!=null)
				clientId=credentials.getUsername();
		}
		if(credentials.getCredentialsId().equals("github"))
		{
			if(!credentials.getAuthToken().equals("") || credentials.getAuthToken() != null)
				githubToken= credentials.getAuthToken();
		}
	}
}
