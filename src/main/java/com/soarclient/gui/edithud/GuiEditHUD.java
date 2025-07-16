package com.soarclient.gui.edithud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.soarclient.Soar;
import com.soarclient.gui.api.SimpleSoarGui;
import com.soarclient.gui.edithud.api.GrabOffset;
import com.soarclient.gui.edithud.api.HUDCore;
import com.soarclient.gui.edithud.api.SnappingLine;
import com.soarclient.management.mod.api.Position;
import com.soarclient.management.mod.api.hud.HUDMod;
import com.soarclient.utils.tuples.Pair;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiEditHUD extends SimpleSoarGui {

	private static final float SCALE_CHANGE_AMOUNT = 0.1F;
	private static final float DEFAULT_LINE_WIDTH = 0.5F;
	private static final int MIDDLE_MOUSE_BUTTON = 2;

	private final GuiScreen prevScreen;
	private final List<HUDMod> mods;
	private final int snappingDistance;

	private Optional<Pair<HUDMod, GrabOffset>> selectedMod;
	private boolean snapping;

	public GuiEditHUD(GuiScreen prevScreen) {
		super(true);
		this.prevScreen = prevScreen;
		this.snappingDistance = 6;
		this.mods = initializeMods();
		this.selectedMod = Optional.empty();
		HUDCore.isEditing = true;
	}

	private List<HUDMod> initializeMods() {
		List<HUDMod> modsList = Soar.getInstance().getModManager().getHUDMods();
		Collections.reverse(modsList);
		return modsList;
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		selectedMod.ifPresent(mod -> updateModPosition(mod, mouseX, mouseY));

		if (selectedMod.isEmpty()) {
			handleMouseWheel(mouseX, mouseY);
		}
	}

	private void updateModPosition(Pair<HUDMod, GrabOffset> mod, int mouseX, int mouseY) {
		setHudPositions(mod, mouseX, mouseY, snapping, DEFAULT_LINE_WIDTH);
	}

	private void handleMouseWheel(int mouseX, int mouseY) {
		int dWheel = Mouse.getDWheel();
		if (dWheel == 0)
			return;

		getHoveredMod(mouseX, mouseY).ifPresent(mod -> {
			Position position = mod.getPosition();
			float newScale = calculateNewScale(position.getScale(), dWheel);
			position.setScale(newScale);
		});
	}

	private float calculateNewScale(float currentScale, int wheelDelta) {
		float change = wheelDelta > 0 ? SCALE_CHANGE_AMOUNT : -SCALE_CHANGE_AMOUNT;
		float newScale = currentScale + change;
		return Math.round(newScale * 10.0F) / 10.0F;
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		if (mouseButton > MIDDLE_MOUSE_BUTTON)
			return;

		getHoveredMod(mouseX, mouseY).ifPresent(mod -> {
			if (mouseButton == MIDDLE_MOUSE_BUTTON) {
				mod.getPosition().setScale(1.0F);
				return;
			}

			GrabOffset offset = new GrabOffset(mouseX - mod.getPosition().getX(), mouseY - mod.getPosition().getY());
			selectedMod = Optional.of(Pair.of(mod, offset));
			snapping = mouseButton == 0;
		});
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		selectedMod = Optional.empty();
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(prevScreen);
		}
	}

	@Override
	public void onClosed() {
		HUDCore.isEditing = false;
	}

	private Optional<HUDMod> getHoveredMod(int mouseX, int mouseY) {
		return mods.stream().filter(mod -> isModInteractable(mod) && isInside(mod, mouseX, mouseY)).findFirst();
	}

	private boolean isModInteractable(HUDMod mod) {
		return mod.isEnabled() && !mod.isHidden() && mod.isMovable();
	}

	private boolean isInside(HUDMod mod, int mouseX, int mouseY) {
		Position pos = mod.getPosition();
		return mouseX >= pos.getX() && mouseX <= pos.getRightX() && mouseY >= pos.getY() && mouseY <= pos.getBottomY();
	}

	private void setHudPositions(Pair<HUDMod, GrabOffset> modPair, int mouseX, int mouseY, boolean snap,
			float lineWidth) {
		GrabOffset offset = modPair.getSecond();
		Position position = modPair.getFirst().getPosition();

		float x = mouseX - offset.getX();
		float y = mouseY - offset.getY();

		if (snap) {
			x = getXSnapping(lineWidth, x, position.getWidth(), true);
			y = getYSnapping(lineWidth, y, position.getHeight(), true);
		}

		position.setPosition(x, y);
	}

	private float getXSnapping(float lineWidth, float x, float width, boolean multipleSides) {
		return getSnappingPosition(getXSnappingLines(), x, width, multipleSides, lineWidth, true);
	}

	private float getYSnapping(float lineWidth, float y, float height, boolean multipleSides) {
		return getSnappingPosition(getYSnappingLines(), y, height, multipleSides, lineWidth, false);
	}

	private float getSnappingPosition(FloatArrayList lines, float position, float size, boolean multipleSides,
			float lineWidth, boolean isHorizontal) {
		List<SnappingLine> snappingLines = findClosestSnappingLines(lines, position, size, multipleSides);

		if (snappingLines.isEmpty()) {
			return position;
		}

		snappingLines.forEach(line -> line.drawLine(lineWidth, isHorizontal));
		return snappingLines.get(0).getPosition();
	}

	private List<SnappingLine> findClosestSnappingLines(FloatArrayList lines, float position, float size,
			boolean multipleSides) {
		List<SnappingLine> snappingLines = new ArrayList<>();
		float closest = snappingDistance;

		for (Float line : lines) {
			SnappingLine snappingLine = new SnappingLine(line, position, size, multipleSides);
			float distance = snappingLine.getDistance();

			if (Math.round(distance) == Math.round(closest)) {
				snappingLines.add(snappingLine);
			} else if (distance < closest) {
				closest = distance;
				snappingLines.clear();
				snappingLines.add(snappingLine);
			}
		}

		return snappingLines;
	}

	private FloatArrayList getXSnappingLines() {
		return getSnappingLines(true);
	}

	private FloatArrayList getYSnappingLines() {
		return getSnappingLines(false);
	}

	private FloatArrayList getSnappingLines(boolean isHorizontal) {

		FloatArrayList lines = new FloatArrayList();
		ScaledResolution sr = ScaledResolution.get(mc);

		lines.add(isHorizontal ? sr.getScaledWidth() / 2F : sr.getScaledHeight() / 2F);

		mods.stream().filter(
				mod -> isModInteractable(mod) && !selectedMod.map(pair -> pair.getFirst().equals(mod)).orElse(false))
				.forEach(mod -> {
					Position p = mod.getPosition();
					if (isHorizontal) {
						lines.add(p.getX());
						lines.add(p.getCenterX());
						lines.add(p.getRightX());
					} else {
						lines.add(p.getY());
						lines.add(p.getCenterY());
						lines.add(p.getBottomY());
					}
				});

		return lines;
	}
}