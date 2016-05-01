package mobile.dsm.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.FileChunks;
import mobile.dsm.utils.HostName_Port;

public class SharedMemory {

	static ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> shared = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
	// ConcurrentHashMap<String, Integer> pi_chunk_location = new
	// ConcurrentHashMap<String, Integer>();

	/*
	 * This create file creates chunks of the file and send the data to the
	 * available nodes
	 */

	public void createFile(File filename) {

		List<SlaveInformation> available_nodes = AvailableSlaves.availableSlaves();
		if (checkAvailable(filename.length(), available_nodes)) {
			try {
				backupNodes(filename, available_nodes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("No memory availabe");
		}
	}

	/* To get file from the nodes */

	public File getFile(String filename) {
		List<File> listoffiles;
		if (shared.containsKey(filename)) {
			listoffiles = getFileChunks();
			return new FileChunks().get(listoffiles);
		} else {
			return null;
		}
	}

	/* The method is used to get the latest available memory in each node */
	public boolean checkAvailable(long size, List<SlaveInformation> slaves) {
		long total_available_memory = size;
		for (int i = 0; i < slaves.size() - 2; i++) {
			total_available_memory -= (slaves.get(i).availableHeapSize);
		}
		return (total_available_memory > 0);
	}

	/*
	 * This method is used to back up the the file to two appropriate nodes
	 * based on the available disk memory
	 */
	public void backupNodes(File Filesystems, List<SlaveInformation> available_nodes) throws IOException {

		// Iterate through the List to get backup nodes
		String Backupnode1 = available_nodes.get(available_nodes.size() - 1).ipAddress;
		String Backupnode2 = available_nodes.get(available_nodes.size() - 2).ipAddress;

		TcpServerConnection conn = new TcpServerConnection(Backupnode1, HostName_Port.SLAVE_SERVER_CONN_PORT);
		String p;

		// sending the back up file to first backup node
		if ((p = conn.read()) != null) {
			String put = new String("put");
			conn.write(put);
			conn.read();
			conn.write(new String("yes"));
			conn.write(new String("backup"));
			conn.write(new String(Filesystems.getName()));
			conn.write(new String(Filesystems.getName()) + "||" + Math.ceil((Filesystems.length() / 60)) + "||"
					+ Filesystems.length());
			sendBackup(Filesystems, conn);
		}

		TcpServerConnection conn1 = new TcpServerConnection(Backupnode1, HostName_Port.SLAVE_SERVER_CONN_PORT);
		String p1;

		// sending the backup file to second node
		if ((p1 = conn1.read()) != null) {
			String put = new String("put");
			conn1.write(put);
			conn1.read();
			conn1.write(new String("yes"));
			conn1.write(new String("backup"));
			conn1.write(new String(Filesystems.getName()) + "||" + Math.ceil((Filesystems.length() / 60)) + "||"
					+ Filesystems.length());
			sendBackup(Filesystems, conn1);
		}

	}

	/*
	 * This method deletes the file chunk from ythe main memory of the node
	 * 
	 */
	public void deleteFileChunk(String filename, String Ipaddress_node) {
		TcpServerConnection conn = new TcpServerConnection(Ipaddress_node, HostName_Port.SLAVE_SERVER_CONN_PORT);
		conn.write(new String("deletechunk"));
		conn.write(new String(filename));

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

	public List<File> getFileChunks() {
		return new ArrayList<File>();
	}

	public void sendFileRequest(List<SlaveInformation> slaves, List<File> filechunks, File filename) {
		FileChunks f = new FileChunks();
		for (int i = 0; i < slaves.size() - 2; i++) {
			TcpServerConnection conn = new TcpServerConnection(slaves.get(i).ipAddress,
					HostName_Port.SLAVE_SERVER_CONN_PORT);
			conn.write(new String("put"));
			// if(Long.parseLong(slaves.get(i).availableHeapSize>100){
			//
			// conn.writeObject(f.put(filename,));
			// }
		}

	}
}