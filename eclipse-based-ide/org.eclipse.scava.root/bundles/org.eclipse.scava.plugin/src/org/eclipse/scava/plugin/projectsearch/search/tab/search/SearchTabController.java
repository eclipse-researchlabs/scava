/*********************************************************************
* Copyright c 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse PublicLicense 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.scava.plugin.projectsearch.search.tab.search;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.scava.plugin.mvc.controller.Controller;
import org.eclipse.scava.plugin.mvc.controller.ModelViewController;
import org.eclipse.scava.plugin.mvc.event.routed.IRoutedEvent;
import org.eclipse.scava.plugin.projectsearch.search.details.DetailsController;
import org.eclipse.scava.plugin.projectsearch.search.details.DetailsModel;
import org.eclipse.scava.plugin.projectsearch.search.details.DetailsView;
import org.eclipse.scava.plugin.projectsearch.search.searchresult.SearchResultController;
import org.eclipse.scava.plugin.projectsearch.search.searchresult.SearchResultModel;
import org.eclipse.scava.plugin.projectsearch.search.searchresult.SearchResultView;
import org.eclipse.scava.plugin.projectsearch.search.searchresult.ShowDetailsRequestEvent;
import org.eclipse.scava.plugin.ui.errorhandler.ErrorHandler;
import org.eclipse.scava.plugin.usermonitoring.ErrorType;

import io.swagger.client.ApiException;
import io.swagger.client.model.Artifact;

public class SearchTabController extends ModelViewController<SearchTabModel, SearchTabView>
		implements ISearchTabViewEventListener {

	public SearchTabController(Controller parent, SearchTabModel model, SearchTabView view) {
		super(parent, model, view);
	}

	@Override
	public void init() {
		super.init();

		loadNextPage();
	}

	private void loadNextPage() {
		try {

			List<Artifact> results = getModel().getNextPageResults();

			List<SearchResultView> resultViews = results.stream().filter(Objects::nonNull).map(a -> {

				SearchResultModel model = new SearchResultModel(a);
				SearchResultView view = new SearchResultView();
				SearchResultController controller = new SearchResultController(SearchTabController.this, model, view);
				controller.init();

				return view;
			}).collect(Collectors.toList());

			getView().showResults(resultViews);
			getView().setDescription(getModel().getDescription());

			if (getModel().hasNextPage()) {
				getView().showShowMoreButton();
			} else {
				getView().hideShowMoreButton();
			}

			routeEventToParentController(new FilterAlreadySelectedProjectsRequestEvent(this, results));
		} catch (ApiException e) {
			e.printStackTrace();
			ErrorHandler.logAndShowErrorMessage(getView().getShell(), e);
			dispose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			ErrorHandler.handle(getView().getShell(), e, ErrorType.ILLEGAL_ARGUMENT);
			dispose();
		}

	}

	@Override
	public void requestViewClose() {
		dispose();
	}

	@Override
	protected void onReceiveRoutedEventFromSubController(IRoutedEvent routedEvent, Controller forwarderController) {

		if (routedEvent instanceof ShowDetailsRequestEvent) {
			
			ShowDetailsRequestEvent event = (ShowDetailsRequestEvent) routedEvent;
			routeEventToSubControllers(event);
			
			DetailsModel model = new DetailsModel(event.getProject());
			DetailsView view = new DetailsView();
			DetailsController controller = new DetailsController(this, model, view);
			controller.init();

			getView().setDetails(view);

			getSubControllers(DetailsController.class).stream().filter(c -> c != controller)
					.forEach(Controller::dispose);

			return;
		}

		super.onReceiveRoutedEventFromSubController(routedEvent, forwarderController);
	}

	@Override
	public void onShowMore() {
		loadNextPage();
	}

}
