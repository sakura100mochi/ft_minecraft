package models.mesh.rendertype_clouds;

import event.ITickEventListener;
import event.TickEvent;

public final class CloudOffsetChanger implements ITickEventListener {
	public final float[]	CloudOffset;

	public CloudOffsetChanger(float[] CloudOffset) {
		this.CloudOffset = CloudOffset;
	}

	@Override
	public void onOneTickPassed(TickEvent event) {
		this.CloudOffset[0] -= 0.05f;
	}
}
