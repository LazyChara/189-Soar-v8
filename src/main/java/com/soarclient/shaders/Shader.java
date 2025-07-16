package com.soarclient.shaders;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Shader {

	public int programID;
	public boolean errored;

	private final Map<String, Integer> uniformCache = new HashMap<>();

	public Shader(int programID, boolean errored) {
		this.programID = programID;
		this.errored = errored;
	}

	public void init() {
		if (errored)
			return;
		GL20.glUseProgram(programID);
	}

	public void bind() {

		ScaledResolution sr = ScaledResolution.get(Minecraft.getMinecraft());

		double width = sr.getScaledWidth_double();
		double height = sr.getScaledHeight_double();

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2d(0, height);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2d(width, height);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2d(width, 0);
		GL11.glEnd();
	}

	public void finish() {
		GL20.glUseProgram(0);
	}

	public void delete() {
		if (programID > 0) {
			GL20.glDeleteProgram(programID);
		}
	}

	public int getUniformLocation(String name) {
		if (errored || programID <= 0)
			return -1;
		return uniformCache.computeIfAbsent(name, uniform -> GL20.glGetUniformLocation(programID, uniform));
	}

	public void setUniform1f(String name, float v1) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniform1f(location, v1);
	}

	public void setUniform1i(String name, int v1) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniform1i(location, v1);
	}

	public void setUniform1fv(String name, FloatBuffer floatBuffer) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniform1fv(location, floatBuffer);
	}

	public void setUniform2f(String name, float v1, float v2) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniform2f(location, v1, v2);
	}

	public void setUniform3f(String name, float v1, float v2, float v3) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniform3f(location, v1, v2, v3);
	}

	public void setUniformMat4(String name, FloatBuffer mat) {
		int location = getUniformLocation(name);
		if (location >= 0)
			GL20.glUniformMatrix4(location, false, mat);
	}
}