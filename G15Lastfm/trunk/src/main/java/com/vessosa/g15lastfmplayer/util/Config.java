package com.vessosa.g15lastfmplayer.util;

public class Config {
	public static final String USER = "user";
	public static final String PASSWORD = "password";

	public static String getValue(String value) {
		if (value.equals(USER))
			return "vessosa";
		if (value.equals(PASSWORD))
			return "12345";
		return null;
	}
}
