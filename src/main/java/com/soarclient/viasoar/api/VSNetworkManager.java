package com.soarclient.viasoar.api;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

public interface VSNetworkManager {

	ProtocolVersion getTrackedVersion();

	void setTrackedVersion(final ProtocolVersion version);
}