package mobile.dsm.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public void backupHelper(TcpServerConnection conn, File Filesystems) {
		String p;
		if ((p = conn.read()) != null) {
			String put = new String("put");
			conn.write(put);
			conn.read();
			conn.write(new String("yes"));
			conn.write(new String("backup"));
			conn.write(new String(Filesystems.getName()));
			conn.write(new String(Filesystems.getName()) + "||" + Math.ceil((Filesystems.length() / 60)) + "||"
					+ Filesystems.length());
		}
	}

	/*
	 * This method is used to back up the the file to two appropriate nodes
	 * based on the available disk memory
	 */
	public void backupNodes(List<MemorySlave> slaves, File Filesystems) throws IOException {

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
	public void sendBackup(File Filesystems, TcpServerConnection conn) throws IOException {
		int size_of_chunks = 60;
		byte filechunks[] = new byte[size_of_chunks];
		int chunk_number = 0;
		FileInputStream file_input = new FileInputStream(Filesystems);

		int rc = file_input.read(filechunks);
		while (rc == 60) {
			conn.writeByte(filechunks);
			filechunks = new byte[60];
			rc = file_input.read(filechunks);
		}
		byte[] filechunks1 = Arrays.copyOfRange(filechunks, 0, rc);
		conn.writeByte(filechunks1);
	}

	public void sendFileRequest(List<MemorySlave> slaves, File filename) {
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

	public void communicateWithClient() {

		TcpServerConnection clientConn = new TcpServerConnection(socket);

		if (clientConn.read().equals("get")) {
			List<MemorySlave> slaves = SharedMemory.getSlaves(clientConn.read());
			if (slaves.size() > 0) {
				clientConn.writeObject(getFile(slaves, clientConn.read()));

			} else {

			}

		} else if (clientConn.read().equals("put")) {
			File file = (File) clientConn.readObject();
			List<MemorySlave> m = (List<MemorySlave>) SharedMemory.getSlaves(file.length());
			sendFileRequest(m, file);
			try {
				backupNodes(m, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (clientConn.read().equals("delete")) {

			deleteFileChunk(clientConn.read());
		}
	}

	@Override
	public void run() {
		communicateWithClient();

	}
}
