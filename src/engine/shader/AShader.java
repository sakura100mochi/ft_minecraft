package engine.shader;

import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;

import data.Data;
import utils.math.Matrix4f;

public abstract class AShader {
	protected final Data					data;
	protected final int						programId;
	protected final int						vertexId;
	protected final int						fragmentId;
	protected final Map<String, Integer>	locationCache = new HashMap<>();

	protected AShader(Data data, String vertexSrc, String fragmentSrc) throws Exception {
		if (data == null || vertexSrc == null || fragmentSrc == null || data.textureManager == null || data.camera == null) {
			throw new IllegalArgumentException("engine.shader.Shader | Invalid argument");
		}

		this.data = data;
		this.programId = GL20.glCreateProgram();
		this.vertexId = Compile(GL20.GL_VERTEX_SHADER, vertexSrc);
		this.fragmentId = Compile(GL20.GL_FRAGMENT_SHADER, fragmentSrc);
	}
	
	protected void attachShaders() {
		GL20.glAttachShader(this.programId, this.vertexId);
		GL20.glAttachShader(this.programId, this.fragmentId);
	}
	
	protected void detachAndDeleteShaders() {
		GL20.glDetachShader(this.programId, this.vertexId);
		GL20.glDetachShader(this.programId, this.fragmentId);
		GL20.glDeleteShader(this.vertexId);
		GL20.glDeleteShader(this.fragmentId);
	}
	
	protected void linkProgram() {
		GL20.glLinkProgram(this.programId);
		if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("engine.shader : Failed to link shader " + GL20.glGetProgramInfoLog(this.programId));
		}
	}

	private int Compile(int type, String src) {
		int id = GL20.glCreateShader(type);
		GL20.glShaderSource(id, src);
		GL20.glCompileShader(id);
		if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("engine.shader : Failed to compile " + GL20.glGetShaderInfoLog(id));
		}

		return id;
	}

	public void bind() {
		GL20.glUseProgram(this.programId);
	}

	public void unbind() {
		GL20.glUseProgram(0);
	}

	public int getProgramID() {
		return this.programId;
	}

	public void setUniformInt(String name, int value) {
		GL20.glUniform1i(getLocationCache(name), value);
	}

	public void setUniformFloat(String name, float value) {
		GL20.glUniform1f(getLocationCache(name), value);
	}

	public void setUniformMatrix4f(String name, float[] matrix) {
		GL20.glUniformMatrix4fv(getLocationCache(name), false, Matrix4f.toFloatBuffer(matrix));
	}

	private int getLocationCache(String name) {
		if (this.locationCache.containsKey(name)) {
			return this.locationCache.get(name);
		}
		int location = GL20.glGetUniformLocation(this.programId, name);
		this.locationCache.put(name, location);
		return location;
	}
}
