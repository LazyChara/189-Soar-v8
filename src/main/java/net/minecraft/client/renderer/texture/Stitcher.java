package net.minecraft.client.renderer.texture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.soarclient.libraries.patches.textures.StbStitcher;
import com.soarclient.utils.tuples.Pair;

public class Stitcher {
	private final int mipmapLevelStitcher;
	private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
	private int currentWidth;
	private int currentHeight;

	/** Max size (width or height) of a single tile */
	private final int maxTileDimension;

	public Stitcher(int maxTextureWidth, int maxTextureHeight, boolean p_i45095_3_, int p_i45095_4_, int mipmapLevel) {
		this.mipmapLevelStitcher = mipmapLevel;
		this.maxTileDimension = p_i45095_4_;
	}

	public int getCurrentWidth() {
		return this.currentWidth;
	}

	public int getCurrentHeight() {
		return this.currentHeight;
	}

	public void addSprite(TextureAtlasSprite p_110934_1_) {
		Stitcher.Holder stitcher$holder = new Stitcher.Holder(p_110934_1_, this.mipmapLevelStitcher);

		if (this.maxTileDimension > 0) {
			stitcher$holder.setNewDimension(this.maxTileDimension);
		}

		this.setStitchHolders.add(stitcher$holder);
	}

	public void doStitch() {

		Stitcher.Holder[] astitcher$holder = this.setStitchHolders.toArray(new Holder[this.setStitchHolders.size()]);
		Arrays.sort(astitcher$holder);

		Pair<Integer, Integer> size = StbStitcher.packRects(astitcher$holder);
		this.currentWidth = size.getFirst();
		this.currentHeight = size.getSecond();
	}

	public List<TextureAtlasSprite> getStichSlots() {

		List<TextureAtlasSprite> arraylist = new ArrayList<>();

		for (Stitcher.Holder holder : (Set<Stitcher.Holder>) this.setStitchHolders) {
			arraylist.add(holder.getAtlasSprite());
		}

		return arraylist;
	}

	private static int getMipmapDimension(int p_147969_0_, int p_147969_1_) {
		return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
	}

	public static class Holder implements Comparable<Stitcher.Holder> {
		private final TextureAtlasSprite theTexture;
		private final int width;
		private final int height;
		private final int mipmapLevelHolder;
		private boolean rotated;
		private float scaleFactor = 1.0F;

		public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_) {
			this.theTexture = p_i45094_1_;
			this.width = p_i45094_1_.getIconWidth();
			this.height = p_i45094_1_.getIconHeight();
			this.mipmapLevelHolder = p_i45094_2_;
			this.rotated = Stitcher.getMipmapDimension(this.height, p_i45094_2_) > Stitcher
					.getMipmapDimension(this.width, p_i45094_2_);
		}

		public TextureAtlasSprite getAtlasSprite() {
			return this.theTexture;
		}

		public int getWidth() {
			return this.rotated
					? Stitcher.getMipmapDimension((int) ((float) this.height * this.scaleFactor),
							this.mipmapLevelHolder)
					: Stitcher.getMipmapDimension((int) ((float) this.width * this.scaleFactor),
							this.mipmapLevelHolder);
		}

		public int getHeight() {
			return this.rotated
					? Stitcher.getMipmapDimension((int) ((float) this.width * this.scaleFactor), this.mipmapLevelHolder)
					: Stitcher.getMipmapDimension((int) ((float) this.height * this.scaleFactor),
							this.mipmapLevelHolder);
		}

		public void rotate() {
			this.rotated = !this.rotated;
		}

		public boolean isRotated() {
			return this.rotated;
		}

		public void setNewDimension(int p_94196_1_) {
			if (this.width > p_94196_1_ && this.height > p_94196_1_) {
				this.scaleFactor = (float) p_94196_1_ / (float) Math.min(this.width, this.height);
			}
		}

		public String toString() {
			return "Holder{width=" + this.width + ", height=" + this.height + '}';
		}

		public int compareTo(Stitcher.Holder p_compareTo_1_) {
			int i;

			if (this.getHeight() == p_compareTo_1_.getHeight()) {
				if (this.getWidth() == p_compareTo_1_.getWidth()) {
					if (this.theTexture.getIconName() == null) {
						return p_compareTo_1_.theTexture.getIconName() == null ? 0 : -1;
					}

					return this.theTexture.getIconName().compareTo(p_compareTo_1_.theTexture.getIconName());
				}

				i = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
			} else {
				i = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
			}

			return i;
		}
	}

	public static class Slot {
		private final int originX;
		private final int originY;
		private final int width;
		private final int height;
		private List<Stitcher.Slot> subSlots;
		private Stitcher.Holder holder;

		public Slot(int p_i1277_1_, int p_i1277_2_, int widthIn, int heightIn) {
			this.originX = p_i1277_1_;
			this.originY = p_i1277_2_;
			this.width = widthIn;
			this.height = heightIn;
		}

		public Stitcher.Holder getStitchHolder() {
			return this.holder;
		}

		public int getOriginX() {
			return this.originX;
		}

		public int getOriginY() {
			return this.originY;
		}

		public boolean addSlot(Stitcher.Holder holderIn) {
			if (this.holder != null) {
				return false;
			} else {
				int i = holderIn.getWidth();
				int j = holderIn.getHeight();

				if (i <= this.width && j <= this.height) {
					if (i == this.width && j == this.height) {
						this.holder = holderIn;
						return true;
					} else {
						if (this.subSlots == null) {
							this.subSlots = Lists.newArrayListWithCapacity(1);
							this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
							int k = this.width - i;
							int l = this.height - j;

							if (l > 0 && k > 0) {
								int i1 = Math.max(this.height, k);
								int j1 = Math.max(this.width, l);

								if (i1 >= j1) {
									this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
									this.subSlots
											.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
								} else {
									this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
									this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
								}
							} else if (k == 0) {
								this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
							} else if (l == 0) {
								this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
							}
						}

						for (Stitcher.Slot stitcher$slot : this.subSlots) {
							if (stitcher$slot.addSlot(holderIn)) {
								return true;
							}
						}

						return false;
					}
				} else {
					return false;
				}
			}
		}

		public void getAllStitchSlots(List<Stitcher.Slot> p_94184_1_) {
			if (this.holder != null) {
				p_94184_1_.add(this);
			} else if (this.subSlots != null) {
				for (Stitcher.Slot stitcher$slot : this.subSlots) {
					stitcher$slot.getAllStitchSlots(p_94184_1_);
				}
			}
		}

		public String toString() {
			return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height="
					+ this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
		}
	}
}
