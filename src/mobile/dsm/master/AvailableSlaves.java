package mobile.dsm.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.Utility;

public class AvailableSlaves {

	static ConcurrentHashMap<SlaveInformation, SlaveInformation> allSlaves = new ConcurrentHashMap<SlaveInformation, SlaveInformation>();

	public static List<SlaveInformation> availableSlaves() {
		List<SlaveInformation> slavesWithAvailableMemory = new ArrayList<SlaveInformation>();
		for (Entry<SlaveInformation, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
			if ((slaveInfo.getValue().usedHeapSize - (Utility.MAX_SLAVE_BACKUP_SPACE)) < Utility.MAX_SLAVE_MEMORY) {
				slavesWithAvailableMemory.add(slaveInfo.getValue());
			}
		}
		Collections.sort(slavesWithAvailableMemory);
		return slavesWithAvailableMemory;
	}
}
