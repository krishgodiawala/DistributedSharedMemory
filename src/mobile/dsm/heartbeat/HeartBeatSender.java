package mobile.dsm.heartbeat;

import mobile.dsm.utils.Utility;

/**
 * This class sends heart beats every 5 seconds
 * 
 * @author Krish Godiawala
 *
 */
public class HeartBeatSender implements Runnable {
	Runtime runtime;
	public final String raspberryPieId;

	public HeartBeatSender(String raspberryPieID) {
		this.raspberryPieId = raspberryPieID;
	}

	@Override
	public void run() {
		sendHeartBeat();
	}

	private void sendHeartBeat() {
		while (true) {
			try {

				Thread.sleep(Utility.HEARTBEATTIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private HeartBeatObject creatObject() {
		runtime = Runtime.getRuntime();

		HeartBeatObject hbj;
		hbj = new HeartBeatObject(runtime.freeMemory() / Utility.MB,
				(runtime.totalMemory() - runtime.freeMemory()) / Utility.MB, runtime.totalMemory() / Utility.MB,
				raspberryPieId);
		return hbj;
	}
}
