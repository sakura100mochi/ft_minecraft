package thread.game_process;

import data.Data;

public final class GameProcessThread extends Thread {
	private Data	data;
	private boolean isRunning = false;

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
				throw new RuntimeException("Exception in GameProcessThread: ", e);
			}
		}
	}

	public void setData(Data data) {
		this.data = data;
	}

	private boolean checkIsRunning() throws Exception {
		if (isRunning == true)
			return true;
		else {
			if (this.data != null && this.data.tick != null)
			{
				this.isRunning = true;
				return true;
			}
			return false;
		}
	}

	private void update() throws Exception {
		this.data.tick.update();
	}
}
