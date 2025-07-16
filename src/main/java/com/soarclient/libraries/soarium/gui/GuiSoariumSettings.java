package com.soarclient.libraries.soarium.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.soarclient.libraries.soarium.Soarium;
import com.soarclient.libraries.soarium.config.SoariumConfig;
import com.soarclient.libraries.soarium.gui.locale.SoariumI18n;
import com.soarclient.libraries.soarium.gui.utils.SoariumGui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;

public class GuiSoariumSettings extends GuiScreen {

	private static GameSettings.Options[] OPTIONS = new GameSettings.Options[] { GameSettings.Options.GRAPHICS,
			GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION,
			GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE,
			GameSettings.Options.GAMMA, GameSettings.Options.BLOCK_ALTERNATIVES };
	private GuiScreen prevScreen;

	public GuiSoariumSettings(GuiScreen prevScreen) {
		this.prevScreen = prevScreen;
	}

	@Override
	public void initGui() {

		this.buttonList.clear();

		for (int i = 0; i < OPTIONS.length; ++i) {
			GameSettings.Options gamesettings$options = OPTIONS[i];

			if (gamesettings$options != null) {
				int j = this.width / 2 - 155 + i % 2 * 160;
				int k = this.height / 6 + 21 * (i / 2) - 12;

				if (gamesettings$options.getEnumFloat()) {
					this.buttonList.add(
							new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
				} else {
					this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), j, k,
							gamesettings$options, mc.gameSettings.getKeyBinding(gamesettings$options)));
				}
			}
		}

		int x = this.width / 2 - 155 + 0;
        int y = this.height / 6 + 21 * (OPTIONS.length / 2) - 12;
        
        List<GuiButton> options = new ArrayList<>();
        
        options.add(new GuiButton(201, 0, 0, 150, 20, SoariumI18n.get("soarium.qualities")));
        options.add(new GuiButton(202, 0, 0, 150, 20, SoariumI18n.get("soarium.animations")));
        options.add(new GuiButton(203, 0, 0, 150, 20, SoariumI18n.get("soarium.particles")));
        options.add(new GuiButton(204, 0, 0, 150, 20, SoariumI18n.get("soarium.details")));
        options.add(new GuiButton(205, 0, 0, 150, 20, SoariumI18n.get("soarium.performances")));
        options.add(new GuiButton(206, 0, 0, 150, 20, SoariumI18n.get("soarium.cullings")));
        options.add(new GuiButton(207, 0, 0, 150, 20, SoariumI18n.get("soarium.renders")));
        options.add(new GuiButton(208, 0, 0, 150, 20, SoariumI18n.get("soarium.advances")));

        this.buttonList.addAll(SoariumGui.sortButtons(options, x, y, OPTIONS.length));
        
		this.buttonList
				.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, SoariumI18n.get("gui.done")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, SoariumI18n.get("options.videoTitle"), this.width / 2, 15,
				16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void actionPerformed(GuiButton button) throws IOException {

		int i = mc.gameSettings.guiScale;
		
		if (button.id < 200 && button instanceof GuiOptionButton) {
			mc.gameSettings.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
			button.displayString = mc.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
		}

		if (mc.gameSettings.guiScale != i) {
			ScaledResolution scaledresolution = ScaledResolution.create(mc);
			int j = scaledresolution.getScaledWidth();
			int k = scaledresolution.getScaledHeight();
			this.setWorldAndResolution(this.mc, j, k);
		}

		if (button.id == 200) {
			this.mc.gameSettings.saveOptions();
			SoariumConfig.save(Soarium.getConfig());
			this.mc.displayGuiScreen(this.prevScreen);
		}
	}
}
