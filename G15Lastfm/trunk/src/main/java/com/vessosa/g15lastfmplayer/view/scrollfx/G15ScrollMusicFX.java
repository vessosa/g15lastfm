package com.vessosa.g15lastfmplayer.view.scrollfx;

import java.awt.Graphics2D;

import com.vessosa.g15lastfmplayer.view.ScrollingText;

public class G15ScrollMusicFX extends AbstractCosineScrollTextUtils {

	public G15ScrollMusicFX(ScrollingText scrollingText, boolean drawShadow, int spaceBetweenLetter, int pxSec,
			int posX, int posY) {
		super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		this.spaceBetweenLetter = 25;
	}

	@Override
	public double getRotate(final float animProgress, final int charnum) {
		double t = ((2 * Math.PI) / this.scrollingText.getWidth()) * getXpos(animProgress, charnum);
		double cos = -Math.cos(t);
		double angle = cos / 4;
		return angle;
	}

	@Override
	public int getXpos(final float animProgress, final int charnum) {
		int x = this.scrollingText.getWidth() + this.textWidth;
		x = (int) ((1f - animProgress) * x) + charnum * this.spaceBetweenLetter - this.textWidth;
		return x;
	}

	@Override
	public int getYpos(final float animProgress, final int charnum) {
		int x = getXpos(animProgress, charnum);
		double t = ((2 * Math.PI) / this.scrollingText.getWidth()) * x;
		double cos = -Math.cos(t);
		int h = this.scrollingText.getHeight() - this.yOffset * 2;
		int res = (int) (cos * (h / 2 - this.scrollingText.getFont().getSize()) + h / 2);
		return res;
	};

	@Override
	public float getTransformedFontSize(final float animProgress, final int fontSize, final int charnum) {
		// int x = getXpos(animProgress, charnum);
		// double t = ((2 * Math.PI) / this.scrollingText.getWidth()) * x;
		// double cos = Math.abs(Math.cos(t / 2 + Math.PI / 2));
		// int res = (int) ((cos * fontSize)) + fontSize / 2;
		// return res;
		int x = getYpos(animProgress, charnum);
		double t = ((2 * Math.PI) / this.scrollingText.getWidth()) * x;
		double sin = Math.abs(Math.sin(t));
		float res = fontSize / 2 + (float) sin * fontSize;
		return res;
	}

	@Override
	public void paintCharacter(final Graphics2D g, final String s) {
		g.drawString(s, 0, 0);
	}

	@Override
	public int getDuration() {
		int pxSec = 50;
		int duration = (this.textWidth + this.scrollingText.getWidth()) / pxSec * 1000;
		return duration;
	}

}
