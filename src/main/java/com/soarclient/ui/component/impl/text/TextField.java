package com.soarclient.ui.component.impl.text;

import org.lwjgl.input.Keyboard;

import com.soarclient.Soar;
import com.soarclient.animation.Animation;
import com.soarclient.animation.Duration;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.animation.cubicbezier.impl.EaseStandard;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.TextHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.mouse.MouseUtils;

public class TextField extends Component {

	private SimpleAnimation cursorAnimation = new SimpleAnimation();
	private Animation cursorFlashAnimation;

	private TextInputHelper input = new TextInputHelper();

	public TextField(float x, float y, float width, String defaultValue) {
		super(x, y);
		this.width = width;
		this.height = 40;
		this.cursorFlashAnimation = new DummyAnimation(0, 0);
		input.setText(defaultValue);
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		Skia.drawRoundedRect(x, y, width, height, 12, palette.getSurfaceContainer());

		Skia.save();
		Skia.clip(x, y, width, height, 20);

		drawCursor();

		String text = getText();

		if (!text.isEmpty() || isFocused()) {
			float availableWidth = width - 20;
			float textWidth = Skia.getTextBounds(text, Fonts.getRegular(16)).getWidth();

			float xOffset = 0;
			if (textWidth > availableWidth) {
				float overflow = textWidth - availableWidth;
				xOffset = -overflow;
			}

			Skia.drawHeightCenteredText(text, x + 12 + xOffset, y + (height / 2), palette.getOnSurfaceVariant(),
					Fonts.getRegular(16));
		}

		Skia.restore();

		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_F) && !isFocused()) {
			input.setFocused(true);
		}
	}

	private void drawCursor() {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		int selectionEnd = input.getSelectionEnd();
		int cursorPosition = input.getCursorPosition();
		String text = getText();
		boolean focused = isFocused();

		if (focused && cursorPosition == selectionEnd) {
			if (cursorFlashAnimation.getEnd() == 0 && cursorFlashAnimation.isFinished()) {
				cursorFlashAnimation = new EaseStandard(Duration.SHORT_4, 0, 1);
			} else if (cursorFlashAnimation.getEnd() == 1 && cursorFlashAnimation.isFinished()) {
				cursorFlashAnimation = new EaseStandard(Duration.SHORT_4, 1, 0);
			}
		} else {
			if (cursorFlashAnimation.getEnd() != 0) {
				cursorFlashAnimation = new EaseStandard(Duration.SHORT_4, 1, 0);
			}
		}

		float textWidth = Skia.getTextBounds(text, Fonts.getRegular(16)).getWidth();
		float availableWidth = width - 20;
		float xOffset = 0;

		if (textWidth > availableWidth) {
			float overflow = textWidth - availableWidth;
			xOffset = -overflow;
		}

		float cursorOffset = Skia.getTextBounds(text.substring(0, cursorPosition), Fonts.getRegular(16)).getWidth();
		cursorAnimation.onTick(cursorOffset, 16);
		float cursorX = x + 12 + cursorAnimation.getValue() + xOffset;

		Skia.drawRect(cursorX, y + 9, 1, 24,
				ColorUtils.applyAlpha(palette.getSurfaceTint(), (int) (cursorFlashAnimation.getValue() * 255)));

		if (cursorPosition != selectionEnd) {
			int start = Math.min(cursorPosition, selectionEnd);
			int end = Math.max(cursorPosition, selectionEnd);

			float selectionWidth = Skia.getTextBounds(text.substring(start, end), Fonts.getRegular(16)).getWidth();
			float selectionOffset = Skia.getTextBounds(text.substring(0, start), Fonts.getRegular(16)).getWidth();

			Skia.drawRect(x + 12 + selectionOffset + xOffset, y + 9, selectionWidth, 24, palette.getSurfaceTint());
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		boolean isInside = MouseUtils.isInside(mouseX, mouseY, x, y, width, height);

		if (mouseButton == 0) {

			if (isInside && !isFocused()) {
				input.setFocused(true);
			} else if (!isInside && isFocused()) {
				input.setFocused(false);
			}
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		input.keyTyped(typedChar, keyCode);

		if (handler instanceof TextHandler) {
			((TextHandler) handler).onTyped(getText());
		}
	}

	public String getText() {
		return input.getText();
	}

	public void setText(String text) {
		input.setText(text);
	}

	public boolean isFocused() {
		return input.isFocused();
	}
}