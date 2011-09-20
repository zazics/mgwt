/*
 * Copyright 2010 Daniel Kurka
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.kurka.gwt.mobile.ui.client.panel;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.kurka.gwt.mobile.mvp.client.AnimatableDisplay;
import de.kurka.gwt.mobile.mvp.client.Animation;
import de.kurka.gwt.mobile.mvp.client.AnimationEndCallback;

public abstract class AnimatableDialogBase implements HasWidgets {
	private AnimatableDisplay display;
	protected FlowPanel container;

	private boolean centerContent;

	public AnimatableDialogBase() {
		display = GWT.create(AnimatableDisplay.class);

		// TODO into css
		display.asWidget().getElement().getStyle().setZIndex(100);
		display.asWidget().getElement().getStyle().setBackgroundColor("rgba(0, 0, 0, 0.4)");

		container = new FlowPanel();
		// TODO into css
		container.getElement().getStyle().setProperty("display", "inline-block");
		display.setFirstWidget(container);
	}

	public void center() {
		centerContent = true;
		show();
	}

	public void setCenterContent(boolean centerContent) {
		this.centerContent = centerContent;
	}

	protected HasWidgets panelToOverlay;

	public void setPanelToOverlay(HasWidgets panel) {
		this.panelToOverlay = panel;
	}

	public HasWidgets getPanelToOverlay() {
		if (panelToOverlay == null) {
			panelToOverlay = RootPanel.get();
		}
		return panelToOverlay;
	}

	public void show() {
		// add overlay to DOM
		HasWidgets panel = getPanelToOverlay();
		panel.add(display.asWidget());

		if (centerContent) {
			container.getElement().setAttribute("style", "");

			// TODO css
			container.getElement().getStyle().setProperty("display", "inline-block");

			// put them somewhere where the user cant see them and measure sizt
			RootPanel rootPanel = RootPanel.get();
			FlowPanel flowPanel = new FlowPanel();
			flowPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
			flowPanel.getElement().getStyle().setLeft(100, Unit.PCT);
			flowPanel.getElement().getStyle().setTop(100, Unit.PCT);
			flowPanel.add(container);
			rootPanel.add(flowPanel);

			// measure
			int parentHeight = display.asWidget().getOffsetHeight();
			int parentWidth = display.asWidget().getOffsetWidth();

			int childHeight = container.getOffsetHeight();

			int childWidth = container.getOffsetWidth();

			// remove helper
			rootPanel.remove(flowPanel);

			// set position

			container.getElement().getStyle().setPosition(Position.ABSOLUTE);
			container.getElement().getStyle().setLeft(((double) parentWidth) / 2 - ((double) childWidth / 2), Unit.PX);
			container.getElement().getStyle().setTop(((double) parentHeight) / 2 - ((double) childHeight / 2), Unit.PX);

		}

		display.setFirstWidget(container);

		// and animiate
		Animation animation = getShowAnimation();

		display.animate(animation, true, new AnimationEndCallback() {

			@Override
			public void onAnimationEnd() {

			}
		});

	}

	public void hide() {

		Animation animation = getHideAnimation();

		display.animate(animation, false, new AnimationEndCallback() {

			@Override
			public void onAnimationEnd() {
				HasWidgets panel = getPanelToOverlay();
				panel.remove(display.asWidget());

			}
		});

	}

	@Override
	public void add(Widget w) {
		container.add(w);

	}

	@Override
	public void clear() {
		container.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return container.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return container.remove(w);
	}

	protected abstract Animation getShowAnimation();

	protected abstract Animation getHideAnimation();
}