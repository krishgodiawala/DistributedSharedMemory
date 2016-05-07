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

public class SharedMemory implements Runnable {

	static ConcurrentHashMap<String, List<MemorySlave>> shared = new ConcurrentHashMap<String, List<MemorySlave>>();
	static ConcurrentHashMap<String, Long> metaData = new ConcurrentHashMap<String, Long>();
	TcpServerConnection conn;

	public SharedMemory() {
		conn = new TcpServerConnection(HostName_Port.MASTERPORT);
	}
	/*
	 * This create file creates chunks of the file and send the data to the
	 * available nodes
	 */

	public static List<MemorySlave> getSlaves(String fileName) {
		if (shared.contains(fileName)) {
			AvailableSlaves.getLockOnSlaves(shared.get(fileName));
			return shared.get(fileName);
		}
		return new ArrayList<MemorySlave>();
	}

	public static void returnSlaves(List<MemorySlave> slaves) {
		AvailableSlaves.returnSlaves(slaves);
	}

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