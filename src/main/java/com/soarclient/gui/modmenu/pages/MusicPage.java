package com.soarclient.gui.modmenu.pages;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.RightLeftTransition;
import com.soarclient.gui.modmenu.component.MusicControlBar;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.music.Music;
import com.soarclient.management.music.MusicManager;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.impl.text.SearchBar;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.SearchUtils;
import com.soarclient.utils.mouse.MouseUtils;
import com.soarclient.utils.mouse.ScrollHelper;

import io.github.humbleui.skija.ClipMode;
import io.github.humbleui.skija.FilterTileMode;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageFilter;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Path;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;

public class MusicPage extends Page {

	private SimpleAnimation controlBarAnimation = new SimpleAnimation();
	private MusicControlBar controlBar;
	private List<Item> items = new ArrayList<>();
	private ScrollHelper scrollHelper = new ScrollHelper();

	private SearchBar searchBar;

	public MusicPage(PageGui parent) {
		super(parent, "text.music", Icon.MUSIC_NOTE, new RightLeftTransition(true));

		for (Music m : Soar.getInstance().getMusicManager().getMusics()) {
			items.add(new Item(m));
		}
	}

	@Override
	public void init() {

		controlBar = new MusicControlBar(x + 22, y + height - 60 - 18, width - 44);

		String text = "";

		if (searchBar != null) {
			text = searchBar.getText();
		}

		searchBar = new SearchBar(x + width - 260 - 32, y + 32, 260, text, () -> {
			scrollHelper.reset();
		});
		for (Item i : items) {
			i.xAnimation.setFirstTick(true);
			i.yAnimation.setFirstTick(true);
		}
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		MusicManager musicManager = Soar.getInstance().getMusicManager();
		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		int index = 0;
		float offsetX = 28;
		float offsetY = 96;

		controlBarAnimation.onTick(MouseUtils.isInside(mouseX, mouseY, controlBar.getX(), controlBar.getY(),
				controlBar.getWidth(), controlBar.getHeight()) ? 1 : 0, 12);

		scrollHelper.onScroll();
		mouseY = (int) (mouseY - scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, scrollHelper.getValue());

		searchBar.draw(mouseX, mouseY);

		for (Item i : items) {

			Music m = i.music;
			SimpleAnimation xAnimation = i.xAnimation;
			SimpleAnimation yAnimation = i.yAnimation;
			SimpleAnimation focusAnimation = i.focusAnimation;

			if (!searchBar.getText().isEmpty()
					&& !SearchUtils.isSimilar(m.getTitle() + " " + m.getArtist(), searchBar.getText())) {
				continue;
			}

			float itemX = x + offsetX;
			float itemY = y + offsetY;

			xAnimation.onTick(itemX, 14);
			yAnimation.onTick(itemY, 14);
			focusAnimation.onTick(MouseUtils.isInside(mouseX, mouseY, itemX, itemY, 174, 174) ? 1 : 0, 10);

			itemX = xAnimation.getValue();
			itemY = yAnimation.getValue();

			if (m.getAlbum() != null) {
				drawRoundedImage(m.getAlbum(), itemX, itemY, 174, 174, 26,
						(Math.abs(focusAnimation.getValue()) + 0.001F) * 6);
			} else {
				Skia.drawRoundedRect(itemX, itemY, 174, 174, 26, palette.getSurfaceContainerHigh());
			}

			String limitedTitle = Skia.getLimitText(m.getTitle(), Fonts.getRegular(15), 174);
			String limitedArtist = Skia.getLimitText(m.getArtist(), Fonts.getRegular(12), 174);

			Skia.drawText(limitedTitle, itemX, itemY + 174 + 6, palette.getOnSurface(), Fonts.getRegular(15));
			Skia.drawText(limitedArtist, itemX, itemY + 174 + 6 + 15, palette.getOnSurfaceVariant(),
					Fonts.getRegular(12));

			String icon = musicManager.getCurrentMusic() != null && musicManager.getCurrentMusic().equals(m)
					&& musicManager.isPlaying() ? Icon.PAUSE : Icon.PLAY_ARROW;

			Skia.save();
			Skia.translate(0, 15 - (focusAnimation.getValue() * 15));
			Skia.drawFullCenteredText(icon, itemX + (174 / 2), itemY + (174 / 2),
					ColorUtils.applyAlpha(Color.WHITE, focusAnimation.getValue()), Fonts.getIconFill(64));
			Skia.restore();

			offsetX += 174 + 32;
			index++;

			if (index % 4 == 0) {
				offsetX = 28;
				offsetY += 206 + 23;
			}
		}

		Skia.restore();

		mouseY = (int) (mouseY + scrollHelper.getValue());

		Skia.save();
		Skia.translate(0, 100 - (controlBarAnimation.getValue() * 100));
		controlBar.draw(mouseX, mouseY);
		Skia.restore();
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		controlBar.mousePressed(mouseX, mouseY, mouseButton);

		if (MouseUtils.isInside(mouseX, mouseY, controlBar.getX(), controlBar.getY(), controlBar.getWidth(),
				controlBar.getHeight())) {
			return;
		}

		mouseY = (int) (mouseY - scrollHelper.getValue());
		searchBar.mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		MusicManager musicManager = Soar.getInstance().getMusicManager();

		controlBar.mouseReleased(mouseX, mouseY, mouseButton);

		if (MouseUtils.isInside(mouseX, mouseY, controlBar.getX(), controlBar.getY(), controlBar.getWidth(),
				controlBar.getHeight())) {
			return;
		}

		mouseY = (int) (mouseY - scrollHelper.getValue());
		searchBar.mouseReleased(mouseX, mouseY, mouseButton);

		for (Item i : items) {

			Music m = i.music;
			float itemX = i.xAnimation.getValue();
			float itemY = i.yAnimation.getValue();

			if (!searchBar.getText().isEmpty()
					&& !SearchUtils.isSimilar(m.getTitle() + " " + m.getArtist(), searchBar.getText())) {
				continue;
			}

			if (MouseUtils.isInside(mouseX, mouseY, itemX, itemY, 174, 174) && mouseButton == 0) {

				if (musicManager.getCurrentMusic() != m) {
					musicManager.stop();
					musicManager.setCurrentMusic(m);
					musicManager.play();
				} else {
					musicManager.switchPlayBack();
				}
			}
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		searchBar.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onClosed() {
	}

	private void drawRoundedImage(File file, float x, float y, float width, float height, float cornerRadius,
			float blurRadius) {

		Path path = new Path();
		path.addRRect(RRect.makeXYWH(x, y, width, height, cornerRadius));

		Paint blurPaint = new Paint();
		blurPaint.setImageFilter(ImageFilter.makeBlur(blurRadius, blurRadius, FilterTileMode.CLAMP));

		Skia.save();

		Skia.getCanvas().clipPath(path, ClipMode.INTERSECT, true);

		Skia.drawImage(file, x, y, width, height);

		if (Skia.getImageHelper().load(file)) {
			Image image = Skia.getImageHelper().get(file.getName());
			if (image != null) {
				Skia.getCanvas().drawImageRect(image, Rect.makeWH(image.getWidth(), image.getHeight()),
						Rect.makeXYWH(x, y, width, height), blurPaint, true);
			}
		}

		Skia.restore();
	}

	private class Item {

		private Music music;
		private SimpleAnimation xAnimation = new SimpleAnimation();
		private SimpleAnimation yAnimation = new SimpleAnimation();
		private SimpleAnimation focusAnimation = new SimpleAnimation();

		private Item(Music music) {
			this.music = music;
		}
	}
}
