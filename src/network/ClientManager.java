package network;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientManager {
	private final ConcurrentHashMap<UUID, ClientConnection>	connectionHashMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, float[][]>		gameState = new ConcurrentHashMap<>();

	public void addClient(UUID id, ClientConnection connection) {
		if (id == null || connection == null)
			throw new IllegalArgumentException("addClient: id or connection is null");

		this.connectionHashMap.put(id, connection);
		System.out.println("[Server] Client connected: " + id + " (online: " + this.connectionHashMap.size() + ")");
	}

	public void removeClient(UUID id) {
		if (this.connectionHashMap.remove(id) != null) {
			this.gameState.remove(id);
			sendUpdatesToClients();
			System.out.println("[Server] Client disconnected: " + id + " (online: " + this.connectionHashMap.size() + ")");
		}
	}

	public void updateAndBroadcast(UUID id, float x, float y, float z, float dirX, float dirY, float dirZ) {
		float[] position  = new float[] {x, y, z};
		float[] direction = new float[] {dirX, dirY, dirZ};
		float[][] state   = new float[][] {position, direction};
		this.gameState.put(id, state);
		sendUpdatesToClients();
	}

	private void sendUpdatesToClients() {
		int count = this.gameState.size();
		ByteBuffer buf = ByteBuffer.allocate(4 + count * 40);
		buf.putInt(count);
		for (Map.Entry<UUID, float[][]> entry : this.gameState.entrySet()) {
			UUID uuid     = entry.getKey();
			long uuidMsb  = uuid.getMostSignificantBits();
			long uuidLsb  = uuid.getLeastSignificantBits();
			float[] pos   = entry.getValue()[0];
			float[] dir   = entry.getValue()[1];
			buf.putLong(uuidMsb);
			buf.putLong(uuidLsb);
			buf.putFloat(pos[0]);
			buf.putFloat(pos[1]);
			buf.putFloat(pos[2]);
			buf.putFloat(dir[0]);
			buf.putFloat(dir[1]);
			buf.putFloat(dir[2]);
		}
		byte[] packet = buf.array();
		for (ClientConnection client : this.connectionHashMap.values())
			client.send(packet);
	}

	public ClientConnection getClient(UUID id) {
		return this.connectionHashMap.get(id);
	}

	public Collection<ClientConnection> getAll() {
		Collection<ClientConnection> values = this.connectionHashMap.values();
		return Collections.unmodifiableCollection(values);
	}

	public int getClientCount() {
		return this.connectionHashMap.size();
	}

	public void disconnectAll() {
		for (ClientConnection client : this.connectionHashMap.values())
			client.disconnect();
	}
}
