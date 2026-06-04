package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ClientConnection {
	public	final UUID				id;
	private final Socket			socket;
	private final DataInputStream	input;
	private final DataOutputStream	output;
	private final Thread			readerThread;
	private final AtomicBoolean		connected = new AtomicBoolean(true);
	private final ClientManager		clientManager;

	public ClientConnection(UUID id, Socket socket, ClientManager clientManager) throws IOException {
		if (id == null || socket == null || clientManager == null) {
			throw new IllegalArgumentException("network.ClientConnection | id, socket or clientManager is null");
		}

		this.id = id;
		this.socket = socket;

		BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
		DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
		this.input = dataInputStream;

		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
		DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
		this.output = dataOutputStream;

		this.clientManager = clientManager;

		Thread readerThread = new Thread(this::readLoop, "client #" + id);
		readerThread.setDaemon(true);
		this.readerThread = readerThread;
	}

	public void start() {
		this.readerThread.start();
	}

	private void readLoop() {
		try {
			long idMsb = this.id.getMostSignificantBits();
			long idLsb = this.id.getLeastSignificantBits();
			this.output.writeLong(idMsb);
			this.output.writeLong(idLsb);
			this.output.flush();
			while (!this.socket.isClosed()) {
				float x    = this.input.readFloat();
				float y    = this.input.readFloat();
				float z    = this.input.readFloat();
				float dirX = this.input.readFloat();
				float dirY = this.input.readFloat();
				float dirZ = this.input.readFloat();
				this.clientManager.updateAndBroadcast(this.id, x, y, z, dirX, dirY, dirZ);
			}
		} catch (IOException ignored) {
		} finally {
			disconnect();
		}
	}

	public synchronized void send(byte[] data) {
		if (!this.connected.get())
			return;

		try {
			this.output.write(data);
			this.output.flush();
		} catch (IOException e) {
			disconnect();
		}
	}

	public void disconnect() {
		if (!this.connected.compareAndSet(true, false))
			return;

		try {
			this.socket.close();
		} catch (IOException ignored) {}

		this.clientManager.removeClient(this.id);
	}

	public boolean isConnected() {
		return this.connected.get();
	}
}
