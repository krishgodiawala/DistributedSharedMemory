import java.net.Socket;

import mobile.dsm.network.TcpServerConnection;

public class DsmClientThread extends Thread {
	TcpServerConnection server_con;
	Socket m;

	DsmClientThread(int port) {

		server_con = new TcpServerConnection(port);

	}

	public void run() {
		while (true) {

		}

	}

}
