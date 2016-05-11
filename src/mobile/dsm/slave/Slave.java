package mobile.dsm.slave;

import java.util.HashMap;
import java.util.Map;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;

/**
 * Each slave Instance
 * @author krishgodiawala
 * @author Vishwas Tantry
 *
 */
public class Slave extends Thread {
	TcpServerConnection conn;
	static Map<String, byte[]> files = new HashMap<String, byte[]>();
	static EWMA ewma;

	public Slave() {
		ewma = new EWMA(0.7f);
	}

	@Override
	public void run() {
		conn = new TcpServerConnection(HostName_Port.SLAVE_SERVER_CONN_PORT);
		while (true) {
			conn.createConnection();
			new Thread(new SlaveCommunicate(conn.getSocket())).start();
		}
	}

}