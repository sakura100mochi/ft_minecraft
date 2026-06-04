package network;

import data.Data;
import player.Player;
import settings.ServerSettings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ClientNetworkThread extends Thread {
	private Data				data;
	private boolean				isRunning = false;
	private Socket				socket;
	private DataInputStream		input;
	private DataOutputStream	output;
	private UUID				myId;

	public void run() {
		while (true) {
			try {
				if (checkIsRunning() == true) {
					update();
				} else {
					Thread.sleep(100);
				}
				if (Thread.interrupted()) {
					break;
				}
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				if (!isInterrupted()) {
					throw new RuntimeException("Exception in ClientNetworkThread: ", e);
				}
				break;
			}
		}
		closeSocket();
	}

	public void setData(Data data) {
		this.data = data;
	}

	private boolean checkIsRunning() throws Exception {
		if (isRunning == true)
			return true;
		else {
			if (this.data != null && this.data.remotePlayers != null && this.data.player != null) {
				try {
					Socket socket = new Socket(ServerSettings.CONNECT_HOST, ServerSettings.PORT);
					this.socket = socket;

					BufferedInputStream bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
					DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
					this.input = dataInputStream;

					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(this.socket.getOutputStream());
					DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
					this.output = dataOutputStream;

					long idMsb = this.input.readLong();
					long idLsb = this.input.readLong();
					this.myId = new UUID(idMsb, idLsb);
					System.out.println("[Client] Connected with ID: " + this.myId);

					Thread readerThread = new Thread(this::readLoop, "client-receiver");
					readerThread.setDaemon(true);
					readerThread.start();

					this.isRunning = true;
					return true;
				} catch (ConnectException e) {
					System.out.println("[Client] Waiting for server...");
					return false;
				}
			}
			return false;
		}
	}

	private void update() throws Exception {
		float[] pos = this.data.player.getPosition();
		float[] dir = this.data.player.getDirection();
		this.output.writeFloat(pos[0]);
		this.output.writeFloat(pos[1]);
		this.output.writeFloat(pos[2]);
		this.output.writeFloat(dir[0]);
		this.output.writeFloat(dir[1]);
		this.output.writeFloat(dir[2]);
		this.output.flush();
		Thread.sleep(100);
	}

	private void readLoop() {
		try {
			while (!this.socket.isClosed()) {
				int count = this.input.readInt();
				Set<UUID> activeIds = new HashSet<>();
				for (int i = 0; i < count; i++) {
					long idMsb    = this.input.readLong();
					long idLsb    = this.input.readLong();
					UUID remoteId = new UUID(idMsb, idLsb);
					float x    = this.input.readFloat();
					float y    = this.input.readFloat();
					float z    = this.input.readFloat();
					float dirX = this.input.readFloat();
					float dirY = this.input.readFloat();
					float dirZ = this.input.readFloat();
					if (remoteId.equals(this.myId))
						continue;
					activeIds.add(remoteId);
					Player remote = this.data.remotePlayers.computeIfAbsent(
						remoteId,
						id -> {
							try {
								return Player.createRemotePlayer(this.data);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					);
					remote.setPosition(x, y, z);
					remote.setDirection(dirX, dirY, dirZ);
				}
				this.data.remotePlayers.keySet().retainAll(activeIds);
			}
		} catch (IOException ignored) {
		} finally {
			this.data.remotePlayers.clear();
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		closeSocket();
	}

	private void closeSocket() {
		if (this.socket != null && !this.socket.isClosed()) {
			try { this.socket.close(); } catch (IOException ignored) {}
		}
	}
}
