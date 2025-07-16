package com.soarclient.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public class IOUtils {

	public static void copyStringToClipboard(String s) {
		StringSelection stringSelection = new StringSelection(s);
		getToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public static String getStringFromClipboard() {
		try {
			return getToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor)
					.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private static Toolkit getToolkit() {
		return Toolkit.getDefaultToolkit();
	}
}
