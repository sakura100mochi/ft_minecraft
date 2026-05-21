package engine.shader;

import org.lwjgl.opengl.GL20;

import data.Data;
import engine.shader.UBO.Fog;
import engine.shader.UBO.DynamicTransforms;
import engine.shader.UBO.Projection;
import engine.shader.uniform.uniform2D.Sampler0;
import engine.shader.uniform.uniform2D.Sampler2;

public final class Rendertype_textShader extends AShader implements IShader {
	private Fog					fog;
	private DynamicTransforms	dynamicTransforms;
	private Projection			projection;
	private Sampler0			sampler0;
	private Sampler2			sampler2;

	protected Rendertype_textShader(Data data, String vertexCode, String fragmentCode) throws Exception {
		super(data, vertexCode, fragmentCode);

		attachShaders();

		GL20.glBindAttribLocation(this.programId, 0, "Position");
		GL20.glBindAttribLocation(this.programId, 1, "Color");
		GL20.glBindAttribLocation(this.programId, 2, "UV0");
		GL20.glBindAttribLocation(this.programId, 3, "UV2");

		linkProgram();
		detachAndDeleteShaders();

		bind();
		setupUBO();
		setupUniforms();
		unbind();
	}
	
	private void setupUBO() throws Exception {
		this.fog = new Fog(this.programId, this.data, true);
		this.dynamicTransforms = new DynamicTransforms(this.programId, this.data, true);
		this.projection = new Projection(this.programId, this.data);
	}

	private void setupUniforms() throws Exception {
		this.sampler0 = new Sampler0(this.programId, this.data.textureManager.fontAtlas.getId());
		this.sampler2 = new Sampler2(this.programId, this.data.textureManager.lightMap.textureId);
	}

	@Override
	public void update() throws Exception {
		this.projection.update();
		this.dynamicTransforms.update();
		this.fog.update();

		this.sampler0.update();
		this.sampler2.update();
	}

	@Override
	public void cleanup() {
		GL20.glDeleteProgram(this.programId);
		this.fog.cleanup();
		this.dynamicTransforms.cleanup();
		this.projection.cleanup();
	}
}
