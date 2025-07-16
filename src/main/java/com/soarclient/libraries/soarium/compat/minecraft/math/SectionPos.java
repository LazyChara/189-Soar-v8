package com.soarclient.libraries.soarium.compat.minecraft.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraft.world.ChunkCoordIntPair;

public class SectionPos extends Vec3i {
	public static final int SECTION_BITS = 4;
	public static final int SECTION_SIZE = 16;
	public static final int SECTION_MASK = 15;
	public static final int SECTION_HALF_SIZE = 8;
	public static final int SECTION_MAX_INDEX = 15;

	SectionPos(int n, int n2, int n3) {
		super(n, n2, n3);
	}

	public static SectionPos of(int n, int n2, int n3) {
		return new SectionPos(n, n2, n3);
	}

	public static SectionPos of(BlockPos blockPos) {
		return new SectionPos(SectionPos.blockToSectionCoord(blockPos.getX()),
				SectionPos.blockToSectionCoord(blockPos.getY()), SectionPos.blockToSectionCoord(blockPos.getZ()));
	}

	public static SectionPos of(ChunkCoordIntPair chunkPos, int n) {
		return new SectionPos(chunkPos.chunkXPos, n, chunkPos.chunkZPos);
	}

	public static SectionPos of(long l) {
		return new SectionPos(SectionPos.x(l), SectionPos.y(l), SectionPos.z(l));
	}
    
	public static long offset(long l, EnumFacing direction) {
		return SectionPos.offset(l, direction.getFrontOffsetX(), direction.getFrontOffsetY(),
				direction.getFrontOffsetZ());
	}

	public static long offset(long l, int n, int n2, int n3) {
		return SectionPos.asLong(SectionPos.x(l) + n, SectionPos.y(l) + n2, SectionPos.z(l) + n3);
	}

	public static int posToSectionCoord(double d) {
		return SectionPos.blockToSectionCoord(MathHelper.floor_double(d));
	}

	public static int blockToSectionCoord(int n) {
		return n >> 4;
	}

	public static int sectionToBlockCoord(int n) {
		return n << 4;
	}

	public static int sectionToBlockCoord(int n, int n2) {
		return SectionPos.sectionToBlockCoord(n) + n2;
	}

	public static int x(long l) {
		return (int) (l << 0 >> 42);
	}

	public static int y(long l) {
		return (int) (l << 44 >> 44);
	}

	public static int z(long l) {
		return (int) (l << 22 >> 42);
	}

	public int x() {
		return this.getX();
	}

	public int y() {
		return this.getY();
	}

	public int z() {
		return this.getZ();
	}

	public int minBlockX() {
		return SectionPos.sectionToBlockCoord(this.x());
	}

	public int minBlockY() {
		return SectionPos.sectionToBlockCoord(this.y());
	}

	public int minBlockZ() {
		return SectionPos.sectionToBlockCoord(this.z());
	}

	public int maxBlockX() {
		return SectionPos.sectionToBlockCoord(this.x(), 15);
	}

	public int maxBlockY() {
		return SectionPos.sectionToBlockCoord(this.y(), 15);
	}

	public int maxBlockZ() {
		return SectionPos.sectionToBlockCoord(this.z(), 15);
	}

	public BlockPos origin() {
		return new BlockPos(SectionPos.sectionToBlockCoord(this.x()), SectionPos.sectionToBlockCoord(this.y()),
				SectionPos.sectionToBlockCoord(this.z()));
	}

	public ChunkCoordIntPair chunk() {
		return new ChunkCoordIntPair(this.x(), this.z());
	}

	public static long asLong(int n, int n2, int n3) {
		long l = 0L;
		l |= ((long) n & 0x3FFFFFL) << 42;
		l |= ((long) n2 & 0xFFFFFL) << 0;
		return l |= ((long) n3 & 0x3FFFFFL) << 20;
	}

	public long asLong() {
		return SectionPos.asLong(this.x(), this.y(), this.z());
	}
}
