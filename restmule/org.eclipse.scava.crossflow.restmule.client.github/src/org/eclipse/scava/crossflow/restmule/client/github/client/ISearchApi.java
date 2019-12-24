package org.eclipse.scava.crossflow.restmule.client.github.client;

import org.eclipse.scava.crossflow.restmule.core.data.IDataSet;
import org.eclipse.scava.crossflow.restmule.client.github.model.*;

public interface ISearchApi {
	
	/**
	 * null
	 * @param order The sort field. if sort param is provided. Can be either asc or desc.
	 * @param q The search terms. This can be any combination of the supported user
	            search parameters:
	            'Search In' Qualifies which fields are searched. With this qualifier you
	            can restrict the search to just the username, public email, full name,
	            location, or any combination of these.
	            'Repository count' Filters users based on the number of repositories they
	            have.
	            'Location' Filter users by the location indicated in their profile.
	            'Language' Search for users that have repositories that match a certain
	            language.
	            'Created' Filter users based on when they joined.
	            'Followers' Filter users based on the number of followers they have.
	 * @param sort If not provided, results are sorted by best match.
	 * @return OK
	 * @path /search/users 
	 */		
	IDataSet<SearchUsers> getSearchUsers(String order, String q, String sort);
	
	/**
	 * null
	 * @param order The sort field. if sort param is provided. Can be either asc or desc.
	 * @param q The search terms. This can be any combination of the supported code
	            search parameters:
	            'Search In' Qualifies which fields are searched. With this qualifier
	            you can restrict the search to just the file contents, the file path,
	            or both.
	            'Languages' Searches code based on the language it's written in.
	            'Forks' Filters repositories based on the number of forks, and/or
	            whether code from forked repositories should be included in the results
	            at all.
	            'Size' Finds files that match a certain size (in bytes).
	            'Path' Specifies the path that the resulting file must be at.
	            'Extension' Matches files with a certain extension.
	            'Users' or 'Repositories' Limits searches to a specific user or repository.
	 * @param sort Can only be 'indexed', which indicates how recently a file has been indexed
	               by the GitHub search infrastructure. If not provided, results are sorted
	               by best match.
	 * @return OK
	 * @path /search/code 
	 */		
	IDataSet<SearchCode> getSearchCode(String order, String q, String sort);
	
	/**
	 * null
	 * @param order The sort field. if sort param is provided. Can be either asc or desc.
	 * @param q The q search term can also contain any combination of the supported issue search qualifiers:
	 * @param sort The sort field. Can be comments, created, or updated. Default: results are sorted by best match.
	 * @return OK
	 * @path /search/issues 
	 */		
	IDataSet<SearchIssues> getSearchIssues(String order, String q, String sort);
	
	/**
	 * null
	 * @param order The sort field. if sort param is provided. Can be either asc or desc.
	 * @param q The search terms. This can be any combination of the supported repository
	            search parameters:
	            'Search In' Qualifies which fields are searched. With this qualifier you
	            can restrict the search to just the repository name, description, readme,
	            or any combination of these.
	            'Size' Finds repositories that match a certain size (in kilobytes).
	            'Forks' Filters repositories based on the number of forks, and/or whether
	            forked repositories should be included in the results at all.
	            'Created' and 'Last Updated' Filters repositories based on times of
	            creation, or when they were last updated.
	            'Users or Repositories' Limits searches to a specific user or repository.
	            'Languages' Searches repositories based on the language they are written in.
	            'Stars' Searches repositories based on the number of stars.
	 * @param sort If not provided, results are sorted by best match.
	 * @return OK
	 * @path /search/repositories 
	 */		
	IDataSet<SearchRepositories> getSearchRepositories(String order, String q, String sort);
	
}