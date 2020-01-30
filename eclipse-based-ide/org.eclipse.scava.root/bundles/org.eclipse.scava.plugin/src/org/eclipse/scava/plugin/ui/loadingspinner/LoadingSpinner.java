/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package org.eclipse.scava.plugin.ui.loadingspinner;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

public class LoadingSpinner extends Label {

	private int frame = 0;
	private Timer timer;
	private static final long DELAY = 1000 / 20;

	private String[] frames = { "icons/control/loadingSpinner/loadingSpinner_1.png",
			"icons/control/loadingSpinner/loadingSpinner_2.png", "icons/control/loadingSpinner/loadingSpinner_3.png",
			"icons/control/loadingSpinner/loadingSpinner_4.png", "icons/control/loadingSpinner/loadingSpinner_5.png",
			"icons/control/loadingSpinner/loadingSpinner_6.png", "icons/control/loadingSpinner/loadingSpinner_7.png",
			"icons/control/loadingSpinner/loadingSpinner_8.png", "icons/control/loadingSpinner/loadingSpinner_9.png",
			"icons/control/loadingSpinner/loadingSpinner_10.png", "icons/control/loadingSpinner/loadingSpinner_11.png",
			"icons/control/loadingSpinner/loadingSpinner_12.png", "icons/control/loadingSpinner/loadingSpinner_13.png",
			"icons/control/loadingSpinner/loadingSpinner_14.png", "icons/control/loadingSpinner/loadingSpinner_15.png",
			"icons/control/loadingSpinner/loadingSpinner_16.png" };

	public LoadingSpinner(Composite parent, int style) {
		super(parent, style);
		loadNextFrame();
	}

	public void startAnimation() {
		startUpdaterThread();
	}

	public void stopAnimation() {
		if (timer != null) {
			timer.cancel();
		}
	}

	private void startUpdaterThread() {
		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				Display.getDefault().asyncExec(() -> {
					if (!isDisposed()) {
						loadNextFrame();
					}else {
						timer.cancel();
					}
				});
			}
		}, DELAY, DELAY);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private void loadNextFrame() {
		if (++frame >= frames.length) {
			frame = 0;
		}

		loadImage(frames[frame]);
	}

	private void loadImage(String fileName) {
		setImage(ResourceManager.getPluginImage("org.eclipse.scava.plugin", fileName));
		requestLayout();
	}

}
