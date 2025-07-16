package net.minecraft.client.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.soarclient.libraries.patches.font.EnhancementManager;
import com.soarclient.libraries.patches.font.hash.impl.StringHash;
import com.soarclient.libraries.patches.font.text.CachedString;
import com.soarclient.libraries.patches.font.text.EnhancedFontRenderer;
import com.soarclient.management.mod.impl.misc.NameProtectMod;
import com.soarclient.utils.tuples.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class FontRenderer implements IResourceManagerReloadListener {

	/** Array of width of all the characters in default.png */
	private int[] charWidth = new int[256];

	/** the height in pixels of default text */
	public int FONT_HEIGHT = 9;
	public Random fontRandom = new Random();

	/**
	 * Array of the start/end column (in upper/lower nibble) for every glyph in the
	 * /font directory.
	 */
	private byte[] glyphWidth = new byte[65536];

	/**
	 * Array of RGB triplets defining the 16 standard chat colors followed by 16
	 * darker version of the same colors for drop shadows.
	 */
	private int[] colorCode = new int[32];
	private final ResourceLocation locationFontTexture;

	/** The RenderEngine used to load and setup glyph textures. */
	private final TextureManager renderEngine;

	/** Current X coordinate at which to draw the next character. */
	private float posX;

	/** Current Y coordinate at which to draw the next character. */
	private float posY;

	/**
	 * If true, strings should be rendered with Unicode fonts instead of the
	 * default.png font
	 */
	private boolean unicodeFlag;

	/**
	 * If true, the Unicode Bidirectional Algorithm should be run before rendering
	 * any string.
	 */
	private boolean bidiFlag;

	/** Used to specify new red value for the current color. */
	private float red;

	/** Used to specify new blue value for the current color. */
	private float green;

	/** Used to specify new green value for the current color. */
	private float blue;

	/** Used to speify new alpha value for the current color. */
	private float alpha;

	/** Text color of the currently rendering string. */
	private int textColor;

	/** Set if the "k" style (random) is active in currently rendering string */
	private boolean randomStyle;

	/** Set if the "l" style (bold) is active in currently rendering string */
	private boolean boldStyle;

	/** Set if the "o" style (italic) is active in currently rendering string */
	private boolean italicStyle;

	/**
	 * Set if the "n" style (underlined) is active in currently rendering string
	 */
	private boolean underlineStyle;

	/**
	 * Set if the "m" style (strikethrough) is active in currently rendering string
	 */
	private boolean strikethroughStyle;

	public static boolean forceRefresh = false;
	public static final String characterDictionary = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
	private final EnhancedFontRenderer enhancedFontRenderer = EnhancementManager.getInstance()
			.getEnhancement(EnhancedFontRenderer.class);
	private final Minecraft mc = Minecraft.getMinecraft();
	public int glTextureId = -1;
	private int texSheetDim = 256;
	private float fontTexHeight = 16 * texSheetDim + 128;
	private float fontTexWidth = 16 * texSheetDim;
	private int regularCharDim = 128;
	private boolean drawing = false;
	private static final String COLOR_RESET_PHRASE = '\u00a7' + "r";

	public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn,
			boolean unicode) {
		this.locationFontTexture = location;
		this.renderEngine = textureManagerIn;
		this.unicodeFlag = unicode;
		textureManagerIn.bindTexture(this.locationFontTexture);

		for (int i = 0; i < 32; ++i) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i >> 0 & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (gameSettingsIn.anaglyph) {
				int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
				int k1 = (k * 30 + l * 70) / 100;
				int l1 = (k * 30 + i1 * 70) / 100;
				k = j1;
				l = k1;
				i1 = l1;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}

		this.readGlyphSizes();
	}

	private void establishSize() {
		int regWidth = 256;
		for (int i = 0; i < 256; i++) {
			try (final InputStream stream = mc.getResourceManager()
					.getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i)))
					.getInputStream()) {
				regWidth = ImageIO.read(stream).getWidth();
				break;
			} catch (Exception ignored) {
			}
		}

		texSheetDim = regWidth;
		int specWidth = 128;

		try (final InputStream stream = mc.getResourceManager().getResource(this.locationFontTexture)
				.getInputStream()) {
			specWidth = ImageIO.read(stream).getWidth();
		} catch (IOException e) {
		}

		regularCharDim = specWidth;
		fontTexHeight = 16 * texSheetDim + specWidth;
		fontTexWidth = 16 * texSheetDim;
	}

	public void create() {

		establishSize();

		forceRefresh = false;

		if (glTextureId != -1) {
			GlStateManager.deleteTexture(glTextureId);
		}

		final BufferedImage bufferedImage = new BufferedImage((int) fontTexWidth, (int) fontTexHeight,
				BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < 256; i++) {
			try (final InputStream stream = mc.getResourceManager()
					.getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i)))
					.getInputStream()) {
				bufferedImage.getGraphics().drawImage(ImageIO.read(stream), i / 16 * texSheetDim, i % 16 * texSheetDim,
						null);
				if (i == 0x0d) {
					try (final InputStream sustream = FontRenderer.class
							.getResourceAsStream("/assets/soar/font_glyph_data.bin")) {
						Graphics2D graphics = bufferedImage.createGraphics();
						graphics.setComposite(AlphaComposite.Src);
						graphics.drawImage(ImageIO.read(Objects.requireNonNull(sustream)), 0x9DE % 16 * 16,
								(i % 16 * texSheetDim) + (0x9DE / 16 - 16), null);
						glyphWidth[0xD9E] = 14;
					}
				}
			} catch (Exception ignored) {
			}
		}

		try (final InputStream stream = mc.getResourceManager().getResource(this.locationFontTexture)
				.getInputStream()) {
			bufferedImage.getGraphics().drawImage(ImageIO.read(stream), 0, 16 * texSheetDim, null);
		} catch (IOException e) {
		}

		glTextureId = new DynamicTexture(bufferedImage).getGlTextureId();
	}

	private void deleteTextureId() {
		if (glTextureId != -1) {
			GlStateManager.deleteTexture(glTextureId);
			glTextureId = -1;
		}
	}

	public static String clearColorReset(String text) {
		int startIndex = 0;
		int endIndex = text.length();

		while (text.indexOf(COLOR_RESET_PHRASE, startIndex) == startIndex) {
			startIndex += 2;
		}
		int e;
		while ((e = text.lastIndexOf(COLOR_RESET_PHRASE, endIndex - 1)) == endIndex - 2 && e != -1) {
			endIndex -= 2;
		}

		if (endIndex < startIndex)
			endIndex = startIndex;

		return text.substring(startIndex, endIndex);
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {

		for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances()) {
			enhancedFontRenderer.invalidateAll();
		}

		forceRefresh = true;
		this.readFontTexture();
	}

	private void readFontTexture() {
		BufferedImage bufferedimage;

		try {
			bufferedimage = TextureUtil.readBufferedImage(Minecraft.getMinecraft().getResourceManager()
					.getResource(this.locationFontTexture).getInputStream());
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		}

		int i = bufferedimage.getWidth();
		int j = bufferedimage.getHeight();
		int[] aint = new int[i * j];
		bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
		int k = j / 16;
		int l = i / 16;
		int i1 = 1;
		float f = 8.0F / (float) l;

		for (int j1 = 0; j1 < 256; ++j1) {
			int k1 = j1 % 16;
			int l1 = j1 / 16;

			if (j1 == 32) {
				this.charWidth[j1] = 3 + i1;
			}

			int i2;

			for (i2 = l - 1; i2 >= 0; --i2) {
				int j2 = k1 * l + i2;
				boolean flag = true;

				for (int k2 = 0; k2 < k && flag; ++k2) {
					int l2 = (l1 * l + k2) * i;

					if ((aint[j2 + l2] >> 24 & 255) != 0) {
						flag = false;
					}
				}

				if (!flag) {
					break;
				}
			}

			++i2;
			this.charWidth[j1] = (int) (0.5D + (double) ((float) i2 * f)) + i1;
		}
	}

	private void readGlyphSizes() {
		InputStream inputstream = null;

		try {
			inputstream = Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
			inputstream.read(this.glyphWidth);
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}
	}

	/**
	 * Render the given char
	 */
	public float renderChar(char ch, boolean italic) {
		if (ch == 32 || ch == 160) {
			return this.unicodeFlag ? 4.0F : getCharWidth(ch);
		} else {
			final int charIndex = characterDictionary.indexOf(ch);
			return charIndex != -1 && !this.unicodeFlag ? this.renderDefaultChar(charIndex, italic, ch)
					: this.renderUnicodeChar(ch, italic);
		}
	}

	/**
	 * Render a single character with the default.png font at current (posX,posY)
	 * location...
	 */
	private float renderDefaultChar(int characterIndex, boolean italic, char ch) {
		final float characterX = (characterIndex % 16 * 8 * regularCharDim >> 7) + .01f;
		final float characterY = ((characterIndex >> 4) * 8 * regularCharDim >> 7) + 16 * texSheetDim + .01f;

		final int italicStyle = italic ? 1 : 0;
		final float charWidth = getCharWidth(ch);
		final float smallCharWidth = charWidth - 0.01F;

		startDrawing();
		final float uvHeight = 7.99F * regularCharDim / 128;
		final float uvWidth = smallCharWidth * regularCharDim / 128;

		GL11.glTexCoord2f(characterX / fontTexWidth, characterY / fontTexHeight);
		GL11.glVertex2f(this.posX + (float) italicStyle, this.posY);

		GL11.glTexCoord2f(characterX / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
		GL11.glVertex2f(this.posX - (float) italicStyle, this.posY + 7.99F);

		final int offset = regularCharDim / 128;
		GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, (characterY + uvHeight) / fontTexHeight);
		GL11.glVertex2f(this.posX + smallCharWidth - 1.0F - (float) italicStyle, this.posY + 7.99F);

		GL11.glTexCoord2f((characterX + uvWidth - offset) / fontTexWidth, characterY / fontTexHeight);
		GL11.glVertex2f(this.posX + smallCharWidth - 1.0F + (float) italicStyle, this.posY);

		return charWidth;
	}

	private void startDrawing() {
		if (!drawing) {
			drawing = true;
			GL11.glBegin(GL11.GL_QUADS);
		}
	}

	private void endDrawing() {
		if (drawing) {
			drawing = false;
			GL11.glEnd();
		}
	}

	private Pair<Float, Float> getUV(char characterIndex) {
		final int page = characterIndex / 256;
		final int row = page >> 4;
		final int column = page % 16;
		final int glyphWidth = this.glyphWidth[characterIndex] >>> 4;
		final float charX = (float) (characterIndex % 16 << 4) + glyphWidth + (.05f * page / 39f);
		final float charY = (float) (((characterIndex & 255) >> 4) * 16) + (.05f * page / 39f);
		return Pair.of((row * texSheetDim + charX) / fontTexWidth, (column * texSheetDim + charY) / fontTexHeight);
	}

	private float renderUnicodeChar(char ch, boolean italic) {
		if (glyphWidth[ch] == 0) {
			return 0.0F;
		} else {
			final Pair<Float, Float> uv = getUV(ch);
			final int glyphX = glyphWidth[ch] >>> 4;
			final int glyphY = glyphWidth[ch] & 15;
			final float floatGlyphX = (float) glyphX;
			final float modifiedY = (float) glyphY + 1;
			final float combinedGlyphSize = modifiedY - floatGlyphX - 0.02F;
			final float italicStyle = italic ? 1.0F : 0.0F;
			startDrawing();

			final float v = 15.98F * texSheetDim / 256;
			GL11.glTexCoord2f(uv.getFirst(), uv.getSecond());
			GL11.glVertex2f(this.posX + italicStyle, this.posY);

			GL11.glTexCoord2f(uv.getFirst(), uv.getSecond() + v / fontTexHeight);
			GL11.glVertex2f(this.posX - italicStyle, this.posY + 7.99F);

			final float texAdj = combinedGlyphSize + .5f;
			GL11.glTexCoord2f(uv.getFirst() + texAdj / fontTexHeight, uv.getSecond() + v / fontTexHeight);
			GL11.glVertex2f(this.posX + combinedGlyphSize / 2.0F - italicStyle, this.posY + 7.99F);

			GL11.glTexCoord2f(uv.getFirst() + texAdj / fontTexHeight, uv.getSecond());
			GL11.glVertex2f(this.posX + combinedGlyphSize / 2.0F + italicStyle, this.posY);

			return (modifiedY - floatGlyphX) / 2.0F + 1.0F;
		}
	}

	/**
	 * Draws the specified string with a shadow.
	 */
	public int drawStringWithShadow(String text, float x, float y, int color) {
		return this.drawString(text, x, y, color, true);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, int x, int y, int color) {
		return this.drawString(text, (float) x, (float) y, color, false);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		GlStateManager.enableAlpha();
		this.resetStyles();
		int i;

		if (dropShadow) {
			i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
			i = Math.max(i, this.renderString(text, x, y, color, false));
		} else {
			i = this.renderString(text, x, y, color, false);
		}

		return i;
	}

	/**
	 * Apply Unicode Bidirectional Algorithm to string and return a new possibly
	 * reordered string for visual rendering.
	 */
	private String bidiReorder(String text) {
		try {
			Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(2);
		} catch (ArabicShapingException var3) {
			return text;
		}
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of
	 * string rendering
	 */
	private void resetStyles() {
		this.randomStyle = false;
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	/**
	 * Render a single line string at the current (posX,posY) and update posX
	 */
	private void renderStringAtPos(String text, boolean shadow) {

		if (this.renderEngine == null) {
			this.deleteTextureId();
			return;
		}

		if (glTextureId == -1 || forceRefresh) {
			create();
		}

		text = clearColorReset(text);

		if (text.isEmpty()) {
			return;
		}

		float posX = this.posX;
		float posY = this.posY;
		this.posY = (0.0f);
		this.posX = (0.0f);

		float red = this.red;
		float green = this.green;
		float blue = this.blue;
		float alpha = this.alpha;

		GlStateManager.bindTexture(glTextureId);
		GlStateManager.translate(posX, posY, 0F);

		final GlStateManager.TextureState[] textureStates = GlStateManager.getTextureState();
		final GlStateManager.TextureState textureState = textureStates[GlStateManager.activeTextureUnit];

		final StringHash hash = new StringHash(text, red, green, blue, alpha, shadow);
		final CachedString cachedString = this.enhancedFontRenderer.get(hash);

		if (cachedString != null) {
			GlStateManager.color(red, green, blue, alpha);
			GlStateManager.callList(cachedString.getListId());

			textureState.textureName = glTextureId;

			GlStateManager.Color colorState = GlStateManager.getColorState();
			colorState.red = cachedString.getLastRed();
			colorState.green = cachedString.getLastGreen();
			colorState.blue = cachedString.getLastBlue();
			colorState.alpha = cachedString.getLastAlpha();
			GlStateManager.translate(-posX, -posY, 0.0f);
			GlStateManager.resetColor();
			GlStateManager.color(1, 1, 1, 1);

			this.posX = (posX + cachedString.getWidth());
			this.posY = (posY + cachedString.getHeight());
			return;
		}

		int list = 0;
		textureState.textureName = glTextureId;
		GlStateManager.resetColor();
		list = enhancedFontRenderer.getGlList();
		GL11.glNewList(list, GL11.GL_COMPILE_AND_EXECUTE);

		boolean obfuscated = false;
		final CachedString value = new CachedString(text, list, this.posX - posX, this.posY - posY);
		final Deque<RenderPair> underline = new LinkedList<>();
		final Deque<RenderPair> strikethrough = new LinkedList<>();

		value.setLastRed(red);
		value.setLastGreen(green);
		value.setLastBlue(blue);
		value.setLastAlpha(alpha);

		for (int messageChar = 0; messageChar < text.length(); ++messageChar) {
			char letter = text.charAt(messageChar);

			if (letter == 167 && messageChar + 1 < text.length()) {
				int styleIndex = "0123456789abcdefklmnor"
						.indexOf(text.toLowerCase(Locale.ENGLISH).charAt(messageChar + 1));

				if (styleIndex < 16) {
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					this.randomStyle = false;
					this.boldStyle = false;

					if (styleIndex < 0) {
						styleIndex = 15;
					}

					if (shadow) {
						styleIndex += 16;
					}

					int currentColor = colorCode[styleIndex];

					this.textColor = currentColor;

					final float colorRed = (float) (currentColor >> 16) / 255.0F;
					final float colorGreen = (float) (currentColor >> 8 & 255) / 255.0F;
					final float colorBlue = (float) (currentColor & 255) / 255.0F;

					GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

					value.setLastAlpha(alpha);
					value.setLastGreen(colorGreen);
					value.setLastBlue(colorBlue);
					value.setLastRed(colorRed);
				} else if (styleIndex == 16) {
					this.randomStyle = true;
					obfuscated = true;
				} else if (styleIndex == 17) {
					this.boldStyle = true;
				} else if (styleIndex == 18) {
					this.strikethroughStyle = true;
				} else if (styleIndex == 19) {
					this.underlineStyle = true;
				} else if (styleIndex == 20) {
					this.italicStyle = true;
				} else {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					GlStateManager.color(red, green, blue, alpha);

					value.setLastGreen(green);
					value.setLastAlpha(alpha);
					value.setLastBlue(blue);
					value.setLastRed(red);
				}

				++messageChar;
			} else {
				int index = characterDictionary.indexOf(letter);

				if (this.randomStyle && index != -1) {
					final float charWidthFloat = getCharWidth(letter);
					char charIndex;

					do {
						index = fontRandom.nextInt(characterDictionary.length());
						charIndex = characterDictionary.charAt(index);
					} while (charWidthFloat != getCharWidth(charIndex));

					letter = charIndex;
				}

				final boolean unicode = this.unicodeFlag;
				final float boldWidth = getBoldOffset(index);
				final boolean small = (letter == 0 || index == -1 || unicode) && shadow;

				if (small) {
					this.posX = this.posX - boldWidth;
					this.posY = this.posY - boldWidth;
				}

				float effectiveWidth = this.renderChar(letter, this.italicStyle);
				if (small) {
					this.posX = this.posX + boldWidth;
					this.posY = this.posY + boldWidth;
				}

				if (this.boldStyle) {
					this.posX = (this.posX + boldWidth);

					if (small) {
						this.posX = this.posX - boldWidth;
						this.posY = this.posY - boldWidth;
					}

					this.renderChar(letter, this.italicStyle);
					this.posX = this.posX - boldWidth;

					if (small) {
						this.posX = this.posX + boldWidth;
						this.posY = this.posY + boldWidth;
					}

					++effectiveWidth;
				}

				if (this.strikethroughStyle) {
					adjustOrAppend(strikethrough, this.posX, effectiveWidth, value.getLastRed(), value.getLastGreen(),
							value.getLastBlue(), value.getLastAlpha());
				}

				if (this.underlineStyle) {
					adjustOrAppend(underline, this.posX, effectiveWidth, value.getLastRed(), value.getLastGreen(),
							value.getLastBlue(), value.getLastAlpha());
				}

				this.posX = this.posX + (int) effectiveWidth;
			}
		}

		endDrawing();
		final boolean hasStyle = underline.size() > 0 || strikethrough.size() > 0;

		if (hasStyle) {
			GlStateManager.disableTexture2D();
			GL11.glBegin(GL11.GL_QUADS);

			for (final RenderPair renderPair : strikethrough) {
				GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
				GL11.glVertex2f(renderPair.posX, this.posY + 4.0f);
				GL11.glVertex2f(renderPair.posX + renderPair.width, this.posY + 4.0f);
				GL11.glVertex2f(renderPair.posX + renderPair.width, this.posY + 3.0f);
				GL11.glVertex2f(renderPair.posX, this.posY + 3.0f);
			}

			for (final RenderPair renderPair : underline) {
				GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
				GL11.glVertex2f(renderPair.posX - 1.0f, this.posY + 9);
				GL11.glVertex2f(renderPair.posX + renderPair.width, this.posY + 9);
				GL11.glVertex2f(renderPair.posX + renderPair.width, this.posY + 9 - 1.0F);
				GL11.glVertex2f(renderPair.posX - 1.0f, this.posY + 9 - 1.0F);
			}

			GL11.glEnd();
			GlStateManager.enableTexture2D();
		}

		GL11.glEndList();
		this.enhancedFontRenderer.cache(hash, value);

		value.setWidth(this.posX);

		this.posX = posX + value.getWidth();
		this.posY = posY + value.getHeight();

		if (obfuscated) {
			this.enhancedFontRenderer.getObfuscated().add(hash);
		}

		GlStateManager.translate(-posX, -posY, 0F);
	}

	private void adjustOrAppend(Deque<RenderPair> style, float posX, float effectiveWidth, float lastRed,
			float lastGreen, float lastBlue, float lastAlpha) {
		final RenderPair lastStart = style.peekLast();
		if (lastStart != null && lastStart.red == lastRed && lastStart.green == lastGreen && lastStart.blue == lastBlue
				&& lastStart.alpha == lastAlpha) {
			if (lastStart.posX + lastStart.width >= posX - 1) {
				lastStart.width = posX + effectiveWidth - lastStart.posX;
				return;
			}
		}
		style.add(new RenderPair(posX, effectiveWidth, lastRed, lastGreen, lastBlue, lastAlpha));
	}

	private float getBoldOffset(int index) {
		return index == -1 || this.unicodeFlag ? 0.5F : 1;
	}

	/**
	 * Render string either left or right aligned depending on bidiFlag
	 */
	private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
		if (this.bidiFlag) {
			int i = this.getStringWidth(this.bidiReorder(text));
			x = x + width - i;
		}

		return this.renderString(text, (float) x, (float) y, color, dropShadow);
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and
	 * calling renderStringAtPos()
	 */
	private int renderString(String text, float x, float y, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		} else {

			if (NameProtectMod.getInstance().isEnabled()) {
				text = text.replace(Minecraft.getMinecraft().getSession().getUsername(),
						NameProtectMod.getInstance().getNameSetting().getValue());
			}

			if (this.bidiFlag) {
				text = this.bidiReorder(text);
			}

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}

			this.red = (float) (color >> 16 & 255) / 255.0F;
			this.green = (float) (color >> 8 & 255) / 255.0F;
			this.blue = (float) (color & 255) / 255.0F;
			this.alpha = (float) (color >> 24 & 255) / 255.0F;
			GlStateManager.color(this.red, this.green, this.blue, this.alpha);
			this.posX = x;
			this.posY = y;
			this.renderStringAtPos(text, dropShadow);
			return (int) this.posX;
		}
	}

	/**
	 * Returns the width of this string. Equivalent of
	 * FontMetrics.stringWidth(String s).
	 */
	public int getStringWidth(String text) {

		if (text == null) {
			return 0;
		}

		return enhancedFontRenderer.getStringWidthCache().computeIfAbsent(text, width -> getUncachedWidth(text));
	}

	private int getUncachedWidth(String text) {
		if (text == null) {
			return 0;
		} else {

			if (NameProtectMod.getInstance().isEnabled()) {
				text = text.replace(Minecraft.getMinecraft().getSession().getUsername(),
						NameProtectMod.getInstance().getNameSetting().getValue());
			}

			float width = 0;
			boolean bold = false;

			for (int messageChar = 0; messageChar < text.length(); ++messageChar) {
				char character = text.charAt(messageChar);
				float characterWidth = getCharWidth(character);

				if (characterWidth < 0 && messageChar < text.length() - 1) {
					++messageChar;
					character = text.charAt(messageChar);

					if (character != 108 && character != 76) {
						if (character == 114 || character == 82) {
							bold = false;
						}
					} else {
						bold = true;
					}

					characterWidth = 0;
				}

				width += characterWidth;

				if (bold && characterWidth > 0) {
					width += 1;
				}
			}

			return (int) width;
		}
	}

	/**
	 * Returns the width of this character as rendered.
	 */
	public int getCharWidth(char character) {
		if (character == 167) {
			return -1;
		} else if (character == 32) {
			return 4;
		} else {
			int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
					.indexOf(character);

			if (character > 0 && i != -1 && !this.unicodeFlag) {
				return this.charWidth[i];
			} else if (this.glyphWidth[character] != 0) {
				int j = this.glyphWidth[character] >>> 4;
				int k = this.glyphWidth[character] & 15;

				if (k > 7) {
					k = 15;
					j = 0;
				}

				++k;
				return (k - j) / 2 + 1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public String trimStringToWidth(String text, int width) {
		return this.trimStringToWidth(text, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public String trimStringToWidth(String text, int width, boolean reverse) {
		StringBuilder stringbuilder = new StringBuilder();
		int i = 0;
		int j = reverse ? text.length() - 1 : 0;
		int k = reverse ? -1 : 1;
		boolean flag = false;
		boolean flag1 = false;

		for (int l = j; l >= 0 && l < text.length() && i < width; l += k) {
			char c0 = text.charAt(l);
			int i1 = this.getCharWidth(c0);

			if (flag) {
				flag = false;

				if (c0 != 108 && c0 != 76) {
					if (c0 == 114 || c0 == 82) {
						flag1 = false;
					}
				} else {
					flag1 = true;
				}
			} else if (i1 < 0) {
				flag = true;
			} else {
				i += i1;

				if (flag1) {
					++i;
				}
			}

			if (i > width) {
				break;
			}

			if (reverse) {
				stringbuilder.insert(0, (char) c0);
			} else {
				stringbuilder.append(c0);
			}
		}

		return stringbuilder.toString();
	}

	/**
	 * Remove all newline characters from the end of the string
	 */
	private String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n")) {
			text = text.substring(0, text.length() - 1);
		}

		return text;
	}

	/**
	 * Splits and draws a String with wordwrap (maximum length is parameter k)
	 */
	public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
		this.resetStyles();
		this.textColor = textColor;
		str = this.trimStringNewline(str);
		this.renderSplitString(str, x, y, wrapWidth, false);
	}

	/**
	 * Perform actual work of rendering a multi-line string with wordwrap and with
	 * darker drop shadow color if flag is set
	 */
	private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
		for (String s : this.listFormattedStringToWidth(str, wrapWidth)) {
			this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
			y += this.FONT_HEIGHT;
		}
	}

	/**
	 * Returns the width of the wordwrapped String (maximum length is parameter k)
	 * 
	 * @param str       The string to split
	 * @param maxLength The maximum length of a word
	 */
	public int splitStringWidth(String str, int maxLength) {
		return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
	}

	/**
	 * Set unicodeFlag controlling whether strings should be rendered with Unicode
	 * fonts instead of the default.png font.
	 */
	public void setUnicodeFlag(boolean unicodeFlagIn) {
		this.unicodeFlag = unicodeFlagIn;
	}

	/**
	 * Get unicodeFlag controlling whether strings should be rendered with Unicode
	 * fonts instead of the default.png font.
	 */
	public boolean getUnicodeFlag() {
		return this.unicodeFlag;
	}

	/**
	 * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run
	 * before rendering any string.
	 */
	public void setBidiFlag(boolean bidiFlagIn) {
		this.bidiFlag = bidiFlagIn;
	}

	public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
		return Arrays.<String>asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
	}

	/**
	 * Inserts newline and formatting into a string to wrap it within the specified
	 * width.
	 */
	String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int i = this.sizeStringToWidth(str, wrapWidth);

		if (str.length() <= i) {
			return str;
		} else {
			String s = str.substring(0, i);
			char c0 = str.charAt(i);
			boolean flag = c0 == 32 || c0 == 10;
			String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
			return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
		}
	}

	/**
	 * Determines how many characters from the string will fit into the specified
	 * width.
	 */
	private int sizeStringToWidth(String str, int wrapWidth) {
		int i = str.length();
		int j = 0;
		int k = 0;
		int l = -1;

		for (boolean flag = false; k < i; ++k) {
			char c0 = str.charAt(k);

			switch (c0) {
			case '\n':
				--k;
				break;

			case ' ':
				l = k;

			default:
				j += this.getCharWidth(c0);

				if (flag) {
					++j;
				}

				break;

			case '\u00a7':
				if (k < i - 1) {
					++k;
					char c1 = str.charAt(k);

					if (c1 != 108 && c1 != 76) {
						if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
							flag = false;
						}
					} else {
						flag = true;
					}
				}
			}

			if (c0 == 10) {
				++k;
				l = k;
				break;
			}

			if (j > wrapWidth) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	/**
	 * Checks if the char code is a hexadecimal character, used to set colour.
	 */
	private static boolean isFormatColor(char colorChar) {
		return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102
				|| colorChar >= 65 && colorChar <= 70;
	}

	/**
	 * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
	 */
	private static boolean isFormatSpecial(char formatChar) {
		return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114
				|| formatChar == 82;
	}

	/**
	 * Digests a string for nonprinting formatting characters then returns a string
	 * containing only that formatting.
	 */
	public static String getFormatFromString(String text) {
		String s = "";
		int i = -1;
		int j = text.length();

		while ((i = text.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c0 = text.charAt(i + 1);

				if (isFormatColor(c0)) {
					s = "\u00a7" + c0;
				} else if (isFormatSpecial(c0)) {
					s = s + "\u00a7" + c0;
				}
			}
		}

		return s;
	}

	/**
	 * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be
	 * run before rendering any string
	 */
	public boolean getBidiFlag() {
		return this.bidiFlag;
	}

	public int getColorCode(char character) {
		return this.colorCode["0123456789abcdef".indexOf(character)];
	}

	static class RenderPair {
		private final float red;
		private final float green;
		private final float blue;
		private final float alpha;
		float posX;
		float width;

		public RenderPair(float posX, float width, float red, float green, float blue, float alpha) {
			this.posX = posX;
			this.width = width;
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
	}
}