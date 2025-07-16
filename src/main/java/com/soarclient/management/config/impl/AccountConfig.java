package com.soarclient.management.config.impl;

import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soarclient.Soar;
import com.soarclient.management.account.Account;
import com.soarclient.management.account.AccountAuth;
import com.soarclient.management.account.AccountManager;
import com.soarclient.management.account.AccountType;
import com.soarclient.management.account.impl.MicrosoftAccount;
import com.soarclient.management.account.impl.OfflineAccount;
import com.soarclient.management.config.Config;
import com.soarclient.management.config.ConfigType;
import com.soarclient.utils.JsonUtils;

public class AccountConfig extends Config {

	public AccountConfig() {
		super(ConfigType.ACCOUNT);
	}

	@Override
	public void onLoad() {

		Gson gson = new Gson();
		AccountManager accountManager = Soar.getInstance().getAccountManager();
		JsonArray jsonArray = JsonUtils.getArrayProperty(jsonObject, "accounts");

		if (jsonArray != null) {

			Iterator<JsonElement> iterator = jsonArray.iterator();

			while (iterator.hasNext()) {

				JsonElement jsonElement = (JsonElement) iterator.next();
				JsonObject accJsonObject = gson.fromJson(jsonElement, JsonObject.class);

				final AccountType type = AccountType.getById(accJsonObject.get("type").getAsString());

				if (type.equals(AccountType.MICROSOFT)) {
					accountManager.getAccounts().add(new MicrosoftAccount(accJsonObject));
				} else if (type.equals(AccountType.OFFLINE)) {
					accountManager.getAccounts().add(new OfflineAccount(accJsonObject));
				}
			}
		}

		accountManager.setCurrentAccount(
				accountManager.getByUuid(JsonUtils.getStringProperty(jsonObject, "currentAccount", "null")));

		Account acc = accountManager.getCurrentAccount();

		if (acc != null) {
			AccountAuth.refresh(acc);
		}
	}

	@Override
	public void onSave() {

		AccountManager accountManager = Soar.getInstance().getAccountManager();
		JsonArray jsonArray = new JsonArray();

		jsonObject.addProperty("currentAccount",
				accountManager.getCurrentAccount().getUUID().toString().replace("-", ""));

		for (Account acc : accountManager.getAccounts()) {
			final JsonObject innerJsonObject = acc.toJson();
			innerJsonObject.addProperty("type", acc.getType().getId());
			jsonArray.add(innerJsonObject);
		}

		jsonObject.add("accounts", jsonArray);
	}
}
