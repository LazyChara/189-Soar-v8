package com.soarclient.management.account;

public enum AccountType {
	MICROSOFT("microsoft"), OFFLINE("offline");

	private String id;

	private AccountType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public static AccountType getById(String id) {

		for (AccountType t : AccountType.values()) {
			if (t.getId().equals(id)) {
				return t;
			}
		}

		return OFFLINE;
	}
}
