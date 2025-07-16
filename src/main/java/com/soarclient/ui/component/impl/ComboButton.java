package com.soarclient.ui.component.impl;

import java.util.List;

import com.soarclient.Soar;
import com.soarclient.animation.Animation;
import com.soarclient.animation.Duration;
import com.soarclient.animation.cubicbezier.impl.EaseStandard;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.api.PressAnimation;
import com.soarclient.ui.component.handler.impl.ComboButtonHandler;
import com.soarclient.utils.ColorUtils;
import com.soarclient.utils.language.I18n;
import com.soarclient.utils.mouse.MouseUtils;

public class ComboButton extends Component {

	private PressAnimation pressAnimation = new PressAnimation();
	private int[] pressedPos;

	private Animation animation;
	private List<String> options;
	private String option;

	public ComboButton(float x, float y, List<String> options, String option) {
		super(x, y);
		this.options = options;
		this.option = option;
		this.animation = new DummyAnimation(1);
		width = 126;
		height = 32;
		pressedPos = new int[] { 0, 0 };
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		Skia.drawRoundedRect(x, y, width, height, 12, palette.getPrimary());
		Skia.save();
		Skia.clip(x, y, width, height, 12);
		pressAnimation.draw(x + pressedPos[0], y + pressedPos[1], width, height, palette.getPrimaryContainer(), 0.12F);
		Skia.restore();
		Skia.drawHeightCenteredText("<", x + 6, y + (height / 2), palette.getSurface(), Fonts.getMedium(14));
		Skia.drawHeightCenteredText(">", x + width - 16, y + (height / 2), palette.getSurface(), Fonts.getMedium(14));
		Skia.drawFullCenteredText(I18n.get(option),
				x + (width / 2) + ((animation.getEnd() - animation.getValue()) * 22), y + (height / 2),
				ColorUtils.applyAlpha(palette.getSurface(), Math.abs(animation.getValue())), Fonts.getMedium(14));
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		if (MouseUtils.isInside(mouseX, mouseY, x, y, width, height) && mouseButton == 0) {
			pressedPos = new int[] { mouseX - (int) x, mouseY - (int) y };
			pressAnimation.mousePressed();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		int max = options.size();
		int index = options.indexOf(option);

		if (mouseButton == 0) {

			if (MouseUtils.isInside(mouseX, mouseY, x, y, 32, 32)) {

				animation = new EaseStandard(Duration.MEDIUM_3, 0, 1);

				if (index > 0) {
					index--;
				} else {
					index = max - 1;
				}

				setOption(options.get(index));
			}

			if (MouseUtils.isInside(mouseX, mouseY, x + width - 32, y, 32, 32)) {

				animation = new EaseStandard(Duration.MEDIUM_3, 0, -1);

				if (index < max - 1) {
					index++;
				} else {
					index = 0;
				}

				setOption(options.get(index));
			}
		}

		pressAnimation.mouseReleased();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {

		this.option = option;

		if (handler instanceof ComboButtonHandler) {
			((ComboButtonHandler) handler).onChanged(option);
		}
	}
}
