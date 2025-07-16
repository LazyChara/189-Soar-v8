package com.soarclient.libraries.soarium.render.chunk;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

import com.soarclient.libraries.soarium.gl.attribute.GlVertexFormat;
import com.soarclient.libraries.soarium.gl.device.CommandList;
import com.soarclient.libraries.soarium.gl.device.RenderDevice;
import com.soarclient.libraries.soarium.gl.shader.*;
import com.soarclient.libraries.soarium.render.chunk.shader.*;
import com.soarclient.libraries.soarium.render.chunk.terrain.TerrainRenderPass;
import com.soarclient.libraries.soarium.render.chunk.vertex.format.ChunkVertexType;

public abstract class ShaderChunkRenderer implements ChunkRenderer {
	private final Map<ChunkShaderOptions, GlProgram<ChunkShaderInterface>> programs = new Object2ObjectOpenHashMap<>();

	protected final ChunkVertexType vertexType;
	protected final GlVertexFormat vertexFormat;

	protected final RenderDevice device;

	protected GlProgram<ChunkShaderInterface> activeProgram;

	public ShaderChunkRenderer(RenderDevice device, ChunkVertexType vertexType) {
		this.device = device;
		this.vertexType = vertexType;
		this.vertexFormat = vertexType.getVertexFormat();
	}

	protected GlProgram<ChunkShaderInterface> compileProgram(ChunkShaderOptions options) {
		GlProgram<ChunkShaderInterface> program = this.programs.get(options);

		if (program == null) {
			this.programs.put(options, program = this.createShader("blocks/block_layer_opaque", options));
		}

		return program;
	}

	private GlProgram<ChunkShaderInterface> createShader(String path, ChunkShaderOptions options) {
		ShaderConstants constants = options.constants();

		GlShader vertShader = ShaderLoader.loadShader(ShaderType.VERTEX, new ResourceLocation("soarium", path + ".vsh"),
				constants);

		GlShader fragShader = ShaderLoader.loadShader(ShaderType.FRAGMENT,
				new ResourceLocation("soarium", path + ".fsh"), constants);

		try {
			return GlProgram.builder(new ResourceLocation("soarium", "chunk_shader")).attachShader(vertShader)
					.attachShader(fragShader).bindAttribute("a_Position", ChunkShaderBindingPoints.ATTRIBUTE_POSITION)
					.bindAttribute("a_Color", ChunkShaderBindingPoints.ATTRIBUTE_COLOR)
					.bindAttribute("a_TexCoord", ChunkShaderBindingPoints.ATTRIBUTE_TEXTURE)
					.bindAttribute("a_LightAndData", ChunkShaderBindingPoints.ATTRIBUTE_LIGHT_MATERIAL_INDEX)
					.bindFragmentData("fragColor", ChunkShaderBindingPoints.FRAG_COLOR)
					.link((shader) -> new DefaultShaderInterface(shader, options));
		} finally {
			vertShader.delete();
			fragShader.delete();
		}
	}

	protected void begin(TerrainRenderPass pass) {
		pass.startDrawing();

		ChunkShaderOptions options = new ChunkShaderOptions(ChunkFogMode.SMOOTH, pass, this.vertexType);

		this.activeProgram = this.compileProgram(options);
		this.activeProgram.bind();
		this.activeProgram.getInterface().setupState();
	}

	protected void end(TerrainRenderPass pass) {
		this.activeProgram.getInterface().resetState();
		this.activeProgram.unbind();
		this.activeProgram = null;

		pass.endDrawing();
	}

	@Override
	public void delete(CommandList commandList) {
		this.programs.values().forEach(GlProgram::delete);
	}

}
