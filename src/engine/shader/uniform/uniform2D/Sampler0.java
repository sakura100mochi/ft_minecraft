package engine.shader.uniform.uniform2D;

import org.lwjgl.opengl.GL20;

import engine.shader.uniform.Uniform;
import texture.TextureLoader;

public final class Sampler0 extends Uniform {
	private final int	textureId;

	public Sampler0(int shaderId, int id) throws Exception {
		super(shaderId, "Sampler0");

		this.textureId = id;
	}

	@Override
	public void update() {
		TextureLoader.bindSampler2D(this.textureId, 0);		
		GL20.glUniform1i(this.location, 0);
	}
}