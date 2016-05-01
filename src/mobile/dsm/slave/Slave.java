package mobile.dsm.slave;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.HostName_Port;
import mobile.dsm.utils.Utility;
import mobile.dsm.utils.WriteToDisk;

public class Slave implements Runnable {
	TcpServerConnection conn;
	Map<String, File> files;

	public Slave() {
		conn = new TcpServerConnection(HostName_Port.SLAVE_SERVER_CONN_PORT);
		files = new HashMap<String, File>();
		// files = new HashSet<File>();
	}

	@Override
	public void run() {
		while (true) {

			conn.writerConnection();
			Runtime runtime = Runtime.getRuntime();
			if (conn.read().equalsIgnoreCase("get")) {
				String fileName = conn.read();
				if (this.files.containsKey(fileName))
					conn.writeObject(this.files.get(fileName));
			} else if (conn.read().equalsIgnoreCase("put")) {
				conn.write(String.valueOf(runtime.freeMemory() / Utility.MB - Utility.MAX_SLAVE_BACKUP_SPACE));
				if (conn.read().equalsIgnoreCase("yes")) {
					if (conn.read().equalsIgnoreCase("main")) {
						File fileName = (File) conn.readObject();
						this.files.put(fileName.getName(), fileName);
					} else if (conn.read().equalsIgnoreCase("backup")) {
						File fileName;
						while (true) {
							fileName = (File) conn.readObject();
							WriteToDisk.write(fileName);
						}
					}
				} else
					continue;
			} else if (conn.read().equals("delete")) {
				if (conn.read().equalsIgnoreCase("main")) {
					File fileName = (File) conn.readObject();
					this.files.remove(fileName.getName());
				} else if (conn.read().equalsIgnoreCase("backup")) {
					File fileName = (File) conn.readObject();
					WriteToDisk.deleteFile(fileName.getName());
				}
			}
		}
	}

}
