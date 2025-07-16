package com.soarclient.gui.modmenu.component;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.Soar;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.music.Music;
import com.soarclient.management.music.MusicManager;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.Component;
import com.soarclient.utils.mouse.MouseUtils;

public class MusicControlBar extends Component {

	private List<ControlButton> buttons = new ArrayList<>();
	private boolean locked;

	public MusicControlBar(float x, float y, float width) {
		super(x, y);
		this.width = width;
		this.height = 64;

		MusicManager musicManager = Soar.getInstance().getMusicManager();

		float offsetY = 26;

		buttons.add(new ControlButton(Icon.REPEAT, 0, y + offsetY, () -> {
			musicManager.setShuffle(false);
			musicManager.setRepeat(!musicManager.isRepeat());
		}));
		buttons.add(new ControlButton(Icon.SKIP_PREVIOUS, 0, y + offsetY, () -> {
			musicManager.back();
		}));
		buttons.add(new ControlButton(musicManager.isPlaying() ? Icon.PAUSE : Icon.PLAY_ARROW, 0, y + offsetY, () -> {
			musicManager.switchPlayBack();
		}));
		buttons.add(new ControlButton(Icon.SKIP_NEXT, 0, y + offsetY, () -> {
			musicManager.next();
		}));
		buttons.add(new ControlButton(Icon.SHUFFLE, 0, y + offsetY, () -> {
			musicManager.setRepeat(false);
			musicManager.setShuffle(!musicManager.isShuffle());
		}));

		float totalWidth = (buttons.size() * 22) + ((buttons.size() - 1) * 2);
		float centerX = x + (width / 2);
		float offsetX = centerX - (totalWidth / 2);

		for (ControlButton b : buttons) {
			b.setX(offsetX);
			offsetX += 30;
		}
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		Soar instance = Soar.getInstance();
		ColorPalette palette = instance.getColorManager().getPalette();
		MusicManager musicManager = instance.getMusicManager();
		Music music = musicManager.getCurrentMusic();

		Skia.drawRoundedRect(x, y, width, height, 16, palette.getSurface());

		File album = music != null ? music.getAlbum() : null;

		if (album != null) {
			Skia.drawRoundedImage(album, x + 8, y + 8, 48, 48, 10);
		} else {
			Skia.drawRoundedRect(x + 8, y + 8, 48, 48, 10, palette.getSurfaceContainerHigh());
		}

		if (music != null) {

			String limitedTitle = Skia.getLimitText(music.getTitle(), Fonts.getRegular(16), 170);
			String limitedArtist = Skia.getLimitText(music.getArtist(), Fonts.getRegular(12), 170);

			Skia.drawText(limitedTitle, x + 66, y + 17, palette.getOnSurface(), Fonts.getRegular(16));
			Skia.drawText(limitedArtist, x + 66, y + 34, palette.getOnSurfaceVariant(), Fonts.getRegular(12));
		}

		float seekWidth = 308;
		float seekHeight = 6;

		drawSeekBar(x + (width / 2) - (seekWidth / 2), y + height - seekHeight - 12, seekWidth, seekHeight);

		buttons.get(2).icon = musicManager.isPlaying() ? Icon.PAUSE : Icon.PLAY_ARROW;
		buttons.get(0).color = musicManager.isRepeat() ? palette.getPrimary() : palette.getOnSurface();
		buttons.get(4).color = musicManager.isShuffle() ? palette.getPrimary() : palette.getOnSurface();

		for (ControlButton b : buttons) {
			b.draw(mouseX, mouseY);
		}
	}

	private void drawSeekBar(float x, float y, float width, float height) {

		Soar instance = Soar.getInstance();
		MusicManager musicManager = instance.getMusicManager();
		ColorPalette palette = instance.getColorManager().getPalette();

		float current = musicManager.getCurrentTime();
		float end = musicManager.getEndTime();

		Skia.drawRoundedRect(x, y, width, height, 3.5F, palette.getSurfaceContainerHigh());
		Skia.drawRoundedRect(x, y, (current / end) * width, height, 3.5F, palette.getPrimary());
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		for (ControlButton b : buttons) {
			b.mousePressed(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for (ControlButton b : buttons) {
			b.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	public boolean isLocked() {
		return locked;
	}

	private class ControlButton extends Component {

		private String icon;
		private Color color;
		private Runnable task;

		public ControlButton(String icon, float x, float y, Runnable task) {
			super(x, y);
			this.icon = icon;
			this.width = 26;
			this.height = 26;
			this.task = task;

			ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

			this.color = palette.getOnSurface();
		}

		@Override
		public void draw(int mouseX, int mouseY) {
			Skia.drawFullCenteredText(icon, ControlButton.this.x, ControlButton.this.y, color, Fonts.getIconFill(28));
		}

		@Override
		public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		}

		@Override
		public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
			if (MouseUtils.isInside(mouseX, mouseY, ControlButton.this.x - 13, ControlButton.this.y - 13, 28, 28)
					&& mouseButton == 0) {
				task.run();
			}
		}

		@Override
		public void keyTyped(char typedChar, int keyCode) {
		}
	}
}
