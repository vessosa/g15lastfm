package com.vessosa.g15lastfmplayer.controller;

import com.vessosa.g15lastfmplayer.util.mvc.DefaultController;

public class Controller extends DefaultController {

	// Constants here
	public static final String SHOW_STATUS_MESSAGE = "ShowStatusMessage";
	public static final String STATUS_MESSAGE = "StatusMessage";
	public static final String MUSIC_NAME = "MusicName";
	public static final String SHOW_MUSIC_PROGRESS = "ShowMusicProgress";
	public static final String MUSIC_PROGRESS = "MusicProgress";
	public static final String SET_LCD_SCREEN = "SetLCDScreen";
	public static final String ANIM_TO_NEXT_RADIO = "AnimToNextRadio";
	public static final String SHOW_SEARCH_DIALOG = "ShowSearchDialog";
	public static final String POPULATE_RADIO_NAMES = "PopulateRadioNames";
	public static final String WORKING = "Working";

	public void play() throws Exception {
		processModelAction("Play");
	}

	public void stop() throws Exception {
		processModelAction("Stop");
	}

	public void skip() throws Exception {
		processModelAction("Skip");
	}

	public void love() throws Exception {
		processModelAction("Love");
	}

	public void ban() throws Exception {
		processModelAction("Ban");
	}

	public void search() throws Exception {
		callModelAction("Search");
	}

	public void softButton1() throws Exception {
		callModelAction("SoftButton1");
	}

	public void searchAndPlay(String radio) throws Exception {
		processModelAction("SearchAndPlay", radio);
	}

	public void checkUpdate() throws Exception {
		callModelAction("Update");
	}
}
