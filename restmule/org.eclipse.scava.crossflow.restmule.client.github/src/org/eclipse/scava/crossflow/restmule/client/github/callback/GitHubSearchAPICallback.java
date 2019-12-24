package org.eclipse.scava.crossflow.restmule.client.github.callback;

import static org.eclipse.scava.crossflow.restmule.core.util.PropertiesUtil.PAGE_INFO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.scava.crossflow.restmule.client.github.data.GitHubSearchAPIDataSet;
import org.eclipse.scava.crossflow.restmule.client.github.page.GitHubSearchAPIPagination;
import org.eclipse.scava.crossflow.restmule.client.github.util.GitHubPropertiesUtil;
import org.eclipse.scava.crossflow.restmule.core.callback.AbstractWrappedCallback;
import org.eclipse.scava.crossflow.restmule.core.page.IWrap;
import org.mortbay.log.Log;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

public class GitHubSearchAPICallback<D, R extends IWrap<D>> extends AbstractWrappedCallback<D, R, GitHubSearchAPIDataSet<D>> {

	private static final Logger LOG = LogManager.getLogger(GitHubSearchAPICallback.class);

	private static GitHubSearchAPIPagination paginationPolicy = GitHubSearchAPIPagination.get();
	private static int maxCount = GitHubSearchAPIPagination.getSearchAPIMaxResults();

	public GitHubSearchAPICallback() {
		super(new GitHubSearchAPIDataSet<D>());
	}

	// FIXME move these methods to super abstract class <--

	@Override
	public void handleResponse(Response<R> response) {
		this.dataset.addElements(response.body().getItems());
	}

	@Override
	public void handleTotal(Response<R> response) {
		Integer totalCount = response.body().getTotalCount();
		if (totalCount > maxCount) {
			Log.info("Please note that for GitHub Search API requests, as totalCount (" + totalCount + ") > maxCount (" + maxCount
					+ "), only " + maxCount + " elements will be retrieved.");
			this.dataset.setTotal(maxCount);
		} else
			this.dataset.setTotal(totalCount);
	}

	@Override
	public void handleError(Call<R> call, Throwable t) {
		LOG.error(t.getMessage());
		LOG.error(call.request().url()); // TODO RETRY
	}

	// --->

	@Override
	public Integer totalIterations(Response<R> response) { // FIXME
		Headers headers = response.headers();
		String pagination = GitHubPropertiesUtil.get(PAGE_INFO);
		String headerValue;
		if ((headerValue = headers.get(pagination)) != null) {
			HashMap<String, String> links = getLinks(headerValue);
			return getPageFromURL(links.get("LAST"));
		}
		return null; // FIXME!! Return pp.start()
	}

	public static final HashMap<String, String> getLinks(String headerValue) { // FIXME
		HashMap<String, String> result = new HashMap<>();
		if (headerValue != null) {
			Iterator<String> iterator = Arrays.asList(headerValue.split(", ")).iterator();
			while (iterator.hasNext()) {
				String[] split = iterator.next().split(">; rel=\"");
				result.put(split[1].substring(0, split[1].length() - 1).toUpperCase(), split[0].substring(1));
			}
		}
		return result;
	}

	public static Integer getPageFromURL(String url) { // FIXME
		String regex = "page=(\\d*)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		} else {
			return null;
		}
	}

}
