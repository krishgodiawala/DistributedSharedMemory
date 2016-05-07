package mobile.dsm.heartbeat;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mobile.dsm.network.Connectivity;
import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;
import mobile.dsm.utils.Utility;

/**
 * This class sends heart beats every 5 seconds
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class HeartBeatSender {
	Runtime runtime;
	public final String raspberryPieId;
	private ScheduledExecutorService execute;
	private TcpServerConnection conn;
	private HeartBeatObject hbo;
	private boolean isRegistered;

	public HeartBeatSender(String raspberryPieID) {
		this.raspberryPieId = raspberryPieID;
		this.hbo = this.creatObject(raspberryPieID);
	}

	/**
	 * This sends heartsBeat messages
	 */
	private Runnable heartbeatsend = new Runnable() {
		public void run() {
			sendHeartBeat();
		}
	};

	private void sendHeartBeat() {
		System.out.println(this.raspberryPieId + " Sending heartbeat");

		conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_HOSTNAME, HostName_Port.HEARTBEATMANAGER_PORT);
		conn.write(this.raspberryPieId);
		conn.writerConnection();
		// String read =
		if (conn.read().equals("notExists")) {
			// if (!isRegistered) {
			System.out.println("registered");
			conn.writeObject(this.hbo);
			isRegistered = true;
		}
		// }
		conn.close();
	}

	/**
	 * This method enables the executor service and begins to send heartbeat
	 * messages
	 */
	public void enable(ScheduledExecutorService executor) {
		if (executor != null) {
			this.execute = executor;
			execute.scheduleAtFixedRate(heartbeatsend, 0, Utility.HEARTBEATTIME, TimeUnit.SECONDS);
		}
	}

	private HeartBeatObject creatObject(String hostname) {
		Runtime runtime = Runtime.getRuntime();
		// long freeMoemory = runtime.freeMemory();
		// if (freeMoemory > 400) {
		// freeMoemory = 300;
		// } else {
		// freeMoemory = freeMoemory / Utility.MB;
		// }
		// System.out.println("Free " + freeMoemory);
		HeartBeatObject hbj;
		hbj = new HeartBeatObject(runtime.freeMemory(), (runtime.totalMemory() - runtime.freeMemory()) / Utility.MB,
				runtime.totalMemory() / Utility.MB, hostname);
		return hbj;
	}

}
