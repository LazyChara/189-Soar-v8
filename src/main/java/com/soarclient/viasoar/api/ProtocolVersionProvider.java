package com.soarclient.viasoar.api;

import java.util.Objects;

import com.soarclient.viasoar.ViaSoar;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.version.BaseVersionProvider;

import net.minecraft.client.Minecraft;

public class ProtocolVersionProvider extends BaseVersionProvider {

	@Override
	public ProtocolVersion getClosestServerProtocol(UserConnection connection) throws Exception {
		if (connection.isClientSide() && !Minecraft.getMinecraft().isIntegratedServerRunning()) {
			return Objects.requireNonNull(connection.getChannel()).attr(ViaSoar.VS_NETWORK_MANAGER).get()
					.getTrackedVersion();
		}
		return super.getClosestServerProtocol(connection);
	}
}