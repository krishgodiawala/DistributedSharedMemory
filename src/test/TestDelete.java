package test;

import java.io.IOException;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;

public class TestDelete {
	public static void main(String args[]) throws IOException {

		TcpServerConnection conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_HOSTNAME,
				HostName_Port.MASTERPORT);
		conn.write(new String("delete"));

		String objectName = new String("output.txt");
		conn.write(objectName);
		conn.write(new String("close"));
		conn.close();
	}
}