package com.vessosa.g15lastfmplayer.util;

import org.apache.log4j.Logger;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.WString;

public class MSNSendPlayingInfo {
	private static final Logger LOGGER = Logger.getLogger(MSNSendPlayingInfo.class);
	private User32 user32Lib;
	private static final int WM_COPYDATA = 74;
	private int hWnd = -1;

	public interface User32 extends Library {
		public int SendMessageA(int hWnd, int Msg, int wParam, Structure.ByReference lparam);

		public int FindWindowA(String lpClassName, String lpWindowName);

	}

	public MSNSendPlayingInfo() {
		user32Lib = (User32) Native.loadLibrary("User32", User32.class);
	}

	private int getWindowHandler() {
		if (hWnd == -1) {
			hWnd = user32Lib.FindWindowA("MsnMsgrUIManager", null);
		}
		return hWnd;
	}

	public void sendPlayingInfo(String title, String artist, String album, boolean isVisible) {
		if (getWindowHandler() == -1)
			return;

		if (!Config.getValueAsBoolean(Config.ENABLE_MSN_PLAYING_INFO))
			isVisible = false;

		if (getWindowHandler() == 0) {
			if (isVisible)
				LOGGER.debug("MSN is not running");
			return;
		}

		String category = "Music";
		// String category = "Games";
		// String category = "Office";

		String displayFormat = null;
		if (Config.getValueAsBoolean(Config.SWAP_MUSIC_NAME_ARTIST_)) {
			displayFormat = "{0} - {1}";
		} else {
			displayFormat = "{1} - {0}";
		}
		WString message = new WString("\\0" + category + "\\0" + (isVisible ? "1" : "0") + "\\0" + displayFormat
				+ "\\0" + title + "\\0" + artist + "\\0" + album + "\\0\\0\0");
		DataStructure.ByReference data = new DataStructure.ByReference();
		data.dwData = 0x0547;
		data.lpData = message;
		data.cbData = (message.length() * 2) + 2;
		user32Lib.SendMessageA(getWindowHandler(), WM_COPYDATA, 0, data);
	}

	public static class DataStructure extends Structure {
		public static class ByReference extends DataStructure implements Structure.ByReference {
		};

		public int dwData;
		public int cbData;
		public WString lpData;

		protected ByReference newByReference() {
			return new ByReference();
		}
	}
}
