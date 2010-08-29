package com.vessosa.g15lastfmplayer.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.log4j.Logger;

public class InstanceChecker {

	private static final Logger LOGGER = Logger.getLogger(InstanceChecker.class);
	private static File f;
	private static FileChannel channel;
	private static FileLock lock;

	public InstanceChecker() {
		try {
			f = new File("g15lastfmInstance.lock");
			// Check if the lock exist
			if (f.exists()) {
				// if exist try to delete it
				f.delete();
			}

			// Try to get the lock
			channel = new RandomAccessFile(f, "rw").getChannel();
			lock = channel.tryLock();
			if (lock == null) {
				// File is lock by other application
				channel.close();
				throw new RuntimeException("G15LastfmPlayer already running");
			}

			// Add shutdown hook to release lock when application shutdown
			ShutdownHook shutdownHook = new ShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownHook);

			// Your application tasks here..
			LOGGER.debug("Running G15Lastfm Player");
		} catch (IOException e) {
			throw new RuntimeException("Could not start process.", e);
		}
	}

	public static void unlockFile() {
		// release and delete file lock
		try {
			if (lock != null) {
				lock.release();
				channel.close();
				f.delete();
			}
		} catch (IOException e) {
			LOGGER.debug(e);
		}
	}

	static class ShutdownHook extends Thread {

		public void run() {
			unlockFile();
		}
	}
}
