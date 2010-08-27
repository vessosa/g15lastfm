package com.vessosa.g15lastfmplayer.view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.pushingpixels.trident.Timeline;

public class SpinningReelScreen extends JPanel {

	private static final long serialVersionUID = 1L;
	private List<String> radioList;
	private String t1;
	private String t2;
	private float animProgress;
	private int angle = 30;
	private Timeline timeline;
	private int circleWidth = 800;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SpinningReelScreen rs = new SpinningReelScreen();
				rs.test();
			}
		});
	}

	public void test() {
		JFrame f = new JFrame();
		// final SpinningReelScreen text = new SpinningReelScreen();
		f.getContentPane().add(this);
		f.setSize(400, 220);
		f.setVisible(true);

		JButton b = new JButton("go");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(getNextRadio());
			}
		});
		f.getContentPane().add(b, BorderLayout.SOUTH);
	}

	public SpinningReelScreen() {
		setFont(getFont().deriveFont(25f));
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

	@Override
	protected void paintComponent(Graphics arg) {
		super.paintComponent(arg);

		int h = getHeight();

		Graphics2D g = (Graphics2D) arg.create();

		g.translate(0, -circleWidth / 2 + h / 2);

		Rectangle2D b1 = g.getFontMetrics().getStringBounds(t1, g);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, circleWidth, circleWidth);

		g.fillRect(0, circleWidth / 2 - h / 2, getWidth(), h);
		g.clipRect(0, circleWidth / 2 - h / 2, getWidth(), h);

		g.setColor(Color.BLACK);

		g.setComposite(AlphaComposite.SrcOver.derive(1f - animProgress));
		g.rotate(Math.toRadians(-angle) * animProgress, circleWidth / 2, circleWidth / 2);
		g.drawString(t1, 0, (int) (circleWidth / 2 + b1.getHeight() / 4));

		g.setComposite(AlphaComposite.SrcOver.derive(animProgress));
		g.rotate(Math.toRadians(angle), circleWidth / 2, circleWidth / 2);
		g.drawString(t2, 0, (int) (circleWidth / 2 + b1.getHeight() / 4));

		g.dispose();

	}

	public void startAnim() {
		if (timeline != null) {
			timeline.cancel();
		}
		timeline = new Timeline(this);
		timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		timeline.play();
	}

	public void setAnimProgress(float animProgress) {
		this.animProgress = animProgress;
		repaint();
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

}
