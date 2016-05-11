package mobile.dsm.heartbeat;

import java.net.Socket;

import mobile.dsm.master.AvailableSlaves;
import mobile.dsm.network.TcpServerConnection;

/**
 * This class receives Heart Beats
 * 
 * @author Krish Godiawala
 * @author Vishwas Tantry
 * 
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
		String raspberryPie = this.conn.read();
		if (AvailableSlaves.allSlaves.containsKey(raspberryPie)) {
			conn.write("exists");
			// System.out.println("Update");
			AvailableSlaves.update(raspberryPie);
		} else {
			conn.write("notExists");
			HeartBeatObject hbo = (HeartBeatObject) this.conn.readObject();
			System.out.println(hbo);
			System.out.println("New Slave");
			AvailableSlaves.newSlave(hbo);
		}
		conn.close();
	}

}
