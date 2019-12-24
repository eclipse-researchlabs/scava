/*******************************************************************************
 * Copyright (c) 2012 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.emc.json.dt;

import org.eclipse.epsilon.common.dt.launching.dialogs.AbstractCachedModelConfigurationDialog;
import org.eclipse.epsilon.emc.json.JsonModel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JsonModelConfigurationDialog extends AbstractCachedModelConfigurationDialog {
	
	@Override
	protected String getModelName() {
		return "JSON Document";
	}

	@Override
	protected String getModelType() {
		return "JSON";
	}
	
	protected Label fileTextLabel;
	protected Text fileText;
	protected Label uriTextLabel;
	protected Text uriText;
	protected Button browseModelFile;
	protected Button filebasedButton;
	
	@Override
	protected void createGroups(Composite control) {
		super.createGroups(control);
		//createFilesGroup(control);
		createLoadStoreOptionsGroup(control);
		toggleEnabledFields();
	}
	
	protected void toggleEnabledFields() {
		if (filebasedButton.getSelection()) {
			fileTextLabel.setEnabled(true);
			fileText.setEnabled(true);
			uriTextLabel.setEnabled(false);
			uriText.setEnabled(false);
			uriText.setText("");
		}
		else {
			fileTextLabel.setEnabled(false);
			fileText.setEnabled(false);
			uriTextLabel.setEnabled(true);
			uriText.setEnabled(true);
			fileText.setText("");
			storeOnDisposalCheckbox.setSelection(false);
		}
	}
	
//	protected Composite createFilesGroup(Composite parent) {
//		final Composite groupContent = createGroupContainer(parent, "Files/URIs", 3); // FIXME: compile-error (also in epsilonlabs/emc-json) requires maintenance
//		
//		filebasedButton = new Button(groupContent, SWT.CHECK);
//		GridData filebasedButtonGridData = new GridData(GridData.FILL_HORIZONTAL);
//		filebasedButtonGridData.horizontalSpan = 3;
//		filebasedButton.setSelection(true);
//		filebasedButton.setText("Workspace file");
//		filebasedButton.setLayoutData(filebasedButtonGridData);
//		filebasedButton.addListener(SWT.Selection, new Listener() {
//
//			
//			public void handleEvent(Event event) {
//				toggleEnabledFields();
//			}
//			
//		});
//		
//		
//		fileTextLabel = new Label(groupContent, SWT.NONE);
//		fileTextLabel.setText("File: ");
//		
//		fileText = new Text(groupContent, SWT.BORDER);
//		fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		browseModelFile = new Button(groupContent, SWT.NONE);
//		browseModelFile.setText("Browse Workspace...");
//		browseModelFile.addListener(SWT.Selection, new BrowseWorkspaceForModelsListener(fileText, "XML Documents in the workspace", "Select an XML document"));
//
//		uriTextLabel = new Label(groupContent, SWT.NONE);
//		uriTextLabel.setText("URI: ");
//		
//		uriText = new Text(groupContent, SWT.BORDER);
//		GridData uriTextGridData = new GridData(GridData.FILL_HORIZONTAL);
//		uriTextGridData.horizontalSpan = 2;
//		uriText.setLayoutData(uriTextGridData);
//		
//		groupContent.layout();
//		groupContent.pack();
//		return groupContent;
//	}

	
	
	@Override
	protected void loadProperties(){
		super.loadProperties();
		if (properties == null) return;
		fileText.setText(properties.getProperty(JsonModel.PROPERTY_FILE));
		uriText.setText(properties.getProperty(JsonModel.PROPERTY_URI));
		filebasedButton.setSelection(properties.getBooleanProperty("fileBased", true));
		toggleEnabledFields();
	}
	
	
	@Override
	protected void storeProperties(){
		super.storeProperties();
		properties.put(JsonModel.PROPERTY_URI, uriText.getText());
		properties.put(JsonModel.PROPERTY_FILE, fileText.getText());
		properties.put("fileBased", filebasedButton.getSelection() + "");
	}
}
