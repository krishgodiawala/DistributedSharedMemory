package mobile.dsm.master;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.utils.FileChunks;
import mobile.dsm.utils.HostName_Port;

/**
 * This class handles whenever a user connects to the master for whatever
 * operations
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class UserHandler implements Runnable {
	Socket socket;

	public UserHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * method gets the file from the slave
	 * 
	 * @param m
	 * @param Filename
	 * @param size
	 * @return
	 */
	public byte[] getFile(List<MemorySlave> m, String Filename, int size) {
		// List<File> tempFile = new ArrayList<File>();
		byte[] b = new byte[size];
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		int currentTot = 0;
		for (int i = 0; i < m.size() - 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(m.get(i).ipAdress, HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("get"));
			conn.write(new String(Filename));
			int length = Integer.valueOf(conn.read());
			Socket sock = conn.getSocket();
			int bytesRead;
			int btsRead = length;
			InputStream is;

			try {
				is = sock.getInputStream();
				DataInputStream in = new DataInputStream(is);
				in.readFully(b, currentTot, length);
				currentTot += length;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Difference in FileSize " + (length - currentTot) + " " + btsRead);
			System.out.println(currentTot);

			//
			updateMemory.add(new MemorySlave(m.get(i).ipAdress, Long.parseLong(conn.read())));
			// conn.close();
			conn.write("quit");
		}
		updateMemory.add(m.get(m.size()-2));
		updateMemory.add(m.get(m.size()-1));
		SharedMemory.returnSlaves(updateMemory);
		// return (new FileChunks().get(tempFile));
		return b;
	}

	/**
	 * This method gets a back up from the slave if primary node is down
	 * 
	 * @param fileName
	 * @param m
	 * @param size
	 * @return
	 */
	public byte[] getBackUp(String fileName, List<MemorySlave> m, int size) {
		byte bytearray[] = null;
		boolean success = false;

		for (int i = m.size() - 2; i < m.size(); i++) {
			if (!success) {
				try {
					TcpServerConnection conn = new TcpServerConnection(m.get(i).ipAdress,
							HostName_Port.SLAVE_SERVER_CONN_PORT);
					conn.write("getBackup");
					conn.write(fileName);
					Socket socket = conn.getSocket();
					size = Integer.parseInt(conn.read());
					bytearray = new byte[size];
					System.out.println("sds " + size);
					InputStream is = socket.getInputStream();
					DataInputStream dis = new DataInputStream(is);
					int currentot = 0;
					int r = size;
					int bytesRead = 0;
					int c = 0;
					while (size > 0 && c < 10) {
						bytesRead = is.read(bytearray, currentot, bytearray.length - currentot);
						if (bytesRead > 0) {
							currentot += bytesRead;
							size -= bytesRead;
							c = 0;
						} else {
							c++;
						}
						System.out.println(bytesRead);
					}
					success = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return bytearray;
	}

	public byte[] testGetBackUp(String fileName, List<MemorySlave> m, int size) {
		byte b[] = null;
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		boolean success = false;
		for (int i = m.size() - 2; i < m.size(); i++) {
			while (!success) {
				try {
					TcpServerConnection conn = new TcpServerConnection(m.get(i).ipAdress,
							HostName_Port.SLAVE_SERVER_CONN_PORT);
					conn.write("getBackup");
					conn.write(fileName);
					Socket socket = conn.getSocket();

					// InputStream is = socket.getInputStream();
					DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
					long fileSize = dis.readLong();
					System.out.println("fileSize " + fileSize);
					// size = Integer.parseInt(conn.read());
					byte bytearray[] = null;
					bytearray = new byte[4026];
					System.out.println("sds " + fileSize);
					int n = 0;
					b = new byte[(int) fileSize];
					System.out.println("Size of byte array " + b.length);
					int read = 0;
					while (fileSize > 0
							&& (n = dis.read(bytearray, 0, (int) Math.min(bytearray.length, fileSize))) != -1) {
						System.arraycopy(bytearray, 0, b, read, n);
						read += n;
						fileSize -= n;
						System.out.println("bytesRead " + read);
					}

					success = true;
					System.out.println(fileSize);
					// Long.parseLong(conn.read());
					conn.write("quit");
				} catch (IOException e) {

				}

			}
		}
		SharedMemory.returnSlaves(m);
		return b;
	}

	public void backupHelper(TcpServerConnection conn, byte[] Filesystems, String filename) {

		String put = new String("put");
		conn.write(put);
		conn.write(new String("backup"));
		conn.write(new String(filename));
		// conn.write(new String(String.valueOf(Filesystems.length)));

	}

	/**
	 * This method is used to back up the the file to two appropriate nodes
	 * based on the available disk memory
	 */
	public void backupNodes(List<MemorySlave> slaves, byte[] Filesystems, String file_name) throws IOException {
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		// Iterate through the List to get backup nodes
		for (int i = 0; i < 2; i++) {
			TcpServerConnection conn = new TcpServerConnection((slaves.get(slaves.size() - (i + 1)).ipAdress),
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			backupHelper(conn, Filesystems, file_name);
			// sending the back up file to first backup node
			// sendBackup(Filesystems, conn);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.writeByte(Filesystems);
			long sizeLeft = Long.parseLong(conn.read());
			conn.write("quit");
			updateMemory.add(new MemorySlave(slaves.get(slaves.size() - (i + 1)).ipAdress, sizeLeft * 1000));
			conn.close();
		}
		SharedMemory.returnSlaves(updateMemory);
	}

	/**
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
			// conn.close();
		}
		for (int i = slaves.size() - 2; i < slaves.size(); i++) {
			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAdress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("delete"));
			conn.write(new String(filename));
			updateMemory.add(new MemorySlave(slaves.get(i).ipAdress, Long.parseLong(conn.read())));
			// conn.close();
		}
		SharedMemory.returnSlaves(updateMemory);
	}

	/**
	 * Helper which is used to perform backup operations
	 * 
	 */
	public void sendBackup(byte[] Filesystems, TcpServerConnection conn) throws IOException {
		FileChunks f = new FileChunks(Filesystems);
		int iteration = (Filesystems.length / 60);
		int i = 1;

		conn.writeByte(Filesystems);

	}

	public void sendFileRequest(List<MemorySlave> slaves, byte[] filename, String fileName) {
		List<MemorySlave> updateMemory = new ArrayList<MemorySlave>();
		FileChunks f = new FileChunks(filename);
		for (int i = 0; i < slaves.size() - 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAdress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("put"));
			conn.write(new String("main"));
			conn.write(new String(fileName));
			byte[] b = f.putbytes(slaves.get(i).memory);
			System.out.println(b.length);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conn.writeByte(b);
			long sizeLeft = Long.parseLong(conn.read());
			conn.write("quit");
			updateMemory.add(new MemorySlave(slaves.get(i).ipAdress, sizeLeft * 1000));

			conn.close();
		}
		SharedMemory.returnSlaves(updateMemory);
	}

	/**
	 * This method communcates with the client when a client connects
	 */
	public void communicateWithClient() {
		TcpServerConnection clientConn = new TcpServerConnection(socket);
		boolean connectionOn = true;
		while (connectionOn) {
			String message = clientConn.read();
			if (message.equals("put") || message.equals("delete") || message.equals("get") || message.equals("close")
					|| message.equals("backupget")) {
				if (message.equals("get")) {
					String fileName = clientConn.read();
					List<MemorySlave> slaves = SharedMemory.getSlaves(fileName);
					if (slaves.size() > 0) {
						int size = (int) SharedMemory.getLength(fileName);
						clientConn.write(String.valueOf(size));
						clientConn.writeByte(getFile(slaves, fileName, size));
					} else {
					}
				} else if (message.equals("put")) {
					// Name of the file
					String fileName = clientConn.read();
					byte[] file = clientConn.readFile();
					List<MemorySlave> m = (List<MemorySlave>) SharedMemory.getSlaves(file.length, fileName);
					if (m.size() > 0) {
						sendFileRequest(m, file, fileName);
						try {
							backupNodes(m, file, fileName);
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
				} else if (message.equals("backupget")) {
					String fileName = clientConn.read();
					List<MemorySlave> slaves = SharedMemory.getSlaves(fileName);
					if (slaves.size() > 0) {
						int size = (int) SharedMemory.getLength(fileName);

						byte b[] = testGetBackUp(fileName, slaves, size);
						clientConn.write(String.valueOf(b.length));
						clientConn.writeByte(b);
					}
				}
			}

		}
	}

	/**
	 * start a new thread when client connects
	 */
	public void run() {
		communicateWithClient();
	}
}