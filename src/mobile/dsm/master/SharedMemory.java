package mobile.dsm.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.network.TcpServerConnection;
import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.FileChunks;
import mobile.dsm.utils.HostName_Port;

/**
 * This class is the shared memory where each Object information and
 * corresponding chunks are stored as well as metadata
 * 
 * @author krishgodiawala
 *
 */
public class SharedMemory implements Runnable {

	//The shared object
	static ConcurrentHashMap<String, List<MemorySlave>> shared = new ConcurrentHashMap<String, List<MemorySlave>>();
	//Metadata about the object
	static ConcurrentHashMap<String, Long> metaData = new ConcurrentHashMap<String, Long>();
	TcpServerConnection conn;

	public SharedMemory() {
		conn = new TcpServerConnection(HostName_Port.MASTERPORT);
	}
	

	/**
	 * Gets slaves for the particular files
	 * @param fileName
	 * @return
	 */
	public static List<MemorySlave> getSlaves(String fileName) {
		if (shared.containsKey(fileName)) {
			AvailableSlaves.getLockOnSlaves(shared.get(fileName));
			return shared.get(fileName);
		}
		return new ArrayList<MemorySlave>();
	}

	/**
	 * Returns the lock on the list of slaves
	 * @param slaves
	 */
	public static void returnSlaves(List<MemorySlave> slaves) {
		AvailableSlaves.returnSlaves(slaves);
	}

	/**
	 * Metadata about the file
	 * @param fileName
	 * @return
	 */
	public static long getLength(String fileName) {
		return metaData.get(fileName);
	}

	/**
	 * returns a list of slaves to store the requested file
	 * @param memory
	 * @param fileName
	 * @return
	 */
	public static List<MemorySlave> getSlaves(long memory, String fileName) {
		List<MemorySlave> slaves = AvailableSlaves.getSlaves(memory);
		if (slaves.size() > 0) {
			SharedMemory.shared.put(fileName, slaves);
			SharedMemory.metaData.put(fileName, memory);
		}
		return slaves;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			Socket sock = conn.createConnection();
			new Thread(new UserHandler(sock)).start();
		}

	}
}