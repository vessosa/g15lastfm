package com.vessosa.g15lastfmplayer.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;
import net.roarsoftware.lastfm.Authenticator;
import net.roarsoftware.lastfm.Caller;
import net.roarsoftware.lastfm.Playlist;
import net.roarsoftware.lastfm.Radio;
import net.roarsoftware.lastfm.Result;
import net.roarsoftware.lastfm.Session;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.scrobble.ResponseStatus;
import net.roarsoftware.lastfm.scrobble.Scrobbler;
import net.roarsoftware.lastfm.scrobble.Source;
import net.roarsoftware.xml.DomElement;

import org.apache.log4j.Logger;

import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.Config;
import com.vessosa.g15lastfmplayer.util.ELCDScreen;
import com.vessosa.g15lastfmplayer.util.mvc.DefaultModel;

public class MainModel extends DefaultModel {
	private static final Logger LOGGER = Logger.getLogger(MainModel.class);
	private boolean isPlaying = false;
	private boolean continuePlaying = false;
	private Session session;
	private Player player;
	private Radio radio;
	private Playlist playlist;
	private ArrayList<Track> tracks;
	private Thread playThread;
	private Thread progressThread;
	private Track currentTrack;
	private long startPlaybackTime;
	private Scrobbler scrobbler;
	private boolean hasSkipped = false;
	private List<String> recentRadios;
	private String currentMusicName;
	private Radio currentRadio;

	public MainModel() {
	}

	public void setShowStatuswMessage(final String message, final int displaySec) {
		new Thread() {

			@Override
			public void run() {
				firePropertyChange(Controller.STATUS_MESSAGE, null, message);
				firePropertyChange(Controller.SHOW_STATUS_MESSAGE, null, true);
				firePropertyChange(Controller.SHOW_MUSIC_PROGRESS, null, false);

				// show progress again
				try {
					Thread.sleep(displaySec * 1000);
					firePropertyChange(Controller.SHOW_STATUS_MESSAGE, null, false);
					if (isPlaying)
						firePropertyChange(Controller.SHOW_MUSIC_PROGRESS, null, true);
					else
						firePropertyChange(Controller.SHOW_MUSIC_PROGRESS, null, false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}.start();
	}

	public void setShowStatuswMessage(String message) {
		setShowStatuswMessage(message, 5);
	}

	public void setMusicName(String musicName) {
		LOGGER.debug("Setting music: " + musicName);
		firePropertyChange(Controller.MUSIC_NAME, null, musicName);
	}

	public void processSearchAndPlay(String radioToSearch) throws Exception {
		firePropertyChange(Controller.WORKING, null, true);
		Session session = login(Config.getValue(Config.USER), Config.getValue(Config.PASSWORD));
		Radio radio = tuneStation(radioToSearch, session, false);
		currentRadio = radio;
		processStop();
		continuePlaying = true;
		keepPlaying(radio, session);
	}

	public void processPlay() throws Exception {
		if (isPlaying) {
			LOGGER.info("Already playing, ignoring");
		} else {
			firePropertyChange(Controller.WORKING, null, true);
			Session session = login(Config.getValue(Config.USER), Config.getValue(Config.PASSWORD));
			String recentStation = getRecentStation(Config.getValue(Config.USER), session);
			Radio radio = tuneStation(recentStation, session, true);
			currentRadio = radio;
			continuePlaying = true;
			keepPlaying(radio, session);
		}
	}

	public void processBan() throws Exception {
		if (isPlaying && currentTrack != null) {
			Session session = login(Config.getValue(Config.USER), Config.getValue(Config.PASSWORD));
			Result result = Track.ban(currentTrack.getArtist(), currentTrack.getName(), session);
			if (result.isSuccessful()) {
				LOGGER.debug("LOVE IT!");
				setShowStatuswMessage("BANNED");
			} else {
				LOGGER.debug("Not possible to set BAN: " + result.getErrorMessage());
			}
			processSkip();
		}
	}

	public void processLove() throws Exception {
		if (isPlaying && currentTrack != null) {
			Session session = login(Config.getValue(Config.USER), Config.getValue(Config.PASSWORD));
			Result result = Track.love(currentTrack.getArtist(), currentTrack.getName(), session);
			if (result.isSuccessful()) {
				LOGGER.debug("LOVE IT!");
				setShowStatuswMessage("LOVE IT!");
			} else {
				LOGGER.debug("Not possible to set Love: " + result.getErrorMessage());
			}

		}
	}

	public void processStop() {
		while (isPlaying) {
			if (player != null) {
				hasSkipped = true;
				continuePlaying = false;
				player.close();
			}
		}
		setMusicName("Stopped");
		setShowStatuswMessage(currentRadio.getStationName(), 20);
	}

	public void processSkip() {
		while (isPlaying) {
			if (player != null) {
				hasSkipped = true;
				player.close();
			}
		}
	}

	private void scrobbleSong(Session session) {
		long now = System.currentTimeMillis() / 1000;
		long totalPlayed = now - startPlaybackTime;
		if (totalPlayed > 40) {
			try {
				Scrobbler scrobbler = getScrobbler(session);
				if (scrobbler != null) {
					ResponseStatus status = scrobbler.submit(currentTrack.getArtist(), currentTrack.getName(),
							currentTrack.getAlbum(), currentTrack.getDuration(), currentTrack.getPosition(),
							Source.USER, startPlaybackTime);
					LOGGER.debug("Submit OK: " + status.ok());
				} else {
					LOGGER.error("Can't scrobble now");
				}
			} catch (Exception e) {
				LOGGER.debug(e);
			}
		}
	}

	InputStream in;

	public void playStreaming(String path) {
		if (isPlaying)
			return;
		try {
			AudioDevice dev = getAudioDevice();
			player = new Player(getURLInputStream(path), dev);
			isPlaying = true;
			player.play();
			in.close();
			LOGGER.debug("------------------------------------------------- FIM DO PLAY");
			isPlaying = false;
		} catch (UnknownHostException e) {
			showErrorMessage("Last.fm seems to be down, please try again later.");
		} catch (IOException e) {
			processStop();
			firePropertyChange(Controller.MUSIC_NAME, null, "ERROR - STOPPED");
			e.printStackTrace();
		} catch (Exception e) {
			processStop();
			LOGGER.debug("Exception: " + e.getCause());
		}
	}

	private InputStream getURLInputStream(String urlPath) throws Exception {
		if (in != null)
			in.close();

		URL url = new URL(urlPath);
		in = url.openStream();
		BufferedInputStream bin = new BufferedInputStream(in);
		return bin;
	}

	private AudioDevice getAudioDevice() throws JavaLayerException {
		return FactoryRegistry.systemRegistry().createAudioDevice();
	}

	private ResponseStatus scrobblerResponse = new ResponseStatus(ResponseStatus.FAILED);

	private Scrobbler getScrobbler(Session session) {
		if (!scrobblerResponse.ok()) {
			try {
				scrobbler = Scrobbler.newScrobbler("tst", "1.0", Config.getValue(Config.USER));
				scrobblerResponse = scrobbler.handshake(session);
			} catch (Exception e) {
				LOGGER.debug(e);
				return null;
			}
		}
		return scrobbler;
	}

	private void prepareProgressBar() {
		if (player != null) {
			if (isPlaying) {
				int duration = currentTrack.getDuration() * 1000;
				int cPos = (player.getPosition() * 100) / duration;
				firePropertyChange(Controller.MUSIC_PROGRESS, null, cPos);
			}
		}
	}

	private void showErrorMessage(String string) {
		LOGGER.error(string);
	}

	public List<String> getRecentStations(String user, Session session) {
		List<String> recentStations = new ArrayList<String>();
		Result result = Caller.getInstance().call("user.getRecentStations", session, "user", user);
		if (!result.isSuccessful())
			return Collections.emptyList();
		DomElement element = result.getContentElement();
		for (DomElement stationElement : element.getChildren("station")) {
			// for (DomElement lista : stationElement.getChildren()) {
			// LOGGER.debug(lista.getTagName());
			// }
			// for (DomElement urlElement : stationElement.getChildren("name"))
			// {
			// LOGGER.debug(urlElement.getText());
			// }
			for (DomElement urlElement : stationElement.getChildren("url")) {
				// LOGGER.debug(urlElement.getText());
				recentStations.add(urlElement.getText());
			}
			// radioFromElement(domElement);
			// radios.add(radioFromElement(domElement));
			// tracks.add(Track.trackFromElement(domElement));
		}
		return recentStations;
	}

	public void callSoftButton1() {
	}

	public void callSearch() {
		firePropertyChange(Controller.SHOW_SEARCH_DIALOG, false, true);
		setShowStatuswMessage("Search", 14);
	}

	private Session login(String user, String password) throws Exception {
		// Authenticate user
		if (session == null) {
			String apiKey = "90120304ba34b682c26aec08425d80e4";
			String apiSig = "a35553cb9a5bb4be934a284ffb114b46";
			try {
				LOGGER.debug("Authenticating...");
				session = Authenticator.getMobileSession(user, password, apiKey, apiSig);

			} catch (Exception e) {
				if (e instanceof UnknownHostException) {
					showErrorMessage("Last.fm seens to be down, please try again later.");
				}
			}
		}
		if (session == null) {
			throw new Exception("Could not login on Last.fm, may be wrong password?");
		}
		return session;
	}

	private Radio tuneStation(String radioName, Session session, boolean tuneKnownRadio) throws Exception {
		Radio radio = null;
		if (tuneKnownRadio) {
			// Tune a real radio
			radio = Radio.tune(radioName, Locale.US, session);
		} else {
			// Tune for similar artists
			radio = Radio.tune(Radio.RadioStation.similarArtists(radioName), session);
		}
		if (radio == null) {
			throw new Exception("Nothing found with this radio name: " + radioName);
		} else {
			LOGGER.debug("Radio tunned: " + radio.getStationName());
		}
		return radio;
	}

	private String getRecentStation(String user, Session session) {
		List<String> recentStations = new ArrayList<String>();
		Result result = Caller.getInstance().call("user.getRecentStations", session, "user", user);
		if (!result.isSuccessful())
			return null;
		DomElement element = result.getContentElement();
		for (DomElement stationElement : element.getChildren("station")) {
			// for (DomElement lista : stationElement.getChildren()) {
			// LOGGER.debug(lista.getTagName());
			// }
			// for (DomElement urlElement : stationElement.getChildren("name"))
			// {
			// LOGGER.debug(urlElement.getText());
			// }
			for (DomElement urlElement : stationElement.getChildren("url")) {
				// LOGGER.debug(urlElement.getText());
				recentStations.add(urlElement.getText());
			}
			// radioFromElement(domElement);
			// radios.add(radioFromElement(domElement));
			// tracks.add(Track.trackFromElement(domElement));
		}
		return recentStations.get(0);
	}

	private List<Track> getPlayListTracks(Radio radio) {
		Playlist playlist = radio.getPlaylist();
		return new ArrayList<Track>(playlist.getTracks());
	}

	private void keepPlaying(final Radio radio, final Session session) {
		prepareProgressThread();
		setShowStatuswMessage(radio.getStationName(), 20);
		playThread = new Thread() {

			@Override
			public void run() {
				List<Track> tracks = new ArrayList<Track>();
				while (continuePlaying) {
					if (tracks.size() == 0) {
						LOGGER.debug("Retrieving new playlist");
						tracks = getPlayListTracks(radio);
						LOGGER.debug("Retrieved " + tracks.size() + " tracks");
					}
					setShowStatuswMessage(radio.getStationName(), 20);
					startPlaybackTime = System.currentTimeMillis() / 1000;
					currentTrack = tracks.get(0);
					nowPlaying(session, currentTrack.getArtist(), currentTrack.getName());
					firePropertyChange(Controller.WORKING, null, false);
					playStreaming(currentTrack.getLocation());
					if (!hasSkipped)
						scrobbleSong(session);

					if (tracks.size() > 0) {
						tracks.remove(0);
						LOGGER.debug("Tracks available: " + tracks.size());
					}
				}
			}
		};

		playThread.setName("playThread");
		playThread.start();
	}

	private void nowPlaying(final Session session, final String artist, final String name) {
		new Thread() {

			@Override
			public void run() {
				try {
					setMusicName(artist + " - " + name);
					Scrobbler scrobbler = getScrobbler(session);
					if (scrobbler != null) {
						ResponseStatus status = scrobbler.nowPlaying(artist, name);
						LOGGER.debug("Submit OK: " + status.ok());
					} else {
						LOGGER.error("Can't scrobble now");
					}
				} catch (Exception e) {
					LOGGER.debug("Error on calling nowPlaying: " + e.getMessage());
				}

			}

		}.start();
	}

	private void prepareProgressThread() {
		firePropertyChange(Controller.SET_LCD_SCREEN, null, ELCDScreen.SCROLLING_TEXT_VIEW);
		if (progressThread == null) {
			progressThread = new Thread() {
				@Override
				public void run() {
					for (;;)
						prepareProgressBar();
				}
			};
			progressThread.setName("progressThread");
			progressThread.start();
		}
	}
}
