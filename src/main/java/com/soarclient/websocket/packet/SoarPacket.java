package com.soarclient.websocket.packet;

import com.google.gson.JsonObject;

public abstract class SoarPacket {

	private final String type;

	protected SoarPacket(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public abstract JsonObject toJson();
}
