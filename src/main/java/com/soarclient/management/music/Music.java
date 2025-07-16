package com.soarclient.management.music;

import java.io.File;

public class Music {

	private final File audio;
	private final String title, artist;
	private final File album;

	public Music(File audio, String title, String artist, File album) {
		this.audio = audio;
		this.title = title;
		this.artist = artist;
		this.album = album;
	}

	public File getAudio() {
		return audio;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public File getAlbum() {
		return album;
	}
}
