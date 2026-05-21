package models.mesh;

import data.Data;
import models.mesh.rendertype_text.Rendertype_textMeshManager;
import models.mesh.sky.SkyMeshManager;
import models.mesh.terrain.TerrainMeshManager;
import models.mesh.terrain.fluid.WaterMeshManager;
import models.mesh.position_color.Position_colorMeshManager;
import models.mesh.gui.GuiMeshManager;
import models.mesh.entity.EntityMeshManager;
import models.mesh.rendertype_clouds.Rendertype_cloudsMeshManager;

public final class AllMeshes {
	public final Data			data;
	public final TerrainMeshManager			terrainMesh;
	public final Rendertype_textMeshManager	rendertype_textMesh;
	public final WaterMeshManager			waterMesh;
	public final SkyMeshManager				skyMesh;
	public final Position_colorMeshManager	position_colorMesh;
	public final GuiMeshManager				guiMesh;
	public final EntityMeshManager			entityMesh;
	public final Rendertype_cloudsMeshManager	rendertype_cloudsMesh;

	public AllMeshes(Data data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("models.mesh.AllMeshes | data is null");
		}
		this.data = data;
		this.terrainMesh = new TerrainMeshManager(data);
		this.rendertype_textMesh = new Rendertype_textMeshManager(data);
		this.waterMesh = new WaterMeshManager(data);
		this.skyMesh = new SkyMeshManager(data);
		this.position_colorMesh = new Position_colorMeshManager();
		this.guiMesh = new GuiMeshManager(data);
		this.entityMesh = new EntityMeshManager(data);
		this.rendertype_cloudsMesh = new Rendertype_cloudsMeshManager(data);
	}

	public void update() throws Exception {
		this.rendertype_textMesh.update();
		this.skyMesh.update();
		this.guiMesh.update();
		this.entityMesh.update();
		this.rendertype_cloudsMesh.update();
	}

	public void cleanup() {
		this.terrainMesh.cleanup();
		this.rendertype_textMesh.cleanup();
		this.waterMesh.cleanup();
		this.skyMesh.cleanup();
		this.position_colorMesh.cleanup();
		this.guiMesh.cleanup();
		this.entityMesh.cleanup();
		this.rendertype_cloudsMesh.cleanup();
	}
}
