package com.soarclient.libraries.soarium.gui.utils;

import java.util.List;

import net.minecraft.client.gui.GuiButton;

public class SoariumGui {

	public static List<GuiButton> sortButtons(List<GuiButton> buttons, int startX, int startY, int index) {

		int horizontalSpacing = 160;
		int verticalSpacing = 21;

		for (int i = 0; i < buttons.size(); i++) {

			GuiButton button = buttons.get(i);

			int x = startX + ((index + i) % 2) * horizontalSpacing;
			int y = startY + (i / 2) * verticalSpacing;

			button.xPosition = x;
			button.yPosition = y;
			button.width = 150;
			button.height = 20;
		}

		return buttons;
	}

	public static List<GuiButton> sortButtons(List<GuiButton> buttons, int startX, int startY) {
		return sortButtons(buttons, startX, startY, 0);
	}
}
