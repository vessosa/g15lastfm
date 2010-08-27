package com.vessosa.g15lastfmplayer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.TwilightSkin;

import com.melloware.jintellitype.JIntellitype;
import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.model.MainModel;
import com.vessosa.g15lastfmplayer.util.Config;
import com.vessosa.g15lastfmplayer.util.InstanceChecker;
import com.vessosa.g15lastfmplayer.view.G15KeysImplementationView;
import com.vessosa.g15lastfmplayer.view.LCDScreen;
import com.vessosa.g15lastfmplayer.view.RadioSearchScreen;

public class G15LastfmPlayer {
	private static boolean running = true;

	public static void main(String[] args) {
		// Checkinstance
		new InstanceChecker();
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new TwilightSkin());
			}

		});
		Config.initConfig();
		Controller controller = new Controller();
		controller.addModel(new MainModel());

		G15KeysImplementationView g15keys = new G15KeysImplementationView(controller);
		controller.addView(g15keys);

		RadioSearchScreen radioScreen = new RadioSearchScreen(controller);
		controller.addView(radioScreen);

		JIntellitype.getInstance();
		JIntellitype.getInstance().addIntellitypeListener(g15keys);

		LCDScreen lcdScreen = new LCDScreen(controller);
		controller.addView(lcdScreen);

		try {
			lcdScreen.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (running) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		JIntellitype.getInstance().unregisterHotKey(1);
		JIntellitype.getInstance().cleanUp();
		System.exit(0);
	}

}
