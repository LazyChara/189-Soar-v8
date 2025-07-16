package com.soarclient.libraries.soarium.render.chunk.compile.tasks;

import java.util.Map;

import org.joml.Vector3dc;

import com.soarclient.libraries.soarium.Soarium;
import com.soarclient.libraries.soarium.render.chunk.RenderSection;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildBuffers;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildContext;
import com.soarclient.libraries.soarium.render.chunk.compile.ChunkBuildOutput;
import com.soarclient.libraries.soarium.render.chunk.compile.executor.ChunkBuilder;
import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.BlockRenderCache;
import com.soarclient.libraries.soarium.render.chunk.compile.pipeline.BlockRenderContext;
import com.soarclient.libraries.soarium.render.chunk.data.BuiltSectionInfo;
import com.soarclient.libraries.soarium.render.chunk.data.BuiltSectionMeshParts;
import com.soarclient.libraries.soarium.render.chunk.terrain.DefaultTerrainRenderPasses;
import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.SortBehavior;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.SortType;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.PresentTranslucentData;
import com.soarclient.libraries.soarium.render.chunk.translucent_sorting.data.TranslucentData;
import com.soarclient.libraries.soarium.util.BlockRenderType;
import com.soarclient.libraries.soarium.util.task.CancellationToken;
import com.soarclient.libraries.soarium.world.LevelSlice;
import com.soarclient.libraries.soarium.world.cloned.ChunkRenderContext;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;

/**
 * Rebuilds all the meshes of a chunk for each given render pass with
 * non-occluded blocks. The result is then uploaded to graphics memory on the
 * main thread.
 * <p>
 * This task takes a slice of the level from the thread it is created on. Since
 * these slices require rather large array allocations, they are pooled to
 * ensure that the garbage collector doesn't become overloaded.
 */
public class ChunkBuilderMeshingTask extends ChunkBuilderTask<ChunkBuildOutput> {
	private final ChunkRenderContext renderContext;

	public ChunkBuilderMeshingTask(RenderSection render, int buildTime, Vector3dc absoluteCameraPos,
			ChunkRenderContext renderContext) {
		super(render, buildTime, absoluteCameraPos);
		this.renderContext = renderContext;
	}

	@Override
	public ChunkBuildOutput execute(ChunkBuildContext buildContext, CancellationToken cancellationToken) {
		Profiler profiler = Minecraft.getMinecraft().mcProfiler;
		BuiltSectionInfo.Builder renderData = new BuiltSectionInfo.Builder();
		VisGraph occluder = new VisGraph();

		ChunkBuildBuffers buffers = buildContext.buffers;
		buffers.init(renderData, this.render.getSectionIndex());

		BlockRenderCache cache = buildContext.cache;
		cache.init(this.renderContext);

		LevelSlice slice = cache.getWorldSlice();

		int minX = this.render.getOriginX();
		int minY = this.render.getOriginY();
		int minZ = this.render.getOriginZ();

		int maxX = minX + 16;
		int maxY = minY + 16;
		int maxZ = minZ + 16;

		// Initialise with minX/minY/minZ so initial getBlockState crash context is
		// correct
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(minX, minY, minZ);
		BlockPos.MutableBlockPos modelOffset = new BlockPos.MutableBlockPos();

		TranslucentGeometryCollector collector = null;
		if (Soarium.getConfig().performance.getSortBehavior() != SortBehavior.OFF) {
			collector = new TranslucentGeometryCollector(render.getPosition());
		}
		BlockRenderContext context = new BlockRenderContext(slice, collector);

		profiler.startSection("render blocks");
		try {
			for (int y = minY; y < maxY; y++) {
				if (cancellationToken.isCancelled()) {
					return null;
				}

				for (int z = minZ; z < maxZ; z++) {
					for (int x = minX; x < maxX; x++) {
						blockPos.set(x, y, z);

						var blockState = slice.getBlockState(blockPos);
						var block = blockState.getBlock();
						var blockType = block.getRenderType();

						if (BlockRenderType.isInvisible(blockType) && block.hasTileEntity()) {
							continue;
						}

						blockState = block.getActualState(blockState, slice, blockPos);

						modelOffset.set(x & 15, y & 15, z & 15);

						if (BlockRenderType.isModel(blockType)) {
							IBakedModel model = cache.getBlockModels().getModelForState(blockState);

							context.update(blockPos, modelOffset, blockState, model);
							cache.getBlockRenderer().renderModel(context, buffers);
						}

						if (BlockRenderType.isLiquid(blockType)) {
							cache.getFluidRenderer().render(slice, blockState, blockState, blockPos, modelOffset,
									collector, buffers);
						}

						if (block.hasTileEntity()) {
						    TileEntity entity = slice.getTileEntity(blockPos);
						    TileEntitySpecialRenderer<TileEntity> renderer = null;
						    
						    if (entity != null) {
						        renderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(entity);

						        if (renderer != null) {
						            entity.setPos(new BlockPos(x, y, z));
						            renderData.addBlockEntity(entity, false);
						        }
						    }
						}

						if (block.isOpaqueCube()) {
							occluder.func_178606_a(blockPos);
						}
					}
				}
			}
		} catch (ReportedException ex) {
			// Propagate existing crashes (add context)
			throw fillCrashInfo(ex.getCrashReport(), slice, blockPos);
		} catch (Exception ex) {
			// Create a new crash report for other exceptions (e.g. thrown in getQuads)
			throw fillCrashInfo(CrashReport.makeCrashReport(ex, "Encountered exception while building chunk meshes"),
					slice, blockPos);
		}
		profiler.endStartSection("mesh appenders");

		SortType sortType = SortType.NONE;
		if (collector != null) {
			sortType = collector.finishRendering();
		}

		Map<TerrainRenderPass, BuiltSectionMeshParts> meshes = new Reference2ReferenceOpenHashMap<>();
		profiler.endStartSection("meshing");

		for (TerrainRenderPass pass : DefaultTerrainRenderPasses.ALL) {
			// consolidate all translucent geometry into UNASSIGNED so that it's rendered
			// all together if it needs to share an index buffer between the directions
			BuiltSectionMeshParts mesh = buffers.createMesh(pass,
					pass.isTranslucent() && sortType.needsDirectionMixing);

			if (mesh != null) {
				meshes.put(pass, mesh);
				renderData.addRenderPass(pass);
			}
		}

		// cancellation opportunity right before translucent sorting
		if (cancellationToken.isCancelled()) {
			meshes.forEach((pass, mesh) -> mesh.getVertexData().free());
			profiler.endSection();
			return null;
		}

		renderData.setOcclusionData(occluder.computeVisibility());

		profiler.endStartSection("translucency sorting");

		boolean reuseUploadedData = false;
		TranslucentData translucentData = null;
		if (collector != null) {
			var oldData = this.render.getTranslucentData();
			translucentData = collector.getTranslucentData(oldData, meshes.get(DefaultTerrainRenderPasses.TRANSLUCENT),
					this);
			reuseUploadedData = translucentData == oldData;
		}

		var output = new ChunkBuildOutput(this.render, this.submitTime, translucentData, renderData.build(), meshes);

		if (collector != null) {
			if (reuseUploadedData) {
				output.markAsReusingUploadedData();
			} else if (translucentData instanceof PresentTranslucentData present) {
				var sorter = present.getSorter();
				sorter.writeIndexBuffer(this, true);
				output.copyResultFrom(sorter);
			}
		}

		profiler.endSection();

		return output;
	}

	private ReportedException fillCrashInfo(CrashReport report, LevelSlice slice, BlockPos pos) {
		CrashReportCategory crashReportSection = report.makeCategoryDepth("Block being rendered", 1);

		crashReportSection.addCrashSection("Chunk section", this.render);
		if (this.renderContext != null) {
			crashReportSection.addCrashSection("Render context volume", this.renderContext.volume());
		}

		return new ReportedException(report);
	}

	@Override
	public int getEffort() {
		return ChunkBuilder.HIGH_EFFORT;
	}
}