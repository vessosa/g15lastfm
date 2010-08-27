package com.vessosa.g15lastfmplayer.view.scrollfx;

import com.vessosa.g15lastfmplayer.view.ScrollingText;
import com.vessosa.g15lastfmplayer.view.ScrollingText.AbstractHorizontalTextUtils;

public class G15ScrollStatusFX extends AbstractHorizontalTextUtils {
	public G15ScrollStatusFX(final ScrollingText scrollingText, boolean drawShadow, int spaceBetweenLetter, int pxSec,
			int posX, int posY) {
		super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
	}

	@Override
	public int getXpos(final float animProgress, final int charnum) {
		int x = this.scrollingText.getWidth() + this.textWidth;
		x = (int) ((1f - animProgress) * x) + charnum * this.spaceBetweenLetter - this.textWidth;
		return x;
	}

	@Override
	public boolean isTimeTolaunchNewTimeline(final float animProgress) {
		int len = this.scrollingText.getText().length();
		if (len > 1) {
			int x1 = getXpos(animProgress, 0);
			int x2 = getXpos(animProgress, len - 1);
			if (x1 < 0 && x2 < (this.scrollingText.getWidth() / 2)) {
				return true;
			}
		}
		return super.isTimeTolaunchNewTimeline(animProgress);
	}
}
