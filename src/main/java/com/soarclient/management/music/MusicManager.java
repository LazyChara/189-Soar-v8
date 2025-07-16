package com.soarclient.management.music;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.soarclient.libraries.flac.FLACDecoder;
import com.soarclient.libraries.flac.metadata.Metadata;
import com.soarclient.libraries.flac.metadata.Picture;
import com.soarclient.libraries.flac.metadata.VorbisComment;
import com.soarclient.utils.RandomUtils;
import com.soarclient.utils.file.FileLocation;
import com.soarclient.utils.file.FileUtils;

import net.minecraft.client.Minecraft;

public class MusicManager {

	private List<Music> musics = new CopyOnWriteArrayList<>();

	private Music currentMusic;
	private MusicPlayer musicPlayer;
	private boolean shuffle;
	private boolean repeat;

	public MusicManager() {

		try {
			load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.musicPlayer = new MusicPlayer(() -> {

			Music nextMusic;

			if (repeat) {
				nextMusic = currentMusic;
			} else if (shuffle) {
				nextMusic = musics.get(RandomUtils.getRandomInt(0, musics.size() - 1));
			} else {
				nextMusic = null;
			}

			setCurrentMusic(nextMusic);

			if (currentMusic != null) {
				play();
			} else {
				musicPlayer.setPlaying(false);
			}
		});
		this.shuffle = false;
		this.repeat = false;
		new Thread("Music Thread") {
			@Override
			public void run() {
				while (Minecraft.getMinecraft().isRunning()) {
					musicPlayer.run();
				}
			}
		}.start();
	}

	public void load() throws Exception {

		File musicDir = FileLocation.MUSIC_DIR;

		if (musicDir.listFiles() == null) {
			return;
		}

		for (File f : musicDir.listFiles()) {

			String name = f.getName();

			if (name.endsWith(".flac")) {

				FLACDecoder decoder = new FLACDecoder(new FileInputStream(f));
				Metadata[] metadata = decoder.readMetadata();
				String title = null;
				String artist = null;
				byte[] imageData = null;

				for (Metadata meta : metadata) {
					if (meta instanceof VorbisComment) {
						VorbisComment comment = (VorbisComment) meta;
						if (comment.getCommentByName("TITLE").length > 0) {
							title = comment.getCommentByName("TITLE")[0];
						}
						if (comment.getCommentByName("ARTIST").length > 0) {
							artist = comment.getCommentByName("ARTIST")[0];
						}
					} else if (meta instanceof Picture) {
						Picture picture = (Picture) meta;
						imageData = picture.getImage();
					}
				}

				String fileHash = FileUtils.getMd5Checksum(f);
				File album = new File(FileLocation.CACHE_DIR, fileHash);

				if (imageData != null && !album.exists()) {

					FileOutputStream fos = new FileOutputStream(album);

					fos.write(imageData);
					fos.close();
				}

				musics.add(new Music(f, title == null ? f.getName().replace(".flac", "") : title,
						artist == null ? "" : artist, album.exists() ? album : null));
			}
		}
	}

	public void play() {

		if (currentMusic == null) {
			return;
		}

		setVolume(getVolume());
		musicPlayer.setCurrentMusic(currentMusic);
	}

	public float getVolume() {
		return musicPlayer.getVolume();
	}

	public void setVolume(float volume) {
		musicPlayer.setVolume(volume);
	}

	public void next() {

		if (currentMusic == null) {
			return;
		}

		int max = musics.size();
		int index = musics.indexOf(currentMusic);

		if (index < max - 1) {
			index++;
		} else {
			index = 0;
		}

		currentMusic = musics.get(index);
		play();
	}

	public void back() {

		if (currentMusic == null) {
			return;
		}

		int max = musics.size();
		int index = musics.indexOf(currentMusic);

		if (index > 0) {
			index--;
		} else {
			index = max - 1;
		}

		currentMusic = musics.get(index);
		play();
	}

	public void switchPlayBack() {
		musicPlayer.setPlaying(!musicPlayer.isPlaying());
	}

	public void stop() {
		musicPlayer.setPlaying(false);
	}

	public boolean isPlaying() {
		return musicPlayer.isPlaying();
	}

	public float getCurrentTime() {
		return musicPlayer.getCurrentTime();
	}

	public float getEndTime() {
		return musicPlayer.getEndTime();
	}

	public List<Music> getMusics() {
		return musics;
	}

	public Music getCurrentMusic() {
		return currentMusic;
	}

	public void setCurrentMusic(Music currentMusic) {
		this.currentMusic = currentMusic;
	}

	public boolean isShuffle() {
		return shuffle;
	}

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
}
