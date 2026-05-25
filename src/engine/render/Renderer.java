package engine.render;

import org.lwjgl.opengl.GL11;

import data.Data;
import engine.Camera;
import engine.shader.ShaderManager;
import models.mesh.AllMeshes;

public final class Renderer {
	private final Camera			camera;
	private final ShaderManager		shaderManager;
	private final AllMeshes			allMeshes;

	public Renderer(Data data) throws Exception {
		if (data == null || data.camera == null || data.shaderManager == null || data.allMeshes == null
			|| data.allMeshes.terrainMesh == null || data.allMeshes.rendertype_textMesh == null) {
			throw new IllegalArgumentException("engine.render.Renderer | data, data.shaderManager or data.chunkManager is null");
		}

		this.camera = data.camera;
		this.shaderManager = data.shaderManager;
		this.allMeshes = data.allMeshes;

		GL11.glClearColor(0f, 0f, 0f, 1.0f);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CCW); 
	}

	public void update(float dt) throws Exception {
		render(dt);
	}

	private void render(float dt) throws Exception {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		this.camera.setProjection(this.camera.getPerspectiveProjection());
		
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);

		this.shaderManager.skyShader.bind();
		this.shaderManager.skyShader.update();
		this.allMeshes.skyMesh.render();
		this.shaderManager.skyShader.unbind();

		this.shaderManager.rendertype_cloudsShader.bind();
		this.shaderManager.rendertype_cloudsShader.update();
		this.allMeshes.rendertype_cloudsMesh.render();
		this.shaderManager.rendertype_cloudsShader.unbind();

		GL11.glEnable(GL11.GL_BLEND);

		this.shaderManager.terrainShader.bind();
		this.shaderManager.terrainShader.update();
		this.allMeshes.terrainMesh.render();
		this.allMeshes.terrainMesh.renderTransparency();
		this.shaderManager.terrainShader.unbind();

		GL11.glDisable(GL11.GL_CULL_FACE);

		this.shaderManager.entityShader.bind();
		this.shaderManager.entityShader.update();
		this.allMeshes.entityMesh.render();
		this.shaderManager.entityShader.unbind();

		this.shaderManager.terrainShader.bind();
		this.shaderManager.terrainShader.update();
		this.allMeshes.waterMesh.render();
		this.shaderManager.terrainShader.unbind();

		this.camera.setProjection(this.camera.getOrthographicProjection());

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		this.shaderManager.guiShader.bind();
		this.shaderManager.guiShader.update();
		this.allMeshes.guiMesh.render();
		this.shaderManager.guiShader.unbind();

		this.shaderManager.rendertype_text.bind();
		this.shaderManager.rendertype_text.update();
		this.allMeshes.rendertype_textMesh.render();
		this.shaderManager.rendertype_text.unbind();
	}
}