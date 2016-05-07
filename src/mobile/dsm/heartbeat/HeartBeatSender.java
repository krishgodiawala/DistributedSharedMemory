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

	public HeartBeatSender(String raspberryPieID, HeartBeatObject hbo) {
		this.raspberryPieId = raspberryPieID;
		this.hbo = hbo;
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
		if (!isRegistered) {
			System.out.println("registered");
			conn.writeObject(this.hbo);
			isRegistered = true;
		}
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

}
