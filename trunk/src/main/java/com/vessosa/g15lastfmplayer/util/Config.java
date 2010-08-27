package com.vessosa.g15lastfmplayer.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;

public class Config {
	private static Properties properties = new Properties();
	public static final String USER = "username";
	public static final String PASSWORD = "password";

	public static void initConfig() {
		try {
			properties.load(getResource("g15lastfm.properties").openStream());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(getResource("g15lastfm.properties").getFile());
		if (getValue(USER) == null || getValue(USER).length() == 0) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JXLoginPane loginPanel = new JXLoginPane(new LoginService() {

						@Override
						public boolean authenticate(String arg0, char[] arg1, String arg2) throws Exception {
							return true;
						}
					});
					loginPanel.setBannerText("Last.fm login");
					if (JXLoginPane.showLoginDialog(null, loginPanel) == JXLoginPane.Status.SUCCEEDED) {
						properties.setProperty(USER, loginPanel.getUserName());
						properties.setProperty(PASSWORD, String.valueOf(loginPanel.getPassword()));
						// Write properties file.
						try {
							properties.store(new FileOutputStream(getResource("g15lastfm.properties").getPath()), null);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			});

		}
	}

	public static String getValue(String value) {
		return properties.getProperty(value);
	}

	public static URL getResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("resources/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}
}
