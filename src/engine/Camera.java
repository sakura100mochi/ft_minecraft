package engine;

import utils.math.Frustum;
import utils.math.Matrix4f;
import data.Data;
import player.Player;
import settings.SystemSettings;
import settings.options.Options;
import settings.options.video_settings.VideoSettings;

public final class Camera {
	private final Window	window;
	private final Player	player;
	private float[]			position;
	private float[]			direction = new float[3];
	private double			fovRad;
	private float[]			projection;
	private float[]			perspectiveProjection = new float[16];
	private float[]			orthographicProjection = new float[16];
	private float[]			view = new float[16];
	private float			aspectRatio;
	private final float		minCameraDistance = 0.1f;
	private float			maxCameraDistance;
	private float[]			dst1 = new float[3];
	private float[]			dst2 = new float[3];
	private float[]			dst3 = new float[3];
	private final Frustum	frustum = new Frustum();
	private final int		min_y;
	private final int		terrainHeight;

	public Camera(Data data) throws Exception {
		if (data.window == null || data.player == null || data.worldgen == null) {
			throw new IllegalArgumentException("engine.Camera | window or player is null");
		}

		this.window = data.window;
		this.player = data.player;
		this.position = this.player.getCameraPos();
		this.min_y = data.parser.worldgen.overworld.min_y;
		this.terrainHeight = data.parser.worldgen.overworld.terrainHeight;
		if (VideoSettings.getPerspective() == VideoSettings.Perspective.THIRD_PERSON_FRONT) {
			direction[0] = -player.getDirection()[0];
			direction[1] = -player.getDirection()[1];
			direction[2] = -player.getDirection()[2];
		} else {
			direction[0] = player.getDirection()[0];
			direction[1] = player.getDirection()[1];
			direction[2] = player.getDirection()[2];
		}
		
		updateProjection();
		setProjection(perspectiveProjection);
	}
	
	public void update() throws Exception {
		if (VideoSettings.getPerspective() == VideoSettings.Perspective.THIRD_PERSON_FRONT) {
			direction[0] = -player.getDirection()[0];
			direction[1] = -player.getDirection()[1];
			direction[2] = -player.getDirection()[2];
		} else {
			direction[0] = player.getDirection()[0];
			direction[1] = player.getDirection()[1];
			direction[2] = player.getDirection()[2];
		}
		
		updateProjection();
		setProjection(this.perspectiveProjection);
		this.frustum.update(this.projection, this.view);
	}
	
	private void updateProjection() throws Exception {
		maxCameraDistance = (float)(VideoSettings.getRender_distance() * 2) * SystemSettings.CHUNK_SIZE + SystemSettings.CHUNK_SIZE;
		fovRad = Math.toRadians(Options.getFov_Deg());
		aspectRatio = (float)this.window.getFrameBuffer_Width()[0] / this.window.getFrameBuffer_Height()[0];
		Matrix4f.perspective((float)fovRad, aspectRatio, minCameraDistance, maxCameraDistance, this.perspectiveProjection);
		Matrix4f.orthographic(0f, this.window.getFrameBuffer_Width()[0], 0f, this.window.getFrameBuffer_Height()[0], -1f, 1f, this.orthographicProjection);
	}

	public void setProjection(float[] projection) throws Exception {
		this.projection = projection;
		Matrix4f.lookAt(position, position[0] + direction[0], position[1] + direction[1], position[2] + direction[2], 0f, 1f, 0f, this.view, dst1, dst2, dst3);
	}

	public float[] getProjection() {
		return projection;
	}

	public float[] getPerspectiveProjection() {
		return perspectiveProjection;
	}

	public float[] getOrthographicProjection() {
		return orthographicProjection;
	}

	public float[] getView() {
		return view;
	}

	public float[] getPosition() {
		return position;
	}

	public float[] getDirection() {
		return direction;
	}

	public boolean isChunkInViewFrustum(float chunk_x, float chunk_z) throws Exception {
		float min_x = chunk_x * SystemSettings.CHUNK_SIZE;
		float min_y = this.min_y;
		float min_z = chunk_z * SystemSettings.CHUNK_SIZE;
		float max_x = min_x + SystemSettings.CHUNK_SIZE;
		float max_y = this.min_y + this.terrainHeight;
		float max_z = min_z + SystemSettings.CHUNK_SIZE;
		return this.frustum.isAABBVisible(min_x, min_y, min_z, max_x, max_y, max_z);
	}
}