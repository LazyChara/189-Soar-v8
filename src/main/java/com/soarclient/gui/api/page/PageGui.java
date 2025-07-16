package com.soarclient.gui.api.page;

import java.util.List;

import com.soarclient.animation.Duration;
import com.soarclient.animation.cubicbezier.impl.EaseEmphasizedDecelerate;
import com.soarclient.animation.other.DummyAnimation;
import com.soarclient.gui.api.GuiTransition;
import com.soarclient.gui.api.SoarGui;
import com.soarclient.skia.Skia;

import net.minecraft.client.gui.GuiScreen;

public abstract class PageGui extends SoarGui {

	protected List<Page> pages;

	protected Page currentPage;
	protected Page lastPage;

	public PageGui(GuiTransition transition, boolean background, boolean blur) {
		super(transition, background, blur);
		this.pages = createPages();

		if (!pages.isEmpty()) {
			this.currentPage = pages.getFirst();
		}
	}

	@Override
	public void init() {
		setPageSize(currentPage);
		super.init();
		currentPage.init();
		currentPage.setAnimation(new DummyAnimation(1));
	}

	public void setPageSize(Page p) {
		p.setX(getX());
		p.setY(getY());
		p.setWidth(getWidth());
		p.setHeight(getHeight());
	}

	@Override
	public void drawSkia(int mouseX, int mouseY) {

		if (currentPage != null && lastPage == null) {
			currentPage.draw(mouseX, mouseY);
		}

		if (lastPage != null) {

			GuiTransition transition = lastPage.getTransition();

			if (currentPage.getTransition().isConsecutive()) {
				Skia.save();

				if (transition != null) {
					float[] result = transition.onTransition(lastPage.getAnimation());
					Skia.translate(result[0] * getWidth(), result[1] * getHeight());
					Skia.scale(getX(), getY(), getWidth(), getHeight(), result[2]);
				}

				lastPage.draw(mouseX, mouseY);
				Skia.restore();
			}

			Skia.save();

			transition = currentPage.getTransition();

			if (transition != null) {
				float[] result = transition.onTransition(currentPage.getAnimation());
				Skia.translate(result[0] * getWidth(), result[1] * getHeight());
				Skia.scale(getX(), getY(), getWidth(), getHeight(), result[2]);
			}

			currentPage.draw(mouseX, mouseY);
			Skia.restore();

			if (lastPage.getAnimation().isFinished()) {
				lastPage = null;
			}
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {

		if (currentPage != null) {
			currentPage.mousePressed(mouseX, mouseY, mouseButton);
		}

		super.mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

		if (currentPage != null) {
			currentPage.mouseReleased(mouseX, mouseY, mouseButton);
		}

		super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) {

		super.keyTyped(typedChar, keyCode);

		if (currentPage != null) {
			currentPage.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	public void close(GuiScreen nextScreen) {
		super.close(nextScreen);
	}

	@Override
	public void onClosed() {
		if (currentPage != null) {
			currentPage.onClosed();
		}
	}

	public Page getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Page page) {

		if (currentPage != null) {
			lastPage = currentPage;
			currentPage.onClosed();
		}

		this.currentPage = page;
		currentPage.setAnimation(new EaseEmphasizedDecelerate(Duration.MEDIUM_1, 0, 1));
		lastPage.setAnimation(new EaseEmphasizedDecelerate(Duration.MEDIUM_1, 1, 0));

		if (currentPage != null) {
			setPageSize(currentPage);
			currentPage.init();
		}
	}

	public void setCurrentPage(Class<? extends Page> clazz) {

		Page page = getPage(clazz);

		if (page != null) {
			setCurrentPage(page);
		}
	}

	public Page getPage(Class<? extends Page> clazz) {

		Page page = null;

		for (Page p : pages) {
			if (p.getClass().equals(clazz)) {
				page = p;
				break;
			}
		}

		return page;
	}

	public abstract List<Page> createPages();

	public List<Page> getPages() {
		return pages;
	}
}
