package com.soarclient.management.mod.settings;

import com.soarclient.management.mod.Mod;

public abstract class Setting {

	private String name;
	private String description;
	private String icon;
	private Mod parent;

	public Setting(String name, String description, String icon, Mod parent) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.parent = parent;
	}

	public abstract void reset();

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}

	public Mod getParent() {
		return parent;
	}
}