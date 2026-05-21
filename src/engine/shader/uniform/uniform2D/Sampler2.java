package engine.shader.uniform.uniform2D;

import org.lwjgl.opengl.GL20;

import engine.shader.uniform.Uniform;
import texture.TextureLoader;

public final class Sampler2 extends Uniform {
	private final int	textureId;

	public Sampler2(int shaderId, int id) throws Exception {
		super(shaderId, "Sampler2");

		this.textureId = id;
	}

	@Override
	public void update() {
		TextureLoader.bindSampler2D(this.textureId, 2);		
		GL20.glUniform1i(this.location, 2);
	}
}