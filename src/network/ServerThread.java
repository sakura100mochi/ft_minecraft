package network;

import data.Data;
import settings.ServerSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public final class ServerThread extends Thread {
	private Data			data;
	private boolean			isRunning = false;
	private ServerSocket	serverSocket;

	public void run() {
		while (true) {
			try {
				if (checkIsRunning() == true) {
					update();
				} else {
					Thread.sleep(50);
				}
				if (Thread.interrupted()) {
					break;
				}
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				if (!isInterrupted()) {
					throw new RuntimeException("Exception in ServerThread: ", e);
				}
				break;
			}
		}
		closeServerSocket();
	}

	public void setData(Data data) {
		this.data = data;
	}

	private boolean checkIsRunning() throws Exception {
		if (isRunning == true)
			return true;
		else {
			if (this.data != null && this.data.clientManager != null) {
				ServerSocket serverSocket = new ServerSocket(ServerSettings.PORT);
				this.serverSocket = serverSocket;
				System.out.println("[Server] Listening on port " + ServerSettings.PORT);
				this.isRunning = true;
				return true;
			}
			return false;
		}
	}

	private void update() throws Exception {
		Socket clientSocket = this.serverSocket.accept();
		UUID clientId = UUID.randomUUID();
		ClientManager clientManager = this.data.clientManager;
		ClientConnection clientConnection = new ClientConnection(clientId, clientSocket, clientManager);
		clientManager.addClient(clientId, clientConnection);
		clientConnection.start();
	}

	@Override
	public void interrupt() {
		super.interrupt();
		closeServerSocket();
	}

	private void closeServerSocket() {
		if (this.serverSocket != null && !this.serverSocket.isClosed()) {
			try { 
				this.serverSocket.close(); 
			} catch (IOException ignored) {
			}
		}
	}
}
