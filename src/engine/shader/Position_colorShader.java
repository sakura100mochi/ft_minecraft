package engine.shader;

import org.lwjgl.opengl.GL20;

import data.Data;
import engine.shader.UBO.DynamicTransforms;
import engine.shader.UBO.Projection;

public final class Position_colorShader extends AShader implements IShader {
	private DynamicTransforms	dynamicTransforms;
	private Projection			projection;

	protected Position_colorShader(Data data, String vertexCode, String fragmentCode) throws Exception {
		super(data, vertexCode, fragmentCode);

		attachShaders();

		GL20.glBindAttribLocation(this.programId, 0, "Position");
		GL20.glBindAttribLocation(this.programId, 1, "Color");

		linkProgram();
		detachAndDeleteShaders();

		bind();
		setupUBO();
		unbind();
	}

	private void setupUBO() throws Exception {
		this.dynamicTransforms = new DynamicTransforms(this.programId, this.data, false);
		this.projection = new Projection(this.programId, this.data);
	}

	@Override
	public void update() throws Exception {
		this.projection.update();
		this.dynamicTransforms.update();
	}

	@Override
	public void cleanup() {
		GL20.glDeleteProgram(this.programId);
		this.projection.cleanup();
		this.dynamicTransforms.cleanup();
	}
}
