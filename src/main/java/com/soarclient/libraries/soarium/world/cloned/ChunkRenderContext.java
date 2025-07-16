package com.soarclient.libraries.soarium.world.cloned;

import com.soarclient.libraries.soarium.compat.minecraft.math.SectionPos;

import net.minecraft.world.gen.structure.StructureBoundingBox;

public record ChunkRenderContext(SectionPos origin, ClonedChunkSection[] sections, StructureBoundingBox volume) {
}
