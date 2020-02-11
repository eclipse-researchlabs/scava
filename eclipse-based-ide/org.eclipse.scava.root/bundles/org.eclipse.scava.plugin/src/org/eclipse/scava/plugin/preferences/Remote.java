/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.scava.plugin.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.scava.plugin.Activator;
import org.eclipse.scava.plugin.knowledgebase.access.KnowledgeBaseAccess;
import org.eclipse.scava.plugin.ui.errorhandler.ErrorHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wb.swt.SWTResourceManager;

import io.swagger.client.ApiException;
import io.swagger.client.api.ArtifactsRestControllerApi;

public class Remote extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private Label connectionResultLabel;
	private StringFieldEditor addressEditor;
	private IntegerFieldEditor portEditor;

	public Remote() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));
		setDescription("Remote access settings");
	}

	@Override
	protected void createFieldEditors() {
		addField(addressEditor = new StringFieldEditor(Preferences.KNOWLEDGEBASE_SERVER_ADDRESS,
				"KnowledgeBase server address", getFieldEditorParent()));
		addField(portEditor = new IntegerFieldEditor(Preferences.KNOWLEDGEBASE_SERVER_PORT, "KnowledgeBase server port",
				getFieldEditorParent()));

		connectionResultLabel = new Label(getFieldEditorParent(), SWT.NONE);
		connectionResultLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		connectionResultLabel.setText("Test your connection to the server");

		Button connectionTestButton = new Button(getFieldEditorParent(), SWT.NONE);
		connectionTestButton.setText("Test connection");
		connectionTestButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		connectionTestButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				testConnection();
			}

		});

		new Label(getFieldEditorParent(), SWT.None);
		new Label(getFieldEditorParent(), SWT.None);

		addField(new StringFieldEditor(Preferences.WEBDASHBOARD_BASE_PATH, "WebDashboard base path",
				getFieldEditorParent()));
	}

	private void testConnection() {
		System.out.println("Testing");
		String ipPort = addressEditor.getStringValue() + ":" + portEditor.getIntValue();
		connectionResultLabel.setText("Testing connection: " + ipPort);
		connectionResultLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		getFieldEditorParent().requestLayout();

		Display.getDefault().asyncExec(() -> {

			KnowledgeBaseAccess knowledgeBaseAccess = new KnowledgeBaseAccess(addressEditor.getStringValue(),
					portEditor.getIntValue());
			ArtifactsRestControllerApi artifactRestControllerApi = knowledgeBaseAccess
					.getArtifactRestControllerApi(Preferences.TIMEOUT_CONNECTION_TEST);
			try {
				artifactRestControllerApi.getArtifactsUsingGET(0, 1, null);
				connectionResultLabel.setText("Server is available at " + ipPort);
				connectionResultLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
				getFieldEditorParent().requestLayout();
			} catch (ApiException e) {
				connectionResultLabel.setText("Failed to connect to " + ipPort);
				connectionResultLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
				getFieldEditorParent().requestLayout();
				ErrorHandler.logAndShowErrorMessage(getShell(), "Connection test failed.", e);
			}
		});
	}
}
