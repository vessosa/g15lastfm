package com.vessosa.g15lastfmplayer.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pushingpixels.trident.Timeline;

import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.mvc.AbstractView;

public class ChooseRadioScreen implements AbstractView {
	private static final Logger LOGGER = Logger.getLogger(ChooseRadioScreen.class);
	private Controller controller;
	private List<String> radioList;
	private String t1;
	private String t2;
	private float animProgress;
	private int angle = 30;
	private Timeline timeline;
	private int circleWidth = 500;
	private int height;
	private int width;
	private int font;

	public ChooseRadioScreen(Controller controller) {
		this.controller = controller;
		setWidth(160);
		setHeight(43);

		this.t1 = "Radio Buchada";
		this.t2 = "Radio Buchada";
		radioList = new ArrayList<String>();
		radioList.add("Radio Electracks");
		radioList.add("Vessosa Loved Tracks");
		radioList.add("Audio Slave Radio");
		radioList.add("DC Talk radio");
		radioList.add("Godsmack radio");
	}

	int i = -1;

	private String getNextRadioName() {
		i++;
		if (i == getRadioList().size())
			i = 0;
		return getRadioList().get(i);
	}

	public String getNextRadio() {
		t1 = t2;
		t2 = getNextRadioName();
		startAnim();
		return t2;
	}

	protected void drawChooserAnimation(Graphics arg) {
		Graphics2D g = (Graphics2D) arg.create();
		g.setFont(LCDScreen.getSmallFont());

		g.translate(0, -circleWidth / 2 + getHeight() / 2);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, circleWidth, circleWidth);

		g.fillRect(0, circleWidth / 2 - getHeight() / 2, getWidth(), getHeight());
		g.clipRect(0, circleWidth / 2 - getHeight() / 2, getWidth(), getHeight());

		g.setColor(Color.BLACK);

		g.rotate(Math.toRadians(-angle) * animProgress, circleWidth / 2, circleWidth / 2);
		g.drawString(t1, 0, (int) (circleWidth / 2 + LCDScreen.getBigFont().getSize() / 4));

		g.rotate(Math.toRadians(angle), circleWidth / 2, circleWidth / 2);
		g.drawString(t2, 0, (int) (circleWidth / 2 + LCDScreen.getBigFont().getSize() / 4));
		g.dispose();
	}

	public void startAnim() {
		if (timeline != null) {
			timeline.cancel();
		}
		timeline = new Timeline(this);
		timeline.setDuration(500);
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.play();
	}

	public void setAnimProgress(float animProgress) {

		this.animProgress = animProgress;
		// repaint();
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	public List<String> getRadioList() {
		return radioList;
	}

	public void setRadioList(List<String> radioList) {
		this.radioList = radioList;
	}

	public Controller getController() {
		return controller;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFont() {
		return font;
	}

	public void setFont(int font) {
		this.font = font;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals((Controller.ANIM_TO_NEXT_RADIO))) {
			getNextRadio();
		}
	}
}
