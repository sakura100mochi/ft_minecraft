package engine.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import data.Data;
import engine.shader.UBO.CloudInfo;
import engine.shader.UBO.DynamicTransforms;
import engine.shader.UBO.Fog;
import engine.shader.UBO.Projection;
import engine.shader.uniform.isamplerBuffer.CloudFaces;

public final class Rendertype_cloudsShader extends AShader implements IShader {
	private CloudInfo			cloudInfo;
	private DynamicTransforms	dynamicTransforms;
	private Fog					fog;
	private Projection			projection;
	private CloudFaces			cloudFaces;

	protected Rendertype_cloudsShader(Data data, String vertexCode, String fragmentCode) throws Exception {
		super(data, vertexCode, fragmentCode);

		attachShaders();

		linkProgram();
		detachAndDeleteShaders();

		bind();
		setupUBO();
		setupUniforms();
		unbind();
	}

	private void setupUBO() throws Exception {
		this.cloudInfo = new CloudInfo(this.programId, this.data);
		this.dynamicTransforms = new DynamicTransforms(this.programId, this.data, false);
		this.fog = new Fog(this.programId, this.data, false);
		this.projection = new Projection(this.programId, this.data);
	}

	private void setupUniforms() throws Exception {
		int textureId = GL11.glGenTextures();
		this.cloudFaces = new CloudFaces(this.programId, textureId, this.data);
	}

	@Override
	public void update() throws Exception {
		this.cloudInfo.update();
		this.projection.update();
		this.fog.update();
		this.dynamicTransforms.update();
		this.cloudFaces.update();
	}

	@Override
	public void cleanup() {
		GL20.glDeleteProgram(this.programId);
		this.cloudInfo.cleanup();
		this.projection.cleanup();
		this.dynamicTransforms.cleanup();
		this.fog.cleanup();
		this.cloudFaces.cleanup();
	}
}
