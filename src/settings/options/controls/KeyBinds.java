package settings.options.controls;

import org.lwjgl.glfw.GLFW;

public final class KeyBinds {
	private KeyBinds() {}

	// player can change
	private static int	Jump = GLFW.GLFW_KEY_SPACE;
	private static int	Sneak = GLFW.GLFW_KEY_LEFT_SHIFT;
	private static int	Sprint = GLFW.GLFW_KEY_Q;
	private static int	Strafe_Left = GLFW.GLFW_KEY_A;
	private static int	Strafe_Right = GLFW.GLFW_KEY_D;
	private static int	Walk_Backward = GLFW.GLFW_KEY_S;
	private static int	Walk_Forward = GLFW.GLFW_KEY_W;
	private static int	Change_Perspective = GLFW.GLFW_KEY_F5;
	private static int	Change_Game_Mode = GLFW.GLFW_KEY_F3;

	//getters
	public static int getJump() { return Jump; }
	public static int getSneak() { return Sneak; }
	public static int getSprint() { return Sprint; }
	public static int getStrafe_Left() { return Strafe_Left; }
	public static int getStrafe_Right() { return Strafe_Right; }
	public static int getWalk_Backward() { return Walk_Backward; }
	public static int getWalk_Forward() { return Walk_Forward; }
	public static int getChange_Perspective() { return Change_Perspective; }
	public static int getChange_Game_Mode() { return Change_Game_Mode; }

	//setters
	public static void setJump(int newJump) {
		if (isValidKey(newJump, Jump) == true) {
			Jump = newJump;
		}
	}
	public static void setSneak(int newSneak) {
		if (isValidKey(newSneak, Sneak) == true) {
			Sneak = newSneak;
		}
	}
	public static void setSprint(int newSprint) {
		if (isValidKey(newSprint, Sprint) == true) {
			Sprint = newSprint;
		}
	}
	public static void setStrafe_Left(int newStrafe_Left) {
		if (isValidKey(newStrafe_Left, Strafe_Left) == true) {
			Strafe_Left = newStrafe_Left;
		}
	}
	public static void setStrafe_Right(int newStrafe_Right) {
		if (isValidKey(newStrafe_Right, Strafe_Right) == true) {
			Strafe_Right = newStrafe_Right;
		}
	}
	public static void setWalk_Backward(int newWalk_Backward) {
		if (isValidKey(newWalk_Backward, Walk_Backward) == true) {
			Walk_Backward = newWalk_Backward;
		}
	}
	public static void setWalk_Forward(int newWalk_Forward) {
		if (isValidKey(newWalk_Forward, Walk_Forward) == true) {
			Walk_Forward = newWalk_Forward;
		}
	}
	public static void setChange_Perspective(int newChange_Perspective) {
		if (isValidKey(newChange_Perspective, Change_Perspective) == true) {
			Change_Perspective = newChange_Perspective;
		}
	}
	public static void setChange_Game_Mode(int newChange_Game_Mode) {
		if (isValidKey(newChange_Game_Mode, Change_Game_Mode) == true) {
			Change_Game_Mode = newChange_Game_Mode;
		}
	}

	// player can't change
	public static final int	EXIT = GLFW.GLFW_KEY_ESCAPE;

	//private functions
	private static boolean isValidKey(int key, int currentKey) {
		if (key < 0 && key > GLFW.GLFW_KEY_LAST)
			return false;
		if (key == EXIT)
			return false;
		if (key != currentKey &&
			(key == Jump || key == Sneak || key == Sprint || key == Strafe_Left || key == Strafe_Right || key == Walk_Backward || key == Walk_Forward))
			return false;
		return true;
	}
}