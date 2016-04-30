package mobile.dsm.slave;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;
import mobile.dsm.utils.Utility;

public class Slave implements Runnable {
	TcpServerConnection conn;

	public Slave() {
		conn = new TcpServerConnection(HostName_Port.SLAVE_SERVER_CONN_PORT);
	}

	@Override
	public void run() {
		while (true) {

			conn.writerConnection();
			Runtime runtime = Runtime.getRuntime();
			conn.write(String.valueOf(runtime.freeMemory() / Utility.MB));
			if (conn.read().equalsIgnoreCase("yes")) {
				
			} else
				continue;
		}
	}

}
