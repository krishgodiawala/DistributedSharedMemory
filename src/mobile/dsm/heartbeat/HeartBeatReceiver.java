package mobile.dsm.heartbeat;

import java.net.Socket;

import mobile.dsm.master.AvailableSlaves;
import mobile.dsm.network.TcpServerConnection;

/**
 * This class receives Heart Beats
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class HeartBeatReceiver extends Thread {

	TcpServerConnection conn;

	public HeartBeatReceiver(Socket socket) {
		conn = new TcpServerConnection(socket);
		start();
	}

	@Override
	public void run() {
		HeartBeatObject hbo = (HeartBeatObject) this.conn.readObject();
		if (AvailableSlaves.allSlaves.containsKey(hbo.raspberryPieId)) {
			System.out.println("Update");
			AvailableSlaves.update(hbo);
		} else {
			System.out.println("New Slave");
			AvailableSlaves.newSlave(hbo);
		}
		conn.close();
	}

}
