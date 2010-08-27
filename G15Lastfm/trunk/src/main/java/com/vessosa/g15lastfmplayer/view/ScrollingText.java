package com.vessosa.g15lastfmplayer.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.TimelineState;
import org.pushingpixels.trident.callback.TimelineCallback;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import com.vessosa.g15lastfmplayer.view.scrollfx.AbstractCosineScrollTextUtils;

/***
 * Many thanks to Damien Mabs
 * 
 * @see http://free-the-pixel.blogspot.com
 * 
 *      I did some modifications here to adapt to use it on G15
 * 
 */
public class ScrollingText {
	private static int fontSize = 20;
	private Font font;
	private int width;
	private int height;
	private boolean drawShadow;
	private int spaceBetweenLetter;
	private int pxSec;
	private int posX, posY;

	public static class AnimationProgressHandler {
		private Timeline timeline;
		private float animProgress = 0;
		private boolean AlreadyStartedNext = false;
		private ScrollingText scrolling;

		private AnimationProgressHandler(final ScrollingText scrolling) {
			this.scrolling = scrolling;
			// prepare the timeline
			this.timeline = new Timeline(this);
			this.timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
			this.timeline.setDuration(scrolling.scrollTextUtils.getDuration());
			this.timeline.addCallback(createCallBack(this.timeline));
		}

		public void setAnimProgress(final float animProgress) {
			this.animProgress = animProgress;
			if (!this.AlreadyStartedNext && this.scrolling.scrollTextUtils.isTimeTolaunchNewTimeline(animProgress)) {
				this.AlreadyStartedNext = true;
				// start the next animation
				this.scrolling.createTimeline().play();
			}
		}

		private TimelineCallback createCallBack(final Timeline parent) {
			return new TimelineCallbackAdapter() {
				@Override
				public void onTimelineStateChanged(final TimelineState oldState, final TimelineState newState,
						final float durationFraction, final float timelinePosition) {
					if (newState == TimelineState.DONE) {
						// when the animation is done
						// remove it
						AnimationProgressHandler.this.scrolling.animHandlers.remove(AnimationProgressHandler.this);
					}
				}
			};
		}

		public float getAnimProgress() {
			return animProgress;
		}
	}

	protected ArrayList<AnimationProgressHandler> animHandlers;
	private String text = "";

	protected IScrollTextUtils scrollTextUtils;

	public void setScrollTextUtils(final IScrollTextUtils scrollUtils) {
		this.scrollTextUtils = scrollUtils;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public ScrollingText() {
		this.font = new Font("Sans", Font.BOLD, fontSize);
		this.width = 160;
		this.height = 55;
		this.drawShadow = true;
		this.spaceBetweenLetter = 20;
		this.pxSec = 70;
		this.posX = 0;
		this.posY = 0;
		this.animHandlers = new ArrayList<AnimationProgressHandler>();
		this.scrollTextUtils = new DefaultRightToLeftCosineScrollTextUtils(this, drawShadow, spaceBetweenLetter, pxSec,
				posX, posY);
	}

	public void setText(final String text) {
		// stop current animations and remove them
		for (AnimationProgressHandler t : this.animHandlers) {
			t.timeline.abort();
		}
		this.animHandlers.clear();

		int totalSpace = text.length() * 10 / 31;
		String space = "";
		for (int i = 0; i < totalSpace; i++) {
			space += " ";
		}
		this.text = text + space;

		// notify the scrollTextUtils to do computation related to the new text
		this.scrollTextUtils.textChanged();
		// start the animation
		createTimeline().play();
	}

	private Timeline createTimeline() {
		AnimationProgressHandler handler = new AnimationProgressHandler(this);
		Timeline t = handler.timeline;
		this.animHandlers.add(handler);
		return t;
	}

	// @Override
	// protected void paintComponent(final Graphics g) {
	// super.paintComponent(g);
	// Graphics2D g2 = (Graphics2D) g.create();
	// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// // paint the text for each running animation
	// for (int i = 0; i < this.animHandlers.size(); i++) {
	// paintText(g2, this.animHandlers.get(i).animProgress);
	// }
	// g2.dispose();
	// }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isDrawShadow() {
		return drawShadow;
	}

	public void setDrawShadow(boolean drawShadow) {
		this.drawShadow = drawShadow;
	}

	public int getSpaceBetweenLetter() {
		return spaceBetweenLetter;
	}

	public void setSpaceBetweenLetter(int spaceBetweenLetter) {
		this.spaceBetweenLetter = spaceBetweenLetter;
	}

	protected void paintText(final Graphics2D g2, final float animProgress) {
		Font f = getFont();
		for (int i = 0; i < this.getText().length(); i++) {
			int x = this.scrollTextUtils.getXpos(animProgress, i);
			if (x < -f.getSize() || x > getWidth()) {
				// do not paint, out of the visible bounds
			} else {
				int y = this.scrollTextUtils.getYpos(animProgress, i);
				if (y < 0 || y > getHeight() + f.getSize()) {
					// do not paint, out of the visible bounds
				} else {
					double angle = this.scrollTextUtils.getRotate(animProgress, i);
					g2.rotate(angle, x, y);
					g2.setFont(f.deriveFont(this.scrollTextUtils.getTransformedFontSize(animProgress, f.getSize(), i)));
					g2.translate(x, y);
					this.scrollTextUtils.paintCharacter(g2, String.valueOf(this.getText().charAt(i)));
					g2.translate(-x, -y);
					g2.rotate(-angle, x, y);
				}
			}
		}
		g2.setFont(f);
	}

	public static interface IScrollTextUtils {
		int getYpos(float animProgress, int charnum);

		int getXpos(float animProgress, int charnum);

		double getRotate(float animProgress, final int charnum);

		float getTransformedFontSize(float animProgress, int fontSize, final int charnum);

		boolean isTimeTolaunchNewTimeline(float animProgress);

		void textChanged();

		void paintCharacter(Graphics2D g, String s);

		int getDuration();
	}

	public static abstract class AbstractScrollTextUtils implements IScrollTextUtils {

		protected ScrollingText scrollingText;
		protected int textWidth;
		protected int spaceBetweenLetter;
		private boolean drawShadow;
		private int pxSec;
		private int posX;
		private int posY;

		public AbstractScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow, int spaceBetweenLetter,
				int pxSec, int posX, int posY) {
			this.scrollingText = scrollingText;
			this.drawShadow = drawShadow;
			this.spaceBetweenLetter = spaceBetweenLetter;
			this.pxSec = pxSec;
			this.posX = posX;
			this.posY = posY;
		}

		@Override
		public float getTransformedFontSize(final float animProgress, final int fontSize, final int charnum) {
			return fontSize;
		}

		@Override
		public double getRotate(final float animProgress, final int charnum) {
			return 0;
		}

		@Override
		public boolean isTimeTolaunchNewTimeline(final float animProgress) {
			return animProgress > 0.7f;
		}

		public void textChanged() {
			this.textWidth = (int) ((this.spaceBetweenLetter) * this.scrollingText.getText().length()) + 50;
		}

		@Override
		public void paintCharacter(final Graphics2D g, final String s) {
			if (drawShadow) {
				// --shadow
				g.setColor(Color.black);
				g.drawString(s + "", posX + 2, posY + 2);
			}
			// --
			g.setColor(Color.black);
			g.drawString(s + "", posX, posY);
		}

		@Override
		public int getDuration() {
			int duration = (this.textWidth + this.scrollingText.getWidth()) / pxSec * 1000;
			return duration;
		}
	}

	// -------------------------------------------------------
	// -----------------------a few implementations-----------
	// -------------------------------------------------------

	// -----------------vertical

	public static abstract class AbstractVerticalScrollTextUtils extends AbstractScrollTextUtils {
		public AbstractVerticalScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getXpos(final float animProgress, final int charnum) {
			return (this.scrollingText.getWidth() - this.scrollingText.getFont().getSize()) / 2;
		}

		@Override
		public int getDuration() {
			int pxSec = 70;
			int duration = (this.textWidth + this.scrollingText.getHeight()) / pxSec * 1000;
			return duration;
		}
	}

	public static class DefaultTopToBottomScrollTextUtils extends AbstractVerticalScrollTextUtils {
		public DefaultTopToBottomScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getYpos(final float animProgress, final int charnum) {
			int y = this.scrollingText.getHeight() + this.textWidth;
			y = (int) (animProgress * y) + charnum * this.spaceBetweenLetter - this.textWidth;
			return y;
		}

		@Override
		public boolean isTimeTolaunchNewTimeline(final float animProgress) {
			int len = this.scrollingText.getText().length();
			if (len > 1) {
				int y1 = getYpos(animProgress, 0);
				int y2 = getYpos(animProgress, len - 1);
				if (y1 > this.scrollingText.getHeight() && y2 > (this.scrollingText.getHeight() / 2)) {
					return true;
				}
			}
			return super.isTimeTolaunchNewTimeline(animProgress);

		}
	}

	public static class DefaultBottomToTopScrollTextUtils extends AbstractVerticalScrollTextUtils {
		public DefaultBottomToTopScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getYpos(final float animProgress, final int charnum) {
			int y = this.scrollingText.getHeight() + this.textWidth;
			y = (int) ((1 - animProgress) * y) + charnum * this.spaceBetweenLetter - this.textWidth;
			return y;
		}

		@Override
		public boolean isTimeTolaunchNewTimeline(final float animProgress) {
			int len = this.scrollingText.getText().length();
			if (len > 1) {
				int y1 = getYpos(animProgress, 0);
				int y2 = getYpos(animProgress, len - 1);
				if (y1 < 0 && y2 < (this.scrollingText.getHeight() / 2)) {
					return true;
				}
			}
			return super.isTimeTolaunchNewTimeline(animProgress);
		}
	}

	// ----horizontal

	public static abstract class AbstractHorizontalTextUtils extends AbstractScrollTextUtils {
		public AbstractHorizontalTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getYpos(final float animProgress, final int charnum) {
			return (this.scrollingText.getHeight() + this.scrollingText.getFont().getSize()) / 2;
		}
	}

	public static class DefaultLeftToRightScrollTextUtils extends AbstractHorizontalTextUtils {
		public DefaultLeftToRightScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getXpos(final float animProgress, final int charnum) {
			int x = this.scrollingText.getWidth() + this.textWidth;
			x = (int) (animProgress * x) + charnum * this.spaceBetweenLetter - this.textWidth;
			return x;
		}

		@Override
		public boolean isTimeTolaunchNewTimeline(final float animProgress) {
			int len = this.scrollingText.getText().length();
			if (len > 1) {
				int x1 = getXpos(animProgress, 0);
				int x2 = getXpos(animProgress, len - 1);
				if (x1 > this.scrollingText.getWidth() && x2 > (this.scrollingText.getWidth() / 2)) {
					return true;
				}
			}
			return super.isTimeTolaunchNewTimeline(animProgress);
		}
	}

	public static class DefaultRightToLeftScrollTextUtils extends AbstractHorizontalTextUtils {
		public DefaultRightToLeftScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
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

	public static class DefaultRightToLeftCosineScrollTextUtils extends AbstractCosineScrollTextUtils {

		public DefaultRightToLeftCosineScrollTextUtils(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getXpos(final float animProgress, final int charnum) {
			int x = this.scrollingText.getWidth() + this.textWidth;
			x = (int) ((1f - animProgress) * x) + charnum * this.spaceBetweenLetter - this.textWidth;
			return x;
		}

		@Override
		public int getYpos(final float animProgress, final int charnum) {
			float x = getXpos(animProgress, charnum);
			double cos = Math.cos((double) Math.toRadians(x));
			int h = this.scrollingText.getHeight() - 2 * this.yOffset - this.scrollingText.getFont().getSize();
			int y = (int) (cos * (h / 2 - this.scrollingText.getFont().getSize() / 2))
					+ (this.scrollingText.getHeight() + this.scrollingText.getFont().getSize()) / 2;
			return y;
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

	public static class DefaultRightToLeftCosineScrollTextUtils2 extends AbstractCosineScrollTextUtils {

		public DefaultRightToLeftCosineScrollTextUtils2(final ScrollingText scrollingText, boolean drawShadow,
				int spaceBetweenLetter, int pxSec, int posX, int posY) {
			super(scrollingText, drawShadow, spaceBetweenLetter, pxSec, posX, posY);
		}

		@Override
		public int getXpos(final float animProgress, final int charnum) {
			int x = this.scrollingText.getWidth() + this.textWidth;
			x = (int) ((1f - animProgress) * x) + charnum * this.spaceBetweenLetter - this.textWidth;
			return x;
		}

		@Override
		public int getYpos(final float animProgress, final int charnum) {
			float x = getXpos(1 - animProgress, charnum);
			double cos = Math.cos((double) Math.toRadians(x));
			int h = this.scrollingText.getHeight() - 2 * this.yOffset - this.scrollingText.getFont().getSize();
			int y = (int) (cos * (h / 2 - this.scrollingText.getFont().getSize() / 2))
					+ (this.scrollingText.getHeight() + this.scrollingText.getFont().getSize()) / 2;
			return y;
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

	public String getText() {
		return text;
	}
}