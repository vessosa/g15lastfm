package com.vessosa.g15lastfmplayer.view.scrollfx;

import com.vessosa.g15lastfmplayer.view.ScrollingText;
import com.vessosa.g15lastfmplayer.view.ScrollingText.AbstractScrollTextUtils;

// ----cosin
public abstract class AbstractCosineScrollTextUtils extends AbstractScrollTextUtils {
	protected int yOffset = 0;

	public AbstractCosineScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow, int spaceBetweenLetter,
			int pxSec, int posX, int posY) {
		super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
	}

	@Override
	public double getRotate(final float animProgress, final int charnum) {
		double x = getXpos(animProgress, charnum);
		return Math.cos((double) Math.toRadians(x));
	}
}