package com.soarclient.management.mod.impl.render;

import java.util.Arrays;

import com.soarclient.event.EventBus;
import com.soarclient.event.impl.ShaderEventListener;
import com.soarclient.management.mod.Mod;
import com.soarclient.management.mod.ModCategory;
import com.soarclient.management.mod.settings.impl.ComboSetting;
import com.soarclient.management.mod.settings.impl.NumberSetting;
import com.soarclient.skia.font.Icon;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

public class MotionBlurMod extends Mod implements ShaderEventListener {

	private static MotionBlurMod instance;

	private ResourceLocation motion_blur = new ResourceLocation("minecraft:shaders/post/motion_blur.json");
	private ShaderGroup group;
	private float groupBlur;
	private boolean loaded;
	private int prevWidth, prevHeight;

	private NumberSetting amountSetting = new NumberSetting("setting.amount", "setting.amount.description",
			Icon.FILTER_5, this, 0.5F, 0.1F, 1.0F, 0.01F);
	private ComboSetting modeSetting = new ComboSetting("setting.mode", "setting.mode.description",
			Icon.FORMAT_LIST_BULLETED, this, Arrays.asList("setting.phosphor", "setting.monkey"), "setting.monkey");

	public MotionBlurMod() {
		super("mod.motionblur.name", "mod.motionblur.description", Icon.MOTION_BLUR, ModCategory.RENDER);

		instance = this;
	}

	@Override
	public void onShader(ShaderEvent event) {

		ScaledResolution sr = ScaledResolution.get(mc);

		if (modeSetting.getOption().equals("setting.phosphor")) {

			if (group == null || prevWidth != sr.getScaledWidth() || prevHeight != sr.getScaledHeight()) {

				prevWidth = sr.getScaledWidth();
				prevHeight = sr.getScaledHeight();

				groupBlur = amountSetting.getValue();

				try {
					group = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(),
							motion_blur);
					group.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (groupBlur != amountSetting.getValue() || !loaded) {
				loaded = true;
				System.out.println("a");
				group.getListShaders().forEach((shader) -> {
					ShaderUniform factor = shader.getShaderManager().getShaderUniform("BlurFactor");
					if (factor != null) {
						factor.set(amountSetting.getValue() - 0.01F);
					}
				});
				groupBlur = amountSetting.getValue();
			}

			event.getGroups().add(group);
		}
	}

	@Override
	public void onEnable() {
		group = null;
		EventBus.getInstance().register(this, ShaderEvent.ID);
	}

	@Override
	public void onDisable() {
		EventBus.getInstance().unregister(this, ShaderEvent.ID);
	}

	public static MotionBlurMod getInstance() {
		return instance;
	}

	public boolean isMonkey() {
		return modeSetting.getOption().equals("setting.monkey");
	}

	public NumberSetting getAmountSetting() {
		return amountSetting;
	}
}
