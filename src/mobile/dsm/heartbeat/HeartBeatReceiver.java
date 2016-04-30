package mobile.dsm.heartbeat;

import java.util.concurrent.ExecutorService;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;

/**
 * This class receives Heart Beats
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class HeartBeatReceiver implements Runnable {

	private ExecutorService execute;
	private TcpServerConnection conn;

	public HeartBeatReceiver() {
		conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_PORT);
	}

	@Override
	public void run() {
		HeartBeatObject hbo = (HeartBeatObject) this.conn.readObject();
		
		
	}

	/**
	 * Enables the thread executor service
	 * 
	 * @param executor
	 */
	public void enable(ExecutorService executor) {
		if (execute == null) {
			this.execute = executor;
			this.execute.execute(this);
		}
	}

}
