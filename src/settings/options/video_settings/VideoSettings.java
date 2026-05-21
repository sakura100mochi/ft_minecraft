package settings.options.video_settings;

import data.Data;

public final class VideoSettings {
	private VideoSettings() {}

	// player can change
	private static int	Render_Distance = 12;

	public static int getRender_distance() {
		return Render_Distance;
	}

	public static void setRender_distance(int newRender_Distance) {
		if (newRender_Distance < 2) {
			Render_Distance = 2;
		} else if (newRender_Distance > 32) {
			Render_Distance = 32;
		} else {
			Render_Distance = newRender_Distance;
		}
	}

	private static int	Cloud_Distance = 24;

	public static int getCloud_Distance() {
		return Cloud_Distance;
	}

	public static void setCloud_Distance(int newCloud_Distance) {
		if (newCloud_Distance < 2) {
			Cloud_Distance = 2;
		} else if (newCloud_Distance > 128) {
			Cloud_Distance = 128;
		} else {
			Cloud_Distance = newCloud_Distance;
		}
	}

	public enum Perspective {
		FIRST_PERSON,
		THIRD_PERSON_BACK,
		THIRD_PERSON_FRONT
	}
	private static Perspective	perspective = Perspective.FIRST_PERSON;

	public static Perspective getPerspective() {
		return perspective;
	}

	public static void setPerspective(Perspective newPerspective) {
		perspective = newPerspective;
	}

	public static void setNextPerspective() {
		switch (perspective) {
			case FIRST_PERSON -> setPerspective(Perspective.THIRD_PERSON_BACK);
			case THIRD_PERSON_BACK -> setPerspective(Perspective.THIRD_PERSON_FRONT);
			case THIRD_PERSON_FRONT -> setPerspective(Perspective.FIRST_PERSON);
		}
	}

	private static int	GUI_Scale = 4;

	public static int getGUI_Scale() {
		return GUI_Scale;
	}

	public static void setGUI_Scale(int newGUI_Scale, Data data) throws Exception {
		int max = getGUI_ScaleMax(data);
		if (newGUI_Scale < 1) {
			GUI_Scale = 1;
		} else if (newGUI_Scale > max) {
			GUI_Scale = max;
		} else {
			GUI_Scale = newGUI_Scale;
		}
	}

	public static void setGUI_ScaleAuto(Data data) throws Exception {
		setGUI_Scale(getGUI_ScaleMax(data), data);
	}

	public static int getGUI_ScaleMax(Data data) throws Exception {
		if (data == null || data.window == null) {
			throw new IllegalArgumentException("settings.options.video_settings.VideoSettings.getGUI_ScaleMax | data or data.window is null");
		}
		int width = data.window.getFrameBuffer_Width()[0];
		int height = data.window.getFrameBuffer_Height()[0];

		if (width >= 3840 && height >= 2160) {
			return 6;
		} else if (width >= 2560 && height >= 1600) {
			return 5;
		} else if (width >= 2560 && height >= 1440) {
			return 4;
		} else if (width >= 1920 && height >= 1080) {
			return 3;
		} else {
			return 2;
		}
	}
}