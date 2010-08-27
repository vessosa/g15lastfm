package com.vessosa.g15lastfmplayer.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;

import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.mvc.AbstractView;
import com.vessosa.g15lastfmplayer.view.scrollfx.G15ScrollStatusFX;

public class StatusScroll extends ScrollingText implements AbstractView {
	private boolean showStatus;

	public StatusScroll() {
		showStatus = true;
		setFont(LCDScreen.getSmallFont());
		setWidth(42);
		setHeight(8);
		setScrollTextUtils(new G15ScrollStatusFX(this, false, 8, 10, 64, 35));
	}

	public void drawStatusMessage(Graphics g) {
		if (showStatus) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(Color.white);
			// g2.fillRect(59, 35, 48, 8);
			g2.setColor(Color.black);
			// paint the text for each running animation
			for (int i = 0; i < this.animHandlers.size(); i++) {
				paintText(g2, this.animHandlers.get(i).getAnimProgress());
			}
			g2.dispose();
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(Controller.SHOW_STATUS_MESSAGE)) {
			showStatus = (Boolean) evt.getNewValue();
		} else if (propertyName.equals(Controller.STATUS_MESSAGE)) {
			String newMessage = (String) evt.getNewValue();
			newMessage = newMessage.replaceAll("’", "'");
			setText(newMessage);
		}
	}
}
