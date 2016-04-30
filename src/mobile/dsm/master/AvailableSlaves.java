package mobile.dsm.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.slave.SlaveInformation;

public class AvailableSlaves {
	private static long MAX_SLAVE_MEMORY = 1024 * 312;
	static ConcurrentHashMap<SlaveInformation, SlaveInformation> allSlaves = new ConcurrentHashMap<SlaveInformation, SlaveInformation>();

	public List<String> availableSlaves() {
		List<String> slavesWithAvailableMemory = new ArrayList<String>();
		long totalAvailableMemory = 0;
		for (Entry<SlaveInformation, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
			totalAvailableMemory += slaveInfo.getValue().availableHeapSize;
			slavesWithAvailableMemory.add(slaveInfo.getValue().ipAddress);
		}
		slavesWithAvailableMemory.add(String.valueOf(totalAvailableMemory));
		return slavesWithAvailableMemory;
	}
	
}
