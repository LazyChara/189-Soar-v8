package com.soarclient.gui.mainmenu.gui.account.pages;

import com.soarclient.Soar;
import com.soarclient.gui.api.page.Page;
import com.soarclient.gui.api.page.PageGui;
import com.soarclient.gui.api.page.impl.RightTransition;
import com.soarclient.management.account.Account;
import com.soarclient.management.account.AccountAuth;
import com.soarclient.management.account.impl.MicrosoftAccount;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.ui.component.impl.Button;
import com.soarclient.utils.IOUtils;
import com.soarclient.utils.language.I18n;

import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.step.msa.StepMsaDeviceCode;

public class WaitAuthPage extends Page {

	private Thread thread;
	private String url;
	private boolean authed, cancelled;

	private Button cancelButton, copyButton;

	public WaitAuthPage(PageGui parent) {
		super(parent, "", "", new RightTransition(true));
		authed = false;
		cancelled = false;
		url = "";
	}

	@Override
	public void init() {
		authed = false;
		cancelButton = new Button("text.cancel", x, y, Button.Style.FILLED);
		cancelButton.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				cancelled = true;
			}
		});

		copyButton = new Button("text.copylink", x, y, Button.Style.TONAL);
		copyButton.setHandler(new ButtonHandler() {

			@Override
			public void onAction() {
				IOUtils.copyStringToClipboard(url);
			}
		});

		float offsetY = (height / 2) + 33;

		cancelButton.setX(x + (width / 2) - (cancelButton.getWidth() / 2) + (cancelButton.getWidth() / 2) + 6);
		cancelButton.setY(y + offsetY);
		copyButton.setX(x + (width / 2) - (copyButton.getWidth() / 2) - (copyButton.getWidth() / 2) - 6);
		copyButton.setY(y + offsetY);
	}

	@Override
	public void draw(int mouseX, int mouseY) {

		ColorPalette palette = Soar.getInstance().getColorManager().getPalette();

		float offsetY = (height / 2) - 73;

		String text = I18n.get("text.login");
		String message1 = I18n.get("text.login.message1");
		String message2 = I18n.get("text.login.message2");

		Skia.drawCenteredText(text, x + (width / 2), y + offsetY, palette.getOnSurfaceVariant(), Fonts.getMedium(34));
		Skia.drawCenteredText(message1, x + (width / 2), y + offsetY + 40, palette.getOnSurfaceVariant(),
				Fonts.getRegular(20));
		Skia.drawCenteredText(message2, x + (width / 2), y + offsetY + 62, palette.getOnSurfaceVariant(),
				Fonts.getRegular(20));

		cancelButton.draw(mouseX, mouseY);
		copyButton.draw(mouseX, mouseY);

		updateThread();
	}

	private void updateThread() {

		if (thread == null && parent.getCurrentPage().getClass().equals(WaitAuthPage.class)) {
			thread = new Thread(() -> {
				try {
					Account acc = new MicrosoftAccount(
							MicrosoftAccount.DEVICE_CODE_LOGIN.getFromInput(MinecraftAuth.createHttpClient(),
									new StepMsaDeviceCode.MsaDeviceCodeCallback(msaDeviceCode -> {
										url = msaDeviceCode.getDirectVerificationUri();
										AccountAuth.openUrlWithDesktop(msaDeviceCode.getDirectVerificationUri());
									})));
					AccountAuth.login(acc);
				} catch (Exception e) {
				}
				authed = true;
			});
			thread.start();
		}

		if (authed) {
			closeThread();
		}

		if (thread != null && cancelled) {
			closeThread();
		}
	}

	private void closeThread() {

		authed = false;
		cancelled = false;

		if (thread != null) {
			thread.interrupt();
			thread = null;
		}

		if (!parent.getCurrentPage().getClass().equals(AccountListPage.class)) {
			parent.setCurrentPage(AccountListPage.class);
		}
	}

	@Override
	public void mousePressed(int mouseX, int mouseY, int mouseButton) {
		cancelButton.mousePressed(mouseX, mouseY, mouseButton);
		copyButton.mousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		cancelButton.mouseReleased(mouseX, mouseY, mouseButton);
		copyButton.mouseReleased(mouseX, mouseY, mouseButton);
	}
}
