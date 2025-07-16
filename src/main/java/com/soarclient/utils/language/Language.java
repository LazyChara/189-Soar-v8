package com.soarclient.utils.language;

public enum Language {
	ENGLISH("en");

	private final String id;

	private Language(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
