package engine.shader;

import org.lwjgl.opengl.GL20;

import engine.shader.UBO.DynamicTransforms;
import engine.shader.UBO.Fog;
import engine.shader.UBO.Light;
import engine.shader.UBO.Projection;
import engine.shader.uniform.uniform2D.Sampler0;
import engine.shader.uniform.uniform2D.Sampler1;
import engine.shader.uniform.uniform2D.Sampler2;
import data.Data;

public final class EntityShader extends AShader implements IShader {
	private Light				light;
	private Fog					fog;
	private DynamicTransforms	dynamicTransforms;
	private Projection			projection;
	private Sampler0			sampler0;
	private Sampler1			sampler1;
	private Sampler2			sampler2;

	protected EntityShader(Data data, String vertexCode, String fragmentCode) throws Exception {
		super(data, vertexCode, fragmentCode);

		attachShaders();

		GL20.glBindAttribLocation(this.programId, 0, "Position");
		GL20.glBindAttribLocation(this.programId, 1, "Color");
		GL20.glBindAttribLocation(this.programId, 2, "UV0");
		GL20.glBindAttribLocation(this.programId, 3, "UV1");
		GL20.glBindAttribLocation(this.programId, 4, "UV2");
		GL20.glBindAttribLocation(this.programId, 5, "Normal");

		linkProgram();
		detachAndDeleteShaders();

		bind();
		setupUBO();
		setupUniforms();
		unbind();
	}

	private void setupUBO() throws Exception {
		this.light = new Light(this.programId, this.data);
		this.fog = new Fog(this.programId, this.data, false);
		this.dynamicTransforms = new DynamicTransforms(this.programId, this.data, false);
		this.projection = new Projection(this.programId, this.data);
	}

	private void setupUniforms() throws Exception {
		this.sampler0 = new Sampler0(this.programId, this.data.textureManager.entityAtlas.getId());
		this.sampler1 = new Sampler1(this.programId, this.data.textureManager.redDamageIndicatorOverlay.textureId);
		this.sampler2 = new Sampler2(this.programId, this.data.textureManager.lightMap.textureId);
	}

	@Override
	public void update() throws Exception {
		this.light.update();
		this.fog.update();
		this.projection.update();
		this.dynamicTransforms.update();

		this.sampler0.update();
		this.sampler1.update();
		this.sampler2.update();
	}

	@Override
	public void cleanup() {
		GL20.glDeleteProgram(this.programId);
		this.light.cleanup();
		this.fog.cleanup();
		this.projection.cleanup();
		this.dynamicTransforms.cleanup();
	}
}
