package com.soarclient.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ImageUtils {

	public static Color calculateAverageColor(BufferedImage image) {
		long totalRed = 0;
		long totalGreen = 0;
		long totalBlue = 0;
		int width = image.getWidth();
		int height = image.getHeight();
		int totalPixels = width * height;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color pixelColor = new Color(image.getRGB(x, y));
				totalRed += pixelColor.getRed();
				totalGreen += pixelColor.getGreen();
				totalBlue += pixelColor.getBlue();
			}
		}

		int averageRed = (int) (totalRed / totalPixels);
		int averageGreen = (int) (totalGreen / totalPixels);
		int averageBlue = (int) (totalBlue / totalPixels);

		return new Color(averageRed, averageGreen, averageBlue);
	}

	public static int toTexture(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(x, y);
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		int textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
				buffer);

		return textureId;
	}

	public static BufferedImage toBufferedImage(int textureID) {

		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, widthBuffer);
		GL11.glGetTexLevelParameteriv(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, heightBuffer);

		int width = widthBuffer.get(0);
		int height = heightBuffer.get(0);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int i = (x + (width * y)) * 4;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = buffer.get(i + 3) & 0xFF;

				int argb = (a << 24) | (r << 16) | (g << 8) | b;
				image.setRGB(x, height - (y + 1), argb);
			}
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return image;
	}

	public static BufferedImage combine(BufferedImage img1, BufferedImage img2) {

		int width = Math.max(img1.getWidth(), img2.getWidth());
		int height = Math.max(img1.getHeight(), img2.getHeight());
		BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics g = combinedImage.createGraphics();
		g.drawImage(img1, 0, 0, null);
		g.drawImage(img2, 0, 0, null);

		return combinedImage;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {

		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage image = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return image;
	}

	public static BufferedImage scissor(BufferedImage img, int x, int y, int width, int height) {

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();

		g2d.drawImage(img, 0, 0, width, height, x, y, x + width, y + height, null);
		g2d.dispose();

		return image;
	}

	public static BufferedImage flipHorizontal(BufferedImage img) {

		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int srcPixel = img.getRGB(x, y);
				int destX = width - x - 1;
				flippedImage.setRGB(destX, y, srcPixel);
			}
		}

		return flippedImage;
	}

	public static BufferedImage flipVertical(BufferedImage img) {

		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int srcPixel = img.getRGB(x, y);
				int destY = height - y - 1;
				flippedImage.setRGB(x, destY, srcPixel);
			}
		}

		return flippedImage;
	}

	public static int[] imageToPixels(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();

		return image.getRGB(0, 0, width, height, null, 0, width);
	}
}