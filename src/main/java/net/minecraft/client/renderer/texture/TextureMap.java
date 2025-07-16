package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.soarclient.libraries.soarium.Soarium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
	private static final Logger logger = LogManager.getLogger();
	public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
	public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
	private final List<TextureAtlasSprite> listAnimatedSprites;
	private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
	private final Map<String, TextureAtlasSprite> mapUploadedSprites;
	private final String basePath;
	private final IIconCreator iconCreator;
	private int mipmapLevels;
	private final TextureAtlasSprite missingImage;

	private final Map<Supplier<Boolean>, List<ResourceLocation>> animatedSprites = Map.of(
			() -> Soarium.getConfig().animationSettings.water,
			List.of(new ResourceLocation("minecraft", "block/water_still"),
					new ResourceLocation("minecraft", "block/water_flow")),
			() -> Soarium.getConfig().animationSettings.lava,
			List.of(new ResourceLocation("minecraft", "block/lava_still"),
					new ResourceLocation("minecraft", "block/lava_flow")),
			() -> Soarium.getConfig().animationSettings.portal,
			List.of(new ResourceLocation("minecraft", "block/nether_portal")),
			() -> Soarium.getConfig().animationSettings.fire,
			List.of(new ResourceLocation("minecraft", "block/fire_0"),
					new ResourceLocation("minecraft", "block/fire_1"),
					new ResourceLocation("minecraft", "block/soul_fire_0"),
					new ResourceLocation("minecraft", "block/soul_fire_1"),
					new ResourceLocation("minecraft", "block/campfire_fire"),
					new ResourceLocation("minecraft", "block/campfire_log_lit"),
					new ResourceLocation("minecraft", "block/soul_campfire_fire"),
					new ResourceLocation("minecraft", "block/soul_campfire_log_lit")),
			() -> Soarium.getConfig().animationSettings.blockAnimations,
			List.of(new ResourceLocation("minecraft", "block/magma"),
					new ResourceLocation("minecraft", "block/lantern"),
					new ResourceLocation("minecraft", "block/sea_lantern"),
					new ResourceLocation("minecraft", "block/soul_lantern"),
					new ResourceLocation("minecraft", "block/kelp"),
					new ResourceLocation("minecraft", "block/kelp_plant"),
					new ResourceLocation("minecraft", "block/seagrass"),
					new ResourceLocation("minecraft", "block/tall_seagrass_top"),
					new ResourceLocation("minecraft", "block/tall_seagrass_bottom"),
					new ResourceLocation("minecraft", "block/warped_stem"),
					new ResourceLocation("minecraft", "block/crimson_stem"),
					new ResourceLocation("minecraft", "block/blast_furnace_front_on"),
					new ResourceLocation("minecraft", "block/smoker_front_on"),
					new ResourceLocation("minecraft", "block/stonecutter_saw"),
					new ResourceLocation("minecraft", "block/prismarine"),
					new ResourceLocation("minecraft", "block/respawn_anchor_top"),
					new ResourceLocation("minecraft", "entity/conduit/wind"),
					new ResourceLocation("minecraft", "entity/conduit/wind_vertical")));

	public TextureMap(String p_i46099_1_) {
		this(p_i46099_1_, null);
	}

	public TextureMap(String p_i46100_1_, IIconCreator iconCreatorIn) {
		this.listAnimatedSprites = Lists.newArrayList();
		this.mapRegisteredSprites = Maps.newHashMap();
		this.mapUploadedSprites = Maps.newHashMap();
		this.missingImage = new TextureAtlasSprite("missingno");
		this.basePath = p_i46100_1_;
		this.iconCreator = iconCreatorIn;
	}

	private void initMissingImage() {
		int[] aint = TextureUtil.missingTextureData;
		this.missingImage.setIconWidth(16);
		this.missingImage.setIconHeight(16);
		int[][] aint1 = new int[this.mipmapLevels + 1][];
		aint1[0] = aint;
		this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] { aint1 }));
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		if (this.iconCreator != null) {
			this.loadSprites(resourceManager, this.iconCreator);
		}
	}

	public void loadSprites(IResourceManager resourceManager, IIconCreator p_174943_2_) {
		this.mapRegisteredSprites.clear();
		p_174943_2_.registerSprites(this);
		this.initMissingImage();
		this.deleteGlTexture();
		this.loadTextureAtlas(resourceManager);
	}

	public void loadTextureAtlas(IResourceManager resourceManager) {
		int i = Minecraft.getGLMaximumTextureSize();
		Stitcher stitcher = new Stitcher(i, i, true, 0, this.mipmapLevels);
		this.mapUploadedSprites.clear();
		this.listAnimatedSprites.clear();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.mipmapLevels;

		for (Entry<String, TextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
			TextureAtlasSprite textureatlassprite = entry.getValue();
			ResourceLocation resourcelocation = new ResourceLocation(textureatlassprite.getIconName());
			ResourceLocation resourcelocation1 = this.completeResourceLocation(resourcelocation, 0);

			try {
				IResource iresource = resourceManager.getResource(resourcelocation1);
				BufferedImage[] abufferedimage = new BufferedImage[1 + this.mipmapLevels];
				abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());
				TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");

				if (texturemetadatasection != null) {
					List<Integer> list = texturemetadatasection.getListMipmaps();

					if (!list.isEmpty()) {
						int l = abufferedimage[0].getWidth();
						int i1 = abufferedimage[0].getHeight();

						if (MathHelper.roundUpToPowerOfTwo(l) != l || MathHelper.roundUpToPowerOfTwo(i1) != i1) {
							throw new RuntimeException(
									"Unable to load extra miplevels, source-texture is not power of two");
						}
					}

					Iterator iterator = list.iterator();

					while (iterator.hasNext()) {
						int i2 = ((Integer) iterator.next()).intValue();

						if (i2 > 0 && i2 < abufferedimage.length - 1 && abufferedimage[i2] == null) {
							ResourceLocation resourcelocation2 = this.completeResourceLocation(resourcelocation, i2);

							try {
								abufferedimage[i2] = TextureUtil.readBufferedImage(
										resourceManager.getResource(resourcelocation2).getInputStream());
							} catch (IOException ioexception) {
								logger.error("Unable to load miplevel {} from: {}", Integer.valueOf(i2),
										resourcelocation2, ioexception);
							}
						}
					}
				}

				AnimationMetadataSection animationmetadatasection = iresource.getMetadata("animation");
				textureatlassprite.loadSprite(abufferedimage, animationmetadatasection);
			} catch (RuntimeException runtimeexception) {
				logger.error("Unable to parse metadata from " + resourcelocation1, runtimeexception);
				continue;
			} catch (IOException ioexception1) {
				logger.error("Using missing texture, unable to load " + resourcelocation1, ioexception1);
				continue;
			}

			j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
			int l1 = Math.min(Integer.lowestOneBit(textureatlassprite.getIconWidth()),
					Integer.lowestOneBit(textureatlassprite.getIconHeight()));

			if (l1 < k) {
				logger.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation1,
						Integer.valueOf(textureatlassprite.getIconWidth()),
						Integer.valueOf(textureatlassprite.getIconHeight()),
						Integer.valueOf(MathHelper.calculateLogBaseTwo(k)),
						Integer.valueOf(MathHelper.calculateLogBaseTwo(l1)));
				k = l1;
			}

			stitcher.addSprite(textureatlassprite);
		}

		int j1 = Math.min(j, k);
		int k1 = MathHelper.calculateLogBaseTwo(j1);

		if (k1 < this.mipmapLevels) {
			logger.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath,
					Integer.valueOf(this.mipmapLevels), Integer.valueOf(k1), Integer.valueOf(j1));
			this.mipmapLevels = k1;
		}

		for (final TextureAtlasSprite textureatlassprite1 : this.mapRegisteredSprites.values()) {
			try {
				textureatlassprite1.generateMipmaps(this.mipmapLevels);
			} catch (Throwable throwable1) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
				crashreportcategory.addCrashSectionCallable("Sprite name", new Callable<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getIconName();
					}
				});
				crashreportcategory.addCrashSectionCallable("Sprite size", new Callable<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getIconWidth() + " x " + textureatlassprite1.getIconHeight();
					}
				});
				crashreportcategory.addCrashSectionCallable("Sprite frames", new Callable<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getFrameCount() + " frames";
					}
				});
				crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
				throw new ReportedException(crashreport);
			}
		}

		this.missingImage.generateMipmaps(this.mipmapLevels);
		stitcher.addSprite(this.missingImage);

		try {
			stitcher.doStitch();
		} catch (StitcherException stitcherexception) {
			throw stitcherexception;
		}

		logger.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()),
				Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
		TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(),
				stitcher.getCurrentHeight());
		Map<String, TextureAtlasSprite> map = Maps.newHashMap(this.mapRegisteredSprites);

		for (TextureAtlasSprite textureatlassprite2 : stitcher.getStichSlots()) {
			String s = textureatlassprite2.getIconName();
			map.remove(s);
			this.mapUploadedSprites.put(s, textureatlassprite2);

			try {
				TextureUtil.uploadTextureMipmap(textureatlassprite2.getFrameTextureData(0),
						textureatlassprite2.getIconWidth(), textureatlassprite2.getIconHeight(),
						textureatlassprite2.getOriginX(), textureatlassprite2.getOriginY(), false, false);
			} catch (Throwable throwable) {
				CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
				CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Texture being stitched together");
				crashreportcategory1.addCrashSection("Atlas path", this.basePath);
				crashreportcategory1.addCrashSection("Sprite", textureatlassprite2);
				throw new ReportedException(crashreport1);
			}

			if (textureatlassprite2.hasAnimationMetadata()) {
				this.listAnimatedSprites.add(textureatlassprite2);
			}
		}

		for (TextureAtlasSprite textureatlassprite3 : map.values()) {
			textureatlassprite3.copyFrom(this.missingImage);
		}
	}

	private ResourceLocation completeResourceLocation(ResourceLocation location, int p_147634_2_) {
		return p_147634_2_ == 0
				? new ResourceLocation(location.getResourceDomain(),
						String.format("%s/%s%s", this.basePath, location.getResourcePath(), ".png"))
				: new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath,
						location.getResourcePath(), Integer.valueOf(p_147634_2_), ".png"));
	}

	public TextureAtlasSprite getAtlasSprite(String iconName) {
		TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

		if (textureatlassprite == null) {
			textureatlassprite = this.missingImage;
		}

		return textureatlassprite;
	}

	public void updateAnimations() {
		TextureUtil.bindTexture(this.getGlTextureId());

		for (TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
			if (Soarium.getConfig().animationSettings.animation
					&& this.shouldAnimate(new ResourceLocation(textureatlassprite.getIconName()))) {
				textureatlassprite.updateAnimation();
			}
		}
	}

	public TextureAtlasSprite registerSprite(ResourceLocation location) {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location.toString());

			if (textureatlassprite == null) {
				textureatlassprite = TextureAtlasSprite.makeAtlasSprite(location);
				this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
			}

			return textureatlassprite;
		}
	}

	public void tick() {
		this.updateAnimations();
	}

	public void setMipmapLevels(int mipmapLevelsIn) {
		this.mipmapLevels = mipmapLevelsIn;
	}

	public TextureAtlasSprite getMissingSprite() {
		return this.missingImage;
	}

	private boolean shouldAnimate(ResourceLocation identifier) {
		if (identifier != null) {
			for (Map.Entry<Supplier<Boolean>, List<ResourceLocation>> supplierListEntry : this.animatedSprites
					.entrySet()) {
				if (supplierListEntry.getValue().contains(identifier)) {
					return supplierListEntry.getKey().get();
				}
			}
		}
		return true;
	}
}
