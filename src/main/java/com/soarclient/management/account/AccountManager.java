package com.soarclient.management.account;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {

	private List<Account> accounts = new ArrayList<>();
	private Account currentAccount;

	public List<Account> getAccounts() {
		return accounts;
	}

	public Account getByUuid(String inputUuid) {

		if (inputUuid == null) {
			return null;
		}

		for (Account acc : accounts) {

			String pUuid = acc.getUUID().toString().replace("-", "");
			String uuid = inputUuid.replace("-", "");

			if (pUuid.equals(uuid)) {
				return acc;
			}
		}

		return null;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(Account currentAccount) {
		this.currentAccount = currentAccount;
	}
}
