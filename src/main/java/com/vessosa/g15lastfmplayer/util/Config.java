package com.vessosa.g15lastfmplayer.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.auth.LoginService;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Config {
	private static final Logger LOGGER = Logger.getLogger(Config.class);
	private static SecretKey key;
	private static Properties properties = new Properties();
	public static final String USER = "username";
	public static final String PASSWORD = "password";
	public static final String ENABLE_MSN_PLAYING_INFO = "enableMsnPlayingInfo";
	public static final String SWAP_MUSIC_NAME_ARTIST_ = "swapMusicNameArtist";
	public static final String CHECK_FOR_UPDATES_ON_STARTUP = "checkForUpdatesOnStartup";

	public static void initConfig() {
		try {
			properties.load(getResource("g15lastfm.properties").openStream());
		} catch (FileNotFoundException e) {
			LOGGER.debug(e);
		} catch (IOException e) {
			LOGGER.debug(e);
		}

		// System.out.println(getResource("g15lastfm.properties").getFile());
		if (getValue(USER) == null || getValue(USER).length() == 0) {
			askLoginInformation();
		}
	}

	public static void askLoginInformation() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JXLoginPane loginPanel = new JXLoginPane(new LoginService() {

					@Override
					public boolean authenticate(String arg0, char[] arg1, String arg2) throws Exception {
						return true;
					}
				});
				loginPanel.setMessage("Please your password for Last.fm account:");
				loginPanel.setBannerText("Last.fm login");
				if (JXLoginPane.showLoginDialog(null, loginPanel) == JXLoginPane.Status.SUCCEEDED) {
					properties.setProperty(USER, loginPanel.getUserName());
					String encPassword = encryptPassword(String.valueOf(loginPanel.getPassword()));
					properties.setProperty(PASSWORD, encPassword);
					// Write properties file.
					try {
						properties.store(new FileOutputStream(getResource("g15lastfm.properties").getPath()), null);
					} catch (IOException e) {
						LOGGER.debug(e);
					}
				}
			}

		});
	}

	public static String getValue(String value) {
		if (value.equals(PASSWORD)) {
			String v = properties.getProperty(value);
			if (v != null)
				v = decryptPassword(v);
			return v;
		}
		return properties.getProperty(value);
	}

	public static boolean getValueAsBoolean(String value) {
		if (properties.getProperty(value).toLowerCase().equals("false"))
			return false;
		else if (properties.getProperty(value).toLowerCase().equals("true"))
			return true;
		else
			LOGGER.error("Invalid boolean value: " + properties.getProperty(value));
		return false;
	}

	public static URL getResource(final String fileName) {
		URL url = ClassLoader.getSystemResource("resources/" + fileName);
		if (url == null) {
			url = ClassLoader.getSystemResource(fileName);
		}
		return url;
	}

	private final static String encryptPassword(String password) {
		String encrypedPwd = "";
		try {
			BASE64Encoder base64encoder = new BASE64Encoder();

			byte[] cleartext = password.getBytes("UTF8");

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, getKey());
			encrypedPwd = base64encoder.encode(cipher.doFinal(cleartext));
		} catch (Exception e) {
			LOGGER.debug(e);
		}

		return encrypedPwd;
	}

	private static String decPassword;

	public static final String decryptPassword(String encryptedPassword) {
		if (decPassword == null) {
			try {
				BASE64Decoder base64decoder = new BASE64Decoder();
				byte[] encrypedPwdBytes = base64decoder.decodeBuffer(encryptedPassword);

				Cipher cipher = Cipher.getInstance("DES");
				cipher.init(Cipher.DECRYPT_MODE, getKey());
				byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
				decPassword = new String(plainTextPwdBytes);
			} catch (Exception e) {
				LOGGER.debug(e);
			}
		}
		return decPassword;
	}

	private static SecretKey getKey() {
		if (key == null) {
			try {
				DESKeySpec keySpec = new DESKeySpec(
						"164MY_REAAAAAALY_SECURE_KEY_3434_OK_I_KNOW_THIS_IS_NOT_REALLY_SECURE_BUT_CAN_HELP_YOU_TO_HIDE_YOUR_PASSWORD"
								.getBytes("UTF8"));

				SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
				key = keyFactory.generateSecret(keySpec);
			} catch (Exception e) {
				LOGGER.debug(e);
			}
		}
		return key;
	}
}
