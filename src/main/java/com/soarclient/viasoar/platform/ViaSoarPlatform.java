package com.soarclient.viasoar.platform;

import com.soarclient.viasoar.api.VSPlatform;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public class ViaSoarPlatform implements VSPlatform {

	public static final ViaSoarPlatform INSTANCE = new ViaSoarPlatform();

	@Override
	public int getGameVersion() {
		return ProtocolVersion.v1_8.getVersion();
	}

}
