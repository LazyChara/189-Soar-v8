package com.soarclient.viasoar.fixes;

import com.soarclient.management.mod.impl.misc.ViaVersionMod;
import com.soarclient.viasoar.ViaSoar;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import net.minecraft.client.Minecraft;

public class ViaHelper {

	public static boolean newerThanOrEqualsTo(ProtocolVersion version) {
		return ViaVersionMod.getInstance().isEnabled() && getVersion() != null
				&& getVersion().newerThanOrEqualTo(version) && !Minecraft.getMinecraft().isIntegratedServerRunning();
	}

	public static boolean olderThanOrEqualsTo(ProtocolVersion version) {
		return ViaVersionMod.getInstance().isEnabled() && getVersion() != null
				&& getVersion().olderThanOrEqualTo(version) && !Minecraft.getMinecraft().isIntegratedServerRunning();
	}

	public static boolean newerThan(ProtocolVersion version) {
		return ViaVersionMod.getInstance().isEnabled() && getVersion() != null && getVersion().newerThan(version)
				&& !Minecraft.getMinecraft().isIntegratedServerRunning();
	}

	public static boolean olderThan(ProtocolVersion version) {
		return ViaVersionMod.getInstance().isEnabled() && getVersion() != null && getVersion().olderThan(version)
				&& !Minecraft.getMinecraft().isIntegratedServerRunning();
	}

	private static ProtocolVersion getVersion() {

		if (ViaSoar.getManager() == null) {
			return null;
		}

		return ViaSoar.getManager().getTargetVersion();
	}
}
