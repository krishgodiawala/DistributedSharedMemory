package test;
import java.io.File;
import java.io.IOException;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;

/**
 * This class tests a sample put file into the DSM
 * @author krishgodiawala
 * @author Vishwas Tantry
 *
 */
public class TestClient {

	public static void main(String args[]) throws IOException {

		TcpServerConnection conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_HOSTNAME,
				HostName_Port.MASTERPORT);
		conn.write(new String("put"));

		File file = new File("output.txt");

		Long fileLength = file.length();
		conn.write(new String(file.getName()));
		conn.writeFile(file);
		if (!file.exists()) {
			System.out.println("hi");
		}
		conn.write(new String("close"));
		conn.close();
	}
}
