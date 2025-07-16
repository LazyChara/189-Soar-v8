package com.soarclient.management.account.impl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.soarclient.management.account.Account;
import com.soarclient.management.account.AccountType;

public class OfflineAccount extends Account {

	private final String name;
	private final UUID uuid;

	public OfflineAccount(JsonObject jsonObject) {
		super(AccountType.OFFLINE);
		this.name = jsonObject.get("name").getAsString();
		this.uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
	}

	public OfflineAccount(final String name) {
		super(AccountType.OFFLINE);
		this.name = name;
		this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public JsonObject toJson() {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", this.name);
		jsonObject.addProperty("uuid", this.uuid.toString());
		return jsonObject;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	@Override
	public String getDisplayString() {
		return this.name + " (Offline)";
	}

	@Override
	public boolean refresh() {
		return false;
	}
}