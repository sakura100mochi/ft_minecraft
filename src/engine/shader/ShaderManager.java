package engine.shader;

import data.Data;

public final class ShaderManager {
	public final IShader terrainShader;
	public final IShader rendertype_text;
	public final IShader skyShader;
	public final IShader position_colorShader;
	public final IShader guiShader;
	public final IShader entityShader;
	public final IShader rendertype_cloudsShader;

	public ShaderManager(Data data) throws Exception {
		if (data.parser == null || data.parser.shaders == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | data.parser or data.parser.shaders is null");
		}

		String terrain_vsh = data.parser.shaders.getCode("terrain.vsh");
		String terrain_fsh = data.parser.shaders.getCode("terrain.fsh");
		if (terrain_vsh == null || terrain_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | data.parser.shaders.terrain_vsh or terrain_fsh is null");
		} else {
			this.terrainShader = new TerrainShader(
				data,
				terrain_vsh,
				addALPHA_CUTOUT(terrain_fsh)
			);
		}

		String rendertype_text_vsh = data.parser.shaders.getCode("rendertype_text.vsh");
		String rendertype_text_fsh = data.parser.shaders.getCode("rendertype_text.fsh");
		if (rendertype_text_vsh == null || rendertype_text_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | rendertype_text_vsh or rendertype_text_fsh is null");
		} else {
			this.rendertype_text = new Rendertype_textShader(
				data,
				rendertype_text_vsh,
				rendertype_text_fsh
			);
		}

		String sky_vsh = data.parser.shaders.getCode("sky.vsh");
		String sky_fsh = data.parser.shaders.getCode("sky.fsh");
		if (sky_vsh == null || sky_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | sky_vsh or sky_fsh is null");
		} else {
			this.skyShader = new SkyShader(
				data,
				sky_vsh,
				sky_fsh
			);
		}

		String position_color_vsh = data.parser.shaders.getCode("position_color.vsh");
		String position_color_fsh = data.parser.shaders.getCode("position_color.fsh");
		if (position_color_vsh == null || position_color_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | position_color_vsh or position_color_fsh is null");
		} else {
			this.position_colorShader = new Position_colorShader(
				data,
				position_color_vsh,
				position_color_fsh
			);
		}

		String gui_vsh = data.parser.shaders.getCode("gui.vsh");
		String gui_fsh = data.parser.shaders.getCode("gui.fsh");
		if (gui_vsh == null || gui_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | gui_vsh or gui_fsh is null");
		} else {
			this.guiShader = new GuiShader(
				data,
				gui_vsh,
				gui_fsh
			);
		}

		String entity_vsh = data.parser.shaders.getCode("entity.vsh");
		String entity_fsh = data.parser.shaders.getCode("entity.fsh");
		if (entity_vsh == null || entity_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | entity_vsh or entity_fsh is null");
		} else {
			this.entityShader = new EntityShader(
				data,
				entity_vsh,
				entity_fsh
			);
		}

		String rendertype_clouds_vsh = data.parser.shaders.getCode("rendertype_clouds.vsh");
		String rendertype_clouds_fsh = data.parser.shaders.getCode("rendertype_clouds.fsh");
		if (rendertype_clouds_vsh == null || rendertype_clouds_fsh == null) {
			throw new IllegalArgumentException("engine.shader.ShaderManager | rendertype_clouds_vsh or rendertype_clouds_fsh is null");
		} else {
			this.rendertype_cloudsShader = new Rendertype_cloudsShader(
				data,
				rendertype_clouds_vsh,
				rendertype_clouds_fsh
			);
		}
	}

	public void cleanup() {
		terrainShader.cleanup();
		rendertype_text.cleanup();
		skyShader.cleanup();
		position_colorShader.cleanup();
		guiShader.cleanup();
		entityShader.cleanup();
		rendertype_cloudsShader.cleanup();
	}

	private String addALPHA_CUTOUT(String src) {
		int index = src.indexOf("\n");
		String firstLine = src.substring(0, index);
		String rest = src.substring(index);
		return firstLine + "\n#define ALPHA_CUTOUT 0.1\n" + rest;
	}
}