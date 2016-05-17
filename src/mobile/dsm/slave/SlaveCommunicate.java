package mobile.dsm.slave;

import java.io.IOException;
import java.net.Socket;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.Utility;
import mobile.dsm.utils.WriteToDisk;

/**
 * This class communicates with master and performs a list of requested
 * operations
 * 
 * @author krishgodiawala
 *
 */
public class SlaveCommunicate extends Thread {
	Socket socket;
	TcpServerConnection conn;

	public SlaveCommunicate(Socket socket) {
		this.socket = socket;
		this.conn = new TcpServerConnection(socket);
	}

	public void run() {
		System.out.println(getMemory());

		long initMemory = getMemory();
		String message = conn.read();
		System.out.println(message);
		if (message.equalsIgnoreCase("get")) {
			String fileName = conn.read();
			if (Slave.files.containsKey(fileName))
				conn.writeByte(Slave.files.get(fileName));
			conn.write(Long.toString(getMemory()));
		} else if (message.equalsIgnoreCase("put")) {
			System.out.println("message1 " + message);
			String insideMessage = conn.read();
			if (insideMessage.equalsIgnoreCase("main")) {
				System.out.println(insideMessage);
				String fileName = conn.read();
				System.out.println(fileName);
				// File fileName = (File) conn.readObject();
				byte[] fil = null;

				try {
					fil = conn.readFile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("file received");
				Slave.files.put(fileName, fil);
				System.out.println("put complete");
			} else if (insideMessage.equalsIgnoreCase("backup")) {
				System.out.println("message1 " + insideMessage);
				// String m[] = conn.read().split("||");
				String filename = conn.read();
				System.out.println("fileName" + filename);
				int length = Integer.parseInt(conn.read());
				System.out.println("Length " + length);
				try {
					WriteToDisk.write(filename, conn, length);
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("File transfer complete");
			}
			Long l = getMemory();
			System.out.println("Writing memory " + l);
			conn.write(Long.toString(l));
			System.out.println("writing complete");
			// } else
			// continue;
		} else if (message.equals("delete")) {
			String fileName = conn.read();
			WriteToDisk.deleteFile(fileName);

			long newMemory = getMemory();
			// test
			while (Math.abs(newMemory - initMemory) <= 10) {
				newMemory = Slave.ewma.getEWMA(getMemory());
			}
			conn.write(Long.toString(newMemory));

		} else if (message.equalsIgnoreCase("deletechunk")) {
			String fileName = conn.read();
			if (Slave.files.containsKey(fileName))
				Slave.files.remove(fileName);
			System.gc();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.write(Long.toString(getMemory()));

		} else if (message.equalsIgnoreCase("getBackup")) {
			String fileName = conn.read();
			System.out.println("Filename " + fileName);
			// int size = Integer.parseInt(conn.read());
			// System.out.println("Size " + size);

			int a = WriteToDisk.testSendChunks(fileName, conn);
			System.out.println("Write Backup Complete "+a);
			conn.write(Long.toString(getMemory()));
		}
		System.out.println("yep " + conn.read());
		System.out.println("closing");
		conn.close();
	}

	/**
	 * Returns current memory instance
	 * 
	 * @return
	 */
	public long getMemory() {
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long diff = totalMemory / Utility.MB - 300;
		return (runtime.freeMemory() / Utility.MB - diff);

	}
}
