package com.vessosa.g15lastfmplayer.view;

import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JOptionPane;

import net.djpowell.lcdjni.KeyCallback;

import org.apache.log4j.Logger;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.mvc.AbstractView;

public class G15KeysImplementationView implements IntellitypeListener, KeyCallback, AbstractView {
	private Controller controller;
	private static final Logger LOGGER = Logger.getLogger(G15KeysImplementationView.class);
	private final int G15_KEY_1 = 1;
	private final int G15_KEY_2 = 2;
	private final int G15_KEY_3 = 4;
	private final int G15_KEY_4 = 8;

	public G15KeysImplementationView(Controller controller) {
		this.controller = controller;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {

			@Override
			public boolean postProcessKeyEvent(KeyEvent evt) {
				return false;
			}
		});
	}

	// Media keys
	@Override
	public void onIntellitype(int type) {
		switch (type) {
		case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
			skipAction();
			break;
		case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
			playAction();
			break;
		case JIntellitype.APPCOMMAND_MEDIA_STOP:
			stopAction();
			break;
		default:
			// LOGGER.debug(type);
			break;
		}
	}

	// g15 buttons
	@Override
	public void onKey(int button) {

	}

	@Override
	public void onKeyDown(int arg0) {

	}

	@Override
	public void onKeyUp(int button) {
		System.out.println("Button pressed: " + button);
		switch (button) {
		case G15_KEY_1:
			button1Action();
			break;
		case G15_KEY_2:
			button2Action();
			break;
		case G15_KEY_3:
			banAction();
			break;
		case G15_KEY_4:
			loveAction();
			break;
		default:
			break;
		}
	}

	private void button1Action() {
		try {
			controller.softButton1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void button2Action() {
		try {
			LOGGER.debug("Search");
			controller.search();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void loveAction() {
		try {
			LOGGER.debug("Loving..");
			controller.love();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "G15Lastfm Player",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void banAction() {
		try {
			LOGGER.debug("Banning..");
			controller.ban();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "G15Lastfm Player",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void skipAction() {
		try {
			LOGGER.debug("skip to next track..");
			controller.skip();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "G15Lastfm Player",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void playAction() {
		try {
			LOGGER.debug("Playing..");
			controller.play();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "G15Lastfm Player",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void stopAction() {
		try {
			LOGGER.debug("Stopping..");
			controller.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {

	}

}
