package sounds;

import data.Data;
import utils.registry.Registry;

public final class PlayerStepSounds {
	private final Data		data;
	private final int		grassId;
	private final Sound[]	grass;
	private int				current_grass_sound_index = 0;

	protected PlayerStepSounds(Data data, SoundsManager soundsManager) throws Exception {
		this.data = data;
		this.grassId = Registry.getId("minecraft:grass_block");
		this.grass = new Sound[] {
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass1.ogg"),
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass2.ogg"),
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass3.ogg"),
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass4.ogg"),
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass5.ogg"),
			soundsManager.getSound("1.21.11/assets/minecraft/sounds/step/grass6.ogg"),
		};
	}

	public void play() {
		int id = this.data.player.getBelowBlockId();
		if (id == this.grassId) {
			this.grass[this.current_grass_sound_index].stop();
			this.current_grass_sound_index++;
			if (this.current_grass_sound_index >= this.grass.length) {
				this.current_grass_sound_index = 0;
			}
			this.grass[this.current_grass_sound_index].play();
		}
	}
}
