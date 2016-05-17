package test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;

/**
 * This class tests backup
 * 
 * @author Vishwas Tantry
 * @author krishgodiawala
 *
 */
public class TestBackUp {

	public TestBackUp() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {
		TcpServerConnection conn = new TcpServerConnection(HostName_Port.HEARTBEATMANAGER_HOSTNAME,
				HostName_Port.MASTERPORT);
		conn.write(new String("backupget"));

		// File file = new File("main.txt");

		conn.write("Readme.txt");
		int size = Integer.parseInt(conn.read());
		byte b[] = new byte[size];
		Socket soc = conn.getSocket();
		try {
			File file = new File("Works2.txt");
			FileOutputStream fileoutput = new FileOutputStream(file);
			DataInputStream is = new DataInputStream(soc.getInputStream());
			System.out.println("size " + size);
			is.readFully(b, 0, b.length);
			fileoutput.write(b, 0, b.length);

			fileoutput.flush();
			System.out.println("Complete??? ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.write(new String("close"));
		conn.close();

	}
}
