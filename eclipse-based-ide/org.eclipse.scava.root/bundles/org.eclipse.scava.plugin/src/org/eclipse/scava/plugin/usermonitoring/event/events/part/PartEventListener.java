/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    Zsolt J�nos Szamosv�lgyi
*    Endre Tam�s V�radi
*    Gerg� Balogh
**********************************************************************/
package org.eclipse.scava.plugin.usermonitoring.event.events.part;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.scava.plugin.Activator;
import org.eclipse.scava.plugin.usermonitoring.event.IEventListener;
import org.eclipse.scava.plugin.usermonitoring.event.events.document.DocumentEventListener;
import org.eclipse.scava.plugin.usermonitoring.event.events.resourceElement.ResourceElementEventListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class PartEventListener implements IEventListener, IPartListener2 {

	public PartEventListener() {

	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		
		
		//Activator.getDefault().getMainController().getEventBus().post(new PartEvent(partRef, PartEventType.ACTIVATED));

	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		Activator.getDefault().getEventBus().post(new PartEvent(partRef, PartEventType.BROUGHT_TO_TOP));

	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		Activator.getDefault().getEventBus().post(new PartEvent(partRef, PartEventType.CLOSED));

	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		//Activator.getDefault().getMainController().getEventBus().post(new PartEvent(partRef, PartEventType.DEACTIVATED));

	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		subscribeDocumentEventListener(partRef);
		Activator.getDefault().getEventBus().post(new PartEvent(partRef, PartEventType.OPENED));
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		//Activator.getDefault().getMainController().getEventBus().post(new PartEvent(partRef, PartEventType.HIDDEN));

	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		//Activator.getDefault().getMainController().getEventBus().post(new PartEvent(partRef, PartEventType.VISIBLE));

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		//Activator.getDefault().getMainController().getEventBus().post(new PartEvent(partRef, PartEventType.INPUT_CHANGED));

	}

	private void subscribeDocumentEventListener(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof IEditorPart) {
			IEditorPart editor = (IEditorPart) part;
			IEditorInput input = editor.getEditorInput();

			if (editor instanceof ITextEditor && input instanceof FileEditorInput) {
				ITextEditor textEditor = (ITextEditor) editor;

				IDocument document = textEditor.getDocumentProvider().getDocument(input);
				IEditorInput editorInput = textEditor.getEditorInput();

				IFile file = ((IFileEditorInput) editorInput).getFile();

				if (file.getFileExtension().toLowerCase().equals("java")) {

					ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
					saveListener(compilationUnit, textEditor);
					DocumentEventListener documentListener = new DocumentEventListener(textEditor.getTitle(), compilationUnit);
					document.addDocumentListener(documentListener);
				}

			}
		}
	}

	private void saveListener(ICompilationUnit compilationUnit, ITextEditor textEditor) {
		IDocumentProvider provider = textEditor.getDocumentProvider();
		IEditorInput input = textEditor.getEditorInput();

		provider.addElementStateListener(new ResourceElementEventListener(input, compilationUnit));
	}

}
