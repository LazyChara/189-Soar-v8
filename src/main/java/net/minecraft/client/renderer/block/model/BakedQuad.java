package net.minecraft.client.renderer.block.model;

import com.soarclient.libraries.soarium.model.quad.BakedQuadView;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFacing;
import com.soarclient.libraries.soarium.model.quad.properties.ModelQuadFlags;
import com.soarclient.libraries.soarium.util.ModelQuadUtil;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class BakedQuad implements BakedQuadView {
	/**
	 * Joined 4 vertex records, each has 7 fields (x, y, z, shadeColor, u, v,
	 * <unused>), see FaceBakery.storeVertexData()
	 */
	protected final int[] vertexData;
	protected final int tintIndex;
	protected final EnumFacing face;

	private int flags;
	private int normal;
	private ModelQuadFacing normalFace = null;

	public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn) {
		this.vertexData = vertexDataIn;
		this.tintIndex = tintIndexIn;
		this.face = faceIn;
		this.normal = this.calculateNormal();
		this.normalFace = ModelQuadFacing.fromPackedNormal(this.normal);

		this.flags = ModelQuadFlags.getQuadFlags(this, faceIn);
	}

	public int[] getVertexData() {
		return this.vertexData;
	}

	public boolean hasTintIndex() {
		return this.tintIndex != -1;
	}

	public EnumFacing getFace() {
		return this.face;
	}

	@Override
	public float getX(int idx) {
		return Float.intBitsToFloat(this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX]);
	}

	@Override
	public float getY(int idx) {
		return Float
				.intBitsToFloat(this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX + 1]);
	}

	@Override
	public float getZ(int idx) {
		return Float
				.intBitsToFloat(this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.POSITION_INDEX + 2]);
	}

	@Override
	public int getColor(int idx) {
		return this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.COLOR_INDEX];
	}

	@Override
	public int getVertexNormal(int idx) {
		return this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.NORMAL_INDEX];
	}

	@Override
	public int getLight(int idx) {
		return this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.LIGHT_INDEX];
	}

	@Override
	public TextureAtlasSprite getSprite() {
		if (this instanceof BreakingFour bq) {
			return bq.getTexture();
		}
		return null;
	}

	@Override
	public float getTexU(int idx) {
		return Float.intBitsToFloat(this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX]);
	}

	@Override
	public float getTexV(int idx) {
		return Float.intBitsToFloat(this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.TEXTURE_INDEX + 1]);
	}

	@Override
	public int getFlags() {
		return this.flags;
	}

	@Override
	public int getTintIndex() {
		return this.tintIndex;
	}

	@Override
	public ModelQuadFacing getNormalFace() {
		return this.normalFace;
	}

	@Override
	public int getFaceNormal() {
		return this.normal;
	}

	@Override
	public EnumFacing getLightFace() {
		return this.face;
	}

	@Override
	public int getMaxLightQuad(int idx) {
		return this.vertexData[ModelQuadUtil.vertexOffset(idx) + ModelQuadUtil.LIGHT_INDEX];
	}

	@Override
	public boolean hasShade() {
		return this.hasTintIndex();
	}

	@Override
	public boolean hasAO() {
		return true;
	}
}
