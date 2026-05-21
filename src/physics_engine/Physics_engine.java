package physics_engine;

import data.Data;
import physics_engine.collision.Collision;
import physics_engine.entity_motion.player.PlayerMotion;

public final class Physics_engine {
	private final Data			data;
	public final Collision		collision;
	public final PlayerMotion	playerMotion;

	public Physics_engine(Data data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("physics_engine.Physics_engine | data is null");
		}
		
		this.data = data;
		this.collision = new Collision(data);
		this.playerMotion = new PlayerMotion(data, this);

		this.data.event.tickEventProducer.addListener(this.playerMotion);
	}
}
