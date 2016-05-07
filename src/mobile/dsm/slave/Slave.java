package mobile.dsm.slave;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.rmi.CORBA.Util;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;
import mobile.dsm.utils.Utility;
import mobile.dsm.utils.WriteToDisk;

public class Slave implements Runnable {
	TcpServerConnection conn;
	Map<String, byte[]> files;

	public Slave() {
		files = new HashMap<String, byte[]>();
		// files = new HashSet<File>();

	}

	@Override
	public void run() {
		System.out.println(getMemory());
		while (true) {
			conn = new TcpServerConnection(HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.writerConnection();

			long initMemory = getMemory();
			if (conn.read().equalsIgnoreCase("get")) {
				String fileName = conn.read();
				if (this.files.containsKey(fileName))
					conn.writeObject(this.files.get(fileName));
				conn.write(Long.toString(getMemory()));
			} else if (conn.read().equalsIgnoreCase("put")) {
				// conn.write(String.valueOf(runtime.freeMemory() / Utility.MB -
				// Utility.MAX_SLAVE_BACKUP_SPACE));
				// if (conn.read().equalsIgnoreCase("yes")) {
				if (conn.read().equalsIgnoreCase("main")) {
					String fileName = conn.read();
					// File fileName = (File) conn.readObject();
					this.files.put(fileName, conn.readFile());
				} else if (conn.read().equalsIgnoreCase("backup")) {
					// String m[] = conn.read().split("||");
					String filename = conn.read();
					int length = Integer.parseInt(conn.read());
					int chunkSize = Integer.parseInt(conn.read());
					try {
						WriteToDisk.write(filename, conn, length, chunkSize);
					} catch (NumberFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				conn.write(Long.toString(getMemory()));
				// } else
				// continue;
			} else if (conn.read().equals("delete")) {
				String fileName = conn.read();
				WriteToDisk.deleteFile(fileName);

				long newMemory = getMemory();
				// test
				while (Math.abs(newMemory - initMemory) <= 10) {
					newMemory = getMemory();
				}
				conn.write(Long.toString(newMemory));

			} else if (conn.read().equalsIgnoreCase("deletechunk")) {
				String fileName = conn.read();
				if (this.files.containsKey(fileName))
					this.files.remove(fileName);
				System.gc();
				conn.write(Long.toString(getMemory()));
			}
			conn.close();
		}
	}

	public long getMemory() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long diff = totalMemory / Utility.MB - 300;
		return (runtime.freeMemory() / Utility.MB - diff);

	}
}