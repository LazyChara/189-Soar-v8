package com.soarclient.utils.file;

import java.io.File;

import net.minecraft.client.Minecraft;

public class FileLocation {

	public static final File MAIN_DIR = new File(Minecraft.getMinecraft().mcDataDir, "soar");
	public static final File MUSIC_DIR = new File(MAIN_DIR, "music");
	public static final File CACHE_DIR = new File(MAIN_DIR, "cache");
	public static final File CONFIG_DIR = new File(MAIN_DIR, "config");
	public static final File PROFILE_DIR = new File(MAIN_DIR, "profile");

	public static void init() {
		FileUtils.createDir(MAIN_DIR);
		FileUtils.createDir(MUSIC_DIR);
		FileUtils.createDir(CACHE_DIR);
		FileUtils.createDir(CONFIG_DIR);
		FileUtils.createDir(PROFILE_DIR);
	}
}
