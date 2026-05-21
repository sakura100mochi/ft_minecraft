package engine.shader.uniform;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	protected final int	shaderId;
	protected final int	location;

	protected Uniform(int shaderId, String uniformName) {
		this.shaderId = shaderId;
		this.location = GL20.glGetUniformLocation(shaderId, uniformName);
	}

	public abstract void update();
}
