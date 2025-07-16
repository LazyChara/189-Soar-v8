package com.soarclient.utils.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;

public class ServerUtils {

	private static Minecraft mc = Minecraft.getMinecraft();

	public static boolean isInTablist(Entity entity) {
		if (isJoinServer()) {
			for (NetworkPlayerInfo item : mc.getNetHandler().getPlayerInfoMap()) {
				NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) item;

				if (playerInfo != null && playerInfo.getGameProfile() != null
						&& playerInfo.getGameProfile().getName().contains(entity.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isJoinServer() {
		return !mc.isSingleplayer() && mc.getCurrentServerData() != null;
	}

	public static boolean isHypixel() {
		return getServerIP().toLowerCase().contains("hypixel");
	}

	public static int getPing() {
		if (!isJoinServer()) {
			return 0;
		} else {
			return (int) mc.getCurrentServerData().pingToServer;
		}
	}

	public static String getServerIP() {

		String ip;

		if (isJoinServer()) {
			ip = mc.getCurrentServerData().serverIP;
		} else {
			ip = "Singleplayer";
		}

		return ip;
	}
}