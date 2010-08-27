package com.vessosa.g15lastfmplayer.view;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.djpowell.lcdjni.AppletCapability;
import net.djpowell.lcdjni.DeviceType;
import net.djpowell.lcdjni.LcdConnection;
import net.djpowell.lcdjni.LcdDevice;
import net.djpowell.lcdjni.LcdException;
import net.djpowell.lcdjni.LcdMonoBitmap;
import net.djpowell.lcdjni.PixelColor;
import net.djpowell.lcdjni.Priority;
import net.djpowell.lcdjni.SyncType;

import org.apache.log4j.Logger;

import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.ELCDScreen;
import com.vessosa.g15lastfmplayer.util.mvc.AbstractView;

public class LCDScreen implements AbstractView {
	private static final Logger LOGGER = Logger.getLogger(LCDScreen.class);
	public static boolean stop;
	private ELCDScreen currentScreen;
	private int paintDuration = 10;
	private Controller controller;
	private final MusicScroll musicScroll;
	private final StatusScroll statusScroll;
	private ChooseRadioScreen radioChooser;
	private static Image mainImage;
	private static Font smallFont;
	private static Font bigFont;

	public LCDScreen(Controller controller) {
		this.controller = controller;

		radioChooser = new ChooseRadioScreen(controller);
		musicScroll = new MusicScroll();
		getController().addView(musicScroll);
		getController().addView(radioChooser);

		statusScroll = new StatusScroll();
		getController().addView(statusScroll);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				musicScroll.setText("G15LastfmPlayer");
				statusScroll.setText("Stopped");
				// st.setText("Three Days Grace - I Hate Everything About You");
			}
		});
	}

	public void init() {
		try {
			LcdConnection con = new LcdConnection("G15LastfmPlayer", false,
					AppletCapability.getCaps(AppletCapability.BW), null, null);

			LcdDevice device = con.openDevice(DeviceType.BW, new G15KeysImplementationView(controller));
			LcdMonoBitmap bmp = device.createMonoBitmap(PixelColor.G15_REV_1);
			musicScroll.drawMainScreen(bmp.getGraphics());
			statusScroll.drawStatusMessage(bmp.getGraphics());
			bmp.updateScreen(Priority.ALERT, SyncType.SYNC);
			device.setForeground(true);
			currentScreen = ELCDScreen.SCROLLING_TEXT_VIEW;
			while (!stop) {
				try {
					Thread.sleep(paintDuration);
				} catch (InterruptedException e) {
					break;
				}
				switch (currentScreen) {
				case SCROLLING_TEXT_VIEW:
					musicScroll.drawMainScreen(bmp.getGraphics());
					statusScroll.drawStatusMessage(bmp.getGraphics());
					break;
				case SEARCH_VIEW:
					radioChooser.drawChooserAnimation(bmp.getGraphics());
					// currentScreen = ELCDScreen.SCROLLING_TEXT_VIEW;
					break;
				default:
					break;
				}
				bmp.updateScreen(Priority.ALERT, SyncType.SYNC);
			}

			device.close();
			con.close();
			LcdConnection.deInit();
		} catch (LcdException e) {
			JOptionPane.showMessageDialog(null,
					"An error has occurred. Please check if Logitech G15 drivers are installed and try again.",
					"G15LastfmPlayer", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public static Font getSmallFont() {
		if (smallFont == null) {
			String fontFileName = "7PX2BUS.TTF";

			try {
				Font tempFont = Font
						.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream(fontFileName));
				smallFont = new Font(tempFont.getName(), tempFont.getStyle(), 8);
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.debug(e);
				JOptionPane.showMessageDialog(null, "Can't find 7PX2BUS.TTF font!", "G15LastfmPlayer",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}

		}
		return smallFont;
	}

	public static Font getBigFont() {
		if (bigFont == null) {
			String fontFileName = "7PX2BUS.TTF";

			try {
				Font tempFont = Font
						.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResourceAsStream(fontFileName));
				bigFont = new Font(tempFont.getName(), tempFont.getStyle(), 14);
			} catch (FontFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.debug(e);
				JOptionPane.showMessageDialog(null, "Can't find 7PX2BUS.TTF font!", "G15LastfmPlayer",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}

		}
		return bigFont;
	}

	public static Image getMainImage() {
		if (mainImage == null) {
			// Read from a file
			try {
				mainImage = ImageIO.read(getResource("main.bmp"));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return mainImage;
	}

	public static URL getResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("resources/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(Controller.SET_LCD_SCREEN)) {
			currentScreen = (ELCDScreen) evt.getNewValue();
		}
	}

	public Controller getController() {
		return controller;
	}
}
