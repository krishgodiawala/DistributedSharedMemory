package mobile.dsm.master;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import com.google.common.primitives.Bytes;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.FileChunks;
import mobile.dsm.utils.HostName_Port;

public class UserHandler implements Runnable {

	Socket socket;

	public UserHandler(Socket socket) {
		this.socket = socket;
	}

	/* To get file from the nodes */

	public File getFile(List<MemorySlave> m, String Filename) {
		List<File> tempFile = new ArrayList<File>();
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		for (int i = 0; i < m.size() - 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(m.get(i).ipAdress, HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("get"));
			conn.write(new String(Filename));
			tempFile.add((File) conn.readObject());
			updateMemory.add(new MemorySlave(m.get(i).ipAdress, Long.parseLong(conn.read())));
			conn.close();
		}
		SharedMemory.returnSlaves(updateMemory);
		return (new FileChunks().get(tempFile));

	}

	public void backupHelper(TcpServerConnection conn, byte[] Filesystems) {
		String p;
		if ((p = conn.read()) != null) {
			String put = new String("put");
			conn.write(put);
			conn.read();
			conn.write(new String("yes"));
			conn.write(new String("backup"));
			conn.write(new String());

		}
	}

	/*
	 * This method is used to back up the the file to two appropriate nodes
	 * based on the available disk memory
	 */
	public void backupNodes(List<MemorySlave> slaves, byte[] Filesystems) throws IOException {

		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		// Iterate through the List to get backup nodes
		String Backupnode1 = slaves.get(slaves.size() - 1).ipAdress;
		String Backupnode2 = slaves.get(slaves.size() - 2).ipAdress;

		for (int i = 0; i < 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(("Backupnode" + i),
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			backupHelper(conn, Filesystems);
			// sending the back up file to first backup node
			sendBackup(Filesystems, conn);
			updateMemory
					.add(new MemorySlave(slaves.get(slaves.size() - (i + 1)).ipAdress, Long.parseLong(conn.read())));
			conn.close();
		}
		SharedMemory.returnSlaves(updateMemory);
	}
	/*
	 * This method deletes the file chunk from ythe main memory of the node
	 * 
	 */

	public void deleteFileChunk(String filename) {
		List<MemorySlave> slaves = SharedMemory.getSlaves(filename);
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		for (int i = 0; i < slaves.size() - 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAdress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("deletechunk"));
			conn.write(new String(filename));
			updateMemory.add(new MemorySlave(slaves.get(i).ipAdress, Long.parseLong(conn.read())));
			conn.close();
		}
		for (int i = slaves.size() - 2; i < slaves.size(); i++) {
			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAdress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("delete"));
			conn.write(new String(filename));
			updateMemory.add(new MemorySlave(slaves.get(i).ipAdress, Long.parseLong(conn.read())));
			conn.close();
		}

		SharedMemory.returnSlaves(updateMemory);
	}

	/*
	 * Helper which is used to perform backup operations
	 * 
	 */
	public void sendBackup(byte[] Filesystems, TcpServerConnection conn) throws IOException {
		FileChunks f = new FileChunks(Filesystems);
		int iteration = (Filesystems.length / 60);
		int i = 1;
		while (i == iteration) {

			conn.writeByte(f.putbytes(60));
		}
		conn.writeByte(f.putbytes(Filesystems.length - (iteration * 60)));

	}

	public void sendFileRequest(List<MemorySlave> slaves, byte[] filename) {
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		FileChunks f = new FileChunks(filename);
		for (int i = 0; i < slaves.size() - 2; i++) {

			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAdress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("put"));
			try {
				conn.writeObject(f.put(slaves.get(i).memory));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateMemory.add(new MemorySlave(slaves.get(i).ipAdress, Long.parseLong(conn.read())));
			conn.close();

		}
		SharedMemory.returnSlaves(updateMemory);

	}

	/**
	 * 
	 */
	public void communicateWithClient() {
		TcpServerConnection clientConn = new TcpServerConnection(socket);
		boolean connectionOn = true;
		while (connectionOn) {
			String message = clientConn.read();
			if (message.equals("get")) {
				List<MemorySlave> slaves = SharedMemory.getSlaves(clientConn.read());
				if (slaves.size() > 0) {
					clientConn.writeObject(getFile(slaves, clientConn.read()));
				} else {

				}
			} else if (message.equals("put")) {
				//Name of the file
				String fileName = clientConn.read();
				byte[] file = clientConn.readFile();

				List<MemorySlave> m = (List<MemorySlave>) SharedMemory.getSlaves(file.length, fileName);
				if (m.size() > 0) {
					sendFileRequest(m, file);
					try {
						backupNodes(m, file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("Sorry no space");
				}
			} else if (message.equals("delete")) {
				deleteFileChunk(clientConn.read());
			} else if (message.equalsIgnoreCase("close")) {
				clientConn.close();
				connectionOn = false;
			}
		}
	}

	@Override
	public void run() {
		communicateWithClient();
	}
}
