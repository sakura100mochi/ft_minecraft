package engine.shader;

public interface IShader {
	public void bind();
	public void unbind();
	public void update() throws Exception;
	public void cleanup();
}
