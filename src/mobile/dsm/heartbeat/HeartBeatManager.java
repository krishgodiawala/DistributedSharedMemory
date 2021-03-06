package mobile.dsm.heartbeat;

import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mobile.dsm.master.AvailableSlaves;
import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.HostName_Port;
import mobile.dsm.utils.Utility;

/**
 * This class is a tracks the heart beats
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class HeartBeatManager implements Runnable {
	private ScheduledExecutorService execute;
	private TcpServerConnection conn;

	public HeartBeatManager() {
		conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_PORT);
	}

	private Runnable nodeAlive = new Runnable() {
		public void run() {
			System.out.println("Manager run called");
			checkisAlive();
		}
	};

	/**
	 * Checks if node is alive
	 */
	private void checkisAlive() {
		for (Entry<String, SlaveInformation> slaveInfo : AvailableSlaves.allSlaves.entrySet()) {
			System.out.println(System.currentTimeMillis() + " " + slaveInfo.getValue().timeStamp + " "
					+ (System.currentTimeMillis() - slaveInfo.getValue().timeStamp));
			if ((System.currentTimeMillis() - slaveInfo.getValue().timeStamp) > Utility.HEART_BEAT_DIFFERENCE) {
				System.err.println("NODE DOWN");
				AvailableSlaves.allSlaves.remove(slaveInfo.getKey());
				runBackUp();
			}
		}
	}

	/**
	 * Enables heart beat
	 * 
	 * @param executor
	 */
	public void enable(ScheduledExecutorService executor) {
		if (executor != null) {
			new Thread(this).start();
			this.execute = executor;
			execute.scheduleAtFixedRate(nodeAlive, 0, Utility.HEART_BEAT_CHECK, TimeUnit.SECONDS);
		}
	}

	/**
	 * Can be implemented
	 */
	private void runBackUp() {

	}

	@Override
	public void run() {
		while (true) {
			Socket socket = conn.createConnection();
			new Thread(new HeartBeatReceiver(socket));
		}
	}
}