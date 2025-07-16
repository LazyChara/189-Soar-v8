package com.soarclient.viasoar.api;

import com.viaversion.vialoader.impl.viaversion.VLLoader;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;

public class ProtocolVLLoader extends VLLoader {

	@Override
	public void load() {
		super.load();

		final ViaProviders providers = Via.getManager().getProviders();

		providers.use(VersionProvider.class, new ProtocolVersionProvider());
	}
}