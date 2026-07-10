package engine.shader;

import org.lwjgl.opengl.GL20;

import data.Data;
import engine.shader.UBO.DynamicTransforms;
import engine.shader.UBO.Projection;
import engine.shader.uniform.uniform2D.Sampler0;

public final class Position_tex_colorShader extends AShader implements IShader {
	private DynamicTransforms	dynamicTransforms;
	private Projection			projection;
	private Sampler0			sampler0;

	protected Position_tex_colorShader(Data data, String vertexCode, String fragmentCode) throws Exception {
		super(data, vertexCode, fragmentCode);

		attachShaders();

		GL20.glBindAttribLocation(this.programId, 0, "Position");
		GL20.glBindAttribLocation(this.programId, 1, "UV0");
		GL20.glBindAttribLocation(this.programId, 2, "Color");

		linkProgram();
		detachAndDeleteShaders();

		bind();
		setupUBO();
		setupUniforms();
		unbind();
	}
	
	private void setupUBO() throws Exception {
		this.dynamicTransforms = new DynamicTransforms(this.programId, this.data, true);
		this.projection = new Projection(this.programId, this.data);
	}

	private void setupUniforms() throws Exception {
		this.sampler0 = new Sampler0(this.programId, this.data.textureManager.guiAtlas.getId());
	}

	@Override
	public void update() throws Exception {
		this.dynamicTransforms.update();
		this.projection.update();

		this.sampler0.update();
	}

	@Override
	public void cleanup() {
		GL20.glDeleteProgram(this.programId);
		this.projection.cleanup();
		this.dynamicTransforms.cleanup();
	}
}
