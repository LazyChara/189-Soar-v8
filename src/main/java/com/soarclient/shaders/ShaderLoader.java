package com.soarclient.shaders;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderLoader {

	public static Shader loadShader(String path, boolean vert, boolean frag) {

		if (!vert && !frag) {
			return new Shader(-1, true);
		}

		String sourceVertex = null;
		String sourceFrag = null;

		if (vert) {
			try {
				sourceVertex = readShader("/assets/soar/shaders/" + path + ".vert");
			} catch (Exception e) {
				e.printStackTrace();
				return new Shader(-1, true);
			}
		}
		if (frag) {
			try {
				sourceFrag = readShader("/assets/soar/shaders/" + path + ".frag");
			} catch (Exception e) {
				e.printStackTrace();
				return new Shader(-1, true);
			}
		}

		int vertex = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		int fragment = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

		if (vert) {
			GL20.glShaderSource(vertex, sourceVertex);
			GL20.glCompileShader(vertex);
		}

		if (frag) {
			GL20.glShaderSource(fragment, sourceFrag);
			GL20.glCompileShader(fragment);
		}

		int program = GL20.glCreateProgram();

		if (vert) {
			GL20.glAttachShader(program, vertex);
		}

		if (frag) {
			GL20.glAttachShader(program, fragment);
		}

		GL20.glLinkProgram(program);

		if (vert) {
			GL20.glDeleteShader(vertex);
		}

		if (frag) {
			GL20.glDeleteShader(fragment);
		}

		int status = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);

		if (status == GL11.GL_FALSE) {
			return new Shader(program, true);
		}

		return new Shader(program, false);
	}

	private static String readShader(String filePath) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(Shader.class.getResourceAsStream(filePath)));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line).append('\n');
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}